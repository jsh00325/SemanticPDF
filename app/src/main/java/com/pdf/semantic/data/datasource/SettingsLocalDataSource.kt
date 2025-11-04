package com.pdf.semantic.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsLocalDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        fun observeIsExpansionOn(): Flow<Boolean> =
            dataStore.data
                .map { preferences ->
                    preferences[PreferencesKeys.IS_EXPANSION_ON] ?: false
                }

        fun observeHasShownGuide(): Flow<Boolean> =
            dataStore.data
                .map { preferences ->
                    preferences[PreferencesKeys.HAS_SHOWN_GUIDE] ?: false
                }

        suspend fun setIsExpansionOn(isEnabled: Boolean) =
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_EXPANSION_ON] = isEnabled
            }

        suspend fun setHasShownGuide() =
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.HAS_SHOWN_GUIDE] = true
            }

        companion object {
            private object PreferencesKeys {
                val IS_EXPANSION_ON =
                    booleanPreferencesKey("is_expansion_on")
                val HAS_SHOWN_GUIDE =
                    booleanPreferencesKey("has_shown_guide")
            }
        }
    }
