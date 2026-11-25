package com.quan.finance_app.ui.auth

import com.quan.finance_app.data.AuthRepository
import com.quan.finance_app.data.PasswordResetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordResetViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepo: FakePasswordResetRepository
    private lateinit var vm: PasswordResetViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakePasswordResetRepository()
        vm = PasswordResetViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        vm.reset()
        Dispatchers.resetMain()
    }

    @Test
    fun sendCode_whenSent_movesToCodeStepAndStartsCountdown() = runTest {
        vm.updateEmail("quan@gmail.com")
        vm.sendCode()

        assertEquals(PasswordResetStep.CODE, vm.state.value.step)
        assertEquals("quan@gmail.com", vm.state.value.email)
        assertEquals(60, vm.state.value.resendSecondsLeft)
        assertNull(vm.state.value.errorMessage)
        vm.reset()
    }

    @Test
    fun verifyCode_whenInvalid_staysOnCodeStepWithInvalidMessage() = runTest {
        vm.sendCode("quan@gmail.com")
        vm.updateCode("123456")
        fakeRepo.verifyOutcome = PasswordResetResult.InvalidCode("Mã xác nhận không đúng hoặc đã hết hạn")

        vm.verifyCode()

        assertEquals(PasswordResetStep.CODE, vm.state.value.step)
        assertEquals(PasswordResetViewModel.INVALID_CODE_MESSAGE, vm.state.value.errorMessage)
        vm.reset()
    }

    @Test
    fun verifyCode_whenAccepted_movesToNewPasswordStep() = runTest {
        vm.sendCode("quan@gmail.com")
        vm.updateCode("654321")
        fakeRepo.verifyOutcome = PasswordResetResult.CodeAccepted

        vm.verifyCode()

        assertEquals(PasswordResetStep.NEW_PASSWORD, vm.state.value.step)
        assertEquals("654321", vm.state.value.code)
        assertNull(vm.state.value.errorMessage)
        vm.reset()
    }

    @Test
    fun submitNewPassword_whenOtpExpiredBetweenStep2And3_returnsToCodeWithExpiredMessage() = runTest {
        vm.sendCode("quan@gmail.com")
        vm.updateCode("654321")
        vm.verifyCode()
        assertEquals(PasswordResetStep.NEW_PASSWORD, vm.state.value.step)

        fakeRepo.resetOutcome = PasswordResetResult.InvalidCode("Mã xác nhận không đúng hoặc đã hết hạn")
        vm.submitNewPassword("new-password-123", "new-password-123")

        assertEquals(PasswordResetStep.CODE, vm.state.value.step)
        assertEquals("", vm.state.value.code)
        assertEquals(PasswordResetViewModel.EXPIRED_CODE_MESSAGE, vm.state.value.errorMessage)
        vm.reset()
    }

    @Test
    fun back_traversesStep3To2To1ThenSignalsExitToLogin() = runTest {
        vm.sendCode("quan@gmail.com")
        vm.updateCode("654321")
        vm.verifyCode()
        assertEquals(PasswordResetStep.NEW_PASSWORD, vm.state.value.step)

        assertTrue(vm.back())
        assertEquals(PasswordResetStep.CODE, vm.state.value.step)

        assertTrue(vm.back())
        assertEquals(PasswordResetStep.EMAIL, vm.state.value.step)
        assertEquals("", vm.state.value.code)
        assertEquals(0, vm.state.value.resendSecondsLeft)

        assertFalse(vm.back())
    }

    @Test
    fun submitNewPassword_whenConfirmationMismatch_setsFieldErrorWithoutCallingApi() = runTest {
        vm.sendCode("quan@gmail.com")
        vm.updateCode("654321")
        vm.verifyCode()

        vm.submitNewPassword("matkhau-moi-123", "matkhau-khac-456")

        assertFalse(fakeRepo.resetCalled)
        assertEquals(
            "Mật khẩu nhập lại không khớp",
            vm.state.value.fieldErrors["confirmPassword"]
        )
        assertFalse(vm.state.value.done)
        vm.reset()
    }

    @Test
    fun submitNewPassword_whenSuccess_marksDoneAndWipesCodeFromRam() = runTest {
        vm.sendCode("quan@gmail.com")
        vm.updateCode("654321")
        vm.verifyCode()

        vm.submitNewPassword("matkhau-moi-123", "matkhau-moi-123")

        assertTrue(fakeRepo.resetCalled)
        assertTrue(vm.state.value.done)
        assertEquals("quan@gmail.com", vm.state.value.completedEmail)
        assertEquals("", vm.state.value.code)

        vm.consumeDone()
        assertFalse(vm.state.value.done)
    }

    @Test
    fun sendCode_whenOffline_staysOnEmailStepWithConnectionMessage() = runTest {
        val offlineMsg = "Không kết nối được máy chủ."
        fakeRepo.requestOutcome = PasswordResetResult.Failure(offlineMsg)

        vm.sendCode("quan@gmail.com")

        assertEquals(PasswordResetStep.EMAIL, vm.state.value.step)
        assertEquals(offlineMsg, vm.state.value.errorMessage)
    }

    private class FakePasswordResetRepository : AuthRepository() {
        var requestOutcome: PasswordResetResult = PasswordResetResult.Sent
        var verifyOutcome: PasswordResetResult = PasswordResetResult.CodeAccepted
        var resetOutcome: PasswordResetResult = PasswordResetResult.Done
        var resetCalled: Boolean = false

        override suspend fun requestPasswordReset(email: String): PasswordResetResult = requestOutcome

        override suspend fun verifyResetCode(email: String, code: String): PasswordResetResult = verifyOutcome

        override suspend fun resetPassword(
            email: String,
            code: String,
            newPassword: String
        ): PasswordResetResult {
            resetCalled = true
            return resetOutcome
        }
    }
}
