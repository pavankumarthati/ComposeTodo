package com.masterbit.composetodo.di

import android.content.Context
import com.masterbit.composetodo.db.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return TaskDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    @IODispatcher
    fun provideDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }


}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IODispatcher