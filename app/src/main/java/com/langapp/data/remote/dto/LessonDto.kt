package com.langapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.langapp.domain.model.Lesson

data class LessonDto(
    val id: Int,
    @SerializedName("level") val levelId: Int,
    @SerializedName("level_nombre") val levelNombre: String?,
    val titulo: String,
    val descripcion: String,
    val orden: Int,
    val icono: String,
)

fun LessonDto.toDomain() = Lesson(
    id = id, levelId = levelId, levelNombre = levelNombre,
    titulo = titulo, descripcion = descripcion,
    orden = orden, icono = icono,
)
