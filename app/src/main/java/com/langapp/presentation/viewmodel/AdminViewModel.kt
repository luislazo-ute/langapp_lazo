package com.langapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.langapp.data.remote.dto.*
import com.langapp.domain.model.*
import com.langapp.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val languages: List<Language> = emptyList(),
    val levels: List<Level> = emptyList(),
    val lessons: List<Lesson> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val admin: AdminRepository,
    private val languageRepo: LanguageRepository,
    private val levelRepo: LevelRepository,
    private val lessonRepo: LessonRepository,
    private val exerciseRepo: ExerciseRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state.asStateFlow()

    fun clearMessages() { _state.update { it.copy(message = null, error = null) } }

    // ---- LISTAR ----
    fun loadLanguages() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        languageRepo.getLanguages()
            .onSuccess { l -> _state.update { it.copy(languages = l, isLoading = false) } }
            .onFailure { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
    }
    fun loadLevels(languageId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        levelRepo.getLevels(languageId)
            .onSuccess { l -> _state.update { it.copy(levels = l, isLoading = false) } }
            .onFailure { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
    }
    fun loadLessons(levelId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        lessonRepo.getLessons(levelId)
            .onSuccess { l -> _state.update { it.copy(lessons = l, isLoading = false) } }
            .onFailure { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
    }
    fun loadExercises(lessonId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        exerciseRepo.getExercises(lessonId)
            .onSuccess { l -> _state.update { it.copy(exercises = l, isLoading = false) } }
            .onFailure { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
    }

    private fun done(msg: String, reload: () -> Unit) {
        _state.update { it.copy(message = msg) }
        reload()
    }
    private fun fail(e: Throwable) { _state.update { it.copy(error = e.message ?: "Error") } }

    // ---- LANGUAGE ----
    fun saveLanguage(id: Int?, body: LanguageWriteDto) = viewModelScope.launch {
        val res = if (id == null) admin.createLanguage(body) else admin.updateLanguage(id, body)
        res.onSuccess { done(if (id == null) "Idioma creado" else "Idioma actualizado", ::loadLanguages) }
            .onFailure(::fail)
    }
    fun deleteLanguage(id: Int) = viewModelScope.launch {
        admin.deleteLanguage(id).onSuccess { done("Idioma eliminado", ::loadLanguages) }.onFailure(::fail)
    }

    // ---- LEVEL ----
    fun saveLevel(id: Int?, body: LevelWriteDto, languageId: Int) = viewModelScope.launch {
        val res = if (id == null) admin.createLevel(body) else admin.updateLevel(id, body)
        res.onSuccess { done(if (id == null) "Nivel creado" else "Nivel actualizado") { loadLevels(languageId) } }
            .onFailure(::fail)
    }
    fun deleteLevel(id: Int, languageId: Int) = viewModelScope.launch {
        admin.deleteLevel(id).onSuccess { done("Nivel eliminado") { loadLevels(languageId) } }.onFailure(::fail)
    }

    // ---- LESSON ----
    fun saveLesson(id: Int?, body: LessonWriteDto, levelId: Int) = viewModelScope.launch {
        val res = if (id == null) admin.createLesson(body) else admin.updateLesson(id, body)
        res.onSuccess { done(if (id == null) "Lección creada" else "Lección actualizada") { loadLessons(levelId) } }
            .onFailure(::fail)
    }
    fun deleteLesson(id: Int, levelId: Int) = viewModelScope.launch {
        admin.deleteLesson(id).onSuccess { done("Lección eliminada") { loadLessons(levelId) } }.onFailure(::fail)
    }

    // ---- EXERCISE ----
    fun saveExercise(id: Int?, body: ExerciseWriteDto, lessonId: Int) = viewModelScope.launch {
        val res = if (id == null) admin.createExercise(body) else admin.updateExercise(id, body)
        res.onSuccess { done(if (id == null) "Ejercicio creado" else "Ejercicio actualizado") { loadExercises(lessonId) } }
            .onFailure(::fail)
    }
    fun deleteExercise(id: Int, lessonId: Int) = viewModelScope.launch {
        admin.deleteExercise(id).onSuccess { done("Ejercicio eliminado") { loadExercises(lessonId) } }.onFailure(::fail)
    }
}