package com.krystian.chessclock.timerPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.krystian.chessclock.ExtraValues;
import com.krystian.chessclock.customMatchPackage.CustomMatchDatabase;
import com.krystian.chessclock.MainActivity;
import com.krystianrymonlipinski.chessclock.R;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    TextView gameNumberText, gameNumberTextRotated;
    TextView pointsText, pointsTextRotated;
    TextView timerText, timerTextRotated;
    TextView firstMoveText, firstMoveTextRotated;

    ToggleButton drawButton, drawButtonRotated;
    Button moveButton, moveButtonRotated;
    Button resignButton, resignButtonRotated;
    Button newGameButton;

    Match match;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setViewComponents(); //find views and attach listeners
        getSettings(); //find match parameters ((non)-custom, time, increments, number of games) and store it in match object
        setMatchGame(); //set n-th game with its parameters
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

    public void getSettings() { //and store it into a match object

        String customMatchName = getIntent().getStringExtra(ExtraValues.CUSTOM_MATCH_NAME);
        int numberOfGames;
        ArrayList<Integer> timeOnes = new ArrayList<>();
        ArrayList<Integer> incrementOnes = new ArrayList<>();
        ArrayList<Integer> timeTwos = new ArrayList<>();
        ArrayList<Integer> incrementTwos = new ArrayList<>();

        if(customMatchName == null) {
            numberOfGames = getIntent().getIntExtra(ExtraValues.NUMBER_OF_GAMES, 1); //if no extra send, it's a single game
            int timeOne = getIntent().getIntExtra(ExtraValues.PLAYER_ONE_TIME, 15);
            int timeTwo = getIntent().getIntExtra(ExtraValues.PLAYER_TWO_TIME, timeOne); //if there's no player two's extra, timeTwo = timeOne;
            int incrementOne = getIntent().getIntExtra(ExtraValues.PLAYER_ONE_INCREMENT, 0);
            int incrementTwo = getIntent().getIntExtra(ExtraValues.PLAYER_TWO_INCREMENT, incrementOne); //same as time
            for(int i=0; i<numberOfGames; i++) {
                timeOnes.add(timeOne);
                incrementOnes.add(incrementOne);
                timeTwos.add(timeTwo);
                incrementTwos.add(incrementTwo);
            }
        }

        else {
            CustomMatchDatabase customDb = new CustomMatchDatabase();
            String query = "SELECT * FROM " + customMatchName + ";";
            Cursor cursor = customDb.accessDatabase(this).rawQuery(query, null);
            numberOfGames = cursor.getCount();
            cursor.moveToFirst();
            do {
                timeOnes.add(cursor.getInt(2));
                incrementOnes.add(cursor.getInt(3));
                timeTwos.add(cursor.getInt(4));
                incrementTwos.add(cursor.getInt(5));
            } while(cursor.moveToNext());
            cursor.close();
            customDb.closeDatabase();
        }
        match = new Match(numberOfGames, timeOnes, incrementOnes, timeTwos, incrementTwos);
    }

    public void setMatchGame() {
        int game = match.getGameNumber();
        match.setCurrentGame(new Game(match.getOneTimes().get(game-1)*60, match.getOneIncrements().get(game-1),
                match.getTwoTimes().get(game-1)*60, match.getTwoIncrements().get(game-1), game)); //create a game
        setViewStartValues(); //textViews values, which button to enable, etc.
        play();
    }

    public void setViewStartValues() {
        setTextViews(); //timer values, points, number of move
        setButtons(); //draw, resign and move buttons
        if(match.getNumberOfGames() > 1) setButtonColors(); //switching sides after every game in a match
    }

    public void play() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                calculateTime();
                checkForResult(); //maybe someone won
                if(match.getCurrentGame().getGameState() != GameState.RUNNING) finishGame();
                else handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId(); //switch gives compilation error: "constant expression required"
        if (id == R.id.move_button) {
            makeWhiteMove();
        } else if (id == R.id.move_button_rotated) {
            makeBlackMove();
        } else if (id == R.id.draw_button) {
            setButton(drawButton, false); //a draw can be offered only once a move and during your own
        } else if (id == R.id.draw_button_rotated) {
            setButton(drawButtonRotated, false);
        } else if (id == R.id.resign_button) {
            match.getCurrentGame().setGameState(GameState.LOST);
        } else if (id == R.id.resign_button_rotated) {
            match.getCurrentGame().setGameState(GameState.WON);
        } else if (id == R.id.new_game_button) {
            if (match.getNumberOfGames() == 1) { //replay a single game
                match = new Match(match); //same settings of a single game with GameState = RUNNING -> handler starts counting again
            } else
                match.setGameNumber(match.getGameNumber() + 1);
            setMatchGame(); //the next one
        }
    }
