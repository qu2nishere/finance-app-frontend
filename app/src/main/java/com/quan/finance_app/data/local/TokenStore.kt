package com.quan.finance_app.data.local

import android.content.Context

/**
 * Giữ token của phiên đăng nhập.
 *
 * Access token chỉ nằm trong RAM: nó sống 15 phút, mất khi tắt app cũng không sao.
 * Refresh token cần sống lâu nên phải ghi xuống đĩa.
 *
 * ⚠ Bản này dùng SharedPreferences THƯỜNG — file XML nằm trần trong bộ nhớ app.
 * Máy đã root là đọc được. Bước 14 trong docs/steps sẽ đổi sang
 * EncryptedSharedPreferences (khoá nằm trong Android Keystore).
 */
class TokenStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    @Volatile
    var accessToken: String? = null
        private set

    val refreshToken: String?
        get() = prefs.getString(KEY_REFRESH, null)

    val isLoggedIn: Boolean
        get() = accessToken != null || refreshToken != null

    fun save(access: String, refresh: String?) {
        accessToken = access
        if (refresh != null) {
            prefs.edit().putString(KEY_REFRESH, refresh).apply()
        }
    }

    fun clear() {
        accessToken = null
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS = "auth_prefs"
        const val KEY_REFRESH = "refresh_token"
    }
}
