package com.langapp.domain.model

data class UserProfile(
    val id: Int,
    val userId: Int,
    val username: String?,
    val email: String?,
    val languageId: Int?,
    val languageNombre: String?,
    val levelId: Int?,
    val levelNombre: String?,
    val rachaDias: Int,
    val xpTotal: Int,
    val fechaInicio: String,
    val isStaff: Boolean = false,   // 👈 ESTA LÍNEA DEBE ESTAR
)