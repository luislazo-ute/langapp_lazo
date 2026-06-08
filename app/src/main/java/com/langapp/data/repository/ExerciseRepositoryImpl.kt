package com.langapp.data.repository

import com.langapp.data.remote.api.ExerciseApi
import com.langapp.data.remote.dto.toDomain
import com.langapp.domain.model.Exercise
import com.langapp.domain.repository.ExerciseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val api: ExerciseApi,
) : ExerciseRepository {
    override suspend fun getExercises(lessonId: Int?): Result<List<Exercise>> = runCatching {
        val r = api.getExercises(lessonId)
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() } else error("Error ${r.code()}")
    }
    override suspend fun getExercise(id: Int): Result<Exercise> = runCatching {
        val r = api.getExercise(id)
        if (r.isSuccessful) r.body()!!.toDomain() else error("Error ${r.code()}")
    }
}