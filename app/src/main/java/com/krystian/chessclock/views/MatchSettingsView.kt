package com.krystian.chessclock.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.ViewMatchSettingsBinding

class MatchSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private val _binding = ViewMatchSettingsBinding.inflate(LayoutInflater.from(context), this, true)

    private var singleGame: RadioButton? = null
    private var chessMatch: RadioButton? = null
    private var differentTime: CheckBox? = null
    private var playerTime: TextView? = null
    private var playerTwoTime: TextView? = null
    private var playerIncrement: TextView? = null
    private var playerTwoIncrement: TextView? = null
    private var numberOfGames: TextView? = null
    private var playButton: Button? = null
    private var playerTimeBar: SeekBar? = null
    private var playerTwoTimeBar: SeekBar? = null
    private var playerIncrementBar: SeekBar? = null
    private var playerTwoIncrementBar: SeekBar? = null
    private var numberOfGamesBar: SeekBar? = null
    private var customGameNumber = 0 /*if 0 then it's a single game or a match with all games on the same terms;
                                    as opposed to customized match with possibly all games
                                    different with time and increment created by a user*/

    private fun setViewComponents(mainView: View) {
        singleGame = mainView.findViewById(R.id.single_game_radio_button) //radios/checkboxes
        differentTime = mainView.findViewById(R.id.different_time_checkbox)
        chessMatch = mainView.findViewById(R.id.chess_match_radio_button)
        singleGame?.setOnClickListener(this)
        chessMatch?.setOnClickListener(this)
        differentTime?.setOnClickListener(this)
        playButton = mainView.findViewById(R.id.play_button) //buttons
        playButton?.setOnClickListener(this)
        playerTime = mainView.findViewById(R.id.game_time_text) //textViews
        playerTwoTime = mainView.findViewById(R.id.game_time_two_text)
        playerIncrement = mainView.findViewById(R.id.increment_text)
        playerTwoIncrement = mainView.findViewById(R.id.increment_two_text)
        numberOfGames = mainView.findViewById(R.id.games_text)
        playerTimeBar = mainView.findViewById(R.id.game_time_seek_bar) //seekBars
        playerTwoTimeBar = mainView.findViewById(R.id.game_time_two_seek_bar)
        playerIncrementBar = mainView.findViewById(R.id.increment_seek_bar)
        playerTwoIncrementBar = mainView.findViewById(R.id.increment_two_seek_bar)
        numberOfGamesBar = mainView.findViewById(R.id.games_seek_bar)
        playerTimeBar?.setOnSeekBarChangeListener(this)
        playerTwoTimeBar?.setOnSeekBarChangeListener(this)
        playerIncrementBar?.setOnSeekBarChangeListener(this)
        playerTwoIncrementBar?.setOnSeekBarChangeListener(this)
        numberOfGamesBar?.setOnSeekBarChangeListener(this)
        playerTimeBar?.progress = 14 //+1 will give 15 minutes as a default time
        playerTwoTimeBar?.progress = 14
        playerIncrement?.text = String.format(context.getString(R.string.increment), 0)
        playerTwoIncrement?.text = String.format(context.getString(R.string.increment_two), 0)
        numberOfGames?.text = String.format(context.getString(R.string.number_of_games), 1)
        if (customGameNumber != 0) {
            chessMatch?.visibility =
                View.INVISIBLE //customizing single game - match shouldn't be available
            playButton?.setText(R.string.save_button)
        }
    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.game_time_seek_bar -> {
                playerTime!!.text = String.format(context.getString(R.string.game_time), progress + 1)
            }

            R.id.game_time_two_seek_bar -> {
                playerTwoTime!!.text =
                    String.format(context.getString(R.string.game_time_two), progress + 1)
            }

            R.id.increment_seek_bar -> {
                playerIncrement!!.text = String.format(context.getString(R.string.increment), progress)
            }

            R.id.increment_two_seek_bar -> {
                playerTwoIncrement!!.text =
                    String.format(context.getString(R.string.increment_two), progress)
            }

            R.id.games_seek_bar -> {
                numberOfGames!!.text =
                    String.format(context.getString(R.string.number_of_games), progress + 1)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onClick(v: View) {
        val id = v.id //switch gives compilation error: "constant expression required"
        if (id == R.id.single_game_radio_button) {
            numberOfGames!!.visibility = View.INVISIBLE
            numberOfGamesBar!!.visibility = View.INVISIBLE
        } else if (id == R.id.chess_match_radio_button) {
            numberOfGames!!.visibility = View.VISIBLE
            numberOfGamesBar!!.visibility = View.VISIBLE
        } else if (id == R.id.different_time_checkbox) {
            if (differentTime!!.isChecked) {
                playerTwoTime!!.visibility = View.VISIBLE
                playerTwoIncrement!!.visibility = View.VISIBLE
                playerTwoTimeBar!!.visibility = View.VISIBLE
                playerTwoIncrementBar!!.visibility = View.VISIBLE
            } else {
                playerTwoTime!!.visibility = View.INVISIBLE
                playerTwoIncrement!!.visibility = View.INVISIBLE
                playerTwoTimeBar!!.visibility = View.INVISIBLE
                playerTwoIncrementBar!!.visibility = View.INVISIBLE
            }
        } else { }
    }

}