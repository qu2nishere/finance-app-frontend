package com.quan.finance_app.data.remote

import retrofit2.Call
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

    @POST("api/v1/auth/google")
    suspend fun loginWithGoogle(@Body body: GoogleLoginRequestDto): Response<AuthResponseDto>

    /** Ban dong bo cho OkHttp Authenticator va restoreSession tren luong IO co khoa dong bo. */
    @POST("api/auth/refresh")
    fun refreshCall(@Body body: RefreshRequestDto): Call<AuthResponseDto>

    @POST("api/auth/logout")
    suspend fun logout(@Body body: RefreshRequestDto): Response<Unit>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequestDto): Response<Unit>

    @POST("api/auth/reset-password/verify")
    suspend fun verifyResetCode(@Body body: VerifyResetCodeRequestDto): Response<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequestDto): Response<Unit>
}
