package com.krystian.chessclock.customMatchPackage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.widget.Toast
import com.krystianrymonlipinski.chessclock.R

class CustomMatchDatabase {
    private var helper: CustomMatchDatabaseHelper? = null
    private var database: SQLiteDatabase? = null
    fun accessDatabase(context: Context?): SQLiteDatabase? {
        return try {
            helper = CustomMatchDatabaseHelper(context) //customMatchActivity or customGameActivity
            database = helper!!.writableDatabase
            database
        } catch (e: SQLiteException) {
            e.printStackTrace()
            Toast.makeText(context, R.string.database_unreachable, Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun createNewCustomMatch(name: String, numberOfGames: Int) {
        val mainTableValues = ContentValues()
        mainTableValues.put(NAME, name)
        mainTableValues.put(NUMBER_OF_GAMES, numberOfGames)
        database!!.insert(CUSTOM_MATCHES_TABLE, null, mainTableValues)
        database!!.execSQL(
            "CREATE TABLE " + name +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GAME_NUMBER + " INTEGER, " +
                    TIME_ONE + " INTEGER, " +
                    INCREMENT_ONE + " INTEGER, " +
                    TIME_TWO + " INTEGER, " +
                    INCREMENT_TWO + " INTEGER);"
        )
        for (i in 0 until numberOfGames) {
            val time = 15 //default settings; user have to change it himself anyway
            val increment = 0
            val timeTwo = 15
            val incrementTwo = 0
            val contentValues = ContentValues()
            contentValues.put(GAME_NUMBER, i + 1)
            contentValues.put(TIME_ONE, time)
            contentValues.put(INCREMENT_ONE, increment)
            contentValues.put(TIME_TWO, timeTwo)
            contentValues.put(INCREMENT_TWO, incrementTwo)
            database!!.insert(name, null, contentValues)
        }
    }

    fun updateCustomGame(customMatchName: String?, gameNumber: Int, gameSettings: IntArray) {
        val contentValues = ContentValues()
        contentValues.put(TIME_ONE, gameSettings[0])
        contentValues.put(INCREMENT_ONE, gameSettings[1])
        contentValues.put(TIME_TWO, gameSettings[2])
        contentValues.put(INCREMENT_TWO, gameSettings[3])
        database!!.update(
            customMatchName,
            contentValues,
            GAME_NUMBER + " = ?",
            arrayOf(gameNumber.toString())
        )
    }

    fun closeDatabase() {
        database!!.close()
        helper!!.close()
    }

    companion object {
        //the main table containing name and number of games of custom matches
        const val CUSTOM_MATCHES_TABLE = "CUSTOM_MATCHES_TABLE"
        const val NAME = "NAME"
        const val NUMBER_OF_GAMES = "NUMBER_OF_GAMES"

        //every single table containing custom games' settings
        const val GAME_NUMBER = "GAME_NUMBER"
        const val TIME_ONE = "TIME_ONE"
        const val INCREMENT_ONE = "INCREMENT_ONE"
        const val TIME_TWO = "TIME_TWO"
        const val INCREMENT_TWO = "INCREMENT_TWO"
    }
}
