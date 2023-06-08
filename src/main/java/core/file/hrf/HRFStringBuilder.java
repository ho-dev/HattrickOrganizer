package core.file.hrf;

import core.HO;
import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.constants.TrainingType;
import core.constants.player.PlayerAggressiveness;
import core.constants.player.PlayerAgreeability;
import core.constants.player.PlayerHonesty;
import core.constants.player.PlayerSpeciality;
import core.file.xml.MyHashtable;
import core.model.enums.MatchType;
import core.model.match.MatchLineupTeam;
import core.model.match.MatchTacticType;
import core.model.match.MatchTeamAttitude;
import core.model.match.StyleOfPlay;
import core.model.player.IMatchRoleID;
import core.util.HOLogger;
import core.util.StringUtils;
import module.lineup.substitution.model.Substitution;
import module.training.Skills;
import module.youth.YouthPlayer;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Map;

public class HRFStringBuilder {
    private StringBuilder basicsStringBuilder;
    private StringBuilder clubStringBuilder;
    private StringBuilder economyStringBuilder;
    private StringBuilder lastLineupStringBuilder;
    private StringBuilder lineupStringBuilder;
    private StringBuilder playersStringBuilder;
    private StringBuilder youthPlayersStringBuilder;
    private StringBuilder teamStringBuilder;
    private StringBuilder leagueStringBuilder;
    private StringBuilder arenaStringBuilder;
    private StringBuilder xtraStringBuilder;
    private StringBuilder staffStringBuilder;

    public StringBuilder createHRF() {
        StringBuilder ret = new StringBuilder();
        if (basicsStringBuilder != null) ret.append(basicsStringBuilder);
        if (leagueStringBuilder != null) ret.append(leagueStringBuilder);
        if (clubStringBuilder != null) ret.append(clubStringBuilder);
        if (teamStringBuilder != null) ret.append(teamStringBuilder);
        if (lineupStringBuilder != null) ret.append(lineupStringBuilder);
        if (economyStringBuilder != null) ret.append(economyStringBuilder);
        if (arenaStringBuilder != null) ret.append(arenaStringBuilder);
        if (playersStringBuilder != null) ret.append(playersStringBuilder);
        if (youthPlayersStringBuilder != null) ret.append(youthPlayersStringBuilder);
        if (xtraStringBuilder != null) ret.append(xtraStringBuilder);
        if (lastLineupStringBuilder != null) ret.append(lastLineupStringBuilder);
        if (staffStringBuilder != null) ret.append(staffStringBuilder);
        return ret;
    }

    private void appendKeyValue(StringBuilder s, String key, String value){
        s.append(key).append("=").append(value!=null?value:"").append("\n");
    }
    private void appendKeyValue(StringBuilder s, String key, Integer value){
        s.append(key).append("=").append(value!=null?value:"").append("\n");
    }
    private void appendKeyValue(StringBuilder s, String key, Double value){
        s.append(key).append("=").append(value!=null?value:"").append("\n");
    }

    /**
     * Create the arena data.
     */
    public void createArena(Map<String, String> arenaDataMap) {
        arenaStringBuilder = new StringBuilder("[arena]\n");
        appendKeyValue(arenaStringBuilder, "arenaname", arenaDataMap.get("ArenaName"));
        appendKeyValue(arenaStringBuilder, "arenaid", arenaDataMap.get("ArenaID"));
        appendKeyValue(arenaStringBuilder, "antalStaplats", arenaDataMap.get("Terraces"));
        appendKeyValue(arenaStringBuilder, "antalSitt", arenaDataMap.get("Basic"));
        appendKeyValue(arenaStringBuilder, "antalTak", arenaDataMap.get("Roof"));
        appendKeyValue(arenaStringBuilder, "antalVIP", arenaDataMap.get("VIP"));
        appendKeyValue(arenaStringBuilder, "seatTotal", arenaDataMap.get("Total"));
        appendKeyValue(arenaStringBuilder, "expandingStaplats", arenaDataMap.get("ExTerraces"));
        appendKeyValue(arenaStringBuilder, "expandingSitt", arenaDataMap.get("ExBasic"));
        appendKeyValue(arenaStringBuilder, "expandingTak", arenaDataMap.get("ExRoof"));
        appendKeyValue(arenaStringBuilder, "expandingVIP", arenaDataMap.get("ExVIP"));
        appendKeyValue(arenaStringBuilder, "expandingSseatTotal", arenaDataMap.get("ExTotal"));
        appendKeyValue(arenaStringBuilder, "isExpanding", arenaDataMap.get("isExpanding"));
        // Achtung bei keiner Erweiterung = 0!
        appendKeyValue(arenaStringBuilder, "ExpansionDate", arenaDataMap.get("ExpansionDate"));
    }

    /**
     * Create the basic data.
     */
    public void createBasics(Map<String, String> teamdetailsDataMap, Map<String, String> worldDataMap) {
        basicsStringBuilder = new StringBuilder("[basics]\n");
        appendKeyValue(basicsStringBuilder, "application", "HO");
        appendKeyValue(basicsStringBuilder, "appversion", HO.VERSION);
        appendKeyValue(basicsStringBuilder, "date", teamdetailsDataMap.get("FetchedDate"));
        appendKeyValue(basicsStringBuilder, "season", worldDataMap.get("Season"));
        appendKeyValue(basicsStringBuilder, "seasonOffset", worldDataMap.get("SeasonOffset"));
        appendKeyValue(basicsStringBuilder, "matchround", worldDataMap.get("MatchRound"));
        appendKeyValue(basicsStringBuilder, "teamID", teamdetailsDataMap.get("TeamID"));
        appendKeyValue(basicsStringBuilder, "teamName", teamdetailsDataMap.get("TeamName"));
        appendKeyValue(basicsStringBuilder, "youthTeamID", teamdetailsDataMap.get("YouthTeamID"));
        appendKeyValue(basicsStringBuilder, "youthTeamName", teamdetailsDataMap.get("YouthTeamName"));
        appendKeyValue(basicsStringBuilder, "activationDate", teamdetailsDataMap.get("ActivationDate"));
        appendKeyValue(basicsStringBuilder, "owner", teamdetailsDataMap.get("Loginname"));
        appendKeyValue(basicsStringBuilder, "ownerHomepage", teamdetailsDataMap.get("HomePage"));
        appendKeyValue(basicsStringBuilder, "countryID", worldDataMap.get("CountryID"));
        appendKeyValue(basicsStringBuilder, "leagueID", teamdetailsDataMap.get("LeagueID"));
        appendKeyValue(basicsStringBuilder, "regionID", teamdetailsDataMap.get("RegionID"));
        appendKeyValue(basicsStringBuilder, "hasSupporter", teamdetailsDataMap.get("HasSupporter"));
        appendKeyValue(basicsStringBuilder, "LastLeagueStatisticsMatchRound", 0);        //TODO: fix this
        appendKeyValue(basicsStringBuilder, "LastLeagueStatisticsSeason", 0);        //TODO: fix this
    }

