package com.langapp.data.repository

import com.langapp.data.remote.api.ProgressApi
import com.langapp.data.remote.dto.SubmitAnswerRequest
import com.langapp.data.remote.dto.toDomain
import com.langapp.domain.model.UserProgress
import com.langapp.domain.repository.ProgressRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val api: ProgressApi,
) : ProgressRepository {
    override suspend fun getProgress(): Result<List<UserProgress>> = runCatching {
        val r = api.getProgress()
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() } else error("Error ${r.code()}")
    }
    override suspend fun submitAnswer(exerciseId: Int, correcto: Boolean): Result<UserProgress> = runCatching {
        val r = api.submitAnswer(SubmitAnswerRequest(exerciseId, correcto))
        if (r.isSuccessful) r.body()!!.toDomain() else error("Error ${r.code()}")
    }
}