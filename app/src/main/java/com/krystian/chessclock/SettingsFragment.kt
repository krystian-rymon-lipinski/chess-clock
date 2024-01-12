package com.krystian.chessclock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.krystian.chessclock.customMatchPackage.CustomMatchDialogFragment
import com.krystian.chessclock.model.CustomGame
import com.krystianrymonlipinski.chessclock.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(), OnSeekBarChangeListener, View.OnClickListener, MenuProvider {
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

    private var customGameId: Long? = null
    private var customMatchId: Long? = null

    private val activityViewModel: MainActivityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForPassedData()
        setViewComponents(view)
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun checkForPassedData() {
        if (findNavController().previousBackStackEntry != null) { // not a start destination
            customGameId = arguments?.getLong("customGameId")
            customMatchId = arguments?.getLong("customMatchId")
        }
    }

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
            if (customGameId == null) { //play a game - it's not custom game editor mode
                val firstPlayerSettings = listOf(
                    ExtraValues.PLAYER_ONE_TIME to playerTimeBar!!.progress + 1,
                    ExtraValues.PLAYER_ONE_INCREMENT to playerIncrementBar!!.progress
                )
                val secondPlayerSettings = if (differentTime!!.isChecked) {
                    listOf(
                        ExtraValues.PLAYER_TWO_TIME to playerTwoTimeBar!!.progress + 1,
                        ExtraValues.PLAYER_TWO_INCREMENT to playerTwoIncrementBar!!.progress
                    )
                } else firstPlayerSettings
                val numberOfGamesSettings = if (!singleGame!!.isChecked) {
                    ExtraValues.NUMBER_OF_GAMES to numberOfGamesBar!!.progress + 1
                } else ExtraValues.NUMBER_OF_GAMES to 1

                val bundle = bundleOf(firstPlayerSettings[0], firstPlayerSettings[1],
                    secondPlayerSettings[0], secondPlayerSettings[1], numberOfGamesSettings

                )

                findNavController().navigate(R.id.action_settingsFragment_to_timerFragment, bundle)
            } else { // save custom game parameters to the database

                val customGameUpdated = CustomGame(
                    id = customGameId ?: -1,
                    whiteTime = playerTimeBar!!.progress + 1,
                    whiteIncrement = playerIncrementBar!!.progress,
                    blackTime = if (differentTime!!.isChecked) playerTwoTimeBar!!.progress + 1 else playerTimeBar!!.progress + 1,
                    blackIncrement = if (differentTime!!.isChecked) playerTwoIncrementBar!!.progress else playerIncrementBar!!.progress,
                    matchId = customMatchId ?: -1
                )
                //TODO: save change to database


                findNavController().popBackStack()
            }
        }
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

}
