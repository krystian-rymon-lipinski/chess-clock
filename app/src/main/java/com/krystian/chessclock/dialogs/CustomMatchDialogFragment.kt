package com.krystian.chessclock.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.krystianrymonlipinski.chessclock.R

class CustomMatchDialogFragment(
    private val callback: Callback
) : DialogFragment(), OnSeekBarChangeListener {
    private var dialogView: View? = null
    private var numberOfGamesBar: SeekBar? = null
    private var numberOfGamesText: TextView? = null
    private var matchName: EditText? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        dialogView = inflater.inflate(R.layout.custom_match_dialog, null)
        setViewComponents()
        builder.setView(dialogView)
            .setTitle(R.string.add_new_match)
            .setPositiveButton(R.string.ok_button) { dialog: DialogInterface?, id: Int ->
                val name = matchName!!.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(activity, R.string.no_name_chosen, Toast.LENGTH_SHORT).show()
                } else {
                    callback.onNewMatchCreated(name, numberOfGamesBar!!.progress + 2)
                }
            }
            .setNegativeButton(R.string.cancel_button) { dialog: DialogInterface?, which: Int -> }
        return builder.create()
    }

    fun setViewComponents() {
        numberOfGamesBar = dialogView!!.findViewById(R.id.number_of_games_bar)
        numberOfGamesText = dialogView!!.findViewById(R.id.number_of_games_dialog)
        matchName = dialogView!!.findViewById(R.id.match_name)
        numberOfGamesBar?.setOnSeekBarChangeListener(this)
        numberOfGamesBar?.progress = 3
        numberOfGamesText?.text = getString(
            R.string.number_of_games, numberOfGamesBar?.progress?.plus(2)
        )
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val numberOfGames = progress + 2 //range 2-30
        numberOfGamesText!!.text = getString(R.string.number_of_games, numberOfGames)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    interface Callback {
        fun onNewMatchCreated(name: String, numberOfGames: Int)
    }
}
