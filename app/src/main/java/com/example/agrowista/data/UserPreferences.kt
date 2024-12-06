package com.example.agrowista.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val USER_ID = stringPreferencesKey("user_id")
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    suspend fun saveUser(id: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id.toString()
        }
    }

    suspend fun setLoggedInStatus(status: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = status
        }
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return dataStore.data
            .map { preferences -> preferences[IS_LOGGED_IN] == true }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[USER_ID] = ""
        }
    }

    fun getUserId(): Flow<String> {
        return dataStore.data
            .map { preferences -> preferences[USER_ID] ?: "" }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context.dataStore).also { INSTANCE = it }
            }
        }
    }
}