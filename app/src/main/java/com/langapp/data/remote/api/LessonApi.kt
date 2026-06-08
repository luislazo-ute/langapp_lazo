package com.langapp.data.remote.api

import com.langapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface LessonApi {
    @GET("lessons/")
    suspend fun getLessons(@Query("level") levelId: Int? = null): Response<PaginatedDto<LessonDto>>

    @GET("lessons/{id}/")
    suspend fun getLesson(@Path("id") id: Int): Response<LessonDto>

    @POST("lessons/")
    suspend fun create(@Body body: LessonWriteDto): Response<LessonDto>

    @PATCH("lessons/{id}/")
    suspend fun update(@Path("id") id: Int, @Body body: LessonWriteDto): Response<LessonDto>

    @DELETE("lessons/{id}/")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}