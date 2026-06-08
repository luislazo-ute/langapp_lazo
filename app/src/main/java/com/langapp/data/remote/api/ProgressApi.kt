package com.langapp.data.remote.api

import com.langapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ProgressApi {
    @GET("progress/")
    suspend fun getProgress(
        @Query("correcto") correcto: Boolean? = null,
    ): Response<PaginatedDto<UserProgressDto>>

    @POST("progress/")
    suspend fun submitAnswer(
        @Body body: SubmitAnswerRequest,
    ): Response<UserProgressDto>
}
