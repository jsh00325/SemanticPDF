package com.pdf.semantic.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    @ModelDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
}
