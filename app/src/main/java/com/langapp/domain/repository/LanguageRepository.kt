package com.langapp.domain.repository

import com.langapp.domain.model.Language

data class LanguagePage(
    val languages: List<Language>,
    val hasNext: Boolean,
)

interface LanguageRepository {
    suspend fun getLanguages(search: String? = null): Result<List<Language>>
    suspend fun getLanguagesPaged(search: String?, page: Int): Result<LanguagePage>
    suspend fun getLanguage(id: Int): Result<Language>
}