package com.krystian.chessclock;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

public class CustomMatchActivityList extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtrasss();
    }

    public void getExtrasss() {
        String name = getIntent().getStringExtra("matchName");
        int number = getIntent().getExtras().getInt("numberOfGames");

        Log.v("Name", name);
        Log.v("Number", ""+number);
    }

}
