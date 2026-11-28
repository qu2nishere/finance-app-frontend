package com.quan.finance_app.data.local

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Giữ token của phiên đăng nhập.
 *
 * - Access token chỉ nằm trong RAM (mất khi tắt app).
 * - Refresh token khi `persist = true` được mã hoá AES-256-GCM qua [TokenCipher] trước khi lưu
 *   vào `SharedPreferences`; khi `persist = false` (bỏ chọn "Ghi nhớ đăng nhập") chỉ giữ trong RAM.
 */
class TokenStore(
    context: Context,
    private val cipher: TokenCipher = TokenCipher()
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Khoá đồng bộ dùng chung giữa restoreSession và TokenAuthenticator để tránh refresh song song. */
    internal val refreshLock = Any()

    @Volatile
    var accessToken: String? = null
        private set

    @Volatile
    private var ramRefreshToken: String? = null

    @Volatile
    private var ramUserId: String? = null

    @Volatile
    var isPersisted: Boolean = false
        private set

    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired: StateFlow<Boolean> = _sessionExpired.asStateFlow()

    init {
        // Dọn key dạng chữ thường của bản cũ nếu còn sót trên máy
        if (prefs.contains(KEY_LEGACY_REFRESH)) {
            prefs.edit().remove(KEY_LEGACY_REFRESH).apply()
        }
        isPersisted = prefs.contains(KEY_REFRESH_ENC)
    }

    @get:Synchronized
    val refreshToken: String?
        get() {
            ramRefreshToken?.let { return it }
            val stored = prefs.getString(KEY_REFRESH_ENC, null) ?: return null
            val decrypted = cipher.decrypt(stored)
            if (decrypted == null) {
                // Giải mã lỗi (mất khoá / bản mã bị sửa) -> xoá ngay để thất bại an toàn
                isPersisted = false
                prefs.edit().remove(KEY_REFRESH_ENC).apply()
                return null
            }
            ramRefreshToken = decrypted
            isPersisted = true
            return decrypted
        }

    val isLoggedIn: Boolean
        get() = accessToken != null || refreshToken != null

    @get:Synchronized
    val userId: String?
        get() = ramUserId ?: prefs.getString(KEY_USER_ID, null)?.also { ramUserId = it }

    @Synchronized
    fun save(
        access: String,
        refresh: String?,
        persist: Boolean = true,
        userId: String? = this.userId
    ) {
        accessToken = access
        ramRefreshToken = refresh
        ramUserId = userId
        isPersisted = persist && refresh != null
        _sessionExpired.value = false

        val editor = prefs.edit().remove(KEY_LEGACY_REFRESH)
        if (refresh != null && persist) {
            val encrypted = try {
                cipher.encrypt(refresh)
            } catch (_: Exception) {
                null
            }
            if (encrypted != null) {
                editor.putString(KEY_REFRESH_ENC, encrypted)
                if (!userId.isNullOrBlank()) {
                    editor.putString(KEY_USER_ID, userId)
                } else {
                    editor.remove(KEY_USER_ID)
                }
            } else {
                isPersisted = false
                editor.remove(KEY_REFRESH_ENC)
                editor.remove(KEY_USER_ID)
            }
        } else {
            editor.remove(KEY_REFRESH_ENC)
            editor.remove(KEY_USER_ID)
        }
        editor.apply()
    }

    @Synchronized
    fun clear() {
        accessToken = null
        ramRefreshToken = null
        ramUserId = null
        isPersisted = false
        prefs.edit().clear().apply()
    }

    fun markSessionExpired() {
        clear()
        _sessionExpired.value = true
    }

    fun consumeSessionExpired() {
        _sessionExpired.value = false
    }

    private companion object {
        const val PREFS = "auth_prefs"
        const val KEY_LEGACY_REFRESH = "refresh_token"
        const val KEY_REFRESH_ENC = "refresh_token_enc"
        const val KEY_USER_ID = "user_id"
    }
}
