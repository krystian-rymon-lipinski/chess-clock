package com.krystian.chessclock.timerPackage

enum class GameState {
    RUNNING,
    PAUSED,
    LOST,
    DRAWN,
    WON //lost and won from the perspective of first player (with not rotated views)
}
