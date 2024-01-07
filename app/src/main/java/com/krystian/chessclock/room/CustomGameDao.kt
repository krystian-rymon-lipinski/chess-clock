package com.krystian.chessclock.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CustomGameDao {

    @Query("")
    fun getGameWithId(id: Int)

    @Insert
    fun addGame()

    @Update
    fun updateGame()

    @Delete
    fun deleteGame()

}