package com.quan.finance_app.ui.budget

import com.quan.finance_app.data.BudgetRepository
import com.quan.finance_app.data.BudgetResult
import com.quan.finance_app.data.CategoryLimitItem
import com.quan.finance_app.data.CurrentBudgetData
import com.quan.finance_app.data.LimitStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepo: FakeBudgetRepository

    private val catFood = ExpenseCategoryOption("cat-food", "Ăn uống", "Storefront", "#EF4444")
    private val catMove = ExpenseCategoryOption("cat-move", "Di chuyển", "DirectionsCar", "#F97316")
    private val catShop = ExpenseCategoryOption("cat-shop", "Mua sắm", "ShoppingBag", "#EAB308")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeBudgetRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_mapsBudgetAndExcludesCategoriesThatAlreadyHaveLimit() = runTest {
        fakeRepo.limits.add(
            CategoryLimitItem(
                id = "lim-1",
                categoryId = catFood.id,
                categoryName = catFood.name,
                categoryIcon = catFood.icon,
                categoryColor = catFood.color,
                limitAmount = 3_000_000L,
                spentAmount = 500_000L,
                alertThreshold = 80,
                status = LimitStatus.OK
            )
        )

        val vm = BudgetViewModel(
            budgetRepository = fakeRepo,
            expenseCategoriesProvider = { listOf(catFood, catMove, catShop) }
        )

        vm.load()

        val state = vm.state.value
        assertFalse(state.loading)
        assertNull(state.errorMessage)
        assertEquals(3_000_000L, state.budget?.totalLimit)
        assertEquals(500_000L, state.budget?.totalSpent)
        assertEquals(1, state.budget?.limits?.size)
        assertEquals(listOf(catMove, catShop), state.availableCategories)
    }

    @Test
    fun createLimit_reloadsBudgetAndUpdatesAvailableCategories() = runTest {
        val vm = BudgetViewModel(
            budgetRepository = fakeRepo,
            expenseCategoriesProvider = { listOf(catFood, catMove) }
        )
        vm.load()
        vm.openCreateSheet()
        assertTrue(vm.state.value.sheetOpen)

        vm.createLimit(catFood, 3_000_000L, 80)

        val state = vm.state.value
        assertFalse(state.submitting)
        assertFalse(state.sheetOpen)
        assertEquals(1, state.budget?.limits?.size)
        assertEquals("Ăn uống", state.budget?.limits?.first()?.categoryName)
        assertEquals(listOf(catMove), state.availableCategories)
    }

    @Test
    fun createLimit_whenConflict409_keepsSheetOpenAndShowsFriendlyMessage() = runTest {
        fakeRepo.nextCreateFailure = BudgetResult.Failure("Duplicate", "LIMIT_ALREADY_EXISTS")
        val vm = BudgetViewModel(
            budgetRepository = fakeRepo,
            expenseCategoriesProvider = { listOf(catFood, catMove) }
        )
        vm.load()
        vm.openCreateSheet()

        vm.createLimit(catFood, 2_000_000L, 80)

        val state = vm.state.value
        assertTrue(state.sheetOpen)
        assertNotNull(state.actionError)
        assertTrue(state.actionError!!.contains("đã có hạn mức"))
    }

    @Test
    fun updateAndDeleteLimit_reloadBudget() = runTest {
        fakeRepo.limits.add(
            CategoryLimitItem(
                id = "lim-1",
                categoryId = catFood.id,
                categoryName = catFood.name,
                categoryIcon = catFood.icon,
                categoryColor = catFood.color,
                limitAmount = 3_000_000L,
                spentAmount = 500_000L,
                alertThreshold = 80,
                status = LimitStatus.OK
            )
        )
        val vm = BudgetViewModel(
            budgetRepository = fakeRepo,
            expenseCategoriesProvider = { listOf(catFood, catMove) }
        )
        vm.load()

        vm.openEditSheet(vm.state.value.budget!!.limits.first())
        vm.updateLimit("lim-1", 4_000_000L, 85)
        assertEquals(4_000_000L, vm.state.value.budget?.limits?.first()?.limitAmount)
        assertEquals(85, vm.state.value.budget?.limits?.first()?.alertThreshold)

        vm.deleteLimit("lim-1")
        assertTrue(vm.state.value.budget?.limits?.isEmpty() == true)
        assertEquals(listOf(catFood, catMove), vm.state.value.availableCategories)
    }

    private class FakeBudgetRepository : BudgetRepository() {
        val limits = mutableListOf<CategoryLimitItem>()
        var nextCreateFailure: BudgetResult.Failure? = null

        override suspend fun getCurrentBudget(period: String?): BudgetResult<CurrentBudgetData> {
            return BudgetResult.Success(
                CurrentBudgetData(
                    periodKey = period ?: "2026-11",
                    periodStart = LocalDate.of(2026, 11, 1),
                    periodEnd = LocalDate.of(2026, 11, 30),
                    daysLeft = 9,
                    totalLimit = limits.sumOf { it.limitAmount },
                    totalSpent = limits.sumOf { it.spentAmount },
                    limits = limits.toList()
                )
            )
        }

        override suspend fun createLimit(
            categoryId: String,
            categoryName: String,
            categoryIcon: String?,
            categoryColor: String?,
            limitAmount: Long,
            alertThreshold: Int,
            periodKey: String?
        ): BudgetResult<CategoryLimitItem> {
            nextCreateFailure?.let {
                nextCreateFailure = null
                return it
            }
            val item = CategoryLimitItem(
                id = "lim-${limits.size + 1}",
                categoryId = categoryId,
                categoryName = categoryName,
                categoryIcon = categoryIcon,
                categoryColor = categoryColor,
                limitAmount = limitAmount,
                spentAmount = 0L,
                alertThreshold = alertThreshold,
                status = LimitStatus.OK
            )
            limits.add(item)
            return BudgetResult.Success(item)
        }

        override suspend fun updateLimit(
            id: String,
            limitAmount: Long?,
            alertThreshold: Int?
        ): BudgetResult<CategoryLimitItem> {
            val idx = limits.indexOfFirst { it.id == id }
            if (idx < 0) return BudgetResult.Failure("Không tìm thấy", "LIMIT_NOT_FOUND")
            val old = limits[idx]
            val updated = old.copy(
                limitAmount = limitAmount ?: old.limitAmount,
                alertThreshold = alertThreshold ?: old.alertThreshold
            )
            limits[idx] = updated
            return BudgetResult.Success(updated)
        }

        override suspend fun deleteLimit(id: String): BudgetResult<Unit> {
            limits.removeAll { it.id == id }
            return BudgetResult.Success(Unit)
        }
    }
}
