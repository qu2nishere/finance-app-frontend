package com.quan.finance_app.data

import com.google.gson.Gson
import com.quan.finance_app.data.local.OnboardingStore
import com.quan.finance_app.data.local.TokenStore
import com.quan.finance_app.data.remote.AuthApi
import com.quan.finance_app.data.remote.AuthResponseDto
import com.quan.finance_app.data.remote.GoogleLoginRequestDto
import com.quan.finance_app.data.remote.LoginRequestDto
import com.quan.finance_app.data.remote.ProblemDetailDto
import com.quan.finance_app.data.remote.RefreshRequestDto
import com.quan.finance_app.data.remote.RegisterRequestDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

/**
 * Ba kết cục có thể xảy ra của một lần gọi API xác thực.
 * ViewModel chỉ cần `when` trên ba nhánh này, không phải đụng tới mã HTTP.
 */
sealed interface AuthResult {
    data class Success(val data: AuthResponseDto) : AuthResult
    /** 400 — sai dữ liệu nhập, map field → thông báo (lấy từ ProblemDetail.errors) */
    data class FieldErrors(val errors: Map<String, String>) : AuthResult
    /** 401/409/423/500/mất mạng — một câu thông báo chung cho cả màn */
    data class Failure(val message: String, val code: String? = null) : AuthResult
}

/** Kết quả khôi phục phiên khi khởi động app (D2, D3). */
enum class SessionResult {
    NoSession,
    Restored,
    Expired,
    Offline
}

open class AuthRepository(
    private val api: AuthApi? = null,
    private val refreshApi: AuthApi? = api,
    private val tokenStore: TokenStore? = null,
    private val onboardingStore: OnboardingStore? = null
) {
    private val gson = Gson()
    private val fallbackSessionExpired = MutableStateFlow(false)

    open val sessionExpired: StateFlow<Boolean>
        get() = tokenStore?.sessionExpired ?: fallbackSessionExpired

    open fun consumeSessionExpired() {
        tokenStore?.consumeSessionExpired() ?: run { fallbackSessionExpired.value = false }
    }

    /** Kiểm tra xem còn token phiên hợp lệ (trong RAM hoặc đĩa) hay không trước khi cho vào khung chính. */
    open suspend fun hasSession(): Boolean = withContext(Dispatchers.IO) {
        requireTokenStore().isLoggedIn
    }

    /** Kiểm tra người dùng hiện tại còn đang dở bước thiết lập ban đầu (bước 2–3) hay không (D3). */
    open suspend fun isOnboardingPending(): Boolean = withContext(Dispatchers.IO) {
        val uid = requireTokenStore().userId
        requireOnboardingStore().isPending(uid)
    }

    /** Xoá cờ onboarding của người dùng hiện tại sau khi hoàn tất hoặc bỏ qua bước 2–3. */
    open suspend fun finishOnboarding() = withContext(Dispatchers.IO) {
        val uid = requireTokenStore().userId
        requireOnboardingStore().clear(uid)
    }

    open suspend fun register(
        email: String,
        password: String,
        fullName: String,
        rememberMe: Boolean = true
    ): AuthResult {
        val result = perform(rememberMe) {
            requireApi().register(RegisterRequestDto(email.trim(), password, fullName.trim()))
        }
        if (result is AuthResult.Success) {
            withContext(Dispatchers.IO) {
                requireOnboardingStore().markPending(result.data.userId)
            }
        }
        return result
    }

    open suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean = true
    ): AuthResult = perform(rememberMe) {
        requireApi().login(LoginRequestDto(email.trim(), password))
    }

    open suspend fun loginWithGoogle(
        idToken: String,
        rememberMe: Boolean = true
    ): AuthResult = perform(rememberMe) {
        requireApi().loginWithGoogle(GoogleLoginRequestDto(idToken))
    }

    /**
     * Đọc refresh token từ đĩa (Keystore) trên luồng IO và gọi `/api/auth/refresh` dưới cùng khoá
     * với [com.quan.finance_app.data.remote.TokenAuthenticator] để không bao giờ refresh trùng.
     */
    open suspend fun restoreSession(): SessionResult = withContext(Dispatchers.IO) {
        val store = requireTokenStore()
        synchronized(store.refreshLock) {
            if (!store.accessToken.isNullOrBlank()) {
                return@withContext SessionResult.Restored
            }

            val refresh = store.refreshToken ?: return@withContext SessionResult.NoSession
            val response = try {
                requireRefreshApi().refreshCall(RefreshRequestDto(refresh)).execute()
            } catch (_: IOException) {
                return@withContext SessionResult.Offline
            }

            val body = response.body()
            if (response.isSuccessful && body != null && body.accessToken.isNotBlank()) {
                store.save(
                    access = body.accessToken,
                    refresh = body.refreshToken ?: refresh,
                    persist = store.isPersisted,
                    userId = body.userId
                )
                return@withContext SessionResult.Restored
            }

            // 400/401/403 -> token đã hết hạn hoặc bị thu hồi; 5xx (vd gateway 502 khi dừng service) coi như Offline
            if (response.code() in 400..499) {
                store.clear()
                return@withContext SessionResult.Expired
            }
            SessionResult.Offline
        }
    }

    /** Thu hồi phiên trên server rồi luôn xoá sạch token trên máy (kể cả khi mất mạng). */
    open suspend fun logout() = withContext(Dispatchers.IO) {
        val store = requireTokenStore()
        val refresh = store.refreshToken
        store.clear()
        if (!refresh.isNullOrBlank()) {
            try {
                requireRefreshApi().logout(RefreshRequestDto(refresh))
            } catch (_: IOException) {
                // Mất mạng vẫn cho đăng xuất trên máy
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Không chặn đăng xuất vì lỗi phía máy chủ
            }
        }
    }

    private suspend fun perform(
        rememberMe: Boolean,
        request: suspend () -> Response<AuthResponseDto>
    ): AuthResult {
        return try {
            val response = request()
            val body = response.body()

            if (response.isSuccessful && body != null) {
                withContext(Dispatchers.IO) {
                    requireTokenStore().save(
                        access = body.accessToken,
                        refresh = body.refreshToken,
                        persist = rememberMe,
                        userId = body.userId
                    )
                }
                AuthResult.Success(body)
            } else {
                toFailure(response)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: IOException) {
            AuthResult.Failure("Không kết nối được máy chủ.\nBackend đã chạy chưa? Địa chỉ trong ApiClient đúng chưa?")
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Lỗi không xác định")
        }
    }

    private fun toFailure(response: Response<AuthResponseDto>): AuthResult {
        val problem = runCatching {
            gson.fromJson(response.errorBody()?.string(), ProblemDetailDto::class.java)
        }.getOrNull()

        val errors = problem?.errors
        if (!errors.isNullOrEmpty()) return AuthResult.FieldErrors(errors)

        val detail = problem?.detail
        if (!detail.isNullOrBlank()) return AuthResult.Failure(detail, problem.code)

        return AuthResult.Failure("Lỗi máy chủ (HTTP ${response.code()})", problem?.code)
    }

    private fun requireApi(): AuthApi =
        requireNotNull(api) { "AuthApi chưa được khởi tạo" }

    private fun requireRefreshApi(): AuthApi =
        requireNotNull(refreshApi) { "Refresh AuthApi chưa được khởi tạo" }

    private fun requireTokenStore(): TokenStore =
        requireNotNull(tokenStore) { "TokenStore chưa được khởi tạo" }

    private fun requireOnboardingStore(): OnboardingStore =
        requireNotNull(onboardingStore) { "OnboardingStore chưa được khởi tạo" }
}
