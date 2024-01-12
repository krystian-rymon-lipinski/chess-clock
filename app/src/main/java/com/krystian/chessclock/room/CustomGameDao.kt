package com.krystian.chessclock.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomGameDao {

    @Query("SELECT * FROM ${ChessClockDatabase.customGameTable}")
    fun getAllGames() : Flow<List<CustomGameEntity>>

    @Query("SELECT * FROM ${ChessClockDatabase.customGameTable} " +
            "WHERE ${ChessClockDatabase.customGameTableMatchIdColumn} = :matchId")
    fun getAllWithMatchId(matchId: Long) : Flow<List<CustomGameEntity>>

    @Query("SELECT * FROM ${ChessClockDatabase.customGameTable} " +
            "WHERE ${ChessClockDatabase.customGameTableIdColumn} = :id")
    fun getGameWithId(id: Int) : Flow<CustomGameEntity>

    @Insert
    fun addGame(game: CustomGameEntity)

    @Update
    fun updateGame(game: CustomGameEntity)

    @Delete
    fun deleteGame(game: CustomGameEntity)

}