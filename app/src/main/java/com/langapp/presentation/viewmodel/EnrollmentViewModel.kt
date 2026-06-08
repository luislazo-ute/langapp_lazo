package com.langapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.langapp.domain.model.Enrollment
import com.langapp.domain.repository.EnrollmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnrollmentUiState(
    val enrollments: List<Enrollment> = emptyList(),
    val message: String? = null,
    val isLoading: Boolean = false,
)

@HiltViewModel
class EnrollmentViewModel @Inject constructor(
    private val repository: EnrollmentRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EnrollmentUiState())
    val state: StateFlow<EnrollmentUiState> = _state.asStateFlow()

    init { loadEnrollments() }

    fun loadEnrollments() = viewModelScope.launch {
        repository.getEnrollments().onSuccess { list ->
            _state.update { it.copy(enrollments = list) }
        }
    }

    fun enroll(levelId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        repository.enroll(levelId)
            .onSuccess {
                _state.update { it.copy(message = "¡Inscrito correctamente!", isLoading = false) }
                loadEnrollments()
            }
            .onFailure { e ->
                val msg = if (e.message?.contains("400") == true) "Ya estás inscrito en este nivel"
                else e.message ?: "Error al inscribirse"
                _state.update { it.copy(message = msg, isLoading = false) }
            }
    }

    fun clearMessage() { _state.update { it.copy(message = null) } }
}