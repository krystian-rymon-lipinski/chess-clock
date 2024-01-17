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
class CustomMatchEntityTest {

    private lateinit var db: ChessClockDatabase
    private lateinit var matchDao: CustomMatchDao

    private val matchEntity = CustomMatchEntity(name = "example")

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ChessClockDatabase::class.java
        ).build()
        matchDao = db.customMatchDao
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun add() = runBlocking {
        matchDao.addMatch(matchEntity)
        matchDao.getAllWithGames().take(1).collect {
            assertEquals(1, it.size)
            assertEquals(1, it[0].matchEntity.id)
            assertEquals("example", it[0].matchEntity.name)
            assertEquals(0, it[0].games.size)
        }
    }

    @Test
    fun update() = runBlocking {
        matchDao.addMatch(matchEntity)
        val updatedEntity = matchEntity.copy(id = 1, name = "updated_example")
        matchDao.updateMatch(updatedEntity)

        matchDao.getAllWithGames().take(1).collect {
            assertEquals(1, it.size)
            assertEquals(1, it[0].matchEntity.id)
            assertEquals("updated_example", it[0].matchEntity.name)
            assertEquals(0, it[0].games.size)
        }
    }

    @Test
    fun delete() = runBlocking {
        matchDao.addMatch(matchEntity)
        val entityToRemain = matchEntity.copy(name = "other_example")
        matchDao.addMatch(entityToRemain)
        val entityToDelete = matchEntity.copy(id = 1)
        matchDao.deleteMatch(entityToDelete)

        matchDao.getAllWithGames().take(1).collect {
            assertEquals(1, it.size)
            assertEquals(2, it[0].matchEntity.id)
            assertEquals("other_example", it[0].matchEntity.name)
            assertEquals(0, it[0].games.size)
        }
    }

    @Test
    fun deleteWithItsGames() = runBlocking {
        matchDao.addMatch(matchEntity)
        val customGame1 = CustomGameEntity(3, 10, 5, 10, 5, 1)
        val customGame2 = CustomGameEntity(4, 10, 5, 10, 5, 1)
        val gameDao = db.customGameDao.also {
            it.addGame(customGame1)
            it.addGame(customGame2)
        }

        gameDao.getAllGames().take(1).collect {
            assertEquals(2, it.size)
        }
        matchDao.getAllWithGames().take(1).collect {
            assertEquals(1, it.size)
            assertEquals(2, it[0].games.size)
        }

        matchDao.deleteMatch(matchEntity.copy(id = 1))
        matchDao.getAllWithGames().take(1).collect {
            assertEquals(0, it.size)
        }
        gameDao.getAllGames().take(1).collect {
            assertEquals(0, it.size)
        }
    }

    @Test
    fun getById() = runBlocking {
        matchDao.addMatch(matchEntity)
        matchDao.addMatch(matchEntity)
        matchDao.getById(2).take(1).collect {
            assertEquals(2, it.id)
            assertEquals(matchEntity.name, it.name)
        }
    }

}