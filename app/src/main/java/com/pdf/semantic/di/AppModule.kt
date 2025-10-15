package com.pdf.semantic.di

import android.content.Context
import com.pdf.semantic.data.repositoryImpl.PdfRepositoryImpl
import com.pdf.semantic.domain.repository.PdfRepository
import com.pdf.semantic.domain.usecase.ParsePdfUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // 1. PdfRepositoryImpl에 필요한 Context를 제공하는 방법
    // Hilt가 이미 알고 있으므로 별도 정의는 불필요하지만, 명시적으로 작성 가능

    // 2. PdfRepository(인터페이스)를 요청받으면 PdfRepositoryImpl(구현체)을 제공하도록 설정
    @Provides
    @Singleton // Repository는 앱 전역에서 하나만 존재하도록 싱글톤으로 설정
    fun providePdfRepository(
        @ApplicationContext context: Context,
    ): PdfRepository = PdfRepositoryImpl(context)

    // 3. ParsePdfUseCase를 제공하는 방법 설정
    // UseCase는 보통 상태를 갖지 않으므로 싱글톤이 아니어도 됨
    @Provides
    fun provideParsePdfUseCase(repository: PdfRepository): ParsePdfUseCase {
        // Hilt가 위에서 정의한 providePdfRepository를 통해 repository를 자동으로 주입해 줌
        return ParsePdfUseCase(repository)
    }
}
