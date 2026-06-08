package com.langapp.domain.repository

import com.langapp.domain.model.Language

interface LanguageRepository {
    suspend fun getLanguages(search: String? = null): Result<List<Language>>
    suspend fun getLanguage(id: Int): Result<Language>
}