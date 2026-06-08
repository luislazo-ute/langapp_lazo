package com.langapp.domain.repository

import com.langapp.domain.model.Level

interface LevelRepository {
    suspend fun getLevels(languageId: Int?): Result<List<Level>>
    suspend fun getLevel(id: Int): Result<Level>
}