package com.krystian.chessclock;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    TextView gameNumberText, gameNumberTextRotated;
    TextView pointsText, pointsTextRotated;
    TextView timerText, timerTextRotated;
    TextView firstMoveText, firstMoveTextRotated;

    ToggleButton drawButton, drawButtonRotated;
    Button moveButton, moveButtonRotated;
    Button resignButton, resignButtonRotated;
    Button newGameButton;

    Game currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Game.setGameNumber(0); //if user goes back from a timer and set different settings
        Game.setPlayerOnePoints(0);
        Game.setPlayerTwoPoints(0);
        getSettings();
        setViewComponents();
        setStartValues();
        play();
    }

    public void getSettings() {
        int timeOne = getIntent().getIntExtra("playerOneTime", 15);
        int timeTwo = getIntent().getIntExtra("playerTwoTime", timeOne);
        int incrementOne = getIntent().getIntExtra("playerOneIncrement", 0);
        int incrementTwo = getIntent().getIntExtra("playerTwoIncrement", incrementOne);
        int numberOfGames = getIntent().getIntExtra("numberOfGames", 1);

        currentGame = new Game(timeOne*60, timeTwo*60, incrementOne, incrementTwo, numberOfGames); //time stored in seconds
    }

    public void setViewComponents() {
        gameNumberText = findViewById(R.id.game_number_text);
        gameNumberTextRotated = findViewById(R.id.game_number_text_rotated);
        pointsText = findViewById(R.id.points_text);
        pointsTextRotated = findViewById(R.id.points_text_rotated);
        timerText = findViewById(R.id.timer_text);
        timerTextRotated = findViewById(R.id.timer_text_rotated);
        firstMoveText = findViewById(R.id.first_move_text);
        firstMoveTextRotated = findViewById(R.id.first_move_text_rotated);
        drawButton = findViewById(R.id.draw_button);
        drawButtonRotated = findViewById(R.id.draw_button_rotated);
        moveButton = findViewById(R.id.move_button);
        moveButtonRotated = findViewById(R.id.move_button_rotated);
        resignButton = findViewById(R.id.resign_button);
        resignButtonRotated = findViewById(R.id.resign_button_rotated);
        newGameButton = findViewById(R.id.new_game_button);

        newGameButton.setOnClickListener(this);
        resignButtonRotated.setOnClickListener(this);
        resignButton.setOnClickListener(this);
        moveButtonRotated.setOnClickListener(this);
        moveButton.setOnClickListener(this);
        drawButtonRotated.setOnClickListener(this);
        drawButton.setOnClickListener(this);
    }

    public void setStartValues() {
        timerText.setText(String.format(getString(R.string.time), currentGame.getFirstTimer()/3600,
                currentGame.getFirstTimer()/60%60, currentGame.getFirstTimer()%60));
        timerTextRotated.setText(String.format(getString(R.string.time), currentGame.getSecondTimer()/3600,
                currentGame.getSecondTimer()/60%60, currentGame.getSecondTimer()%60));
        firstMoveText.setText(String.format(getString(R.string.first_move_text), 30));
        firstMoveTextRotated.setText(String.format(getString(R.string.first_move_text), 30));
        pointsText.setText(String.format(getString(R.string.points), Game.getPlayerOnePoints(),
                Game.getPlayerTwoPoints()));
        pointsTextRotated.setText(String.format(getString(R.string.points), Game.getPlayerTwoPoints(),
                Game.getPlayerOnePoints()));
        gameNumberText.setText(String.format(getString(R.string.game_number), Game.getGameNumber(),
                currentGame.getNumberOfGames()));
        gameNumberTextRotated.setText(String.format(getString(R.string.game_number), Game.getGameNumber(),
                currentGame.getNumberOfGames()));
        moveButton.setText(String.format(getString(R.string.move_number), currentGame.getMoveNumber()));
        moveButtonRotated.setText(String.format(getString(R.string.move_number), currentGame.getMoveNumberRotated()));

        newGameButton.setVisibility(View.INVISIBLE);
        firstMoveText.setVisibility(View.VISIBLE);
        firstMoveTextRotated.setVisibility(View.VISIBLE);
        drawButton.setChecked(false);
        drawButtonRotated.setChecked(false);
        resignButton.setAlpha(1);
        resignButton.setClickable(true);
        resignButtonRotated.setAlpha(1);
        resignButtonRotated.setClickable(true);
        if(currentGame.getIsFirstPlayerMove()) {
            moveButton.setAlpha(1);
            moveButton.setClickable(true);
            moveButtonRotated.setAlpha(0.5f);
            moveButtonRotated.setClickable(false);
            drawButton.setAlpha(1);
            drawButton.setClickable(true);
            drawButtonRotated.setAlpha(0.5f);
            drawButtonRotated.setClickable(false);
        }
        else {
            moveButton.setAlpha(0.5f);
            moveButton.setClickable(false);
            moveButtonRotated.setAlpha(1);
            moveButtonRotated.setClickable(true);
            drawButton.setAlpha(0.5f);
            drawButton.setClickable(false);
            drawButtonRotated.setAlpha(1);
            drawButtonRotated.setClickable(true);

        }
    }

    public void play() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                calculateTime();
                checkForResult();
                if(currentGame.getGameState() != GameState.RUNNING) finishGame();
                else handler.postDelayed(this, 1000);
            }
        });
    }

    public void calculateTime() { //and display it
        if(currentGame.getIsFirstPlayerMove()) {
            if(currentGame.getMoveNumber() == 1) {
                currentGame.setFirstMoveTime(currentGame.getFirstMoveTime()-1);
                firstMoveText.setText(String.format(getString(R.string.first_move_text),
                        currentGame.getFirstMoveTime()));
            }
            else {
                currentGame.setFirstTimer(currentGame.getFirstTimer()-1);
                timerText.setText(String.format(getString(R.string.time), currentGame.getFirstTimer()/3600,
                        currentGame.getFirstTimer()/60%60, currentGame.getFirstTimer()%60));
            }
        }
        else {
            if(currentGame.getMoveNumberRotated() == 1) {
                currentGame.setFirstMoveTimeRotated(currentGame.getFirstMoveTimeRotated()-1);
                firstMoveTextRotated.setText(String.format(getString(R.string.first_move_text),
                        currentGame.getFirstMoveTimeRotated()));
            }
            else {
                currentGame.setSecondTimer(currentGame.getSecondTimer()-1);
                timerTextRotated.setText(String.format(getString(R.string.time), currentGame.getSecondTimer()/3600,
                        currentGame.getSecondTimer()/60%60, currentGame.getSecondTimer()%60));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.move_button:
                currentGame.setIsFirstPlayerMove(false);
                currentGame.setMoveNumberRotated(currentGame.getMoveNumberRotated()+1);
                drawButton.setAlpha(0.5f);
                drawButton.setClickable(false);
                drawButtonRotated.setAlpha(1);
                drawButtonRotated.setClickable(true); //making a move declines a draw offer
                drawButtonRotated.setChecked(false); //a player must re-offer a draw every single time after it being declined
                moveButton.setAlpha(0.5f);
                moveButton.setClickable(false);
                moveButtonRotated.setAlpha(1);
                moveButtonRotated.setClickable(true);
                moveButtonRotated.setText(String.format(getString(R.string.move_number),
                        currentGame.getMoveNumberRotated()));
                if(currentGame.getMoveNumber() > 1) {
                    currentGame.setFirstTimer(currentGame.getFirstTimer()+currentGame.getFirstIncrement());
                    timerText.setText(String.format(getString(R.string.time), currentGame.getFirstTimer()/3600,
                            currentGame.getFirstTimer()/60%60, currentGame.getFirstTimer()%60));
                }
                else firstMoveText.setVisibility(View.INVISIBLE); //text no longer needed
                break;
            case R.id.move_button_rotated:
                currentGame.setIsFirstPlayerMove(true);
                currentGame.setMoveNumber(currentGame.getMoveNumber()+1);
                drawButtonRotated.setAlpha(0.5f);
                drawButtonRotated.setClickable(false);
                drawButton.setAlpha(1);
                drawButton.setClickable(true);
                drawButton.setChecked(false);
                moveButtonRotated.setAlpha(0.5f);
                moveButtonRotated.setClickable(false);
                moveButton.setAlpha(1);
                moveButton.setClickable(true);
                moveButton.setText(String.format(getString(R.string.move_number),
                        currentGame.getMoveNumber()));
                if(currentGame.getMoveNumberRotated() > 1) {
                    currentGame.setSecondTimer(currentGame.getSecondTimer()+currentGame.getSecondIncrement());
                    timerTextRotated.setText(String.format(getString(R.string.time), currentGame.getSecondTimer()/3600,
                            currentGame.getSecondTimer()/60%60, currentGame.getSecondTimer()%60));
                }
                else firstMoveTextRotated.setVisibility(View.INVISIBLE);
                break;
            case R.id.draw_button:
                drawButton.setAlpha(0.5f); //a draw can be offered only once a move and during your own
                drawButton.setClickable(false);
                break;
            case R.id.draw_button_rotated:
                drawButtonRotated.setAlpha(0.5f);
                drawButtonRotated.setClickable(false);
                break;
            case R.id.resign_button:
                currentGame.setGameState(GameState.LOST);
                break;
            case R.id.resign_button_rotated:
                currentGame.setGameState(GameState.WON);
                break;
            case R.id.new_game_button:
                if(currentGame.getNumberOfGames() == 1) {
                    Game.setGameNumber(0); //new game, but not a part of a match
                    Game.setPlayerOnePoints(0);
                    Game.setPlayerTwoPoints(0);
                }
                getSettings();
                setStartValues();
                play();
                break;
        }
    }

    public void checkForResult() {
        if(currentGame.getFirstMoveTime() == 0 || currentGame.getFirstTimer() == 0)
            currentGame.setGameState(GameState.LOST);
        if(currentGame.getFirstMoveTimeRotated() == 0 || currentGame.getSecondTimer() == 0)
            currentGame.setGameState(GameState.WON);
        if(drawButton.isChecked() && drawButtonRotated.isChecked())
            currentGame.setGameState(GameState.DRAWN);
    }

    public void finishGame() {
        disableAllButtons();
        if(currentGame.getNumberOfGames() == 1) {
            newGameButton.setText(getString(R.string.new_game));
            newGameButton.setVisibility(View.VISIBLE);
        }
        else
            if(Game.getGameNumber() < currentGame.getNumberOfGames()) {
                newGameButton.setText((getString(R.string.next_game)));
                newGameButton.setVisibility(View.VISIBLE);
            }
            else {
                newGameButton.setVisibility(View.INVISIBLE);
                showResults(); //in Alert Dialog
            }

        switch(currentGame.getGameState()) {
            case WON:
                Game.setPlayerOnePoints(Game.getPlayerOnePoints()+1);
                break;
            case DRAWN:
                Game.setPlayerOnePoints(Game.getPlayerOnePoints()+0.5f);
                Game.setPlayerTwoPoints(Game.getPlayerTwoPoints()+0.5f);
                break;
            case LOST:
                Game.setPlayerTwoPoints(Game.getPlayerTwoPoints()+1);
                break;
        }
        pointsText.setText(String.format(getString(R.string.points),
                Game.getPlayerOnePoints(), Game.getPlayerTwoPoints()));
        pointsTextRotated.setText(String.format(getString(R.string.points),
                Game.getPlayerTwoPoints(), Game.getPlayerOnePoints()));
    }


    public void disableAllButtons() {
        moveButton.setClickable(false);
        moveButtonRotated.setClickable(false);
        drawButton.setClickable(false);
        drawButtonRotated.setClickable(false);
        resignButton.setClickable(false);
        resignButtonRotated.setClickable(false);

        moveButton.setAlpha(0.5f);
        moveButtonRotated.setAlpha(0.5f);
        drawButton.setAlpha(0.5f);
        drawButtonRotated.setAlpha(0.5f);
        resignButton.setAlpha(0.5f);
        resignButtonRotated.setAlpha(0.5f);
    }
    public void showResults() {
        //AlertDialog
    }
}
