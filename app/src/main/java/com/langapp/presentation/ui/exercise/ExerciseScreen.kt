package com.langapp.presentation.ui.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.langapp.presentation.components.ErrorScreen
import com.langapp.presentation.components.LangButton
import com.langapp.presentation.components.LoadingScreen
import com.langapp.presentation.viewmodel.ExerciseViewModel
import com.langapp.theme.*
import androidx.compose.ui.draw.clip

@Composable
fun ExerciseScreen(
    lessonId: Int,
    onExit: () -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(lessonId) { viewModel.load(lessonId) }

    when {
        state.isLoading -> LoadingScreen("Cargando ejercicios...")
        state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.load(lessonId) })
        state.exercises.isEmpty() -> {
            Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Esta lección no tiene ejercicios", color = TextSecondary)
                    Spacer(Modifier.height(24.dp))
                    LangButton("Volver", onExit, modifier = Modifier.width(200.dp))
                }
            }
        }
        state.finished -> FinishScreen(state.correctCount, state.exercises.size, onExit)
        else -> ExerciseContent(viewModel, onExit)
    }
}

@Composable
private fun ExerciseContent(viewModel: ExerciseViewModel, onExit: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val exercise = state.exercises[state.currentIndex]

    var selected by remember(exercise.id) { mutableStateOf<String?>(null) }
    var answered by remember(exercise.id) { mutableStateOf(false) }

    // Como el backend NO envía la respuesta correcta a usuarios normales,
    // marcamos como "respondido" cuando el usuario elige. En un caso real,
    // el backend valida vía el endpoint de progress.
    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(24.dp),
    ) {
        // Barra superior: cerrar + progreso
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, "Salir", tint = TextSecondary)
            }
            Spacer(Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { (state.currentIndex + 1f) / state.exercises.size },
                modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = Accent,
                trackColor = Surface2,
            )
        }

        Spacer(Modifier.height(32.dp))

        Text("Pregunta ${state.currentIndex + 1} de ${state.exercises.size}",
            style = MaterialTheme.typography.labelSmall, color = Accent)
        Spacer(Modifier.height(12.dp))
        Text(exercise.pregunta, style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(Modifier.height(32.dp))

        // Opciones
        exercise.opciones.forEach { opcion ->
            val isSelected = selected == opcion
            Surface(
                onClick = {
                    if (!answered) { selected = opcion }
                },
                shape = MaterialTheme.shapes.medium,
                color = if (isSelected) Accent.copy(alpha = 0.15f) else Surface,
                border = androidx.compose.foundation.BorderStroke(
                    2.dp, if (isSelected) Accent else Border
                ),
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            ) {
                Text(
                    opcion,
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) Accent else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Botón
        if (!answered) {
            LangButton(
                "Comprobar",
                onClick = {
                    answered = true
                    // En este demo asumimos correcto = true al responder.
                    // El backend registra el progreso y suma XP.
                    viewModel.answer(exercise.id, correcto = true)
                },
                enabled = selected != null,
            )
        } else {
            LangButton(
                if (state.currentIndex < state.exercises.lastIndex) "Siguiente" else "Terminar",
                onClick = { viewModel.next() },
            )
        }
    }
}

@Composable
private fun FinishScreen(correct: Int, total: Int, onExit: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text("🎉", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("¡Lección completada!", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text("Respondiste $total ejercicios", color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(32.dp))
            LangButton("Continuar", onExit, modifier = Modifier.width(220.dp))
        }
    }
}