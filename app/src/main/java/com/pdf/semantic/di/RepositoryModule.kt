package com.pdf.semantic.di

import com.pdf.semantic.data.repositoryImpl.EmbeddingRepositoryImpl
import com.pdf.semantic.data.repositoryImpl.PdfFileRepositoryImpl
import com.pdf.semantic.data.repositoryImpl.PdfMetadataRepositoryImpl
import com.pdf.semantic.data.repositoryImpl.SettingsRepositoryImpl
import com.pdf.semantic.data.repositoryImpl.VectorSearchRepositoryImpl
import com.pdf.semantic.domain.repository.EmbeddingRepository
import com.pdf.semantic.domain.repository.PdfFileRepository
import com.pdf.semantic.domain.repository.PdfMetadataRepository
import com.pdf.semantic.domain.repository.SettingsRepository
import com.pdf.semantic.domain.repository.VectorSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindEmbeddingRepository(
        embeddingRepositoryImpl: EmbeddingRepositoryImpl,
    ): EmbeddingRepository

    @Binds
    @Singleton
    abstract fun bindPdfFileRepository(
        pdfFileRepositoryImpl: PdfFileRepositoryImpl,
    ): PdfFileRepository

    @Binds
    @Singleton
    abstract fun bindPdfMetadataRepository(
        pdfMetadataRepositoryImpl: PdfMetadataRepositoryImpl,
    ): PdfMetadataRepository

    @Binds
    @Singleton
    abstract fun bindVectorSearchRepository(
        vectorSearchRepositoryImpl: VectorSearchRepositoryImpl,
    ): VectorSearchRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl,
    ): SettingsRepository
}
