package com.krystian.chessclock.customMatchPackage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomMatchDatabaseHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "CUSTOM_MATCH_DATABASE";
    private final static int DB_VERSION = 1;

    public CustomMatchDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CustomMatchDatabase.CUSTOM_MATCHES_TABLE +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CustomMatchDatabase.NAME + " TEXT, " +
                CustomMatchDatabase.NUMBER_OF_GAMES + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(DB_VERSION < 1) {
            onCreate(db);
        }
    }
}
