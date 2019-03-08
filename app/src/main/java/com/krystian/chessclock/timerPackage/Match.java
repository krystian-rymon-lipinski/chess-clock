package com.krystian.chessclock.timerPackage;

import java.util.ArrayList;

public class Match {
    private Game currentGame;

    private int numberOfGames;
    private float firstPlayerPoints;
    private float secondPlayerPoints;
    private int gameNumber;

    private ArrayList<Integer> oneTimes;
    private ArrayList<Integer> oneIncrements;
    private ArrayList<Integer> twoTimes;
    private ArrayList<Integer> twoIncrements;

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

    public Match(int numberOfGames, ArrayList<Integer> oneTimes, ArrayList<Integer> oneIncrements,
                 ArrayList<Integer> twoTimes, ArrayList<Integer> twoIncrements) {
        this(numberOfGames);
        this.oneTimes = oneTimes;
        this.oneIncrements = oneIncrements;
        this.twoTimes = twoTimes;
        this.twoIncrements = twoIncrements;
    }

    public Match reset(Match match) {
        match.setGameNumber(1);
        match.setFirstPlayerPoints(0);
        match.setSecondPlayerPoints(0);
        match.setNumberOfGames(1);


        return match;
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

    public ArrayList<Integer> getOneTimes() { return oneTimes; }
    public void setOneTimes(ArrayList<Integer> oneTimes) { this.oneTimes = oneTimes; }

    public ArrayList<Integer> getOneIncrements() { return oneIncrements; }
    public void setOneIncrements(ArrayList<Integer> oneIncrements) { this.oneIncrements = oneIncrements; }

    public ArrayList<Integer> getTwoTimes() { return twoTimes; }
    public void setTwoTimes(ArrayList<Integer> twoTimes) { this.twoTimes = twoTimes; }

    public ArrayList<Integer> getTwoIncrements() { return twoIncrements; }
    public void setTwoIncrements(ArrayList<Integer> twoIncrements) { this.twoIncrements = twoIncrements; }
}
