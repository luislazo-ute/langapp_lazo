package com.langapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.langapp.domain.model.Exercise
import com.langapp.domain.repository.ExerciseRepository
import com.langapp.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val finished: Boolean = false,
)

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepo: ExerciseRepository,
    private val progressRepo: ProgressRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseUiState())
    val state: StateFlow<ExerciseUiState> = _state.asStateFlow()

    fun load(lessonId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            exerciseRepo.getExercises(lessonId)
                .onSuccess { list ->
                    _state.update {
                        it.copy(exercises = list.sortedBy { e -> e.orden }, isLoading = false)
                    }
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    // Envía la respuesta al backend. El backend decide si es correcta
    // y suma XP. Aquí guardamos si acertó para el contador local.
    fun answer(exerciseId: Int, correcto: Boolean) {
        viewModelScope.launch {
            progressRepo.submitAnswer(exerciseId, correcto)
            if (correcto) {
                _state.update { it.copy(correctCount = it.correctCount + 1) }
            }
        }
    }

    fun next() {
        _state.update { s ->
            if (s.currentIndex < s.exercises.lastIndex) {
                s.copy(currentIndex = s.currentIndex + 1)
            } else {
                s.copy(finished = true)
            }
        }
    }
}