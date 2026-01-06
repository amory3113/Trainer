package com.example.trainer.GeneralScreen.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trainer.Logic.Models.ActivityLevel
import com.example.trainer.data.UserEntity
import com.example.trainer.Logic.NutritionCalculator
import com.example.trainer.Logic.Models.Gender
import com.example.trainer.Logic.Models.Goal
import com.example.trainer.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserEntity?>(null)
    val userProfile = _userProfile.asStateFlow()

    init {
        viewModelScope.launch {
            repository.userFlow.collect { user ->
                _userProfile.value = user
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = repository.getUserProfile()
            _userProfile.value = user
        }
    }
    fun clearData(onCleared: () -> Unit) {
        viewModelScope.launch {
            repository.clearData()
            onCleared()
        }
    }

    fun updateProfile(type: EditType, newValue: String) {
        val currentUser = _userProfile.value ?: return

        viewModelScope.launch {
            var updatedUser = when (type) {
                EditType.GOAL -> currentUser.copy(goal = newValue)
                EditType.ACTIVITY -> currentUser.copy(activityLevel = newValue)
                EditType.WEIGHT -> {
                    val newWeight = newValue.toDoubleOrNull() ?: currentUser.weight
                    if (newWeight != currentUser.weight) {
                        repository.addWeightEntry(newWeight.toFloat())
                    }
                    currentUser.copy(weight = newWeight)
                }
            }

            val genderEnum = try { Gender.valueOf(updatedUser.gender) } catch (e: Exception) { Gender.MALE }
            val goalEnum = try { Goal.valueOf(updatedUser.goal) } catch (e: Exception) { Goal.MAINTAIN_FITNESS }
            val activityEnum = try { ActivityLevel.valueOf(updatedUser.activityLevel) } catch (e: Exception) { ActivityLevel.INTERMEDIATE }


            val newPlan = NutritionCalculator.calculate(
                gender = genderEnum,
                weight = updatedUser.weight,
                height = updatedUser.height,
                age = updatedUser.age,
                activityLevel = activityEnum,
                goal = goalEnum
            )
            updatedUser = updatedUser.copy(
                targetCalories = newPlan.calories,
                proteinGrams = newPlan.protein,
                fatGrams = newPlan.fat,
                carbGrams = newPlan.carbs
            )
            repository.updateUser(updatedUser)
        }
    }
}
class ProfileViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}