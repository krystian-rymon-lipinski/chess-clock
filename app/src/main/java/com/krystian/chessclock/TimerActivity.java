package com.krystian.chessclock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
        setViewStartValues();
        play();
    }

    public void getSettings() {
        int timeOne = getIntent().getIntExtra("playerOneTime", 15);
        int timeTwo = getIntent().getIntExtra("playerTwoTime", timeOne); //if there's no player two, it means
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

    public void setViewStartValues() {
        setTextViews();
        setButtons();
        if(currentGame.getNumberOfGames() > 1) setButtonColors(); //switching sides after every game in a match
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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.move_button:
                makeWhiteMove();
                break;
            case R.id.move_button_rotated:
                makeBlackMove();
                break;
            case R.id.draw_button:
                setButton(drawButton, false); //a draw can be offered only once a move and during your own
                break;
            case R.id.draw_button_rotated:
                setButton(drawButtonRotated, false);
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
                setViewStartValues();
                play();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(currentGame.getGameState() == GameState.RUNNING)
            currentGame.setGameState(GameState.PAUSED);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentGame.getGameState() == GameState.PAUSED) {
            currentGame.setGameState(GameState.RUNNING);
            setButtons();
            play();
        }
    }

    public void setTextViews() {
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

        firstMoveText.setVisibility(View.VISIBLE);
        firstMoveTextRotated.setVisibility(View.VISIBLE);
    }

    public void setButtons() {
        newGameButton.setVisibility(View.INVISIBLE);
        drawButton.setChecked(false);
        drawButtonRotated.setChecked(false);
        setButton(resignButton, true);
        setButton(resignButtonRotated, true);
        if(currentGame.getIsFirstPlayerMove()) {
            setButton(moveButton, true);
            setButton(drawButton, true);
            setButton(moveButtonRotated, false);
            setButton(drawButtonRotated, false);
        }
        else {
            setButton(moveButton, false);
            setButton(drawButton, false);
            setButton(moveButtonRotated, true);
            setButton(drawButtonRotated, true);
        }
    }

    public void setButton(Button button, boolean isEnabled) {
        if(isEnabled) {
            button.setClickable(true);
            button.setAlpha(1);
        }
        else {
            button.setClickable(false);
            button.setAlpha(0.5f);
        }
    }

    public void setButtonColors() {
        if(Game.getGameNumber()%2 != 0) {
            moveButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_white_button));
            drawButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_white_button));
            resignButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_white_button));
            moveButton.setTextColor(getResources().getColor(R.color.blackColor));
            drawButton.setTextColor(getResources().getColor(R.color.blackColor));
            resignButton.setTextColor(getResources().getColor(R.color.blackColor));

            moveButtonRotated.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_black_button));
            drawButtonRotated.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_black_button));
            resignButtonRotated.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_black_button));
            moveButtonRotated.setTextColor(getResources().getColor(R.color.whiteColor));
            drawButtonRotated.setTextColor(getResources().getColor(R.color.whiteColor));
            resignButtonRotated.setTextColor(getResources().getColor(R.color.whiteColor));
        }
        else {
            moveButtonRotated.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_white_button));
            drawButtonRotated.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_white_button));
            resignButtonRotated.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_white_button));
            moveButtonRotated.setTextColor(getResources().getColor(R.color.blackColor));
            drawButtonRotated.setTextColor(getResources().getColor(R.color.blackColor));
            resignButtonRotated.setTextColor(getResources().getColor(R.color.blackColor));

            moveButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_black_button));
            drawButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_black_button));
            resignButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.timer_black_button));
            moveButton.setTextColor(getResources().getColor(R.color.whiteColor));
            drawButton.setTextColor(getResources().getColor(R.color.whiteColor));
            resignButton.setTextColor(getResources().getColor(R.color.whiteColor));
        }
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



    public void makeWhiteMove() {
        currentGame.setIsFirstPlayerMove(false);
        currentGame.setMoveNumberRotated(currentGame.getMoveNumberRotated()+1);
        setButton(moveButton, false);
        setButton(drawButton, false);
        setButton(moveButtonRotated, true);
        setButton(drawButtonRotated, true); //making a move declines a draw offer

        drawButtonRotated.setChecked(false); //a player must re-offer a draw every single time after it being declined
        moveButtonRotated.setText(String.format(getString(R.string.move_number),
                currentGame.getMoveNumberRotated()));
        if(currentGame.getMoveNumber() > 1) {
            if(currentGame.getFirstIncrement() != 0) {
                currentGame.setFirstTimer(currentGame.getFirstTimer()+currentGame.getFirstIncrement());
                timerText.setText(String.format(getString(R.string.time), currentGame.getFirstTimer()/3600,
                        currentGame.getFirstTimer()/60%60, currentGame.getFirstTimer()%60));
            }
        }
        else firstMoveText.setVisibility(View.INVISIBLE); //text no longer needed - first move already made
    }

    public void makeBlackMove() {
        currentGame.setIsFirstPlayerMove(true);
        currentGame.setMoveNumber(currentGame.getMoveNumber()+1);
        setButton(moveButton, true);
        setButton(drawButton, true);
        setButton(moveButtonRotated, false);
        setButton(drawButtonRotated, false);

        drawButton.setChecked(false);
        moveButton.setText(String.format(getString(R.string.move_number),
                currentGame.getMoveNumber()));
        if(currentGame.getMoveNumberRotated() > 1) {
            if(currentGame.getSecondIncrement() != 0) {
                currentGame.setSecondTimer(currentGame.getSecondTimer() + currentGame.getSecondIncrement());
                timerTextRotated.setText(String.format(getString(R.string.time), currentGame.getSecondTimer() / 3600,
                        currentGame.getSecondTimer() / 60 % 60, currentGame.getSecondTimer() % 60));
            }
        }
        else firstMoveTextRotated.setVisibility(View.INVISIBLE);
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

        if(currentGame.getNumberOfGames() == 1) {
            newGameButton.setText(getString(R.string.new_game));
            newGameButton.setVisibility(View.VISIBLE);
        }
        else {
            if (Game.getGameNumber() < currentGame.getNumberOfGames()) {
                newGameButton.setText((getString(R.string.next_game)));
                newGameButton.setVisibility(View.VISIBLE);
            } else {
                newGameButton.setVisibility(View.INVISIBLE);
                showResults(); //in Alert Dialog
            }
        }
    }


    public void disableAllButtons() {
        setButton(moveButton, false);
        setButton(drawButton, false);
        setButton(resignButton, false);

        setButton(moveButtonRotated, false);
        setButton(drawButtonRotated, false);
        setButton(resignButtonRotated, false);
    }
    public void showResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.results, Game.getPlayerOnePoints(), Game.getPlayerTwoPoints()));
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(TimerActivity.this, MainActivity.class));
            }
        });
        builder.show();
    }
}
