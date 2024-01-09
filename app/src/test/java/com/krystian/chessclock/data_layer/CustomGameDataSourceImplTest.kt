package com.krystian.chessclock.data_layer

import com.krystian.chessclock.model.CustomGame
import com.krystian.chessclock.room.CustomGameDao
import com.krystian.chessclock.room.CustomGameEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.capture
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class CustomGameDataSourceImplTest {

    private lateinit var testObj: CustomGameDataSourceImpl

    @Mock
    lateinit var customGameDao: CustomGameDao

    @Captor
    lateinit var customGameEntityCaptor: ArgumentCaptor<CustomGameEntity>

    private val customGame = CustomGame(1, 2, 3, 4, 5, 6)
    private val expectedCapturedEntity = CustomGameEntity(1, 2, 3, 4, 5, 6)

    @Before
    fun setup() {
        testObj = CustomGameDataSourceImpl(customGameDao)
    }

    @Test
    fun addGame() = runTest {
        testObj.addGame(customGame)
        verify(customGameDao).addGame(capture(customGameEntityCaptor))
        assertEquals(expectedCapturedEntity, customGameEntityCaptor.value)
    }

    @Test
    fun updateGame() = runTest {
        testObj.updateGame(customGame)
        verify(customGameDao).updateGame(capture(customGameEntityCaptor))
        assertEquals(expectedCapturedEntity, customGameEntityCaptor.value)
    }

    @Test
    fun deleteGame() = runTest {
        testObj.deleteGame(customGame)
        verify(customGameDao).deleteGame(capture(customGameEntityCaptor))
        assertEquals(expectedCapturedEntity, customGameEntityCaptor.value)
    }

}