    /**
     * Create the club data.
     */
    public void createClub(Map<String, String> clubDataMap, Map<String, String> economyDataMap, Map<String, String> teamdetailsDataMap) {
        clubStringBuilder = new StringBuilder("[club]\n");
        appendKeyValue(clubStringBuilder, "hjTranare", clubDataMap.get("AssistantTrainers"));
        appendKeyValue(clubStringBuilder, "psykolog", clubDataMap.get("Psychologists"));
        appendKeyValue(clubStringBuilder, "presstalesman", clubDataMap.get("PressSpokesmen"));
        appendKeyValue(clubStringBuilder, "lakare", clubDataMap.get("Doctors"));
        appendKeyValue(clubStringBuilder, "financialDirectorLevels", clubDataMap.get("FinancialDirectorLevels"));
        appendKeyValue(clubStringBuilder, "formCoachLevels", clubDataMap.get("FormCoachLevels"));
        appendKeyValue(clubStringBuilder, "tacticalAssistantLevels", clubDataMap.get("TacticalAssistantLevels"));
        appendKeyValue(clubStringBuilder, "juniorverksamhet", clubDataMap.get("YouthLevel"));
        appendKeyValue(clubStringBuilder, "undefeated", teamdetailsDataMap.get("NumberOfUndefeated"));
        appendKeyValue(clubStringBuilder, "victories", teamdetailsDataMap.get("NumberOfVictories"));
        appendKeyValue(clubStringBuilder, "fanclub", economyDataMap.get("FanClubSize"));
        appendKeyValue(clubStringBuilder, "GlobalRanking", teamdetailsDataMap.get("GlobalRanking"));
        appendKeyValue(clubStringBuilder, "LeagueRanking", teamdetailsDataMap.get("LeagueRanking"));
        appendKeyValue(clubStringBuilder, "RegionRanking", teamdetailsDataMap.get("RegionRanking"));
        appendKeyValue(clubStringBuilder, "PowerRating", teamdetailsDataMap.get("PowerRating"));
    }

