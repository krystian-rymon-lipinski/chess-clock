package com.krystian.chessclock.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.krystian.chessclock.MainActivityViewModel
import com.krystian.chessclock.adapters.CustomMatchAdapter
import com.krystian.chessclock.models.CustomMatch
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.FragmentCustomMatchesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomMatchesFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by viewModels<MainActivityViewModel>()
    private val _binding by lazy { FragmentCustomMatchesBinding.inflate(layoutInflater) }

    private var matchesAdapter: CustomMatchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeChanges()
        setupAdapter()

        /*
        _binding.rvCustomMatchesList.setOnTouchListener(onTouchListener)

         */
    }

    private fun observeChanges() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                activityViewModel.allMatches.collect {
                    matchesAdapter?.let { adapter ->
                        adapter.setData(it)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        matchesAdapter = CustomMatchAdapter(callback = adapterCallback)
        _binding.rvCustomMatchesList.apply {
            adapter = matchesAdapter
            layoutManager = LinearLayoutManager(context).apply { orientation = VERTICAL }
        }
    }

    private val adapterCallback: CustomMatchAdapter.Callback = object : CustomMatchAdapter.Callback {
        override fun onMatchClicked(matchId: Long) {
            val bundle = bundleOf("customMatchId" to matchId)
            findNavController().navigate(R.id.action_customMatchFragmentList_to_customGameListFragment, bundle)
        }
        override fun onMatchLongClicked(match: CustomMatch) {
            buildDeleteMatchAlertDialog(match)
        }
    }

    private fun buildDeleteMatchAlertDialog(match: CustomMatch) {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_match)
            .setPositiveButton(R.string.ok_button) { dialog, _ ->
                activityViewModel.deleteCustomMatch(match)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel_button) { dialog, _ -> dialog.dismiss()}
            .show()
    }


    //TODO: rework playing custom matches
    /*
    private val onTouchListener = object : OnTouchListener {
        private var xStart = 0f
        private var  itemTapped = 0

        override fun onTouch(
            v: View,
            event: MotionEvent
        ): Boolean { //play a custom match by swiping list item right
            val listView = _binding.rvCustomMatchesList

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
                        if (itemTapped == itemReleased && xFinish - xStart > minSwipe) {
                            customMatchName.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorAccent
                                )
                            )

                            val bundle = bundleOf("customMatchId" to customMatches[itemReleased].id)
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
*/

}
