package com.langapp.presentation.ui.home

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
import com.langapp.domain.model.Language
import com.langapp.presentation.components.ErrorScreen
import com.langapp.presentation.components.LoadingScreen
import com.langapp.presentation.viewmodel.LanguagesViewModel
import com.langapp.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

@Composable
fun HomeScreen(
    onLanguageClick: (Int) -> Unit,
    viewModel: LanguagesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> LoadingScreen("Cargando idiomas...")
        state.error != null -> ErrorScreen(state.error!!, onRetry = viewModel::load)
        else -> {
            Column(Modifier.fillMaxSize().background(Background)) {
                Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 8.dp)) {
                    Text("¿Qué quieres aprender?", style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::onQueryChange,
                        placeholder = { Text("Buscar idioma...", color = TextFaint) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent, unfocusedBorderColor = Border,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                        ),
                    )
                }
                if (state.languages.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron idiomas", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.languages, key = { it.id }) { lang ->
                            LanguageCard(lang) { onLanguageClick(lang.id) }
                        }
                        if (state.hasNext) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                    if (state.isLoadingMore) {
                                        CircularProgressIndicator(color = Accent, strokeWidth = 2.dp,
                                            modifier = Modifier.size(28.dp))
                                    } else {
                                        OutlinedButton(
                                            onClick = { viewModel.loadMore() },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Accent),
                                        ) { Text("Cargar más") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageCard(language: Language, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(language.banderaEmoji, fontSize = 40.sp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(language.nombre, style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(language.codigo.uppercase(), style = MaterialTheme.typography.bodySmall,
                    color = Accent, fontWeight = FontWeight.SemiBold)
            }
            Text("›", fontSize = 28.sp, color = TextFaint)
        }
    }
}