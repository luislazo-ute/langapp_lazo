package com.langapp.presentation.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.langapp.presentation.components.LangButton
import com.langapp.presentation.components.LangTextField
import com.langapp.presentation.viewmodel.AuthUiState
import com.langapp.presentation.viewmodel.AuthViewModel
import com.langapp.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (isStaff: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).user.isStaff)
        }
    }

    val isLoading = uiState is AuthUiState.Loading
    val errorMsg = (uiState as? AuthUiState.Error)?.message

    Box(Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp).padding(top = 80.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("🌍", fontSize = 56.sp)
            Text("LangApp", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Accent)
            Spacer(Modifier.height(8.dp))
            Text("Aprende un nuevo idioma", color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(40.dp))

            Surface(shape = MaterialTheme.shapes.large, color = Surface, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp)) {
                    if (errorMsg != null) {
                        Surface(color = Error.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()) {
                            Text(errorMsg, color = Error, style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    LangTextField(username, { username = it; viewModel.clearError() },
                        "Usuario", placeholder = "tu_usuario", enabled = !isLoading)
                    Spacer(Modifier.height(16.dp))
                    LangTextField(password, { password = it; viewModel.clearError() },
                        "Contraseña", placeholder = "••••••••", isPassword = true,
                        enabled = !isLoading, keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                    Spacer(Modifier.height(24.dp))
                    LangButton("Iniciar sesión", { viewModel.login(username, password) },
                        isLoading = isLoading, enabled = username.isNotBlank() && password.isNotBlank())
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta? ", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Regístrate", color = Accent, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}