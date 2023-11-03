package core.file.hrf

import core.HO
import core.constants.TeamConfidence
import core.constants.TeamSpirit
import core.constants.TrainingType
import core.constants.player.PlayerAggressiveness
import core.constants.player.PlayerAgreeability
import core.constants.player.PlayerHonesty
import core.constants.player.PlayerSpeciality
import core.file.xml.MyHashtable
import core.model.enums.MatchType
import core.model.match.MatchLineupTeam
import core.model.match.MatchTacticType
import core.model.match.MatchTeamAttitude
import core.model.match.StyleOfPlay
import core.model.player.IMatchRoleID
import core.util.HOLogger
import core.util.StringUtils
import module.training.Skills.HTSkillID
import module.youth.YouthPlayer
import org.apache.commons.lang3.math.NumberUtils

class HRFStringBuilder {
    private var basicsStringBuilder: StringBuilder? = null
    private var clubStringBuilder: StringBuilder? = null
    private var economyStringBuilder: StringBuilder? = null
    private var lastLineupStringBuilder: StringBuilder? = null
    private var lineupStringBuilder: StringBuilder? = null
    private var playersStringBuilder: StringBuilder? = null
    private var youthPlayersStringBuilder: StringBuilder? = null
    private var teamStringBuilder: StringBuilder? = null
    private var leagueStringBuilder: StringBuilder? = null
    private var arenaStringBuilder: StringBuilder? = null
    private var xtraStringBuilder: StringBuilder? = null
    private var staffStringBuilder: StringBuilder? = null

    fun createHRF(): StringBuilder {
        val ret = StringBuilder()
        if (basicsStringBuilder != null) ret.append(basicsStringBuilder)
        if (leagueStringBuilder != null) ret.append(leagueStringBuilder)
        if (clubStringBuilder != null) ret.append(clubStringBuilder)
        if (teamStringBuilder != null) ret.append(teamStringBuilder)
        if (lineupStringBuilder != null) ret.append(lineupStringBuilder)
        if (economyStringBuilder != null) ret.append(economyStringBuilder)
        if (arenaStringBuilder != null) ret.append(arenaStringBuilder)
        if (playersStringBuilder != null) ret.append(playersStringBuilder)
        if (youthPlayersStringBuilder != null) ret.append(youthPlayersStringBuilder)
        if (xtraStringBuilder != null) ret.append(xtraStringBuilder)
        if (lastLineupStringBuilder != null) ret.append(lastLineupStringBuilder)
        if (staffStringBuilder != null) ret.append(staffStringBuilder)
        return ret
    }

    private fun appendKeyValue(s: StringBuilder, key: String, value: String?) {
        s.append(key).append("=").append(value ?: "").append("\n")
    }

    private fun appendKeyValue(s: StringBuilder, key: String, value: Int?) {
        s.append(key).append("=").append(value ?: "").append("\n")
    }

    private fun appendKeyValue(s: StringBuilder, key: String, value: Double?) {
        s.append(key).append("=").append(value ?: "").append("\n")
    }

    /**
     * Create the arena data.
     */
    fun createArena(arenaDataMap: Map<String?, String?>) {
        arenaStringBuilder = StringBuilder("[arena]\n")
        appendKeyValue(arenaStringBuilder!!, "arenaname", arenaDataMap["ArenaName"])
        appendKeyValue(arenaStringBuilder!!, "arenaid", arenaDataMap["ArenaID"])
        appendKeyValue(arenaStringBuilder!!, "antalStaplats", arenaDataMap["Terraces"])
        appendKeyValue(arenaStringBuilder!!, "antalSitt", arenaDataMap["Basic"])
        appendKeyValue(arenaStringBuilder!!, "antalTak", arenaDataMap["Roof"])
        appendKeyValue(arenaStringBuilder!!, "antalVIP", arenaDataMap["VIP"])
        appendKeyValue(arenaStringBuilder!!, "seatTotal", arenaDataMap["Total"])
        appendKeyValue(arenaStringBuilder!!, "expandingStaplats", arenaDataMap["ExTerraces"])
        appendKeyValue(arenaStringBuilder!!, "expandingSitt", arenaDataMap["ExBasic"])
        appendKeyValue(arenaStringBuilder!!, "expandingTak", arenaDataMap["ExRoof"])
        appendKeyValue(arenaStringBuilder!!, "expandingVIP", arenaDataMap["ExVIP"])
        appendKeyValue(arenaStringBuilder!!, "expandingSseatTotal", arenaDataMap["ExTotal"])
        appendKeyValue(arenaStringBuilder!!, "isExpanding", arenaDataMap["isExpanding"])
        // Achtung bei keiner Erweiterung = 0!
        appendKeyValue(arenaStringBuilder!!, "ExpansionDate", arenaDataMap["ExpansionDate"])
    }

