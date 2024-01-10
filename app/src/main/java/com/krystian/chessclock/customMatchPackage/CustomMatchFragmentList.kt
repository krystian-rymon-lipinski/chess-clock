package com.krystian.chessclock.customMatchPackage

import android.app.AlertDialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.ListFragment
import androidx.navigation.fragment.findNavController
import com.krystianrymonlipinski.chessclock.R
import kotlin.math.floor

class CustomMatchFragmentList : ListFragment(), OnTouchListener {
    private var customDb: CustomMatchDatabase? = null
    private var cursor: Cursor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listView = listView
        listView.setOnTouchListener(this)
        customDb = CustomMatchDatabase()
        displayCustomMatches(customDb!!.accessDatabase(requireContext())!!)
        setLongClick()
    }

    private fun displayCustomMatches(db: SQLiteDatabase) {
        val query = "SELECT * FROM " + CustomMatchDatabase.CUSTOM_MATCHES_TABLE + ";"
        cursor = db.rawQuery(query, null)
        val adapter = SimpleCursorAdapter(
            requireContext(),
            R.layout.custom_match_list_item,
            cursor,
            arrayOf(CustomMatchDatabase.NAME, CustomMatchDatabase.NUMBER_OF_GAMES),
            intArrayOf(R.id.custom_match_name, R.id.custom_match_games)
        )
        adapter.viewBinder =
            SimpleCursorAdapter.ViewBinder { view: View, cursor: Cursor, columnIndex: Int ->
                when (columnIndex) {
                    1 -> {
                        val matchName = cursor.getString(columnIndex)
                        val customMatchName = view as TextView
                        customMatchName.text =
                            String.format(getString(R.string.custom_match_name), matchName)
                        customMatchName.setTextColor(Color.rgb(30, 30, 30))
                        true
                    }

                    2 -> {
                        val matchGames = cursor.getInt(columnIndex)
                        val customMatchGames = view as TextView
                        customMatchGames.text =
                            String.format(getString(R.string.number_of_games), matchGames)
                        true
                    }
                }
                false
            }
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val name = v.findViewById<TextView>(R.id.custom_match_name)
        val matchName = name.text.toString()

        //TODO: pass id instead (after reworking list items)
        val bundle = bundleOf("customMatchId" to matchName)
        findNavController().navigate(R.id.action_customMatchFragmentList_to_customGameListFragment, bundle)
    }

    private fun setLongClick() { //to delete custom match if there's such need
        val listView = listView
        listView.onItemLongClickListener =
            OnItemLongClickListener { parent: AdapterView<*>?, view: View, position: Int, id: Long ->
                val customMatchText = view.findViewById<TextView>(R.id.custom_match_name)
                val customMatchName = customMatchText.text.toString()
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(R.string.delete_match)
                    .setPositiveButton(R.string.ok_button) { dialog, which ->
                        val customDb = CustomMatchDatabase()
                        customDb.accessDatabase(requireContext())?.delete(
                            CustomMatchDatabase.CUSTOM_MATCHES_TABLE,
                            CustomMatchDatabase.NAME + " = ?", arrayOf(customMatchName)
                        )
                        customDb.closeDatabase()
                        recreate(requireActivity())
                    }
                    .setNegativeButton(R.string.cancel_button) { dialog, which -> }
                builder.show()
                true
            }
    }

    private var xStart = 0f
    private var  itemTapped = 0
    override fun onTouch(
        v: View,
        event: MotionEvent
    ): Boolean { //play a custom match by swiping list item right
        val itemHeight: Float
        val minSwipe = 400 //how long the swipe needs to be, to qualify as such an event
        return if (listView.childCount != 0) { //there must be a list item to play a match
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val yStart: Float
                    itemHeight = listView.getChildAt(0).height.toFloat()
                    val scrollStart: Float = listView.firstVisiblePosition * itemHeight +
                            (itemHeight - listView.getChildAt(0).bottom) //list is scrolled down by n pixels
                    xStart = event.x
                    yStart = event.y + scrollStart
                    itemTapped = floor((yStart / itemHeight).toDouble())
                        .toInt() //index considering the whole list, not only shown items
                    false
                }

                MotionEvent.ACTION_UP -> {
                    val yFinish: Float
                    val itemsScrolledFinish: Int //how many items were scrolled at finish of a gesture
                    itemHeight = listView.getChildAt(0).height.toFloat()
                    val scrollFinish: Float = listView.firstVisiblePosition * itemHeight +
                            (itemHeight - listView.getChildAt(0).bottom) //list is scrolled down by n pixels
                    val xFinish: Float = event.x
                    yFinish = event.y + scrollFinish
                    val itemReleased: Int = floor((yFinish / itemHeight).toDouble())
                        .toInt() //at which item event performed action_up - is this the same from action_up? //index considering the whole list, not only shown items
                    itemsScrolledFinish = floor((scrollFinish / itemHeight).toDouble())
                        .toInt() //how many list items are COMPLETELY scrolled by (not visible)
                    val view =
                        listView.getChildAt(itemReleased - itemsScrolledFinish) //which item is being swiped
                    val customMatchName =
                        view.findViewById<TextView>(R.id.custom_match_name)
                    val name = customMatchName.text.toString()
                    if (itemTapped == itemReleased && xFinish - xStart > minSwipe) {
                        customMatchName.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorAccent
                            )
                        )

                        //TODO: pass id instead (after reworking list items)
                        val bundle = bundleOf("customMatchId" to name)
                        findNavController().navigate(R.id.action_customMatchFragmentList_to_timerFragment, bundle)
                        true //event consumed
                    } else { //user wants to change custom games, not to play a match
                        view.performClick()
                        false //don't consume event, save it for itemClick
                    }
                }

                else -> false
            }
        } else false
    }

    override fun onDestroy() {
        super.onDestroy()
        cursor!!.close()
        customDb!!.closeDatabase()
    }
}
