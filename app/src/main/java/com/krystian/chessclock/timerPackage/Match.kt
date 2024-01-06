package com.krystian.chessclock.timerPackage

class Match @JvmOverloads constructor(@JvmField var numberOfGames: Int = 1) {
    @JvmField
    var currentGame: Game? = null
    @JvmField
    var firstPlayerPoints = 0f
    @JvmField
    var secondPlayerPoints = 0f
    @JvmField
    var gameNumber = 1
    lateinit var oneTimes: ArrayList<Int?>
    lateinit var oneIncrements: ArrayList<Int?>
    lateinit var twoTimes: ArrayList<Int?>
    lateinit var twoIncrements: ArrayList<Int?>

    constructor(match: Match) : this(
        match.numberOfGames,
        match.oneTimes,
        match.oneIncrements,
        match.twoTimes,
        match.twoIncrements
    )

    constructor(
        numberOfGames: Int, oneTimes: ArrayList<Int?>, oneIncrements: ArrayList<Int?>,
        twoTimes: ArrayList<Int?>, twoIncrements: ArrayList<Int?>
    ) : this(numberOfGames) {
        this.oneTimes = oneTimes
        this.oneIncrements = oneIncrements
        this.twoTimes = twoTimes
        this.twoIncrements = twoIncrements
    }

    fun reset(match: Match): Match {
        match.gameNumber = 1
        match.firstPlayerPoints = 0f
        match.secondPlayerPoints = 0f
        match.numberOfGames = 1
        return match
    }
}
