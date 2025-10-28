package com.pdf.semantic.di

import com.pdf.semantic.data.repositoryImpl.EmbeddingRepositoryImpl
import com.pdf.semantic.data.repositoryImpl.PdfFileRepositoryImpl
import com.pdf.semantic.domain.repository.EmbeddingRepository
import com.pdf.semantic.domain.repository.PdfFileRepository
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
    abstract fun bindPdfRepository(pdfRepositoryImpl: PdfFileRepositoryImpl): PdfFileRepository

    @Binds
    @Singleton
    abstract fun bindEmbeddingRepository(embeddingRepositoryImpl: EmbeddingRepositoryImpl): EmbeddingRepository
}
