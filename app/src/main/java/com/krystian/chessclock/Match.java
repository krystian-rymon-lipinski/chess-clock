package com.krystian.chessclock;

public class Match {
    private Game currentGame;

    private int numberOfGames;
    private float firstPlayerPoints;
    private float secondPlayerPoints;
    private int gameNumber;

    private int[] oneTimes;
    private int[] oneIncrements;
    private int[] twoTimes;
    private int[] twoIncrements;

    public Match(int numberOfGames) {
        this.numberOfGames = numberOfGames;
        firstPlayerPoints = 0;
        secondPlayerPoints = 0;
        gameNumber = 1;
    }

    public Match() {
        this(1);
    }

    public Match(Match match) {
        this(match.numberOfGames, match.oneTimes, match.oneIncrements, match.twoTimes, match.twoIncrements);
    }

    public Match(int numberOfGames, int[] oneTimes, int[] oneIncrements, int[] twoTimes, int[] twoIncrements) { //custom matches
        this(numberOfGames);
        this.oneTimes = oneTimes;
        this.oneIncrements = oneIncrements;
        this.twoTimes = twoTimes;
        this.twoIncrements = twoIncrements;
    }

    public Game getCurrentGame() { return currentGame; }
    public void setCurrentGame(Game currentGame) { this.currentGame = currentGame; }

    public int getNumberOfGames() { return numberOfGames; }
    public void setNumberOfGames(int numberOfGames) { this.numberOfGames = numberOfGames; }

    public float getFirstPlayerPoints() { return firstPlayerPoints; }
    public void setFirstPlayerPoints(float firstPlayerPoints) { this.firstPlayerPoints = firstPlayerPoints; }

    public float getSecondPlayerPoints() { return secondPlayerPoints; }
    public void setSecondPlayerPoints(float secondPlayerPoints) { this.secondPlayerPoints = secondPlayerPoints; }

    public int getGameNumber() { return gameNumber; }
    public void setGameNumber(int gameNumber) { this.gameNumber = gameNumber; }

    public int[] getOneTimes() { return oneTimes; }
    public void setOneTimes(int[] oneTimes) { this.oneTimes = oneTimes; }

    public int[] getOneIncrements() { return oneIncrements; }
    public void setOneIncrements(int[] oneIncrements) { this.oneIncrements = oneIncrements; }

    public int[] getTwoTimes() { return twoTimes; }
    public void setTwoTimes(int[] twoTimes) { this.twoTimes = twoTimes; }

    public int[] getTwoIncrements() { return twoIncrements; }
    public void setTwoIncrements(int[] twoIncrements) { this.twoIncrements = twoIncrements; }
}
