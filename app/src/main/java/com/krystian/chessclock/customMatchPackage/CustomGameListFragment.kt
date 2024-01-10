package com.krystian.chessclock.customMatchPackage

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import com.krystianrymonlipinski.chessclock.R

class CustomGameListFragment : ListFragment() {
    private var customDb: CustomMatchDatabase? = null
    private var cursor: Cursor? = null
    private var customMatchName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customDb = CustomMatchDatabase()

        //TODO: retrieve passed parameter
        /*
        customMatchName =
            intent.extras!!.getString(ExtraValues.CUSTOM_MATCH_NAME) //activity is launched only after clicking
         */
        displayCustomGames(customDb!!.accessDatabase(requireContext())!!) /*a custom match list item or setting custom game -
                                                            - there will be an extra */
    }

    private fun displayCustomGames(db: SQLiteDatabase) {
        val query = "SELECT * FROM $customMatchName;"
        cursor = db.rawQuery(query, null)

        //TODO: retrieve passed parameter
        val gameChanged = 0
        /*
        val gameChanged =
            intent.getIntExtra(ExtraValues.CUSTOM_GAME_NUMBER, 0) //to color the one being changed
         */
        val adapter = SimpleCursorAdapter(
            requireContext(), R.layout.custom_game_list_item, cursor, arrayOf(
                CustomMatchDatabase.GAME_NUMBER, CustomMatchDatabase.TIME_ONE,
                CustomMatchDatabase.INCREMENT_ONE, CustomMatchDatabase.TIME_TWO,
                CustomMatchDatabase.INCREMENT_TWO
            ), intArrayOf(R.id.custom_game_number, R.id.custom_game)
        )
        adapter.viewBinder =
            SimpleCursorAdapter.ViewBinder { view: View, cursor: Cursor, columnIndex: Int ->
                if (columnIndex == 1) {
                    val gameNumber = cursor.getInt(columnIndex)
                    val customGameNumber = view as TextView
                    customGameNumber.text =
                        String.format(getString(R.string.number_of_custom_game), gameNumber)
                    if (cursor.position + 1 == gameChanged) customGameNumber.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.colorAccent)
                    )
                    true
                } else if (columnIndex == 2) { //set string once
                    val customGame = view as TextView
                    customGame.text = String.format(
                        getString(R.string.custom_game), cursor.getInt(2),
                        cursor.getInt(3), cursor.getInt(4), cursor.getInt(5)
                    )
                    if (cursor.position + 1 == gameChanged) customGame.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorAccent
                        )
                    )
                    true
                }
                false
            }
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        //TODO: pass parameters, navigate to fragment
        /*
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(
            ExtraValues.CUSTOM_MATCH_NAME,
            customMatchName
        ) //to know where (in which match) to save the updates
        intent.putExtra(ExtraValues.CUSTOM_GAME_NUMBER, position + 1)
        startActivity(intent)
         */
    }

    //TODO: navigate between fragments, not activities
    /*
    override fun onBackPressed() {
        startActivity(
            Intent(
                this,
                CustomMatchFragmentList::class.java
            )
        ) //there's no other way to go back
    } //and it would push settings back if not overridden
*/
    override fun onDestroy() {
        super.onDestroy()
        cursor!!.close()
        customDb!!.closeDatabase()
    }
}