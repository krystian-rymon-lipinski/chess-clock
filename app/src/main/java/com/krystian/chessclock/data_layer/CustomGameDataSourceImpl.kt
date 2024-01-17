package com.krystian.chessclock.data_layer

import com.krystian.chessclock.models.CustomGame
import com.krystian.chessclock.room.CustomGameDao
import com.krystian.chessclock.room.CustomGameEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomGameDataSourceImpl @Inject constructor(
    private val customGameDao: CustomGameDao
) : CustomGameDataSource {

    override fun getAllWithMatchId(matchId: Long) : Flow<List<CustomGame>> {
        return customGameDao.getAllWithMatchId(matchId).map { list ->
            list.map { convertFromEntity(it) }
        }
    }

    override fun getById(gameId: Long): Flow<CustomGame> {
        return customGameDao.getGameWithId(gameId).map {
            convertFromEntity(it)
        }
    }

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

    private fun convertFromEntity(entity: CustomGameEntity) : CustomGame {
        return CustomGame(
            id = entity.id,
            whiteTime = entity.whiteTime,
            whiteIncrement = entity.whiteIncrement,
            blackTime = entity.blackTime,
            blackIncrement = entity.blackIncrement,
            matchId = entity.matchId
        )
    }
}