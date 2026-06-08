package com.langapp.domain.repository

import com.langapp.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getMyProfile(): Result<UserProfile>
}