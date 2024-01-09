package com.krystian.chessclock.data_layer

import com.krystian.chessclock.model.CustomGame

interface CustomGameDataSource {

    suspend fun addGame(game: CustomGame)
    suspend fun updateGame(game: CustomGame)
    suspend fun deleteGame(game: CustomGame)
}