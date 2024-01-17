package com.krystian.chessclock.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = ChessClockDatabase.customGameTable,
    foreignKeys = [ForeignKey(
        entity = CustomMatchEntity::class,
        parentColumns = [ChessClockDatabase.customMatchTableIdColumn],
        childColumns = [ChessClockDatabase.customGameTableMatchIdColumn],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CustomGameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "white_time") val whiteTime: Int,
    @ColumnInfo(name = "white_increment") val whiteIncrement: Int,
    @ColumnInfo(name = "black_time") val blackTime: Int,
    @ColumnInfo(name = "black_increment") val blackIncrement: Int,
    @ColumnInfo(name = ChessClockDatabase.customGameTableMatchIdColumn, index = true) val matchId: Long
)