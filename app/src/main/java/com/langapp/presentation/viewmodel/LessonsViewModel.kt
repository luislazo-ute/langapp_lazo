package com.langapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.langapp.domain.model.Lesson
import com.langapp.domain.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonsUiState(
    val lessons: List<Lesson> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LessonsViewModel @Inject constructor(
    private val repository: LessonRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LessonsUiState())
    val state: StateFlow<LessonsUiState> = _state.asStateFlow()

    fun load(levelId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getLessons(levelId)
                .onSuccess { lessons ->
                    _state.update { it.copy(lessons = lessons.sortedBy { l -> l.orden }, isLoading = false) }
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}