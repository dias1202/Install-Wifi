package com.dias.installwifi.data.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dias.installwifi.data.model.User
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class UserPreference @Inject constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: User) {
        dataStore.edit { preferences ->
            preferences[UID_KEY] = user.uid
            preferences[NAME_KEY] = user.name
            preferences[EMAIL_KEY] = user.email
            preferences[CREATED_AT_KEY] = user.createdAt.toString()
            preferences[PHOTO_URL] = user.photoUrl ?: ""
            preferences[IS_GOOGLE_LOGIN_KEY] = user.isGoogleLogin == true
            preferences[IS_LOGIN_KEY] = user.isLogin == true
        }
    }

    fun getSession(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[UID_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[CREATED_AT_KEY]?.toLong() ?: 0,
                preferences[PHOTO_URL] ?: "",
                preferences[IS_GOOGLE_LOGIN_KEY] == true,
                preferences[IS_LOGIN_KEY] == true
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val UID_KEY = stringPreferencesKey("uid")
        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val CREATED_AT_KEY = stringPreferencesKey("createdAt")
        private val PHOTO_URL = stringPreferencesKey("photoUrl")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val IS_GOOGLE_LOGIN_KEY = booleanPreferencesKey("isGoogleLogin")
    }
}