/*
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
*/
    public void setTextViews() {
        timerText.setText(String.format(getString(R.string.time), match.getCurrentGame().getFirstTimer()/3600,
                match.getCurrentGame().getFirstTimer()/60%60, match.getCurrentGame().getFirstTimer()%60));
        timerTextRotated.setText(String.format(getString(R.string.time), match.getCurrentGame().getSecondTimer()/3600,
                match.getCurrentGame().getSecondTimer()/60%60, match.getCurrentGame().getSecondTimer()%60));
        firstMoveText.setText(String.format(getString(R.string.first_move_text), 30));
        firstMoveTextRotated.setText(String.format(getString(R.string.first_move_text), 30));
        pointsText.setText(String.format(getString(R.string.points), match.getFirstPlayerPoints(),
                match.getSecondPlayerPoints()));
        pointsTextRotated.setText(String.format(getString(R.string.points), match.getSecondPlayerPoints(),
                match.getFirstPlayerPoints()));
        gameNumberText.setText(String.format(getString(R.string.game_number), match.getGameNumber(),
                match.getNumberOfGames()));
        gameNumberTextRotated.setText(String.format(getString(R.string.game_number), match.getGameNumber(),
                match.getNumberOfGames()));
        moveButton.setText(String.format(getString(R.string.move_number), match.getCurrentGame().getMoveNumber()));
        moveButtonRotated.setText(String.format(getString(R.string.move_number), match.getCurrentGame().getMoveNumberRotated()));

        firstMoveText.setVisibility(View.VISIBLE);
        firstMoveTextRotated.setVisibility(View.VISIBLE);
    }

    public void setButtons() {
        newGameButton.setVisibility(View.INVISIBLE);
        drawButton.setChecked(false);
        drawButtonRotated.setChecked(false);
        setButton(resignButton, true);
        setButton(resignButtonRotated, true);
        if(match.getCurrentGame().getIsFirstPlayerMove()) {
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
        if(match.getGameNumber()%2 != 0) {
            moveButton.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_white_button));
            moveButton.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_white_button));
            drawButton.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_white_button));
            resignButton.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_white_button));
            moveButton.setTextColor(ContextCompat.getColor(this, R.color.blackColor));
            drawButton.setTextColor(ContextCompat.getColor(this, R.color.blackColor));
            resignButton.setTextColor(ContextCompat.getColor(this, R.color.blackColor));

            moveButtonRotated.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_black_button));
            drawButtonRotated.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_black_button));
            resignButtonRotated.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_black_button));
            moveButtonRotated.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            drawButtonRotated.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            resignButtonRotated.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        }
        else {
            moveButtonRotated.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_white_button));
            drawButtonRotated.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_white_button));
            resignButtonRotated.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_white_button));
            moveButtonRotated.setTextColor(ContextCompat.getColor(this, R.color.blackColor));
            drawButtonRotated.setTextColor(ContextCompat.getColor(this, R.color.blackColor));
            resignButtonRotated.setTextColor(ContextCompat.getColor(this, R.color.blackColor));

            moveButton.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_black_button));
            drawButton.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_black_button));
            resignButton.setBackground(ContextCompat.getDrawable(this, R.drawable.timer_black_button));
            moveButton.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            drawButton.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            resignButton.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        }
    }



    public void calculateTime() { //and display it
        if(match.getCurrentGame().getIsFirstPlayerMove()) {
            if(match.getCurrentGame().getMoveNumber() == 1) {
                match.getCurrentGame().setFirstMoveTime(match.getCurrentGame().getFirstMoveTime()-1);
                firstMoveText.setText(String.format(getString(R.string.first_move_text),
                        match.getCurrentGame().getFirstMoveTime()));
            }
            else {
                match.getCurrentGame().setFirstTimer(match.getCurrentGame().getFirstTimer()-1);
                timerText.setText(String.format(getString(R.string.time), match.getCurrentGame().getFirstTimer()/3600,
                        match.getCurrentGame().getFirstTimer()/60%60, match.getCurrentGame().getFirstTimer()%60));
            }
        }
        else {
            if(match.getCurrentGame().getMoveNumberRotated() == 1) {
                match.getCurrentGame().setFirstMoveTimeRotated(match.getCurrentGame().getFirstMoveTimeRotated()-1);
                firstMoveTextRotated.setText(String.format(getString(R.string.first_move_text),
                        match.getCurrentGame().getFirstMoveTimeRotated()));
            }
            else {
                match.getCurrentGame().setSecondTimer(match.getCurrentGame().getSecondTimer()-1);
                timerTextRotated.setText(String.format(getString(R.string.time), match.getCurrentGame().getSecondTimer()/3600,
                        match.getCurrentGame().getSecondTimer()/60%60, match.getCurrentGame().getSecondTimer()%60));
            }
        }
    }

    public void makeWhiteMove() {
        match.getCurrentGame().setIsFirstPlayerMove(false);
        match.getCurrentGame().setMoveNumberRotated(match.getCurrentGame().getMoveNumberRotated()+1);
        setButton(moveButton, false);
        setButton(drawButton, false);
        setButton(moveButtonRotated, true);
        setButton(drawButtonRotated, true); //making a move declines a draw offer

        drawButtonRotated.setChecked(false); //a player must re-offer a draw every single time after it being declined
        moveButtonRotated.setText(String.format(getString(R.string.move_number),
                match.getCurrentGame().getMoveNumberRotated()));
        if(match.getCurrentGame().getMoveNumber() > 1) {
            if(match.getCurrentGame().getFirstIncrement() != 0) {
                match.getCurrentGame().setFirstTimer(
                        match.getCurrentGame().getFirstTimer()+match.getCurrentGame().getFirstIncrement());
                timerText.setText(String.format(getString(R.string.time), match.getCurrentGame().getFirstTimer()/3600,
                        match.getCurrentGame().getFirstTimer()/60%60, match.getCurrentGame().getFirstTimer()%60));
            }
        }
        else firstMoveText.setVisibility(View.INVISIBLE); //text no longer needed - first move already made
    }

    public void makeBlackMove() {
        match.getCurrentGame().setIsFirstPlayerMove(true);
        match.getCurrentGame().setMoveNumber(match.getCurrentGame().getMoveNumber()+1);
        setButton(moveButton, true);
        setButton(drawButton, true);
        setButton(moveButtonRotated, false);
        setButton(drawButtonRotated, false);

        drawButton.setChecked(false);
        moveButton.setText(String.format(getString(R.string.move_number),
                match.getCurrentGame().getMoveNumber()));
        if(match.getCurrentGame().getMoveNumberRotated() > 1) {
            if(match.getCurrentGame().getSecondIncrement() != 0) {
                match.getCurrentGame().setSecondTimer(
                        match.getCurrentGame().getSecondTimer() + match.getCurrentGame().getSecondIncrement());
                timerTextRotated.setText(String.format(getString(R.string.time), match.getCurrentGame().getSecondTimer() / 3600,
                        match.getCurrentGame().getSecondTimer() / 60 % 60, match.getCurrentGame().getSecondTimer() % 60));
            }
        }
        else firstMoveTextRotated.setVisibility(View.INVISIBLE);
    }

    public void checkForResult() {
        if(match.getCurrentGame().getFirstMoveTime() == 0 || match.getCurrentGame().getFirstTimer() == 0)
            match.getCurrentGame().setGameState(GameState.LOST);
        else if(match.getCurrentGame().getFirstMoveTimeRotated() == 0 || match.getCurrentGame().getSecondTimer() == 0)
            match.getCurrentGame().setGameState(GameState.WON);
        else if(drawButton.isChecked() && drawButtonRotated.isChecked())
            match.getCurrentGame().setGameState(GameState.DRAWN);
    }

    public void finishGame() {
        disableAllButtons();

        switch(match.getCurrentGame().getGameState()) {
            case WON: //from player one perspective
                match.setFirstPlayerPoints(match.getFirstPlayerPoints()+1);
                break;
            case DRAWN:
                match.setFirstPlayerPoints(match.getFirstPlayerPoints()+0.5f);
                match.setSecondPlayerPoints(match.getSecondPlayerPoints()+0.5f);
                break;
            case LOST:
                match.setSecondPlayerPoints(match.getSecondPlayerPoints()+1);
                break;
        }
        pointsText.setText(String.format(getString(R.string.points),
                match.getFirstPlayerPoints(), match.getSecondPlayerPoints()));
        pointsTextRotated.setText(String.format(getString(R.string.points),
                match.getSecondPlayerPoints(), match.getFirstPlayerPoints()));

        if(match.getNumberOfGames() == 1) {
            newGameButton.setText(getString(R.string.new_game));
            newGameButton.setVisibility(View.VISIBLE);
        }
        else {
            if (match.getGameNumber() < match.getNumberOfGames()) {
                newGameButton.setText((getString(R.string.next_game)));
                newGameButton.setVisibility(View.VISIBLE);
            }
            else {
                newGameButton.setVisibility(View.INVISIBLE);
                showResults(); //in Alert Dialog; end of a match
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
        builder.setMessage(getString(R.string.results,
                match.getFirstPlayerPoints(), match.getSecondPlayerPoints()))
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(TimerActivity.this, MainActivity.class));
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() { startActivity(new Intent(this, MainActivity.class)); }
}
