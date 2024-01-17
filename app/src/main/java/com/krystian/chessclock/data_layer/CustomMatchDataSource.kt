package com.krystian.chessclock.data_layer

import com.krystian.chessclock.models.CustomMatch
import kotlinx.coroutines.flow.Flow

interface CustomMatchDataSource {

    fun getAll() : Flow<List<CustomMatch>>
    fun getById(matchId: Long) : Flow<CustomMatch>
    fun getAllWithGames() : Flow<List<CustomMatch>>
    suspend fun addMatch(match: CustomMatch) : Long
    suspend fun updateMatch(match: CustomMatch)
    suspend fun deleteMatch(match: CustomMatch)
}