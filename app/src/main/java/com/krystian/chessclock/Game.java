package com.krystian.chessclock;

public class Game {
    private static int gameNumber;
    private static float playerOnePoints;
    private static float playerTwoPoints;

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
    private int numberOfGames;


    public Game(int firstTimer, int secondTimer, int firstIncrement, int secondIncrement, int numberOfGames) {
        this.firstTimer = firstTimer;
        this.secondTimer = secondTimer;
        this.firstIncrement = firstIncrement;
        this.secondIncrement = secondIncrement;
        this.numberOfGames = numberOfGames;
        firstMoveTime = 30;
        firstMoveTimeRotated = 30;
        gameNumber++;
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

    public Game(Game game) { //if there are more games with the same settings
        this(game.firstTimer, game.secondTimer, game.firstIncrement, game.secondIncrement, game.numberOfGames);
    }

    public boolean getIsFirstPlayerMove() { return isFirstPlayerMove; }
    public void setIsFirstPlayerMove(boolean firstPlayerMove) { isFirstPlayerMove = firstPlayerMove; }

    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }

    public static int getGameNumber() { return gameNumber; }
    public static void setGameNumber(int gameNumber) { Game.gameNumber = gameNumber; }

    public static float getPlayerOnePoints() { return playerOnePoints; }
    public static void setPlayerOnePoints(float playerOnePoints) { Game.playerOnePoints = playerOnePoints; }

    public static float getPlayerTwoPoints() { return playerTwoPoints; }
    public static void setPlayerTwoPoints(float playerTwoPoints) { Game.playerTwoPoints = playerTwoPoints; }

    public int getFirstTimer() { return firstTimer; }
    public void setFirstTimer(int firstTimer) { this.firstTimer = firstTimer; }

    public int getSecondTimer() { return secondTimer; }
    public void setSecondTimer(int secondTimer) { this.secondTimer = secondTimer; }

    public int getFirstIncrement() { return firstIncrement; }
    public void setFirstIncrement(int firstIncrement) { this.firstIncrement = firstIncrement; }

    public int getSecondIncrement() { return secondIncrement; }
    public void setSecondIncrement(int secondIncrement) { this.secondIncrement = secondIncrement; }

    public int getNumberOfGames() { return numberOfGames; }
    public void setNumberOfGames(int numberOfGames) { this.numberOfGames = numberOfGames; }

    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }

    public int getMoveNumberRotated() { return moveNumberRotated; }
    public void setMoveNumberRotated(int moveNumberRotated) { this.moveNumberRotated = moveNumberRotated; }

    public int getFirstMoveTime() { return firstMoveTime; }
    public void setFirstMoveTime(int firstMoveTime) { this.firstMoveTime = firstMoveTime; }

    public int getFirstMoveTimeRotated() { return firstMoveTimeRotated; }
    public void setFirstMoveTimeRotated(int firstMoveTimeRotated) { this.firstMoveTimeRotated = firstMoveTimeRotated; }
}
