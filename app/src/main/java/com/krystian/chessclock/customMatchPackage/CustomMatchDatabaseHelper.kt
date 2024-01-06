package com.krystian.chessclock.customMatchPackage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CustomMatchDatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE " + CustomMatchDatabase.CUSTOM_MATCHES_TABLE +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CustomMatchDatabase.NAME + " TEXT, " +
                    CustomMatchDatabase.NUMBER_OF_GAMES + " INTEGER);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (DB_VERSION < 1) {
            onCreate(db)
        }
    }

    companion object {
        private const val DB_NAME = "CUSTOM_MATCH_DATABASE"
        private const val DB_VERSION = 1
    }
}
