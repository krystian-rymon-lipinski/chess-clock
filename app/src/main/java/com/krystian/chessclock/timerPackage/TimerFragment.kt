package com.krystian.chessclock.timerPackage

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.krystian.chessclock.customMatchPackage.CustomMatchDatabase
import com.krystianrymonlipinski.chessclock.R

class TimerFragment : Fragment(), View.OnClickListener {
    private var gameNumberText: TextView? = null
    private var gameNumberTextRotated: TextView? = null
    private var pointsText: TextView? = null
    private var pointsTextRotated: TextView? = null
    private var timerText: TextView? = null
    private var timerTextRotated: TextView? = null
    private var firstMoveText: TextView? = null
    private var firstMoveTextRotated: TextView? = null
    private var drawButton: ToggleButton? = null
    private var drawButtonRotated: ToggleButton? = null
    private var moveButton: Button? = null
    private var moveButtonRotated: Button? = null
    private var resignButton: Button? = null
    private var resignButtonRotated: Button? = null
    private var newGameButton: Button? = null
    private var match: Match? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewComponents(view) //find views and attach listeners
        settings //find match parameters ((non)-custom, time, increments, number of games) and store it in match object
        setMatchGame() //set n-th game with its parameters
    }

    private fun setViewComponents(mainView: View) {
        gameNumberText = mainView.findViewById(R.id.game_number_text)
        gameNumberTextRotated = mainView.findViewById(R.id.game_number_text_rotated)
        pointsText = mainView.findViewById(R.id.points_text)
        pointsTextRotated = mainView.findViewById(R.id.points_text_rotated)
        timerText = mainView.findViewById(R.id.timer_text)
        timerTextRotated = mainView.findViewById(R.id.timer_text_rotated)
        firstMoveText = mainView.findViewById(R.id.first_move_text)
        firstMoveTextRotated = mainView.findViewById(R.id.first_move_text_rotated)
        drawButton = mainView.findViewById(R.id.draw_button)
        drawButtonRotated = mainView.findViewById(R.id.draw_button_rotated)
        moveButton = mainView.findViewById(R.id.move_button)
        moveButtonRotated = mainView.findViewById(R.id.move_button_rotated)
        resignButton = mainView.findViewById(R.id.resign_button)
        resignButtonRotated = mainView.findViewById(R.id.resign_button_rotated)
        newGameButton = mainView.findViewById(R.id.new_game_button)
        newGameButton?.setOnClickListener(this)
        resignButtonRotated?.setOnClickListener(this)
        resignButton?.setOnClickListener(this)
        moveButtonRotated?.setOnClickListener(this)
        moveButton?.setOnClickListener(this)
        drawButtonRotated?.setOnClickListener(this)
        drawButton?.setOnClickListener(this)
    }

    private val settings: Unit
        get() { //and store it into a match object

            //TODO: retrieve passed parameters
            val customMatchName = null
            /*
            val customMatchName = intent.getStringExtra(ExtraValues.CUSTOM_MATCH_NAME)
             */

            var numberOfGames = 0
            val timeOnes = ArrayList<Int?>()
            val incrementOnes = ArrayList<Int?>()
            val timeTwos = ArrayList<Int?>()
            val incrementTwos = ArrayList<Int?>()
            if (customMatchName == null) {
                //TODO: retrieve passed parameters
                /*
                numberOfGames = intent.getIntExtra(
                    ExtraValues.NUMBER_OF_GAMES,
                    1
                ) //if no extra send, it's a single game
                val timeOne = intent.getIntExtra(ExtraValues.PLAYER_ONE_TIME, 15)
                val timeTwo = intent.getIntExtra(
                    ExtraValues.PLAYER_TWO_TIME,
                    timeOne
                ) //if there's no player two's extra, timeTwo = timeOne;
                val incrementOne = intent.getIntExtra(ExtraValues.PLAYER_ONE_INCREMENT, 0)
                val incrementTwo = intent.getIntExtra(
                    ExtraValues.PLAYER_TWO_INCREMENT,
                    incrementOne
                ) //same as time
                for (i in 0 until numberOfGames) {
                    timeOnes.add(timeOne)
                    incrementOnes.add(incrementOne)
                    timeTwos.add(timeTwo)
                    incrementTwos.add(incrementTwo)
                }
                 */
            } else {
                val customDb = CustomMatchDatabase()
                val query = "SELECT * FROM $customMatchName;"
                val cursor = customDb.accessDatabase(requireContext())!!
                    .rawQuery(query, null)
                numberOfGames = cursor.count
                cursor.moveToFirst()
                do {
                    timeOnes.add(cursor.getInt(2))
                    incrementOnes.add(cursor.getInt(3))
                    timeTwos.add(cursor.getInt(4))
                    incrementTwos.add(cursor.getInt(5))
                } while (cursor.moveToNext())
                cursor.close()
                customDb.closeDatabase()
            }
            match = Match(numberOfGames, timeOnes, incrementOnes, timeTwos, incrementTwos)
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
                drawButton,
                false
            ) //a draw can be offered only once a move and during your own
        } else if (id == R.id.draw_button_rotated) {
            setButton(drawButtonRotated, false)
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
        timerText!!.text = String.format(
            getString(R.string.time), match!!.currentGame!!.firstTimer / 3600,
            match!!.currentGame!!.firstTimer / 60 % 60, match!!.currentGame!!.firstTimer % 60
        )
        timerTextRotated!!.text = String.format(
            getString(R.string.time), match!!.currentGame!!.secondTimer / 3600,
            match!!.currentGame!!.secondTimer / 60 % 60, match!!.currentGame!!.secondTimer % 60
        )
        firstMoveText!!.text = String.format(getString(R.string.first_move_text), 30)
        firstMoveTextRotated!!.text = String.format(getString(R.string.first_move_text), 30)
        pointsText!!.text = String.format(
            getString(R.string.points), match!!.firstPlayerPoints,
            match!!.secondPlayerPoints
        )
        pointsTextRotated!!.text = String.format(
            getString(R.string.points), match!!.secondPlayerPoints,
            match!!.firstPlayerPoints
        )
        gameNumberText!!.text = String.format(
            getString(R.string.game_number), match!!.gameNumber,
            match!!.numberOfGames
        )
        gameNumberTextRotated!!.text = String.format(
            getString(R.string.game_number), match!!.gameNumber,
            match!!.numberOfGames
        )
        moveButton!!.text =
            String.format(getString(R.string.move_number), match!!.currentGame!!.moveNumber)
        moveButtonRotated!!.text = String.format(
            getString(R.string.move_number),
            match!!.currentGame!!.moveNumberRotated
        )
        firstMoveText!!.visibility = View.VISIBLE
        firstMoveTextRotated!!.visibility = View.VISIBLE
    }

    private fun setButtons() {
        newGameButton!!.visibility = View.INVISIBLE
        drawButton!!.isChecked = false
        drawButtonRotated!!.isChecked = false
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
        if (match!!.gameNumber % 2 != 0) {
            moveButton!!.background = ContextCompat.getDrawable(context, R.drawable.timer_white_button)
            moveButton!!.background = ContextCompat.getDrawable(context, R.drawable.timer_white_button)
            drawButton!!.background = ContextCompat.getDrawable(context, R.drawable.timer_white_button)
            resignButton!!.background =
                ContextCompat.getDrawable(context, R.drawable.timer_white_button)
            moveButton!!.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
            drawButton!!.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
            resignButton!!.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
            moveButtonRotated!!.background =
                ContextCompat.getDrawable(context, R.drawable.timer_black_button)
            drawButtonRotated!!.background =
                ContextCompat.getDrawable(context, R.drawable.timer_black_button)
            resignButtonRotated!!.background =
                ContextCompat.getDrawable(context, R.drawable.timer_black_button)
            moveButtonRotated!!.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
            drawButtonRotated!!.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
            resignButtonRotated!!.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
        } else {
            moveButtonRotated!!.background =
                ContextCompat.getDrawable(context, R.drawable.timer_white_button)
            drawButtonRotated!!.background =
                ContextCompat.getDrawable(context, R.drawable.timer_white_button)
            resignButtonRotated!!.background =
                ContextCompat.getDrawable(context, R.drawable.timer_white_button)
            moveButtonRotated!!.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
            drawButtonRotated!!.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
            resignButtonRotated!!.setTextColor(ContextCompat.getColor(context, R.color.blackColor))
            moveButton!!.background = ContextCompat.getDrawable(context, R.drawable.timer_black_button)
            drawButton!!.background = ContextCompat.getDrawable(context, R.drawable.timer_black_button)
            resignButton!!.background =
                ContextCompat.getDrawable(context, R.drawable.timer_black_button)
            moveButton!!.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
            drawButton!!.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
            resignButton!!.setTextColor(ContextCompat.getColor(context, R.color.whiteColor))
        }
    }

    fun calculateTime() { //and display it
        if (match!!.currentGame?.isFirstPlayerMove == true) {
            if (match!!.currentGame!!.moveNumber == 1) {
                match!!.currentGame!!.firstMoveTime = match!!.currentGame!!.firstMoveTime - 1
                firstMoveText!!.text = String.format(
                    getString(R.string.first_move_text),
                    match!!.currentGame!!.firstMoveTime
                )
            } else {
                match!!.currentGame!!.firstTimer = match!!.currentGame!!.firstTimer - 1
                timerText!!.text = String.format(
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
                firstMoveTextRotated!!.text = String.format(
                    getString(R.string.first_move_text),
                    match!!.currentGame!!.firstMoveTimeRotated
                )
            } else {
                match!!.currentGame!!.secondTimer = match!!.currentGame!!.secondTimer - 1
                timerTextRotated!!.text = String.format(
                    getString(R.string.time),
                    match!!.currentGame!!.secondTimer / 3600,
                    match!!.currentGame!!.secondTimer / 60 % 60,
                    match!!.currentGame!!.secondTimer % 60
                )
            }
        }
    }

    private fun makeWhiteMove() {
        match!!.currentGame?.isFirstPlayerMove = false
        match!!.currentGame!!.moveNumberRotated = match!!.currentGame!!.moveNumberRotated + 1
        setButton(moveButton, false)
        setButton(drawButton, false)
        setButton(moveButtonRotated, true)
        setButton(drawButtonRotated, true) //making a move declines a draw offer
        drawButtonRotated!!.isChecked =
            false //a player must re-offer a draw every single time after it being declined
        moveButtonRotated!!.text = String.format(
            getString(R.string.move_number),
            match!!.currentGame!!.moveNumberRotated
        )
        if (match!!.currentGame!!.moveNumber > 1) {
            if (match!!.currentGame!!.firstIncrement != 0) {
                match!!.currentGame!!.firstTimer =
                    match!!.currentGame!!.firstTimer + match!!.currentGame!!.firstIncrement
                timerText!!.text = String.format(
                    getString(R.string.time),
                    match!!.currentGame!!.firstTimer / 3600,
                    match!!.currentGame!!.firstTimer / 60 % 60,
                    match!!.currentGame!!.firstTimer % 60
                )
            }
        } else firstMoveText!!.visibility =
            View.INVISIBLE //text no longer needed - first move already made
    }

    private fun makeBlackMove() {
        match!!.currentGame?.isFirstPlayerMove = true
        match!!.currentGame!!.moveNumber = match!!.currentGame!!.moveNumber + 1
        setButton(moveButton, true)
        setButton(drawButton, true)
        setButton(moveButtonRotated, false)
        setButton(drawButtonRotated, false)
        drawButton!!.isChecked = false
        moveButton!!.text = String.format(
            getString(R.string.move_number),
            match!!.currentGame!!.moveNumber
        )
        if (match!!.currentGame!!.moveNumberRotated > 1) {
            if (match!!.currentGame!!.secondIncrement != 0) {
                match!!.currentGame!!.secondTimer =
                    match!!.currentGame!!.secondTimer + match!!.currentGame!!.secondIncrement
                timerTextRotated!!.text = String.format(
                    getString(R.string.time),
                    match!!.currentGame!!.secondTimer / 3600,
                    match!!.currentGame!!.secondTimer / 60 % 60,
                    match!!.currentGame!!.secondTimer % 60
                )
            }
        } else firstMoveTextRotated!!.visibility = View.INVISIBLE
    }

    fun checkForResult() {
        if (match!!.currentGame!!.firstMoveTime == 0 || match!!.currentGame!!.firstTimer == 0) match!!.currentGame!!.gameState =
            GameState.LOST else if (match!!.currentGame!!.firstMoveTimeRotated == 0 || match!!.currentGame!!.secondTimer == 0) match!!.currentGame!!.gameState =
            GameState.WON else if (drawButton!!.isChecked && drawButtonRotated!!.isChecked) match!!.currentGame!!.gameState =
            GameState.DRAWN
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
        pointsText!!.text = String.format(
            getString(R.string.points),
            match!!.firstPlayerPoints, match!!.secondPlayerPoints
        )
        pointsTextRotated!!.text = String.format(
            getString(R.string.points),
            match!!.secondPlayerPoints, match!!.firstPlayerPoints
        )
        if (match!!.numberOfGames == 1) {
            newGameButton!!.text = getString(R.string.new_game)
            newGameButton!!.visibility = View.VISIBLE
        } else {
            if (match!!.gameNumber < match!!.numberOfGames) {
                newGameButton!!.text = getString(R.string.next_game)
                newGameButton!!.visibility = View.VISIBLE
            } else {
                newGameButton!!.visibility = View.INVISIBLE
                showResults() //in Alert Dialog; end of a match
            }
        }
    }

    private fun disableAllButtons() {
        setButton(moveButton, false)
        setButton(drawButton, false)
        setButton(resignButton, false)
        setButton(moveButtonRotated, false)
        setButton(drawButtonRotated, false)
        setButton(resignButtonRotated, false)
    }

    private fun showResults() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(
            getString(
                R.string.results,
                match!!.firstPlayerPoints, match!!.secondPlayerPoints
            )
        )
            .setPositiveButton(R.string.ok_button) { dialog: DialogInterface?, which: Int ->

                //TODO: navigate between fragments
                /*
                startActivity(
                    Intent(this@TimerFragment, MainActivity::class.java)
                )
                 */
            }
        builder.show()
    }

    //TODO: navigate between fragments
    /*
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
     */
}
