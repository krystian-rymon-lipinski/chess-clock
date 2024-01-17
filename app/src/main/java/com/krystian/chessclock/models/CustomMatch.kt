package com.krystian.chessclock.models

data class CustomMatch(
    val id: Long = 0,
    val name: String,
    val games: List<CustomGame> = emptyList()
)