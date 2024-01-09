package com.krystian.chessclock

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.krystian.chessclock.customMatchPackage.CustomGameActivityList
import com.krystian.chessclock.customMatchPackage.CustomMatchFragmentList
import com.krystian.chessclock.customMatchPackage.CustomMatchDatabase
import com.krystian.chessclock.customMatchPackage.CustomMatchDialogFragment
import com.krystian.chessclock.timerPackage.TimerActivity
import com.krystianrymonlipinski.chessclock.R

class MainActivity : AppCompatActivity(), OnSeekBarChangeListener, View.OnClickListener {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customGameNumber = intent.getIntExtra(
            ExtraValues.CUSTOM_GAME_NUMBER,
            0
        ) //if it's a non-custom, set value as 0;
        setViewComponents()
    }

    private fun setViewComponents() {
        singleGame = findViewById(R.id.single_game_radio_button) //radios/checkboxes
        differentTime = findViewById(R.id.different_time_checkbox)
        chessMatch = findViewById(R.id.chess_match_radio_button)
        singleGame?.setOnClickListener(this)
        chessMatch?.setOnClickListener(this)
        differentTime?.setOnClickListener(this)
        playButton = findViewById(R.id.play_button) //buttons
        playButton?.setOnClickListener(this)
        playerTime = findViewById(R.id.game_time_text) //textViews
        playerTwoTime = findViewById(R.id.game_time_two_text)
        playerIncrement = findViewById(R.id.increment_text)
        playerTwoIncrement = findViewById(R.id.increment_two_text)
        numberOfGames = findViewById(R.id.games_text)
        playerTimeBar = findViewById(R.id.game_time_seek_bar) //seekBars
        playerTwoTimeBar = findViewById(R.id.game_time_two_seek_bar)
        playerIncrementBar = findViewById(R.id.increment_seek_bar)
        playerTwoIncrementBar = findViewById(R.id.increment_two_seek_bar)
        numberOfGamesBar = findViewById(R.id.games_seek_bar)
        playerTimeBar?.setOnSeekBarChangeListener(this)
        playerTwoTimeBar?.setOnSeekBarChangeListener(this)
        playerIncrementBar?.setOnSeekBarChangeListener(this)
        playerTwoIncrementBar?.setOnSeekBarChangeListener(this)
        numberOfGamesBar?.setOnSeekBarChangeListener(this)
        playerTimeBar?.progress = 14 //+1 will give 15 minutes as a default time
        playerTwoTimeBar?.progress = 14
        playerIncrement?.text = String.format(getString(R.string.increment), 0)
        playerTwoIncrement?.text = String.format(getString(R.string.increment_two), 0)
        numberOfGames?.text = String.format(getString(R.string.number_of_games), 1)
        if (customGameNumber != 0) {
            chessMatch?.visibility = View.INVISIBLE //customizing single game - match shouldn't be available
            playButton?.setText(R.string.save_button)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.game_time_seek_bar -> {
                playerTime!!.text = String.format(getString(R.string.game_time), progress + 1)
            }
            R.id.game_time_two_seek_bar -> {
                playerTwoTime!!.text = String.format(getString(R.string.game_time_two), progress + 1)
            }
            R.id.increment_seek_bar -> {
                playerIncrement!!.text = String.format(getString(R.string.increment), progress)
            }
            R.id.increment_two_seek_bar -> {
                playerTwoIncrement!!.text = String.format(getString(R.string.increment_two), progress)
            }
            R.id.games_seek_bar -> {
                numberOfGames!!.text = String.format(getString(R.string.number_of_games), progress + 1)
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
        } else if (id == R.id.play_button) {
            val intent: Intent
            if (customGameNumber == 0) { //play a game - it's not custom game editor mode
                intent = Intent(this, TimerActivity::class.java)
                intent.putExtra(ExtraValues.PLAYER_ONE_TIME, playerTimeBar!!.progress + 1)
                intent.putExtra(ExtraValues.PLAYER_ONE_INCREMENT, playerIncrementBar!!.progress)
                if (differentTime!!.isChecked) {
                    intent.putExtra(ExtraValues.PLAYER_TWO_TIME, playerTwoTimeBar!!.progress + 1)
                    intent.putExtra(
                        ExtraValues.PLAYER_TWO_INCREMENT,
                        playerTwoIncrementBar!!.progress
                    )
                }
                if (!singleGame!!.isChecked) //no possibility to uncheck in custom version
                    intent.putExtra(ExtraValues.NUMBER_OF_GAMES, numberOfGamesBar!!.progress + 1)
                startActivity(intent)
            } else { //edit custom game parameters, save it into database and go back to the list
                val customMatchName = getIntent().extras!!.getString(ExtraValues.CUSTOM_MATCH_NAME)
                val gameSettings = IntArray(4)
                gameSettings[0] = playerTimeBar!!.progress + 1
                gameSettings[1] = playerIncrementBar!!.progress
                if (differentTime!!.isChecked) {
                    gameSettings[2] = playerTwoTimeBar!!.progress + 1
                    gameSettings[3] = playerTwoIncrementBar!!.progress
                } else {
                    gameSettings[2] = gameSettings[0]
                    gameSettings[3] = gameSettings[1]
                }
                val customDb = CustomMatchDatabase()
                customDb.accessDatabase(this)
                customDb.updateCustomGame(customMatchName, customGameNumber, gameSettings)
                customDb.closeDatabase()
                intent = Intent(this, CustomGameActivityList::class.java)
                intent.putExtra(
                    ExtraValues.CUSTOM_GAME_NUMBER,
                    customGameNumber
                ) //to show which game is being updated
                intent.putExtra(
                    ExtraValues.CUSTOM_MATCH_NAME,
                    customMatchName
                ) //games from which match to show
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId //switch gives compilation error: "constant expression required"
        if (itemId == R.id.add_new_match) {
            val custom = CustomMatchDialogFragment()
            custom.show(supportFragmentManager, "CustomMatchDialog")
            return true
        } else if (itemId == R.id.choose_custom_match) {

            //TODO: implement fragment navigation
            /*
            startActivity(Intent(this, CustomMatchFragmentList::class.java))
             */
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {}
}
