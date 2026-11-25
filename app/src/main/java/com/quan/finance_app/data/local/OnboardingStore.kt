package com.quan.finance_app.data.local

import android.content.Context

/**
 * Lưu tập `userId` chưa hoàn tất thiết lập ban đầu (bước 2–3 sau đăng ký).
 *
 * File `onboarding_prefs` tách riêng khỏi `auth_prefs` để không bị xoá khi đăng xuất hay hết hạn phiên,
 * đồng thời loại trừ khỏi backup để cài lại / xoá dữ liệu app là mất sạch đúng như "xoá app" (D3).
 */
class OnboardingStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val lock = Any()

    fun markPending(userId: String) {
        if (userId.isBlank()) return
        synchronized(lock) {
            val updated = prefs.getStringSet(KEY_PENDING_USERS, emptySet()).orEmpty().toMutableSet()
            updated.add(userId)
            prefs.edit().putStringSet(KEY_PENDING_USERS, updated).apply()
        }
    }

    fun isPending(userId: String?): Boolean {
        if (userId.isNullOrBlank()) return false
        synchronized(lock) {
            return prefs.getStringSet(KEY_PENDING_USERS, emptySet()).orEmpty().contains(userId)
        }
    }

    fun clear(userId: String?) {
        if (userId.isNullOrBlank()) return
        synchronized(lock) {
            val current = prefs.getStringSet(KEY_PENDING_USERS, emptySet()).orEmpty()
            if (userId !in current) return
            val updated = current.toMutableSet().apply { remove(userId) }
            prefs.edit().putStringSet(KEY_PENDING_USERS, updated).apply()
        }
    }

    private companion object {
        const val PREFS = "onboarding_prefs"
        const val KEY_PENDING_USERS = "pending_user_ids"
    }
}
