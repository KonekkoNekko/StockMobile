package com.bielcode.stockmobile.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveUser(account: Account) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = account.email ?: ""
            preferences[NAME_KEY] = account.name ?: ""
            preferences[PHONE_KEY] = account.phone ?: ""
            preferences[ROLE_KEY] = account.role ?: ""
        }
        Log.d(
            "UserPreference",
            "saveUser: ${account.email} ${account.name} ${account.phone} ${account.role}"
        )
//        Log.d(
//            "UserPreference",
//            "saveUser: ${dataStore.data.collect()}"
//        )
    }

    fun getUser(): Flow<Account> {
        return dataStore.data.map { preferences ->
            Account(
                preferences[EMAIL_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[PHONE_KEY] ?: "",
                preferences[ROLE_KEY] ?: ""
            )
        }
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // New methods to save and retrieve location
    suspend fun saveLocation(address: String, coordinate: String) {
        dataStore.edit { preferences ->
            preferences[ADDRESS_KEY] = address
            preferences[COORDINATE_KEY] = coordinate
        }
    }

    fun getLocation(): Flow<Pair<String, String>> {
        return dataStore.data.map { preferences ->
            val address = preferences[ADDRESS_KEY] ?: ""
            val coordinate = preferences[COORDINATE_KEY] ?: ""
            Pair(address, coordinate)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        private val PHONE_KEY = stringPreferencesKey("phone")
        private val ROLE_KEY = stringPreferencesKey("role")
        private val ADDRESS_KEY = stringPreferencesKey("address")
        private val COORDINATE_KEY = stringPreferencesKey("coordinate")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}