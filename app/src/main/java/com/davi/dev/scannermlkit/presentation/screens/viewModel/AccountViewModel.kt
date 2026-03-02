package com.davi.dev.scannermlkit.presentation.screens.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class ThemeColor(
    val name: String,
    val color: Color
)

class AccountViewModel : ViewModel() {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    val colorOptions = listOf(
        ThemeColor("Pink",Color(0XFFb6407f)),
        ThemeColor("Blue", Color(0xFF2196F3)),
        ThemeColor("Green", Color(0xFF4CAF50)),
        ThemeColor("Red", Color(0xFFF44336)),
        ThemeColor("Purple", Color(0xFF9C27B0)),
        ThemeColor("Orange", Color(0xFFFF9800))
    )

    var selectedColor by mutableStateOf(colorOptions[0])
        private set

    fun setTheme(mode: ThemeMode) {
        themeMode = mode
    }

    fun setColor(color: ThemeColor) {
        selectedColor = color
    }

    val appVersion = "1.6.0"
    val supportEmail = "davigomesflorencio@gmail.com"
}
