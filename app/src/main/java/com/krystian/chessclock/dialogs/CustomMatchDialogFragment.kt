package com.krystian.chessclock.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.DialogFragmentNewCustomMatchBinding


class CustomMatchDialogFragment(
    private val callback: Callback
) : DialogFragment() {

    private val _binding: DialogFragmentNewCustomMatchBinding by lazy {
        DialogFragmentNewCustomMatchBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_new_match)
            .setView(_binding.root)
            .setPositiveButton(R.string.ok_button) { dialog: DialogInterface?, _: Int ->
                callback.onNewMatchCreated(
                    _binding.etMatchName.text.toString(),
                    _binding.slNumberOfGames.value.toInt()
                )
                dialog?.dismiss()
            }
            .setNegativeButton(R.string.cancel_button) { dialog: DialogInterface?, _: Int ->
                dialog?.dismiss()
            }
            .show()
            .also {
                setGamesText(_binding.slNumberOfGames.value.toInt())
                setIsPositiveButtonEnabled(it, _binding.tfMatchName.editText?.text?.isNotBlank() ?: false)
                setupListeners(it)
            }
    }

    private fun setupListeners(alertDialog: AlertDialog) {
        with(_binding) {
            slNumberOfGames.addOnChangeListener { _, value, _ ->
                setGamesText(value.toInt())
            }
            tfMatchName.editText?.doAfterTextChanged { text ->
                text?.let {
                    setIsPositiveButtonEnabled(alertDialog, text.isNotBlank())
                }
            }
        }
    }

    private fun setGamesText(number: Int) {
        _binding.tvNumberOfGames.text = String.format(getString(R.string.number_of_games, number))
    }

    private fun setIsPositiveButtonEnabled(alertDialog: AlertDialog, isEnabled: Boolean) {
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = isEnabled
    }

    interface Callback {
        fun onNewMatchCreated(name: String, numberOfGames: Int)
    }
}
