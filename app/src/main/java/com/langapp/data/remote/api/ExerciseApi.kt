package com.langapp.data.remote.api

import com.langapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ExerciseApi {
    @GET("exercises/")
    suspend fun getExercises(
        @Query("lesson") lessonId: Int? = null,
        @Query("tipo") tipo: String? = null,
    ): Response<PaginatedDto<ExerciseDto>>

    @GET("exercises/{id}/")
    suspend fun getExercise(@Path("id") id: Int): Response<ExerciseDto>

    @POST("exercises/")
    suspend fun create(@Body body: ExerciseWriteDto): Response<ExerciseDto>

    @PATCH("exercises/{id}/")
    suspend fun update(@Path("id") id: Int, @Body body: ExerciseWriteDto): Response<ExerciseDto>

    @DELETE("exercises/{id}/")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}