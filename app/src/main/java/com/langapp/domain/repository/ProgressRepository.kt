package com.langapp.domain.repository

import com.langapp.domain.model.UserProgress

interface ProgressRepository {
    suspend fun getProgress(): Result<List<UserProgress>>
    suspend fun submitAnswer(exerciseId: Int, correcto: Boolean): Result<UserProgress>
}