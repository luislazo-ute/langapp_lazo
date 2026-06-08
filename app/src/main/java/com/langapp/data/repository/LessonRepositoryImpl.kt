package com.langapp.data.repository

import com.langapp.data.remote.api.LessonApi
import com.langapp.data.remote.dto.toDomain
import com.langapp.domain.model.Lesson
import com.langapp.domain.repository.LessonRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonRepositoryImpl @Inject constructor(
    private val api: LessonApi,
) : LessonRepository {
    override suspend fun getLessons(levelId: Int?): Result<List<Lesson>> = runCatching {
        val r = api.getLessons(levelId)
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() } else error("Error ${r.code()}")
    }
    override suspend fun getLesson(id: Int): Result<Lesson> = runCatching {
        val r = api.getLesson(id)
        if (r.isSuccessful) r.body()!!.toDomain() else error("Error ${r.code()}")
    }
}