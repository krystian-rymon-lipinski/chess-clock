package com.krystian.chessclock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomMatchDatabaseHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "CUSTOM_MATCH_DATABASE";
    private final static int DB_VERSION = 1;

    public static final String MAIN_TABLE = "CUSTOM_MATCHES_TABLE"; //contains names of custom matches stored in other tables


    public CustomMatchDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MAIN_TABLE +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, " +
                "NUMBER_OF_GAMES INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(DB_VERSION < 1) {
            onCreate(db);
        }
    }
}
