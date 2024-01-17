package com.krystian.chessclock.customMatchPackage

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
import androidx.recyclerview.widget.RecyclerView
import com.krystian.chessclock.MainActivityViewModel
import com.krystian.chessclock.adapters.CustomGameAdapter
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.FragmentCustomGamesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomGamesFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by viewModels()
    private val _binding by lazy { FragmentCustomGamesBinding.inflate(layoutInflater) }

    private var gamesAdapter: CustomGameAdapter? = null
    private var customMatchId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customMatchId = arguments?.getLong("customMatchId")
        setupAdapter()
        observeChanges(customMatchId)
    }

    private fun setupAdapter() {
        gamesAdapter = CustomGameAdapter(callback = gamesAdapterCallback)
        _binding.rvCustomGamesList.apply {
            adapter = gamesAdapter
            layoutManager = LinearLayoutManager(context).apply { orientation = RecyclerView.VERTICAL }
        }
    }

    private fun observeChanges(customMatchId: Long?) {
        customMatchId?.let { matchId ->
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    activityViewModel.getAllGamesForCustomMatchStream(matchId).collect {
                        gamesAdapter?.let { adapter ->
                            adapter.setData(it)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

            }
        }
    }

    private val gamesAdapterCallback = object : CustomGameAdapter.Callback {
        override fun onGameClicked(gameId: Long) {
            val bundle = bundleOf(
                "customMatchId" to customMatchId,
                "customGameId" to gameId
            )
            findNavController().navigate(R.id.action_customGameListFragment_to_gameUpdateFragment, bundle)
        }

    }

}
