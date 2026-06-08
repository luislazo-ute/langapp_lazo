package com.langapp.data.remote.api

import com.langapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ProfileApi {
    @GET("profiles/me/")
    suspend fun getMyProfile(): Response<UserProfileDto>

    @PATCH("profiles/me/")
    suspend fun updateProfile(
        @Body body: UserProfileDto,
    ): Response<UserProfileDto>
}
