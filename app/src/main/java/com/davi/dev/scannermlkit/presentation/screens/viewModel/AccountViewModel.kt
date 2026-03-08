package com.davi.dev.scannermlkit.presentation.screens.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davi.dev.scannermlkit.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class ThemeColor(
    val name: String,
    val color: Color
)

class AccountViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    val colorOptions = listOf(
        ThemeColor("Pink", Color(0XFFb6407f)),
        ThemeColor("Blue", Color(0xFF2196F3)),
        ThemeColor("Green", Color(0xFF4CAF50)),
        ThemeColor("Red", Color(0xFFF44336)),
        ThemeColor("Purple", Color(0xFF9C27B0)),
        ThemeColor("Orange", Color(0xFFFF9800))
    )

    var selectedColor by mutableStateOf(colorOptions[0])
        private set

    init {
        repository.theme
            .onEach { mode ->
                themeMode = mode
            }
            .launchIn(viewModelScope)

        repository.userPreferences
            .onEach { preferences ->
                selectedColor = colorOptions.getOrElse(preferences.themeColorIndex) { colorOptions[0] }
            }
            .launchIn(viewModelScope)
    }

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            repository.updateThemeMode(mode)
        }
    }

    fun setColor(color: ThemeColor) {
        val index = colorOptions.indexOf(color)
        if (index != -1) {
            viewModelScope.launch {
                repository.updateThemeColorIndex(index)
            }
        }
    }

    val appVersion = "1.7.0"
    val supportEmail = "davigomesflorencio@gmail.com"
}
