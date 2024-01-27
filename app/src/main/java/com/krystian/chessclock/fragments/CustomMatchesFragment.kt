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
import androidx.recyclerview.widget.RecyclerView.GONE
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
    }

    private fun observeChanges() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                activityViewModel.allMatches.collect {
                    matchesAdapter?.let { adapter ->
                        adapter.setData(it)
                        adapter.notifyDataSetChanged()
                        setMainLayout()
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

    private fun setMainLayout() {
        matchesAdapter?.itemCount?.let {
            _binding.rvCustomMatchesList.visibility = if (it > 0 ) View.VISIBLE else GONE
            _binding.tvNoCustomMatches.visibility = if (it > 0) View.GONE else View.VISIBLE
        }
    }

    private val adapterCallback: CustomMatchAdapter.Callback = object : CustomMatchAdapter.Callback {
        override fun onMatchClicked(matchId: Long) {
            val bundle = bundleOf("customMatchId" to matchId)
            findNavController().navigate(R.id.action_customMatchFragmentList_to_timerFragment, bundle)
        }
        override fun onEditMatchClicked(matchId: Long) {
            val bundle = bundleOf("customMatchId" to matchId)
            findNavController().navigate(R.id.action_customMatchFragmentList_to_customGameListFragment, bundle)
        }
        override fun onDeleteMatchClicked(match: CustomMatch) {
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

}
