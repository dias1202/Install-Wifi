package com.dias.installwifi.data.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dias.installwifi.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class UserPreference @Inject constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: User, isTechnician: Boolean) {
        dataStore.edit { preferences ->
            preferences[UID_KEY] = user.uid
            preferences[NAME_KEY] = user.name
            preferences[EMAIL_KEY] = user.email
            preferences[CREATED_AT_KEY] = user.createdAt.toString()
            preferences[PHOTO_URL] = user.photoUrl ?: ""
            preferences[PHONE_NUMBER] = user.phoneNumber ?: ""
            preferences[ADDRESS] = user.address ?: ""
            preferences[IS_GOOGLE_LOGIN_KEY] = user.isGoogleLogin == true
            preferences[IS_LOGIN_KEY] = user.isLogin == true
            preferences[ASSIGNED_JOB_ID] = user.assignedJobId ?: ""
            preferences[STATUS] = user.status ?: ""
            preferences[TOTAL_JOBS_COMPLETED] = user.totalJobsCompleted ?: 0
            preferences[IS_TECHNICIAN_KEY] = isTechnician
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
                preferences[PHONE_NUMBER] ?: "",
                preferences[ADDRESS] ?: "",
                preferences[IS_GOOGLE_LOGIN_KEY] == true,
                preferences[IS_LOGIN_KEY] == true,
                preferences[ASSIGNED_JOB_ID] ?: "",
                preferences[STATUS] ?: "",
                preferences[TOTAL_JOBS_COMPLETED] ?: 0,
                preferences[IS_TECHNICIAN_KEY] == true
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
        private val PHONE_NUMBER = stringPreferencesKey("phoneNumber")
        private val ADDRESS = stringPreferencesKey("address")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val IS_GOOGLE_LOGIN_KEY = booleanPreferencesKey("isGoogleLogin")
        private val ASSIGNED_JOB_ID = stringPreferencesKey("assignedJobId")
        private val STATUS = stringPreferencesKey("status")
        private val TOTAL_JOBS_COMPLETED = intPreferencesKey("totalJobsCompleted")
        private val IS_TECHNICIAN_KEY = booleanPreferencesKey("isTechnician")
    }
}