package com.quan.finance_app.data.remote

import com.quan.finance_app.BuildConfig
import com.quan.finance_app.data.ServiceLocator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // 8080 = qua Gateway Nginx (dieu huong toi user/ledger/budget/report-service)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private fun createLogger(level: HttpLoggingInterceptor.Level): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            this.level = level
            redactHeader("Authorization")
        }

    private val bodyLogger = createLogger(HttpLoggingInterceptor.Level.BODY)
    private val headerLogger = createLogger(HttpLoggingInterceptor.Level.HEADERS)

    /** Không in body các endpoint xác thực ra Logcat vì chứa accessToken và refreshToken. */
    private val safeLogging = Interceptor { chain ->
        val path = chain.request().url.encodedPath
        val isAuthEndpoint = path.startsWith("/api/auth") || path.startsWith("/api/v1/auth") || path == "/api/users/me"
        val delegate = if (isAuthEndpoint) headerLogger else bodyLogger
        delegate.intercept(chain)
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = try {
            ServiceLocator.tokenStore.accessToken
        } catch (_: Exception) {
            null
        }

        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        chain.proceed(request)
    }

    /** Client cơ sở không có authenticator — dùng riêng cho /refresh và /logout để tránh đệ quy. */
    private val baseClient: OkHttpClient = OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(safeLogging)
            }
        }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val refreshRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(baseClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val refreshAuthApi: AuthApi = refreshRetrofit.create(AuthApi::class.java)

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(safeLogging)
            }
        }
        .authenticator(
            TokenAuthenticator(
                tokenStoreProvider = { ServiceLocator.tokenStore },
                refreshApi = refreshAuthApi
            )
        )
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val profileApi: ProfileApi = retrofit.create(ProfileApi::class.java)
    val ledgerApi: LedgerApi = retrofit.create(LedgerApi::class.java)
    val budgetApi: BudgetApi = retrofit.create(BudgetApi::class.java)
    val reportApi: ReportApi = retrofit.create(ReportApi::class.java)
}
