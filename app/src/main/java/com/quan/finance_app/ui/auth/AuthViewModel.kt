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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Trạng thái của hai màn Đăng nhập / Đăng ký.
 * Màn hình chỉ vẽ lại theo state này, không tự gọi API.
 */
data class AuthUiState(
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
    }

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) = run("login", email) {
        repository.login(email, password)
    }

    fun register(email: String, password: String, fullName: String) = run("register", email) {
        repository.register(email, password, fullName)
    }

    fun loginWithGoogle(idToken: String) = run("login", "") {
        repository.loginWithGoogle(idToken)
    }

    fun onGoogleSignInClicked(
        context: Context,
        serverClientId: String = SERVER_WEB_CLIENT_ID
    ) {
        val credentialManager = CredentialManager.create(context)

        viewModelScope.launch {
            try {
                // Dành cho sự kiện bấm nút: dùng GetSignInWithGoogleOption để bật bảng chọn tài khoản
                val signInOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInOption)
                    .build()

                val response = credentialManager.getCredential(context = context, request = request)
                handleCredentialResponse(response)
            } catch (e: GetCredentialCancellationException) {
                // Người dùng chủ động đóng hộp thoại chọn tài khoản
            } catch (e: NoCredentialException) {
                // Thử lại với GetGoogleIdOption
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
                    handleCredentialResponse(response)
                } catch (ex: GetCredentialCancellationException) {
                    // Người dùng huỷ
                } catch (ex: Exception) {
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

    private fun handleCredentialResponse(response: GetCredentialResponse) {
        val credential = response.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken
            loginWithGoogle(idToken)
        } else {
            _state.update { it.copy(errorMessage = "Loại thông tin xác thực không hợp lệ") }
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
        viewModelScope.launch {
            repository.logout()
        }
        _state.value = AuthUiState()
    }

    private fun run(action: String, email: String, block: suspend () -> AuthResult) {
        if (_state.value.loading) return          // chan bam nut hai lan
        _state.update { it.copy(loading = true, errorMessage = null, fieldErrors = emptyMap()) }

        viewModelScope.launch {
            when (val result = block()) {
                is AuthResult.Success -> _state.update {
                    it.copy(
                        loading = false,
                        displayName = result.data.fullName,
                        loginSuccess = action == "login",
                        registerSuccess = action == "register"
                    )
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
