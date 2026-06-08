package com.langapp.presentation.ui.levels

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
import com.langapp.domain.model.Level
import com.langapp.presentation.components.ErrorScreen
import com.langapp.presentation.components.LoadingScreen
import com.langapp.presentation.viewmodel.LevelsViewModel
import com.langapp.theme.*
import com.langapp.presentation.viewmodel.EnrollmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelsScreen(
    languageId: Int,
    onBack: () -> Unit,
    onLevelClick: (Int) -> Unit,
    viewModel: LevelsViewModel = hiltViewModel(),
    enrollmentViewModel: EnrollmentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val enrollState by enrollmentViewModel.state.collectAsState()
    LaunchedEffect(languageId) { viewModel.load(languageId) }

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(enrollState.message) {
        enrollState.message?.let { snackbar.showSnackbar(it); enrollmentViewModel.clearMessage() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Niveles", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            state.isLoading -> Box(Modifier.padding(padding)) { LoadingScreen("Cargando niveles...") }
            state.error != null -> Box(Modifier.padding(padding)) {
                ErrorScreen(state.error!!, onRetry = { viewModel.load(languageId) })
            }
            else -> LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.levels, key = { it.id }) { level ->
                    val enrolled = enrollState.enrollments.any { it.levelId == level.id }
                    LevelCard(
                        level = level,
                        enrolled = enrolled,
                        onClick = { onLevelClick(level.id) },
                        onEnroll = { enrollmentViewModel.enroll(level.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: Level,
    enrolled: Boolean,
    onClick: () -> Unit,
    onEnroll: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Accent, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center,
            ) {
                Text(level.codigoCefr, color = AccentOnDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(level.nombre, style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
                level.languageNombre?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            if (enrolled) {
                Text("✓ Inscrito", color = Success, style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold)
            } else {
                TextButton(onClick = onEnroll) {
                    Text("Inscribirse", color = Accent, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}