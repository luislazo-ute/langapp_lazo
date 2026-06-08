package com.langapp.presentation.ui.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.langapp.domain.model.Lesson
import com.langapp.presentation.components.ErrorScreen
import com.langapp.presentation.components.LoadingScreen
import com.langapp.presentation.viewmodel.LessonsViewModel
import com.langapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    levelId: Int,
    onBack: () -> Unit,
    onLessonClick: (Int) -> Unit,
    viewModel: LessonsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(levelId) { viewModel.load(levelId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lecciones", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
            )
        },
        containerColor = Background,
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.padding(padding)) { LoadingScreen("Cargando lecciones...") }
            state.error != null -> Box(Modifier.padding(padding)) {
                ErrorScreen(state.error!!, onRetry = { viewModel.load(levelId) })
            }
            else -> LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.lessons, key = { it.id }) { lesson ->
                    LessonCard(lesson) { onLessonClick(lesson.id) }
                }
            }
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(lesson.icono.ifBlank { "📘" }, fontSize = 32.sp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(lesson.titulo, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
                if (lesson.descripcion.isNotBlank()) {
                    Text(lesson.descripcion, style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary, maxLines = 2)
                }
            }
            Text("›", fontSize = 28.sp, color = TextFaint)
        }
    }
}