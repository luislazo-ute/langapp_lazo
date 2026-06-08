package com.langapp.presentation.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.langapp.data.remote.dto.*
import com.langapp.presentation.components.LangTextField
import com.langapp.presentation.viewmodel.AdminViewModel
import com.langapp.theme.*

private sealed class AdminLevel {
    data object Languages : AdminLevel()
    data class Levels(val langId: Int, val langName: String) : AdminLevel()
    data class Lessons(val levelId: Int, val levelName: String, val langId: Int) : AdminLevel()
    data class Exercises(val lessonId: Int, val lessonName: String, val levelId: Int) : AdminLevel()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onBack: () -> Unit, viewModel: AdminViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var nav by remember { mutableStateOf<AdminLevel>(AdminLevel.Languages) }
    var showForm by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<Int?>(null) }

    // carga inicial / al cambiar de nivel
    LaunchedEffect(nav) {
        when (val n = nav) {
            is AdminLevel.Languages -> viewModel.loadLanguages()
            is AdminLevel.Levels -> viewModel.loadLevels(n.langId)
            is AdminLevel.Lessons -> viewModel.loadLessons(n.levelId)
            is AdminLevel.Exercises -> viewModel.loadExercises(n.lessonId)
        }
    }

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message, state.error) {
        state.message?.let { snackbar.showSnackbar(it); viewModel.clearMessages() }
        state.error?.let { snackbar.showSnackbar("⚠️ $it"); viewModel.clearMessages() }
    }

    val title = when (val n = nav) {
        is AdminLevel.Languages -> "Admin · Idiomas"
        is AdminLevel.Levels -> "Niveles · ${n.langName}"
        is AdminLevel.Lessons -> "Lecciones · ${n.levelName}"
        is AdminLevel.Exercises -> "Ejercicios · ${n.lessonName}"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = {
                        nav = when (val n = nav) {
                            is AdminLevel.Languages -> { onBack(); return@IconButton }
                            is AdminLevel.Levels -> AdminLevel.Languages
                            is AdminLevel.Lessons -> AdminLevel.Levels(n.langId, "")
                            is AdminLevel.Exercises -> AdminLevel.Lessons(n.levelId, "", 0)
                        }
                    }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editId = null; showForm = true },
                containerColor = Accent, contentColor = AccentOnDark,
            ) { Icon(Icons.Default.Add, "Agregar") }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (val n = nav) {
                is AdminLevel.Languages -> items(state.languages, key = { it.id }) { item ->
                    AdminRow("${item.banderaEmoji} ${item.nombre}", item.codigo.uppercase(),
                        onOpen = { nav = AdminLevel.Levels(item.id, item.nombre) },
                        onEdit = { editId = item.id; showForm = true },
                        onDelete = { viewModel.deleteLanguage(item.id) })
                }
                is AdminLevel.Levels -> items(state.levels, key = { it.id }) { item ->
                    AdminRow("${item.codigoCefr} · ${item.nombre}", "Orden ${item.orden}",
                        onOpen = { nav = AdminLevel.Lessons(item.id, item.nombre, n.langId) },
                        onEdit = { editId = item.id; showForm = true },
                        onDelete = { viewModel.deleteLevel(item.id, n.langId) })
                }
                is AdminLevel.Lessons -> items(state.lessons, key = { it.id }) { item ->
                    AdminRow("${item.icono} ${item.titulo}", item.descripcion,
                        onOpen = { nav = AdminLevel.Exercises(item.id, item.titulo, n.levelId) },
                        onEdit = { editId = item.id; showForm = true },
                        onDelete = { viewModel.deleteLesson(item.id, n.levelId) })
                }
                is AdminLevel.Exercises -> items(state.exercises, key = { it.id }) { item ->
                    AdminRow(item.pregunta, "${item.puntos} XP · ${item.tipo}",
                        onOpen = null,
                        onEdit = { editId = item.id; showForm = true },
                        onDelete = { viewModel.deleteExercise(item.id, n.lessonId) })
                }
            }
        }
    }

    if (showForm) {
        AdminFormDialog(
            nav = nav, editId = editId, state = state,
            onDismiss = { showForm = false },
            onSave = { showForm = false },
            viewModel = viewModel,
        )
    }
}

@Composable
private fun AdminRow(
    title: String, subtitle: String,
    onOpen: (() -> Unit)?, onEdit: () -> Unit, onDelete: () -> Unit,
) {
    var confirmDelete by remember { mutableStateOf(false) }
    Surface(color = Surface, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth(),
        onClick = { onOpen?.invoke() }) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge, maxLines = 2)
                if (subtitle.isNotBlank())
                    Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar", tint = Info) }
            IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Default.Delete, "Borrar", tint = Error) }
            if (onOpen != null) Icon(Icons.Default.ChevronRight, null, tint = TextFaint)
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("¿Eliminar?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; onDelete() }) {
                    Text("Eliminar", color = Error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } },
            containerColor = Surface,
        )
    }
}

