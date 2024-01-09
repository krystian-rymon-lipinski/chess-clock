package com.krystian.chessclock.dependency_injection

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.krystian.chessclock.room.ChessClockDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context) : RoomDatabase {
        return Room.databaseBuilder(
            context,
            ChessClockDatabase::class.java,
            ChessClockDatabase.databaseName
        ).build()
    }
}