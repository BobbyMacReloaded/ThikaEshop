package com.example.thikaeshop.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class SimplePrefs(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val SAVED_PHONE = stringPreferencesKey("saved_phone")
    }

    // Save phone number
    suspend fun savePhoneNumber(phone: String) {
        dataStore.edit { prefs ->
            prefs[SAVED_PHONE] = phone
        }
    }

    // Get saved phone number (returns empty string if not found)
    suspend fun getSavedPhoneNumber(): String {
        val prefs = dataStore.data.first()
        return prefs[SAVED_PHONE] ?: ""
    }
}