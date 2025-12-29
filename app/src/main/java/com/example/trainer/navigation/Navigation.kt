package com.example.trainer.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import androidx.compose.runtime.remember
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trainer.data.UserRepository
import com.example.trainer.takeinfo.WelcomeScreen
import com.example.trainer.takeinfo.GenderScreen
import com.example.trainer.takeinfo.TakeCelScreen
import com.example.trainer.takeinfo.TakeInfo
import com.example.trainer.takeinfo.HealthQuestion
import com.example.trainer.takeinfo.MoreHealthQuest
import com.example.trainer.takeinfo.ActivityLevel
import com.example.trainer.takeinfo.Feeling
import com.example.trainer.takeinfo.LoadScreen
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trainer.takeinfo.OnboardingViewModel
import com.example.trainer.GeneralScreen.MainScreen
import com.example.trainer.GeneralScreen.Workout.CreateWorkoutScreen

object Routes {
    const val WELCOME = "welcome"
    const val ONBOARDING_GRAPH = "onboarding_graph"
    const val TAKE_CEL = "take_cel"
    const val GENDER = "gender"
    const val INFO = "info"
    const val HEALTH_QUESTION = "health_question"
    const val MORE_HEALTH_QUEST = "more_health_quest"
    const val ACTIVITY_LEVEL = "activity_level"
    const val FEELING = "feeling"
    const val LOAD_SCREEN = "load_screen"
    const val MAIN = "main_screen"
    const val CREATE_WORKOUT = "create_workout"
}

@Composable
fun AppNavigation(repository: UserRepository, startDestination: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Стартовый экран (отдельно)
        composable(Routes.WELCOME) { WelcomeScreen( onNextClick = { navController.navigate(Routes.TAKE_CEL) }) }

        // --- НАЧАЛО ОБЩЕЙ ЗОНЫ (Где живет ViewModel) ---
        navigation(
            startDestination = Routes.TAKE_CEL,
            route = Routes.ONBOARDING_GRAPH // Это имя "комнаты", где лежит рюкзак
        ) {

            composable(Routes.TAKE_CEL) {
                val viewModel = getOnboardingViewModel(navController, repository)
                TakeCelScreen(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.GENDER) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.GENDER) {
                val viewModel = getOnboardingViewModel(navController, repository)
                GenderScreen(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.INFO) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.INFO) {
                val viewModel = getOnboardingViewModel(navController, repository)
                TakeInfo(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.HEALTH_QUESTION) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Экран 1 Здоровья
            composable(Routes.HEALTH_QUESTION) {
                // Получаем общий ViewModel
                val viewModel = getOnboardingViewModel(navController, repository)

                HealthQuestion(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.MORE_HEALTH_QUEST) },
                    // Если "Нет ограничений", сразу идем к Активности
                    onNoLimitations = { navController.navigate(Routes.ACTIVITY_LEVEL) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Экран 2 Здоровья
            composable(Routes.MORE_HEALTH_QUEST) {
                val viewModel = getOnboardingViewModel(navController, repository)

                MoreHealthQuest(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.ACTIVITY_LEVEL) }
                )
            }

            composable(Routes.ACTIVITY_LEVEL) {
                val viewModel = getOnboardingViewModel(navController, repository)
                ActivityLevel(
                     viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.FEELING) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.FEELING) {
                val viewModel = getOnboardingViewModel(navController, repository)
                Feeling(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.LOAD_SCREEN) },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Routes.LOAD_SCREEN) {
                val viewModel = getOnboardingViewModel(navController, repository)
                LoadScreen(
                    viewModel = viewModel,
                    onPlanReady = {
                        viewModel.saveFinalDataToDatabase()
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }

        }

        composable(Routes.MAIN) {
            MainScreen(repository = repository, navController = navController)
        }

        // 2. Экран создания (НОВЫЙ)
        composable(Routes.CREATE_WORKOUT) {
            // Нам нужно создать ViewModel здесь.
            // В идеале использовать Hilt, но мы делаем вручную:
            val context = androidx.compose.ui.platform.LocalContext.current
            val db = com.example.trainer.data.AppDatabase.getDatabase(context)
            val workoutRepo = com.example.trainer.data.Exercise.WorkoutRepository(db.workoutDao())

            val workoutViewModel: com.example.trainer.GeneralScreen.Workout.WorkoutViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.example.trainer.GeneralScreen.Workout.WorkoutViewModelFactory(workoutRepo, db.exerciseDao())
                )

            CreateWorkoutScreen(
                viewModel = workoutViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun getOnboardingViewModel(navController: NavController, repository: UserRepository): OnboardingViewModel {
    val navBackStackEntry = remember(navController) {
        navController.getBackStackEntry(Routes.ONBOARDING_GRAPH)
    }

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(repository) as T
        }
    }
    return viewModel(navBackStackEntry, factory = factory)
}