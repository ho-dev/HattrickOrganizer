package core.file.xml

class TeamStats {
    @JvmField
    var teamId = 0
    @JvmField
    var teamName: String? = null
    @JvmField
    var leagueName: String? = null
    @JvmField
    var leagueRank = 0
    @JvmField
    var position = 0
    @JvmField
    var points = 0
    @JvmField
    var goalsFor = 0
    @JvmField
    var goalsAgainst = 0
    var observedRank = 0
    val goalsDiff: Int
        get() = goalsFor - goalsAgainst

    /**
     * Calculates the ranking score for the current team.
     *
     * digit 1 = 10 - Division_Rank
     * digit 2 = 8 - position in Division
     * Digit 3-4 : nb points   (between 0 and 42)
     * digit 5-6-7:  500 + goals difference
     * digit 8-9-10: 500 + goal For
     * digit 11-12-13-14-15 initialized at 00000
     *
     * If no teams have duplicated score, we are done, otherwise for the team with duplicated score, we download teamDetails and
     * digits 11-12-13-14-15:  99 999 - visible rank* with
     * visible rank* = 99 999 if visible rank = 0  (bot team)
     *
     *
     * @return
     */
    fun rankingScore(): Long {
        return (10 - leagueRank) * 100000000000000L + (8 - position) * 10000000000000L + points * 100000000000L + (500 + goalsDiff) * 100000000L + (500 + goalsFor) * 100000L
    }

    override fun toString(): String {
        return "TeamStats[ $teamName rank: $observedRank score: ${rankingScore()} ]"
    }
}
