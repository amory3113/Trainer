package com.example.trainer.GeneralScreen.Stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.NutritionEntity
import com.example.trainer.data.UserEntity
import com.example.trainer.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

class StatsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserEntity?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _weightHistory = MutableStateFlow<List<Float>>(emptyList())
    val weightHistory = _weightHistory.asStateFlow()

    // Список питания
    private val _nutritionHistory = MutableStateFlow<List<NutritionEntity>>(emptyList())
    val nutritionHistory = _nutritionHistory.asStateFlow()

    init {
        // 1. Слушаем профиль
        viewModelScope.launch {
            repository.userFlow.collect { user ->
                _userProfile.value = user
            }
        }

        // 2. Слушаем историю веса
        viewModelScope.launch {
            repository.getWeightHistoryFlow().collect { listEntities ->
                _weightHistory.value = listEntities.map { it.weight }
            }
        }

        // 3. СЛУШАЕМ ИСТОРИЮ ПИТАНИЯ (НОВОЕ)
        // Теперь мы используем .collect, чтобы данные обновлялись сами
        viewModelScope.launch {
            repository.getNutritionHistoryStream().collect { list ->
                _nutritionHistory.value = list
            }
        }
    }

    // Функция loadNutritionData() больше не нужна, так как работает init выше.

    fun addNewWeight(newWeight: Float) {
        viewModelScope.launch {
            repository.addWeightEntry(newWeight)
        }
    }

    fun calculateBMI(): Double {
        val user = _userProfile.value ?: return 0.0
        val heightM = user.height / 100.0
        return if (heightM > 0) user.weight / heightM.pow(2) else 0.0
    }

    fun getBmiStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Deficyt wagi"
            bmi < 25.0 -> "Norma"
            bmi < 30.0 -> "Nadwaga"
            else -> "Otyłość"
        }
    }
}

class StatsViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}