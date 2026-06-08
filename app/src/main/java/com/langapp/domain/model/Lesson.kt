package com.langapp.domain.model

data class Lesson(
    val id: Int,
    val levelId: Int,
    val levelNombre: String?,
    val titulo: String,
    val descripcion: String,
    val orden: Int,
    val icono: String,
)
