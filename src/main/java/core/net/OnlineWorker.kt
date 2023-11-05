package core.net

import core.db.DBManager
import core.db.user.UserManager
import core.file.ExtensionFileFilter
import core.file.hrf.HRFStringBuilder
import core.file.hrf.HRFStringParser
import core.file.xml.*
import core.gui.HOMainFrame
import core.gui.InfoPanel
import core.gui.theme.ThemeManager
import core.model.HOModel
import core.model.HOVerwaltung
import core.model.Tournament.TournamentDetails
import core.model.UserParameter
import core.model.enums.MatchType
import core.model.enums.MatchTypeExtended
import core.model.match.*
import core.model.misc.Regiondetails
import core.model.player.Player
import core.util.HODateTime
import core.util.HOLogger
import core.util.Helper
import core.util.StringUtils
import module.lineup.Lineup
import module.nthrf.NtTeamDetails
import module.series.Spielplan
import org.apache.commons.lang3.math.NumberUtils

import java.awt.Color
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.temporal.ChronoUnit
import java.util.*

import javax.swing.JDialog
import javax.swing.JFileChooser
import javax.swing.JOptionPane

/**
 * @author thomas.werth
 */
object OnlineWorker {

    /**
     * Get and optionally save HRF
     *
     */
    fun getHrf(parent: JDialog): Boolean {
        // Show wait dialog
        var ok = true
        try {
            val hov = HOVerwaltung.instance()
            var hrf: String? = null
            try {
                hrf = ConvertXml2Hrf.createHrf()
                if (hrf == null) {
                    return false
                }

            } catch (e: IOException) {
                // Info
                val msg = getLangString("Downloadfehler") +
                        " : Error converting xml 2 HRF. Corrupt/Missing Data : "
                setInfoMsg(msg, InfoPanel.FEHLERFARBE)
                Helper.showMessage(
                    parent, msg + "\n" + e + "\n", getLangString("Fehler"),
                    JOptionPane.ERROR_MESSAGE
                )
                ok = false
            }

            if (hrf != null) {
                if (hrf.contains("playingMatch=true")) {
                    HOMainFrame.resetInformation()
                    JOptionPane.showMessageDialog(
                        parent, getLangString("NO_HRF_Spiel"),
                        getLangString("NO_HRF_ERROR"), JOptionPane.INFORMATION_MESSAGE
                    )
                } else if (hrf.contains("NOT AVAILABLE")) {
                    HOMainFrame.resetInformation()
                    JOptionPane.showMessageDialog(
                        parent, getLangString("NO_HRF_ERROR"),
                        getLangString("NO_HRF_ERROR"), JOptionPane.INFORMATION_MESSAGE
                    )
                } else {
                    // Create HOModel from the hrf data
                    val hoModel = HRFStringParser.parse(hrf)
                    if (hoModel == null) {
                        // Info
                        setInfoMsg(getLangString("Importfehler"), InfoPanel.FEHLERFARBE)
                        // Error
                        Helper.showMessage(
                            parent, getLangString("Importfehler"),
                            getLangString("Fehler"), JOptionPane.ERROR_MESSAGE
                        )
                    } else {
                        // save the model in the database
                        hoModel.saveHRF()
                        hoModel.fixtures = hov.model.getFixtures()
                        // Add old players to the model
                        hoModel.setFormerPlayers(DBManager.loadAllPlayers())
                        // Only update when the model is newer than existing
                        if (HOVerwaltung.isNewModel(hoModel)) {
                            // Reimport Skillup
                            DBManager.checkSkillup(hoModel)
                            // Show
                            hov.setModel(hoModel)
                            // reset value of TS, confidence in Lineup Settings Panel after data download
                            HOMainFrame.lineupPanel?.backupRealGameSettings()
                        }
                        // Info
                        saveHRFToFile(parent, hrf)
                    }
                }
            }
        } finally {
            HOMainFrame.setInformation(getLangString("HRFErfolg"), 0)
        }
        return ok
    }

