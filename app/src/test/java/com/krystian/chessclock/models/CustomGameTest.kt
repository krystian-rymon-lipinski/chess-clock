package com.krystian.chessclock.models

import com.krystian.chessclock.model.CustomGame
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CustomGameTest {

    private var testObj = CustomGame(
        whiteTime = 15,
        whiteIncrement = 0,
        blackTime = 15,
        blackIncrement = 0,
        matchId = 1
    )

    @Test
    fun areTimeSettingsDifferent() {
        assertFalse(testObj.areTimeSettingsDifferent())

        testObj = testObj.copy(whiteTime = 12)
        assertTrue(testObj.areTimeSettingsDifferent())

        testObj = testObj.copy(blackTime = 12, blackIncrement = 4)
        assertTrue(testObj.areTimeSettingsDifferent())

        testObj = testObj.copy(whiteIncrement = 4)
        assertFalse(testObj.areTimeSettingsDifferent())
    }
}