package com.krystian.chessclock;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViewComponents();
    }

    public void setViewComponents() {
        singleGame = findViewById(R.id.single_game_radio_button); //radios/checkboxes
        differentTime = findViewById(R.id.different_time_checkbox);
        chessMatch = findViewById(R.id.chess_match_radio_button);
        singleGame.setOnClickListener(this);
        differentTime.setOnClickListener(this);

        playButton = findViewById(R.id.play_button); //buttons
        chessMatch.setOnClickListener(this);
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
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch(seekBar.getId()) {
            case R.id.game_time_seek_bar:
                playerTime.setText(String.format(getString(R.string.game_time), progress+1));
                break;
            case R.id.game_time_two_seek_bar:
                playerTwoTime.setText(String.format(getString(R.string.game_time_two), progress+1));
                break;
            case R.id.increment_seek_bar:
                playerIncrement.setText(String.format(getString(R.string.increment), progress));
                break;
            case R.id.increment_two_seek_bar:
                playerTwoIncrement.setText(String.format(getString(R.string.increment_two), progress));
                break;
            case R.id.games_seek_bar:
                numberOfGames.setText(String.format(getString(R.string.number_of_games), progress + 1));
                break;
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
        switch(v.getId()) {
            case R.id.single_game_radio_button:
                Log.v("Single game checked", "" + singleGame.isChecked());
                numberOfGames.setVisibility(View.INVISIBLE);
                numberOfGamesBar.setVisibility(View.INVISIBLE);
                break;

            case R.id.chess_match_radio_button:
                Log.v("Chess match checked", "" + singleGame.isChecked());
                numberOfGames.setVisibility(View.VISIBLE);
                numberOfGamesBar.setVisibility(View.VISIBLE);
                break;

            case R.id.different_time_checkbox:
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
                break;

            case R.id.play_button:
                Intent intent = new Intent(this, TimerActivity.class);
                intent.putExtra("playerOneTime", playerTimeBar.getProgress()+1);
                intent.putExtra("playerOneIncrement", playerIncrementBar.getProgress());

                if (differentTime.isChecked()) {
                    intent.putExtra("playerTwoTime", playerTwoTimeBar.getProgress()+1);
                    intent.putExtra("playerTwoIncrement", playerTwoIncrementBar.getProgress());
                }
                if (!singleGame.isChecked())
                    intent.putExtra("numberOfGames", numberOfGamesBar.getProgress() + 1);

                startActivity(intent);

                break;
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
        switch(item.getItemId()) {
            case R.id.add_new_match:
                CustomMatchDialogFragment custom = new CustomMatchDialogFragment();
                custom.show(getSupportFragmentManager(), "CustomMatchDialog");
                return true;
            case R.id.choose_custom_match:
                startActivity(new Intent(this, CustomMatchActivityList.class));
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }


}