    /**
     * Create the basic data.
     */
    fun createBasics(teamDetailsDataMap: Map<String?, String?>, worldDataMap: Map<String?, String?>) {
        basicsStringBuilder = StringBuilder("[basics]\n")
        appendKeyValue(basicsStringBuilder!!, "application", "HO")
        appendKeyValue(basicsStringBuilder!!, "appversion", HO.VERSION)
        appendKeyValue(basicsStringBuilder!!, "date", teamDetailsDataMap["FetchedDate"])
        appendKeyValue(basicsStringBuilder!!, "season", worldDataMap["Season"])
        appendKeyValue(basicsStringBuilder!!, "seasonOffset", worldDataMap["SeasonOffset"])
        appendKeyValue(basicsStringBuilder!!, "matchround", worldDataMap["MatchRound"])
        appendKeyValue(basicsStringBuilder!!, "teamID", teamDetailsDataMap["TeamID"])
        appendKeyValue(basicsStringBuilder!!, "teamName", teamDetailsDataMap["TeamName"])
        appendKeyValue(basicsStringBuilder!!, "youthTeamID", teamDetailsDataMap["YouthTeamID"])
        appendKeyValue(basicsStringBuilder!!, "youthTeamName", teamDetailsDataMap["YouthTeamName"])
        appendKeyValue(basicsStringBuilder!!, "activationDate", teamDetailsDataMap["ActivationDate"])
        appendKeyValue(basicsStringBuilder!!, "owner", teamDetailsDataMap["Loginname"])
        appendKeyValue(basicsStringBuilder!!, "ownerHomepage", teamDetailsDataMap["HomePage"])
        appendKeyValue(basicsStringBuilder!!, "countryID", worldDataMap["CountryID"])
        appendKeyValue(basicsStringBuilder!!, "leagueID", teamDetailsDataMap["LeagueID"])
        appendKeyValue(basicsStringBuilder!!, "regionID", teamDetailsDataMap["RegionID"])
        appendKeyValue(basicsStringBuilder!!, "hasSupporter", teamDetailsDataMap["HasSupporter"])
        appendKeyValue(basicsStringBuilder!!, "LastLeagueStatisticsMatchRound", 0) //TODO: fix this
        appendKeyValue(basicsStringBuilder!!, "LastLeagueStatisticsSeason", 0) //TODO: fix this
    }

    /**
     * Create the club data.
     */
    fun createClub(
        clubDataMap: Map<String?, String?>,
        economyDataMap: Map<String?, String?>,
        teamDetailsDataMap: Map<String?, String?>
    ) {
        clubStringBuilder = StringBuilder("[club]\n")
        appendKeyValue(clubStringBuilder!!, "hjTranare", clubDataMap["AssistantTrainers"])
        appendKeyValue(clubStringBuilder!!, "psykolog", clubDataMap["Psychologists"])
        appendKeyValue(clubStringBuilder!!, "presstalesman", clubDataMap["PressSpokesmen"])
        appendKeyValue(clubStringBuilder!!, "lakare", clubDataMap["Doctors"])
        appendKeyValue(clubStringBuilder!!, "financialDirectorLevels", clubDataMap["FinancialDirectorLevels"])
        appendKeyValue(clubStringBuilder!!, "formCoachLevels", clubDataMap["FormCoachLevels"])
        appendKeyValue(clubStringBuilder!!, "tacticalAssistantLevels", clubDataMap["TacticalAssistantLevels"])
        appendKeyValue(clubStringBuilder!!, "juniorverksamhet", clubDataMap["YouthLevel"])
        appendKeyValue(clubStringBuilder!!, "undefeated", teamDetailsDataMap["NumberOfUndefeated"])
        appendKeyValue(clubStringBuilder!!, "victories", teamDetailsDataMap["NumberOfVictories"])
        appendKeyValue(clubStringBuilder!!, "fanclub", economyDataMap["FanClubSize"])
        appendKeyValue(clubStringBuilder!!, "GlobalRanking", teamDetailsDataMap["GlobalRanking"])
        appendKeyValue(clubStringBuilder!!, "LeagueRanking", teamDetailsDataMap["LeagueRanking"])
        appendKeyValue(clubStringBuilder!!, "RegionRanking", teamDetailsDataMap["RegionRanking"])
        appendKeyValue(clubStringBuilder!!, "PowerRating", teamDetailsDataMap["PowerRating"])
    }

