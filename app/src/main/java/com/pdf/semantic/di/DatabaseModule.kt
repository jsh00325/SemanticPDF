package com.pdf.semantic.di

import android.content.Context
import com.pdf.semantic.data.entity.MyObjectBox
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.objectbox.BoxStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideBoxStore(
        @ApplicationContext context: Context,
    ): BoxStore = MyObjectBox.builder().androidContext(context).build()
}
