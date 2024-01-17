package com.krystian.chessclock.timerPackage

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.krystian.chessclock.MainActivityViewModel
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerFragment : Fragment(), View.OnClickListener {

    private var match: Match? = null

    private val activityViewModel: MainActivityViewModel by viewModels()
    private lateinit var _binding: FragmentTimerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
        setupMatch()

    }

    private fun setClickListeners() {
        _binding.apply {
            drawButton.setOnClickListener(this@TimerFragment)
            drawButtonRotated.setOnClickListener(this@TimerFragment)
            moveButton.setOnClickListener(this@TimerFragment)
            moveButtonRotated.setOnClickListener(this@TimerFragment)
            resignButton.setOnClickListener(this@TimerFragment)
            resignButtonRotated.setOnClickListener(this@TimerFragment)
            newGameButton.setOnClickListener(this@TimerFragment)
        }
    }

    private fun setupMatch() {
        val matchId = arguments?.getLong("customMatchId")

        var numberOfGames = 0
        val timeOnes = ArrayList<Int?>()
        val incrementOnes = ArrayList<Int?>()
        val timeTwos = ArrayList<Int?>()
        val incrementTwos = ArrayList<Int?>()
        if (matchId == NO_ARGUMENT_MATCH_ID_FOUND) {

            numberOfGames = arguments?.getInt("numberOfGames") ?: 1
            val timeOne = arguments?.getInt("firstPlayerTime") ?: 15
            val timeTwo = arguments?.getInt("secondPlayerTime") ?: 15
            val incrementOne = arguments?.getInt("firstPlayerIncrement") ?: 3
            val incrementTwo = arguments?.getInt("secondPlayerIncrement") ?: 3
            for (i in 0 until numberOfGames) {
                timeOnes.add(timeOne)
                incrementOnes.add(incrementOne)
                timeTwos.add(timeTwo)
                incrementTwos.add(incrementTwo)
            }

            match = Match(numberOfGames, timeOnes, incrementOnes, timeTwos, incrementTwos)
            setMatchGame() //set n-th game with its parameters

        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                activityViewModel.getMatchByIdWithGames(matchId!!).take(1).collect {
                    for (game in it.games) {
                        timeOnes.add(game.whiteTime)
                        incrementOnes.add(game.whiteIncrement)
                        timeTwos.add(game.blackTime)
                        incrementTwos.add(game.blackIncrement)
                    }

                    match = Match(numberOfGames, timeOnes, incrementOnes, timeTwos, incrementTwos)
                    setMatchGame() //set n-th game with its parameters
                }
            }
        }

    }

    private fun setMatchGame() {
        val game = match!!.gameNumber
        match!!.currentGame = Game(
            match!!.oneTimes[game - 1]!! * 60, match!!.oneIncrements[game - 1]!!,
            match!!.twoTimes[game - 1]!! * 60, match!!.twoIncrements[game - 1]!!, game
        ) //create a game
        setViewStartValues() //textViews values, which button to enable, etc.
        play()
    }

    private fun setViewStartValues() {
        setTextViews() //timer values, points, number of move
        setButtons() //draw, resign and move buttons
        if (match!!.numberOfGames > 1) setButtonColors() //switching sides after every game in a match
    }

    private fun play() {
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                calculateTime()
                checkForResult() //maybe someone won
                if (match!!.currentGame!!.gameState !== GameState.RUNNING) finishGame() else handler.postDelayed(
                    this,
                    1000
                )
            }
        })
    }

    override fun onClick(v: View) {
        val id = v.id //switch gives compilation error: "constant expression required"
        if (id == R.id.move_button) {
            makeWhiteMove()
        } else if (id == R.id.move_button_rotated) {
            makeBlackMove()
        } else if (id == R.id.draw_button) {
            setButton(
                _binding.drawButton,
                false
            ) //a draw can be offered only once a move and during your own
        } else if (id == R.id.draw_button_rotated) {
            setButton(_binding.drawButtonRotated, false)
        } else if (id == R.id.resign_button) {
            match!!.currentGame!!.gameState = GameState.LOST
        } else if (id == R.id.resign_button_rotated) {
            match!!.currentGame!!.gameState = GameState.WON
        } else if (id == R.id.new_game_button) {
            if (match!!.numberOfGames == 1) { //replay a single game
                match =
                    Match(match!!) //same settings of a single game with GameState = RUNNING -> handler starts counting again
            } else match!!.gameNumber = match!!.gameNumber + 1
            setMatchGame() //the next one
        }
    }

    /*
    @Override
    public void onPause() {
        super.onPause();
        if(currentGame.getGameState() == GameState.RUNNING)
            currentGame.setGameState(GameState.PAUSED);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentGame.getGameState() == GameState.PAUSED) {
            currentGame.setGameState(GameState.RUNNING);
            setButtons();
            play();
        }
    }
*/
    private fun setTextViews() {
        _binding.apply {
            timerText.text = String.format(
                getString(R.string.time), match!!.currentGame!!.firstTimer / 3600,
                match!!.currentGame!!.firstTimer / 60 % 60, match!!.currentGame!!.firstTimer % 60
            )
            timerTextRotated.text = String.format(
                getString(R.string.time), match!!.currentGame!!.secondTimer / 3600,
                match!!.currentGame!!.secondTimer / 60 % 60, match!!.currentGame!!.secondTimer % 60
            )
            firstMoveText.text = String.format(getString(R.string.first_move_text), 30)
            firstMoveTextRotated.text = String.format(getString(R.string.first_move_text), 30)
            pointsText.text = String.format(
                getString(R.string.points), match!!.firstPlayerPoints,
                match!!.secondPlayerPoints
            )
            pointsTextRotated.text = String.format(
                getString(R.string.points), match!!.secondPlayerPoints,
                match!!.firstPlayerPoints
            )
            gameNumberText.text = String.format(
                getString(R.string.game_number), match!!.gameNumber,
                match!!.numberOfGames
            )
            gameNumberTextRotated.text = String.format(
                getString(R.string.game_number), match!!.gameNumber,
                match!!.numberOfGames
            )
            moveButton.text =
                String.format(getString(R.string.move_number), match!!.currentGame!!.moveNumber)
            moveButtonRotated.text = String.format(
                getString(R.string.move_number),
                match!!.currentGame!!.moveNumberRotated
            )
            firstMoveText.visibility = View.VISIBLE
            firstMoveTextRotated.visibility = View.VISIBLE
        }

    }

    private fun setButtons() {
        _binding.apply {
            newGameButton.visibility = View.INVISIBLE
            drawButton.isChecked = false
            drawButtonRotated.isChecked = false
            setButton(resignButton, true)
            setButton(resignButtonRotated, true)
            if (match!!.currentGame?.isFirstPlayerMove == true) {
                setButton(moveButton, true)
                setButton(drawButton, true)
                setButton(moveButtonRotated, false)
                setButton(drawButtonRotated, false)
            } else {
                setButton(moveButton, false)
                setButton(drawButton, false)
                setButton(moveButtonRotated, true)
                setButton(drawButtonRotated, true)
            }
        }
    }

    private fun setButton(button: Button?, isEnabled: Boolean) {
        if (isEnabled) {
            button!!.isClickable = true
            button.alpha = 1f
        } else {
            button!!.isClickable = false
            button.alpha = 0.5f
        }
    }

    private fun setButtonColors() {
        val context = requireContext()

        _binding.apply {
            if (match!!.gameNumber % 2 != 0) {
                moveButton.background = ContextCompat.getDrawable(context, R.drawable.timer_white_button)
                moveButton.background = ContextCompat.getDrawable(context, R.drawable.timer_white_button)
                drawButton.background = ContextCompat.getDrawable(context, R.drawable.timer_white_button)
                resignButton.background =
                    ContextCompat.getDrawable(context, R.drawable.timer_white_button)
                moveButton.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
                drawButton.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
                resignButton.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
                moveButtonRotated.background =
                    ContextCompat.getDrawable(context, R.drawable.timer_black_button)
                drawButtonRotated.background =
                    ContextCompat.getDrawable(context, R.drawable.timer_black_button)
                resignButtonRotated.background =
                    ContextCompat.getDrawable(context, R.drawable.timer_black_button)
                moveButtonRotated.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                drawButtonRotated.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                resignButtonRotated.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
            } else {
                moveButtonRotated.background =
                    ContextCompat.getDrawable(context, R.drawable.timer_white_button)
                drawButtonRotated.background =
                    ContextCompat.getDrawable(context, R.drawable.timer_white_button)
                resignButtonRotated.background =
                    ContextCompat.getDrawable(context, R.drawable.timer_white_button)
                moveButtonRotated.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
                drawButtonRotated.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
                resignButtonRotated.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
                moveButton.background = ContextCompat.getDrawable(context, R.drawable.timer_black_button)
                drawButton.background = ContextCompat.getDrawable(context, R.drawable.timer_black_button)
                resignButton.background =
                    ContextCompat.getDrawable(context, R.drawable.timer_black_button)
                moveButton.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                drawButton.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
                resignButton.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
            }
        }
    }

    fun calculateTime() { //and display it
        _binding.apply {
            if (match!!.currentGame?.isFirstPlayerMove == true) {
                if (match!!.currentGame!!.moveNumber == 1) {
                    match!!.currentGame!!.firstMoveTime = match!!.currentGame!!.firstMoveTime - 1
                    firstMoveText.text = String.format(
                        getString(R.string.first_move_text),
                        match!!.currentGame!!.firstMoveTime
                    )
                } else {
                    match!!.currentGame!!.firstTimer = match!!.currentGame!!.firstTimer - 1
                    timerText.text = String.format(
                        getString(R.string.time),
                        match!!.currentGame!!.firstTimer / 3600,
                        match!!.currentGame!!.firstTimer / 60 % 60,
                        match!!.currentGame!!.firstTimer % 60
                    )
                }
            } else {
                if (match!!.currentGame!!.moveNumberRotated == 1) {
                    match!!.currentGame!!.firstMoveTimeRotated =
                        match!!.currentGame!!.firstMoveTimeRotated - 1
                    firstMoveTextRotated.text = String.format(
                        getString(R.string.first_move_text),
                        match!!.currentGame!!.firstMoveTimeRotated
                    )
                } else {
                    match!!.currentGame!!.secondTimer = match!!.currentGame!!.secondTimer - 1
                    timerTextRotated.text = String.format(
                        getString(R.string.time),
                        match!!.currentGame!!.secondTimer / 3600,
                        match!!.currentGame!!.secondTimer / 60 % 60,
                        match!!.currentGame!!.secondTimer % 60
                    )
                }
            }
        }

    }

    private fun makeWhiteMove() {
        _binding.apply {
            match!!.currentGame?.isFirstPlayerMove = false
            match!!.currentGame!!.moveNumberRotated = match!!.currentGame!!.moveNumberRotated + 1
            setButton(moveButton, false)
            setButton(drawButton, false)
            setButton(moveButtonRotated, true)
            setButton(drawButtonRotated, true) //making a move declines a draw offer
            drawButtonRotated.isChecked =
                false //a player must re-offer a draw every single time after it being declined
            moveButtonRotated.text = String.format(
                getString(R.string.move_number),
                match!!.currentGame!!.moveNumberRotated
            )
            if (match!!.currentGame!!.moveNumber > 1) {
                if (match!!.currentGame!!.firstIncrement != 0) {
                    match!!.currentGame!!.firstTimer =
                        match!!.currentGame!!.firstTimer + match!!.currentGame!!.firstIncrement
                    timerText.text = String.format(
                        getString(R.string.time),
                        match!!.currentGame!!.firstTimer / 3600,
                        match!!.currentGame!!.firstTimer / 60 % 60,
                        match!!.currentGame!!.firstTimer % 60
                    )
                }
            } else firstMoveText.visibility =
                View.INVISIBLE //text no longer needed - first move already made
        }
    }

    private fun makeBlackMove() {
        _binding.apply {
            match!!.currentGame?.isFirstPlayerMove = true
            match!!.currentGame!!.moveNumber = match!!.currentGame!!.moveNumber + 1
            setButton(moveButton, true)
            setButton(drawButton, true)
            setButton(moveButtonRotated, false)
            setButton(drawButtonRotated, false)
            drawButton.isChecked = false
            moveButton.text = String.format(
                getString(R.string.move_number),
                match!!.currentGame!!.moveNumber
            )
            if (match!!.currentGame!!.moveNumberRotated > 1) {
                if (match!!.currentGame!!.secondIncrement != 0) {
                    match!!.currentGame!!.secondTimer =
                        match!!.currentGame!!.secondTimer + match!!.currentGame!!.secondIncrement
                    timerTextRotated.text = String.format(
                        getString(R.string.time),
                        match!!.currentGame!!.secondTimer / 3600,
                        match!!.currentGame!!.secondTimer / 60 % 60,
                        match!!.currentGame!!.secondTimer % 60
                    )
                }
            } else firstMoveTextRotated.visibility = View.INVISIBLE
        }

    }

    fun checkForResult() {
        _binding.apply {
            if (match!!.currentGame!!.firstMoveTime == 0 || match!!.currentGame!!.firstTimer == 0) match!!.currentGame!!.gameState =
                GameState.LOST else if (match!!.currentGame!!.firstMoveTimeRotated == 0 || match!!.currentGame!!.secondTimer == 0) match!!.currentGame!!.gameState =
                GameState.WON else if (drawButton!!.isChecked && drawButtonRotated!!.isChecked) match!!.currentGame!!.gameState =
                GameState.DRAWN
        }
    }

    fun finishGame() {
        disableAllButtons()
        when (match!!.currentGame!!.gameState) {
            GameState.WON -> match!!.firstPlayerPoints = match!!.firstPlayerPoints + 1
            GameState.DRAWN -> {
                match!!.firstPlayerPoints = match!!.firstPlayerPoints + 0.5f
                match!!.secondPlayerPoints = match!!.secondPlayerPoints + 0.5f
            }

            GameState.LOST -> match!!.secondPlayerPoints = match!!.secondPlayerPoints + 1
            else -> {}
        }

        _binding.apply {
            pointsText.text = String.format(
                getString(R.string.points),
                match!!.firstPlayerPoints, match!!.secondPlayerPoints
            )
            pointsTextRotated.text = String.format(
                getString(R.string.points),
                match!!.secondPlayerPoints, match!!.firstPlayerPoints
            )
            if (match!!.numberOfGames == 1) {
                newGameButton.text = getString(R.string.new_game)
                newGameButton.visibility = View.VISIBLE
            } else {
                if (match!!.gameNumber < match!!.numberOfGames) {
                    newGameButton.text = getString(R.string.next_game)
                    newGameButton.visibility = View.VISIBLE
                } else {
                    newGameButton.visibility = View.INVISIBLE
                    showResults() //in Alert Dialog; end of a match
                }
            }
        }

    }

    private fun disableAllButtons() {
        _binding.apply {
            setButton(moveButton, false)
            setButton(drawButton, false)
            setButton(resignButton, false)
            setButton(moveButtonRotated, false)
            setButton(drawButtonRotated, false)
            setButton(resignButtonRotated, false)
        }
    }

    private fun showResults() {
        AlertDialog.Builder(requireContext())
            .setMessage(
                getString(
                    R.string.results,
                    match!!.firstPlayerPoints, match!!.secondPlayerPoints
                )
            )
            .setPositiveButton(R.string.ok_button) { dialog: DialogInterface?, which: Int ->
                findNavController().popBackStack()
            }
            .show()
    }

    companion object {
        private const val NO_ARGUMENT_MATCH_ID_FOUND = -1L
    }

}
