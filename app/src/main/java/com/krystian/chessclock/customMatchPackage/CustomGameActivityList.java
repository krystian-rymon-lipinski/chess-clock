package com.krystian.chessclock.customMatchPackage;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.krystian.chessclock.ExtraValues;
import com.krystian.chessclock.MainActivity;
import com.krystianrymonlipinski.chessclock.R;


public class CustomGameActivityList extends ListActivity {

    CustomMatchDatabase customDb;
    Cursor cursor;

    String customMatchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDb = new CustomMatchDatabase();
        customMatchName = getIntent().getExtras().getString(ExtraValues.CUSTOM_MATCH_NAME); //activity is launched only after clicking
        displayCustomGames(customDb.accessDatabase(this)); /*a custom match list item or setting custom game -
                                                            - there will be an extra */
    }

    public void displayCustomGames(SQLiteDatabase db) {
        String query = "SELECT * FROM " + customMatchName +";";
        cursor = db.rawQuery(query, null);
        final int gameChanged = getIntent().getIntExtra(ExtraValues.CUSTOM_GAME_NUMBER, 0); //to color the one being changed

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.custom_game_list_item, cursor,
                new String[]{CustomMatchDatabase.GAME_NUMBER, CustomMatchDatabase.TIME_ONE,
                        CustomMatchDatabase.INCREMENT_ONE, CustomMatchDatabase.TIME_TWO,
                        CustomMatchDatabase.INCREMENT_TWO},
                new int[]{R.id.custom_game_number, R.id.custom_game});
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(columnIndex == 1) {
                    int gameNumber = cursor.getInt(columnIndex);
                    TextView customGameNumber = (TextView) view;
                    customGameNumber.setText(String.format(getString(R.string.number_of_custom_game), gameNumber));
                    if (cursor.getPosition()+1 == gameChanged)
                        customGameNumber.setTextColor(getResources().getColor(R.color.colorAccent));
                    return true;
                }
                else if(columnIndex == 2){ //set string once
                    TextView customGame = (TextView) view;
                    customGame.setText(String.format(getString(R.string.custom_game), cursor.getInt(2),
                            cursor.getInt(3), cursor.getInt(4), cursor.getInt(5)));
                    if (cursor.getPosition()+1 == gameChanged)
                        customGame.setTextColor(getResources().getColor(R.color.colorAccent));
                    return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ExtraValues.CUSTOM_MATCH_NAME, customMatchName); //to know where (in which match) to save the updates
        intent.putExtra(ExtraValues.CUSTOM_GAME_NUMBER, position+1);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CustomMatchActivityList.class)); //there's no other way to go back
    }                                                                   //and it would push settings back if not overridden

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        customDb.closeDatabase();
    }
}
