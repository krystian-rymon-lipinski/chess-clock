package com.krystian.chessclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        getSettings();
    }

    public void getSettings() {
        int timeOne = getIntent().getIntExtra("playerOneTime", 15);
        int timeTwo = getIntent().getIntExtra("playerTwoTime", timeOne);
        int incrementOne = getIntent().getIntExtra("playerOneIncrement", 0);
        int incrementTwo = getIntent().getIntExtra("playerTwoIncrement", incrementOne);
        int numberOfGames = getIntent().getIntExtra("numberOfGames", 1);

        Log.v("PlayerOneTime", ""+timeOne);
        Log.v("PlayerTwoTime", ""+timeTwo);
        Log.v("PlayerOneIncrement", ""+incrementOne);
        Log.v("PlayerTwoIncrement", ""+incrementTwo);
        Log.v("NumberOfGames", ""+numberOfGames);


    }
}
