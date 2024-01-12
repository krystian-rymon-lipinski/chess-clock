package com.krystian.chessclock.model

data class CustomGame(
    val id: Long = 0,
    val whiteTime: Int,
    val whiteIncrement: Int,
    val blackTime: Int,
    val blackIncrement: Int,
    val matchId: Long
)

