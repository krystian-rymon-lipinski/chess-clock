package com.krystian.chessclock.customMatchPackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.ListFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.krystian.chessclock.MainActivityViewModel
import com.krystian.chessclock.model.CustomGame
import com.krystianrymonlipinski.chessclock.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomGameListFragment : ListFragment() {
    private var customMatchName: String? = null

    private val activityViewModel: MainActivityViewModel by viewModels()
    private var games: List<CustomGame> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customMatchId = arguments?.getLong("customMatchId")
        observeChanges(customMatchId)

        setupAdapter()
    }

    private fun setupAdapter() {
        listAdapter = CustomGameAdapter()
    }

    private fun observeChanges(customMatchId: Long?) {
        customMatchId?.let { matchId ->
            viewLifecycleOwner.lifecycleScope.launch {
                activityViewModel.getAllGamesForCustomMatchStream(matchId).collect {
                    games = it
                    setupAdapter()
                }
            }
        }
    }

    private inner class CustomGameAdapter : ArrayAdapter<CustomGame>(context!!, R.layout.custom_game_list_item, games) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val viewToReturn = convertView ?:
                LayoutInflater.from(context).inflate(R.layout.custom_game_list_item, null, false)

            getItem(position)?.let {
                val gameNumberTextView = viewToReturn.findViewById<TextView>(R.id.custom_game_number)
                gameNumberTextView.text = StringBuilder().append(position + 1).toString()
                val gameDescriptionTextView = viewToReturn.findViewById<TextView>(R.id.custom_game)
                gameDescriptionTextView.text = String.format(
                    getString(R.string.custom_game),
                    it.whiteTime,
                    it.whiteIncrement,
                    it.blackTime,
                    it.blackIncrement
                )
            }

            return viewToReturn
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        //TODO: rework parameters for proper IDs
        val bundle = bundleOf(
            "customMatchId" to customMatchName,
            "customGameId" to position
        )
        findNavController().navigate(R.id.action_customGameListFragment_to_settingsFragment, bundle)
    }
}
