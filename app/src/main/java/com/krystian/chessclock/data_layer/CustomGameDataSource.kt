package com.krystian.chessclock.data_layer

import com.krystian.chessclock.model.CustomGame
import kotlinx.coroutines.flow.Flow

interface CustomGameDataSource {

    fun getAllWithMatchId(matchId: Long) : Flow<List<CustomGame>>
    suspend fun addGame(game: CustomGame)
    suspend fun updateGame(game: CustomGame)
    suspend fun deleteGame(game: CustomGame)
}