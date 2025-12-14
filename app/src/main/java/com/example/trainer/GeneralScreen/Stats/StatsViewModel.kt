package com.example.trainer.GeneralScreen.Stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.UserEntity
import com.example.trainer.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

class StatsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserEntity?>(null)
    val userProfile = _userProfile.asStateFlow()

    // Фейковые данные для графика (позже прикрутим реальную базу)
    // Просто чтобы показать красивую кривую
    val weightHistory = listOf(82f, 81.5f, 81.2f, 80.8f, 80.5f, 79.9f, 79.5f)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _userProfile.value = repository.getUserProfile()
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