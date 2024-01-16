package com.krystian.chessclock.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.krystian.chessclock.states.MatchSettingUiState
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.ViewMatchSettingsBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val _binding = ViewMatchSettingsBinding.inflate(LayoutInflater.from(context), this, true)
    private val _viewState: MutableStateFlow<MatchSettingUiState> = MutableStateFlow(MatchSettingUiState())
    val viewState = _viewState.asStateFlow()


    override fun onFinishInflate() {
        super.onFinishInflate()
        setupListeners()
    }


    fun setInitialState(state: MatchSettingUiState) {
        setTextViews(state)
        setSeekBarsProgress(state)
        setVisibilities(state)
    }

    fun observeState(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewState.collect { state ->
                    setTextViews(state)
                    setVisibilities(state)
                }
            }
        }
    }

    private fun setupListeners() {
        _binding.apply {
            gameTimeSeekBar.setOnSeekBarChangeListener(firstPlayerGameTimeSeekBarListener)
            incrementSeekBar.setOnSeekBarChangeListener(firstPlayerIncrementSeekBarListener)
            gameTimeTwoSeekBar.setOnSeekBarChangeListener(secondPlayerGameTimeSeekBarListener)
            incrementTwoSeekBar.setOnSeekBarChangeListener(secondPlayerIncrementSeekBarListener)
            numberOfGamesSeekBar.setOnSeekBarChangeListener(numberOfGamesSeekbarListener)

            differentTimeCheckbox.setOnClickListener(onDifferentTimeCheckboxClicked)
            matchSettingsRadioGroup.setOnCheckedChangeListener(onMatchSettingCheckedChangedListener)
        }
    }

    private fun setTextViews(state: MatchSettingUiState) {
        _binding.apply {
            gameTimeText.text = String.format(context.getString(R.string.game_time), state.firstPlayerGameTime)
            incrementText.text = String.format(context.getString(R.string.increment), state.firstPlayerIncrement)
            gameTimeTwoText.text = String.format(context.getString(R.string.game_time_two), state.secondPlayerGameTime)
            incrementTwoText.text = String.format(context.getString(R.string.increment_two), state.secondPlayerIncrement)
            gamesText.text = String.format(context.getString(R.string.number_of_games), state.numberOfGames)
        }
    }

    private fun setSeekBarsProgress(state: MatchSettingUiState) {
        _binding.apply {
            gameTimeSeekBar.progress = state.firstPlayerGameTime - 1
            incrementSeekBar.progress = state.firstPlayerIncrement - 1
            gameTimeTwoSeekBar.progress = state.secondPlayerGameTime - 1
            incrementTwoSeekBar.progress = state.secondPlayerIncrement - 1
            numberOfGamesSeekBar.progress = state.numberOfGames - 1
        }
    }

    private fun setVisibilities(state: MatchSettingUiState) {
        _binding.apply {
            gameTimeTwoText.visibility =
                if (state.isTimeDifferentChecked) View.VISIBLE else View.INVISIBLE
            gameTimeTwoSeekBar.visibility =
                if (state.isTimeDifferentChecked) View.VISIBLE else View.INVISIBLE
            incrementTwoText.visibility =
                if (state.isTimeDifferentChecked) View.VISIBLE else View.INVISIBLE
            incrementTwoSeekBar.visibility =
                if (state.isTimeDifferentChecked) View.VISIBLE else View.INVISIBLE

            gamesText.visibility =
                if (state.isSingleGameChecked) View.INVISIBLE else View.VISIBLE
            numberOfGamesSeekBar.visibility =
                if (state.isSingleGameChecked) View.INVISIBLE else View.VISIBLE

            differentTimeCheckbox.isChecked = state.isTimeDifferentChecked
            chessMatchRadioButton.isEnabled = state.mode == MatchSettingUiState.Mode.MATCH_SETTING
        }
    }


    private val firstPlayerGameTimeSeekBarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _viewState.value = _viewState.value.changeFirstPlayerGameTime(newValue = progress + 1)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val firstPlayerIncrementSeekBarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _viewState.value = _viewState.value.changeFirstPlayerIncrement(newValue = progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val secondPlayerGameTimeSeekBarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _viewState.value = _viewState.value.changeSecondPlayerGameTime(newValue = progress + 1)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val secondPlayerIncrementSeekBarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _viewState.value = _viewState.value.changeSecondPlayerIncrement(newValue = progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val numberOfGamesSeekbarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _viewState.value = _viewState.value.changeNumberOfGames(newValue = progress + 1)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val onDifferentTimeCheckboxClicked = OnClickListener {
        _viewState.value = _viewState.value.toggleIsTimeDifferentChecked()
    }

    private val onMatchSettingCheckedChangedListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        _viewState.value = _viewState.value.changeIsSingleGameChecked(checkedId == R.id.single_game_radio_button)
    }

}