package com.langapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.langapp.presentation.components.LoadingScreen
import com.langapp.presentation.ui.auth.LoginScreen
import com.langapp.presentation.ui.auth.RegisterScreen
import com.langapp.presentation.ui.exercise.ExerciseScreen
import com.langapp.presentation.ui.home.HomeScreen
import com.langapp.presentation.ui.lessons.LessonsScreen
import com.langapp.presentation.ui.levels.LevelsScreen
import com.langapp.presentation.ui.profile.ProfileScreen
import com.langapp.presentation.ui.progress.ProgressScreen
import com.langapp.presentation.viewmodel.AuthViewModel
import com.langapp.theme.Surface
import com.langapp.presentation.ui.admin.AdminScreen

@Composable
fun NavGraph(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val isChecking by authViewModel.isCheckingSession.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    if (isChecking) { LoadingScreen("Iniciando LangApp..."); return }

    val start = if (isAuthenticated) Screen.Home.route else Screen.Login.route

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // La barra inferior solo se ve en las pantallas principales
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route, Screen.Progress.route, Screen.Profile.route,
    )

    Scaffold(
        containerColor = Surface,
        bottomBar = { if (showBottomBar) BottomNavBar(navController) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = start,
            modifier = Modifier.padding(padding),
        ) {

            composable(Screen.Admin.route) {
                AdminScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(onLanguageClick = { id -> navController.navigate("levels/$id") })
            }
            composable(Screen.Progress.route) { ProgressScreen() }
            composable(Screen.Profile.route) {
                val isStaff by authViewModel.isStaff.collectAsState()
                ProfileScreen(
                    isStaff = isStaff,
                    onProfileLoaded = { staff -> authViewModel.updateStaffStatus(staff) },
                    onAdminClick = { navController.navigate(Screen.Admin.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                )
            }
            composable(
                route = "levels/{langId}",
                arguments = listOf(navArgument("langId") { type = NavType.IntType }),
            ) { entry ->
                val langId = entry.arguments?.getInt("langId") ?: return@composable
                LevelsScreen(
                    languageId = langId,
                    onBack = { navController.popBackStack() },
                    onLevelClick = { id -> navController.navigate("lessons/$id") },
                )
            }
            composable(
                route = "lessons/{levelId}",
                arguments = listOf(navArgument("levelId") { type = NavType.IntType }),
            ) { entry ->
                val levelId = entry.arguments?.getInt("levelId") ?: return@composable
                LessonsScreen(
                    levelId = levelId,
                    onBack = { navController.popBackStack() },
                    onLessonClick = { id -> navController.navigate("exercises/$id") },
                )
            }
            composable(
                route = "exercises/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.IntType }),
            ) { entry ->
                val lessonId = entry.arguments?.getInt("lessonId") ?: return@composable
                ExerciseScreen(lessonId = lessonId, onExit = { navController.popBackStack() })
            }
        }
    }
}