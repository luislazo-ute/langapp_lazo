package com.langapp.data.repository

import com.langapp.data.remote.api.*
import com.langapp.data.remote.dto.*
import com.langapp.domain.repository.AdminRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val languageApi: LanguageApi,
    private val levelApi: LevelApi,
    private val lessonApi: LessonApi,
    private val exerciseApi: ExerciseApi,
) : AdminRepository {

    private suspend fun <T> wrap(call: suspend () -> retrofit2.Response<T>): Result<Unit> = runCatching {
        val r = call()
        if (!r.isSuccessful) {
            val msg = when (r.code()) {
                400 -> "Datos inválidos"
                401 -> "Sesión expirada, vuelve a entrar"
                403 -> "Sin permisos de administrador"
                404 -> "No encontrado"
                500 -> "Error del servidor"
                else -> "Error ${r.code()}"
            }
            error(msg)
        }
        Unit
    }

    override suspend fun createLanguage(body: LanguageWriteDto) = wrap { languageApi.create(body) }
    override suspend fun updateLanguage(id: Int, body: LanguageWriteDto) = wrap { languageApi.update(id, body) }
    override suspend fun deleteLanguage(id: Int) = wrap { languageApi.delete(id) }

    override suspend fun createLevel(body: LevelWriteDto) = wrap { levelApi.create(body) }
    override suspend fun updateLevel(id: Int, body: LevelWriteDto) = wrap { levelApi.update(id, body) }
    override suspend fun deleteLevel(id: Int) = wrap { levelApi.delete(id) }

    override suspend fun createLesson(body: LessonWriteDto) = wrap { lessonApi.create(body) }
    override suspend fun updateLesson(id: Int, body: LessonWriteDto) = wrap { lessonApi.update(id, body) }
    override suspend fun deleteLesson(id: Int) = wrap { lessonApi.delete(id) }

    override suspend fun createExercise(body: ExerciseWriteDto) = wrap { exerciseApi.create(body) }
    override suspend fun updateExercise(id: Int, body: ExerciseWriteDto) = wrap { exerciseApi.update(id, body) }
    override suspend fun deleteExercise(id: Int) = wrap { exerciseApi.delete(id) }
}