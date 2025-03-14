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
    private val textToSpeechKey = booleanPreferencesKey("text_to_speech")
    private val ttsNameKey = booleanPreferencesKey("tts_location_name")
    private val ttsDistanceKey = booleanPreferencesKey("tts_distance")
    private val ttsOneOrAllKey = booleanPreferencesKey("tts_amount_of_locations")
    private val ttsTemperatureKey = booleanPreferencesKey("tts_temperature")
    private val ttsHumidityKey = booleanPreferencesKey("tts_humidity")
    private val ttsWindSpeedKey = booleanPreferencesKey("tts_wind_speed")
    private val ttsWindGustKey = booleanPreferencesKey("tts_wind_gust")
    private val ttsWindDirectionKey = booleanPreferencesKey("tts_wind_direction")
    private val ttsCloudBaseKey = booleanPreferencesKey("tts_cloud_base")
    private val ttsFlightLevel65Key = booleanPreferencesKey("tts_fl65")
    private val ttsFlightLevel95Key = booleanPreferencesKey("tts_fl95")

    // Expose flow to observe dark theme settings; default is false.
    val darkThemeFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[darkThemeKey] ?: false }

    // Expose flow to observe location toggle; default is true.
    val locationFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[locationKey] ?: true }

    val textToSpeechFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[textToSpeechKey] ?: false }

    val ttsNameFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsNameKey] ?: true }

    val ttsDistanceFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsDistanceKey] ?: true }

    val ttsOneOrAllFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsOneOrAllKey] ?: true }

    val ttsTemperatureFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsTemperatureKey] ?: false }

    val ttsHumidityFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsHumidityKey] ?: false }

    val ttsWindSpeedFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsWindSpeedKey] ?: true }

    val ttsWindGustFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsWindGustKey] ?: true }

    val ttsWindDirectionFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsWindDirectionKey] ?: true }

    val ttsCloudBaseFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsCloudBaseKey] ?: true }

    val ttsFlightLevel65Flow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsFlightLevel65Key] ?: true }

    val ttsFlightLevel95Flow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ttsFlightLevel95Key] ?: true }


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
    suspend fun setTtsName(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsNameKey] = isOn
        }
    }
    suspend fun setTtsDistance(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsDistanceKey] = isOn
        }
    }
    suspend fun setTtsOneOrAll(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsOneOrAllKey] = isOn
        }
    }
    suspend fun setTtsTemperature(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsTemperatureKey] = isOn
        }
    }
    suspend fun setTtsHumidity(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsHumidityKey] = isOn
        }
    }
    suspend fun setTtsWindSpeed(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsWindSpeedKey] = isOn
        }
    }
    suspend fun setTtsWindGust(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsWindGustKey] = isOn
        }
    }
    suspend fun setTtsWindDirection(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsWindDirectionKey] = isOn
        }
    }
    suspend fun setTtsCloudBase(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsCloudBaseKey] = isOn
        }
    }
    suspend fun setTtsFlightLevel65(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsFlightLevel65Key] = isOn
        }
    }
    suspend fun setTtsFlightLevel95(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ttsFlightLevel95Key] = isOn
        }
    }
    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
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