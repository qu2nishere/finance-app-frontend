package com.quan.finance_app.data

import com.google.gson.Gson
import com.quan.finance_app.data.local.TokenStore
import com.quan.finance_app.data.remote.AuthApi
import com.quan.finance_app.data.remote.AuthResponseDto
import com.quan.finance_app.data.remote.LoginRequestDto
import com.quan.finance_app.data.remote.ProblemDetailDto
import com.quan.finance_app.data.remote.RegisterRequestDto
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
    data class Failure(val message: String) : AuthResult
}

class AuthRepository(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) {
    private val gson = Gson()

    suspend fun register(email: String, password: String, fullName: String): AuthResult =
        perform { api.register(RegisterRequestDto(email.trim(), password, fullName.trim())) }

    suspend fun login(email: String, password: String): AuthResult =
        perform { api.login(LoginRequestDto(email.trim(), password)) }

    suspend fun loginWithGoogle(idToken: String): AuthResult =
        perform { api.loginWithGoogle(com.quan.finance_app.data.remote.GoogleLoginRequestDto(idToken)) }

    fun logout() = tokenStore.clear()

    private suspend fun perform(request: suspend () -> Response<AuthResponseDto>): AuthResult {
        return try {
            val response = request()
            val body = response.body()

            if (response.isSuccessful && body != null) {
                tokenStore.save(body.accessToken, body.refreshToken)
                AuthResult.Success(body)
            } else {
                toFailure(response)
            }
        } catch (e: IOException) {
            // Không có mạng, sai IP, backend chưa chạy, tường lửa chặn...
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
        if (!detail.isNullOrBlank()) return AuthResult.Failure(detail)

        return AuthResult.Failure("Lỗi máy chủ (HTTP ${response.code()})")
    }
}
