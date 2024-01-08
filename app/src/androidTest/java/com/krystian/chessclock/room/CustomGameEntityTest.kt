package com.krystian.chessclock.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomGameEntityTest {

    private lateinit var db: ChessClockDatabase
    private lateinit var dao: CustomGameDao

    private val gameEntity = CustomGameEntity(
        whiteTime = 20,
        whiteIncrement = 10,
        blackTime = 20,
        blackIncrement = 10,
        matchId = MATCH_ID
    )

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ChessClockDatabase::class.java
        ).build()
        dao = db.customGameDao

        val matchDao = db.customMatchDao
        matchDao.addMatch(CustomMatchEntity(id = MATCH_ID, name = "custom_match"))
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun add() = runBlocking {
        dao.addGame(gameEntity)
        dao.getAllGames().take(1).collect {
            assertEquals(1, it.size)
            assertEquals(1, it[0].id)
            assertEqualsGameEntity(gameEntity, it[0])
        }
    }

    @Test
    fun update() = runBlocking {
        dao.addGame(gameEntity)
        val updatedEntity = gameEntity.copy(id = 1, whiteTime = 42, blackIncrement = 4)
        dao.updateGame(updatedEntity)
        dao.getAllGames().take(1).collect {
            assertEquals(1, it.size)
            assertEquals(1, it[0].id)
            assertEqualsGameEntity(updatedEntity, it[0])
        }

    }

    @Test
    fun delete() = runBlocking {
        dao.addGame(gameEntity)
        val entityToRemain = gameEntity.copy(blackIncrement = 20)
        dao.addGame(entityToRemain)
        val entityToDelete = gameEntity.copy(id = 1)
        dao.deleteGame(entityToDelete)

        dao.getAllGames().take(1).collect {
            assertEquals(1, it.size)
            assertEquals(2, it[0].id)
            assertEqualsGameEntity(entityToRemain, it[0])
        }
    }

    @Test
    fun getEntityWithId() = runBlocking {
        val addedEntity = gameEntity.copy(7)
        dao.addGame(addedEntity)
        dao.getGameWithId(7).take(1).collect {
            assertEquals(7, it.id)
            assertEqualsGameEntity(addedEntity, it)
        }
    }

    private fun assertEqualsGameEntity(expected: CustomGameEntity, actual: CustomGameEntity) {
        assertEquals(expected.whiteTime, actual.whiteTime)
        assertEquals(expected.whiteIncrement, actual.whiteIncrement)
        assertEquals(expected.blackTime, actual.blackTime)
        assertEquals(expected.blackIncrement, actual.blackIncrement)
        assertEquals(expected.matchId, actual.matchId)
    }

    companion object {
        const val MATCH_ID = 1
    }
}