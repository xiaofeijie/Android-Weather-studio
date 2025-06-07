package com.juanantbuit.weatherproject.framework.helpers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreHelper @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val FIRST_APP_START_KEY = booleanPreferencesKey("isFirstAppStart")
        val LAST_GEO_ID_KEY = stringPreferencesKey("lastGeoId")
        val LAST_LATITUDE_KEY = floatPreferencesKey("lastLatitude")
        val LAST_LONGITUDE_KEY = floatPreferencesKey("lastLongitude")
        val LAST_SAVED_ARE_COORDINATES = booleanPreferencesKey("lastSavedIsCoordinated")
        val LANGUAGE_CODE_KEY = stringPreferencesKey("languageCode")
        val UNIT_SYSTEM_KEY = stringPreferencesKey("unitSystem")
        val FIRST_SAVE_NAME_KEY = stringPreferencesKey("firstSaveName")
        val SECOND_SAVE_NAME_KEY = stringPreferencesKey("secondSaveName")
        val THIRD_SAVE_NAME_KEY = stringPreferencesKey("thirdSaveName")
    }

    /** READERS **/

    fun read(key: Preferences.Key<*>) = dataStore.data.map { preferences ->
        preferences[key]
    }

    fun readGeonameId(geoIdKey: String) = dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(geoIdKey)]
    }

    fun readSavedCityName(saveNameKey: String) = dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(saveNameKey)]
    }


    /** WRITERS **/
    suspend fun writeLastGeonameId(geoId: String) {
        dataStore.edit { preferences ->
            preferences[LAST_GEO_ID_KEY] = geoId
        }
    }

    suspend fun writeGeonameId(geoIdKey: String, geoId: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(geoIdKey)] = geoId
        }
    }

    suspend fun writeLastLatitude(lastLatitude: Float) {
        dataStore.edit { preferences ->
            preferences[LAST_LATITUDE_KEY] = lastLatitude
        }
    }

    suspend fun writeLastLongitude(lastLongitude: Float) {
        dataStore.edit { preferences ->
            preferences[LAST_LONGITUDE_KEY] = lastLongitude
        }
    }

    suspend fun writeLastSavedIsCoordinated(lastSavedIsCoordinated: Boolean) {
        dataStore.edit { preferences ->
            preferences[LAST_SAVED_ARE_COORDINATES] = lastSavedIsCoordinated
        }
    }

    suspend fun writeLanguageCode(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE_KEY] = languageCode
        }
    }

    suspend fun writeUnitSystem(unitSystem: String) {
        dataStore.edit { preferences ->
            preferences[UNIT_SYSTEM_KEY] = unitSystem
        }
    }

    suspend fun writeIsFirstAppStart(isFirstAppStart: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_APP_START_KEY] = isFirstAppStart
        }
    }

    suspend fun writeSavedCityName(savedCityNameId: String, savedCityName: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(savedCityNameId)] = savedCityName
        }
    }

    suspend fun writeFirstSaveName(firstSaveName: String) {
        dataStore.edit { preferences ->
            preferences[FIRST_SAVE_NAME_KEY] = firstSaveName
        }
    }

    suspend fun writeSecondSaveName(secondSaveName: String) {
        dataStore.edit { preferences ->
            preferences[SECOND_SAVE_NAME_KEY] = secondSaveName
        }
    }

    suspend fun writeThirdSaveName(thirdSaveName: String) {
        dataStore.edit { preferences ->
            preferences[THIRD_SAVE_NAME_KEY] = thirdSaveName
        }
    }
}

