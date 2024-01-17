package com.krystian.chessclock.models

enum class GameState {
    RUNNING,
    PAUSED,
    LOST,
    DRAWN,
    WON //lost and won from the perspective of first player (with not rotated views)
}
