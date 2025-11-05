package com.pdf.semantic.domain.usecase.setting

import com.pdf.semantic.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsExpansionOnUsecase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        operator fun invoke(): Flow<Boolean> = settingsRepository.observeIsExpansionOn()
    }
