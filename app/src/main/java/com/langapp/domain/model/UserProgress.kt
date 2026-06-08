package com.langapp.domain.model

data class UserProgress(
    val id: Int,
    val exerciseId: Int,
    val exercisePregunta: String?,
    val correcto: Boolean,
    val puntosObtenidos: Int,
    val completadoEn: String,
)
