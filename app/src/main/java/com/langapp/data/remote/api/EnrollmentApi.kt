package com.langapp.data.remote.api

import com.langapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface EnrollmentApi {
    @GET("enrollments/")
    suspend fun getEnrollments(): Response<PaginatedDto<EnrollmentDto>>

    @POST("enrollments/")
    suspend fun enroll(@Body body: EnrollmentRequest): Response<EnrollmentDto>
}
