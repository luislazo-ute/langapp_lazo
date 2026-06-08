package com.langapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.langapp.domain.model.LoggedUser
import com.langapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: LoggedUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<LoggedUser?>(null)
    val currentUser: StateFlow<LoggedUser?> = _currentUser.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = _currentUser
        .map { it != null }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isStaff: StateFlow<Boolean> = _currentUser
        .map { it?.isStaff == true }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession.asStateFlow()

    init { restoreSession() }

    private fun restoreSession() {
        viewModelScope.launch {
            try {
                val snap = authRepository.getStoredUser()
                if (snap != null && authRepository.isLoggedIn()) {
                    _currentUser.value = LoggedUser(snap.id, snap.username, snap.email, snap.isStaff)
                }
            } finally { _isCheckingSession.value = false }
        }
    }

    fun login(username: String, password: String) {
        if (_uiState.value is AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(username.trim(), password)
                .onSuccess { _currentUser.value = it; _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Error al iniciar sesión") }
        }
    }

    fun register(username: String, email: String, password: String, password2: String) {
        if (_uiState.value is AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(username.trim(), email.trim(), password, password2)
                .onSuccess { _currentUser.value = it; _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Error al registrarse") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _uiState.value = AuthUiState.Idle
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) _uiState.value = AuthUiState.Idle
    }

    fun updateStaffStatus(isStaff: Boolean) {
        val u = _currentUser.value ?: return
        if (u.isStaff != isStaff) {
            _currentUser.value = u.copy(isStaff = isStaff)
        }
    }

}
