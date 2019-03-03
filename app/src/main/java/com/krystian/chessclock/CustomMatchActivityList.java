package com.krystian.chessclock;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class CustomMatchActivityList extends ListActivity implements View.OnTouchListener {

    CustomMatchDatabase customDb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = getListView();
        listView.setOnTouchListener(this);

        customDb = new CustomMatchDatabase();
        customDb.accessDatabase(this);
        getExtras(); //new custom match - if there is one
        displayCustomMatches(customDb.getDatabase());
        setLongClick();
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
        intent.putExtra("customMatchName", matchName);
        startActivity(intent);
    }

    public void setLongClick() { //to possibly delete custom match
        ListView listView = getListView();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView customMatchText = view.findViewById(R.id.custom_match_name);
                final String customMatchName = customMatchText.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomMatchActivityList.this);
                builder.setMessage(R.string.delete_match)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CustomMatchDatabase customDb = new CustomMatchDatabase();
                                customDb.accessDatabase(getApplicationContext()).
                                        delete("CUSTOM_MATCHES_TABLE", "NAME = ?", new String[]{customMatchName});
                                customDb.closeDatabase();
                                CustomMatchActivityList.this.recreate();
                            }
                        })
                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int minimalSwipe = 250;
        float x_start = 0, x_finish = 0;
        int start_id = 0, finish_id = 0;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x_start = event.getX();
                start_id = v.getId();
                Log.v("Tapped", ""+x_start);
                break;
            case MotionEvent.ACTION_UP:
                x_finish = event.getX();
                finish_id = v.getId();
                Log.v("Untapped", ""+x_finish);
                break;
            default: break;
        }
        float delta = x_finish - x_start;
        TextView customMatchText = v.findViewById(R.id.custom_match_name);
        String customMatchName = customMatchText.getText().toString();
        if(delta > minimalSwipe && start_id == finish_id) {
            Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, TimerActivity.class);
            intent.putExtra("customMatchName", customMatchName);
            startActivity(intent);
            return true;
        }
        else {
            v.performClick();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        customDb.closeDatabase();
    }



}
