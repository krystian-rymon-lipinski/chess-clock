package com.krystian.chessclock;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CustomGameActivityList extends ListActivity {

    CustomMatchDatabase customDb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDb = new CustomMatchDatabase();
        customDb.accessDatabase(getApplicationContext());
        String name = getIntent().getExtras().getString("matchName"); //activity is launched only after clicking
        displayCustomGames(customDb.getDatabase(), name); //a custom match list item - there will be an extra

        customDb.closeDatabase();

    }

    public void displayCustomGames(SQLiteDatabase db, String matchName) {
        String query = "SELECT * FROM " + matchName +";";
        cursor = db.rawQuery(query, null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.custom_game_list_item, cursor,
                new String[]{"GAME_NUMBER", "TIME_ONE", "INCREMENT_ONE", "TIME_TWO", "INCREMENT_TWO"},
                new int[]{R.id.custom_game_number, R.id.custom_game});
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(columnIndex == 1) {
                    int gameNumber = cursor.getInt(columnIndex);
                    TextView customGameNumber = (TextView) view;
                    customGameNumber.setText(String.format(getString(R.string.number_of_custom_game), gameNumber));
                    return true;
                }
                else if(columnIndex == 2){ //set string once
                    TextView customGame = (TextView) view;
                    customGame.setText(String.format(getString(R.string.custom_game), cursor.getInt(2),
                            cursor.getInt(3), cursor.getInt(4), cursor.getInt(5)));
                    return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        customDb.closeDatabase();
    }
}
