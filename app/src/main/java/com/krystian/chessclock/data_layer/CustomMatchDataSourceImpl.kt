package com.krystian.chessclock.data_layer

import com.krystian.chessclock.model.CustomMatch
import com.krystian.chessclock.room.CustomMatchDao
import com.krystian.chessclock.room.CustomMatchEntity
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
}