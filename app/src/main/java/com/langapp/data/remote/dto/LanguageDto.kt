package com.langapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.langapp.domain.model.Language

data class LanguageDto(
    val id: Int,
    val nombre: String,
    val codigo: String,
    @SerializedName("bandera_emoji") val banderaEmoji: String,
    val activo: Boolean,
)

fun LanguageDto.toDomain() = Language(
    id = id, nombre = nombre, codigo = codigo,
    banderaEmoji = banderaEmoji, activo = activo,
)
