package com.quan.finance_app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) = run("login", email) {
        repository.login(email, password)
    }

    fun register(email: String, password: String, fullName: String) = run("register", email) {
        repository.register(email, password, fullName)
    }

    /** Màn hình gọi sau khi đã điều hướng xong, để cờ không kích hoạt lại lần nữa. */
    fun consumeNavigation() {
        _state.update { it.copy(loginSuccess = false, registerSuccess = false) }
    }

    fun clearErrors() {
        _state.update { it.copy(errorMessage = null, fieldErrors = emptyMap()) }
    }

    fun logout() {
        repository.logout()
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
