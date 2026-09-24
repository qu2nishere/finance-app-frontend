package com.quan.finance_app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit sinh phần cài đặt cho interface này lúc chạy.
 *
 * Trả `Response<T>` thay vì `T` để còn đọc được body của lỗi 400/401/409.
 * Nếu khai báo trả thẳng `T`, Retrofit sẽ ném HttpException và ta mất nội dung lỗi.
 */
interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<AuthResponseDto>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<AuthResponseDto>
}
