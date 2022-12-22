package com.saadfauzi.uasmad.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CustomSettingPreferences private constructor(private val datastore: DataStore<Preferences>) {

    private val STATE_LOGGED = booleanPreferencesKey("is_logged")
    private val ACCESS_TOKEN = stringPreferencesKey("token")
    private val PREF_USERNAME = stringPreferencesKey("username")
    private val PREF_EMAIL = stringPreferencesKey("email")
    private val PREF_IMAGE = stringPreferencesKey("image")

    fun getAccessToken(): Flow<String> {
        return datastore.data.map { preferences ->
            preferences[ACCESS_TOKEN] ?: "No auth"
        }
    }

    fun getLoginState(): Flow<Boolean> {
        return datastore.data.map { preferences ->
            preferences[STATE_LOGGED] ?: false
        }
    }

    fun getUsername(): Flow<String> {
        return datastore.data.map { preferences ->
            preferences[PREF_USERNAME] ?: "No auth"
        }
    }

    fun getEmail(): Flow<String> {
        return datastore.data.map { preferences ->
            preferences[PREF_EMAIL] ?: "No auth"
        }
    }

    fun getPhoto(): Flow<String> {
        return datastore.data.map { preferences ->
            preferences[PREF_IMAGE] ?: "No auth"
        }
    }

    suspend fun saveAccessToken(isLogged: Boolean, token: String, username: String, email: String, image: String){
        datastore.edit { preferences ->
            preferences[STATE_LOGGED] = isLogged
            preferences[ACCESS_TOKEN] = token
            preferences[PREF_USERNAME] = username
            preferences[PREF_EMAIL] = email
            preferences[PREF_IMAGE] = image
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CustomSettingPreferences? = null

        fun getInstance(datastore: DataStore<Preferences>): CustomSettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = CustomSettingPreferences(datastore)
                INSTANCE = instance
                return instance
            }
        }
    }
}