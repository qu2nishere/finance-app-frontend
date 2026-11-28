package com.quan.finance_app.data.remote

/**
 * Các lớp này phải khớp JSON của backend TỪNG KÝ TỰ — Gson map theo tên field.
 * Đối chiếu: backend/user-service/.../auth/dto/
 *
 * Lưu ý về Gson: nếu JSON thiếu một field, Gson vẫn gán null cho nó, kể cả khi
 * kiểu Kotlin khai báo là non-null. Nên field nào backend có thể không trả về
 * thì khai báo nullable (như refreshToken — bước 7 mới có).
 */

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val fullName: String,
    val timezone: String? = null
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class GoogleLoginRequestDto(
    val idToken: String,
    val timezone: String? = null
)

data class RefreshRequestDto(
    val refreshToken: String
)

data class ForgotPasswordRequestDto(
    val email: String
)

data class VerifyResetCodeRequestDto(
    val email: String,
    val code: String
)

data class ResetPasswordRequestDto(
    val email: String,
    val code: String,
    val newPassword: String
)

data class AuthResponseDto(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val userId: String,
    val email: String,
    val fullName: String,
    val refreshToken: String? = null,
    val timezone: String? = null
)

/**
 * Body lỗi chuẩn RFC 9457 mà GlobalExceptionHandler bên backend trả về.
 * `errors` là map field → thông báo, chỉ có ở lỗi 400 do @Valid.
 */
data class ProblemDetailDto(
    val status: Int = 0,
    val title: String? = null,
    val detail: String? = null,
    val code: String? = null,
    val errors: Map<String, String>? = null
)
