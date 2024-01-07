package com.krystian.chessclock.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CustomMatchDao {

    @Query("")
    fun getAll()

    @Insert
    fun addMatch()

    @Update
    fun updateMatch()

    @Delete
    fun deleteMatch()
}