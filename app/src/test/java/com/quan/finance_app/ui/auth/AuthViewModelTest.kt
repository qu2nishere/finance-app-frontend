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
        assertFalse(vm.state.value.onboardingPending)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun init_whenRestored_transitionsToLoggedIn() = runTest {
        fakeRepo.restoreOutcome = SessionResult.Restored

        val vm = AuthViewModel(fakeRepo)

        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertFalse(vm.state.value.onboardingPending)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun init_whenRestoredWithPendingOnboarding_setsOnboardingPendingTrue() = runTest {
        fakeRepo.currentUserId = "user-pending"
        fakeRepo.pendingUserIds.add("user-pending")
        fakeRepo.restoreOutcome = SessionResult.Restored

        val vm = AuthViewModel(fakeRepo)

        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertTrue(vm.state.value.onboardingPending)
    }

    @Test
    fun init_whenExpired_transitionsToLoggedOutWithExpiredMessage() = runTest {
        fakeRepo.restoreOutcome = SessionResult.Expired

        val vm = AuthViewModel(fakeRepo)

        assertEquals(SessionStatus.LoggedOut, vm.state.value.session)
        assertEquals(AuthViewModel.SESSION_EXPIRED_MESSAGE, vm.state.value.errorMessage)
    }

    @Test
    fun init_whenOffline_transitionsToLoggedInAndPreservesPendingOnboarding() = runTest {
        fakeRepo.currentUserId = "user-offline"
        fakeRepo.pendingUserIds.add("user-offline")
        fakeRepo.restoreOutcome = SessionResult.Offline

        val vm = AuthViewModel(fakeRepo)

        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertTrue(vm.state.value.onboardingPending)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun login_passesRememberMeAndTransitionsToLoggedIn() = runTest {
        fakeRepo.restoreOutcome = SessionResult.NoSession
        val vm = AuthViewModel(fakeRepo)

        vm.login("quan@gmail.com", "matkhau123", rememberMe = false)

        assertEquals(false, fakeRepo.lastRememberMe)
        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertFalse(vm.state.value.onboardingPending)
        assertTrue(vm.state.value.loginSuccess)
        assertEquals("Nguyễn Minh", vm.state.value.displayName)
    }

    @Test
    fun register_marksUserLoggedInWithOnboardingPending() = runTest {
        fakeRepo.restoreOutcome = SessionResult.NoSession
        val vm = AuthViewModel(fakeRepo)

        vm.register("new@gmail.com", "matkhau123", "Người mới")

        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertTrue(vm.state.value.onboardingPending)
        assertTrue(vm.state.value.registerSuccess)
    }

    @Test
    fun completeOnboarding_whenSessionExists_clearsPendingFlagAndEntersApp() = runTest {
        fakeRepo.restoreOutcome = SessionResult.NoSession
        val vm = AuthViewModel(fakeRepo)
        vm.register("new@gmail.com", "matkhau123", "Người mới")
        assertTrue(vm.state.value.onboardingPending)

        vm.completeOnboarding()

        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertFalse(vm.state.value.onboardingPending)
        assertFalse(fakeRepo.pendingUserIds.contains("user-new@gmail.com"))
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun completeOnboarding_whenSessionMissing_returnsToLoggedOutWithMissingSessionMessage() = runTest {
        fakeRepo.restoreOutcome = SessionResult.NoSession
        val vm = AuthViewModel(fakeRepo)
        fakeRepo.hasActiveSession = false

        vm.completeOnboarding()

        assertEquals(SessionStatus.LoggedOut, vm.state.value.session)
        assertFalse(vm.state.value.onboardingPending)
        assertEquals(AuthViewModel.MISSING_SESSION_MESSAGE, vm.state.value.errorMessage)
    }

    @Test
    fun onboardingPending_keptAcrossLogoutForSameUser_notShownForAnotherUser() = runTest {
        fakeRepo.restoreOutcome = SessionResult.NoSession
        val vm = AuthViewModel(fakeRepo)

        vm.register("userA@gmail.com", "matkhau123", "User A")
        assertTrue(vm.state.value.onboardingPending)

        // Đăng xuất khi chưa xong bước 2–3 -> cờ vẫn nằm trong OnboardingStore
        vm.logout()
        assertEquals(SessionStatus.LoggedOut, vm.state.value.session)
        assertFalse(vm.state.value.onboardingPending)

        // User B đăng nhập trên cùng máy -> không thấy onboarding của User A
        vm.login("userB@gmail.com", "matkhau123")
        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertFalse(vm.state.value.onboardingPending)

        vm.logout()

        // User A đăng nhập lại -> vẫn tiếp tục onboarding đang dở
        vm.login("userA@gmail.com", "matkhau123")
        assertEquals(SessionStatus.LoggedIn, vm.state.value.session)
        assertTrue(vm.state.value.onboardingPending)
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
        var hasActiveSession: Boolean = false
        var currentUserId: String? = null
        val pendingUserIds = mutableSetOf<String>()
        var lastRememberMe: Boolean? = null
        var logoutCalled: Boolean = false

        private val expiredState = MutableStateFlow(false)
        override val sessionExpired: StateFlow<Boolean> = expiredState

        override fun consumeSessionExpired() {
            expiredState.value = false
        }

        fun emitSessionExpired() {
            hasActiveSession = false
            currentUserId = null
            expiredState.value = true
        }

        override suspend fun restoreSession(): SessionResult {
            if (restoreOutcome == SessionResult.Restored || restoreOutcome == SessionResult.Offline) {
                hasActiveSession = true
            }
            return restoreOutcome
        }

        override suspend fun hasSession(): Boolean = hasActiveSession

        override suspend fun isOnboardingPending(): Boolean =
            currentUserId != null && currentUserId in pendingUserIds

        override suspend fun finishOnboarding() {
            currentUserId?.let { pendingUserIds.remove(it) }
        }

        override suspend fun register(
            email: String,
            password: String,
            fullName: String,
            rememberMe: Boolean
        ): AuthResult {
            val uid = "user-$email"
            currentUserId = uid
            hasActiveSession = true
            pendingUserIds.add(uid)
            return AuthResult.Success(
                AuthResponseDto(
                    accessToken = "access-reg",
                    refreshToken = "refresh-reg",
                    tokenType = "Bearer",
                    expiresIn = 900L,
                    userId = uid,
                    email = email,
                    fullName = fullName
                )
            )
        }

        override suspend fun login(
            email: String,
            password: String,
            rememberMe: Boolean
        ): AuthResult {
            lastRememberMe = rememberMe
            val uid = "user-$email"
            currentUserId = uid
            hasActiveSession = true
            return AuthResult.Success(
                AuthResponseDto(
                    accessToken = "access-1",
                    refreshToken = "refresh-1",
                    tokenType = "Bearer",
                    expiresIn = 900L,
                    userId = uid,
                    email = email,
                    fullName = "Nguyễn Minh"
                )
            )
        }

        override suspend fun logout() {
            logoutCalled = true
            hasActiveSession = false
            currentUserId = null
        }
    }
}
