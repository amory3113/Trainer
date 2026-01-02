package com.example.trainer.GeneralScreen.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trainer.data.Exercise.WorkoutRepository
import com.example.trainer.data.UserRepository

class HomeViewModelFactory(
    private val userRepository: UserRepository, private val workoutRepository: WorkoutRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(userRepository, workoutRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

