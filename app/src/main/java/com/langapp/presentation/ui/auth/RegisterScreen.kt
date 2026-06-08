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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: (isStaff: Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }

    val mismatch = password.isNotEmpty() && password2.isNotEmpty() && password != password2

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess((uiState as AuthUiState.Success).user.isStaff)
        }
    }

    val isLoading = uiState is AuthUiState.Loading
    val errorMsg = (uiState as? AuthUiState.Error)?.message

    Box(Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp).padding(top = 60.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("LangApp", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Accent)
            Spacer(Modifier.height(8.dp))
            Text("Crea tu cuenta gratis", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(32.dp))

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
                        "Usuario", placeholder = "mínimo 3 caracteres", enabled = !isLoading,
                        isError = username.isNotEmpty() && username.length < 3, errorMessage = "Mínimo 3 caracteres")
                    Spacer(Modifier.height(14.dp))
                    LangTextField(email, { email = it; viewModel.clearError() },
                        "Email", placeholder = "tu@email.com", enabled = !isLoading,
                        keyboardType = KeyboardType.Email,
                        isError = email.isNotEmpty() && !email.contains("@"), errorMessage = "Email inválido")
                    Spacer(Modifier.height(14.dp))
                    LangTextField(password, { password = it; viewModel.clearError() },
                        "Contraseña", placeholder = "mínimo 8 caracteres", isPassword = true,
                        enabled = !isLoading, keyboardType = KeyboardType.Password,
                        isError = password.isNotEmpty() && password.length < 8, errorMessage = "Mínimo 8 caracteres")
                    Spacer(Modifier.height(14.dp))
                    LangTextField(password2, { password2 = it; viewModel.clearError() },
                        "Confirmar contraseña", placeholder = "repite la contraseña", isPassword = true,
                        enabled = !isLoading, keyboardType = KeyboardType.Password, imeAction = ImeAction.Done,
                        isError = mismatch, errorMessage = "Las contraseñas no coinciden")
                    Spacer(Modifier.height(24.dp))
                    val canSubmit = username.length >= 3 && email.contains("@") &&
                            password.length >= 8 && !mismatch && !isLoading
                    LangButton("Crear mi cuenta",
                        { viewModel.register(username, email, password, password2) },
                        isLoading = isLoading, enabled = canSubmit)
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes cuenta? ", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Inicia sesión", color = Accent, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}