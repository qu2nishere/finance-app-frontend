package com.quan.finance_app.data.remote

import com.quan.finance_app.data.local.TokenStore
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

/**
 * Tự động đổi access token mới khi API nghiệp vụ trả 401.
 *
 * - Chỉ thử lại tối đa 1 lần (`priorResponse != null` thì dừng).
 * - Khoá trên `tokenStore.refreshLock` để nhiều request cùng nhận 401 không gọi `/refresh` song song
 *   (tránh kích hoạt phát hiện tái sử dụng ở server).
 * - Gọi `/refresh` qua client riêng không gắn authenticator để không đệ quy.
 */
class TokenAuthenticator(
    private val tokenStoreProvider: () -> TokenStore,
    private val refreshApi: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.priorResponse != null) return null

        val path = response.request.url.encodedPath
        if (path.startsWith("/api/auth/") || path.startsWith("/api/v1/auth/")) {
            return null
        }

        val tokenStore = try {
            tokenStoreProvider()
        } catch (_: Exception) {
            return null
        }

        synchronized(tokenStore.refreshLock) {
            val sentToken = response.request.header("Authorization")
                ?.removePrefix("Bearer ")
                ?.trim()
            val currentToken = tokenStore.accessToken

            // Luồng khác vừa refresh xong -> dùng luôn access token mới, không gọi /refresh lần nữa
            if (!currentToken.isNullOrBlank() && currentToken != sentToken) {
                return response.request.withBearer(currentToken)
            }

            val refresh = tokenStore.refreshToken ?: return expireSession(tokenStore)
            val refreshResponse = try {
                refreshApi.refreshCall(RefreshRequestDto(refresh)).execute()
            } catch (_: IOException) {
                // Mất mạng tạm thời: giữ nguyên phiên để thử lại khi có mạng
                return null
            }

            val body = refreshResponse.body()
            if (refreshResponse.isSuccessful && body != null && body.accessToken.isNotBlank()) {
                tokenStore.save(
                    access = body.accessToken,
                    refresh = body.refreshToken ?: refresh,
                    persist = tokenStore.isPersisted
                )
                return response.request.withBearer(body.accessToken)
            }

            // Chỉ thu hồi phiên trên máy khi server từ chối token (400/401/403), không xoá khi 5xx
            if (refreshResponse.code() in 400..499) {
                return expireSession(tokenStore)
            }
            return null
        }
    }

    private fun expireSession(tokenStore: TokenStore): Request? {
        tokenStore.markSessionExpired()
        return null
    }

    private fun Request.withBearer(token: String): Request =
        newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
}
