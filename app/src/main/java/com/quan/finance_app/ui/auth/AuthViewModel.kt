package com.quan.finance_app.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.quan.finance_app.data.AuthRepository
import com.quan.finance_app.data.AuthResult
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.SessionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SessionStatus {
    Checking,
    LoggedIn,
    LoggedOut
}

/**
 * Trạng thái xác thực và phiên đăng nhập của toàn app.
 * Màn hình chỉ vẽ lại theo state này, không tự gọi API.
 */
data class AuthUiState(
    val session: SessionStatus = SessionStatus.Checking,
    val onboardingPending: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false,
    val displayName: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = ServiceLocator.authRepository
) : ViewModel() {

    companion object {
        const val SERVER_WEB_CLIENT_ID = "335542221014-44m3m8dcpsmbcdadev03dtqb2kfopgjk.apps.googleusercontent.com"
        const val SESSION_EXPIRED_MESSAGE = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
        const val MISSING_SESSION_MESSAGE = "Phiên đăng nhập không còn hiệu lực. Vui lòng đăng nhập lại."
    }

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        observeSessionExpiry()
        restoreSession()
    }

    fun restoreSession() {
        _state.update { it.copy(session = SessionStatus.Checking, errorMessage = null) }
        viewModelScope.launch {
            when (repository.restoreSession()) {
                SessionResult.Restored -> {
                    val pending = repository.isOnboardingPending()
                    _state.update {
                        it.copy(
                            session = SessionStatus.LoggedIn,
                            onboardingPending = pending,
                            errorMessage = null
                        )
                    }
                }
                SessionResult.Offline -> {
                    // D3-B: mất mạng không phải hết hạn phiên -> vẫn vào Home (hoặc onboarding nếu đang dở), giữ refresh token
                    val pending = repository.isOnboardingPending()
                    _state.update {
                        it.copy(
                            session = SessionStatus.LoggedIn,
                            onboardingPending = pending,
                            errorMessage = null
                        )
                    }
                }
                SessionResult.NoSession -> {
                    _state.update {
                        it.copy(
                            session = SessionStatus.LoggedOut,
                            onboardingPending = false,
                            errorMessage = null
                        )
                    }
                }
                SessionResult.Expired -> {
                    _state.update {
                        it.copy(
                            session = SessionStatus.LoggedOut,
                            onboardingPending = false,
                            errorMessage = SESSION_EXPIRED_MESSAGE
                        )
                    }
                }
            }
        }
    }

    private fun observeSessionExpiry() {
        viewModelScope.launch {
            repository.sessionExpired.collect { expired ->
                if (expired) {
                    repository.consumeSessionExpired()
                    _state.update {
                        AuthUiState(
                            session = SessionStatus.LoggedOut,
                            errorMessage = SESSION_EXPIRED_MESSAGE
                        )
                    }
                }
            }
        }
    }

    fun login(email: String, password: String, rememberMe: Boolean = true) = run("login") {
        repository.login(email, password, rememberMe)
    }

    fun register(email: String, password: String, fullName: String) = run("register") {
        repository.register(email, password, fullName, rememberMe = true)
    }

    fun loginWithGoogle(idToken: String, rememberMe: Boolean = true) = run("login") {
        repository.loginWithGoogle(idToken, rememberMe)
    }

    fun onGoogleSignInClicked(
        context: Context,
        rememberMe: Boolean = true,
        serverClientId: String = SERVER_WEB_CLIENT_ID
    ) {
        val credentialManager = CredentialManager.create(context)

        viewModelScope.launch {
            try {
                val signInOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInOption)
                    .build()

                val response = credentialManager.getCredential(context = context, request = request)
                handleCredentialResponse(response, rememberMe)
            } catch (_: GetCredentialCancellationException) {
                // Người dùng chủ động đóng hộp thoại chọn tài khoản
            } catch (_: NoCredentialException) {
                try {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(serverClientId)
                        .setAutoSelectEnabled(false)
                        .build()
                    val fallbackRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()
                    val response = credentialManager.getCredential(context = context, request = fallbackRequest)
                    handleCredentialResponse(response, rememberMe)
                } catch (_: GetCredentialCancellationException) {
                    // Người dùng huỷ
                } catch (_: Exception) {
                    _state.update {
                        it.copy(errorMessage = "Chưa có tài khoản Google trên máy hoặc chưa cấu hình Android Client ID trong Google Cloud.")
                    }
                }
            } catch (e: GetCredentialException) {
                _state.update { it.copy(errorMessage = e.message ?: "Lỗi xác thực Google") }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message ?: "Lỗi đăng nhập Google") }
            }
        }
    }

    private fun handleCredentialResponse(response: GetCredentialResponse, rememberMe: Boolean) {
        val credential = response.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            loginWithGoogle(googleIdTokenCredential.idToken, rememberMe)
        } else {
            _state.update { it.copy(errorMessage = "Loại thông tin xác thực không hợp lệ") }
        }
    }

    /** Hoàn tất các bước thiết lập sau đăng ký để vào khung chính; không có phiên thì trả về Đăng nhập (D2). */
    fun completeOnboarding() {
        viewModelScope.launch {
            if (repository.hasSession()) {
                repository.finishOnboarding()
                _state.update {
                    it.copy(
                        session = SessionStatus.LoggedIn,
                        onboardingPending = false,
                        registerSuccess = false,
                        errorMessage = null
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        session = SessionStatus.LoggedOut,
                        onboardingPending = false,
                        registerSuccess = false,
                        errorMessage = MISSING_SESSION_MESSAGE
                    )
                }
            }
        }
    }

    /** Màn hình gọi sau khi đã điều hướng xong, để cờ không kích hoạt lại lần nữa. */
    fun consumeNavigation() {
        _state.update { it.copy(loginSuccess = false, registerSuccess = false) }
    }

    fun clearErrors() {
        _state.update { it.copy(errorMessage = null, fieldErrors = emptyMap()) }
    }

    fun logout() {
        _state.value = AuthUiState(session = SessionStatus.LoggedOut)
        viewModelScope.launch {
            repository.logout()
        }
    }

    private fun run(action: String, block: suspend () -> AuthResult) {
        if (_state.value.loading) return
        _state.update { it.copy(loading = true, errorMessage = null, fieldErrors = emptyMap()) }

        viewModelScope.launch {
            when (val result = block()) {
                is AuthResult.Success -> {
                    val pending = repository.isOnboardingPending()
                    _state.update {
                        it.copy(
                            session = SessionStatus.LoggedIn,
                            onboardingPending = pending,
                            loading = false,
                            displayName = result.data.fullName,
                            loginSuccess = action == "login",
                            registerSuccess = action == "register"
                        )
                    }
                }
                is AuthResult.FieldErrors -> _state.update {
                    it.copy(loading = false, fieldErrors = result.errors)
                }
                is AuthResult.Failure -> _state.update {
                    it.copy(loading = false, errorMessage = result.message)
                }
            }
        }
    }
}
