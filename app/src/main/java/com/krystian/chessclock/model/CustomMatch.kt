package com.krystian.chessclock.model

data class CustomMatch(
    val id: Long = 0,
    val name: String,
    val games: List<CustomGame> = emptyList()
)