package core.db

import core.db.DBManager.PreparedStatementBuilder
import core.gui.model.ArenaStatistikModel
import core.gui.model.ArenaStatistikTableModel
import core.model.HOVerwaltung
import core.model.UserParameter
import core.model.enums.MatchType
import core.model.match.MatchKurzInfo
import core.util.HODateTime
import core.util.HOLogger
import tool.arenasizer.Stadium
import java.sql.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors
import kotlin.math.max

object StatisticQuery {
    private var getSpielerDaten4StatistikStatementBuilder: PreparedSelectINStatementBuilder? = null
    private fun getSpielerDaten4StatistikStatement(anzahl: Int): PreparedStatement? {
        if (getSpielerDaten4StatistikStatementBuilder == null || getSpielerDaten4StatistikStatementBuilder!!.anzahl != anzahl) {
            getSpielerDaten4StatistikStatementBuilder = PreparedSelectINStatementBuilder(
                "SELECT * FROM SPIELER WHERE SpielerID=? AND HRF_ID IN (" + DBManager.getPlaceholders(anzahl) + ") ORDER BY Datum DESC",
                anzahl
            )
        }
        return getSpielerDaten4StatistikStatementBuilder!!.getStatement()
    }

    private val getSpielerBewertungStatementBuilder = PreparedStatementBuilder(
        "SELECT Bewertung FROM SPIELER WHERE Bewertung>0 AND Datum>=? AND Datum<=? AND SpielerID=? ORDER BY Datum"
    )

