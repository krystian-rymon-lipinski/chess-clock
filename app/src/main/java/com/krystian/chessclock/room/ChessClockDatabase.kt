package com.krystian.chessclock.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CustomMatchEntity::class, CustomGameEntity::class], version = 1)
abstract class ChessClockDatabase : RoomDatabase() {

    abstract val customMatchDao: CustomMatchDao
    abstract val customGameDao: CustomGameDao


    companion object {
        const val databaseName = "chess-clock-database"
        const val customMatchTable = "custom_match_table"
        const val customGameTable = "custom_game_table"

        const val customMatchTableIdColumn = "id"
        const val customGameTableMatchIdColumn = "match_id"
    }
}