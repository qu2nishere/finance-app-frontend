package com.quan.finance_app.data

import com.quan.finance_app.data.remote.*
import java.util.UUID

class LedgerRepository(private val api: LedgerApi) {

    private var cachedCategories: List<CategoryDto>? = null

    companion object {
        val DEFAULT_CATEGORIES: List<CategoryDto> = listOf(
            // 11 Danh mục Chi mặc định
            CategoryDto("11111111-0000-4000-8000-000000000001", null, "Ăn uống", "EXPENSE", "Storefront", "#EF4444", true, 0),
            CategoryDto("11111111-0000-4000-8000-000000000002", null, "Di chuyển", "EXPENSE", "LocalShipping", "#F97316", true, 0),
            CategoryDto("11111111-0000-4000-8000-000000000003", null, "Mua sắm", "EXPENSE", "ShoppingBag", "#EAB308", true, 0),
            CategoryDto("11111111-0000-4000-8000-000000000004", null, "Giải trí", "EXPENSE", "LocalFireDepartment", "#16A34A", true, 0),
            CategoryDto("11111111-0000-4000-8000-000000000005", null, "Sức khoẻ", "EXPENSE", "FavoriteBorder", "#F97316", true, 0),
            CategoryDto("11111111-0000-4000-8000-000000000006", null, "Hoá đơn & nhà ở", "EXPENSE", "ReceiptLong", "#64748B", true, 0),
            CategoryDto("11111111-0000-4000-8000-000000000007", null, "Điện thoại & internet", "EXPENSE", "PhoneIphone", "#0EA5E9", true, 0),
            CategoryDto("11111111-0000-4000-8000-000000000008", null, "Quà tặng", "EXPENSE", "CardGiftcard", "#EC4899", true, 0),
            CategoryDto("11111111-0000-4000-8000-000000000009", null, "Thú cưng", "EXPENSE", "Inventory2", "#A855F7", true, 0),
            CategoryDto("11111111-0000-4000-8000-00000000000a", null, "Giáo dục", "EXPENSE", "Category", "#2563EB", true, 0),
            CategoryDto("11111111-0000-4000-8000-00000000000b", null, "Khác", "EXPENSE", "Category", "#94A3B8", true, 0),

            // 6 Danh mục Thu mặc định
            CategoryDto("22222222-0000-4000-8000-000000000001", null, "Lương", "INCOME", "Payments", "#16A34A", true, 0),
            CategoryDto("22222222-0000-4000-8000-000000000002", null, "Thưởng", "INCOME", "CardGiftcard", "#16A34A", true, 0),
            CategoryDto("22222222-0000-4000-8000-000000000003", null, "Freelance", "INCOME", "Storefront", "#16A34A", true, 0),
            CategoryDto("22222222-0000-4000-8000-000000000004", null, "Đầu tư", "INCOME", "TrendingUp", "#16A34A", true, 0),
            CategoryDto("22222222-0000-4000-8000-000000000005", null, "Được tặng", "INCOME", "Redeem", "#16A34A", true, 0),
            CategoryDto("22222222-0000-4000-8000-000000000006", null, "Khác", "INCOME", "Category", "#94A3B8", true, 0)
        )

        val DEFAULT_WALLETS: List<WalletDto> = listOf(
            WalletDto(
                id = "00000000-0000-4000-8000-000000000001",
                name = "Tiền mặt",
                kind = "CASH",
                currencyCode = "VND",
                initialBalance = 0L,
                currentBalance = 0L,
                icon = "AccountBalanceWallet",
                color = "#2563EB",
                includeInTotal = true,
                displayOrder = 1,
                note = null
            )
        )
    }

    suspend fun getCategories(forceRefresh: Boolean = false): Result<List<CategoryDto>> {
        if (!forceRefresh && cachedCategories != null && cachedCategories!!.isNotEmpty()) {
            return Result.success(cachedCategories!!)
        }
        return runCatching {
            val list = api.getCategories()
            if (list.isNotEmpty()) {
                cachedCategories = list
                list
            } else {
                cachedCategories = DEFAULT_CATEGORIES
                DEFAULT_CATEGORIES
            }
        }.recover {
            // Khi gặp lỗi kết nối hoặc server chưa có dữ liệu, trả về danh sách mặc định
            cachedCategories = DEFAULT_CATEGORIES
            DEFAULT_CATEGORIES
        }
    }

    suspend fun createCategory(body: CreateCategoryDto): Result<CategoryDto> {
        return runCatching {
            val created = api.createCategory(body)
            cachedCategories = (cachedCategories ?: emptyList()) + created
            created
        }.recover {
            // Fallback khi offline hoặc lỗi mạng: tạo cục bộ để không gián đoạn trải nghiệm người dùng
            val local = CategoryDto(
                id = UUID.randomUUID().toString(),
                parentId = null,
                name = body.name,
                flow = body.flow,
                icon = body.icon ?: "Category",
                color = body.color ?: (if (body.flow == "EXPENSE") "#EF4444" else "#16A34A"),
                system = false,
                usageCount = 0
            )
            cachedCategories = (cachedCategories ?: emptyList()) + local
            local
        }
    }

    suspend fun getWallets(): Result<List<WalletDto>> {
        return runCatching {
            val list = api.getWallets()
            if (list.isNotEmpty()) list else DEFAULT_WALLETS
        }.recover {
            DEFAULT_WALLETS
        }
    }

    suspend fun createWallet(body: CreateWalletDto): Result<WalletDto> {
        return runCatching {
            api.createWallet(body)
        }.recover {
            WalletDto(
                id = UUID.randomUUID().toString(),
                name = body.name,
                kind = body.kind,
                currencyCode = "VND",
                initialBalance = body.initialBalance,
                currentBalance = body.initialBalance,
                icon = body.icon ?: "AccountBalanceWallet",
                color = body.color ?: "#2563EB",
                includeInTotal = true,
                displayOrder = 99,
                note = body.note
            )
        }
    }

    suspend fun getMonthlySummary(): Result<MonthlySummaryDto> {
        return runCatching { api.getMonthlySummary() }
    }

    suspend fun getRecentTransactions(size: Int = 5): Result<List<TransactionDto>> {
        return runCatching {
            val page = api.getTransactions(page = 0, size = size)
            page.content
        }
    }

    suspend fun getTransactionsPaged(page: Int = 0, size: Int = 20): Result<PageDto<TransactionDto>> {
        return runCatching {
            api.getTransactions(page = page, size = size)
        }
    }

    suspend fun createTransaction(body: CreateTransactionDto): Result<TransactionDto> {
        return runCatching { api.createTransaction(body) }
    }

    suspend fun deleteTransaction(id: String): Result<Unit> {
        return runCatching { api.deleteTransaction(id) }
    }
}
