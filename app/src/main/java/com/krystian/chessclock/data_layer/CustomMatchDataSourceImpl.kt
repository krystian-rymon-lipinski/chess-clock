package com.krystian.chessclock.data_layer

import com.krystian.chessclock.models.CustomGame
import com.krystian.chessclock.models.CustomMatch
import com.krystian.chessclock.room.CustomGameEntity
import com.krystian.chessclock.room.CustomMatchDao
import com.krystian.chessclock.room.CustomMatchEntity
import com.krystian.chessclock.room.MatchWithGamesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomMatchDataSourceImpl @Inject constructor(
    private val customMatchDao: CustomMatchDao
) : CustomMatchDataSource {

    override fun getAll() : Flow<List<CustomMatch>> {
        return customMatchDao.getAll().map { list ->
            list.map { entity -> convertFromEntity(entity) }
        }
    }

    override fun getAllWithGames() : Flow<List<CustomMatch>> {
        return customMatchDao.getAllWithGames().map { list ->
            list.map { entityWithGames -> convertFromEntityWithGames(entityWithGames) }
        }
    }

    override fun getById(matchId: Long) : Flow<CustomMatch> {
        return customMatchDao.getById(matchId).map { convertFromEntity(it) }
    }

    override suspend fun addMatch(match: CustomMatch) = withContext(Dispatchers.IO) {
        customMatchDao.addMatch(convertToEntity(match))
    }

    override suspend fun updateMatch(match: CustomMatch) = withContext(Dispatchers.IO) {
        customMatchDao.updateMatch(convertToEntity(match))
    }

    override suspend fun deleteMatch(match: CustomMatch) = withContext(Dispatchers.IO) {
        customMatchDao.deleteMatch(convertToEntity(match))
    }

    private fun convertToEntity(match: CustomMatch) : CustomMatchEntity {
        return CustomMatchEntity(
            id = match.id,
            name = match.name
        )
    }

    private fun convertFromEntity(entity: CustomMatchEntity) : CustomMatch {
        return CustomMatch(
            id = entity.id,
            name = entity.name
        )
    }

    private fun convertFromEntityWithGames(entityWithGames: MatchWithGamesEntity) : CustomMatch {
        return CustomMatch(
            id = entityWithGames.matchEntity.id,
            name = entityWithGames.matchEntity.name,
            games = entityWithGames.games.map { convertFromGameEntity(it) }
        )
    }

    private fun convertFromGameEntity(entity: CustomGameEntity) : CustomGame {
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