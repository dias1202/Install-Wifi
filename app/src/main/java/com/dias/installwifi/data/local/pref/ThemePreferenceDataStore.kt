package com.dias.installwifi.data.local.pref

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ThemePreferenceDataStore {
    private const val DATASTORE_NAME = "theme_preferences"
    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)
    private val THEME_KEY = intPreferencesKey("app_theme")

    fun getTheme(context: Context): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: -1 // -1 = follow system
        }

    suspend fun setTheme(context: Context, mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode
        }
    }
}