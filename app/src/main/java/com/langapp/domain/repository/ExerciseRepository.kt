package com.langapp.domain.repository

import com.langapp.domain.model.Exercise

interface ExerciseRepository {
    suspend fun getExercises(lessonId: Int?): Result<List<Exercise>>
    suspend fun getExercise(id: Int): Result<Exercise>
}