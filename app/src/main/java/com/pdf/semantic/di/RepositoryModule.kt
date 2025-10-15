package com.pdf.semantic.di

import com.pdf.semantic.data.repositoryImpl.EmbeddingRepositoryImpl
import com.pdf.semantic.data.repositoryImpl.PdfRepositoryImpl
import com.pdf.semantic.domain.repository.EmbeddingRepository
import com.pdf.semantic.domain.repository.PdfRepository
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
    abstract fun bindPdfRepository(pdfRepositoryImpl: PdfRepositoryImpl): PdfRepository

    @Binds
    @Singleton
    abstract fun bindEmbeddingRepository(embeddingRepositoryImpl: EmbeddingRepositoryImpl): EmbeddingRepository
}
