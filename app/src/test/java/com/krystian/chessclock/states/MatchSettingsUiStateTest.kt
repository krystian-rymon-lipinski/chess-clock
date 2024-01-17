package com.krystian.chessclock.states

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class MatchSettingsUiStateTest {

    private var testObj = MatchSettingUiState()

    @Test
    fun changeAllStateFields() {
        testObj = testObj.changeFirstPlayerGameTime(newValue = 20)
        assertEquals(20, testObj.firstPlayerGameTime)

        testObj = testObj.changeFirstPlayerIncrement(newValue = 7)
        assertEquals(7, testObj.firstPlayerIncrement)

        testObj = testObj.changeSecondPlayerGameTime(newValue = 3)
        assertEquals(3, testObj.secondPlayerGameTime)

        testObj = testObj.changeSecondPlayerIncrement(6)
        assertEquals(6, testObj.secondPlayerIncrement)

        testObj = testObj.changeNumberOfGames(4)
        assertEquals(4, testObj.numberOfGames)

        testObj = testObj.changeMode(newMode = MatchSettingUiState.Mode.GAME_UPDATE)
        assertEquals(MatchSettingUiState.Mode.GAME_UPDATE, testObj.mode)

        testObj = testObj.changeIsSingleGameChecked(newValue = false)
        assertFalse(testObj.isSingleGameChecked)

        testObj = testObj.toggleIsTimeDifferentChecked()
        assertTrue(testObj.isTimeDifferentChecked)
    }
}