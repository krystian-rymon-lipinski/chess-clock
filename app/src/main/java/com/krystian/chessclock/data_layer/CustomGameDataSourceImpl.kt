package com.krystian.chessclock.data_layer

import com.krystian.chessclock.model.CustomGame
import com.krystian.chessclock.room.CustomGameDao
import com.krystian.chessclock.room.CustomGameEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomGameDataSourceImpl @Inject constructor(
    private val customGameDao: CustomGameDao
) : CustomGameDataSource {


    override suspend fun addGame(game: CustomGame) = withContext(Dispatchers.IO) {
        customGameDao.addGame(convertToEntity(game))
    }

    override suspend fun updateGame(game: CustomGame) = withContext(Dispatchers.IO) {
        customGameDao.updateGame(convertToEntity(game))
    }

    override suspend fun deleteGame(game: CustomGame) = withContext(Dispatchers.IO) {
        customGameDao.deleteGame(convertToEntity(game))
    }


    private fun convertToEntity(game: CustomGame) : CustomGameEntity {
        return CustomGameEntity(
            id = game.id,
            whiteTime = game.whiteTime,
            whiteIncrement = game.whiteIncrement,
            blackTime = game.blackTime,
            blackIncrement = game.blackIncrement,
            matchId = game.matchId
        )
    }
}