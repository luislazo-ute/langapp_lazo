package com.langapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.langapp.domain.model.Level

data class LevelDto(
    val id: Int,
    @SerializedName("language") val languageId: Int,
    @SerializedName("language_nombre") val languageNombre: String?,
    val nombre: String,
    @SerializedName("codigo_cefr") val codigoCefr: String,
    val orden: Int,
)

fun LevelDto.toDomain() = Level(
    id = id, languageId = languageId,
    languageNombre = languageNombre, nombre = nombre,
    codigoCefr = codigoCefr, orden = orden,
)
