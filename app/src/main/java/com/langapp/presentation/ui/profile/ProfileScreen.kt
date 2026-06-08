package com.langapp.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.langapp.presentation.components.LoadingScreen
import com.langapp.presentation.viewmodel.ProfileViewModel
import com.langapp.theme.*

@Composable
fun ProfileScreen(
    isStaff: Boolean = false,
    onProfileLoaded: (Boolean) -> Unit = {},
    onAdminClick: () -> Unit = {},
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Cuando el perfil carga, avisar el is_staff real al AuthViewModel
    LaunchedEffect(state.profile) {
        state.profile?.let { onProfileLoaded(it.isStaff) }
    }

    if (state.isLoading) { LoadingScreen("Cargando perfil..."); return }
    val profile = state.profile

    Column(
        modifier = Modifier.fillMaxSize().background(Background)
            .verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier.size(88.dp).background(
                Brush.linearGradient(listOf(Accent, AccentLight)), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                profile?.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                fontSize = 40.sp, fontWeight = FontWeight.Bold, color = AccentOnDark,
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(profile?.username ?: "—", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(profile?.email ?: "—", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)


        if (isStaff) {
            Spacer(Modifier.height(8.dp))
            Surface(color = Info.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
                Text("ADMIN", color = Info, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
        }

        Spacer(Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("⭐", "${profile?.xpTotal ?: 0}", "XP total", Modifier.weight(1f))
            StatCard("🔥", "${profile?.rachaDias ?: 0}", "Días de racha", Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        Surface(color = Surface, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                InfoRow("Idioma actual", profile?.languageNombre ?: "No seleccionado")
                HorizontalDivider(color = BorderLight)
                InfoRow("Nivel actual", profile?.levelNombre ?: "No seleccionado")
                HorizontalDivider(color = BorderLight)
                InfoRow("Miembro desde", profile?.fechaInicio?.take(10) ?: "—")
            }
        }

        Spacer(Modifier.height(28.dp))

        if (isStaff) {
            Button(
                onClick = onAdminClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Info, contentColor = AccentOnDark),
                shape = MaterialTheme.shapes.medium,
            ) {
                Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Panel de administración", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
            border = androidx.compose.foundation.BorderStroke(1.dp, Error.copy(alpha = 0.5f)),
            shape = MaterialTheme.shapes.medium,
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String, modifier: Modifier) {
    Surface(color = Surface, shape = MaterialTheme.shapes.large, modifier = modifier) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = Accent)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = TextPrimary, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium)
    }
}