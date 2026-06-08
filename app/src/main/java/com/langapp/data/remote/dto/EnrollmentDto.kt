package com.langapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.langapp.domain.model.Enrollment

data class EnrollmentDto(
    val id: Int,
    @SerializedName("level") val levelId: Int,
    @SerializedName("level_nombre") val levelNombre: String?,
    @SerializedName("language_nombre") val languageNombre: String?,
    @SerializedName("inscrito_en") val inscritoEn: String,
    val completado: Boolean,
)

data class EnrollmentRequest(val level: Int)

fun EnrollmentDto.toDomain() = Enrollment(
    id = id, levelId = levelId, levelNombre = levelNombre,
    languageNombre = languageNombre, inscritoEn = inscritoEn,
    completado = completado,
)
