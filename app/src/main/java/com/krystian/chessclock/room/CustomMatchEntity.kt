package com.krystian.chessclock.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ChessClockDatabase.customMatchTable)
data class CustomMatchEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ChessClockDatabase.customMatchTableIdColumn) val id: Int = 0,
    val name: String
)