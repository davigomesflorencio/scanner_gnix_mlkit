package com.davi.dev.scannermlkit.domain.repository

import com.davi.dev.scannermlkit.domain.model.UserPreferences
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ThemeMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateThemeColorIndex(themeColorIndex: Int)
    val theme: Flow<ThemeMode>
}