    /**
     * Add the economy data to the HRF buffer
     */
    fun createEconomy(economyDataMap: Map<String?, String?>) {
        economyStringBuilder = StringBuilder("[economy]\n")
        appendKeyValue(economyStringBuilder!!, "Cash", economyDataMap["Cash"])
        appendKeyValue(economyStringBuilder!!, "ExpectedCash", economyDataMap["ExpectedCash"])
        if (economyDataMap["SponsorsPopularity"] != null) {
            appendKeyValue(economyStringBuilder!!, "SupportersPopularity", economyDataMap["SupportersPopularity"])
            appendKeyValue(economyStringBuilder!!, "SponsorsPopularity", economyDataMap["SponsorsPopularity"])
            appendKeyValue(economyStringBuilder!!, "PlayingMatch", "false")
        } else {
            appendKeyValue(economyStringBuilder!!, "PlayingMatch", "true")
        }

        // recreate defect IncomeTemporary field for compatibility reasons
        val iIncomeTemporary = economyDataMap["IncomeSoldPlayers"]!!.toInt() +
                economyDataMap["IncomeSoldPlayersCommission"]!!.toInt() +
                economyDataMap["IncomeTemporary"]!!.toInt()
        val iCostsTemporary = economyDataMap["CostsBoughtPlayers"]!!.toInt() +
                economyDataMap["CostsArenaBuilding"]!!.toInt()
        val iLastIncomeTemporary = economyDataMap["LastIncomeSoldPlayers"]!!.toInt() +
                economyDataMap["LastIncomeSoldPlayersCommission"]!!.toInt() +
                economyDataMap["LastIncomeTemporary"]!!.toInt()
        val iLastCostsTemporary = economyDataMap["LastCostsBoughtPlayers"]!!.toInt() +
                economyDataMap["LastCostsArenaBuilding"]!!.toInt()
        appendKeyValue(economyStringBuilder!!, "IncomeSpectators", economyDataMap["IncomeSpectators"])
        appendKeyValue(economyStringBuilder!!, "IncomeSponsors", economyDataMap["IncomeSponsors"])
        appendKeyValue(economyStringBuilder!!, "IncomeSponsorsBonus", economyDataMap["IncomeSponsorsBonus"])
        appendKeyValue(economyStringBuilder!!, "IncomeFinancial", economyDataMap["IncomeFinancial"])
        appendKeyValue(economyStringBuilder!!, "IncomeSoldPlayers", economyDataMap["IncomeSoldPlayers"])
        appendKeyValue(
            economyStringBuilder!!,
            "IncomeSoldPlayersCommission",
            economyDataMap["IncomeSoldPlayersCommission"]
        )
        appendKeyValue(
            economyStringBuilder!!,
            "IncomeTemporary",
            iIncomeTemporary
        ) // recreate defect IncomeTemporary field for compatibility reasons
        appendKeyValue(economyStringBuilder!!, "IncomeSum", economyDataMap["IncomeSum"])
        appendKeyValue(economyStringBuilder!!, "CostsArena", economyDataMap["CostsArena"])
        appendKeyValue(economyStringBuilder!!, "CostsPlayers", economyDataMap["CostsPlayers"])
        appendKeyValue(economyStringBuilder!!, "CostsFinancial", economyDataMap["CostsFinancial"])
        appendKeyValue(economyStringBuilder!!, "CostsStaff", economyDataMap["CostsStaff"])
        appendKeyValue(economyStringBuilder!!, "CostsBoughtPlayers", economyDataMap["CostsBoughtPlayers"])
        appendKeyValue(economyStringBuilder!!, "CostsArenaBuilding", economyDataMap["CostsArenaBuilding"])
        appendKeyValue(
            economyStringBuilder!!,
            "CostsTemporary",
            iCostsTemporary
        ) // recreate defect CostsTemporary field for compatibility reasons
        appendKeyValue(economyStringBuilder!!, "CostsYouth", economyDataMap["CostsYouth"])
        appendKeyValue(economyStringBuilder!!, "CostsSum", economyDataMap["CostsSum"])
        appendKeyValue(economyStringBuilder!!, "ExpectedWeeksTotal", economyDataMap["ExpectedWeeksTotal"])
        appendKeyValue(economyStringBuilder!!, "LastIncomeSpectators", economyDataMap["LastIncomeSpectators"])
        appendKeyValue(economyStringBuilder!!, "LastIncomeSponsors", economyDataMap["LastIncomeSponsors"])
        appendKeyValue(economyStringBuilder!!, "LastIncomeSponsorsBonus", economyDataMap["LastIncomeSponsorsBonus"])
        appendKeyValue(economyStringBuilder!!, "LastIncomeFinancial", economyDataMap["LastIncomeFinancial"])
        appendKeyValue(economyStringBuilder!!, "LastIncomeSoldPlayers", economyDataMap["LastIncomeSoldPlayers"])
        appendKeyValue(
            economyStringBuilder!!,
            "LastIncomeSoldPlayersCommission",
            economyDataMap["LastIncomeSoldPlayersCommission"]
        )
        appendKeyValue(
            economyStringBuilder!!,
            "LastIncomeTemporary",
            iLastIncomeTemporary
        ) // recreate defect LastIncomeTemporary field for compatibility reasons
        appendKeyValue(economyStringBuilder!!, "LastIncomeSum", economyDataMap["LastIncomeSum"])
        appendKeyValue(economyStringBuilder!!, "lastCostsArena", economyDataMap["LastCostsArena"])
        appendKeyValue(economyStringBuilder!!, "LastCostsPlayers", economyDataMap["LastCostsPlayers"])
        appendKeyValue(economyStringBuilder!!, "LastCostsFinancial", economyDataMap["LastCostsFinancial"])
        appendKeyValue(economyStringBuilder!!, "lastCostsPersonal", economyDataMap["LastCostsStaff"])
        appendKeyValue(economyStringBuilder!!, "LastCostsBoughtPlayers", economyDataMap["LastCostsBoughtPlayers"])
        appendKeyValue(economyStringBuilder!!, "LastCostsArenaBuilding", economyDataMap["LastCostsArenaBuilding"])
        appendKeyValue(
            economyStringBuilder!!,
            "LastCostsTemporary",
            iLastCostsTemporary
        ) // recreate defect LastCostsTemporary field for compatibility reasons
        appendKeyValue(economyStringBuilder!!, "LastCostsYouth", economyDataMap["LastCostsYouth"])
        appendKeyValue(economyStringBuilder!!, "LastCostsSum", economyDataMap["LastCostsSum"])
        appendKeyValue(economyStringBuilder!!, "LastWeeksTotal", economyDataMap["LastWeeksTotal"])
    }

