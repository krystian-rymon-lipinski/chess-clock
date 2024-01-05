package com.krystian.chessclock;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.krystianrymonlipinski.chessclock.R;

import com.krystian.chessclock.customMatchPackage.CustomGameActivityList;
import com.krystian.chessclock.customMatchPackage.CustomMatchActivityList;
import com.krystian.chessclock.customMatchPackage.CustomMatchDatabase;
import com.krystian.chessclock.customMatchPackage.CustomMatchDialogFragment;
import com.krystian.chessclock.timerPackage.TimerActivity;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    RadioButton singleGame, chessMatch;
    CheckBox differentTime;

    TextView playerTime, playerTwoTime;
    TextView playerIncrement, playerTwoIncrement;
    TextView numberOfGames;
    Button playButton;

    SeekBar playerTimeBar, playerTwoTimeBar;
    SeekBar playerIncrementBar, playerTwoIncrementBar;
    SeekBar numberOfGamesBar;

    int customGameNumber; /*if 0 then it's a single game or a match with all games on the same terms;
                                    as opposed to customized match with possibly all games
                                    different with time and increment created by a user*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customGameNumber = getIntent().getIntExtra(ExtraValues.CUSTOM_GAME_NUMBER, 0); //if it's a non-custom, set value as 0;
        setViewComponents();
    }

    public void setViewComponents() {
        singleGame = findViewById(R.id.single_game_radio_button); //radios/checkboxes
        differentTime = findViewById(R.id.different_time_checkbox);
        chessMatch = findViewById(R.id.chess_match_radio_button);
        singleGame.setOnClickListener(this);
        chessMatch.setOnClickListener(this);
        differentTime.setOnClickListener(this);

        playButton = findViewById(R.id.play_button); //buttons
        playButton.setOnClickListener(this);

        playerTime = findViewById(R.id.game_time_text); //textViews
        playerTwoTime = findViewById(R.id.game_time_two_text);
        playerIncrement = findViewById(R.id.increment_text);
        playerTwoIncrement = findViewById(R.id.increment_two_text);
        numberOfGames = findViewById(R.id.games_text);

        playerTimeBar = findViewById(R.id.game_time_seek_bar); //seekBars
        playerTwoTimeBar = findViewById(R.id.game_time_two_seek_bar);
        playerIncrementBar = findViewById(R.id.increment_seek_bar);
        playerTwoIncrementBar = findViewById(R.id.increment_two_seek_bar);
        numberOfGamesBar = findViewById(R.id.games_seek_bar);

        playerTimeBar.setOnSeekBarChangeListener(this);
        playerTwoTimeBar.setOnSeekBarChangeListener(this);
        playerIncrementBar.setOnSeekBarChangeListener(this);
        playerTwoIncrementBar.setOnSeekBarChangeListener(this);
        numberOfGamesBar.setOnSeekBarChangeListener(this);

        playerTimeBar.setProgress(14); //+1 will give 15 minutes as a default time
        playerTwoTimeBar.setProgress(14);
        playerIncrement.setText(String.format(getString(R.string.increment), 0));
        playerTwoIncrement.setText(String.format(getString(R.string.increment_two), 0));
        numberOfGames.setText(String.format(getString(R.string.number_of_games), 1));

        if(customGameNumber != 0) {
            chessMatch.setVisibility(View.INVISIBLE); //customizing single game - match shouldn't be available
            playButton.setText(R.string.save_button);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId(); //switch gives compilation error: "constant expression required"
        if (id == R.id.game_time_seek_bar) {
            playerTime.setText(String.format(getString(R.string.game_time), progress + 1));
        } else if (id == R.id.game_time_two_seek_bar) {
            playerTwoTime.setText(String.format(getString(R.string.game_time_two), progress + 1));
        } else if (id == R.id.increment_seek_bar) {
            playerIncrement.setText(String.format(getString(R.string.increment), progress));
        } else if (id == R.id.increment_two_seek_bar) {
            playerTwoIncrement.setText(String.format(getString(R.string.increment_two), progress));
        } else if (id == R.id.games_seek_bar) {
            numberOfGames.setText(String.format(getString(R.string.number_of_games), progress + 1));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId(); //switch gives compilation error: "constant expression required"
        if (id == R.id.single_game_radio_button) {
            numberOfGames.setVisibility(View.INVISIBLE);
            numberOfGamesBar.setVisibility(View.INVISIBLE);
        } else if (id == R.id.chess_match_radio_button) {
            numberOfGames.setVisibility(View.VISIBLE);
            numberOfGamesBar.setVisibility(View.VISIBLE);
        } else if (id == R.id.different_time_checkbox) {
            if (differentTime.isChecked()) {
                playerTwoTime.setVisibility(View.VISIBLE);
                playerTwoIncrement.setVisibility(View.VISIBLE);
                playerTwoTimeBar.setVisibility(View.VISIBLE);
                playerTwoIncrementBar.setVisibility(View.VISIBLE);
            } else {
                playerTwoTime.setVisibility(View.INVISIBLE);
                playerTwoIncrement.setVisibility(View.INVISIBLE);
                playerTwoTimeBar.setVisibility(View.INVISIBLE);
                playerTwoIncrementBar.setVisibility(View.INVISIBLE);
            }
        } else if (id == R.id.play_button) {
            Intent intent;
            if (customGameNumber == 0) { //play a game - it's not custom game editor mode
                intent = new Intent(this, TimerActivity.class);
                intent.putExtra(ExtraValues.PLAYER_ONE_TIME, playerTimeBar.getProgress() + 1);
                intent.putExtra(ExtraValues.PLAYER_ONE_INCREMENT, playerIncrementBar.getProgress());

                if (differentTime.isChecked()) {
                    intent.putExtra(ExtraValues.PLAYER_TWO_TIME, playerTwoTimeBar.getProgress() + 1);
                    intent.putExtra(ExtraValues.PLAYER_TWO_INCREMENT, playerTwoIncrementBar.getProgress());
                }
                if (!singleGame.isChecked()) //no possibility to uncheck in custom version
                    intent.putExtra(ExtraValues.NUMBER_OF_GAMES, numberOfGamesBar.getProgress() + 1);

                startActivity(intent);
            } else { //edit custom game parameters, save it into database and go back to the list
                String customMatchName = getIntent().getExtras().getString(ExtraValues.CUSTOM_MATCH_NAME);
                int[] gameSettings = new int[4];
                gameSettings[0] = playerTimeBar.getProgress() + 1;
                gameSettings[1] = playerIncrementBar.getProgress();
                if (differentTime.isChecked()) {
                    gameSettings[2] = playerTwoTimeBar.getProgress() + 1;
                    gameSettings[3] = playerTwoIncrementBar.getProgress();
                } else {
                    gameSettings[2] = gameSettings[0];
                    gameSettings[3] = gameSettings[1];
                }
                CustomMatchDatabase customDb = new CustomMatchDatabase();
                customDb.accessDatabase(this);
                customDb.updateCustomGame(customMatchName, customGameNumber, gameSettings);
                customDb.closeDatabase();
                intent = new Intent(this, CustomGameActivityList.class);
                intent.putExtra(ExtraValues.CUSTOM_GAME_NUMBER, customGameNumber); //to show which game is being updated
                intent.putExtra(ExtraValues.CUSTOM_MATCH_NAME, customMatchName); //games from which match to show
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId(); //switch gives compilation error: "constant expression required"
        if (itemId == R.id.add_new_match) {
            CustomMatchDialogFragment custom = new CustomMatchDialogFragment();
            custom.show(getSupportFragmentManager(), "CustomMatchDialog");
            return true;
        } else if (itemId == R.id.choose_custom_match) {
            startActivity(new Intent(this, CustomMatchActivityList.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { }
}
