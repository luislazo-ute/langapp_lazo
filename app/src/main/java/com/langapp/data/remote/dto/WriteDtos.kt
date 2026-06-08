package com.langapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LanguageWriteDto(
    val nombre: String,
    val codigo: String,
    @SerializedName("bandera_emoji") val banderaEmoji: String,
    val activo: Boolean,
)

data class LevelWriteDto(
    val language: Int,
    val nombre: String,
    @SerializedName("codigo_cefr") val codigoCefr: String,
    val orden: Int,
)

data class LessonWriteDto(
    val level: Int,
    val titulo: String,
    val descripcion: String,
    val orden: Int,
    val icono: String,
)

data class ExerciseWriteDto(
    val lesson: Int,
    val tipo: String,
    val pregunta: String,
    val opciones: List<String>,
    @SerializedName("respuesta_correcta") val respuestaCorrecta: String,
    val puntos: Int,
    val orden: Int,
)