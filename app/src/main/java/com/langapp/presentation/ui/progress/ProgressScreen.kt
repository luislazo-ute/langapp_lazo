package com.langapp.presentation.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.langapp.domain.model.UserProgress
import com.langapp.presentation.components.ErrorScreen
import com.langapp.presentation.components.LoadingScreen
import com.langapp.presentation.viewmodel.ProgressViewModel
import com.langapp.theme.*

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> LoadingScreen("Cargando progreso...")
        state.error != null -> ErrorScreen(state.error!!, onRetry = viewModel::load)
        else -> Column(Modifier.fillMaxSize().background(Background)) {
            Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 8.dp)) {
                Text("Mi progreso", style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("${state.progress.size} ejercicios completados",
                    color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
            if (state.progress.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 48.sp)
                        Text("Aún no tienes progreso", color = TextSecondary)
                        Text("Completa ejercicios para verlos aquí",
                            color = TextFaint, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.progress, key = { it.id }) { prog -> ProgressCard(prog) }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(progress: UserProgress) {
    Surface(color = Surface, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (progress.correcto) "✅" else "❌", fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(progress.exercisePregunta ?: "Ejercicio #${progress.exerciseId}",
                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
                    color = TextPrimary, maxLines = 2)
                Text(progress.completadoEn.take(10), style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary)
            }
            Text("+${progress.puntosObtenidos} XP", color = Accent,
                fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        }
    }
}