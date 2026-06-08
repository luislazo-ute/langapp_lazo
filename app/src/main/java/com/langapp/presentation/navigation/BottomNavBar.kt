package com.langapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.langapp.theme.*

private data class NavItem(val screen: Screen, val label: String, val icon: ImageVector, val iconSel: ImageVector)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem(Screen.Home, "Inicio", Icons.Outlined.Home, Icons.Filled.Home),
        NavItem(Screen.Progress, "Progreso", Icons.Outlined.Timeline, Icons.Filled.Timeline),
        NavItem(Screen.Profile, "Perfil", Icons.Outlined.AccountCircle, Icons.Filled.AccountCircle),
    )
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    NavigationBar(containerColor = Surface) {
        items.forEach { item ->
            val selected = current == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(if (selected) item.iconSel else item.icon, item.label) },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Accent, selectedTextColor = Accent,
                    indicatorColor = Accent.copy(alpha = 0.12f),
                    unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary,
                ),
            )
        }
    }
}