    fun getSpielerDaten4Statistik(spielerId: Int, anzahlHRF: Int): Array<DoubleArray> {
        var curCountHrf = anzahlHRF
        val anzahlSpalten = 16
        val faktor = UserParameter.instance().FXrate
        var returnWerte = Array(0) { DoubleArray(0) }
        val vWerte = Vector<DoubleArray>()
        val hrflist = loadHrfIdPerWeekList(curCountHrf)
        if (hrflist.size < curCountHrf) curCountHrf = hrflist.size
        val params = ArrayList<Any?>()
        params.add(spielerId)
        params.addAll(hrflist)
        var rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executePreparedQuery(
            getSpielerDaten4StatistikStatement(curCountHrf), *params.toTypedArray()
        )
        if (rs != null) {
            try {
                while (rs.next()) {
                    val tempValue = DoubleArray(anzahlSpalten)
                    //faktor;
                    tempValue[0] = rs.getDouble("Marktwert")
                    tempValue[1] = rs.getDouble("Gehalt") / faktor
                    tempValue[2] = rs.getDouble("Fuehrung")
                    tempValue[3] = rs.getDouble("Erfahrung") + rs.getDouble("SubExperience")
                    tempValue[4] = rs.getDouble("Form")
                    tempValue[5] = rs.getDouble("Kondition")
                    tempValue[6] = rs.getDouble("Torwart") + rs.getDouble("SubTorwart")
                    tempValue[7] = rs.getDouble("Verteidigung") + rs.getDouble("SubVerteidigung")
                    tempValue[8] = rs.getDouble("Spielaufbau") + rs.getDouble("SubSpielaufbau")
                    tempValue[9] = rs.getDouble("Passpiel") + rs.getDouble("SubPasspiel")
                    tempValue[10] = rs.getDouble("Fluegel") + rs.getDouble("SubFluegel")
                    tempValue[11] = rs.getDouble("Torschuss") + rs.getDouble("SubTorschuss")
                    tempValue[12] = rs.getDouble("Standards") + rs.getDouble("SubStandards")
                    tempValue[13] = rs.getDouble("Bewertung") / 2.0
                    tempValue[14] = rs.getDouble("Loyalty")
                    tempValue[15] = rs.getTimestamp("Datum").getTime().toDouble()

                    //TSI, alle Marktwerte / 1000 teilen
                    if (rs.getTimestamp("Datum").before(DBManager.TSIDATE)) {
                        tempValue[0] /= 1000.0
                    }
                    vWerte.add(tempValue)
                }
                returnWerte = Array(anzahlSpalten) { DoubleArray(vWerte.size) }
                for (i in vWerte.indices) {
                    val werte = vWerte[i]

                    //Alle Ratings, die == 0 sind -> bis zu 6 Tage vorher nach Rating suchen
                    if (werte[13] == 0.0) {
                        val hrftime = Timestamp(werte[15].toLong())

                        //6 Tage vorher
                        val beforetime = Timestamp(werte[15].toLong() - 518_400_000)
                        rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter)
                            .executePreparedQuery(
                                getSpielerBewertungStatementBuilder.getStatement(), beforetime, hrftime, spielerId
                            )
                        assert(rs != null)
                        if (rs!!.next()) {
                            werte[13] = rs.getDouble("Bewertung") / 2.0
                        }
                    }
                    for (j in werte.indices) {
                        returnWerte[j][i] = werte[j]
                    }
                }
            } catch (e: Exception) {
                HOLogger.instance().log(StatisticQuery::class.java, "DatenbankZugriff.getSpielerDaten4Statistik $e")
            }
        }
        return returnWerte
    }

    /**
     * Gibt die MatchDetails zu einem Match zurück
     */
    fun getArenaStatisticsModel(iMatchType: Int): ArenaStatistikTableModel {
        val tablemodel: ArenaStatistikTableModel
        val arenamodels: Array<ArenaStatistikModel>
        var arenamodel: ArenaStatistikModel
        var sql: String
        var rs: ResultSet?
        val liste = ArrayList<ArenaStatistikModel>()
        val teamId = HOVerwaltung.instance().model.getBasics().teamId
        var maxFans = 0
        var maxArenaGroesse = 0
        try {
            sql = "SELECT " + MatchDetailsTable.Companion.TABLENAME + ".*,"
            sql += MatchesKurzInfoTable.Companion.TABLENAME + ".MatchTyp, "
            sql += MatchesKurzInfoTable.Companion.TABLENAME + ".status "
            sql += " FROM " + MatchesKurzInfoTable.Companion.TABLENAME + " INNER JOIN  " + MatchDetailsTable.Companion.TABLENAME + " on " + MatchesKurzInfoTable.Companion.TABLENAME + ".matchid = " + MatchDetailsTable.Companion.TABLENAME + ".matchid "
            sql += " WHERE  Arenaname in (SELECT DISTINCT Stadionname as Arenaname FROM " + StadionTable.Companion.TABLENAME + ") AND " + MatchDetailsTable.Companion.TABLENAME + ".HeimID = " + teamId + " AND Status=" + MatchKurzInfo.FINISHED
            sql += MatchesKurzInfoTable.Companion.getMatchTypWhereClause(iMatchType).toString()
            sql += " ORDER BY MatchDate DESC"
            rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executeQuery(sql)
            assert(rs != null)
            while (rs!!.next()) {
                //Paarung auslesen
                arenamodel = ArenaStatistikModel()
                arenamodel.matchDate = HODateTime.fromDbTimestamp(rs.getTimestamp("SpielDatum"))
                arenamodel.gastName = rs.getString("GastName")
                arenamodel.heimName = rs.getString("HeimName")
                arenamodel.matchID = rs.getInt("MatchID")
                arenamodel.gastTore = rs.getInt("GastTore")
                arenamodel.heimTore = rs.getInt("HeimTore")
                arenamodel.matchTyp = MatchType.getById(rs.getInt("MatchTyp"))
                arenamodel.matchStatus = rs.getInt("Status")
                arenamodel.setTerraces(rs.getInt("soldTerraces"))
                arenamodel.setBasics(rs.getInt("soldBasic"))
                arenamodel.setRoof(rs.getInt("soldRoof"))
                arenamodel.setVip(rs.getInt("soldVIP"))
                arenamodel.zuschaueranzahl = rs.getInt("Zuschauer")
                arenamodel.wetter = rs.getInt("WetterId")
                liste.add(arenamodel)
            }
        } catch (e: Exception) {
            HOLogger.instance().log(StatisticQuery::class.java, e)
        }
        arenamodels = liste.toTypedArray<ArenaStatistikModel>()


        // Jetzt noch die Arenadate für die Zeit holen
        for (arenaStatistikModel in arenamodels) {
            val hrfid: Int = DBManager.getHRFID4Date(arenaStatistikModel.matchDate.toDbTimestamp())
            var stadium: Stadium? = DBManager.getStadion(hrfid)
            if (stadium != null) {
                arenaStatistikModel.arenaGroesse = stadium.totalSize()
                arenaStatistikModel.maxTerraces = stadium.standing
                arenaStatistikModel.maxBasic = stadium.basicSeating
                arenaStatistikModel.maxRoof = stadium.seatingUnderRoof
                arenaStatistikModel.maxVip = stadium.vip
                maxArenaGroesse = max(arenaStatistikModel.arenaGroesse.toDouble(), maxArenaGroesse.toDouble())
                    .toInt()
            }
            if (arenaStatistikModel.zuschaueranzahl > arenaStatistikModel.arenaGroesse) {
                stadium = DBManager.getStadion(hrfid + 1)
                if (stadium != null) {
                    arenaStatistikModel.arenaGroesse = stadium.totalSize()
                    arenaStatistikModel.maxTerraces = stadium.standing
                    arenaStatistikModel.maxBasic = stadium.basicSeating
                    arenaStatistikModel.maxRoof = stadium.seatingUnderRoof
                    arenaStatistikModel.maxVip = stadium.vip
                    maxArenaGroesse = max(arenaStatistikModel.arenaGroesse.toDouble(), maxArenaGroesse.toDouble())
                        .toInt()
                }
            }
            try {
                // Fan count
                sql = "SELECT Fans FROM " + VereinTable.Companion.TABLENAME + " WHERE HRF_ID=" + hrfid
                rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executeQuery(sql)
                if (rs!!.next()) {
                    arenaStatistikModel.fans = rs.getInt("Fans")
                    maxFans = max(arenaStatistikModel.fans.toDouble(), maxFans.toDouble()).toInt()
                }
                rs.close()

                //Fan satisfaction
                sql = "SELECT SupportersPopularity FROM " + EconomyTable.Companion.TABLENAME + " WHERE HRF_ID=" + hrfid
                rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executeQuery(sql)
                assert(rs != null)
                if (rs!!.next()) {
                    arenaStatistikModel.fanZufriedenheit = rs.getInt("SupportersPopularity")
                }
                rs.close()

                //Ligaplatz
                sql = "SELECT Platz FROM " + LigaTable.Companion.TABLENAME + " WHERE HRF_ID=" + hrfid
                rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executeQuery(sql)
                assert(rs != null)
                if (rs!!.next()) {
                    arenaStatistikModel.ligaPlatz = rs.getInt("Platz")
                }
                rs.close()
            } catch (e: Exception) {
                HOLogger.instance().log(StatisticQuery::class.java, e)
            }
        }
        tablemodel = ArenaStatistikTableModel(arenamodels, maxArenaGroesse, maxFans)
        return tablemodel
    }

    fun getDataForTeamStatisticsPanel(nbHRF: Int, group: String): Array<DoubleArray> {
        val factor = UserParameter.instance().FXrate
        var returnValues = Array(0) { DoubleArray(0) }
        val values = Vector<DoubleArray>()
        val nbColumns = 29
        val nbColumnsHRF = (nbColumns - 1) / 2
        var statement = "SELECT * FROM SPIELER"

        //One group selected
        statement += if (group != "") {
            ", SPIELERNOTIZ WHERE (SPIELERID IN (SELECT SPIELERID FROM SPIELER WHERE (HRF_ID = (SELECT MAX(HRF_ID) FROM HRF)))) AND (SPIELERNOTIZ.TeamInfoSmilie='$group') AND (SPIELERNOTIZ.SpielerID=SPIELER.SpielerID) AND"
        } else {
            " WHERE "
        }
        statement += (" Trainer=0 AND SPIELER.HRF_ID IN ("
                + loadHrfIdPerWeekList(nbHRF).stream()
            .map<String> { obj: Int? -> java.lang.String.valueOf(obj) }.collect(Collectors.joining(","))
                + ") ORDER BY Datum DESC")
        try {
            val rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter)
                .executeQuery(statement)
            if (rs != null) {
                var lastHRFID = -1
                var nbPlayersInHRF = 0
                var allValues = DoubleArray(nbColumns)
                while (rs.next()) {
                    val thisHRFvalues = DoubleArray(nbColumnsHRF)
                    thisHRFvalues[0] = rs.getDouble("Fuehrung") //Leadership
                    thisHRFvalues[1] = rs.getDouble("Erfahrung") //Experience
                    thisHRFvalues[2] = rs.getDouble("Form")
                    thisHRFvalues[3] = rs.getDouble("Kondition") //Stamina
                    thisHRFvalues[4] = rs.getDouble("Torwart") + rs.getDouble("SubTorwart") //Goalkeeper
                    thisHRFvalues[5] = rs.getDouble("Verteidigung") + rs.getDouble("SubVerteidigung") //Defence
                    thisHRFvalues[6] = rs.getDouble("Spielaufbau") + rs.getDouble("SubSpielaufbau") //Playmaking
                    thisHRFvalues[7] = rs.getDouble("Passpiel") + rs.getDouble("SubPasspiel") // Passing
                    thisHRFvalues[8] = rs.getDouble("Fluegel") + rs.getDouble("SubFluegel") // Winger
                    thisHRFvalues[9] = rs.getDouble("Torschuss") + rs.getDouble("SubTorschuss") //Scoring
                    thisHRFvalues[10] = rs.getDouble("Standards") + rs.getDouble("SubStandards") //SetPieces
                    thisHRFvalues[11] = rs.getDouble("Loyalty")
                    thisHRFvalues[12] = rs.getDouble("Marktwert") //TSI
                    if (rs.getTimestamp("Datum").before(DBManager.TSIDATE)) {
                        thisHRFvalues[12] /= 1000.0
                    }
                    thisHRFvalues[13] = rs.getDouble("Gehalt") / factor // Wage
                    //Initialisation
                    if (lastHRFID == -1) {
                        //Reset HRFID, necessary, if last record was -1
                        lastHRFID = rs.getInt("HRF_ID")

                        //initialze sum values
                        Arrays.fill(allValues, 0.0)
                    }

                    //New HRF begins
                    if (lastHRFID != rs.getInt("HRF_ID")) {
                        //sum values divided by number of players per HRF
                        for (i in nbColumnsHRF until nbColumns - 1) {
                            allValues[i] = allValues[i - nbColumnsHRF] / nbPlayersInHRF
                        }

                        //save sum values
                        values.add(allValues)

                        //---Prepared for new HRF----
                        lastHRFID = rs.getInt("HRF_ID")
                        allValues = DoubleArray(nbColumns)

                        //initialze sum values
                        Arrays.fill(allValues, 0.0)
                        nbPlayersInHRF = 0
                    }

                    //Add all temp values to the total values
                    for (i in 0 until nbColumnsHRF) {
                        allValues[i] += thisHRFvalues[i]
                    }

                    //Datum
                    allValues[nbColumns - 1] = rs.getTimestamp("Datum").getTime().toDouble()

                    // Increase number of players per HRF
                    nbPlayersInHRF++
                }

                //take over the last values
                //sum values divided by number of players per HRF
                for (i in 0 until allValues.size - 1) {
                    allValues[i] = allValues[i] / nbPlayersInHRF
                }

                //summenwerte speichern
                values.add(allValues)
                returnValues = Array(nbColumns) { DoubleArray(values.size - 1) }
                for (i in 0 until values.size - 1) {
                    val werte = values[i]
                    for (j in werte.indices) {
                        returnValues[j][i] = werte[j]
                    }
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(StatisticQuery::class.java, "DatenbankZugriff.getSpielerDaten4Statistik $e")
        }
        return returnValues
    }

    private var getDataForClubStatisticsPanelStatementBuilder: PreparedSelectINStatementBuilder? = null
    private fun getGetDataForClubStatisticsPanelStatement(anzahl: Int): PreparedStatement? {
        if (getDataForClubStatisticsPanelStatementBuilder == null || getDataForClubStatisticsPanelStatementBuilder!!.anzahl != anzahl) {
            getDataForClubStatisticsPanelStatementBuilder = PreparedSelectINStatementBuilder(
                "SELECT * FROM VEREIN INNER JOIN HRF on VEREIN.HRF_ID = HRF.HRF_ID WHERE HRF.HRF_ID IN (" +
                        DBManager.getPlaceholders(anzahl) +
                        ") ORDER BY HRF.DATUM ASC",
                anzahl
            )
        }
        return getDataForClubStatisticsPanelStatementBuilder!!.getStatement()
    }

    // The data returned by this function are displayed in the Club tab of the statistics module
    fun getDataForClubStatisticsPanel(iNumberHRF: Int): Array<DoubleArray> {
        var numberHRF = iNumberHRF
        val iNumberColumns = 12
        val returnValues: Array<DoubleArray>
        val values = Vector<DoubleArray>()
        val hrflist = loadHrfIdPerWeekList(numberHRF)
        if (hrflist.size < numberHRF) numberHRF = hrflist.size
        try {
            val rs = DBManager.jdbcAdapter.executePreparedQuery(
                getGetDataForClubStatisticsPanelStatement(numberHRF), *hrflist.toTypedArray()
            )
                ?: return Array<DoubleArray>(0) { DoubleArray(0) }
            var tempValues: DoubleArray
            while (rs.next()) {
                tempValues = DoubleArray(iNumberColumns)
                tempValues[0] = rs.getDouble("COTrainer") // AssistantTrainerLevels
                tempValues[1] = rs.getDouble("Finanzberater") // FinancialDirectorLevels
                tempValues[2] = rs.getDouble("FormAssist") // FormCoachLevels
                tempValues[3] = rs.getDouble("Aerzte") // DoctorLevel
                tempValues[4] = rs.getDouble("PRManager") // SpokespersonLevel
                tempValues[5] = rs.getDouble("Pschyologen") // SportPsychologistLevel
                tempValues[6] = rs.getDouble("TacticAssist") // TacticalAssistantLevel
                tempValues[7] = rs.getDouble("Fans") // FanClubSize
                tempValues[8] = rs.getDouble("globalranking") // GlobalRanking
                tempValues[9] = rs.getDouble("leagueranking") // LeagueRanking
                tempValues[10] = rs.getDouble("powerrating") // PowerRating
                tempValues[11] = rs.getTimestamp("DATUM").getTime().toDouble()

                //save values
                values.add(tempValues)
            }

            // copy values into returnValues
            returnValues = Array(iNumberColumns) { DoubleArray(values.size) }
            for (i in values.indices) {
                val werte = values[i]
                for (j in werte.indices) {
                    returnValues[j][i] = werte[j]
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(StatisticQuery::class.java, e)
            return Array(0) { DoubleArray(0) }
        }
        return returnValues
    }

    private val getDataForFinancesStatisticsPanelBuilder = PreparedStatementBuilder(
        "SELECT * FROM ECONOMY WHERE FetchedDate >= ? ORDER BY FetchedDate DESC"
    )

    // The data returned by this function are displayed in the Finance tab of the statistics module
    fun getDataForFinancesStatisticsPanel(iNumberWeeks: Int): Array<DoubleArray> {
        val iNumberColumns = 18
        val fxRate = UserParameter.instance().FXrate
        val returnValues: Array<DoubleArray>
        val values = Vector<DoubleArray>()
        try {
            val from = HODateTime.now().minus(iNumberWeeks * 7, ChronoUnit.DAYS)
            val rs =
                Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executePreparedQuery(
                    getDataForFinancesStatisticsPanelBuilder.getStatement(), from.toDbTimestamp()
                )
                    ?: return Array<DoubleArray>(0) { DoubleArray(0) }
            var tempValues: DoubleArray
            while (rs.next()) {
                tempValues = DoubleArray(iNumberColumns)
                tempValues[0] = rs.getDouble("Cash") / fxRate
                tempValues[1] = rs.getDouble("IncomeSponsors") / fxRate
                tempValues[2] = rs.getDouble("CostsPlayers") / fxRate
                tempValues[3] = rs.getDouble("IncomeSum") / fxRate
                tempValues[4] = rs.getDouble("CostsSum") / fxRate
                tempValues[5] = tempValues[4] - tempValues[3]
                tempValues[6] =
                    tempValues[3] - (rs.getDouble("IncomeSoldPlayers") + rs.getDouble("IncomeSoldPlayersCommission")) / fxRate
                tempValues[7] = tempValues[4] - rs.getDouble("CostsBoughtPlayers") / fxRate
                tempValues[8] = tempValues[7] - tempValues[6]
                tempValues[9] = rs.getDouble("IncomeSpectators") / fxRate
                tempValues[10] = rs.getDouble("IncomeSoldPlayers") / fxRate
                tempValues[11] = rs.getDouble("IncomeSoldPlayersCommission") / fxRate
                tempValues[12] =
                    rs.getDouble("IncomeSum") / fxRate - (tempValues[10] + tempValues[11] + tempValues[1] + tempValues[9]) // Income Other
                tempValues[13] = rs.getDouble("CostsArena") / fxRate
                tempValues[14] = rs.getDouble("CostsBoughtPlayers") / fxRate
                tempValues[15] = rs.getDouble("CostsStaff") / fxRate
                tempValues[16] =
                    rs.getDouble("CostsSum") / fxRate - (tempValues[2] + tempValues[13] + tempValues[14] + tempValues[15]) // Costs Other
                tempValues[17] =
                    rs.getTimestamp("FetchedDate").getTime().toDouble() // TODO: convert to String: HT Season - HTWeek

                //save values
                values.add(tempValues)
            }

            // copy values into returnValues
            returnValues = Array(iNumberColumns) { DoubleArray(values.size) }
            for (i in values.indices) {
                val werte = values[i]
                for (j in werte.indices) {
                    returnValues[j][i] = werte[j]
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(StatisticQuery::class.java, e)
            return Array(0) { DoubleArray(0) }
        }
        return returnValues
    }

    /**
     * Get a list of HRF Ids of the last weeks
     * only one id per week is returned
     *
     * @param nWeeks number of weeks
     * @return comma separated list of hrf ids
     */
    private fun loadHrfIdPerWeekList(nWeeks: Int): List<Int?> {
        return DBManager.loadHrfIdPerWeekList(nWeeks)
    }

    internal class PreparedSelectINStatementBuilder(sql: String, val anzahl: Int) : PreparedStatementBuilder(sql)
}
