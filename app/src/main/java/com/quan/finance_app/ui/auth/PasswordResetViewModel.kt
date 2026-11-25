package com.quan.finance_app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.AuthRepository
import com.quan.finance_app.data.PasswordResetResult
import com.quan.finance_app.data.ServiceLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PasswordResetStep {
    EMAIL,
    CODE,
    NEW_PASSWORD
}

/**
 * State của luồng 3 màn quên mật khẩu.
 * `code` chỉ nằm trong RAM của ViewModel, xoá ngay khi gửi lại, lùi về bước 1 hoặc hoàn tất đổi mật khẩu.
 */
data class PasswordResetUiState(
    val step: PasswordResetStep = PasswordResetStep.EMAIL,
    val email: String = "",
    val code: String = "",
    val loading: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val resendSecondsLeft: Int = 0,
    val done: Boolean = false,
    val completedEmail: String? = null
)

class PasswordResetViewModel(
    private val repository: AuthRepository = ServiceLocator.authRepository
) : ViewModel() {

    companion object {
        const val RESEND_COOLDOWN_SECONDS = 60
        const val INVALID_CODE_MESSAGE = "Mã không đúng hoặc đã hết hạn"
        const val EXPIRED_CODE_MESSAGE = "Mã đã hết hạn, vui lòng gửi lại"
        const val RESENT_INFO_MESSAGE = "Đã gửi lại mã xác nhận mới."
        const val RESET_SUCCESS_MESSAGE = "Đổi mật khẩu thành công. Đăng nhập bằng mật khẩu mới để tiếp tục."
    }

    private val _state = MutableStateFlow(PasswordResetUiState())
    val state: StateFlow<PasswordResetUiState> = _state.asStateFlow()

    private var countdownJob: Job? = null

    fun start(prefillEmail: String = "") {
        countdownJob?.cancel()
        _state.value = PasswordResetUiState(email = prefillEmail.trim())
    }

    fun updateEmail(value: String) {
        _state.update {
            it.copy(
                email = value,
                errorMessage = null,
                fieldErrors = it.fieldErrors - "email"
            )
        }
    }

    fun updateCode(value: String) {
        val digits = value.filter { it.isDigit() }.take(6)
        _state.update {
            it.copy(
                code = digits,
                errorMessage = null,
                infoMessage = null,
                fieldErrors = it.fieldErrors - "code"
            )
        }
    }

    fun sendCode(emailInput: String = _state.value.email) {
        if (_state.value.loading) return
        val trimmed = emailInput.trim()
        if (trimmed.isBlank()) {
            _state.update {
                it.copy(
                    fieldErrors = mapOf("email" to "Email không được để trống"),
                    errorMessage = null
                )
            }
            return
        }

        _state.update {
            it.copy(
                email = trimmed,
                loading = true,
                errorMessage = null,
                infoMessage = null,
                fieldErrors = emptyMap()
            )
        }

        viewModelScope.launch {
            when (val result = repository.requestPasswordReset(trimmed)) {
                PasswordResetResult.Sent -> {
                    _state.update {
                        it.copy(
                            step = PasswordResetStep.CODE,
                            code = "",
                            loading = false,
                            errorMessage = null,
                            infoMessage = null,
                            fieldErrors = emptyMap()
                        )
                    }
                    startResendCountdown()
                }
                is PasswordResetResult.FieldErrors -> {
                    _state.update { it.copy(loading = false, fieldErrors = result.errors) }
                }
                is PasswordResetResult.InvalidCode -> {
                    _state.update { it.copy(loading = false, errorMessage = result.message) }
                }
                is PasswordResetResult.Failure -> {
                    _state.update { it.copy(loading = false, errorMessage = result.message) }
                }
                PasswordResetResult.CodeAccepted,
                PasswordResetResult.Done -> {
                    _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    fun resendCode() {
        val current = _state.value
        if (current.loading || current.resendSecondsLeft > 0 || current.email.isBlank()) return

        _state.update {
            it.copy(
                code = "",
                loading = true,
                errorMessage = null,
                infoMessage = null,
                fieldErrors = emptyMap()
            )
        }

        viewModelScope.launch {
            when (val result = repository.requestPasswordReset(current.email)) {
                PasswordResetResult.Sent -> {
                    _state.update {
                        it.copy(
                            code = "",
                            loading = false,
                            infoMessage = RESENT_INFO_MESSAGE
                        )
                    }
                    startResendCountdown()
                }
                is PasswordResetResult.FieldErrors -> {
                    _state.update { it.copy(loading = false, fieldErrors = result.errors) }
                }
                is PasswordResetResult.InvalidCode -> {
                    _state.update { it.copy(loading = false, errorMessage = result.message) }
                }
                is PasswordResetResult.Failure -> {
                    _state.update { it.copy(loading = false, errorMessage = result.message) }
                }
                PasswordResetResult.CodeAccepted,
                PasswordResetResult.Done -> {
                    _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    fun verifyCode() {
        val current = _state.value
        if (current.loading) return
        if (current.code.length != 6) {
            _state.update {
                it.copy(
                    fieldErrors = mapOf("code" to "Mã xác nhận phải gồm 6 chữ số"),
                    errorMessage = null,
                    infoMessage = null
                )
            }
            return
        }

        _state.update {
            it.copy(
                loading = true,
                errorMessage = null,
                infoMessage = null,
                fieldErrors = emptyMap()
            )
        }

        viewModelScope.launch {
            when (val result = repository.verifyResetCode(current.email, current.code)) {
                PasswordResetResult.CodeAccepted -> {
                    _state.update {
                        it.copy(
                            step = PasswordResetStep.NEW_PASSWORD,
                            loading = false,
                            errorMessage = null,
                            infoMessage = null,
                            fieldErrors = emptyMap()
                        )
                    }
                }
                is PasswordResetResult.InvalidCode -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            errorMessage = INVALID_CODE_MESSAGE
                        )
                    }
                }
                is PasswordResetResult.FieldErrors -> {
                    _state.update { it.copy(loading = false, fieldErrors = result.errors) }
                }
                is PasswordResetResult.Failure -> {
                    _state.update { it.copy(loading = false, errorMessage = result.message) }
                }
                PasswordResetResult.Sent,
                PasswordResetResult.Done -> {
                    _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    fun submitNewPassword(newPassword: String, confirmPassword: String) {
        val current = _state.value
        if (current.loading) return

        val clientErrors = buildMap {
            if (newPassword.length !in 8..72) {
                put("newPassword", "Mật khẩu phải từ 8 đến 72 ký tự")
            }
            if (confirmPassword != newPassword) {
                put("confirmPassword", "Mật khẩu nhập lại không khớp")
            }
        }
        if (clientErrors.isNotEmpty()) {
            _state.update {
                it.copy(
                    fieldErrors = clientErrors,
                    errorMessage = null,
                    infoMessage = null
                )
            }
            return
        }

        _state.update {
            it.copy(
                loading = true,
                errorMessage = null,
                infoMessage = null,
                fieldErrors = emptyMap()
            )
        }

        viewModelScope.launch {
            when (val result = repository.resetPassword(current.email, current.code, newPassword)) {
                PasswordResetResult.Done -> {
                    countdownJob?.cancel()
                    _state.update {
                        it.copy(
                            loading = false,
                            code = "",
                            resendSecondsLeft = 0,
                            errorMessage = null,
                            infoMessage = null,
                            fieldErrors = emptyMap(),
                            done = true,
                            completedEmail = it.email
                        )
                    }
                }
                is PasswordResetResult.InvalidCode -> {
                    // D12-A: mã hết hạn hoặc vượt số lần thử trong lúc người dùng ở màn 3 -> quay lại màn 2
                    _state.update {
                        it.copy(
                            step = PasswordResetStep.CODE,
                            code = "",
                            loading = false,
                            errorMessage = EXPIRED_CODE_MESSAGE,
                            infoMessage = null,
                            fieldErrors = emptyMap()
                        )
                    }
                }
                is PasswordResetResult.FieldErrors -> {
                    _state.update { it.copy(loading = false, fieldErrors = result.errors) }
                }
                is PasswordResetResult.Failure -> {
                    _state.update { it.copy(loading = false, errorMessage = result.message) }
                }
                PasswordResetResult.Sent,
                PasswordResetResult.CodeAccepted -> {
                    _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    /**
     * Lùi một bước trong luồng quên mật khẩu (③ → ② → ①).
     * Trả `true` nếu đã lùi nội bộ; trả `false` khi đang ở ① để `MainActivity` quay về màn Đăng nhập.
     */
    fun back(): Boolean {
        return when (_state.value.step) {
            PasswordResetStep.NEW_PASSWORD -> {
                _state.update {
                    it.copy(
                        step = PasswordResetStep.CODE,
                        errorMessage = null,
                        infoMessage = null,
                        fieldErrors = emptyMap()
                    )
                }
                true
            }
            PasswordResetStep.CODE -> {
                countdownJob?.cancel()
                _state.update {
                    it.copy(
                        step = PasswordResetStep.EMAIL,
                        code = "",
                        resendSecondsLeft = 0,
                        errorMessage = null,
                        infoMessage = null,
                        fieldErrors = emptyMap()
                    )
                }
                true
            }
            PasswordResetStep.EMAIL -> {
                reset()
                false
            }
        }
    }

    fun consumeDone() {
        _state.update { it.copy(done = false) }
    }

    fun reset() {
        countdownJob?.cancel()
        _state.value = PasswordResetUiState()
    }

    private fun startResendCountdown() {
        countdownJob?.cancel()
        _state.update { it.copy(resendSecondsLeft = RESEND_COOLDOWN_SECONDS) }
        countdownJob = viewModelScope.launch {
            while (_state.value.resendSecondsLeft > 0) {
                delay(1_000L)
                _state.update { s ->
                    s.copy(resendSecondsLeft = (s.resendSecondsLeft - 1).coerceAtLeast(0))
                }
            }
        }
    }

    override fun onCleared() {
        countdownJob?.cancel()
        super.onCleared()
    }
}
