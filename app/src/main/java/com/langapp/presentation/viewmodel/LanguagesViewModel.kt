package com.langapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.langapp.domain.model.Language
import com.langapp.domain.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LanguagesUiState(
    val languages: List<Language> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
)

@HiltViewModel
class LanguagesViewModel @Inject constructor(
    private val repository: LanguageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LanguagesUiState())
    val state: StateFlow<LanguagesUiState> = _state.asStateFlow()

    init { load() }

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        load(q)
    }

    fun load(search: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getLanguages(search?.ifBlank { null })
                .onSuccess { langs ->
                    _state.update { it.copy(languages = langs.filter { l -> l.activo }, isLoading = false) }
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}