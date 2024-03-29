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

    @Query("SELECT * FROM ${ChessClockDatabase.customMatchTable}")
    fun getAll() : Flow<List<CustomMatchEntity>>

    @Query("SELECT * FROM ${ChessClockDatabase.customMatchTable} " +
            "WHERE ${ChessClockDatabase.customMatchTableIdColumn} = :matchId")
    fun getById(matchId: Long) : Flow<CustomMatchEntity>

    @Transaction
    @Query("SELECT * FROM ${ChessClockDatabase.customMatchTable}")
    fun getAllWithGames() : Flow<List<MatchWithGamesEntity>>

    @Insert
    fun addMatch(match: CustomMatchEntity): Long

    @Update
    fun updateMatch(match: CustomMatchEntity)

    @Delete
    fun deleteMatch(match: CustomMatchEntity)
}