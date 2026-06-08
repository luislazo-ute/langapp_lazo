package com.langapp.data.remote.api

import com.langapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface LevelApi {
    @GET("levels/")
    suspend fun getLevels(@Query("language") languageId: Int? = null): Response<PaginatedDto<LevelDto>>

    @GET("levels/{id}/")
    suspend fun getLevel(@Path("id") id: Int): Response<LevelDto>

    @POST("levels/")
    suspend fun create(@Body body: LevelWriteDto): Response<LevelDto>

    @PATCH("levels/{id}/")
    suspend fun update(@Path("id") id: Int, @Body body: LevelWriteDto): Response<LevelDto>

    @DELETE("levels/{id}/")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}