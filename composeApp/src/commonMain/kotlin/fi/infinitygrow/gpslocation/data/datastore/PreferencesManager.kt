package fi.infinitygrow.gpslocation.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val COUNTER_KEY = intPreferencesKey("counter")
        val THEME_KEY = booleanPreferencesKey("dark_theme")
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    val counterFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[COUNTER_KEY] ?: 0
    }

    val isDarkThemeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: false
    }

    val usernameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[USERNAME_KEY] ?: ""
    }

    suspend fun incrementCounter() {
        dataStore.edit { preferences ->
            val currentValue = preferences[COUNTER_KEY] ?: 0
            preferences[COUNTER_KEY] = currentValue + 1
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
    }

    suspend fun setUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
