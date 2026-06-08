package com.langapp.data.repository

import com.langapp.data.remote.api.LanguageApi
import com.langapp.data.remote.dto.toDomain
import com.langapp.domain.model.Language
import com.langapp.domain.repository.LanguageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepositoryImpl @Inject constructor(
    private val api: LanguageApi,
) : LanguageRepository {

    override suspend fun getLanguages(search: String?): Result<List<Language>> = runCatching {
        val r = api.getLanguages(search)
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() } else error("Error ${r.code()}")
    }

    override suspend fun getLanguage(id: Int): Result<Language> = runCatching {
        val r = api.getLanguage(id)
        if (r.isSuccessful) r.body()!!.toDomain() else error("Error ${r.code()}")
    }
}