    /**
     * Add the economy data to the HRF buffer
     */
    public void createEconomy(Map<String, String> economyDataMap) {
        economyStringBuilder = new StringBuilder("[economy]\n");
        appendKeyValue(economyStringBuilder, "Cash", economyDataMap.get("Cash"));
        appendKeyValue(economyStringBuilder, "ExpectedCash", economyDataMap.get("ExpectedCash"));

        if (economyDataMap.get("SponsorsPopularity") != null) {
            appendKeyValue(economyStringBuilder, "SupportersPopularity", economyDataMap.get("SupportersPopularity"));
            appendKeyValue(economyStringBuilder, "SponsorsPopularity", economyDataMap.get("SponsorsPopularity"));
            appendKeyValue(economyStringBuilder, "PlayingMatch", "false");
        } else {
            appendKeyValue(economyStringBuilder, "PlayingMatch", "true");
        }

        // recreate defect IncomeTemporary field for compatibility reasons
        int iIncomeTemporary = Integer.parseInt(economyDataMap.get("IncomeSoldPlayers")) +
                Integer.parseInt(economyDataMap.get("IncomeSoldPlayersCommission")) +
                Integer.parseInt(economyDataMap.get("IncomeTemporary"));
        int iCostsTemporary = Integer.parseInt(economyDataMap.get("CostsBoughtPlayers")) + Integer.parseInt(economyDataMap.get("CostsArenaBuilding"));
        int iLastIncomeTemporary = Integer.parseInt(economyDataMap.get("LastIncomeSoldPlayers")) +
                Integer.parseInt(economyDataMap.get("LastIncomeSoldPlayersCommission")) +
                Integer.parseInt(economyDataMap.get("LastIncomeTemporary"));
        int iLastCostsTemporary = Integer.parseInt(economyDataMap.get("LastCostsBoughtPlayers")) + Integer.parseInt(economyDataMap.get("LastCostsArenaBuilding"));

        appendKeyValue(economyStringBuilder, "IncomeSpectators", economyDataMap.get("IncomeSpectators"));
        appendKeyValue(economyStringBuilder, "IncomeSponsors", economyDataMap.get("IncomeSponsors"));
        appendKeyValue(economyStringBuilder, "IncomeSponsorsBonus", economyDataMap.get("IncomeSponsorsBonus"));
        appendKeyValue(economyStringBuilder, "IncomeFinancial", economyDataMap.get("IncomeFinancial"));
        appendKeyValue(economyStringBuilder, "IncomeSoldPlayers", economyDataMap.get("IncomeSoldPlayers"));
        appendKeyValue(economyStringBuilder, "IncomeSoldPlayersCommission", economyDataMap.get("IncomeSoldPlayersCommission"));
        appendKeyValue(economyStringBuilder, "IncomeTemporary", iIncomeTemporary);  // recreate defect IncomeTemporary field for compatibility reasons
        appendKeyValue(economyStringBuilder, "IncomeSum", economyDataMap.get("IncomeSum"));
        appendKeyValue(economyStringBuilder, "CostsArena", economyDataMap.get("CostsArena"));
        appendKeyValue(economyStringBuilder, "CostsPlayers", economyDataMap.get("CostsPlayers"));
        appendKeyValue(economyStringBuilder, "CostsFinancial", economyDataMap.get("CostsFinancial"));
        appendKeyValue(economyStringBuilder, "CostsStaff", economyDataMap.get("CostsStaff"));
        appendKeyValue(economyStringBuilder, "CostsBoughtPlayers", economyDataMap.get("CostsBoughtPlayers"));
        appendKeyValue(economyStringBuilder, "CostsArenaBuilding", economyDataMap.get("CostsArenaBuilding"));
        appendKeyValue(economyStringBuilder, "CostsTemporary", iCostsTemporary); // recreate defect CostsTemporary field for compatibility reasons
        appendKeyValue(economyStringBuilder, "CostsYouth", economyDataMap.get("CostsYouth"));
        appendKeyValue(economyStringBuilder, "CostsSum", economyDataMap.get("CostsSum"));
        appendKeyValue(economyStringBuilder, "ExpectedWeeksTotal", economyDataMap.get("ExpectedWeeksTotal"));
        appendKeyValue(economyStringBuilder, "LastIncomeSpectators", economyDataMap.get("LastIncomeSpectators"));
        appendKeyValue(economyStringBuilder, "LastIncomeSponsors", economyDataMap.get("LastIncomeSponsors"));
        appendKeyValue(economyStringBuilder, "LastIncomeSponsorsBonus", economyDataMap.get("LastIncomeSponsorsBonus"));
        appendKeyValue(economyStringBuilder, "LastIncomeFinancial", economyDataMap.get("LastIncomeFinancial"));
        appendKeyValue(economyStringBuilder, "LastIncomeSoldPlayers", economyDataMap.get("LastIncomeSoldPlayers"));
        appendKeyValue(economyStringBuilder, "LastIncomeSoldPlayersCommission", economyDataMap.get("LastIncomeSoldPlayersCommission"));
        appendKeyValue(economyStringBuilder, "LastIncomeTemporary", iLastIncomeTemporary);  // recreate defect LastIncomeTemporary field for compatibility reasons
        appendKeyValue(economyStringBuilder, "LastIncomeSum", economyDataMap.get("LastIncomeSum"));
        appendKeyValue(economyStringBuilder, "lastCostsArena", economyDataMap.get("LastCostsArena"));
        appendKeyValue(economyStringBuilder, "LastCostsPlayers", economyDataMap.get("LastCostsPlayers"));
        appendKeyValue(economyStringBuilder, "LastCostsFinancial", economyDataMap.get("LastCostsFinancial"));
        appendKeyValue(economyStringBuilder, "lastCostsPersonal", economyDataMap.get("LastCostsStaff"));
        appendKeyValue(economyStringBuilder, "LastCostsBoughtPlayers", economyDataMap.get("LastCostsBoughtPlayers"));
        appendKeyValue(economyStringBuilder, "LastCostsArenaBuilding", economyDataMap.get("LastCostsArenaBuilding"));
        appendKeyValue(economyStringBuilder, "LastCostsTemporary", iLastCostsTemporary); // recreate defect LastCostsTemporary field for compatibility reasons
        appendKeyValue(economyStringBuilder, "LastCostsYouth", economyDataMap.get("LastCostsYouth"));
        appendKeyValue(economyStringBuilder, "LastCostsSum", economyDataMap.get("LastCostsSum"));
        appendKeyValue(economyStringBuilder, "LastWeeksTotal", economyDataMap.get("LastWeeksTotal"));
    }

    private static String getPlayerIdByPositionValue(MatchLineupTeam team, int position){
        var matchLineupPosition = team.getPlayerByPosition(position);
        if ( matchLineupPosition != null){
            return String.valueOf(matchLineupPosition.getPlayerId());
        }
        return "0";
    }

    private static String getBehaviourByPositionValue(MatchLineupTeam team, int position){
        var matchLineupPosition = team.getPlayerByPosition(position);
        if ( matchLineupPosition != null){
            return String.valueOf(matchLineupPosition.getBehaviour());
        }
        return "0";
    }

