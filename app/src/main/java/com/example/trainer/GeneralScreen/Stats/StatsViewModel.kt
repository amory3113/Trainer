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

    // Теперь здесь реальные данные из базы
    private val _weightHistory = MutableStateFlow<List<Float>>(emptyList())
    val weightHistory = _weightHistory.asStateFlow()

    // Добавляем поток для истории питания
    private val _nutritionHistory = MutableStateFlow<List<NutritionEntity>>(emptyList())
    val nutritionHistory = _nutritionHistory.asStateFlow()

    init {
        // 1. ПОДПИСКА НА ПРОФИЛЬ (Главное исправление)
        // Теперь мы не просто загружаем 1 раз, а "слушаем" базу вечно
        viewModelScope.launch {
            repository.userFlow.collect { user ->
                _userProfile.value = user
            }
        }

        // 2. Подписка на историю веса (как мы делали в прошлый раз)
        viewModelScope.launch {
            repository.getWeightHistoryFlow().collect { listEntities ->
                _weightHistory.value = listEntities.map { it.weight }
            }
        }

        // 3. Питание (можно оставить так или тоже переделать на flow, если захочешь)
        loadNutritionData()
    }

    fun loadNutritionData() {
        viewModelScope.launch {
            val nHistory = repository.getNutritionHistory()
            _nutritionHistory.value = nHistory
        }
    }

//    fun loadData() { // Сделал public, чтобы вызывать при обновлении
//        viewModelScope.launch {
//            _userProfile.value = repository.getUserProfile()
//
//            // Вес
//            val wHistory = repository.getWeightHistory()
//            _weightHistory.value = wHistory.map { it.weight }
//
//            // Питание (НОВОЕ)
//            val nHistory = repository.getNutritionHistory()
//            _nutritionHistory.value = nHistory
//        }
//    }

    // Функция добавления нового веса
    fun addNewWeight(newWeight: Float) {
        viewModelScope.launch {
            repository.addWeightEntry(newWeight)
        }
    }

    // Расчет ИМТ (Индекс Массы Тела)
    fun calculateBMI(): Double {
        val user = _userProfile.value ?: return 0.0
        val heightM = user.height / 100.0 // переводим см в метры
        return if (heightM > 0) user.weight / heightM.pow(2) else 0.0
    }

    // Текстовая интерпретация ИМТ
    fun getBmiStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Дефицит массы"
            bmi < 25.0 -> "Норма"
            bmi < 30.0 -> "Избыточный вес"
            else -> "Ожирение"
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