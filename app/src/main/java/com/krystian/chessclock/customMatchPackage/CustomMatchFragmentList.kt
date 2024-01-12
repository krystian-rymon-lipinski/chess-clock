package com.krystian.chessclock.customMatchPackage

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.ListFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.krystian.chessclock.MainActivityViewModel
import com.krystian.chessclock.model.CustomMatch
import com.krystianrymonlipinski.chessclock.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.floor

@AndroidEntryPoint
class CustomMatchFragmentList : ListFragment(), OnTouchListener {

    private val activityViewModel: MainActivityViewModel by viewModels<MainActivityViewModel>()
    private var customMatches = mutableListOf<CustomMatch>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView.setOnTouchListener(this)
        observeChanges()
        customMatches.addAll(activityViewModel.allMatches.value)
        setupAdapter()
        setLongClick()
    }

    private fun observeChanges() {
        lifecycleScope.launch {
            activityViewModel.allMatches.collect {
                customMatches = it.toMutableList()
                setupAdapter()
            }
        }
    }

    private fun setupAdapter() {
        listAdapter = CustomMatchAdapter()
    }

    private inner class CustomMatchAdapter : ArrayAdapter<CustomMatch>(context!!, R.layout.custom_match_list_item, customMatches) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val viewToReturn = convertView ?:
                LayoutInflater.from(context).inflate(R.layout.custom_match_list_item, null ,false)

            getItem(position)?.let {
                val matchNameTextView = viewToReturn?.findViewById<TextView>(R.id.custom_match_name)
                matchNameTextView?.text = it.name
                val gamesTextView = viewToReturn?.findViewById<TextView>(R.id.custom_match_games)
                gamesTextView?.text = String.format(getString(R.string.number_of_games), it.games.size)
            }
            return viewToReturn
        }
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
}
