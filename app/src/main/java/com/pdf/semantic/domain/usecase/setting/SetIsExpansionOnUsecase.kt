package com.pdf.semantic.domain.usecase.setting

import com.pdf.semantic.domain.repository.SettingsRepository
import javax.inject.Inject

class SetIsExpansionOnUsecase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        suspend operator fun invoke(isEnabled: Boolean) {
            settingsRepository.setIsExpansionOn(isEnabled)
        }
    }
