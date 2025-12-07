package com.example.trainer.GeneralScreen.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.UserEntity
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