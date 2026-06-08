package com.langapp.domain.repository

import com.langapp.data.remote.dto.*

interface AdminRepository {
    suspend fun createLanguage(body: LanguageWriteDto): Result<Unit>
    suspend fun updateLanguage(id: Int, body: LanguageWriteDto): Result<Unit>
    suspend fun deleteLanguage(id: Int): Result<Unit>

    suspend fun createLevel(body: LevelWriteDto): Result<Unit>
    suspend fun updateLevel(id: Int, body: LevelWriteDto): Result<Unit>
    suspend fun deleteLevel(id: Int): Result<Unit>

    suspend fun createLesson(body: LessonWriteDto): Result<Unit>
    suspend fun updateLesson(id: Int, body: LessonWriteDto): Result<Unit>
    suspend fun deleteLesson(id: Int): Result<Unit>

    suspend fun createExercise(body: ExerciseWriteDto): Result<Unit>
    suspend fun updateExercise(id: Int, body: ExerciseWriteDto): Result<Unit>
    suspend fun deleteExercise(id: Int): Result<Unit>
}