@Composable
private fun AdminFormDialog(
    nav: AdminLevel, editId: Int?, state: com.langapp.presentation.viewmodel.AdminUiState,
    onDismiss: () -> Unit, onSave: () -> Unit, viewModel: AdminViewModel,
) {
    // valores iniciales si es edición
    var f1 by remember { mutableStateOf("") }
    var f2 by remember { mutableStateOf("") }
    var f3 by remember { mutableStateOf("") }
    var f4 by remember { mutableStateOf("") }
    var f5 by remember { mutableStateOf("") }

    LaunchedEffect(editId, nav) {
        when (val n = nav) {
            is AdminLevel.Languages -> state.languages.find { it.id == editId }?.let {
                f1 = it.nombre; f2 = it.codigo; f3 = it.banderaEmoji
            }
            is AdminLevel.Levels -> state.levels.find { it.id == editId }?.let {
                f1 = it.nombre; f2 = it.codigoCefr; f3 = it.orden.toString()
            }
            is AdminLevel.Lessons -> state.lessons.find { it.id == editId }?.let {
                f1 = it.titulo; f2 = it.descripcion; f3 = it.orden.toString(); f4 = it.icono
            }
            is AdminLevel.Exercises -> state.exercises.find { it.id == editId }?.let {
                f1 = it.pregunta; f2 = it.opciones.joinToString(","); f3 = ""; f4 = it.puntos.toString(); f5 = it.orden.toString()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text(if (editId == null) "Crear" else "Editar", color = TextPrimary) },
        text = {
            Column {
                when (nav) {
                    is AdminLevel.Languages -> {
                        LangTextField(f1, { f1 = it }, "Nombre"); Spacer(Modifier.height(8.dp))
                        LangTextField(f2, { f2 = it }, "Código (en, fr...)"); Spacer(Modifier.height(8.dp))
                        LangTextField(f3, { f3 = it }, "Bandera emoji")
                    }
                    is AdminLevel.Levels -> {
                        LangTextField(f1, { f1 = it }, "Nombre"); Spacer(Modifier.height(8.dp))
                        LangTextField(f2, { f2 = it }, "Código CEFR (A1...)"); Spacer(Modifier.height(8.dp))
                        LangTextField(f3, { f3 = it }, "Orden", keyboardType = KeyboardType.Number)
                    }
                    is AdminLevel.Lessons -> {
                        LangTextField(f1, { f1 = it }, "Título"); Spacer(Modifier.height(8.dp))
                        LangTextField(f2, { f2 = it }, "Descripción"); Spacer(Modifier.height(8.dp))
                        LangTextField(f3, { f3 = it }, "Orden", keyboardType = KeyboardType.Number); Spacer(Modifier.height(8.dp))
                        LangTextField(f4, { f4 = it }, "Icono emoji")
                    }
                    is AdminLevel.Exercises -> {
                        LangTextField(f1, { f1 = it }, "Pregunta"); Spacer(Modifier.height(8.dp))
                        LangTextField(f2, { f2 = it }, "Opciones (separa con coma)"); Spacer(Modifier.height(8.dp))
                        LangTextField(f3, { f3 = it }, "Respuesta correcta"); Spacer(Modifier.height(8.dp))
                        LangTextField(f4, { f4 = it }, "Puntos", keyboardType = KeyboardType.Number); Spacer(Modifier.height(8.dp))
                        LangTextField(f5, { f5 = it }, "Orden", keyboardType = KeyboardType.Number)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when (val n = nav) {
                    is AdminLevel.Languages -> viewModel.saveLanguage(editId,
                        LanguageWriteDto(f1, f2, f3, true))
                    is AdminLevel.Levels -> viewModel.saveLevel(editId,
                        LevelWriteDto(n.langId, f1, f2, f3.toIntOrNull() ?: 1), n.langId)
                    is AdminLevel.Lessons -> viewModel.saveLesson(editId,
                        LessonWriteDto(n.levelId, f1, f2, f3.toIntOrNull() ?: 1, f4.ifBlank { "📘" }), n.levelId)
                    is AdminLevel.Exercises -> viewModel.saveExercise(editId,
                        ExerciseWriteDto(n.lessonId, "multiple_choice", f1,
                            f2.split(",").map { it.trim() }.filter { it.isNotBlank() },
                            f3, f4.toIntOrNull() ?: 10, f5.toIntOrNull() ?: 1), n.lessonId)
                }
                onSave()
            }) { Text("Guardar", color = Accent, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}