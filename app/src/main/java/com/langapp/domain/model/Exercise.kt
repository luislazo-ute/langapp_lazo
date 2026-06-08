package com.langapp.domain.model

data class Exercise(
    val id: Int,
    val lessonId: Int,
    val tipo: String,
    val tipoDisplay: String?,
    val pregunta: String,
    val opciones: List<String>,
    val puntos: Int,
    val orden: Int,
)
