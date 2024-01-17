package com.krystian.chessclock

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.krystian.chessclock.customMatchPackage.CustomMatchDialogFragment
import com.krystian.chessclock.states.MatchSettingUiState
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(), MenuProvider {

    private val activityViewModel: MainActivityViewModel by viewModels()
    private lateinit var _binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val savedUiSetting = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState?.getParcelable("settingsUiState", MatchSettingUiState::class.java)
        } else savedInstanceState?.getParcelable("settingsUiState") as? MatchSettingUiState

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        _binding.playButton.setOnClickListener(onPlayButtonClicked)
        _binding.settingsView.also {
            it.setInitialState(savedUiSetting ?: MatchSettingUiState())
            it.observeState(viewLifecycleOwner)
        }
    }

    private val onPlayButtonClicked = OnClickListener {
        val bundle = produceArgumentBundleFromViewState()
        findNavController().navigate(R.id.action_settingsFragment_to_timerFragment, bundle)
    }

    private fun produceArgumentBundleFromViewState() : Bundle {
        val viewState = _binding.settingsView.viewState.value

        return bundleOf(
            "firstPlayerTime" to viewState.firstPlayerGameTime,
            "firstPlayerIncrement" to viewState.firstPlayerIncrement,
            "secondPlayerTime" to viewState.secondPlayerGameTime,
            "secondPlayerIncrement" to viewState.secondPlayerIncrement,
            "numberOfGames" to viewState.numberOfGames
        )
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.add_new_match -> {
                val custom = CustomMatchDialogFragment(newMatchCreatedCallback)
                custom.show(childFragmentManager, "CustomMatchDialog")
                true
            }
            R.id.choose_custom_match -> {
                findNavController().navigate(R.id.action_settingsFragment_to_customMatchFragmentList)
                true
            }
            else -> false
        }
    }

    private val newMatchCreatedCallback = object : CustomMatchDialogFragment.Callback {
        override fun onNewMatchCreated(name: String, numberOfGames: Int) {
            activityViewModel.addCustomMatchWithDefaultGames(name, numberOfGames)
            Toast.makeText(activity, R.string.match_created, Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_settingsFragment_to_customMatchFragmentList)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("settingsUiState", _binding.settingsView.viewState.value)
    }

}
