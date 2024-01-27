package com.krystian.chessclock.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
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
        _viewState.value = state
        setTextViews()
        setSliders()
        setVisibilities()
    }

    fun observeState(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewState.collect { _ ->
                    setTextViews()
                    setVisibilities()
                }
            }
        }
    }

    private fun setupListeners() {
        _binding.apply {
            slGameTimeOne.addOnChangeListener { _, value, _ ->
                _viewState.value = _viewState.value.changeFirstPlayerGameTime(newValue = value.toInt())
            }
            slIncrementOne.addOnChangeListener { _, value, _ ->
                _viewState.value = _viewState.value.changeFirstPlayerIncrement(newValue = value.toInt())
            }
            slGameTimeTwo.addOnChangeListener { _, value, _ ->
                _viewState.value = _viewState.value.changeSecondPlayerGameTime(newValue = value.toInt())
            }
            slIncrementTwo.addOnChangeListener { _, value, _ ->
                _viewState.value = _viewState.value.changeSecondPlayerIncrement(newValue = value.toInt())
            }
            slNumberOfGames.addOnChangeListener { _, value, _ ->
                _viewState.value = _viewState.value.changeNumberOfGames(newValue = value.toInt())
            }

            differentTimeCheckbox.setOnClickListener {
                _viewState.value = _viewState.value.toggleIsTimeDifferentChecked()
            }
            matchSettingsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                _viewState.value = _viewState.value.changeIsSingleGameChecked(checkedId == R.id.single_game_radio_button)
            }
        }
    }

    private fun setTextViews() {
        val state = _viewState.value
        _binding.apply {
            tvGameTimeOne.text = String.format(context.getString(R.string.game_time), state.firstPlayerGameTime)
            tvIncrementOne.text = String.format(context.getString(R.string.increment), state.firstPlayerIncrement)
            tvGameTimeTwo.text = String.format(context.getString(R.string.game_time_two), state.secondPlayerGameTime)
            tvIncrementTwo.text = String.format(context.getString(R.string.increment_two), state.secondPlayerIncrement)
            tvNumberOfGames.text = String.format(context.getString(R.string.number_of_games), state.numberOfGames)
        }
    }

    private fun setSliders() {
        val state = _viewState.value
        _binding.apply {
            slGameTimeOne.value = (state.firstPlayerGameTime).toFloat()
            slIncrementOne.value = (state.firstPlayerIncrement).toFloat()
            slGameTimeTwo.value = (state.secondPlayerGameTime).toFloat()
            slIncrementTwo.value = (state.secondPlayerIncrement).toFloat()
            slNumberOfGames.value = (state.numberOfGames).toFloat()
        }
    }

    private fun setVisibilities() {
        val state = _viewState.value
        _binding.apply {
            tvGameTimeTwo.visibility =
                if (state.isTimeDifferentChecked) View.VISIBLE else View.GONE
            slGameTimeTwo.visibility =
                if (state.isTimeDifferentChecked) View.VISIBLE else View.GONE
            tvIncrementTwo.visibility =
                if (state.isTimeDifferentChecked) View.VISIBLE else View.GONE
            slIncrementTwo.visibility =
                if (state.isTimeDifferentChecked) View.VISIBLE else View.GONE

            tvNumberOfGames.visibility =
                if (state.isSingleGameChecked) View.INVISIBLE else View.VISIBLE
            slNumberOfGames.visibility =
                if (state.isSingleGameChecked) View.INVISIBLE else View.VISIBLE

            differentTimeCheckbox.isChecked = state.isTimeDifferentChecked
            chessMatchRadioButton.isEnabled = state.mode == MatchSettingUiState.Mode.MATCH_SETTING
        }
    }

}