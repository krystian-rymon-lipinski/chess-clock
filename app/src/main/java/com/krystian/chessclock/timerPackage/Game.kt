package com.krystian.chessclock.timerPackage;

public class Game {

    private boolean isFirstPlayerMove;
    private GameState gameState;
    private int firstTimer; //time stored in seconds
    private int secondTimer;
    private int firstIncrement; //time added after each move
    private int secondIncrement;
    private int moveNumber;
    private int moveNumberRotated;
    private int firstMoveTime; //"n seconds to make a first move"
    private int firstMoveTimeRotated;


    public Game(int firstTimer, int firstIncrement, int secondTimer, int secondIncrement, int gameNumber) {
        this.firstTimer = firstTimer;
        this.secondTimer = secondTimer;
        this.firstIncrement = firstIncrement;
        this.secondIncrement = secondIncrement;
        firstMoveTime = 30;
        firstMoveTimeRotated = 30;
        gameState = GameState.RUNNING;
        if(gameNumber%2!=0) {
            isFirstPlayerMove = true;
            moveNumber = 1; //first player as white
            moveNumberRotated = 0;
        }
        else {
            isFirstPlayerMove = false;
            moveNumber = 0; //second player as white
            moveNumberRotated = 1;
        }
    }

    public boolean getIsFirstPlayerMove() { return isFirstPlayerMove; }
    public void setIsFirstPlayerMove(boolean firstPlayerMove) { isFirstPlayerMove = firstPlayerMove; }

    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }

    public int getFirstTimer() { return firstTimer; }
    public void setFirstTimer(int firstTimer) { this.firstTimer = firstTimer; }

    public int getSecondTimer() { return secondTimer; }
    public void setSecondTimer(int secondTimer) { this.secondTimer = secondTimer; }

    public int getFirstIncrement() { return firstIncrement; }
    public void setFirstIncrement(int firstIncrement) { this.firstIncrement = firstIncrement; }

    public int getSecondIncrement() { return secondIncrement; }
    public void setSecondIncrement(int secondIncrement) { this.secondIncrement = secondIncrement; }

    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }

    public int getMoveNumberRotated() { return moveNumberRotated; }
    public void setMoveNumberRotated(int moveNumberRotated) { this.moveNumberRotated = moveNumberRotated; }

    public int getFirstMoveTime() { return firstMoveTime; }
    public void setFirstMoveTime(int firstMoveTime) { this.firstMoveTime = firstMoveTime; }

    public int getFirstMoveTimeRotated() { return firstMoveTimeRotated; }
    public void setFirstMoveTimeRotated(int firstMoveTimeRotated) { this.firstMoveTimeRotated = firstMoveTimeRotated; }
}
