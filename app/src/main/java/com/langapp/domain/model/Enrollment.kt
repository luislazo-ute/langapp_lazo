package com.langapp.domain.model

data class Enrollment(
    val id: Int,
    val levelId: Int,
    val levelNombre: String?,
    val languageNombre: String?,
    val inscritoEn: String,
    val completado: Boolean,
)
