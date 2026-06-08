package com.langapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.langapp.domain.model.Exercise

data class ExerciseDto(
    val id: Int,
    @SerializedName("lesson") val lessonId: Int,
    val tipo: String,
    @SerializedName("tipo_display") val tipoDisplay: String?,
    val pregunta: String,
    val opciones: List<String>,
    val puntos: Int,
    val orden: Int,
)

fun ExerciseDto.toDomain() = Exercise(
    id = id, lessonId = lessonId, tipo = tipo,
    tipoDisplay = tipoDisplay, pregunta = pregunta,
    opciones = opciones, puntos = puntos, orden = orden,
)
