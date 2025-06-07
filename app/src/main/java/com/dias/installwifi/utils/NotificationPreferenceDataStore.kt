package com.dias.installwifi.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object NotificationPreferenceDataStore {
    private const val DATASTORE_NAME = "notification_preferences"
    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)
    private val NOTIFICATION_KEY = booleanPreferencesKey("notification_enabled")

    fun getNotificationEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[NOTIFICATION_KEY] != false // default: enabled
        }

    suspend fun setNotificationEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_KEY] = enabled
        }
    }
}

