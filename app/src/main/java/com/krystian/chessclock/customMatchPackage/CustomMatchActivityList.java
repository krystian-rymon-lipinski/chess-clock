package com.krystian.chessclock.customMatchPackage;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.krystian.chessclock.ExtraValues;
import com.krystian.chessclock.MainActivity;
import com.krystian.chessclock.timerPackage.TimerActivity;
import com.krystianrymonlipinski.chessclock.R;

public class CustomMatchActivityList extends ListActivity implements  View.OnTouchListener {

    CustomMatchDatabase customDb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = getListView();
        listView.setOnTouchListener(this);
        customDb = new CustomMatchDatabase();
        displayCustomMatches(customDb.accessDatabase(this));
        setLongClick();
    }


    public void displayCustomMatches(SQLiteDatabase db) {

        String query = "SELECT * FROM " + CustomMatchDatabase.CUSTOM_MATCHES_TABLE + ";";
        cursor = db.rawQuery(query, null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.custom_match_list_item, cursor,
                new String[]{CustomMatchDatabase.NAME, CustomMatchDatabase.NUMBER_OF_GAMES},
                new int[]{R.id.custom_match_name, R.id.custom_match_games});
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) { //put cursor data into views prepared in custom_match layout
                switch(columnIndex) {
                    case 1:
                        String matchName = cursor.getString(columnIndex);
                        TextView customMatchName = (TextView) view;
                        customMatchName.setText(String.format(getString(R.string.custom_match_name), matchName));
                        customMatchName.setTextColor(Color.rgb(30, 30, 30));
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
        intent.putExtra(ExtraValues.CUSTOM_MATCH_NAME, matchName);
        startActivity(intent);
    }

    public void setLongClick() { //to delete custom match if there's such need
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
                                        delete(CustomMatchDatabase.CUSTOM_MATCHES_TABLE,
                                                CustomMatchDatabase.NAME + " = ?", new String[]{customMatchName});
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


    float xStart;
    int itemTapped;

    @Override
    public boolean onTouch(View v, MotionEvent event) { //play a custom match by swiping list item right

        float itemHeight;
        int minSwipe = 400; //how long the swipe needs to be, to qualify as such an event
        if(getListView().getChildCount() != 0) { //there must be a list item to play a match
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float scrollStart, yStart;
                    itemHeight = getListView().getChildAt(0).getHeight();
                    scrollStart = getListView().getFirstVisiblePosition() * itemHeight +
                            (itemHeight - getListView().getChildAt(0).getBottom()); //list is scrolled down by n pixels
                    xStart = event.getX();
                    yStart = event.getY() + scrollStart;
                    itemTapped = (int) Math.floor(yStart / itemHeight); //index considering the whole list, not only shown items
                    return false;
                case MotionEvent.ACTION_UP:
                    float scrollFinish, xFinish, yFinish;
                    int itemReleased; //at which item event performed action_up - is this the same from action_up?
                    int itemsScrolledFinish; //how many items were scrolled at finish of a gesture

                    itemHeight = getListView().getChildAt(0).getHeight();
                    scrollFinish = getListView().getFirstVisiblePosition() * itemHeight +
                            (itemHeight - getListView().getChildAt(0).getBottom()); //list is scrolled down by n pixels
                    xFinish = event.getX();
                    yFinish = event.getY() + scrollFinish;

                    itemReleased = (int) Math.floor(yFinish / itemHeight); //index considering the whole list, not only shown items
                    itemsScrolledFinish = (int) Math.floor(scrollFinish / itemHeight); //how many list items are COMPLETELY scrolled by (not visible)

                    View view = getListView().getChildAt(itemReleased - itemsScrolledFinish); //which item is being swiped
                    TextView customMatchName = view.findViewById(R.id.custom_match_name);
                    String name = customMatchName.getText().toString();

                    if(itemTapped == itemReleased && xFinish - xStart > minSwipe) {
                        customMatchName.setTextColor(getResources().getColor(R.color.colorAccent));
                        Intent intent = new Intent(this, TimerActivity.class);
                        intent.putExtra(ExtraValues.CUSTOM_MATCH_NAME, name);
                        startActivity(intent);
                        return true; //event consumed
                    }
                    else { //user wants to change custom games, not to play a match
                        view.performClick();
                        return false; //don't consume event, save it for itemClick
                    }
                default: return false;
            }
        }
        return false;
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
