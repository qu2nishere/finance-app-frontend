package com.quan.finance_app.ui.auth

import com.quan.finance_app.data.AuthRepository
import com.quan.finance_app.data.AuthResult
import com.quan.finance_app.data.SessionResult
import com.quan.finance_app.data.remote.AuthResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepo: FakeAuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeAuthRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_whenNoSession_transitionsToLoggedOutWithoutError() = runTest {
        fakeRepo.restoreOutcome = SessionResult.NoSession

        val vm = AuthViewModel(fakeRepo)

        assertEquals(SessionStatus.LoggedOut, vm.state.value.session)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun init_whenRestored_transitionsToLoggedIn() = runTest {
        fakeRepo.restoreOutcome = SessionResult.Restored

        val vm = AuthViewModel(fakeRepo)

        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun init_whenExpired_transitionsToLoggedOutWithExpiredMessage() = runTest {
        fakeRepo.restoreOutcome = SessionResult.Expired

        val vm = AuthViewModel(fakeRepo)

        assertEquals(SessionStatus.LoggedOut, vm.state.value.session)
        assertEquals(AuthViewModel.SESSION_EXPIRED_MESSAGE, vm.state.value.errorMessage)
    }

    @Test
    fun init_whenOffline_transitionsToLoggedIn() = runTest {
        fakeRepo.restoreOutcome = SessionResult.Offline

        val vm = AuthViewModel(fakeRepo)

        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun login_passesRememberMeAndTransitionsToLoggedIn() = runTest {
        fakeRepo.restoreOutcome = SessionResult.NoSession
        val vm = AuthViewModel(fakeRepo)

        vm.login("quan@gmail.com", "matkhau123", rememberMe = false)

        assertEquals(false, fakeRepo.lastRememberMe)
        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertTrue(vm.state.value.loginSuccess)
        assertEquals("Nguyễn Minh", vm.state.value.displayName)
    }

    @Test
    fun sessionExpired_transitionsLoggedInToLoggedOutWithMessage() = runTest {
        fakeRepo.restoreOutcome = SessionResult.Restored
        val vm = AuthViewModel(fakeRepo)
        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)

        fakeRepo.emitSessionExpired()

        assertEquals(SessionStatus.LoggedOut, vm.state.value.session)
        assertEquals(AuthViewModel.SESSION_EXPIRED_MESSAGE, vm.state.value.errorMessage)
        assertFalse(fakeRepo.sessionExpired.value)
    }

    @Test
    fun logout_callsRepositoryAndTransitionsToLoggedOut() = runTest {
        fakeRepo.restoreOutcome = SessionResult.Restored
        val vm = AuthViewModel(fakeRepo)
        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)

        vm.logout()

        assertTrue(fakeRepo.logoutCalled)
        assertEquals(SessionStatus.LoggedOut, vm.state.value.session)
        assertNull(vm.state.value.errorMessage)
    }

    private class FakeAuthRepository : AuthRepository() {
        var restoreOutcome: SessionResult = SessionResult.NoSession
        var lastRememberMe: Boolean? = null
        var logoutCalled: Boolean = false

        private val expiredState = MutableStateFlow(false)
        override val sessionExpired: StateFlow<Boolean> = expiredState

        override fun consumeSessionExpired() {
            expiredState.value = false
        }

        fun emitSessionExpired() {
            expiredState.value = true
        }

        override suspend fun restoreSession(): SessionResult = restoreOutcome

        override suspend fun login(
            email: String,
            password: String,
            rememberMe: Boolean
        ): AuthResult {
            lastRememberMe = rememberMe
            return AuthResult.Success(
                AuthResponseDto(
                    accessToken = "access-1",
                    refreshToken = "refresh-1",
                    tokenType = "Bearer",
                    expiresIn = 900L,
                    userId = "user-1",
                    email = email,
                    fullName = "Nguyễn Minh"
                )
            )
        }

        override suspend fun logout() {
            logoutCalled = true
        }
    }
}
