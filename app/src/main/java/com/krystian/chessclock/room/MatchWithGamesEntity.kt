package com.krystian.chessclock.room

import androidx.room.Embedded
import androidx.room.Relation

data class MatchWithGamesEntity(
    @Embedded val matchEntity: CustomMatchEntity,
    @Relation(
        parentColumn = ChessClockDatabase.customMatchTableIdColumn,
        entityColumn = ChessClockDatabase.customGameTableMatchIdColumn
    )
    val games: List<CustomGameEntity>
)