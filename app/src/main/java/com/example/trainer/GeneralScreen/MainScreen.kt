package com.example.trainer.GeneralScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trainer.GeneralScreen.Home.HomeScreen
import com.example.trainer.GeneralScreen.Home.HomeViewModel
import com.example.trainer.GeneralScreen.Home.HomeViewModelFactory
import com.example.trainer.GeneralScreen.Profile.Profile // Твой экран профиля
import com.example.trainer.GeneralScreen.Profile.ProfileViewModel
import com.example.trainer.GeneralScreen.Profile.ProfileViewModelFactory
import com.example.trainer.GeneralScreen.Stats.StatsScreen
import com.example.trainer.GeneralScreen.Stats.StatsViewModel
import com.example.trainer.GeneralScreen.Stats.StatsViewModelFactory
import com.example.trainer.GeneralScreen.Workout.WorkoutScreen
import com.example.trainer.data.UserRepository
import com.example.trainer.navigation.Routes

// Описываем пункты меню
sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen("home", "Главная", Icons.Default.Home)
    object Workout : BottomBarScreen("workout", "Тренировки", Icons.Filled.FitnessCenter)
    object Stats : BottomBarScreen("stats", "Прогресс", Icons.AutoMirrored.Filled.TrendingUp)
    object Profile : BottomBarScreen("profile", "Профиль", Icons.Default.Person)
}

@Composable
fun MainScreen(repository: UserRepository, navController: androidx.navigation.NavController) {
    // У этого экрана свой СОБСТВЕННЫЙ контроллер навигации (для вкладок)
    val bottomNavController = rememberNavController()

    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Workout,
        BottomBarScreen.Stats,
        BottomBarScreen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White // Белый фон меню
            ) {
                // Определяем, какая вкладка сейчас открыта
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        label = { Text(text = screen.title) },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                // Настройки для правильной работы вкладок:
                                // 1. При нажатии "Назад" не уходим из приложения, а возвращаемся на "Home"
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // 2. Не создаем копии экранов, если много раз тыкать
                                launchSingleTop = true
                                // 3. Сохраняем состояние (скролл и т.д.)
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF2196F3), // Твой синий цвет
                            selectedTextColor = Color(0xFF2196F3),
                            indicatorColor = Color(0xFF2196F3).copy(alpha = 0.1f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Здесь меняется контент при переключении вкладок
        NavHost(
            navController = bottomNavController,
            startDestination = BottomBarScreen.Home.route,
            modifier = Modifier.padding(innerPadding) // Важно! Отступ, чтобы меню не перекрывало контент
        ) {
            composable(BottomBarScreen.Home.route) {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(repository)
                )
                HomeScreen(viewModel = viewModel)
            }

            composable(BottomBarScreen.Workout.route) {
                WorkoutScreen(onNavigateToCreate = { navController.navigate(Routes.CREATE_WORKOUT) })
            }

            composable(BottomBarScreen.Stats.route) {
                val viewModel: StatsViewModel = viewModel(
                    factory = StatsViewModelFactory(repository)
                )
                StatsScreen(viewModel = viewModel)
            }
            composable(BottomBarScreen.Profile.route) {
                val viewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(repository) )
            Profile(
                viewModel = viewModel,
                onLogout = {
                    // Действие при выходе из профиля
                    // Например, можно вызвать метод из ViewModel для очистки данных
                }
            )
            } // Твой готовый экран
        }
    }
}