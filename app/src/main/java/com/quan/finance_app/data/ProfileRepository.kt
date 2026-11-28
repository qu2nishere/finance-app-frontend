package com.quan.finance_app.data

import com.quan.finance_app.data.local.TokenStore
import com.quan.finance_app.data.remote.ProfileApi
import com.quan.finance_app.data.remote.TimezoneRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ProfileRepository(
    private val api: ProfileApi,
    private val store: TokenStore,
    private val auth: AuthRepository
) {
    suspend fun load(userId: String): String = withContext(Dispatchers.IO) {
        synchronized(store.refreshLock) {
            check(store.userId == userId)
            val profile = body(api.getProfile().execute())
            check(profile.userId == userId)
            if (profile.timezone != AccountTimeZone.current.id) {
                when (auth.refreshStoredSession(store)) {
                    SessionResult.Restored -> check(store.userId == userId)
                    SessionResult.Expired, SessionResult.NoSession -> {
                        store.markSessionExpired()
                        throw IOException("Phiên đăng nhập đã hết hạn")
                    }
                    SessionResult.Offline -> throw IOException("Chưa đồng bộ được hồ sơ")
                }
            }
            AccountTimeZone.current.id
        }
    }

    suspend fun update(userId: String, zone: String): String = withContext(Dispatchers.IO) {
        // Serialize with refresh and logout so the token and zone change together.
        synchronized(store.refreshLock) {
            check(store.userId == userId)
            val profile = body(api.updateTimezone(TimezoneRequest(zone)).execute())
            check(profile.userId == userId && store.userId == userId)
            store.updateAccess(profile.accessToken, profile.timezone)
            profile.timezone
        }
    }

    private fun <T> body(response: Response<T>): T {
        if (!response.isSuccessful) throw HttpException(response)
        return response.body() ?: throw IOException("Thiếu dữ liệu hồ sơ")
    }
}
