package com.krystian.chessclock.dependency_injection

import com.krystian.chessclock.room.ChessClockDatabase
import com.krystian.chessclock.room.CustomGameDao
import com.krystian.chessclock.room.CustomMatchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {


    @Singleton
    @Provides
    fun provideCustomMatchDao(db: ChessClockDatabase) : CustomMatchDao {
        return db.customMatchDao
    }

    @Singleton
    @Provides
    fun provideCustomGameDao(db: ChessClockDatabase) : CustomGameDao {
        return db.customGameDao
    }
}