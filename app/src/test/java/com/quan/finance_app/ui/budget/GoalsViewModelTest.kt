package com.quan.finance_app.ui.budget

import com.quan.finance_app.data.BudgetRepository
import com.quan.finance_app.data.BudgetResult
import com.quan.finance_app.data.DepositResultItem
import com.quan.finance_app.data.GoalDepositItem
import com.quan.finance_app.data.GoalStatus
import com.quan.finance_app.data.SavingsGoalItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GoalsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepo: FakeGoalsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeGoalsRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_splitsActiveAndCompletedAndComputesTotalAccumulated() = runTest {
        fakeRepo.goals.addAll(
            listOf(
                makeGoal("g-1", "Du lịch Đà Lạt", 10_000_000L, 4_500_000L, GoalStatus.ACTIVE),
                makeGoal("g-2", "Quỹ dự phòng", 50_000_000L, 11_000_000L, GoalStatus.ACTIVE),
                makeGoal("g-3", "Laptop mới", 20_000_000L, 20_000_000L, GoalStatus.COMPLETED)
            )
        )

        val vm = GoalsViewModel(
            budgetRepository = fakeRepo,
            walletsProvider = { listOf(WalletOption("w-1", "Tiền mặt")) }
        )

        vm.load()

        val state = vm.state.value
        assertFalse(state.loading)
        assertEquals(2, state.activeGoals.size)
        assertEquals(1, state.completedGoals.size)
        assertEquals(35_500_000L, state.totalSaved)
        assertEquals(80_000_000L, state.totalTarget)
        assertEquals(1, state.wallets.size)
    }

    @Test
    fun deposit_updatesSavedAmountAndMovesGoalToCompletedWhenTargetReached() = runTest {
        fakeRepo.goals.add(
            makeGoal("g-1", "Du lịch Đà Lạt", 10_000_000L, 4_500_000L, GoalStatus.ACTIVE)
        )
        val vm = GoalsViewModel(
            budgetRepository = fakeRepo,
            walletsProvider = { emptyList() }
        )
        vm.load()

        var callbackInvoked = false
        vm.deposit("g-1", 5_500_000L, "Hoàn thành") { callbackInvoked = true }

        val state = vm.state.value
        assertTrue(callbackInvoked)
        assertFalse(state.submitting)
        assertTrue(state.activeGoals.isEmpty())
        assertEquals(1, state.completedGoals.size)
        assertEquals(10_000_000L, state.completedGoals.first().savedAmount)
        assertEquals(10_000_000L, state.totalSaved)
    }

    @Test
    fun createUpdateAndDeleteGoal_updatesState() = runTest {
        val vm = GoalsViewModel(
            budgetRepository = fakeRepo,
            walletsProvider = { emptyList() }
        )
        vm.load()

        vm.createGoal(name = "Du lịch Đà Lạt", targetAmount = 10_000_000L)
        assertEquals(1, vm.state.value.activeGoals.size)
        val id = vm.state.value.activeGoals.first().id

        vm.updateGoal(
            goalId = id,
            name = "Du lịch Phú Quốc",
            targetAmount = 12_000_000L,
            icon = "CardGiftcard",
            color = "#0FA162",
            targetDate = LocalDate.of(2026, 12, 31),
            wallet = null,
            priority = 1,
            note = null
        )
        assertEquals("Du lịch Phú Quốc", vm.state.value.activeGoals.first().name)
        assertEquals(12_000_000L, vm.state.value.activeGoals.first().targetAmount)

        vm.deleteGoal(id)
        assertTrue(vm.state.value.allGoals.isEmpty())
    }

    private fun makeGoal(
        id: String,
        name: String,
        target: Long,
        saved: Long,
        status: GoalStatus
    ) = SavingsGoalItem(
        id = id,
        name = name,
        icon = "CardGiftcard",
        color = "#0FA162",
        targetAmount = target,
        savedAmount = saved,
        targetDate = LocalDate.of(2026, 12, 31),
        walletId = null,
        walletName = null,
        priority = 1,
        status = status,
        completedAt = if (status == GoalStatus.COMPLETED) Instant.now() else null,
        note = null
    )

    private class FakeGoalsRepository : BudgetRepository() {
        val goals = mutableListOf<SavingsGoalItem>()

        override suspend fun getGoals(status: GoalStatus?): BudgetResult<List<SavingsGoalItem>> {
            val filtered = if (status == null) goals.toList() else goals.filter { it.status == status }
            return BudgetResult.Success(filtered)
        }

        override suspend fun createGoal(
            name: String,
            icon: String?,
            color: String?,
            targetAmount: Long,
            targetDate: LocalDate?,
            walletId: String?,
            walletName: String?,
            priority: Int?,
            note: String?
        ): BudgetResult<SavingsGoalItem> {
            val created = SavingsGoalItem(
                id = "g-${goals.size + 1}",
                name = name,
                icon = icon ?: "CardGiftcard",
                color = color ?: "#0FA162",
                targetAmount = targetAmount,
                savedAmount = 0L,
                targetDate = targetDate,
                walletId = walletId,
                walletName = walletName,
                priority = priority ?: 0,
                status = GoalStatus.ACTIVE,
                completedAt = null,
                note = note
            )
            goals.add(created)
            return BudgetResult.Success(created)
        }

        override suspend fun updateGoal(
            id: String,
            name: String?,
            icon: String?,
            color: String?,
            targetAmount: Long?,
            targetDate: LocalDate?,
            walletId: String?,
            walletName: String?,
            priority: Int?,
            note: String?
        ): BudgetResult<SavingsGoalItem> {
            val idx = goals.indexOfFirst { it.id == id }
            if (idx < 0) return BudgetResult.Failure("Không tìm thấy", "GOAL_NOT_FOUND")
            val old = goals[idx]
            val updated = old.copy(
                name = name ?: old.name,
                targetAmount = targetAmount ?: old.targetAmount,
                targetDate = targetDate ?: old.targetDate,
                priority = priority ?: old.priority
            )
            goals[idx] = updated
            return BudgetResult.Success(updated)
        }

        override suspend fun deleteGoal(id: String): BudgetResult<Unit> {
            goals.removeAll { it.id == id }
            return BudgetResult.Success(Unit)
        }

        override suspend fun depositToGoal(
            goalId: String,
            amount: Long,
            note: String?
        ): BudgetResult<DepositResultItem> {
            val idx = goals.indexOfFirst { it.id == goalId }
            if (idx < 0) return BudgetResult.Failure("Không tìm thấy", "GOAL_NOT_FOUND")
            val old = goals[idx]
            val newSaved = old.savedAmount + amount
            val completed = newSaved >= old.targetAmount
            val updated = old.copy(
                savedAmount = newSaved,
                status = if (completed) GoalStatus.COMPLETED else old.status,
                completedAt = if (completed) Instant.now() else old.completedAt
            )
            goals[idx] = updated
            return BudgetResult.Success(
                DepositResultItem(
                    deposit = GoalDepositItem("dep-1", amount, Instant.now(), note),
                    goal = updated
                )
            )
        }
    }
}
