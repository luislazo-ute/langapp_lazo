package com.langapp.data.repository

import com.langapp.data.remote.api.LevelApi
import com.langapp.data.remote.dto.toDomain
import com.langapp.domain.model.Level
import com.langapp.domain.repository.LevelRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LevelRepositoryImpl @Inject constructor(
    private val api: LevelApi,
) : LevelRepository {
    override suspend fun getLevels(languageId: Int?): Result<List<Level>> = runCatching {
        val r = api.getLevels(languageId)
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() } else error("Error ${r.code()}")
    }
    override suspend fun getLevel(id: Int): Result<Level> = runCatching {
        val r = api.getLevel(id)
        if (r.isSuccessful) r.body()!!.toDomain() else error("Error ${r.code()}")
    }
}