    /**
     * Create last lineup section.
     */
    fun createLastLineUp(matchLineupTeam: MatchLineupTeam?, teamdetailsDataMap: Map<String?, String?>) {
        lastLineupStringBuilder = StringBuilder("[lastlineup]\n")
        appendKeyValue(lastLineupStringBuilder!!, "trainer", teamdetailsDataMap["TrainerID"])
        try {
            if (matchLineupTeam != null) {
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "installning",
                    MatchTeamAttitude.toInt(matchLineupTeam.matchTeamAttitude)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "tactictype",
                    MatchTacticType.toInt(matchLineupTeam.matchTacticType)
                )
                // The field is coachmodifier in matchOrders and StyleOfPlay in MatchLineup
                // but we both named it styleOfPlay
                appendKeyValue(lastLineupStringBuilder!!, "styleOfPlay", StyleOfPlay.toInt(matchLineupTeam.styleOfPlay))
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "keeper",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.keeper)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "rightBack",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightBack)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "insideBack1",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightCentralDefender)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "insideBack2",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftCentralDefender)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "insideBack3",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.middleCentralDefender)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "leftBack",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftBack)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "rightWinger",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightWinger)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "insideMid1",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightInnerMidfield)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "insideMid2",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftInnerMidfield)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "insideMid3",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.centralInnerMidfield)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "leftWinger",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftWinger)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "forward1",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightForward)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "forward2",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftForward)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "forward3",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.centralForward)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "substBack",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substCD1)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "substInsideMid",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substIM1)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "substWinger",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substWI1)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "substKeeper",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substGK1)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "substForward",
                    getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substFW1)
                )
                appendKeyValue(lastLineupStringBuilder!!, "captain", matchLineupTeam.getLineup().captain)
                appendKeyValue(lastLineupStringBuilder!!, "kicker1", matchLineupTeam.getLineup().kicker)
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behrightBack",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightBack)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behinsideBack1",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightCentralDefender)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behinsideBack2",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftCentralDefender)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behinsideBack3",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.middleCentralDefender)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behleftBack",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftBack)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behrightWinger",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightWinger)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behinsideMid1",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightInnerMidfield)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behinsideMid2",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftInnerMidfield)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behinsideMid3",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.centralInnerMidfield)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behleftWinger",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftWinger)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behforward1",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightForward)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behforward2",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftForward)
                )
                appendKeyValue(
                    lastLineupStringBuilder!!,
                    "behforward3",
                    getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.centralForward)
                )
                var i = 0
                for (sub in matchLineupTeam.substitutions) {
                    if (sub != null) {
                        appendKeyValue(lastLineupStringBuilder!!, "subst" + i + "playerOrderID", sub.playerOrderId)
                        appendKeyValue(lastLineupStringBuilder!!, "subst" + i + "playerIn", sub.objectPlayerID)
                        appendKeyValue(lastLineupStringBuilder!!, "subst" + i + "playerOut", sub.subjectPlayerID)
                        appendKeyValue(lastLineupStringBuilder!!, "subst" + i + "orderType", sub.orderType.id.toInt())
                        appendKeyValue(
                            lastLineupStringBuilder!!,
                            "subst" + i + "matchMinuteCriteria",
                            sub.matchMinuteCriteria
                        )
                        appendKeyValue(lastLineupStringBuilder!!, "subst" + i + "pos", sub.roleId.toInt())
                        appendKeyValue(lastLineupStringBuilder!!, "subst" + i + "behaviour", sub.behaviour.toInt())
                        appendKeyValue(lastLineupStringBuilder!!, "subst" + i + "card", sub.redCardCriteria.id.toInt())
                        appendKeyValue(lastLineupStringBuilder!!, "subst" + i + "standing", sub.standing.id.toInt())
                        i++
                    }
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().debug(
                HRFStringBuilder::class.java,
                "Error(last lineup): $e"
            )
        }
    }

    /**
     * Create the league data.
     */
    fun createLeague(ligaDataMap: Map<String?, String?>) {
        leagueStringBuilder = StringBuilder("[league]\n")
        appendKeyValue(leagueStringBuilder!!, "serie", ligaDataMap["LeagueLevelUnitName"])
        appendKeyValue(leagueStringBuilder!!, "spelade", ligaDataMap["Matches"])
        appendKeyValue(leagueStringBuilder!!, "gjorda", ligaDataMap["GoalsFor"])
        appendKeyValue(leagueStringBuilder!!, "inslappta", ligaDataMap["GoalsAgainst"])
        appendKeyValue(leagueStringBuilder!!, "poang", ligaDataMap["Points"])
        appendKeyValue(leagueStringBuilder!!, "placering", ligaDataMap["Position"])
    }

    /**
     * Creates the lineup data.
     * @param trainerId
     * The playerId of the trainer of the club.
     * @param teamId
     * team id (-1 for lineup templates)
     * @param nextLineup
     * map containing the lineup
     */
    fun createLineUp(trainerId: String?, teamId: Int, nextLineup: Map<String?, String?>?) {
        lineupStringBuilder = StringBuilder("[lineup]\n")
        if (nextLineup != null) {
            val matchId = NumberUtils.toInt(nextLineup["MatchID"], 0)
            val matchType = NumberUtils.toInt(nextLineup["MatchType"], MatchType.NONE.matchTypeId)
            try {
                appendKeyValue(lineupStringBuilder!!, "teamid", teamId)
                appendKeyValue(lineupStringBuilder!!, "matchid", matchId)
                appendKeyValue(lineupStringBuilder!!, "matchtyp", matchType)
                appendKeyValue(lineupStringBuilder!!, "trainer", trainerId)
                appendKeyValue(lineupStringBuilder!!, "installning", nextLineup["Attitude"])
                appendKeyValue(lineupStringBuilder!!, "styleOfPlay", nextLineup["StyleOfPlay"])
                appendKeyValue(lineupStringBuilder!!, "tactictype", nextLineup["TacticType"])
                appendKeyValue(lineupStringBuilder!!, "keeper", getPlayerForNextLineup("KeeperID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "rightBack", getPlayerForNextLineup("RightBackID", nextLineup))
                appendKeyValue(
                    lineupStringBuilder!!,
                    "rightCentralDefender",
                    getPlayerForNextLineup("RightCentralDefenderID", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "leftCentralDefender",
                    getPlayerForNextLineup("LeftCentralDefenderID", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "middleCentralDefender",
                    getPlayerForNextLineup("MiddleCentralDefenderID", nextLineup)
                )
                appendKeyValue(lineupStringBuilder!!, "leftBack", getPlayerForNextLineup("LeftBackID", nextLineup))
                appendKeyValue(
                    lineupStringBuilder!!,
                    "rightwinger",
                    getPlayerForNextLineup("RightWingerID", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "rightInnerMidfield",
                    getPlayerForNextLineup("RightInnerMidfieldID", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "leftInnerMidfield",
                    getPlayerForNextLineup("LeftInnerMidfieldID", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "middleInnerMidfield",
                    getPlayerForNextLineup("CentralInnerMidfieldID", nextLineup)
                )
                appendKeyValue(lineupStringBuilder!!, "leftwinger", getPlayerForNextLineup("LeftWingerID", nextLineup))
                appendKeyValue(
                    lineupStringBuilder!!,
                    "rightForward",
                    getPlayerForNextLineup("RightForwardID", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "leftForward",
                    getPlayerForNextLineup("LeftForwardID", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "centralForward",
                    getPlayerForNextLineup("CentralForwardID", nextLineup)
                )
                appendKeyValue(lineupStringBuilder!!, "substgk1", getPlayerForNextLineup("substGK1ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substgk2", getPlayerForNextLineup("substGK2ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substcd1", getPlayerForNextLineup("substCD1ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substcd2", getPlayerForNextLineup("substCD2ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substwb1", getPlayerForNextLineup("substWB1ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substwb2", getPlayerForNextLineup("substWB2ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substim1", getPlayerForNextLineup("substIM1ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substim2", getPlayerForNextLineup("substIM2ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substwi1", getPlayerForNextLineup("substWI1ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substwi2", getPlayerForNextLineup("substWI2ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substfw1", getPlayerForNextLineup("substFW1ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substfw2", getPlayerForNextLineup("substFW2ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substxt1", getPlayerForNextLineup("substXT1ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "substxt2", getPlayerForNextLineup("substXT2ID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "captain", getPlayerForNextLineup("CaptainID", nextLineup))
                appendKeyValue(lineupStringBuilder!!, "kicker1", getPlayerForNextLineup("KickerID", nextLineup))
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_rightback",
                    getPlayerOrderForNextLineup("RightBackOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_rightCentralDefender",
                    getPlayerOrderForNextLineup("RightCentralDefenderOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_leftCentralDefender",
                    getPlayerOrderForNextLineup("LeftCentralDefenderOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_middleCentralDefender",
                    getPlayerOrderForNextLineup("MiddleCentralDefenderOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_leftBack",
                    getPlayerOrderForNextLineup("LeftBackOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_rightWinger",
                    getPlayerOrderForNextLineup("RightWingerOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_rightInnerMidfield",
                    getPlayerOrderForNextLineup("RightInnerMidfieldOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_leftInnerMidfield",
                    getPlayerOrderForNextLineup("LeftInnerMidfieldOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_centralInnerMidfield",
                    getPlayerOrderForNextLineup("CentralInnerMidfieldOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_leftWinger",
                    getPlayerOrderForNextLineup("LeftWingerOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_rightForward",
                    getPlayerOrderForNextLineup("RightForwardOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_leftForward",
                    getPlayerOrderForNextLineup("LeftForwardOrder", nextLineup)
                )
                appendKeyValue(
                    lineupStringBuilder!!,
                    "order_centralForward",
                    getPlayerOrderForNextLineup("CentralForwardOrder", nextLineup)
                )
                var iSub = -1
                var playerOrderIdString: String?
                while (getMatchOrderInfo(nextLineup, ++iSub, "playerOrderID").also {
                        playerOrderIdString = it
                    } != null) {
                    lineupStringBuilder!!.append(playerOrderIdString)
                        .append(getMatchOrderInfo(nextLineup, iSub, "playerIn"))
                        .append(getMatchOrderInfo(nextLineup, iSub, "playerOut"))
                        .append(getMatchOrderInfo(nextLineup, iSub, "orderType"))
                        .append(getMatchOrderInfo(nextLineup, iSub, "matchMinuteCriteria"))
                        .append(getMatchOrderInfo(nextLineup, iSub, "pos"))
                        .append(getMatchOrderInfo(nextLineup, iSub, "behaviour"))
                        .append(getMatchOrderInfo(nextLineup, iSub, "card"))
                        .append(getMatchOrderInfo(nextLineup, iSub, "standing"))
                }
                for (i in 0..10) {
                    val key = "PenaltyTaker$i"
                    appendKeyValue(lineupStringBuilder!!, "penalty$i", getPlayerForNextLineup(key, nextLineup))
                }
            } catch (e: Exception) {
                HOLogger.instance().debug(
                    HRFStringBuilder::class.java,
                    "Error(lineup): $e"
                )
            }
        }
    }

    /**
     * Create the player data.
     */
    fun createPlayers(matchLineupTeam: MatchLineupTeam?, playersData: List<MyHashtable>?) {
        playersStringBuilder = StringBuilder()
        var i = 0
        while (playersData != null && i < playersData.size) {
            val ht = playersData[i]
            playersStringBuilder!!.append("[player").append(ht["PlayerID"]).append("]\n")
            val firstName = ht["FirstName"]
            val lastName = ht["LastName"]
            val nickName = ht["NickName"]
            if (!nickName!!.isEmpty()) {
                appendKeyValue(playersStringBuilder!!, "name", "$firstName '$nickName' $lastName")
            } else {
                appendKeyValue(playersStringBuilder!!, "name", "$firstName $lastName")
            }
            appendKeyValue(playersStringBuilder!!, "firstname", ht["FirstName"])
            appendKeyValue(playersStringBuilder!!, "nickname", ht["NickName"])
            appendKeyValue(playersStringBuilder!!, "lastname", ht["LastName"])
            appendKeyValue(playersStringBuilder!!, "ald", ht["Age"])
            appendKeyValue(playersStringBuilder!!, "agedays", ht["AgeDays"])
            appendKeyValue(playersStringBuilder!!, "arrivaldate", ht["ArrivalDate"])
            appendKeyValue(playersStringBuilder!!, "ska", ht["InjuryLevel"])
            appendKeyValue(playersStringBuilder!!, "for", ht["PlayerForm"])
            appendKeyValue(playersStringBuilder!!, "uth", ht["StaminaSkill"])
            appendKeyValue(playersStringBuilder!!, "spe", ht["PlaymakerSkill"])
            appendKeyValue(playersStringBuilder!!, "mal", ht["ScorerSkill"])
            appendKeyValue(playersStringBuilder!!, "fra", ht["PassingSkill"])
            appendKeyValue(playersStringBuilder!!, "ytt", ht["WingerSkill"])
            appendKeyValue(playersStringBuilder!!, "fas", ht["SetPiecesSkill"])
            appendKeyValue(playersStringBuilder!!, "bac", ht["DefenderSkill"])
            appendKeyValue(playersStringBuilder!!, "mlv", ht["KeeperSkill"])
            appendKeyValue(playersStringBuilder!!, "rut", ht["Experience"])
            appendKeyValue(playersStringBuilder!!, "loy", ht["Loyalty"])
            appendKeyValue(playersStringBuilder!!, "homegr", ht["MotherClubBonus"])
            appendKeyValue(playersStringBuilder!!, "led", ht["Leadership"])
            appendKeyValue(playersStringBuilder!!, "sal", ht["Salary"])
            appendKeyValue(playersStringBuilder!!, "mkt", ht["MarketValue"])
            appendKeyValue(playersStringBuilder!!, "gev", ht["CareerGoals"])
            appendKeyValue(playersStringBuilder!!, "gtl", ht["LeagueGoals"])
            appendKeyValue(playersStringBuilder!!, "gtc", ht["CupGoals"])
            appendKeyValue(playersStringBuilder!!, "gtt", ht["FriendliesGoals"])
            appendKeyValue(playersStringBuilder!!, "GoalsCurrentTeam", ht["GoalsCurrentTeam"])
            appendKeyValue(playersStringBuilder!!, "MatchesCurrentTeam", ht["MatchesCurrentTeam"])
            appendKeyValue(playersStringBuilder!!, "hat", ht["CareerHattricks"])
            appendKeyValue(playersStringBuilder!!, "CountryID", ht["CountryID"])
            appendKeyValue(playersStringBuilder!!, "warnings", ht["Cards"])
            appendKeyValue(playersStringBuilder!!, "speciality", ht["Specialty"])
            appendKeyValue(
                playersStringBuilder!!,
                "specialityLabel",
                PlayerSpeciality.toString(ht["Specialty"]!!.toInt())
            )
            appendKeyValue(playersStringBuilder!!, "gentleness", ht["Agreeability"])
            appendKeyValue(
                playersStringBuilder!!, "gentlenessLabel", PlayerAgreeability.toString(
                    ht["Agreeability"]!!.toInt()
                )
            )
            appendKeyValue(playersStringBuilder!!, "honesty", ht["Honesty"])
            appendKeyValue(playersStringBuilder!!, "honestyLabel", PlayerHonesty.toString(ht["Honesty"]!!.toInt()))
            appendKeyValue(playersStringBuilder!!, "Aggressiveness", ht["Aggressiveness"])
            appendKeyValue(
                playersStringBuilder!!, "AggressivenessLabel", PlayerAggressiveness.toString(
                    ht["Aggressiveness"]!!.toInt()
                )
            )
            appendKeyValue(playersStringBuilder!!, "TrainerType", ht["TrainerType"])
            appendKeyValue(playersStringBuilder!!, "ContractDate", ht["ContractDate"])
            appendKeyValue(playersStringBuilder!!, "Cost", ht["Cost"])
            appendKeyValue(playersStringBuilder!!, "TrainerSkillLevel", ht["TrainerSkillLevel"])
            appendKeyValue(playersStringBuilder!!, "TrainerStatus", ht["TrainerStatus"])
            appendKeyValue(playersStringBuilder!!, "LastMatch_Date", ht["LastMatch_Date"])
            appendKeyValue(playersStringBuilder!!, "LastMatch_Rating", ht["LastMatch_Rating"])
            appendKeyValue(playersStringBuilder!!, "LastMatch_id", ht["LastMatch_id"])
            appendKeyValue(playersStringBuilder!!, "LastMatch_PositionCode", ht["LastMatch_PositionCode"])
            appendKeyValue(playersStringBuilder!!, "LastMatch_PlayedMinutes", ht["LastMatch_PlayedMinutes"])
            appendKeyValue(playersStringBuilder!!, "LastMatch_RatingEndOfGame", ht["LastMatch_RatingEndOfGame"])
            val lastMatchType = ht.getOrDefault("LastMatch_Type", "0")
            appendKeyValue(playersStringBuilder!!, "LastMatch_Type", lastMatchType)
            if (matchLineupTeam?.getPlayerByID(ht["PlayerID"]!!.toInt()) != null && matchLineupTeam.getPlayerByID(
                    ht["PlayerID"]!!.toInt()
                ).rating >= 0
            ) {
                appendKeyValue(
                    playersStringBuilder!!,
                    "rating",
                    (matchLineupTeam.getPlayerByID(ht["PlayerID"]!!.toInt()).rating * 2).toInt()
                )
            } else {
                appendKeyValue(playersStringBuilder!!, "rating", "0")
            }
            appendKeyValueIfNotNull(ht, playersStringBuilder!!, "PlayerNumber", "")
            appendKeyValue(playersStringBuilder!!, "TransferListed", ht["TransferListed"])
            appendKeyValue(playersStringBuilder!!, "NationalTeamID", ht["NationalTeamID"])
            appendKeyValue(playersStringBuilder!!, "Caps", ht["Caps"])
            appendKeyValue(playersStringBuilder!!, "CapsU20", ht["CapsU20"])
            appendKeyValue(playersStringBuilder!!, "PlayerCategoryId", ht["PlayerCategoryId"])
            // We transport all data through the hrf file.
            // We have to replace the new lines
            appendKeyValue(playersStringBuilder!!, "Statement", serializeMultiLine(ht["Statement"]))
            appendKeyValue(playersStringBuilder!!, "OwnerNotes", serializeMultiLine(ht["OwnerNotes"]))
            appendKeyValueIfNotNull(ht, playersStringBuilder!!, "LineupDisabled", "false")
            i++
        }
    }

    private fun appendKeyValueIfNotNull(ht: MyHashtable, s: StringBuilder, key: String, defaultValue: String) {
        var property = ht[key]
        if (property.isNullOrEmpty()) {
            property = defaultValue
        }
        appendKeyValue(s, key, property)
    }

    private fun serializeMultiLine(value: String?): String {
        return value?.replace("\\R".toRegex(), "<br>") ?: ""
    }

    /**
     * Append youth player data to buffer
     */
    fun appendYouthPlayers(playersData: List<MyHashtable>) {
        youthPlayersStringBuilder = StringBuilder()
        for (player in playersData) {
            youthPlayersStringBuilder!!.append("[youthplayer").append(player["YouthPlayerID"]).append(']').append('\n')
            appendHRFLine(youthPlayersStringBuilder!!, player, "FirstName")
            appendHRFLine(youthPlayersStringBuilder!!, player, "NickName")
            appendHRFLine(youthPlayersStringBuilder!!, player, "LastName")
            appendHRFLine(youthPlayersStringBuilder!!, player, "Age")
            appendHRFLine(youthPlayersStringBuilder!!, player, "AgeDays")
            appendHRFLine(youthPlayersStringBuilder!!, player, "ArrivalDate")
            appendHRFLine(youthPlayersStringBuilder!!, player, "CanBePromotedIn")
            appendHRFLine(youthPlayersStringBuilder!!, player, "PlayerNumber")
            appendHRFLine(youthPlayersStringBuilder!!, player, "Statement")
            appendHRFLine(youthPlayersStringBuilder!!, player, "OwnerNotes")
            appendHRFLine(youthPlayersStringBuilder!!, player, "PlayerCategoryID")
            appendHRFLine(youthPlayersStringBuilder!!, player, "Cards")
            appendHRFLine(youthPlayersStringBuilder!!, player, "InjuryLevel")
            appendHRFLine(youthPlayersStringBuilder!!, player, "Specialty")
            appendHRFLine(youthPlayersStringBuilder!!, player, "CareerGoals")
            appendHRFLine(youthPlayersStringBuilder!!, player, "CareerHattricks")
            appendHRFLine(youthPlayersStringBuilder!!, player, "LeagueGoals")
            appendHRFLine(youthPlayersStringBuilder!!, player, "FriendlyGoals")
            for (skillId in YouthPlayer.skillIds) {
                appendHRFSkillLines(youthPlayersStringBuilder!!, player, skillId)
            }
            appendHRFLine(youthPlayersStringBuilder!!, player, "ScoutId")
            appendHRFLine(youthPlayersStringBuilder!!, player, "ScoutName")
            appendHRFLine(youthPlayersStringBuilder!!, player, "ScoutingRegionID")
            var i = 0
            while (appendScoutComment(youthPlayersStringBuilder!!, player, i)) {
                i++
            }
            appendHRFLine(youthPlayersStringBuilder!!, player, "YouthMatchID")
            appendHRFLine(youthPlayersStringBuilder!!, player, "YouthMatchDate")
            appendHRFLine(youthPlayersStringBuilder!!, player, "PositionCode")
            appendHRFLine(youthPlayersStringBuilder!!, player, "PlayedMinutes")
            appendHRFLine(youthPlayersStringBuilder!!, player, "Rating")
        }
    }

    private fun appendScoutComment(buffer: StringBuilder, player: MyHashtable, i: Int): Boolean {
        val prefix = "ScoutComment$i"
        val text = player[prefix + "Text"]
        if (text != null) {
            appendHRFLine(buffer, player, prefix + "Text")
            appendHRFLine(buffer, player, prefix + "Type")
            appendHRFLine(buffer, player, prefix + "Variation")
            appendHRFLine(buffer, player, prefix + "SkillType")
            appendHRFLine(buffer, player, prefix + "SkillLevel")
            return true
        }
        return false
    }

    private fun appendHRFSkillLines(buffer: StringBuilder, player: MyHashtable, skillId: HTSkillID) {
        var skill = skillId.toString() + "Skill"
        appendHRFLine(buffer, player, skill)
        appendHRFLine(buffer, player, skill + "IsAvailable")
        appendHRFLine(buffer, player, skill + "IsMaxReached")
        appendHRFLine(buffer, player, skill + "MayUnlock")
        skill += "Max"
        appendHRFLine(buffer, player, skill)
        appendHRFLine(buffer, player, skill + "IsAvailable")
        appendHRFLine(buffer, player, skill + "MayUnlock")
    }

    private fun appendHRFLine(buffer: StringBuilder, player: MyHashtable, key: String) {
        appendKeyValue(buffer, key, player[key])
    }

    /**
     * Create team related data (training, confidence, formation experience,
     * etc.).
     */
    fun createTeam(trainingDataMap: Map<String?, String?>) {
        teamStringBuilder = StringBuilder("[team]\n")
        appendKeyValue(teamStringBuilder!!, "trLevel", trainingDataMap["TrainingLevel"])
        appendKeyValue(teamStringBuilder!!, "staminaTrainingPart", trainingDataMap["StaminaTrainingPart"])
        appendKeyValue(teamStringBuilder!!, "trTypeValue", trainingDataMap["TrainingType"])
        val training = NumberUtils.toInt(trainingDataMap["TrainingType"], 0)
        appendKeyValue(teamStringBuilder!!, "trType", TrainingType.toString(training))
        if (trainingDataMap["Morale"] != null && trainingDataMap["SelfConfidence"] != null) {
            appendKeyValue(teamStringBuilder!!, "stamningValue", trainingDataMap["Morale"])
            appendKeyValue(teamStringBuilder!!, "stamning", TeamSpirit.toString(trainingDataMap["Morale"]!!.toInt()))
            appendKeyValue(teamStringBuilder!!, "sjalvfortroendeValue", trainingDataMap["SelfConfidence"])
            appendKeyValue(
                teamStringBuilder!!, "sjalvfortroende", TeamConfidence.toString(
                    trainingDataMap["SelfConfidence"]!!.toInt()
                )
            )
        } else {
            appendKeyValue(teamStringBuilder!!, "playingMatch", "true")
        }
        appendKeyValue(teamStringBuilder!!, "exper433", trainingDataMap["Experience433"])
        appendKeyValue(teamStringBuilder!!, "exper451", trainingDataMap["Experience451"])
        appendKeyValue(teamStringBuilder!!, "exper352", trainingDataMap["Experience352"])
        appendKeyValue(teamStringBuilder!!, "exper532", trainingDataMap["Experience532"])
        appendKeyValue(teamStringBuilder!!, "exper343", trainingDataMap["Experience343"])
        appendKeyValue(teamStringBuilder!!, "exper541", trainingDataMap["Experience541"])
        appendKeyValue(teamStringBuilder!!, "exper442", trainingDataMap["Experience442"])
        appendKeyValue(teamStringBuilder!!, "exper523", trainingDataMap["Experience523"])
        appendKeyValue(teamStringBuilder!!, "exper550", trainingDataMap["Experience550"])
        appendKeyValue(teamStringBuilder!!, "exper253", trainingDataMap["Experience253"])
    }

    /**
     * Create the world data.
     */
    fun createWorld(
        clubDataMap: Map<String?, String?>,
        teamDetailsDataMap: Map<String?, String?>,
        trainingDataMap: Map<String?, String?>,
        worldDataMap: Map<String?, String?>
    ) {
        xtraStringBuilder = StringBuilder("[xtra]\n")
        appendKeyValue(xtraStringBuilder!!, "TrainingDate", worldDataMap["TrainingDate"])
        appendKeyValue(xtraStringBuilder!!, "EconomyDate", worldDataMap["EconomyDate"])
        appendKeyValue(xtraStringBuilder!!, "SeriesMatchDate", worldDataMap["SeriesMatchDate"])
        appendKeyValue(xtraStringBuilder!!, "CountryId", worldDataMap["CountryID"])
        var currencyRate = worldDataMap["CurrencyRate"]
        if (!StringUtils.isEmpty(currencyRate)) {
            currencyRate = currencyRate!!.replace(',', '.')
        }
        appendKeyValue(xtraStringBuilder!!, "CurrencyRate", currencyRate)
        appendKeyValue(xtraStringBuilder!!, "LogoURL", teamDetailsDataMap["LogoURL"])
        appendKeyValue(xtraStringBuilder!!, "HasPromoted", clubDataMap["HasPromoted"])
        appendKeyValue(xtraStringBuilder!!, "TrainerID", trainingDataMap["TrainerID"])
        appendKeyValue(xtraStringBuilder!!, "TrainerName", trainingDataMap["TrainerName"])
        appendKeyValue(xtraStringBuilder!!, "ArrivalDate", trainingDataMap["ArrivalDate"])
        appendKeyValue(xtraStringBuilder!!, "LeagueLevelUnitID", teamDetailsDataMap["LeagueLevelUnitID"])
    }

    fun createStaff(staffList: List<MyHashtable>?) {
        staffStringBuilder = StringBuilder("[staff]\n")
        var i = 0
        while (staffList != null && i < staffList.size) {
            val hash = staffList[i]
            appendKeyValue(staffStringBuilder!!, "staff" + i + "Name", hash["Name"])
            appendKeyValue(staffStringBuilder!!, "staff" + i + "StaffId", hash["StaffId"])
            appendKeyValue(staffStringBuilder!!, "staff" + i + "StaffType", hash["StaffType"])
            appendKeyValue(staffStringBuilder!!, "staff" + i + "StaffLevel", hash["StaffLevel"])
            appendKeyValue(staffStringBuilder!!, "staff" + i + "Cost", hash["Cost"])
            i++
        }
    }

    companion object {
        private fun getPlayerIdByPositionValue(team: MatchLineupTeam, position: Int): String {
            val matchLineupPosition = team.getPlayerByPosition(position)
            return matchLineupPosition?.playerId?.toString() ?: "0"
        }

        private fun getBehaviourByPositionValue(team: MatchLineupTeam, position: Int): String {
            val matchLineupPosition = team.getPlayerByPosition(position)
            return matchLineupPosition?.behaviour?.toString() ?: "0"
        }

        private fun getMatchOrderInfo(nextLineup: Map<String?, String?>, i: Int, key: String): String? {
            val _key = "subst$i$key"
            val value = nextLineup[_key]
            return if (value != null) "$_key=$value\n" else null
        }

        private fun getPlayerOrderForNextLineup(
            position: String,
            map: Map<*, *>?
        ): String {
            if (map != null) {
                var ret = map[position] as String?
                if (ret != null) {
                    ret = ret.trim { it <= ' ' }
                    if ("null" != ret && ret.isNotEmpty()) {
                        return ret.trim { it <= ' ' }
                    }
                }
            }
            return "0"
        }

        private fun getPlayerForNextLineup(position: String, next: Map<*, *>?): String {
            if (next != null) {
                val ret = next[position]
                if (ret != null) {
                    return ret.toString()
                }
            }
            return "0"
        }
    }
}
