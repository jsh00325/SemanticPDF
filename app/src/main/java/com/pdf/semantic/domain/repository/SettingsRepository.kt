package com.pdf.semantic.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeIsExpansionOn(): Flow<Boolean>

    fun observeHasShownGuide(): Flow<Boolean>

    suspend fun setIsExpansionOn(isOn: Boolean)

    suspend fun setHasShownGuide()
}
