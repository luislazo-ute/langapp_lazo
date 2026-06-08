package com.langapp.domain.model

data class Language(
    val id: Int,
    val nombre: String,
    val codigo: String,
    val banderaEmoji: String,
    val activo: Boolean,
)
