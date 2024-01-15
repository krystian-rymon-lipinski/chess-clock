package com.krystian.chessclock.data_layer

import com.krystian.chessclock.model.CustomMatch
import com.krystian.chessclock.room.CustomGameEntity
import com.krystian.chessclock.room.CustomMatchDao
import com.krystian.chessclock.room.CustomMatchEntity
import com.krystian.chessclock.room.MatchWithGamesEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.capture
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CustomMatchDataSourceImplTest {

    private lateinit var testObj: CustomMatchDataSourceImpl

    @Mock
    lateinit var customMatchDao: CustomMatchDao

    @Captor
    lateinit var matchCaptor: ArgumentCaptor<CustomMatchEntity>

    private val customMatch = CustomMatch(2, "example")
    private val expectedCustomMatchEntity = CustomMatchEntity(2, "example")

    @Before
    fun setup() {
        testObj = CustomMatchDataSourceImpl(customMatchDao)
    }


    @Test
    fun getAll() = runTest {
        val expectedRetrievedMatches = listOf(customMatch)
        whenever(customMatchDao.getAll()).thenReturn(flowOf(listOf(expectedCustomMatchEntity)))

        testObj.getAll().take(1).collect {
            assertEquals(expectedRetrievedMatches, it)
        }
        verify(customMatchDao).getAll()
    }

    @Test
    fun getAllWithGames() = runTest {
        val entities = listOf(MatchWithGamesEntity(
            matchEntity = expectedCustomMatchEntity,
            games = listOf(CustomGameEntity(1, 2, 3, 4, 5, 6))
        ))
        whenever(customMatchDao.getAllWithGames()).thenReturn(flowOf(entities))

        testObj.getAllWithGames().take(1).collect {
            assertEquals(1, it.size)
            assertEquals(entities[0].matchEntity.id, it[0].id)
            assertEquals(entities[0].matchEntity.name, it[0].name)
            assertEquals(entities[0].games.size, it[0].games.size)
        }
        verify(customMatchDao).getAllWithGames()
    }

    @Test
    fun getById() = runTest {
        val expectedMatch = customMatch
        whenever(customMatchDao.getById(anyLong())).thenReturn(flowOf(expectedCustomMatchEntity))

        testObj.getById(12).take(1).collect {
            assertEquals(expectedMatch, it)
        }
    }

    @Test
    fun addMatch() = runTest {
        testObj.addMatch(customMatch)
        verify(customMatchDao).addMatch(capture(matchCaptor))
        assertEquals(expectedCustomMatchEntity, matchCaptor.value)
    }

    @Test
    fun updateMatch() = runTest {
        testObj.updateMatch(customMatch)
        verify(customMatchDao).updateMatch(capture(matchCaptor))
        assertEquals(expectedCustomMatchEntity, matchCaptor.value)
    }

    @Test
    fun deleteMatch() = runTest {
        testObj.deleteMatch(customMatch)
        verify(customMatchDao).deleteMatch(capture(matchCaptor))
        assertEquals(expectedCustomMatchEntity, matchCaptor.value)
    }
}