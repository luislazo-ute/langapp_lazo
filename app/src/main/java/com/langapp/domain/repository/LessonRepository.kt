package com.langapp.domain.repository

import com.langapp.domain.model.Lesson

interface LessonRepository {
    suspend fun getLessons(levelId: Int?): Result<List<Lesson>>
    suspend fun getLesson(id: Int): Result<Lesson>
}