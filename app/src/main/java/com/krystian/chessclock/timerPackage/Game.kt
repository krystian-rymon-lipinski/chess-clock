package com.krystian.chessclock.timerPackage

class Game(
    var firstTimer: Int,
    var firstIncrement: Int,
    var secondTimer: Int,
    var secondIncrement: Int,
    gameNumber: Int
) {
    @JvmField
    var isFirstPlayerMove = false
    var gameState: GameState
    var moveNumber = 0
    var moveNumberRotated = 0
    var firstMoveTime = 30
    var firstMoveTimeRotated = 30

    init {
        gameState = GameState.RUNNING
        if (gameNumber % 2 != 0) {
            isFirstPlayerMove = true
            moveNumber = 1 //first player as white
            moveNumberRotated = 0
        } else {
            isFirstPlayerMove = false
            moveNumber = 0 //second player as white
            moveNumberRotated = 1
        }
    }
}