    /**
     * saugt das Archiv
     *
     * @param teamId
     *            null falls unnötig sonst im Format 2004-02-01
     * @param firstDate
     *            null falls unnötig sonst im Format 2004-02-01
     * @param store
     *            True if matches are to be downloaded and stored. False if only
     *            a match list is wanted.
     *
     * @return The list of MatchKurzInfo. This can be null on error, or empty.
     */
    fun getMatchArchive(teamId: Int, firstDate: HODateTime, store: Boolean): List<MatchKurzInfo>? {

        val allMatches = mutableListOf<MatchKurzInfo>()
        val endDate = HODateTime.now()

        // Show wait Dialog
        HOMainFrame.resetInformation()

        var matchesString: String?
        try {
            var curTime = firstDate
            while (firstDate.isBefore(endDate)) {
                var lastDate = firstDate.plus(90, ChronoUnit.DAYS)
                if (!lastDate.isBefore(endDate)) {
                    lastDate = endDate
                }

                try {
                    HOMainFrame.setInformation(
                        HOVerwaltung.instance().getLanguageString("ls.update_status.match_info"),
                        20
                    )
                    matchesString = MyConnector.instance().getMatchesArchive(teamId, firstDate, lastDate)
                    HOMainFrame.setInformation(
                        HOVerwaltung.instance().getLanguageString("ls.update_status.match_info"),
                        20
                    )
                } catch (e: Exception) {
                    // Info
                    val msg = getLangString("Downloadfehler") + " : Error fetching MatchArchiv : "
                    setInfoMsg(msg, InfoPanel.FEHLERFARBE)
                    Helper.showMessage(
                        HOMainFrame, msg, getLangString("Fehler"),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return null
                }

                HOMainFrame.setInformation(HOVerwaltung.instance().getLanguageString("ls.update_status.match_info"), 20)
                val matches = XMLMatchArchivParser.parseMatchesFromString(matchesString)

                // Add the new matches to the list of all matches
                allMatches.addAll(matches)

                // Zeitfenster neu setzen
                curTime = curTime.plus(90, ChronoUnit.DAYS)
            }

            // Store in the db if store is true
            if (store && (allMatches.size > 0)) {

                HOMainFrame.setInformation(HOVerwaltung.instance().getLanguageString("ls.update_status.match_info"), 20)
                DBManager.storeMatchKurzInfos(allMatches)

                // Store full info for all matches
                for (match in allMatches) {
                    // if match is available and match is finished
                    if ((DBManager.isMatchInDB(match.matchID, match.getMatchType()))
                        && (match.matchStatus == MatchKurzInfo.FINISHED)
                    ) {
                        downloadMatchData(match.matchID, match.getMatchType(), true)
                    }
                }
            }
        } finally {
            HOMainFrame.setInformationCompleted()
        }
        return allMatches
    }

    /**
     * Downloads a match with the given criteria and stores it in the database.
     * If a match is already in the db, and refresh is false, nothing is
     * downloaded.
     *
     * @param matchid
     *            ID for the match to be downloaded
     * @param matchType
     *            matchType for the match to be downloaded.
     * @param refresh
     *            If true the match will always be downloaded.
     *
     * @return true if the match is in the db afterwards
     */
    fun downloadMatchData(matchid: Int, matchType: MatchType, refresh: Boolean): Boolean {
        val info: MatchKurzInfo?
        if (DBManager.isMatchInDB(matchid, matchType)) {
            info = DBManager.getMatchesKurzInfoByMatchID(matchid, matchType)
        } else {
            info = MatchKurzInfo()
            info.matchID = matchid
            info.matchType = matchType
        }
        return downloadMatchData(info, refresh)
    }

    fun downloadMatchData(info: MatchKurzInfo?, refresh: Boolean): Boolean {
        if (info == null) {
            return false
        }

        if (info.isObsolet) {
            return true
        }

        HOLogger.instance().debug(OnlineWorker.javaClass, "Get Lineup : " + info.matchID)

        val matchID = info.matchID
        if (matchID < 0) {
            return false
        }

        HOMainFrame.setWaitInformation()
        // Only download if not present in the database, or if refresh is true or if match not oboslet
        if (refresh
            || !DBManager.isMatchInDB(matchID, info.getMatchType())
            || DBManager.hasUnsureWeatherForecast(matchID)
            || DBManager.matchLineupIsNotStored(info.getMatchType(), matchID)
        ) {
            try {
                var details: Matchdetails?

                // If ids not found, download matchdetails to obtain them.
                // Highlights will be missing.
                // ArenaId==0 in division battles
                val newInfo = info.homeTeamID <= 0 || info.guestTeamID <= 0
                val weatherDetails: Weather.Forecast? = info.weatherForecast
                val bWeatherKnown = ((weatherDetails != null) && weatherDetails.isSure)

                if (newInfo || !bWeatherKnown) {
                    HOMainFrame.setWaitInformation()
                    details = downloadMatchDetails(matchID, info.getMatchType(), null)
                    if (details != null) {
                        info.homeTeamID = details.homeTeamId
                        info.setGuestTeamID(details.getGuestTeamId())
                        info.setArenaId(details.getArenaID())
                        info.setMatchSchedule(details.getMatchDate())
                        val wetterId = details.getWetterId()
                        if (wetterId != -1) {
                            info.setMatchStatus(MatchKurzInfo.FINISHED)
                            info.setWeather(Weather.getById(details.getWetterId()))
                            info.setWeatherForecast(Weather.Forecast.HAPPENED)
                        } else if (info.getArenaId() > 0) {
                            info.setRegionId(details.getRegionId())

                            if (!info.getWeatherForecast().isSure()) {
                                val regiondetails = getRegionDetails(info.getRegionId())
                                if (regiondetails != null) {
                                    var matchDate = info.getMatchSchedule().toLocaleDate()
                                    var weatherDate = regiondetails.getFetchDatum().toLocaleDate()
                                    if (matchDate.equals(weatherDate)) {
                                        info.setWeatherForecast(Weather.Forecast.TODAY)
                                        info.setWeather(regiondetails.getWeather())
                                    } else {
                                        val forecastDate =
                                            regiondetails.getFetchDatum().plus(1, ChronoUnit.DAYS).toLocaleDate()
                                        if (matchDate.equals(forecastDate)) {
                                            info.setWeatherForecast(Weather.Forecast.TOMORROW)
                                        } else {
                                            info.setWeatherForecast((Weather.Forecast.UNSURE))
                                        }
                                        info.setWeather(regiondetails.getWeatherTomorrow())
                                    }
                                }
                            }
                        }

                        // get the other team
                        val otherId = if (info.isHomeMatch()) {
                            info.guestTeamID
                        } else {
                            info.homeTeamID
                        }
                        if (otherId > 0) {
                            val otherTeam = getTeam(otherId)
                            info.setIsDerby(getRegionId(otherTeam) == HOVerwaltung.instance().model.getBasics().regionId)
                            info.setIsNeutral(
                                info.arenaId != HOVerwaltung.instance().model.getStadium().arenaId
                                        && info.arenaId != getArenaId(otherTeam)
                            )
                            downloadTeamLogo(otherTeam)
                        } else {
                            // Verlegenheitstruppe 08/15
                            info.setIsDerby(false)
                            info.setIsNeutral(false)
                        }
                    }
                }

                val success: Boolean
                if (info.matchStatus == MatchKurzInfo.FINISHED && !info.isObsolet) {
                    val lineup = downloadMatchlineup(matchID, info.getMatchType(), info.homeTeamID, info.guestTeamID)
                    if (lineup == null) {
                        if (!isSilentDownload()) {
                            val msg = getLangString("Downloadfehler") + " : Error fetching Matchlineup :"
                            // Info
                            setInfoMsg(msg, InfoPanel.FEHLERFARBE)
                            Helper.showMessage(
                                HOMainFrame, msg, getLangString("Fehler"),
                                JOptionPane.ERROR_MESSAGE
                            )
                        }
                        return false
                    }

                    downloadTeamRatings(matchID, info.getMatchType(), info.getHomeTeamID())
                    downloadTeamRatings(matchID, info.getMatchType(), info.getGuestTeamID())

                    // Get details with highlights.
                    HOMainFrame.setWaitInformation()
                    details = downloadMatchDetails(matchID, info.getMatchType(), lineup)

                    if (details == null) {
                        HOLogger.instance().error(
                            OnlineWorker.javaClass,
                            "Error downloading match. Details is null: $matchID"
                        )
                        return false
                    }
                    info.setDuration(details.getLastMinute())
                    info.setGuestTeamGoals(details.getGuestGoals())
                    info.setHomeTeamGoals(details.getHomeGoals())
                    info.setGuestTeamID(lineup.getGuestTeamId())
                    info.setGuestTeamName(lineup.getGuestTeamName())
                    info.setHomeTeamID(lineup.getHomeTeamId())
                    info.setHomeTeamName(lineup.getHomeTeamName())
                    success = DBManager.storeMatch(info, details, lineup)
                } else {
                    // Update arena and region ids
                    val matches = ArrayList<MatchKurzInfo>()
                    matches.add(info)
                    DBManager.storeMatchKurzInfos(matches)
                    success = true
                }
                if (!success) {
                    return false
                }
            } catch (ex: Exception) {
                HOLogger.instance().error(
                    OnlineWorker.javaClass,
                    "downloadMatchData:  Error in downloading match: $ex"
                )
                return false
            }
        }
        return true
    }

    private fun downloadTeamRatings(matchID: Int, matchType: MatchType, teamID: Int) {
        try {
            val xml = MyConnector.instance().getTeamdetails(teamID)
            val teamRating =
                MatchTeamRating(matchID, matchType, XMLTeamDetailsParser.parseTeamDetailsFromString(xml, teamID))
            DBManager.storeTeamRatings(teamRating)
        } catch (e: Exception) {
            String
            val msg = getLangString("Downloadfehler") + " : Error fetching Team ratings :"
            // Info
            setInfoMsg(msg, InfoPanel.FEHLERFARBE)
            Helper.showMessage(
                HOMainFrame, msg, getLangString("Fehler"),
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun getTeam(teamId: Int): Map<String, String> {
        val str = MyConnector.instance().fetchTeamDetails(teamId)
        return XMLTeamDetailsParser.parseTeamDetailsFromString(str, teamId)
    }

    private fun getRegionId(team: Map<String, String>): Int {
        val str = team["RegionID"]
        return str?.toInt() ?: 0
    }

    private fun getArenaId(team: Map<String, String>): Int {
        val str = team["ArenaID"]
        return str?.toInt() ?: 0
    }


    /**
     * Loads the data for the given match from HT and updates the data for this
     * match in the DB.
     *
     * @param teamId
     *            the id of the team
     * @param match
     *            the match to update
     * @return a new MatchKurzInfo object with the current data from HT or null
     *         if match could not be downloaded.
     */
    fun updateMatch(teamId: Int, match: MatchKurzInfo): MatchKurzInfo? {
        val matchDate = match.matchSchedule
        // At the moment, HT does not support getting a single match.
        val matches: List<MatchKurzInfo> = getMatches(teamId, matchDate.plus(1, ChronoUnit.MINUTES))
        for (m in matches) {
            if (m.matchID == match.matchID) {
                //DBManager.INSTANCE.updateMatchKurzInfo(m)
                return m
            }
        }
        return null
    }

    /**
     * Gets the most recent and upcoming matches for a given teamId and up to a
     * specific date. Nothing is stored to DB.
     *
     * @param teamId
     *            the id of the team.
     * @param date
     *            last date (+time) to get matches to.
     * @return the most recent and upcoming matches up to the given date.
     */
    fun getMatches(teamId: Int, date: HODateTime): List<MatchKurzInfo> {
        var matchesString: String? = null
        try {
            matchesString = MyConnector.instance().getMatches(teamId, true, date)
        } catch (e: IOException) {
            Helper.showMessage(
                HOMainFrame, getLangString("Downloadfehler")
                        + " : Error fetching matches : " + e, getLangString("Fehler"),
                JOptionPane.ERROR_MESSAGE
            )
            HOLogger.instance().log(OnlineWorker.javaClass, e)
        }

        if (matchesString != null && !StringUtils.isEmpty(matchesString)) {
            return XMLMatchesParser.parseMatchesFromString(matchesString)
        }

        return emptyList<MatchKurzInfo>()
    }

    /**
     * Download information about a given tournament
     */

    fun getTournamentDetails(tournamentId: Int): TournamentDetails? {
        var oTournamentDetails: TournamentDetails? = null
        var tournamentString = ""

        try {
            tournamentString = MyConnector.instance().getTournamentDetails(tournamentId)
        } catch (e: IOException) {
            Helper.showMessage(
                HOMainFrame, getLangString("Downloadfehler")
                        + " : Error fetching Tournament Details : " + e, getLangString("Fehler"),
                JOptionPane.ERROR_MESSAGE
            )
            HOLogger.instance().log(OnlineWorker.javaClass, e)
        }

        if (!StringUtils.isEmpty(tournamentString)) {
            oTournamentDetails = XMLTournamentDetailsParser.parseTournamentDetailsFromString(tournamentString)
        }

        return oTournamentDetails
    }

    /**
     * Download series data
     *
     * @param teamId
     *            team id (optional <1 for current team
     * @param forceRefresh (unused?!)
     * @param store
     *            true if the full match details are to be stored, false if not.
     * @param upcoming
     *            true if upcoming matches should be included
     *
     * @return The list of MatchKurzInfos found or null if an exception
     *         occurred.
     */
    fun getMatches(teamId: Int, forceRefresh: Boolean, store: Boolean, upcoming: Boolean): List<MatchKurzInfo>? {
        val matchesString: String?
        var matches: MutableList<MatchKurzInfo>? = mutableListOf()
        val bOK: Boolean
        HOMainFrame.setWaitInformation()

        try {
            matchesString = MyConnector.instance().getMatches(teamId, forceRefresh, upcoming)
            bOK = !matchesString.isNullOrEmpty()
            if (bOK)
                HOMainFrame.setWaitInformation()
        } catch (e: Exception) {
            val msg = getLangString("Downloadfehler") + " : Error fetching matches: " + e.message
            // Info
            setInfoMsg(msg, InfoPanel.FEHLERFARBE)
            Helper.showMessage(
                HOMainFrame, msg, getLangString("Fehler"),
                JOptionPane.ERROR_MESSAGE
            )
            HOLogger.instance().log(OnlineWorker.javaClass, e)
            return null
        }

        if (bOK) {
            matches?.addAll(XMLMatchesParser.parseMatchesFromString(matchesString))

            // Store in DB if store is true
            if (store) {
                HOMainFrame.setWaitInformation()
                matches = filterUserSelection(matches)
                DBManager.storeMatchKurzInfos(matches)
                HOMainFrame.setWaitInformation()

                // Automatically download additional match infos (lineup + arena)
                for (match in matches) {
                    val curMatchId = match.matchID
                    val refresh = !DBManager.isMatchInDB(curMatchId, match.getMatchType())
                            || (match.matchStatus != MatchKurzInfo.FINISHED && DBManager.hasUnsureWeatherForecast(
                        curMatchId
                    ))
                            || DBManager.matchLineupIsNotStored(match.getMatchType(), curMatchId)

                    if (refresh) {
                        // No lineup or arenaId in DB
                        val result = downloadMatchData(curMatchId, match.getMatchType(), refresh)
                        if (!result) {
                            break
                        }
                    }
                }
            }
        }
        HOMainFrame.setWaitInformation()
        return matches
    }

    fun filterUserSelection(matches: List<MatchKurzInfo>?): MutableList<MatchKurzInfo> {
        val ret = mutableListOf<MatchKurzInfo>()
        if (matches != null) {
            for (m in matches) {
                when (m.getMatchType()) {
                    MatchType.INTSPIEL, MatchType.NATIONALCOMPNORMAL, MatchType.NATIONALCOMPCUPRULES, MatchType.NATIONALFRIENDLY,
                    MatchType.PREPARATION, MatchType.LEAGUE, MatchType.QUALIFICATION, MatchType.CUP, MatchType.FRIENDLYNORMAL, MatchType.FRIENDLYCUPRULES, MatchType.INTFRIENDLYNORMAL, MatchType.INTFRIENDLYCUPRULES, MatchType.MASTERS -> {
                        if (UserParameter.instance().downloadCurrentMatchlist) {
                            ret.add(m)
                        }
                    }

                    MatchType.TOURNAMENTGROUP -> {
                        // this is TOURNAMENTGROUP but more specifically a division battle
                        if (m.getMatchTypeExtended() == MatchTypeExtended.DIVISIONBATTLE) {
                            if (UserParameter.instance().downloadDivisionBattleMatches) {
                                // we add the game only if user selected division battle category
                                ret.add(m)
                            }
                        } else {
                            // this is TOURNAMENTGROUP but not a division battle
                            if (UserParameter.instance().downloadTournamentGroupMatches) {
                                ret.add(m)
                            }
                        }
                    }

                    MatchType.TOURNAMENTPLAYOFF -> {
                        if (UserParameter.instance().downloadTournamentPlayoffMatches) {
                            ret.add(m)
                        }
                    }

                    MatchType.SINGLE -> {
                        if (UserParameter.instance().downloadSingleMatches) {
                            ret.add(m)
                        }
                    }

                    MatchType.LADDER -> {
                        if (UserParameter.instance().downloadLadderMatches) {
                            ret.add(m)
                        }
                    }

                    else ->
                        HOLogger.instance().warning(
                            OnlineWorker.javaClass,
                            "Unknown Matchtyp:" + m.getMatchType() + ". Is not downloaded!"
                        )
                }
            }
        }
        return ret
    }

    /**
     * Download match lineup
     *
     * @param matchId
     * 			Match Id
     * @param matchType
     * 			MatchType
     * @param teamId1
     * 			Id of first team to include to the returned lineup
     * @param teamId2
     * 			Optional id of second team
     * @return
     * 			MatchLineup containing specified teams
     */
    private fun downloadMatchlineup(matchId: Int, matchType: MatchType, teamId1: Int, teamId2: Int): MatchLineup? {
        var lineUp2: MatchLineup? = null

        // Wait Dialog zeigen
        HOMainFrame.setWaitInformation()

        // Lineups holen
        val lineUp1 = downloadMatchLineup(matchId, teamId1, matchType)
        if (lineUp1 != null) {
            HOMainFrame.setWaitInformation()
            if (teamId2 > 0)
                lineUp2 = downloadMatchLineup(matchId, teamId2, matchType)

            // Merge the two
            if ((lineUp2 != null)) {
                if (lineUp1.isHomeTeamNotLoaded())
                    lineUp1.setHomeTeam(lineUp2.getHomeTeam())
                else if (!lineUp1.isGuestTeamLoaded())
                    lineUp1.setGuestTeam(lineUp2.getGuestTeam())
            } else {
                // Get the 2nd lineup
                if (lineUp1.isHomeTeamNotLoaded()) {
                    lineUp2 = downloadMatchLineup(matchId, lineUp1.getHomeTeamId(), matchType)
                    if (lineUp2 != null)
                        lineUp1.setHomeTeam(lineUp2.getHomeTeam())
                } else {
                    lineUp2 = downloadMatchLineup(matchId, lineUp1.getGuestTeamId(), matchType)
                    if (lineUp2 != null)
                        lineUp1.setGuestTeam(lineUp2.getGuestTeam())
                }
            }
        }
        HOMainFrame.setWaitInformation()
        return lineUp1
    }

    /**
     * Get the Fixtures list
     *
     * @param season
     *            - The season, -1 for current
     * @param leagueID
     *            - The ID of the league to get the fixtures for
     *
     * @return true on success, false on failure
     */
    fun downloadLeagueFixtures(season: Int, leagueID: Int): Spielplan? {

        try {
            HOMainFrame.setWaitInformation()
            val leagueFixtures = MyConnector.instance().getLeagueFixtures(season, leagueID)
            HOMainFrame.setWaitInformation()
            return XMLSpielplanParser.parseSpielplanFromString(leagueFixtures)
        } catch (e: Exception) {
            HOLogger.instance().log(OnlineWorker.javaClass, e)
            val msg = getLangString("Downloadfehler") + " : Error fetching leagueFixture: " + e.message
            setInfoMsg(msg, InfoPanel.FEHLERFARBE)
            Helper.showMessage(
                HOMainFrame, msg, getLangString("Fehler"),
                JOptionPane.ERROR_MESSAGE
            )
        }
        return null
    }

    /**
     * Uploads the given order to Hattrick
     *
     * @param matchId
     *            The id of the match in question. If left at 0 the match ID
     *            from the model will be used (next match).
     * @param lineup
     *            The lineup object to be uploaded
     * @return A string response with any error message
     */

    fun uploadMatchOrder(matchId: Int, matchType: MatchType, lineup: Lineup): String {
        val orders = lineup.toJson()
        try {
            return MyConnector.instance().uploadMatchOrder(
                matchId,
                HOVerwaltung.instance().getModel().getBasics().getTeamId(),
                matchType,
                orders
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Try to recover missing matchType information by querying HT with different source system and returning first result
     *
     * @param _match the match id
     * @return the match type
     */
    fun inferMissingMatchType(_match: MatchKurzInfo): MatchKurzInfo {
        val conn = MyConnector.instance()
        conn.setSilentDownload(true)

        var details: Matchdetails?

        try {
            var matchDetails = conn.downloadMatchdetails(_match.getMatchID(), MatchType.LEAGUE)
            if ((matchDetails != null) && (!matchDetails.equals(""))) {
                details = XMLMatchdetailsParser.parseMatchDetailsFromString(matchDetails, null)
            } else {
                details = null
            }
            if (details != null) {
                if (details.getHomeTeamId() == _match.getHomeTeamID()) {
                    _match.setMatchType(details.getMatchType())
                    _match.setMatchContextId(details.getMatchContextId())
                    _match.setCupLevel(details.getCupLevel())
                    _match.setCupLevelIndex(details.getCupLevelIndex())
                    conn.setSilentDownload(false)
                    return _match
                }
            }

            matchDetails = conn.downloadMatchdetails(_match.getMatchID(), MatchType.LADDER)
            if ((matchDetails != null) && (!matchDetails.equals(""))) {
                details = XMLMatchdetailsParser.parseMatchDetailsFromString(matchDetails, null)
            } else {
                details = null
            }
            if (details != null) {
                if (details.getHomeTeamId() == _match.getHomeTeamID()) {
                    _match.setMatchType(details.getMatchType())
                    _match.setMatchContextId(details.getMatchContextId())
                    _match.setCupLevel(details.getCupLevel())
                    _match.setCupLevelIndex(details.getCupLevelIndex())
                    conn.setSilentDownload(false)
                    return _match
                }
            }

            matchDetails = conn.downloadMatchdetails(_match.getMatchID(), MatchType.YOUTHLEAGUE)
            if ((matchDetails != null) && (!matchDetails.equals(""))) {
                details = XMLMatchdetailsParser.parseMatchDetailsFromString(matchDetails, null)
            } else {
                details = null
            }
            if (details != null) {
                if (details.getHomeTeamId() == _match.getHomeTeamID()) {
                    _match.setMatchType(details.getMatchType())
                    _match.setMatchContextId(details.getMatchContextId())
                    _match.setCupLevel(details.getCupLevel())
                    _match.setCupLevelIndex(details.getCupLevelIndex())
                    conn.setSilentDownload(false)
                    return _match
                }
            }
            _match.setMatchType(MatchType.TOURNAMENTGROUP)
            _match.setTournamentTypeID(TournamentType.DIVISIONBATTLE.getId())
            _match.setisObsolet(true)
            conn.setSilentDownload(false)
            return _match
        } catch (e: Exception) {
            HOLogger.instance().error(OnlineWorker.javaClass, "can't infer MatchType of match: " + _match.getMatchID())
            _match.setMatchType(MatchType.NONE)
            conn.setSilentDownload(false)
            return _match
        }
    }

    private fun downloadMatchDetails(matchID: Int, matchType: MatchType, lineup: MatchLineup?): Matchdetails? {

        try {
            val matchDetails = MyConnector.instance().downloadMatchdetails(matchID, matchType)
            if (matchDetails.isEmpty()) {
                HOLogger.instance().warning(OnlineWorker.javaClass, "Unable to fetch details for match $matchID")
                return null
            }
            HOMainFrame.setWaitInformation()
            val details = XMLMatchdetailsParser.parseMatchDetailsFromString(matchDetails, lineup)
            HOMainFrame.setWaitInformation()
            if (details == null) {
                HOLogger.instance().warning(OnlineWorker.javaClass, "Unable to fetch details for match $matchID")
                return null
            }

            val arenaString = MyConnector.instance().downloadArena(details.arenaID)
            HOMainFrame.setWaitInformation()
            val regionIdAsString = XMLArenaParser.parseArenaFromString(arenaString)["RegionID"]
            details.regionId = regionIdAsString?.toInt() ?: -1

            return details
        } catch (e: Exception) {
            val msg = getLangString("Downloadfehler") + ": Error fetching Matchdetails XML.: "
            // Info
            setInfoMsg(msg, InfoPanel.FEHLERFARBE)
            Helper.showMessage(HOMainFrame, msg, getLangString("Fehler"), JOptionPane.ERROR_MESSAGE)

        }
        return null
    }

    fun downloadMatchLineup(matchID: Int, teamID: Int, matchType: MatchType): MatchLineup? {
        val bOK: Boolean
        val matchLineup: String?
        try {
            matchLineup = MyConnector.instance().downloadMatchLineup(matchID, teamID, matchType)
            bOK = !matchLineup.isNullOrEmpty()
        } catch (e: Exception) {
            val msg = getLangString("Downloadfehler") + " : Error fetching Matchlineup :"
            // Info
            setInfoMsg(msg, InfoPanel.FEHLERFARBE)
            Helper.showMessage(HOMainFrame, msg, getLangString("Fehler"), JOptionPane.ERROR_MESSAGE)
            return null
        }
        if (bOK) {
            return XMLMatchLineupParser.parseMatchLineupFromString(matchLineup)
        }
        return null
    }

    /**
     * Get all lineups for MatchKurzInfos, if they're not there already
     */
    fun getAllLineups(nbGames: Int?) {
        val infos = if (nbGames == null) {
            DBManager.getMatchesKurzInfo(-1)
        } else {
            DBManager.getPlayedMatchInfo(nbGames, false, ownTeam = false)
        }

        if (infos != null) {
            for (info in infos) {
                if (info != null) {
                    val curMatchId = info.matchID
                    if (!(info.isObsolet) && DBManager.matchLineupIsNotStored(info.getMatchType(), curMatchId)) {
                        if (info.matchStatus == MatchKurzInfo.FINISHED) {
                            val bOK = downloadMatchData(curMatchId, info.getMatchType(), false)
                            if (!bOK) {
                                HOLogger.instance().error(OnlineWorker.javaClass, "Error fetching Match: $curMatchId")
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param matchId
     *            The match ID for the match to download
     * @param matchType
     *            The matchTyp for the match to download
     * @return The Lineup object with the downloaded match data
     */
    fun getLineupbyMatchId(matchId: Int, matchType: MatchType): MatchLineupTeam? {

        try {
            val teamId = HOVerwaltung.instance().model.getBasics().teamId
            val xml = MyConnector.instance().downloadMatchOrder(matchId, matchType, teamId)

            if (!StringUtils.isEmpty(xml)) {
                val map = XMLMatchOrderParser.parseMatchOrderFromString(xml)
                var trainerID = "-1"
                try {
                    trainerID = HOVerwaltung.instance().model.getTrainer().playerID.toString()
                } catch (e: Exception) {
                    //It is possible that NTs struggle here.
                }
                val hrfStringBuilder = HRFStringBuilder()
                hrfStringBuilder.createLineUp(trainerID, teamId, map)
                return MatchLineupTeam(getProperties(hrfStringBuilder.createHRF().toString()))
            }
        } catch (e: Exception) {
            val msg = getLangString("Downloadfehler") + " : Error fetching Matchorder :"
            setInfoMsg(msg, InfoPanel.FEHLERFARBE)
            Helper.showMessage(
                HOMainFrame, msg, getLangString("Fehler"),
                JOptionPane.ERROR_MESSAGE
            )
            HOLogger.instance().error(OnlineWorker.javaClass, e.message)
        }

        return null
    }

    fun getRegionDetails(regionId: Int): Regiondetails? {
        try {
            val xml = MyConnector.instance().getRegion(regionId)
            if (!StringUtils.isEmpty(xml)) {
                return Regiondetails(XMLRegionParser.parseRegionDetailsFromString(xml))
            }
        } catch (e: Exception) {
            val msg = getLangString("Downloadfehler") + " : Error fetching region details :"
            setInfoMsg(msg, InfoPanel.FEHLERFARBE)
            Helper.showMessage(HOMainFrame, msg, getLangString("Fehler"), JOptionPane.ERROR_MESSAGE)
            HOLogger.instance().error(OnlineWorker.javaClass, e.message)
        }
        return null
    }

    @Throws(IOException::class)
    private fun getProperties(data: String): Properties {
        val bis = ByteArrayInputStream(data.toByteArray(charset = Charsets.UTF_8))
        val isr = InputStreamReader(bis, StandardCharsets.UTF_8)
        val hrfReader = BufferedReader(isr)
        val properties = Properties()

        // Lose the first line
        hrfReader.readLine()
        while (hrfReader.ready()) {
            val lineString = hrfReader.readLine()
            // Ignore empty lines
            if (!StringUtils.isEmpty(lineString)) {
                val indexEqualsSign = lineString.indexOf('=')
                if (indexEqualsSign > 0) {
                    properties.setProperty(
                        lineString.substring(0, indexEqualsSign).lowercase(Locale.ENGLISH),
                        lineString.substring(indexEqualsSign + 1)
                    )
                }
            }
        }
        return properties
    }

    /**
     * Shows a file chooser asking for the location for the HRF file and saves
     * it to the location chosen by the user.
     *
     * @param hrfData
     *            the HRF data as string
     */
    private fun saveHRFToFile(parent: JDialog, hrfData: String) {
        setInfoMsg(getLangString("HRFSave"))

        val path = File(UserParameter.instance().hrfImport_HRFPath)
        var file: File? = File(path, getHRFFileName())
        // Show dialog if path not set or the file already exists
        if (UserParameter.instance().showHRFSaveDialog || !path.exists() || !path.isDirectory() || file == null || file.exists()) {
            file = askForHRFPath(parent, file!!)
        }

        if (file != null) {
            // Save Path
            UserParameter.instance().hrfImport_HRFPath = file.getParentFile().absolutePath

            // File exists?
            var value = JOptionPane.OK_OPTION
            if (file.exists()) {
                value = JOptionPane.showConfirmDialog(
                    HOMainFrame,
                    getLangString("overwrite"),
                    HOVerwaltung.instance().getLanguageString("confirmation.title"),
                    JOptionPane.YES_NO_OPTION
                )
            }

            // Save
            if (value == JOptionPane.OK_OPTION) {
                try {
                    saveFile(file.getPath(), hrfData)
                } catch (e: IOException) {
                    Helper.showMessage(
                        HOMainFrame,
                        HOVerwaltung.instance()
                            .getLanguageString("Show_SaveHRF_Failed") + " " + file.getParentFile() + ".\nError: " + e.message,
                        getLangString("Fehler"), JOptionPane.ERROR_MESSAGE
                    )
                }
            } else {
                // Canceled
                setInfoMsg(getLangString("HRFAbbruch"), InfoPanel.FEHLERFARBE)
            }
        }

    }

    /**
     * Gets a HRF file name, based on the current date.
     *
     * @return the HRF file name.
     */
    private fun getHRFFileName(): String {
        val calendar = Calendar.getInstance() as GregorianCalendar
        val builder = StringBuilder()

        builder.append(HOVerwaltung.instance().model.getBasics().teamId)
        builder.append('-')

        builder.append(calendar.get(Calendar.YEAR))
        builder.append('-')
        val month = calendar.get(Calendar.MONTH) + 1
        if (month < 10) {
            builder.append('0')
        }
        builder.append(month)
        builder.append('-')
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        if (day < 10) {
            builder.append('0')
        }
        builder.append(day)
        builder.append(".hrf")
        return builder.toString()
    }

    /**
     * Shows a file chooser dialog to ask the user for the location to save the
     * HRF file.
     *
     * @param file
     *            the recommendation for the file name/location.
     * @return the file location chosen by the user or null if the canceled the
     *         dialog.
     */
    private fun askForHRFPath(parent: JDialog, file: File): File? {
        val fileChooser = JFileChooser()
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG)
        fileChooser.setDialogTitle(getLangString("FileExport"))

        val filter = ExtensionFileFilter()
        filter.addExtension("hrf")
        filter.setDescription(HOVerwaltung.instance().getLanguageString("filetypedescription.hrf"))
        fileChooser.setFileFilter(filter)

        val path = file.getParentFile()
        if (path.exists() && path.isDirectory()) {
            fileChooser.setCurrentDirectory(path)
        }
        fileChooser.setSelectedFile(file)
        val returnVal = fileChooser.showSaveDialog(parent)
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            var f = fileChooser.selectedFile
            // File doesn't end with .hrf?
            if (!f.path.endsWith(".hrf")) {
                f = File(file.absolutePath + ".hrf")
            }
            return f
        }
        return null
    }

    /**
     * Convenience method for
     * HOMainFrame.INSTANCE.getInfoPanel().setLangInfoText(msg)
     *
     * @param msg
     *            the message to show
     */
    private fun setInfoMsg(msg: String) {
        HOMainFrame.setInformation(msg)
    }

    /**
     * Convenience method for
     * HOMainFrame.INSTANCE.getInfoPanel().setLangInfoText(msg, color)
     *
     * @param msg
     *            the message to show
     * @param color
     *            the color
     */
    private fun setInfoMsg(msg: String, color: Color) {
        HOMainFrame.setInformation(msg, color)
    }

    /**
     * Convenience method for HOVerwaltung.instance().getLanguageString(key)
     *
     * @param key
     *            the key for the language string
     * @return the string for the current language
     */
    fun getLangString(key: String): String {
        return HOVerwaltung.instance().getLanguageString(key)
    }

    /**
     * Save the passed in data to the passed in file
     *
     * @param fileName Name of the file to save the data to
     * @param content  The content to write to the file
     */
    @Throws(IOException::class)
    private fun saveFile(fileName: String, content: String) {
        val outFile = File(fileName)
        if (outFile.exists()) {
            outFile.delete()
        }
        outFile.createNewFile()
        val outWrit = OutputStreamWriter(FileOutputStream(outFile), StandardCharsets.UTF_8)
        val out = BufferedWriter(outWrit)
        out.write(content)
        out.newLine()
        out.close()
    }

    fun isSilentDownload(): Boolean {
        return MyConnector.instance().isSilentDownload()
    }

    fun setSilentDownload(silentDownload: Boolean) {
        MyConnector.instance().setSilentDownload(silentDownload)
    }

    fun downloadMatchesOfSeason(teamId: Int, season: Int): List<MatchKurzInfo>? {
        try {
            val xml = MyConnector.instance().getMatchesOfSeason(teamId, season)
            return XMLMatchArchivParser.parseMatchesFromString(xml)
        } catch (exception: Exception) {
            HOLogger.instance().error(
                OnlineWorker.javaClass,
                "downloadMatchData:  Error in downloading matches of season: $exception"
            )
        }
        return null
    }

    fun downloadMissingYouthMatchData(model: HOModel, dateSince: HODateTime) {
        var startDate: HODateTime? = dateSince
        val youthTeamId = model.getBasics().youthTeamId
        val lastStoredYouthMatchDate = HODateTime.fromDbTimestamp(DBManager.getLastYouthMatchDate())

        if (lastStoredYouthMatchDate != null && lastStoredYouthMatchDate.isAfter(startDate)) {
            // if there are no youth matches in database, take the limit from arrival date of 'oldest' youth players
            startDate = lastStoredYouthMatchDate
        }

        var flag = true
        var endDate: HODateTime?
        if (startDate != null) {

            // Retrieve missing youth match details by chunks of 90 days, until now
            while (flag) {
                if (startDate == null) {
                    break
                }
                if (startDate.isBefore(HODateTime.now().minus(90, ChronoUnit.DAYS))) {
                    endDate = startDate.plus(90, ChronoUnit.DAYS)
                } else {
                    endDate = null;    // until now
                    flag = false
                }
                val mc = MyConnector.instance()
                try {
                    val xml = mc.getMatchesArchive(SourceSystem.YOUTH, youthTeamId, startDate, endDate)
                    val youthMatches = XMLMatchArchivParser.parseMatchesFromString(xml)
                    for (match in youthMatches) {
                        val lineup = downloadMatchlineup(
                            match.matchID,
                            match.getMatchType(),
                            match.homeTeamID,
                            match.guestTeamID
                        )
                        if (lineup != null) {
                            val details = downloadMatchDetails(match.matchID, match.getMatchType(), lineup)
                            DBManager.storeMatchDetails(details)
                            DBManager.storeMatchLineup(lineup, youthTeamId)
                            lineup.setMatchDetails(details)
                            model.addYouthMatchLineup(lineup)
                        }
                    }
                } catch (e: IOException) {
                    HOLogger.instance()
                        .error(OnlineWorker.javaClass, "Error retrieving Youth Match details: ${e.message}")
                }

                startDate = endDate
            }
        }
    }

    fun downloadNtTeams(ntTeams: List<NtTeamDetails>?, matches: List<MatchKurzInfo>) {
        val ret = mutableListOf<NtTeamDetails>()
        if (ntTeams != null) {
            for (team in ntTeams) {
                ret.add(downloadNtTeam(team.teamId))
            }
        }
        for (match in matches) {
            for (teamId in match.getTeamIds()) {
                if (teamId != HOVerwaltung.instance().model.getBasics().teamId) {
                    // not the own team
                    val found = ret.any { ntTeamDetails -> ntTeamDetails.teamId == teamId }
                    if (!found) {
                        // new team in match found
                        ret.add(downloadNtTeam(teamId))
                    }
                }
            }
        }
    }

    private fun downloadNtTeam(teamId: Int): NtTeamDetails {
        val xml = MyConnector.instance().downloadNtTeamDetails(teamId)
        val details = NtTeamDetails()
        details.parseDetails(xml)
        details.setHrfId(DBManager.getLatestHRF().hrfId)
        DBManager.storeNtTeamDetails(details)
        return details
    }

    fun downloadPlayerDetails(playerID: String): Player? {
        val xml = MyConnector.instance().downloadPlayerDetails(playerID)
        return XMLPlayersParser.parsePlayerDetailsFromString(xml)
    }

    fun downloadTeamLogo(team: Map<String, String>) {
        val url = team["LogoURL"]
        if (StringUtils.isEmpty(url)) return
        val teamIdString = team["TeamID"]
        val teamId = NumberUtils.toInt(teamIdString)

        DBManager.storeTeamLogoInfo(teamId, url, null)
        val logoFilename = ThemeManager.instance().getTeamLogoFilename(teamId)
        if (logoFilename != null &&
            teamId == HOVerwaltung.instance().model.getBasics().teamId &&
            !logoFilename.equals(UserManager.getCurrentUser().clubLogo)
        ) {
            UserManager.getCurrentUser().clubLogo = logoFilename
            UserManager.save()
        }
    }

    fun downloadLastLineup(matches: List<MatchKurzInfo>?, teamId: Int): MatchLineupTeam? {
        var matchLineupTeam: MatchLineupTeam? = null
        var matchLineup: MatchLineup? = null
        val lastFinishedMatch: MatchKurzInfo? =
            matches?.filter { matchKurzInfo -> matchKurzInfo.matchStatus == MatchKurzInfo.FINISHED }
                ?.maxBy { matchKurzInfo -> matchKurzInfo.matchSchedule }
        if (lastFinishedMatch != null) {
            val matchLineupString = MyConnector.instance().downloadMatchLineup(
                lastFinishedMatch.matchID,
                teamId,
                lastFinishedMatch.getMatchType()
            )
            if (matchLineupString.isNotEmpty()) {
                matchLineup = XMLMatchLineupParser.parseMatchLineupFromString(matchLineupString)
            }
        }

        var lastAttitude = 0
        var lastTactic = 0
        // Identify team, important for player ratings
        if (matchLineup != null) {
            val md = XMLMatchdetailsParser
                .parseMatchDetailsFromString(
                    MyConnector.instance().downloadMatchdetails(
                        matchLineup.getMatchID(),
                        matchLineup.getMatchTyp()
                    ), null
                )

            if (matchLineup.getHomeTeamId() == teamId) {
                matchLineupTeam = matchLineup.getHomeTeam()
                if (md != null) {
                    lastAttitude = md.getHomeEinstellung()
                    lastTactic = md.getHomeTacticType()
                }
            } else {
                matchLineupTeam = matchLineup.getGuestTeam()
                if (md != null) {
                    lastAttitude = md.getGuestEinstellung()
                    lastTactic = md.getGuestTacticType()
                }
            }
            matchLineupTeam.setMatchTeamAttitude(MatchTeamAttitude.fromInt(lastAttitude))
            matchLineupTeam.setMatchTacticType(MatchTacticType.fromInt(lastTactic))
        }

        return matchLineupTeam
    }

    fun downloadNextMatchOrder(matches: List<MatchKurzInfo>?, teamId: Int): Map<String, String> {
        try {
            // next upcoming match
            val nextMatch: MatchKurzInfo? =
                matches?.filter { matchKurzInfo -> matchKurzInfo.matchStatus == MatchKurzInfo.UPCOMING }
                    ?.minBy { matchKurzInfo -> matchKurzInfo.matchSchedule }

            if (nextMatch != null) {
                return XMLMatchOrderParser.parseMatchOrderFromString(
                    MyConnector.instance().downloadMatchOrder(
                        nextMatch.matchID, nextMatch.getMatchType(), teamId
                    )
                )
            }
        } catch (ignore: Exception) {
        }
        return SafeInsertMap()
    }

}
