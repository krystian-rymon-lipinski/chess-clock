package com.krystian.chessclock.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomMatchDao {

    @Transaction
    @Query("SELECT * FROM ${ChessClockDatabase.customMatchTable}")
    fun getAll() : Flow<List<MatchWithGamesEntity>>

    @Insert
    fun addMatch(match: CustomMatchEntity)

    @Update
    fun updateMatch(match: CustomMatchEntity)

    @Delete
    fun deleteMatch(match: CustomMatchEntity)
}