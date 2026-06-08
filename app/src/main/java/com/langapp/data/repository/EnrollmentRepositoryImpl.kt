package com.langapp.data.repository

import com.langapp.data.remote.api.EnrollmentApi
import com.langapp.data.remote.dto.EnrollmentRequest
import com.langapp.data.remote.dto.toDomain
import com.langapp.domain.model.Enrollment
import com.langapp.domain.repository.EnrollmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnrollmentRepositoryImpl @Inject constructor(
    private val api: EnrollmentApi,
) : EnrollmentRepository {
    override suspend fun getEnrollments(): Result<List<Enrollment>> = runCatching {
        val r = api.getEnrollments()
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() } else error("Error ${r.code()}")
    }
    override suspend fun enroll(levelId: Int): Result<Enrollment> = runCatching {
        val r = api.enroll(EnrollmentRequest(levelId))
        if (r.isSuccessful) r.body()!!.toDomain() else error("Error ${r.code()}")
    }
}