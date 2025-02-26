package fi.infinitygrow.gpslocation.domain.repository

interface PreferencesRepository {
    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String, defaultValue: String = ""): String

    suspend fun saveInt(key: String, value: Int)
    suspend fun getInt(key: String, defaultValue: Int = 0): Int

    suspend fun saveBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    suspend fun clear()
}
