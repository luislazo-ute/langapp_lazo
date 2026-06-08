package com.langapp.data.repository

import com.langapp.data.remote.api.ProfileApi
import com.langapp.data.remote.dto.toDomain
import com.langapp.domain.model.UserProfile
import com.langapp.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
) : ProfileRepository {
    override suspend fun getMyProfile(): Result<UserProfile> = runCatching {
        val r = api.getMyProfile()
        if (r.isSuccessful) r.body()!!.toDomain() else error("Error ${r.code()}")
    }
}