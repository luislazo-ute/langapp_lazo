package com.langapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.langapp.domain.model.UserProfile

data class UserProfileDto(
    val id: Int,
    @SerializedName("user") val userId: Int,
    val username: String?,
    val email: String?,
    @SerializedName("language") val languageId: Int?,
    @SerializedName("language_nombre") val languageNombre: String?,
    @SerializedName("level") val levelId: Int?,
    @SerializedName("level_nombre") val levelNombre: String?,
    @SerializedName("racha_dias") val rachaDias: Int,
    @SerializedName("xp_total") val xpTotal: Int,
    @SerializedName("fecha_inicio") val fechaInicio: String,
    @SerializedName("is_staff") val isStaff: Boolean = false,
)

fun UserProfileDto.toDomain() = UserProfile(
    id = id,
    userId = userId,
    username = username,
    email = email,
    languageId = languageId,
    languageNombre = languageNombre,
    levelId = levelId,
    levelNombre = levelNombre,
    rachaDias = rachaDias,
    xpTotal = xpTotal,
    fechaInicio = fechaInicio,
    isStaff = isStaff,
)