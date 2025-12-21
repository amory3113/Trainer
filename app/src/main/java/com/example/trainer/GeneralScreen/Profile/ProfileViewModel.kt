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

    // Храним данные пользователя. Изначально null (пока не загрузились)
    private val _userProfile = MutableStateFlow<UserEntity?>(null)
    val userProfile = _userProfile.asStateFlow()

    init {
        // Как только ViewModel создалась - сразу грузим данные
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // Запрос в базу (через репозиторий)
            val user = repository.getUserProfile()
            _userProfile.value = user
        }
    }

    // Функция для кнопки "Сбросить прогресс"
    fun clearData(onCleared: () -> Unit) {
        viewModelScope.launch {
            repository.clearData()
            onCleared() // Сообщаем экрану, что всё удалено (чтобы перейти на Welcome)
        }
    }

    // --- НОВАЯ ФУНКЦИЯ ОБНОВЛЕНИЯ ---
    fun updateProfile(type: EditType, newValue: String) {
        val currentUser = _userProfile.value ?: return

        viewModelScope.launch {
            // 1. Создаем копию пользователя с новыми данными
            var updatedUser = when (type) {
                EditType.GOAL -> currentUser.copy(goal = newValue)
                EditType.ACTIVITY -> currentUser.copy(activityLevel = newValue)
                EditType.WEIGHT -> {
                    val newWeight = newValue.toDoubleOrNull() ?: currentUser.weight
                    // Если меняем вес, надо добавить и в историю веса
                    if (newWeight != currentUser.weight) {
                        repository.addWeightEntry(newWeight.toFloat())
                    }
                    currentUser.copy(weight = newWeight)
                }
            }

            // 2. ПЕРЕСЧЕТ КБЖУ (Самое важное!)
            // Нам нужно снова вызвать логику расчета, так как вводные данные изменились
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

            // 3. Применяем новые нормы калорий к пользователю
            updatedUser = updatedUser.copy(
                targetCalories = newPlan.calories,
                proteinGrams = newPlan.protein,
                fatGrams = newPlan.fat,
                carbGrams = newPlan.carbs
            )

            // 4. Сохраняем в базу
            repository.updateUser(updatedUser) // Нам нужен метод updateUser в DAO/Repo

            // 5. Обновляем экран
            loadUserProfile()
        }
    }
}


// Фабрика для создания ViewModel с репозиторием (стандартный шаблон)
class ProfileViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}