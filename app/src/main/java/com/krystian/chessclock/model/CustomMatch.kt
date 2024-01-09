package com.krystian.chessclock.model

data class CustomMatch(
    val id: Int = 0,
    val name: String,
    val games: List<CustomGame> = emptyList()
)