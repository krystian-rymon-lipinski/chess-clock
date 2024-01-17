package com.krystian.chessclock.models

data class CustomGame(
    val id: Long = 0,
    val whiteTime: Int,
    val whiteIncrement: Int,
    val blackTime: Int,
    val blackIncrement: Int,
    val matchId: Long
) {

    fun areTimeSettingsDifferent() = (whiteTime != blackTime || whiteIncrement != blackIncrement)

    companion object {
        fun buildDefaultGame(matchId: Long) = CustomGame(
            whiteTime = 15,
            whiteIncrement = 3,
            blackTime = 15,
            blackIncrement = 3,
            matchId = matchId
        )
    }
}

