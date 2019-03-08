package com.krystian.chessclock.customMatchPackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.krystian.chessclock.R;

public class CustomMatchDatabase {

    //the main table containing name and number of games of custom matches
    public static final String CUSTOM_MATCHES_TABLE = "CUSTOM_MATCHES_TABLE";
    public static final String NAME = "NAME";
    public static final String NUMBER_OF_GAMES = "NUMBER_OF_GAMES";

    //every single table containing custom games' settings
    public static final String GAME_NUMBER = "GAME_NUMBER";
    public static final String TIME_ONE = "TIME_ONE";
    public static final String INCREMENT_ONE = "INCREMENT_ONE";
    public static final String TIME_TWO = "TIME_TWO";
    public static final String INCREMENT_TWO = "INCREMENT_TWO";

    private CustomMatchDatabaseHelper helper;
    private SQLiteDatabase database;

    public CustomMatchDatabase() {
    }

    public SQLiteDatabase accessDatabase(Context context) {
        try {
            helper = new CustomMatchDatabaseHelper(context); //customMatchActivity or customGameActivity
            database = helper.getWritableDatabase();
            return database;
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.database_unreachable, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void createNewCustomMatch(String name, int numberOfGames) {

        ContentValues mainTableValues = new ContentValues();
        mainTableValues.put(NAME, name);
        mainTableValues.put(NUMBER_OF_GAMES, numberOfGames);
        database.insert(CUSTOM_MATCHES_TABLE, null, mainTableValues);

        database.execSQL("CREATE TABLE " + name +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GAME_NUMBER + " INTEGER, " +
                TIME_ONE + " INTEGER, " +
                INCREMENT_ONE + " INTEGER, " +
                TIME_TWO + " INTEGER, " +
                INCREMENT_TWO + " INTEGER);");

        for(int i=0; i<numberOfGames; i++) {
            int time = 15; //default settings; user have to change it himself anyway
            int increment = 0;
            int timeTwo = 15;
            int incrementTwo = 0;

            ContentValues contentValues = new ContentValues();
            contentValues.put(GAME_NUMBER, i+1);
            contentValues.put(TIME_ONE, time);
            contentValues.put(INCREMENT_ONE, increment);
            contentValues.put(TIME_TWO, timeTwo);
            contentValues.put(INCREMENT_TWO, incrementTwo);
            database.insert(name, null, contentValues);
        }
    }

    public void updateCustomGame(String customMatchName, int gameNumber, int[] gameSettings) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIME_ONE, gameSettings[0]);
        contentValues.put(INCREMENT_ONE, gameSettings[1]);
        contentValues.put(TIME_TWO, gameSettings[2]);
        contentValues.put(INCREMENT_TWO, gameSettings[3]);
        database.update(customMatchName, contentValues, GAME_NUMBER + " = ?", new String[]{String.valueOf(gameNumber)});
    }

    public void closeDatabase() {
        database.close();
        helper.close();
    }


}
