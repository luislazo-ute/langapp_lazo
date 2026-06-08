package com.langapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Languages : Screen("languages")
    data class Levels(val langId: Int = 0) : Screen("levels/{langId}") {
        fun createRoute(id: Int) = "levels/$id"
    }
    data class Lessons(val levelId: Int = 0) : Screen("lessons/{levelId}") {
        fun createRoute(id: Int) = "lessons/$id"
    }
    data class Exercises(val lessonId: Int = 0) : Screen("exercises/{lessonId}") {
        fun createRoute(id: Int) = "exercises/$id"
    }
    data object Progress : Screen("progress")
    data object Profile : Screen("profile")

    data object Admin : Screen("admin")
}