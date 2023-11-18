/*
 * ConvertXml2Hrf.java
 *
 * Created on 12. Januar 2004, 09:44
 */
package core.file.xml

import core.file.hrf.HRFStringBuilder
import core.gui.CursorToolkit
import core.gui.HOMainFrame
import core.gui.HOMainFrame.setInformation
import core.gui.theme.ThemeManager
import core.model.HOVerwaltung
import core.model.player.TrainerStatus
import core.module.config.ModuleConfig
import core.net.MyConnector
import core.net.OnlineWorker
import core.util.Helper
import java.io.IOException

/**
 * Convert the necessary xml data into a HRF file.
 *
 * @author thomas.werth
 */
object ConvertXml2Hrf {
    /**
     * Create the HRF data and return it in one string.
     */
    @JvmStatic
	@Throws(IOException::class)
    fun createHrf(): String? {
        val progressIncrement = 3
        setInformation(Helper.getTranslation("ls.update_status.connection"), progressIncrement)
        val mc = MyConnector.instance()
        var teamId = HOVerwaltung.instance().model.getBasics().teamId
        var youthTeamId = HOVerwaltung.instance().model.getBasics().youthTeamId
        val teamDetails = mc.getTeamdetails(-1) ?: return null
        val teamInfoList = XMLTeamDetailsParser.getTeamInfoFromString(teamDetails)
        var usersPremierTeamInfo =
            teamInfoList.stream().filter { obj: TeamInfo? -> obj!!.primaryTeam }.findFirst().get()
        val usersPremierTeamId = usersPremierTeamInfo.teamId
        if (teamId <= 0 || youthTeamId == null) {
            // We have no team selected or the youth team information is never downloaded before
            if (teamInfoList.size == 1) {
                // user has only one single team
                teamId = teamInfoList[0].teamId
                youthTeamId = teamInfoList[0].youthTeamId
            } else if (teamInfoList.size >= 2) {
                // user has more than one team
                if (teamId <= 0) {
                    // Select one of user's teams, if not done before
                    CursorToolkit.stopWaitCursor(HOMainFrame.rootPane)
                    val selection = TeamSelectionDialog(HOMainFrame, teamInfoList)
                    selection.isVisible = true
                    if (selection.cancel) {
                        return null
                    }
                    teamId = selection.getSelectedTeam()?.teamId ?: -1
                    youthTeamId = selection.getSelectedTeam()?.youthTeamId ?: -1
                } else {
                    // team id is in DB and this is the first time we download youth team information
                    val finalTeamId = teamId
                    val teamInfo = teamInfoList.stream()
                        .filter { x: TeamInfo? -> x!!.teamId == finalTeamId }
                        .findAny()
                        .orElse(null)
                    if (teamInfo != null) {
                        youthTeamId = teamInfo.youthTeamId
                    }
                }
            } else {
                return null
            }
        }
        val teamDetailsDataMap:Map<String, String> = XMLTeamDetailsParser.parseTeamDetailsFromString(teamDetails, teamId)
        if (teamDetailsDataMap.isEmpty()) return null
        setInformation(Helper.getTranslation("ls.update_status.team_logo"), progressIncrement)
        OnlineWorker.downloadTeamLogo(teamDetailsDataMap)
        setInformation(Helper.getTranslation("ls.update_status.club_info"), progressIncrement)
        val clubDataMap = XMLClubParser.parseClubFromString(mc.getVerein(teamId))
        setInformation(Helper.getTranslation("ls.update_status.league_details"), progressIncrement)
        val ligaDataMap = XMLLeagueDetailsParser.parseLeagueDetailsFromString(
            mc.getLeagueDetails(teamDetailsDataMap["LeagueLevelUnitID"]),
            teamId.toString()
        )
        setInformation(Helper.getTranslation("ls.update_status.world_details"), progressIncrement)
        val leagueId:String = teamDetailsDataMap["LeagueID"]!! // TODO: Figure out why !! is needed here and below??
        var worldDataMap = XMLWorldDetailsParser.parseWorldDetailsFromString(
            mc.getWorldDetails(teamDetailsDataMap["LeagueID"]!!.toInt()),
            leagueId
        )

        // Currency fix
        val lastPremierId = ModuleConfig.instance().getInteger("UsersPremierTeamId")
        if (lastPremierId != null && lastPremierId == usersPremierTeamId) {
            worldDataMap = worldDataMap + mapOf(
                "CurrencyRate" to ModuleConfig.instance().getString("CurrencyRate"),
                "CountryID" to ModuleConfig.instance().getString("CountryId")
                )
        } else {
            // We need to get hold of the currency info for the primary team, no matter which team we download.
            usersPremierTeamInfo = XMLWorldDetailsParser.updateTeamInfoWithCurrency(
                usersPremierTeamInfo,
                mc.getWorldDetails(usersPremierTeamInfo.leagueId)
            )
            ModuleConfig.instance().setString("CurrencyRate", usersPremierTeamInfo.currencyRate!!.trim { it <= ' ' })
            ModuleConfig.instance().setString("CountryId", usersPremierTeamInfo.countryId)
            ModuleConfig.instance().setInteger("UsersPremierTeamId", usersPremierTeamInfo.teamId)
            worldDataMap = worldDataMap + mapOf(
                "CurrencyRate" to ModuleConfig.instance().getString("CurrencyRate"),
                "CountryID" to ModuleConfig.instance().getString("CountryId")
            )
        }
        setInformation(Helper.getTranslation("ls.update_status.players_information"), progressIncrement)
        val playersData: MutableList<SafeInsertMap> =
            XMLPlayersParser.parsePlayersFromString(mc.downloadPlayers(teamId))

        // Download players' avatar
        setInformation(Helper.getTranslation("ls.update_status.players_avatars"), progressIncrement)
        val playersAvatar = XMLAvatarsParser.parseAvatarsFromString(mc.getAvatars(teamId))
        ThemeManager.instance().generateAllPlayerAvatar(playersAvatar, 1)
        var youthPlayers = emptyList<SafeInsertMap>()
        if (youthTeamId != null && youthTeamId > 0) {
            youthPlayers = XMLPlayersParser.parseYouthPlayersFromString(mc.downloadYouthPlayers(youthTeamId))
        }
        setInformation(Helper.getTranslation("ls.update_status.economy"), progressIncrement)
        val economyDataMap = XMLEconomyParser.parseEconomyFromString(mc.getEconomy(teamId))
        setInformation(Helper.getTranslation("ls.update_status.training"), progressIncrement)
        val trainingDataMap = XMLTrainingParser.parseTrainingFromString(mc.getTraining(teamId))
        setInformation(Helper.getTranslation("ls.update_status.staff"), progressIncrement)
        val staffData = XMLStaffParser.parseStaffFromString(mc.getStaff(teamId))
        val trainer = staffData[0]
        val trainerId = trainer["TrainerId"].toString()
        if (trainer.containsKey("TrainerId")) {
            val trainerStatus = TrainerStatus.fromInt(trainer["TrainerStatus"]!!.toInt())
            if (trainerStatus == TrainerStatus.PlayingTrainer) {
                for (p in playersData) {
                    if (p["PlayerID"] == trainerId) {
                        p.putAll(trainer)
                        break
                    }
                }
            } else {
                trainer["LineupDisabled"] = "true"
                playersData.add(trainer)
            }
        }
        var arenaId = 0
        try {
            arenaId = teamDetailsDataMap["ArenaID"]!!.toInt()
        } catch (ignored: Exception) {
        }
        val arenaDataMap = XMLArenaParser.parseArenaFromString(mc.downloadArena(arenaId))

        // MatchOrder
        setInformation(Helper.getTranslation("ls.update_status.match_orders"), progressIncrement)
        val matches = XMLMatchesParser
            .parseMatchesFromString(
                mc.getMatches(
                    teamDetailsDataMap["TeamID"]!!.toInt(),
                    false, true
                )
            )
        setInformation(Helper.getTranslation("ls.update_status.match_info"), progressIncrement)
        val nextLineupDataMap = OnlineWorker.downloadNextMatchOrder(matches, teamId)
        setInformation(Helper.getTranslation("ls.update_status.match_lineup"), progressIncrement)
        val matchLineupTeam = OnlineWorker.downloadLastLineup(matches, teamId)
        val hrfStringBuilder = HRFStringBuilder()
        // Abschnitte erstellen
        // basics
        setInformation(Helper.getTranslation("ls.update_status.create_basics"), progressIncrement)
        hrfStringBuilder.createBasics(teamDetailsDataMap, worldDataMap)

        // Liga
        hrfStringBuilder.createLeague(ligaDataMap)
        setInformation(Helper.getTranslation("ls.update_status.create_league"), progressIncrement)

        // Club
        setInformation(Helper.getTranslation("ls.update_status.create_club"), progressIncrement)
        hrfStringBuilder.createClub(clubDataMap, economyDataMap, teamDetailsDataMap)

        // team
        setInformation(Helper.getTranslation("ls.update_status.create_team"), progressIncrement)
        hrfStringBuilder.createTeam(trainingDataMap)

        // lineup
        setInformation(Helper.getTranslation("ls.update_status.create_lineups"), progressIncrement)
        hrfStringBuilder.createLineUp(trainerId, teamId, nextLineupDataMap)

        // economy
        setInformation(Helper.getTranslation("ls.update_status.create_economy"), progressIncrement)
        hrfStringBuilder.createEconomy(economyDataMap)

        // Arena
        setInformation(Helper.getTranslation("ls.update_status.create_arena"), progressIncrement)
        hrfStringBuilder.createArena(arenaDataMap)

        // players
        setInformation(Helper.getTranslation("ls.update_status.create_players"), progressIncrement)
        hrfStringBuilder.createPlayers(matchLineupTeam, playersData)

        // youth players
        setInformation(Helper.getTranslation("ls.update_status.create_youth_players"), progressIncrement)
        hrfStringBuilder.appendYouthPlayers(youthPlayers)

        // xtra Data
        setInformation(Helper.getTranslation("ls.update_status.create_world"), progressIncrement)
        hrfStringBuilder.createWorld(clubDataMap, teamDetailsDataMap, trainingDataMap, worldDataMap)

        // lineup of the last match
        setInformation(Helper.getTranslation("ls.update_status.create_last_lineup"), progressIncrement)
        hrfStringBuilder.createLastLineUp(matchLineupTeam, teamDetailsDataMap)

        // staff
        setInformation(Helper.getTranslation("ls.update_status.create_staff"), progressIncrement)
        hrfStringBuilder.createStaff(staffData)
        return hrfStringBuilder.createHRF().toString()
    }
}