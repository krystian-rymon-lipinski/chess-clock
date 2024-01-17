package com.krystian.chessclock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.krystian.chessclock.model.CustomGame
import com.krystian.chessclock.states.MatchSettingUiState
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameUpdateFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by viewModels()
    private lateinit var _binding: FragmentSettingsBinding

    private var customGameId: Long = GAME_ID_ARGUMENT_NOT_FOUND
    private var customMatchId: Long = MATCH_ID_ARGUMENT_NOT_FOUND

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customGameId = arguments?.getLong("customGameId") ?: GAME_ID_ARGUMENT_NOT_FOUND
        customMatchId = arguments?.getLong("customMatchId") ?: MATCH_ID_ARGUMENT_NOT_FOUND
        setupUi()
        collectGameData()
    }

    private fun setupUi() {
        _binding.playButton.apply {
            text = getString(R.string.save_button)
            setOnClickListener(onSaveButton)
        }
    }

    private fun collectGameData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.getGameById(customGameId).collect {
                    setupViewState(it)
                }
            }
        }
    }

    private fun setupViewState(game: CustomGame) {
        _binding.settingsView.also {
            it.setInitialState(MatchSettingUiState(
                mode = MatchSettingUiState.Mode.GAME_UPDATE,
                firstPlayerGameTime = game.whiteTime,
                firstPlayerIncrement = game.whiteIncrement,
                secondPlayerGameTime = game.blackTime,
                secondPlayerIncrement = game.blackIncrement,
                numberOfGames = 1,
                isSingleGameChecked = true,
                isTimeDifferentChecked = game.areTimeSettingsDifferent()

            ))
            it.observeState(viewLifecycleOwner)
        }
    }

    private val onSaveButton = OnClickListener {
        produceDatabaseObjectFromViewState()?.let {
            activityViewModel.updateCustomGame(it)
        }
        //TODO: handle failure with at least a toast
        findNavController().popBackStack()
    }

    private fun produceDatabaseObjectFromViewState() : CustomGame? {
        val viewState = _binding.settingsView.viewState.value

        return if (customGameId == GAME_ID_ARGUMENT_NOT_FOUND) null
        else CustomGame(
            id = customGameId,
            whiteTime = viewState.firstPlayerGameTime,
            whiteIncrement = viewState.firstPlayerIncrement,
            blackTime = viewState.secondPlayerGameTime,
            blackIncrement = viewState.secondPlayerIncrement,
            matchId = customMatchId
        )
    }


    companion object {
        private const val GAME_ID_ARGUMENT_NOT_FOUND = -1L
        private const val MATCH_ID_ARGUMENT_NOT_FOUND = -1L
    }
}