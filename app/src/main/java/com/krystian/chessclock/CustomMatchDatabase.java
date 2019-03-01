package com.krystian.chessclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

public class CustomMatchDatabase {

    private CustomMatchDatabaseHelper helper;
    private SQLiteDatabase database;

    public CustomMatchDatabase() {
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void accessDatabase(Context context) {
        try {
            helper = new CustomMatchDatabaseHelper(context); //customMatchActivity or customGameActivity
            database = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.database_unreachable, Toast.LENGTH_SHORT).show();
        }
    }

    public void createNewCustomMatch(String name, int numberOfGames) {

        ContentValues mainTableValues = new ContentValues();
        mainTableValues.put("NAME", name);
        mainTableValues.put("NUMBER_OF_GAMES", numberOfGames);
        database.insert(CustomMatchDatabaseHelper.MAIN_TABLE, null, mainTableValues);

        database.execSQL("CREATE TABLE " + name +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "GAME_NUMBER INTEGER, " +
                "TIME_ONE INTEGER, " +
                "INCREMENT_ONE INTEGER, " +
                "TIME_TWO INTEGER, " +
                "INCREMENT_TWO INTEGER);");

        for(int i=0; i<numberOfGames; i++) {
            int time = 15; //default settings; user have to change it himself anyway
            int increment = 0;
            int timeTwo = 15;
            int incrementTwo = 0;

            ContentValues contentValues = new ContentValues();
            contentValues.put("GAME_NUMBER", i+1);
            contentValues.put("TIME_ONE", time);
            contentValues.put("INCREMENT_ONE", increment);
            contentValues.put("TIME_TWO", timeTwo);
            contentValues.put("INCREMENT_TWO", incrementTwo);
            database.insert(name, null, contentValues);
        }
    }
/*
    public int[][] decodeCustomGame(Cursor cursor) {
        cursor.moveToFirst(); //only one row from the table
        String[] customMatch = new String[cursor.getInt(1)]; //string-array to put in adapter
        for (int i = 2; i < cursor.getColumnCount(); i++) {
            String allGames = cursor.getString(i); //string from database with hashes
            for(int j=0; j<cursor.getInt(1); j++) { //number of games/rows in the list
                int hashIndex = allGames.indexOf('#');
                String singleGame = allGames.substring(0, hashIndex); //cut integer from string
                allGames = allGames.substring(hashIndex+1); //one game taken, shorten this string
                customGame[j][i-2] = Integer.parseInt(singleGame);
            }
        }
        return customGame;
    }
*/


    public void closeDatabase() {
        database.close();
        helper.close();
    }


}
