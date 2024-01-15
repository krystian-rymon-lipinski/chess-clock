package com.krystian.chessclock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.krystian.chessclock.customMatchPackage.CustomMatchDialogFragment
import com.krystianrymonlipinski.chessclock.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(), View.OnClickListener, MenuProvider {

    private val activityViewModel: MainActivityViewModel by viewModels()

    private var customGameId: Long? = null
    private var customMatchId: Long? = null

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
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun checkForPassedData() {
        if (findNavController().previousBackStackEntry != null) { // not a start destination
            customGameId = arguments?.getLong("customGameId")
            customMatchId = arguments?.getLong("customMatchId")
        }
    }




    override fun onClick(v: View) {

        //TODO: retrieve state from view
        /*
        val id = v.id //switch gives compilation error: "constant expression required"
        if (id == R.id.play_button) {
            if (customGameId == null) { //play a game - it's not custom game editor mode
                val firstPlayerSettings = listOf(
                    "firstPlayerTime" to playerTimeBar!!.progress + 1,
                    "firstPlayerIncrement" to playerIncrementBar!!.progress
                )
                val secondPlayerSettings = if (differentTime!!.isChecked) {
                    listOf(
                        "secondPlayerTime" to playerTwoTimeBar!!.progress + 1,
                        "secondPlayerIncrement" to playerTwoIncrementBar!!.progress
                    )
                } else firstPlayerSettings
                val numberOfGamesSettings = if (!singleGame!!.isChecked) {
                    "numberOfGames" to numberOfGamesBar!!.progress + 1
                } else "numberOfGames" to 1

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

                activityViewModel.updateCustomGame(customGameUpdated)
                findNavController().popBackStack()
            }
        }

         */
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
