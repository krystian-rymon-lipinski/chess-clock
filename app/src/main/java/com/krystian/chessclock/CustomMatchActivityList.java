package com.krystian.chessclock;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomMatchActivityList extends ListActivity {

    CustomMatchDatabase customDb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDb = new CustomMatchDatabase();
        customDb.accessDatabase(getApplicationContext());
        getExtras();
        displayCustomMatches(customDb.getDatabase());
    }

    public void getExtras() {
        String name = getIntent().getStringExtra("matchName"); //new custom match was created OR
        int number = getIntent().getIntExtra("numberOfGames", 0); //user wants to play/change ones he already defined

        if(number != 0) //there was an extra, so it is an intent from creating a new custom match
            customDb.createNewCustomMatch(name, number);
    }

    public void displayCustomMatches(SQLiteDatabase db) {

        String query = "SELECT * FROM " + CustomMatchDatabaseHelper.MAIN_TABLE + ";"; //custom matches' names
        cursor = db.rawQuery(query, null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.custom_match_list_item, cursor,
                new String[]{"NAME", "NUMBER_OF_GAMES"},
                new int[]{R.id.custom_match_name, R.id.custom_match_games});
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch(columnIndex) {
                    case 1:
                        String matchName = cursor.getString(columnIndex);
                        TextView customMatchName = (TextView) view;
                        customMatchName.setText(String.format(getString(R.string.custom_match_name), matchName));
                        return true;
                    case 2:
                        int matchGames = cursor.getInt(columnIndex);
                        TextView customMatchGames = (TextView) view;
                        customMatchGames.setText(String.format(getString(R.string.number_of_games), matchGames));
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
        TextView name = v.findViewById(R.id.custom_match_name);
        String matchName = name.getText().toString();

        Intent intent = new Intent(this, CustomGameActivityList.class);
        intent.putExtra("matchName", matchName);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        customDb.closeDatabase();
    }
}
