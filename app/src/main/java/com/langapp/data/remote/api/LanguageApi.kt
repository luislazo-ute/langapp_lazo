package com.langapp.data.remote.api

import com.langapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface LanguageApi {
    @GET("languages/")
    suspend fun getLanguages(@Query("search") search: String? = null): Response<PaginatedDto<LanguageDto>>

    @GET("languages/{id}/")
    suspend fun getLanguage(@Path("id") id: Int): Response<LanguageDto>

    @POST("languages/")
    suspend fun create(@Body body: LanguageWriteDto): Response<LanguageDto>

    @PATCH("languages/{id}/")
    suspend fun update(@Path("id") id: Int, @Body body: LanguageWriteDto): Response<LanguageDto>

    @DELETE("languages/{id}/")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}