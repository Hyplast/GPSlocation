package fi.infinitygrow.gpslocation.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    // Define the preference keys.
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val locationKey = booleanPreferencesKey("location")

    // Expose flow to observe dark theme settings; default is false.
    val darkThemeFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[darkThemeKey] ?: false }

    // Expose flow to observe location toggle; default is true.
    val locationFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[locationKey] ?: true }

    // Function to update dark theme preference.
    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[darkThemeKey] = isDark
        }
    }

    // Function to update location toggle preference.
    suspend fun setLocationOn(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[locationKey] = isOn
        }
    }
}
//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/*
class DataStorePreferencesRepository(private val context: Context) : PreferencesRepository {

    override suspend fun saveString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getString(key: String, defaultValue: String): String {
        val preferencesKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }.first()
    }

    override suspend fun saveInt(key: String, value: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getInt(key: String, defaultValue: Int): Int {
        val preferencesKey = intPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }.first()
    }

    override suspend fun saveBoolean(key: String, value: Boolean) {
        val preferencesKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val preferencesKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }.first()
    }

    override suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

 */