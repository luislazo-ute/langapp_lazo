package com.langapp.domain.model

data class Level(
    val id: Int,
    val languageId: Int,
    val languageNombre: String?,
    val nombre: String,
    val codigoCefr: String,
    val orden: Int,
)
