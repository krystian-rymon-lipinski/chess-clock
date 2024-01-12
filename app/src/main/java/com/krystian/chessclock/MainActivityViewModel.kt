package com.krystian.chessclock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krystian.chessclock.data_layer.CustomGameDataSource
import com.krystian.chessclock.data_layer.CustomMatchDataSource
import com.krystian.chessclock.model.CustomGame
import com.krystian.chessclock.model.CustomMatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val customMatchDataSource: CustomMatchDataSource,
    private val customGameDataSource: CustomGameDataSource
) : ViewModel() {

    fun getAllCustomMatches() : Flow<List<CustomMatch>> = customMatchDataSource.getAll()

    fun addCustomMatch(match: CustomMatch) = viewModelScope.launch {
        customMatchDataSource.addMatch(match)
    }

    fun addCustomMatchWithGames(name: String, numberOfGames: Int) = viewModelScope.launch {
        val createdMatchId = customMatchDataSource.addMatch(CustomMatch(name = name))

        for (i in 0 until numberOfGames) {
            customGameDataSource.addGame(CustomGame.buildDefaultGame(createdMatchId))
        }
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