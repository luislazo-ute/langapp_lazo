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
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val hasNext: Boolean = false,
)

@HiltViewModel
class LanguagesViewModel @Inject constructor(
    private val repository: LanguageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LanguagesUiState())
    val state: StateFlow<LanguagesUiState> = _state.asStateFlow()

    private var currentPage = 1

    init { load() }

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        load()
    }

    // Carga la primera página (o recarga con búsqueda)
    fun load() {
        currentPage = 1
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val search = _state.value.query.ifBlank { null }
            repository.getLanguagesPaged(search, currentPage)
                .onSuccess { page ->
                    _state.update {
                        it.copy(
                            languages = page.languages,
                            hasNext = page.hasNext,
                            isLoading = false,
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    // Carga la siguiente página y la agrega a la lista existente
    fun loadMore() {
        if (_state.value.isLoadingMore || !_state.value.hasNext) return
        currentPage++
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            val search = _state.value.query.ifBlank { null }
            repository.getLanguagesPaged(search, currentPage)
                .onSuccess { page ->
                    _state.update {
                        it.copy(
                            languages = it.languages + page.languages,
                            hasNext = page.hasNext,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure { e ->
                    currentPage--
                    _state.update { it.copy(isLoadingMore = false, error = e.message) }
                }
        }
    }
}