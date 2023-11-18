package core.file.xml

import core.db.DBManager
import core.model.HOVerwaltung
import core.model.match.*
import core.model.player.IMatchRoleID
import core.model.player.Player
import core.util.HODateTime
import core.util.HOLogger

object MatchExporter {
    /**
     * List of useful data for export
     *
     * @param startingDate starting data to export from (for all matchTypes)
     *
     * @return List of ExportMatchData objects
     */
    fun getDataUsefulMatches(startingDate: HODateTime): List<ExportMatchData> {
        return getDataUsefulMatches(startingDate, startingDate)
    }

    private fun getDataUsefulMatches(startingDate: HODateTime, startingDateForFriendlies: HODateTime): List<ExportMatchData> {
        return getDataUsefulMatches(startingDate, startingDateForFriendlies, false)
    }

    private fun getDataUsefulMatches(
        startingDate: HODateTime,
        startingDateForFriendlies: HODateTime,
        strict: Boolean
    ): List<ExportMatchData> {
        return getDataUsefulMatches(startingDate, startingDateForFriendlies, strict, false)
    }

    /**
     * List of useful data for export
     *
     * @param startingDate starting data to export from (for non friendlies)
     * @param startingDateForFriendlies starting data to export from (for friendlies)
     * @param strict is true, export only matches *without* cards, injuries, tactical problems / overconfidence / weather SE...
     * @param skipPullBack is true, skip matches with pull back event
     *
     * @return List of ExportMatchData objects
     */
    private fun getDataUsefulMatches(
        startingDate: HODateTime,
        startingDateForFriendlies: HODateTime,
        strict: Boolean,
        skipPullBack: Boolean
    ): List<ExportMatchData> {
        HOLogger.instance().log(MatchExporter.javaClass, "Collecting MatchData")
        val export = mutableListOf<ExportMatchData>()
        val teamId = HOVerwaltung.instance().model.getBasics().teamId
        val matches = DBManager.getMatchesKurzInfo(teamId)

        //check all matches
        if (matches != null) {
            for (match in matches) {
                if (match != null) {
                    val details = DBManager.loadMatchDetails(match.getMatchType().id, match.matchID)
                    val isFriendly = match.getMatchType().isFriendly
                    if ((isValidMatch(match, details, startingDateForFriendlies, strict, skipPullBack) && isFriendly)
                        || (isValidMatch(match, details, startingDate, strict, skipPullBack) && !isFriendly)) {

                        //Nun lineup durchlaufen und Spielerdaten holen
                        val matchLineup = DBManager.getMatchLineupPlayers(details.matchID, details.matchType, teamId)
                        val lineUpISpieler = mutableMapOf<Int, Player>()

                        var dataOK = true

                        for (k in matchLineup.indices) {
                            //MatchDaten zum Player holen
                            val player = matchLineup[k]

                            if (player != null) {
                                //Bankl + verlketzte ï¿½berspringen
                                if (player.getRoleId() >= IMatchRoleID.startReserves) {
                                    continue
                                }

                                val formerPlayerData = DBManager.getSpielerAtDate(
                                    player.playerId,
                                    match.matchSchedule.toDbTimestamp()
                                )

                                //Keine Daten verfï¿½gbar ?
                                if (formerPlayerData == null) {
                                    //Abbruch
                                    dataOK = false
                                    lineUpISpieler.clear()
                                    break
                                }

                                //ISpieler in ht ablegen
                                lineUpISpieler[player.playerId] = formerPlayerData
                            }
                        }

                        //Matchdaten ablegen da einwandfrei
                        if (dataOK) {
                            val data = ExportMatchData()
                            data.details = details
                            data.info = match
                            data.players = lineUpISpieler
                            export.add(data)
                        }
                    }
                } //end For usefull Matches
            }
        }
        return export;
    }

    private fun isValidMatch(
        info: MatchKurzInfo,
        details: Matchdetails,
        startingDate: HODateTime,
        strict: Boolean,
        skipPullBack: Boolean
    ): Boolean {
        val teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId()
        if ((info.getMatchStatus() != MatchKurzInfo.FINISHED) || (details.getMatchID() == -1)) {
            HOLogger.instance().debug(MatchExporter.javaClass, "Ignoring match ${info.matchID}: not finished")
            return false
        }
        // Check for WO
        if (details.getHomeMidfield() <= 1 && details.getGuestMidfield() <= 1) {
            HOLogger.instance().debug(MatchExporter.javaClass, "Ignoring match ${info.matchID}: Walk over")
            return false
        }
        val highlights = details.downloadHighlightsIfMissing()
        //Aussortieren starten...
        if (info.getMatchSchedule().isBefore(startingDate)) { //Zu alt !!!
            return false
        }
//        else if (DBManager.getHrfIDSameTraining(info.getMatchSchedule().toDbTimestamp()) == -1) {
//            //Kein HRF gefunden
//            HOLogger.instance().debug(MatchExporter.javaClass, "Ignoring match ${info.matchID}: No matching HRF found");
//            return false
//        }
        else if (strict)//Datum i.O. weitere checks fahren
        {
            //Highlights prüfen auf Verletzung, Rote Karte, Verwirrung, Unterschätzung
            // Check Highlights for our team only

            if (MatchHelper.instance().hasRedCard(highlights, teamId)) {
                //Karten check
                HOLogger.instance()
                    .debug(MatchExporter.javaClass, "Ignoring match " + info.getMatchID() + ": Got a red card");
                return false;
            }
            //injury / tactical problems / overconfidence check
            else if (MatchHelper.instance().hasInjury(highlights, teamId)) {
                HOLogger.instance().debug(
                    MatchExporter.javaClass,
                    "Ignoring match " + info.getMatchID() + ": Injured or bruised player"
                );
                return false;
            } else if (MatchHelper.instance().hasTacticalProblems(highlights, teamId)) {
                // Tactical Problems // Verwirrung
                HOLogger.instance()
                    .debug(MatchExporter.javaClass, "Ignoring match " + info.getMatchID() + ": Tactical problems");
                return false;
            } else if (MatchHelper.instance().hasOverConfidence(highlights, teamId)) {
                // Overconfidence // Unterschaetzen
                HOLogger.instance()
                    .debug(MatchExporter.javaClass, "Ignoring match " + info.getMatchID() + ": Overconfidence");
                return false;
            } else if (MatchHelper.instance().hasWeatherSE(highlights, teamId)) {
                // Weather based SpecialEvents check (as this SE alters player ratings)
                HOLogger.instance()
                    .debug(MatchExporter.javaClass, "Ignoring match " + info.getMatchID() + ": Weather SE");
                return false;
                // Manual Substitution
            } else if (MatchHelper.instance().hasManualSubstitution(highlights, teamId)) {
                HOLogger.instance()
                    .debug(MatchExporter.javaClass, "Ignoring match " + info.getMatchID() + ": Manual Substitution");
                return false;
            }

        }
        if (skipPullBack && MatchHelper.instance().hasPullBack(highlights, teamId)) {
            HOLogger.instance().debug(MatchExporter.javaClass, "Ignoring match " + info.getMatchID() + ": Pull Back");
            return false;
        }
        //ende for highlight check

        //		HOLogger.instance().debug(MatchExporter.class, "Exporting match " + info.getMatchID());
        return true;

    }
}
