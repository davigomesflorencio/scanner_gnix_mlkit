package com.davi.dev.scannermlkit.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.davi.dev.scannermlkit.domain.model.UserPreferences
import com.davi.dev.scannermlkit.domain.repository.UserPreferencesRepository
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    private object Keys {
        val themeColorIndex = intPreferencesKey("theme_color_index")
        val themeMode = stringPreferencesKey("theme_mode")
    }

    private inline val Preferences.themeMode
        get() = this[Keys.themeMode] ?: ThemeMode.SYSTEM

    private inline val Preferences.themeColorIndex
        get() = this[Keys.themeColorIndex] ?: 0

    override val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            UserPreferences(
                themeMode = preferences.themeMode.toString(),
                themeColorIndex = preferences.themeColorIndex,
            )
        }
        .distinctUntilChanged()

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[Keys.themeMode] = themeMode.name
        }
    }

    override suspend fun updateThemeColorIndex(themeColorIndex: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.themeColorIndex] = themeColorIndex
        }
    }

    override val theme: Flow<ThemeMode> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            when (it[Keys.themeMode]) {
                "LIGHT" -> ThemeMode.LIGHT
                "DARK" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
        }
        .distinctUntilChanged()
}