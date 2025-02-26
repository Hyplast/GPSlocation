package fi.infinitygrow.gpslocation.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import fi.infinitygrow.gpslocation.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    // MutableStateFlow to hold the user name
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> get() = _userName

    init {
        // Load the user name from DataStore when the repository is initialized
        loadUserName()
    }

    private fun loadUserName() {
        // Load user name from DataStore and update the StateFlow
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.collect { preferences ->
                _userName.value = preferences[USER_NAME_KEY]
            }
        }
    }

    // Save user name
    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            _userName.value = name // Update the StateFlow
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