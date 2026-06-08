package com.langapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.langapp.domain.model.Level
import com.langapp.domain.repository.LevelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LevelsUiState(
    val levels: List<Level> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LevelsViewModel @Inject constructor(
    private val repository: LevelRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LevelsUiState())
    val state: StateFlow<LevelsUiState> = _state.asStateFlow()

    fun load(languageId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getLevels(languageId)
                .onSuccess { levels ->
                    _state.update { it.copy(levels = levels.sortedBy { l -> l.orden }, isLoading = false) }
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}