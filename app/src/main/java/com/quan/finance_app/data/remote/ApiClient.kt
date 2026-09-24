package com.quan.finance_app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Nơi duy nhất biết địa chỉ backend.
 *
 * ĐỊA CHỈ PHẢI CHỌN ĐÚNG THEO CÁCH CHẠY:
 *   - Emulator trong Android Studio: 10.0.2.2 chính là "localhost của máy tính".
 *     Gõ localhost trong emulator là trỏ vào chính máy ảo, không phải máy bạn.
 *   - Điện thoại thật cùng WiFi: đổi thành IP LAN của máy tính, ví dụ 192.168.1.12
 *     (xem bằng `ipconfig`). Máy tính phải cho phép cổng đó qua tường lửa.
 *
 * CỔNG:
 *   - 8081 = gọi thẳng user-service (đang chạy bằng .\gradlew.bat bootRun)
 *   - 8080 = qua gateway Nginx (khi đã `docker compose up`)
 */
object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8081/"

    // Level.BODY in cả nội dung request/response ra Logcat (lọc theo tag "OkHttp").
    // Rất tiện lúc học, nhưng nó in ra CẢ TOKEN — nhớ đổi về NONE trước khi phát hành.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}
