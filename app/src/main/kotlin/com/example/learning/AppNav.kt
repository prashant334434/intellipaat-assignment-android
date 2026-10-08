package com.example.learning

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learning.ui.courses.CoursesScreen
import com.example.learning.ui.courses.CoursesViewModel
import com.example.learning.ui.detail.CourseDetailScreen
import com.example.learning.ui.detail.CourseDetailViewModel
import com.example.learning.ui.login.LoginScreen
import com.example.learning.ui.login.LoginViewModel

@Composable
fun AppNav(container: AppContainer) {
    val nav = rememberNavController()
    val start = if (container.tokenStore.current != null) "courses" else "login"

    NavHost(navController = nav, startDestination = start) {
        composable("login") {
            val vm: LoginViewModel = viewModel(
                factory = viewModelFactory { initializer { LoginViewModel(container.authRepository) } },
            )
            LoginScreen(vm, onLoggedIn = {
                nav.navigate("courses") { popUpTo("login") { inclusive = true } }
            })
        }
        composable("courses") {
            val vm: CoursesViewModel = viewModel(
                factory = viewModelFactory { initializer { CoursesViewModel(container.courseRepository, container.mockCourseApi) } },
            )
            CoursesScreen(vm, onOpenCourse = { id -> nav.navigate("course/$id") })
        }
        composable(
            route = "course/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.IntType }),
        ) { entry ->
            val courseId = requireNotNull(entry.arguments).getInt("courseId")
            val vm: CourseDetailViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { CourseDetailViewModel(container.courseRepository, courseId) }
                },
            )
            CourseDetailScreen(vm, onBack = { nav.popBackStack() })
        }
    }
}
