package com.example.trainer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trainer.takeinfo.WelcomeScreen
import com.example.trainer.takeinfo.GenderScreen
import com.example.trainer.takeinfo.TakeCelScreen

object Routes {
    const val WELCOME = "welcome"
    const val TAKE_CEL = "take_cel"
    const val GENDER = "gender"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onNextClick = {
                    navController.navigate(Routes.TAKE_CEL)
                }
            )
        }

        composable(Routes.TAKE_CEL) {
            TakeCelScreen(
                onNextClick = {
                    navController.navigate(Routes.GENDER)
                }
            )
        }

        composable(Routes.GENDER) {
            GenderScreen(
                onNextClick = {
                    // TODO: Navigate to the next screen
                }
            )
        }
    }
}