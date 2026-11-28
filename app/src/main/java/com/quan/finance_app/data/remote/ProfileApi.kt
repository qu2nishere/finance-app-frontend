package com.quan.finance_app.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

data class TimezoneRequest(val timezone: String)
data class ProfileDto(val userId: String, val timezone: String)
data class UpdatedProfileDto(val userId: String, val timezone: String, val accessToken: String)

interface ProfileApi {
    @GET("api/users/me")
    fun getProfile(): Call<ProfileDto>

    @PATCH("api/users/me")
    fun updateTimezone(@Body body: TimezoneRequest): Call<UpdatedProfileDto>
}
