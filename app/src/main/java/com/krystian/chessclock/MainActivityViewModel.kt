package com.krystian.chessclock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krystian.chessclock.data_layer.CustomGameDataSource
import com.krystian.chessclock.data_layer.CustomMatchDataSource
import com.krystian.chessclock.models.CustomGame
import com.krystian.chessclock.models.CustomMatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val customMatchDataSource: CustomMatchDataSource,
    private val customGameDataSource: CustomGameDataSource
) : ViewModel() {

    val allMatches: StateFlow<List<CustomMatch>> = customMatchDataSource.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun getAllCustomMatches() : Flow<List<CustomMatch>> = customMatchDataSource.getAll()

    fun getMatchByIdWithGames(matchId: Long) : Flow<CustomMatch> = customMatchDataSource.getAllWithGames()
        .map { list -> list.first { it.id == matchId } }

    fun getGameById(gameId: Long) : Flow<CustomGame> = customGameDataSource.getById(gameId)

    fun addCustomMatch(match: CustomMatch) = viewModelScope.launch {
        customMatchDataSource.addMatch(match)
    }

    fun addCustomMatchWithDefaultGames(name: String, numberOfGames: Int) = viewModelScope.launch {
        val createdMatchId = customMatchDataSource.addMatch(CustomMatch(name = name))

        for (i in 0 until numberOfGames) {
            customGameDataSource.addGame(CustomGame.buildDefaultGame(createdMatchId))
        }
    }

    fun getAllGamesForCustomMatchStream(matchId: Long) : Flow<List<CustomGame>> {
        return customGameDataSource.getAllWithMatchId(matchId)
    }

    fun updateCustomMatch(match: CustomMatch) = viewModelScope.launch {
        customMatchDataSource.updateMatch(match)
    }

    fun deleteCustomMatch(match: CustomMatch) = viewModelScope.launch {
        customMatchDataSource.deleteMatch(match)
    }

    fun addCustomGame(game: CustomGame) = viewModelScope.launch {
        customGameDataSource.addGame(game)
    }

    fun updateCustomGame(game: CustomGame) = viewModelScope.launch {
        customGameDataSource.updateGame(game)
    }

    fun deleteCustomGame(game: CustomGame) = viewModelScope.launch {
        customGameDataSource.deleteGame(game)
    }

}