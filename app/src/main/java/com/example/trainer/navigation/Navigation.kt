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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.trainer.GeneralScreen.BottomBarScreen
import com.example.trainer.takeinfo.OnboardingViewModel
import com.example.trainer.GeneralScreen.MainScreen
import com.example.trainer.GeneralScreen.Workout.CreateWorkoutScreen
import com.example.trainer.GeneralScreen.Workout.WorkoutScreen

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
    const val CREATE_WORKOUT = "create_workout?workoutId={workoutId}"
}

@Composable
fun AppNavigation(repository: UserRepository, startDestination: String) {
    val navController = rememberNavController()

    // --- ДОБАВЛЕНО: Инициализация зависимостей для генератора тренировок ---
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = com.example.trainer.data.AppDatabase.getDatabase(context)
    val workoutRepo = remember { com.example.trainer.data.Exercise.WorkoutRepository(db.workoutDao(), db.exerciseDao()) }
    val exerciseDao = remember { db.exerciseDao() }
    // ----------------------------------------------------------------------

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.WELCOME) { WelcomeScreen( onNextClick = { navController.navigate(Routes.TAKE_CEL) }) }

        navigation(
            startDestination = Routes.TAKE_CEL,
            route = Routes.ONBOARDING_GRAPH
        ) {
            composable(Routes.TAKE_CEL) {
                // Передаем workoutRepo и exerciseDao
                val viewModel = getOnboardingViewModel(navController, repository, workoutRepo, exerciseDao)
                TakeCelScreen(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.GENDER) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.GENDER) {
                val viewModel = getOnboardingViewModel(navController, repository, workoutRepo, exerciseDao)
                GenderScreen(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.INFO) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.INFO) {
                val viewModel = getOnboardingViewModel(navController, repository, workoutRepo, exerciseDao)
                TakeInfo(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.HEALTH_QUESTION) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.HEALTH_QUESTION) {
                val viewModel = getOnboardingViewModel(navController, repository, workoutRepo, exerciseDao)

                HealthQuestion(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.MORE_HEALTH_QUEST) },
                    onNoLimitations = { navController.navigate(Routes.ACTIVITY_LEVEL) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.MORE_HEALTH_QUEST) {
                val viewModel = getOnboardingViewModel(navController, repository, workoutRepo, exerciseDao)

                MoreHealthQuest(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.ACTIVITY_LEVEL) }
                )
            }

            composable(Routes.ACTIVITY_LEVEL) {
                val viewModel = getOnboardingViewModel(navController, repository, workoutRepo, exerciseDao)
                ActivityLevel(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.FEELING) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.FEELING) {
                val viewModel = getOnboardingViewModel(navController, repository, workoutRepo, exerciseDao)
                Feeling(
                    viewModel = viewModel,
                    onNextClick = { navController.navigate(Routes.LOAD_SCREEN) },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Routes.LOAD_SCREEN) {
                val viewModel = getOnboardingViewModel(navController, repository, workoutRepo, exerciseDao)
                val currentContext = androidx.compose.ui.platform.LocalContext.current // переименовал, чтобы не путать с внешним context
                LoadScreen(
                    viewModel = viewModel,
                    onPlanReady = {
                        // Здесь запустится генератор тренировок
                        viewModel.saveFinalDataToDatabase(currentContext)
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

        composable(
            Routes.CREATE_WORKOUT,
            arguments = listOf(
                navArgument("workoutId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: -1

            // Тут мы используем те же переменные workoutRepo, что создали в начале
            val workoutViewModel: com.example.trainer.GeneralScreen.Workout.WorkoutViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.example.trainer.GeneralScreen.Workout.WorkoutViewModelFactory(workoutRepo, exerciseDao)
                )

            LaunchedEffect(workoutId) {
                workoutViewModel.loadWorkoutForEdit(workoutId)
            }

            CreateWorkoutScreen(
                viewModel = workoutViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun getOnboardingViewModel(
    navController: NavController,
    repository: UserRepository,
    workoutRepository: com.example.trainer.data.Exercise.WorkoutRepository,
    exerciseDao: com.example.trainer.data.Exercise.ExerciseDao
): OnboardingViewModel {
    val navBackStackEntry = remember(navController) {
        navController.getBackStackEntry(Routes.ONBOARDING_GRAPH)
    }

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Передаем все три зависимости в конструктор ViewModel
            return OnboardingViewModel(repository, workoutRepository, exerciseDao) as T
        }
    }
    return viewModel(navBackStackEntry, factory = factory)
}