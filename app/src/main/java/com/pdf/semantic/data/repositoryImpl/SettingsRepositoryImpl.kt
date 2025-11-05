package com.pdf.semantic.data.repositoryImpl

import com.pdf.semantic.data.datasource.SettingsLocalDataSource
import com.pdf.semantic.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: SettingsLocalDataSource,
    ) : SettingsRepository {
        override fun observeIsExpansionOn(): Flow<Boolean> = localDataSource.observeIsExpansionOn()

        override fun observeHasShownGuide(): Flow<Boolean> = localDataSource.observeHasShownGuide()

        override suspend fun setIsExpansionOn(isOn: Boolean) {
            localDataSource.setIsExpansionOn(isOn)
        }

        override suspend fun setHasShownGuide() {
            localDataSource.setHasShownGuide()
        }
    }
