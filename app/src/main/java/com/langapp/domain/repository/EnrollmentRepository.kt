package com.langapp.domain.repository

import com.langapp.domain.model.Enrollment

interface EnrollmentRepository {
    suspend fun getEnrollments(): Result<List<Enrollment>>
    suspend fun enroll(levelId: Int): Result<Enrollment>
}