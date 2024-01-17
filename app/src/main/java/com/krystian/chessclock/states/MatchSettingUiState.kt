package com.krystian.chessclock.states

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MatchSettingUiState(
    val mode: Mode = Mode.MATCH_SETTING,
    val firstPlayerGameTime: Int = 15,
    val firstPlayerIncrement: Int = 0,
    val secondPlayerGameTime: Int = 15,
    val secondPlayerIncrement: Int = 0,
    val numberOfGames: Int = 5,
    val isSingleGameChecked: Boolean = true,
    val isTimeDifferentChecked: Boolean = false
) : Parcelable {

    enum class Mode {
        MATCH_SETTING,
        GAME_UPDATE
    }

    fun changeMode(newMode: Mode) = copy(mode = newMode)
    fun changeFirstPlayerGameTime(newValue: Int) = copy(firstPlayerGameTime = newValue)
    fun changeFirstPlayerIncrement(newValue: Int) = copy(firstPlayerIncrement = newValue)
    fun changeSecondPlayerGameTime(newValue: Int) = copy(secondPlayerGameTime = newValue)
    fun changeSecondPlayerIncrement(newValue: Int) = copy(secondPlayerIncrement = newValue)
    fun changeNumberOfGames(newValue: Int) = copy(numberOfGames = newValue)
    fun changeIsSingleGameChecked(newValue: Boolean) = copy(isSingleGameChecked = newValue)
    fun toggleIsTimeDifferentChecked() = copy(isTimeDifferentChecked = !isTimeDifferentChecked)

}