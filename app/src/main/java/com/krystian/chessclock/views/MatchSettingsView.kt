package com.krystian.chessclock.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.ViewMatchSettingsBinding

class MatchSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val _binding = ViewMatchSettingsBinding.inflate(LayoutInflater.from(context), this, true)

    private var customGameNumber = 0 /*if 0 then it's a single game or a match with all games on the same terms;
                                    as opposed to customized match with possibly all games
                                    different with time and increment created by a user*/

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupListeners()
        showInitialValues()
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

    private fun showInitialValues() {
        _binding.apply {
            gameTimeSeekBar.progress = 14
            gameTimeTwoSeekBar.progress = 14

            incrementText.text = String.format(context.getString(R.string.increment), 0)
            incrementTwoText.text = String.format(context.getString(R.string.increment_two), 0)
            gamesText.text = String.format(context.getString(R.string.number_of_games), 1)

            if (customGameNumber != 0) {
                chessMatchRadioButton.visibility = View.INVISIBLE
            }
        }
    }


    //TODO: handle UI changes through state changes

    private val firstPlayerGameTimeSeekBarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _binding.gameTimeText.text = String.format(context.getString(R.string.game_time), progress + 1)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val firstPlayerIncrementSeekBarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _binding.incrementText.text = String.format(context.getString(R.string.increment), progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val secondPlayerGameTimeSeekBarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _binding.gameTimeTwoText.text = String.format(context.getString(R.string.game_time_two), progress + 1)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val secondPlayerIncrementSeekBarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _binding.incrementTwoText.text = String.format(context.getString(R.string.increment_two), progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val numberOfGamesSeekbarListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _binding.gamesText.text = String.format(context.getString(R.string.number_of_games), progress + 1)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val onDifferentTimeCheckboxClicked = OnClickListener {
        _binding.apply {
            gameTimeTwoText.visibility =
                if (differentTimeCheckbox.isChecked) View.VISIBLE else View.INVISIBLE
            gameTimeTwoSeekBar.visibility =
                if (differentTimeCheckbox.isChecked) View.VISIBLE else View.INVISIBLE
            incrementTwoText.visibility =
                if (differentTimeCheckbox.isChecked) View.VISIBLE else View.INVISIBLE
            incrementTwoSeekBar.visibility =
                if (differentTimeCheckbox.isChecked) View.VISIBLE else View.INVISIBLE
        }

    }

    private val onMatchSettingCheckedChangedListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        _binding.apply {
            gamesText.visibility =
                if (checkedId == R.id.single_game_radio_button) View.INVISIBLE else View.VISIBLE
            numberOfGamesSeekBar.visibility =
                if (checkedId == R.id.single_game_radio_button) View.INVISIBLE else View.VISIBLE
        }
    }

}