    /**
     * Create last lineup section.
     */
    public void createLastLineUp(MatchLineupTeam matchLineupTeam, Map<String, String> teamdetailsDataMap) {
        lastLineupStringBuilder = new StringBuilder("[lastlineup]\n");

        appendKeyValue(lastLineupStringBuilder, "trainer", teamdetailsDataMap.get("TrainerID"));

        try {
            if (matchLineupTeam != null ) {
                appendKeyValue(lastLineupStringBuilder, "installning", MatchTeamAttitude.toInt(matchLineupTeam.getMatchTeamAttitude()));
                appendKeyValue(lastLineupStringBuilder, "tactictype", MatchTacticType.toInt(matchLineupTeam.getMatchTacticType()));
                // The field is coachmodifier in matchOrders and StyleOfPlay in MatchLineup
                // but we both named it styleOfPlay
                appendKeyValue(lastLineupStringBuilder, "styleOfPlay", StyleOfPlay.toInt(matchLineupTeam.getStyleOfPlay()));
                appendKeyValue(lastLineupStringBuilder, "keeper", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.keeper));
                appendKeyValue(lastLineupStringBuilder, "rightBack", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightBack));
                appendKeyValue(lastLineupStringBuilder, "insideBack1", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightCentralDefender));
                appendKeyValue(lastLineupStringBuilder, "insideBack2", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftCentralDefender));
                appendKeyValue(lastLineupStringBuilder, "insideBack3", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.middleCentralDefender));
                appendKeyValue(lastLineupStringBuilder, "leftBack", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftBack));
                appendKeyValue(lastLineupStringBuilder, "rightWinger",getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightWinger));
                appendKeyValue(lastLineupStringBuilder, "insideMid1", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightInnerMidfield));
                appendKeyValue(lastLineupStringBuilder, "insideMid2", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftInnerMidfield));
                appendKeyValue(lastLineupStringBuilder, "insideMid3", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.centralInnerMidfield));
                appendKeyValue(lastLineupStringBuilder, "leftWinger", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftWinger));
                appendKeyValue(lastLineupStringBuilder, "forward1", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.rightForward));
                appendKeyValue(lastLineupStringBuilder, "forward2", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.leftForward));
                appendKeyValue(lastLineupStringBuilder, "forward3", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.centralForward));
                appendKeyValue(lastLineupStringBuilder, "substBack", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substCD1));
                appendKeyValue(lastLineupStringBuilder, "substInsideMid", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substIM1));
                appendKeyValue(lastLineupStringBuilder, "substWinger", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substWI1));
                appendKeyValue(lastLineupStringBuilder, "substKeeper", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substGK1));
                appendKeyValue(lastLineupStringBuilder, "substForward", getPlayerIdByPositionValue(matchLineupTeam, IMatchRoleID.substFW1));
                appendKeyValue(lastLineupStringBuilder, "captain", matchLineupTeam.getLineup().getCaptain());
                appendKeyValue(lastLineupStringBuilder, "kicker1", matchLineupTeam.getLineup().getKicker());

                appendKeyValue(lastLineupStringBuilder, "behrightBack", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightBack));
                appendKeyValue(lastLineupStringBuilder, "behinsideBack1", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightCentralDefender));
                appendKeyValue(lastLineupStringBuilder, "behinsideBack2", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftCentralDefender));
                appendKeyValue(lastLineupStringBuilder, "behinsideBack3", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.middleCentralDefender));
                appendKeyValue(lastLineupStringBuilder, "behleftBack", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftBack));
                appendKeyValue(lastLineupStringBuilder, "behrightWinger", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightWinger));
                appendKeyValue(lastLineupStringBuilder, "behinsideMid1", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightInnerMidfield));
                appendKeyValue(lastLineupStringBuilder, "behinsideMid2", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftInnerMidfield));
                appendKeyValue(lastLineupStringBuilder, "behinsideMid3", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.centralInnerMidfield));
                appendKeyValue(lastLineupStringBuilder, "behleftWinger", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftWinger));
                appendKeyValue(lastLineupStringBuilder, "behforward1", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.rightForward));
                appendKeyValue(lastLineupStringBuilder, "behforward2", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.leftForward));
                appendKeyValue(lastLineupStringBuilder, "behforward3", getBehaviourByPositionValue(matchLineupTeam, IMatchRoleID.centralForward));

                int i = 0;
                for (Substitution sub : matchLineupTeam.getSubstitutions()) {
                    if (sub != null) {
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"playerOrderID", sub.getPlayerOrderId());
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"playerIn", sub.getObjectPlayerID());
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"playerOut", sub.getSubjectPlayerID());
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"orderType", (int) sub.getOrderType().getId());
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"matchMinuteCriteria", (int) sub.getMatchMinuteCriteria());
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"pos", (int) sub.getRoleId());
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"behaviour", (int) sub.getBehaviour());
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"card", (int) sub.getRedCardCriteria().getId());
                        appendKeyValue(lastLineupStringBuilder, "subst"+i+"standing", (int) sub.getStanding().getId());
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            HOLogger.instance().debug(HRFStringBuilder.class,
                    "Error(last lineup): " + e);
        }
    }

    /**
     * Create the league data.
     */
    public void createLeague(Map<String, String> ligaDataMap) {
        leagueStringBuilder = new StringBuilder("[league]\n");
        appendKeyValue(leagueStringBuilder,"serie",ligaDataMap.get("LeagueLevelUnitName"));
        appendKeyValue(leagueStringBuilder,"spelade",ligaDataMap.get("Matches"));
        appendKeyValue(leagueStringBuilder,"gjorda",ligaDataMap.get("GoalsFor"));
        appendKeyValue(leagueStringBuilder,"inslappta",ligaDataMap.get("GoalsAgainst"));
        appendKeyValue(leagueStringBuilder,"poang",ligaDataMap.get("Points"));
        appendKeyValue(leagueStringBuilder,"placering",ligaDataMap.get("Position"));
    }

    /**
     * Creates the lineup data.
     *  @param trainerId
     *            The playerId of the trainer of the club.
     * @param teamId
     * 			team id (-1 for lineup templates)
     * @param nextLineup
     * 			map containing the lineup
     */
    public void createLineUp(String trainerId, int teamId, Map<String, String> nextLineup) {
        lineupStringBuilder = new StringBuilder("[lineup]\n");
        if (nextLineup != null) {
            var matchId = NumberUtils.toInt(nextLineup.get("MatchID"),0);
            var matchtype = NumberUtils.toInt(nextLineup.get("MatchType"), MatchType.NONE.getMatchTypeId());

            try {
                appendKeyValue(lineupStringBuilder,"teamid",teamId);
                appendKeyValue(lineupStringBuilder,"matchid",matchId);
                appendKeyValue(lineupStringBuilder,"matchtyp",matchtype);
                appendKeyValue(lineupStringBuilder,"trainer",trainerId);
                appendKeyValue(lineupStringBuilder,"installning",nextLineup.get("Attitude"));
                appendKeyValue(lineupStringBuilder,"styleOfPlay",nextLineup.get("StyleOfPlay"));
                appendKeyValue(lineupStringBuilder,"tactictype",nextLineup.get("TacticType"));
                appendKeyValue(lineupStringBuilder,"keeper",getPlayerForNextLineup("KeeperID", nextLineup))	;
                appendKeyValue(lineupStringBuilder,"rightBack",getPlayerForNextLineup("RightBackID", nextLineup));
                appendKeyValue(lineupStringBuilder,"rightCentralDefender",getPlayerForNextLineup("RightCentralDefenderID",nextLineup));
                appendKeyValue(lineupStringBuilder,"leftCentralDefender",getPlayerForNextLineup("LeftCentralDefenderID",nextLineup));
                appendKeyValue(lineupStringBuilder,"middleCentralDefender",getPlayerForNextLineup("MiddleCentralDefenderID",nextLineup));
                appendKeyValue(lineupStringBuilder,"leftBack",getPlayerForNextLineup("LeftBackID", nextLineup))	;
                appendKeyValue(lineupStringBuilder,"rightwinger",getPlayerForNextLineup("RightWingerID", nextLineup));
                appendKeyValue(lineupStringBuilder,"rightInnerMidfield",getPlayerForNextLineup("RightInnerMidfieldID",nextLineup));
                appendKeyValue(lineupStringBuilder,"leftInnerMidfield",getPlayerForNextLineup("LeftInnerMidfieldID",nextLineup));
                appendKeyValue(lineupStringBuilder,"middleInnerMidfield",getPlayerForNextLineup("CentralInnerMidfieldID",nextLineup));
                appendKeyValue(lineupStringBuilder,"leftwinger",getPlayerForNextLineup("LeftWingerID", nextLineup));
                appendKeyValue(lineupStringBuilder,"rightForward",getPlayerForNextLineup("RightForwardID", nextLineup));
                appendKeyValue(lineupStringBuilder,"leftForward",getPlayerForNextLineup("LeftForwardID", nextLineup));
                appendKeyValue(lineupStringBuilder,"centralForward",getPlayerForNextLineup("CentralForwardID",nextLineup));
                appendKeyValue(lineupStringBuilder,"substgk1",getPlayerForNextLineup("substGK1ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substgk2",getPlayerForNextLineup("substGK2ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substcd1",getPlayerForNextLineup("substCD1ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substcd2",getPlayerForNextLineup("substCD2ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substwb1",getPlayerForNextLineup("substWB1ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substwb2",getPlayerForNextLineup("substWB2ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substim1",getPlayerForNextLineup("substIM1ID",nextLineup));
                appendKeyValue(lineupStringBuilder,"substim2",getPlayerForNextLineup("substIM2ID",nextLineup));
                appendKeyValue(lineupStringBuilder,"substwi1",getPlayerForNextLineup("substWI1ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substwi2",getPlayerForNextLineup("substWI2ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substfw1",getPlayerForNextLineup("substFW1ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substfw2",getPlayerForNextLineup("substFW2ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substxt1",getPlayerForNextLineup("substXT1ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"substxt2",getPlayerForNextLineup("substXT2ID", nextLineup));
                appendKeyValue(lineupStringBuilder,"captain",getPlayerForNextLineup("CaptainID", nextLineup));
                appendKeyValue(lineupStringBuilder,"kicker1",getPlayerForNextLineup("KickerID", nextLineup));

                appendKeyValue(lineupStringBuilder,"order_rightback",getPlayerOrderForNextLineup("RightBackOrder",	nextLineup));
                appendKeyValue(lineupStringBuilder,"order_rightCentralDefender",getPlayerOrderForNextLineup("RightCentralDefenderOrder", nextLineup));
                appendKeyValue(lineupStringBuilder,"order_leftCentralDefender",getPlayerOrderForNextLineup("LeftCentralDefenderOrder", nextLineup));
                appendKeyValue(lineupStringBuilder,"order_middleCentralDefender",getPlayerOrderForNextLineup("MiddleCentralDefenderOrder", nextLineup));
                appendKeyValue(lineupStringBuilder,"order_leftBack",getPlayerOrderForNextLineup("LeftBackOrder",nextLineup));
                appendKeyValue(lineupStringBuilder,"order_rightWinger",getPlayerOrderForNextLineup("RightWingerOrder",	nextLineup));
                appendKeyValue(lineupStringBuilder,"order_rightInnerMidfield",getPlayerOrderForNextLineup("RightInnerMidfieldOrder", nextLineup));
                appendKeyValue(lineupStringBuilder,"order_leftInnerMidfield",getPlayerOrderForNextLineup("LeftInnerMidfieldOrder", nextLineup));
                appendKeyValue(lineupStringBuilder,"order_centralInnerMidfield",getPlayerOrderForNextLineup("CentralInnerMidfieldOrder", nextLineup));
                appendKeyValue(lineupStringBuilder,"order_leftWinger",getPlayerOrderForNextLineup("LeftWingerOrder",nextLineup));
                appendKeyValue(lineupStringBuilder,"order_rightForward",getPlayerOrderForNextLineup("RightForwardOrder",nextLineup));
                appendKeyValue(lineupStringBuilder,"order_leftForward",getPlayerOrderForNextLineup("LeftForwardOrder",nextLineup));
                appendKeyValue(lineupStringBuilder,"order_centralForward",getPlayerOrderForNextLineup("CentralForwardOrder",	nextLineup));

                int iSub=-1;
                String playerOrderIdString;
                while ((playerOrderIdString = getMatchOrderInfo( nextLineup, ++iSub, "playerOrderID")) != null ) {
                    lineupStringBuilder.append(playerOrderIdString)
                            .append(getMatchOrderInfo(nextLineup, iSub, "playerIn"))
                            .append(getMatchOrderInfo(nextLineup, iSub, "playerOut"))
                            .append(getMatchOrderInfo(nextLineup, iSub, "orderType"))
                            .append(getMatchOrderInfo(nextLineup, iSub, "matchMinuteCriteria"))
                            .append(getMatchOrderInfo(nextLineup, iSub, "pos"))
                            .append(getMatchOrderInfo(nextLineup, iSub, "behaviour"))
                            .append(getMatchOrderInfo(nextLineup, iSub, "card"))
                            .append(getMatchOrderInfo(nextLineup, iSub, "standing"));
                }

                for (int i = 0; i < 11; i++) {
                    String key = "PenaltyTaker" + i;
                    appendKeyValue(lineupStringBuilder,"penalty"+i, getPlayerForNextLineup(key, nextLineup));
                }

            } catch (Exception e) {
                HOLogger.instance().debug(HRFStringBuilder.class,
                        "Error(lineup): " + e);
            }
        }
    }

    private static String getMatchOrderInfo(Map<String, String> nextLineup, int i, String key) {
        String _key = "subst"+i+key;
        var value = nextLineup.get(_key);
        if ( value != null)	return _key + "=" + value + '\n';
        return null;
    }

    /**
     * Create the player data.
     */
    public void createPlayers(MatchLineupTeam matchLineupTeam, List<MyHashtable> playersData) {
        playersStringBuilder = new StringBuilder();
        for (int i = 0; (playersData != null) && (i < playersData.size()); i++) {
            var ht = playersData.get(i);

            playersStringBuilder.append("[player").append(ht.get("PlayerID")).append("]\n");

            var firstName = ht.get("FirstName");
            var lastName = ht.get("LastName");
            var nickName = ht.get("NickName");
            if (nickName.length() > 0) {
                appendKeyValue(playersStringBuilder, "name", firstName + " '" + nickName + "' " + lastName);
            } else {
                appendKeyValue(playersStringBuilder, "name", firstName + " " + lastName);
            }
            appendKeyValue(playersStringBuilder, "firstname", ht.get("FirstName"));
            appendKeyValue(playersStringBuilder, "nickname", ht.get("NickName"));
            appendKeyValue(playersStringBuilder, "lastname", ht.get("LastName"));
            appendKeyValue(playersStringBuilder, "ald", ht.get("Age"));
            appendKeyValue(playersStringBuilder, "agedays", ht.get("AgeDays"));
            appendKeyValue(playersStringBuilder, "arrivaldate", ht.get("ArrivalDate"));
            appendKeyValue(playersStringBuilder, "ska", ht.get("InjuryLevel"));
            appendKeyValue(playersStringBuilder, "for", ht.get("PlayerForm"));
            appendKeyValue(playersStringBuilder, "uth", ht.get("StaminaSkill"));
            appendKeyValue(playersStringBuilder, "spe", ht.get("PlaymakerSkill"));
            appendKeyValue(playersStringBuilder, "mal", ht.get("ScorerSkill"));
            appendKeyValue(playersStringBuilder, "fra", ht.get("PassingSkill"));
            appendKeyValue(playersStringBuilder, "ytt", ht.get("WingerSkill"));
            appendKeyValue(playersStringBuilder, "fas", ht.get("SetPiecesSkill"));
            appendKeyValue(playersStringBuilder, "bac", ht.get("DefenderSkill"));
            appendKeyValue(playersStringBuilder, "mlv", ht.get("KeeperSkill"));
            appendKeyValue(playersStringBuilder, "rut", ht.get("Experience"));
            appendKeyValue(playersStringBuilder, "loy", ht.get("Loyalty"));
            appendKeyValue(playersStringBuilder, "homegr", ht.get("MotherClubBonus"));
            appendKeyValue(playersStringBuilder, "led", ht.get("Leadership"));
            appendKeyValue(playersStringBuilder, "sal", ht.get("Salary"));
            appendKeyValue(playersStringBuilder, "mkt", ht.get("MarketValue"));
            appendKeyValue(playersStringBuilder, "gev", ht.get("CareerGoals"));
            appendKeyValue(playersStringBuilder, "gtl", ht.get("LeagueGoals"));
            appendKeyValue(playersStringBuilder, "gtc", ht.get("CupGoals"));
            appendKeyValue(playersStringBuilder, "gtt", ht.get("FriendliesGoals"));
            appendKeyValue(playersStringBuilder, "GoalsCurrentTeam", ht.get("GoalsCurrentTeam"));
            appendKeyValue(playersStringBuilder, "MatchesCurrentTeam", ht.get("MatchesCurrentTeam"));
            appendKeyValue(playersStringBuilder, "hat", ht.get("CareerHattricks"));
            appendKeyValue(playersStringBuilder, "CountryID", ht.get("CountryID"));
            appendKeyValue(playersStringBuilder, "warnings", ht.get("Cards"));
            appendKeyValue(playersStringBuilder, "speciality", ht.get("Specialty"));
            appendKeyValue(playersStringBuilder, "specialityLabel", PlayerSpeciality.toString(Integer.parseInt(ht.get("Specialty"))));
            appendKeyValue(playersStringBuilder, "gentleness", ht.get("Agreeability"));
            appendKeyValue(playersStringBuilder, "gentlenessLabel", PlayerAgreeability.toString(Integer.parseInt(ht.get("Agreeability"))));
            appendKeyValue(playersStringBuilder, "honesty", ht.get("Honesty"));
            appendKeyValue(playersStringBuilder, "honestyLabel", PlayerHonesty.toString(Integer.parseInt(ht.get("Honesty"))));
            appendKeyValue(playersStringBuilder, "Aggressiveness", ht.get("Aggressiveness"));
            appendKeyValue(playersStringBuilder, "AggressivenessLabel", PlayerAggressiveness.toString(Integer.parseInt(ht.get("Aggressiveness"))));

            appendKeyValue(playersStringBuilder, "TrainerType", ht.get("TrainerType"));
            appendKeyValue(playersStringBuilder, "TrainerSkill", ht.get("TrainerSkill"));

            appendKeyValue(playersStringBuilder, "LastMatch_Date", ht.get("LastMatch_Date"));
            appendKeyValue(playersStringBuilder, "LastMatch_Rating", ht.get("LastMatch_Rating"));
            appendKeyValue(playersStringBuilder, "LastMatch_id", ht.get("LastMatch_id"));
            appendKeyValue(playersStringBuilder, "LastMatch_PositionCode", ht.get("LastMatch_PositionCode"));
            appendKeyValue(playersStringBuilder, "LastMatch_PlayedMinutes", ht.get("LastMatch_PlayedMinutes"));
            appendKeyValue(playersStringBuilder, "LastMatch_RatingEndOfGame", ht.get("LastMatch_RatingEndOfGame"));

            String lastMatchType = ht.getOrDefault("LastMatch_Type", "0");
            appendKeyValue(playersStringBuilder, "LastMatch_Type", lastMatchType);

            if ((matchLineupTeam != null)
                    && (matchLineupTeam.getPlayerByID(Integer.parseInt(ht.get("PlayerID"))) != null)
                    && (matchLineupTeam.getPlayerByID(Integer.parseInt(ht.get("PlayerID")))
                    .getRating() >= 0)) {
                appendKeyValue(playersStringBuilder, "rating", (int) (matchLineupTeam.getPlayerByID(Integer.parseInt(ht.get("PlayerID"))).getRating() * 2));
            } else {
                appendKeyValue(playersStringBuilder, "rating", "0");
            }

            if ((ht.get("PlayerNumber") != null)
                    || (!ht.get("PlayerNumber").equals(""))) {
                appendKeyValue(playersStringBuilder, "PlayerNumber", ht.get("PlayerNumber"));
            }

            appendKeyValue(playersStringBuilder, "TransferListed", ht.get("TransferListed"));
            appendKeyValue(playersStringBuilder, "NationalTeamID", ht.get("NationalTeamID"));
            appendKeyValue(playersStringBuilder, "Caps", ht.get("Caps"));
            appendKeyValue(playersStringBuilder, "CapsU20", ht.get("CapsU20"));
            appendKeyValue(playersStringBuilder, "PlayerCategoryId", ht.get("PlayerCategoryId"));
            // TODO: since we transport all data through the hrf file, we have to loose the new lines
            appendKeyValue(playersStringBuilder, "Statement", serializeMultiLine(ht.get("Statement")));
            appendKeyValue(playersStringBuilder, "OwnerNotes", serializeMultiLine(ht.get("OwnerNotes")));
        }
    }

    private String serializeMultiLine(String value){
        if ( value != null ){
            return value.replaceAll("\\R", "<br>");
        }
        return "";
    }

    /**
     * Append youth player data to buffer
     */
    public void appendYouthPlayers(List<MyHashtable> playersData) {
        youthPlayersStringBuilder = new StringBuilder();

        for (var player: playersData ) {
            youthPlayersStringBuilder.append("[youthplayer").append(player.get("YouthPlayerID")).append(']').append('\n');
            appendHRFLine(youthPlayersStringBuilder, player, "FirstName");
            appendHRFLine(youthPlayersStringBuilder, player, "NickName");
            appendHRFLine(youthPlayersStringBuilder, player, "LastName");
            appendHRFLine(youthPlayersStringBuilder, player, "Age");
            appendHRFLine(youthPlayersStringBuilder, player, "AgeDays");
            appendHRFLine(youthPlayersStringBuilder, player, "ArrivalDate");
            appendHRFLine(youthPlayersStringBuilder, player, "CanBePromotedIn");
            appendHRFLine(youthPlayersStringBuilder, player, "PlayerNumber");
            appendHRFLine(youthPlayersStringBuilder, player, "Statement");
            appendHRFLine(youthPlayersStringBuilder, player, "OwnerNotes");
            appendHRFLine(youthPlayersStringBuilder, player, "PlayerCategoryID");

            appendHRFLine(youthPlayersStringBuilder, player, "Cards");
            appendHRFLine(youthPlayersStringBuilder, player, "InjuryLevel");
            appendHRFLine(youthPlayersStringBuilder, player, "Specialty");
            appendHRFLine(youthPlayersStringBuilder, player, "CareerGoals");
            appendHRFLine(youthPlayersStringBuilder, player, "CareerHattricks");
            appendHRFLine(youthPlayersStringBuilder, player, "LeagueGoals");
            appendHRFLine(youthPlayersStringBuilder, player, "FriendlyGoals");

            for ( var skillId: YouthPlayer.skillIds){
                appendHRFSkillLines(youthPlayersStringBuilder, player, skillId);
            }

            appendHRFLine(youthPlayersStringBuilder, player, "ScoutId");
            appendHRFLine(youthPlayersStringBuilder, player, "ScoutName");
            appendHRFLine(youthPlayersStringBuilder, player, "ScoutingRegionID");

            for (int i = 0; appendScoutComment(youthPlayersStringBuilder, player, i); i++) {}

            appendHRFLine(youthPlayersStringBuilder, player, "YouthMatchID");
            appendHRFLine(youthPlayersStringBuilder, player, "YouthMatchDate");
            appendHRFLine(youthPlayersStringBuilder, player, "PositionCode");
            appendHRFLine(youthPlayersStringBuilder, player, "PlayedMinutes");
            appendHRFLine(youthPlayersStringBuilder, player, "Rating");
        }
    }

    private boolean appendScoutComment(StringBuilder buffer, MyHashtable player, int i) {
        var prefix = "ScoutComment"+i;

        var text = player.get(prefix+"Text");
        if ( text != null){
            appendHRFLine(buffer, player, prefix+"Text");
            appendHRFLine(buffer, player, prefix+"Type");
            appendHRFLine(buffer, player, prefix+"Variation");
            appendHRFLine(buffer, player, prefix+"SkillType");
            appendHRFLine(buffer, player, prefix+"SkillLevel");
            return true;
        }
        return false;
    }

    private void appendHRFSkillLines(StringBuilder buffer, MyHashtable player, Skills.HTSkillID skillId) {
        var skill = skillId.toString() + "Skill";
        appendHRFLine(buffer, player, skill);
        appendHRFLine(buffer, player, skill+"IsAvailable");
        appendHRFLine(buffer, player, skill+"IsMaxReached");
        appendHRFLine(buffer, player, skill+"MayUnlock");
        skill += "Max";
        appendHRFLine(buffer, player, skill);
        appendHRFLine(buffer, player, skill+"IsAvailable");
        appendHRFLine(buffer, player, skill+"MayUnlock");
    }

    private void appendHRFLine(StringBuilder buffer, MyHashtable player, String key) {
        appendKeyValue( buffer, key, player.get(key));
    }

    /**
     * Create team related data (training, confidence, formation experience,
     * etc.).
     */
    public void createTeam(Map<String, String> trainingDataMap) {
        teamStringBuilder = new StringBuilder("[team]\n");
        appendKeyValue(teamStringBuilder,"trLevel",trainingDataMap.get("TrainingLevel"));
        appendKeyValue(teamStringBuilder,"staminaTrainingPart", trainingDataMap.get("StaminaTrainingPart"));
        appendKeyValue(teamStringBuilder,"trTypeValue", trainingDataMap.get("TrainingType"));
        var training = NumberUtils.toInt( trainingDataMap.get("TrainingType"), 0);
        appendKeyValue(teamStringBuilder,"trType",TrainingType.toString(training));

        if ((trainingDataMap.get("Morale") != null)
                && (trainingDataMap.get("SelfConfidence") != null)) {
            appendKeyValue(teamStringBuilder,"stamningValue", trainingDataMap.get("Morale"));
            appendKeyValue(teamStringBuilder,"stamning", TeamSpirit.toString(Integer.parseInt(trainingDataMap.get("Morale"))));
            appendKeyValue(teamStringBuilder,"sjalvfortroendeValue", trainingDataMap.get("SelfConfidence"));
            appendKeyValue(teamStringBuilder,"sjalvfortroende", TeamConfidence.toString(Integer.parseInt(trainingDataMap.get("SelfConfidence"))));
        } else {
            appendKeyValue(teamStringBuilder,"playingMatch", "true");
        }

        appendKeyValue(teamStringBuilder,"exper433",trainingDataMap.get("Experience433"));
        appendKeyValue(teamStringBuilder,"exper451",trainingDataMap.get("Experience451"));
        appendKeyValue(teamStringBuilder,"exper352",trainingDataMap.get("Experience352"));
        appendKeyValue(teamStringBuilder,"exper532",trainingDataMap.get("Experience532"));
        appendKeyValue(teamStringBuilder,"exper343",trainingDataMap.get("Experience343"));
        appendKeyValue(teamStringBuilder,"exper541",trainingDataMap.get("Experience541"));
        appendKeyValue(teamStringBuilder,"exper442",trainingDataMap.get("Experience442"));
        appendKeyValue(teamStringBuilder,"exper523", trainingDataMap.get("Experience523"));
        appendKeyValue(teamStringBuilder,"exper550", trainingDataMap.get("Experience550"));
        appendKeyValue(teamStringBuilder,"exper253", trainingDataMap.get("Experience253"));
    }

    /**
     * Create the world data.
     */
    public void createWorld(Map<String, String> clubDataMap,
                            Map<String, String> teamdetailsDataMap,
                            Map<String, String> trainingDataMap,
                            Map<String, String> worldDataMap) {
        xtraStringBuilder = new StringBuilder("[xtra]\n");
        appendKeyValue(xtraStringBuilder, "TrainingDate", worldDataMap.get("TrainingDate"));
        appendKeyValue(xtraStringBuilder, "EconomyDate", worldDataMap.get("EconomyDate"));
        appendKeyValue(xtraStringBuilder, "SeriesMatchDate", worldDataMap.get("SeriesMatchDate"));
        appendKeyValue(xtraStringBuilder, "CountryId", worldDataMap.get("CountryID"));
        var currencyRate = worldDataMap.get("CurrencyRate");
        if (!StringUtils.isEmpty(currencyRate)) {
            currencyRate = currencyRate.replace(',', '.');
        }
        appendKeyValue(xtraStringBuilder, "CurrencyRate", currencyRate);
        appendKeyValue(xtraStringBuilder, "LogoURL", teamdetailsDataMap.get("LogoURL"));
        appendKeyValue(xtraStringBuilder, "HasPromoted", clubDataMap.get("HasPromoted"));
        appendKeyValue(xtraStringBuilder, "TrainerID", trainingDataMap.get("TrainerID"));
        appendKeyValue(xtraStringBuilder, "TrainerName", trainingDataMap.get("TrainerName"));
        appendKeyValue(xtraStringBuilder, "ArrivalDate", trainingDataMap.get("ArrivalDate"));
        appendKeyValue(xtraStringBuilder, "LeagueLevelUnitID", teamdetailsDataMap.get("LeagueLevelUnitID"));
    }

    public void createStaff(List<MyHashtable> staffList) {
        staffStringBuilder = new StringBuilder("[staff]\n");

        for (int i = 0; (staffList != null) && (i < staffList.size()); i++) {
            MyHashtable hash = staffList.get(i);

            appendKeyValue(staffStringBuilder,"staff"+i+"Name",hash.get("Name"));
            appendKeyValue(staffStringBuilder,"staff"+i+"StaffId",hash.get("StaffId"));
            appendKeyValue(staffStringBuilder,"staff"+i+"StaffType",hash.get("StaffType"));
            appendKeyValue(staffStringBuilder,"staff"+i+"StaffLevel",hash.get("StaffLevel"));
            appendKeyValue(staffStringBuilder,"staff"+i+"Cost",hash.get("Cost"));
        }
    }

    private static String getPlayerOrderForNextLineup(String position,
            Map<?, ?> map) {
        if (map != null) {
            String ret = (String) map.get(position);

            if (ret != null) {
                ret = ret.trim();
                if (!"null".equals(ret) && !"".equals(ret)) {
                    return ret.trim();
                }
            }
        }
        return "0";
    }

    private static String getPlayerForNextLineup(String position, Map<?, ?> next) {
        if (next != null) {
            final Object ret = next.get(position);
            if (ret != null) {
                return ret.toString();
            }
        }
        return "0";
    }
}
