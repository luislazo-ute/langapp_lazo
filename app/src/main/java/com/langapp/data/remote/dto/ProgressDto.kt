package com.langapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.langapp.domain.model.UserProgress

data class SubmitAnswerRequest(
    val exercise: Int,
    val correcto: Boolean,
)

data class UserProgressDto(
    val id: Int,
    @SerializedName("exercise") val exerciseId: Int,
    @SerializedName("exercise_pregunta") val exercisePregunta: String?,
    val correcto: Boolean,
    @SerializedName("puntos_obtenidos") val puntosObtenidos: Int,
    @SerializedName("completado_en") val completadoEn: String,
)

fun UserProgressDto.toDomain() = UserProgress(
    id = id, exerciseId = exerciseId,
    exercisePregunta = exercisePregunta, correcto = correcto,
    puntosObtenidos = puntosObtenidos, completadoEn = completadoEn,
)
