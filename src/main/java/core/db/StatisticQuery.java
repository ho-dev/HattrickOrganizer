package core.db;

import core.gui.model.ArenaStatistikModel;
import core.gui.model.ArenaStatistikTableModel;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


public class StatisticQuery {

	static class PreparedSelectINStatementBuilder extends DBManager.PreparedStatementBuilder{
		private final int anzahl;

		public PreparedSelectINStatementBuilder(String sql, int anzahl ) {
			super(sql);
			this.anzahl=anzahl;
		}

		public int getAnzahl() {
			return anzahl;
		}
	}

	private static PreparedSelectINStatementBuilder getSpielerDaten4StatistikStatementBuilder=null;
	private static PreparedStatement getSpielerDaten4StatistikStatement(int anzahl){
		if ( getSpielerDaten4StatistikStatementBuilder == null || getSpielerDaten4StatistikStatementBuilder.getAnzahl() != anzahl){
			getSpielerDaten4StatistikStatementBuilder= new PreparedSelectINStatementBuilder(
					"SELECT * FROM SPIELER WHERE SpielerID=? AND HRF_ID IN (" + DBManager.getPlaceholders(anzahl) + ") ORDER BY Datum DESC",
					anzahl);
		}
		return getSpielerDaten4StatistikStatementBuilder.getStatement();
	}
	private static final DBManager.PreparedStatementBuilder getSpielerBewertungStatementBuilder =
			new DBManager.PreparedStatementBuilder(
					"SELECT Bewertung FROM SPIELER WHERE Bewertung>0 AND Datum>=? AND Datum<=? AND SpielerID=? ORDER BY Datum");
	public static double[][] getSpielerDaten4Statistik(int spielerId, int anzahlHRF) {
		final int anzahlSpalten = 16;
		final float faktor = core.model.UserParameter.instance().FXrate;

		double[][] returnWerte = new double[0][0];
		final Vector<double[]> vWerte = new Vector<>();
		var hrflist = loadHrfIdPerWeekList(anzahlHRF);
		if ( hrflist.size()< anzahlHRF) anzahlHRF = hrflist.size();
		var params = new ArrayList<>();
		params.add(spielerId);
		params.addAll(hrflist);

		var rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executePreparedQuery(getSpielerDaten4StatistikStatement(anzahlHRF), params.toArray() );
		if (rs != null) {
			try {
				while (rs.next()) {
					final double[] tempwerte = new double[anzahlSpalten];
					//faktor;
					tempwerte[0] = rs.getDouble("Marktwert");
					tempwerte[1] = rs.getDouble("Gehalt") / faktor;
					tempwerte[2] = rs.getDouble("Fuehrung");
					tempwerte[3] = rs.getDouble("Erfahrung") + rs.getDouble("SubExperience");
					tempwerte[4] = rs.getDouble("Form");
					tempwerte[5] = rs.getDouble("Kondition");
					tempwerte[6] = rs.getDouble("Torwart") + rs.getDouble("SubTorwart");
					tempwerte[7] = rs.getDouble("Verteidigung") + rs.getDouble("SubVerteidigung");
					tempwerte[8] = rs.getDouble("Spielaufbau") + rs.getDouble("SubSpielaufbau");
					tempwerte[9] = rs.getDouble("Passpiel") + rs.getDouble("SubPasspiel");
					tempwerte[10] = rs.getDouble("Fluegel") + rs.getDouble("SubFluegel");
					tempwerte[11] = rs.getDouble("Torschuss") + rs.getDouble("SubTorschuss");
					tempwerte[12] = rs.getDouble("Standards") + rs.getDouble("SubStandards");
					tempwerte[13] = rs.getDouble("Bewertung") / 2d;
					tempwerte[14] = rs.getDouble("Loyalty");
					tempwerte[15] = rs.getTimestamp("Datum").getTime();
					
					//TSI, alle Marktwerte / 1000 teilen
					if (rs.getTimestamp("Datum").before(DBManager.TSIDATE)) {
						tempwerte[0] /= 1000d;
					}

					vWerte.add(tempwerte);
				}

				returnWerte = new double[anzahlSpalten][vWerte.size()];
				for (int i = 0; i < vWerte.size(); i++) {
					final double[] werte = vWerte.get(i);

					//Alle Ratings, die == 0 sind -> bis zu 6 Tage vorher nach Rating suchen
					if (werte[13] == 0) {
						final Timestamp hrftime = new Timestamp((long) werte[15]);

						//6 Tage vorher
						final Timestamp beforetime = new Timestamp((long) werte[15] - 518400000);
						rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executePreparedQuery(getSpielerBewertungStatementBuilder.getStatement(), beforetime, hrftime, spielerId);
						//Wert gefunden
						assert rs != null;
						if (rs.next()) {
							werte[13] = rs.getDouble("Bewertung") / 2d;
						}
					}

					for (int j = 0; j < werte.length; j++) {
						returnWerte[j][i] = werte[j];
					}
				}
			} catch (Exception e) {
				HOLogger.instance().log(StatisticQuery.class, "DatenbankZugriff.getSpielerDaten4Statistik " + e);
			}
		}

		return returnWerte;
	}

	/**
	 * Gibt die MatchDetails zu einem Match zurück
	 */
	public static ArenaStatistikTableModel getArenaStatisticsModel(int iMatchType) {
		ArenaStatistikTableModel tablemodel;

		ArenaStatistikModel[] arenamodels;
		ArenaStatistikModel arenamodel;
		String sql;
		ResultSet rs;
		final ArrayList<ArenaStatistikModel> liste = new ArrayList<>();
		final int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		int maxFans = 0;
		int maxArenaGroesse = 0;

		try {
			sql = "SELECT " + MatchDetailsTable.TABLENAME + ".*,";
			sql += MatchesKurzInfoTable.TABLENAME + ".MatchTyp, ";
			sql += MatchesKurzInfoTable.TABLENAME + ".status ";
			sql += " FROM " + MatchesKurzInfoTable.TABLENAME + " INNER JOIN  " + MatchDetailsTable.TABLENAME + " on " + MatchesKurzInfoTable.TABLENAME + ".matchid = " + MatchDetailsTable.TABLENAME + ".matchid ";
			sql += " WHERE  Arenaname in (SELECT DISTINCT Stadionname as Arenaname FROM " + StadionTable.TABLENAME + ") AND " + MatchDetailsTable.TABLENAME + ".HeimID = " + teamId + " AND Status=" + MatchKurzInfo.FINISHED;

			sql += MatchesKurzInfoTable.getMatchTypWhereClause(iMatchType).toString();

			sql += " ORDER BY MatchDate DESC";

			rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executeQuery(sql);

			assert rs != null;
			while (rs.next()) {
				//Paarung auslesen
				arenamodel = new core.gui.model.ArenaStatistikModel();
				arenamodel.setMatchDate(HODateTime.fromDbTimestamp(rs.getTimestamp("SpielDatum")));
				arenamodel.setGastName(rs.getString("GastName"));
				arenamodel.setHeimName(rs.getString("HeimName"));
				arenamodel.setMatchID(rs.getInt("MatchID"));
				arenamodel.setGastTore(rs.getInt("GastTore"));
				arenamodel.setHeimTore(rs.getInt("HeimTore"));
				arenamodel.setMatchTyp(MatchType.getById(rs.getInt("MatchTyp")));
				arenamodel.setMatchStatus(rs.getInt("Status"));
				arenamodel.setTerraces(rs.getInt("soldTerraces"));
				arenamodel.setBasics(rs.getInt("soldBasic"));
				arenamodel.setRoof(rs.getInt("soldRoof"));
				arenamodel.setVip(rs.getInt("soldVIP"));
				arenamodel.setZuschaueranzahl(rs.getInt("Zuschauer"));
				arenamodel.setWetter(rs.getInt("WetterId"));
				liste.add(arenamodel);
			}
		} catch (Exception e) {
			HOLogger.instance().log(StatisticQuery.class, e);
		}

		arenamodels = liste.toArray(new ArenaStatistikModel[0]);
		

		// Jetzt noch die Arenadate für die Zeit holen
		for (ArenaStatistikModel arenaStatistikModel : arenamodels) {
			final int hrfid = DBManager.instance().getHRFID4Date(arenaStatistikModel.getMatchDate().toDbTimestamp());

			try {
				//Get the stadium capacities
				sql = "SELECT GesamtGr, AnzSteh, AnzSitz , AnzDach , AnzLogen FROM " + StadionTable.TABLENAME + " WHERE HRF_ID=" + hrfid;
				rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executeQuery(sql);
				assert rs != null;
				if (rs.next()) {
					arenaStatistikModel.setArenaGroesse(rs.getInt("GesamtGr"));
					arenaStatistikModel.setMaxTerraces(rs.getInt("AnzSteh"));
					arenaStatistikModel.setMaxBasic(rs.getInt("AnzSitz"));
					arenaStatistikModel.setMaxRoof(rs.getInt("AnzDach"));
					arenaStatistikModel.setMaxVip(rs.getInt("AnzLogen"));
					maxArenaGroesse = Math.max(arenaStatistikModel.getArenaGroesse(), maxArenaGroesse);
				}
				rs.close();

				// fix bug when visitors exceed the stadium size
				try {
					if (arenaStatistikModel.getZuschaueranzahl() > arenaStatistikModel.getArenaGroesse()) {
						rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executeQuery("SELECT GesamtGr, AnzSteh, AnzSitz , AnzDach , AnzLogen FROM " + StadionTable.TABLENAME + " WHERE HRF_ID=" + (hrfid + 1));
						assert rs != null;
						if (rs.next()) {
							arenaStatistikModel.setArenaGroesse(rs.getInt("GesamtGr"));
							arenaStatistikModel.setMaxTerraces(rs.getInt("AnzSteh"));
							arenaStatistikModel.setMaxBasic(rs.getInt("AnzSitz"));
							arenaStatistikModel.setMaxRoof(rs.getInt("AnzDach"));
							arenaStatistikModel.setMaxVip(rs.getInt("AnzLogen"));
							maxArenaGroesse = Math.max(arenaStatistikModel.getArenaGroesse(), maxArenaGroesse);
						}
					}
				} catch (Exception e) {
					HOLogger.instance().log(StatisticQuery.class, "Error(>100% handling): " + e);
				} finally {
					if (rs != null) rs.close();
				}

				//Fananzahl
				sql = "SELECT Fans FROM " + VereinTable.TABLENAME + " WHERE HRF_ID=" + hrfid;
				rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executeQuery(sql);
				assert rs != null;
				if (rs.next()) {
					arenaStatistikModel.setFans(rs.getInt("Fans"));
					maxFans = Math.max(arenaStatistikModel.getFans(), maxFans);
				}
				rs.close();

				//Fan satisfaction
				sql = "SELECT SupportersPopularity FROM " + EconomyTable.TABLENAME + " WHERE HRF_ID=" + hrfid;
				rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executeQuery(sql);
				assert rs != null;
				if (rs.next()) {
					arenaStatistikModel.setFanZufriedenheit(rs.getInt("SupportersPopularity"));
				}
				rs.close();

				//Ligaplatz
				sql = "SELECT Platz FROM " + LigaTable.TABLENAME + " WHERE HRF_ID=" + hrfid;
				rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executeQuery(sql);
				assert rs != null;
				if (rs.next()) {
					arenaStatistikModel.setLigaPlatz(rs.getInt("Platz"));
				}
				rs.close();
			} catch (Exception e) {
				HOLogger.instance().log(StatisticQuery.class, e);
			}
		}

        tablemodel = new ArenaStatistikTableModel(arenamodels, maxArenaGroesse, maxFans);

		return tablemodel;
	}

	public static double[][] getDataForTeamStatisticsPanel(int nbHRF, String group) {
		final float factor = core.model.UserParameter.instance().FXrate;
		double[][] returnValues = new double[0][0];
		final Vector<double[]> values = new Vector<>();
		final int nbColumns = 29;
		final int nbColumnsHRF = (nbColumns - 1) / 2;

		String statement = "SELECT * FROM SPIELER";

		//One group selected
		if (!group.equals("")) {
			statement += (", SPIELERNOTIZ WHERE (SPIELERID IN (SELECT SPIELERID FROM SPIELER WHERE (HRF_ID = (SELECT MAX(HRF_ID) FROM HRF)))) AND (SPIELERNOTIZ.TeamInfoSmilie='" + group + "') AND (SPIELERNOTIZ.SpielerID=SPIELER.SpielerID) AND");
		} else {
			statement += " WHERE ";
		}


		statement += (" Trainer=0 AND SPIELER.HRF_ID IN ("
				+ loadHrfIdPerWeekList(nbHRF).stream().map(String::valueOf).collect(Collectors.joining(","))
				+ ") ORDER BY Datum DESC");

		try {
			final ResultSet rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executeQuery(statement);

			if (rs != null) {
				int lastHRFID = -1;
				int nbPlayersInHRF = 0;
				double[] allValues = new double[nbColumns];

				while (rs.next()) {
					final double[] thisHRFvalues = new double[nbColumnsHRF];
					thisHRFvalues[0] = rs.getDouble("Fuehrung");  //Leadership
					thisHRFvalues[1] = rs.getDouble("Erfahrung"); //Experience
					thisHRFvalues[2] = rs.getDouble("Form");
					thisHRFvalues[3] = rs.getDouble("Kondition"); //Stamina
					thisHRFvalues[4] = rs.getDouble("Torwart") + rs.getDouble("SubTorwart");  //Goalkeeper
					thisHRFvalues[5] = rs.getDouble("Verteidigung") + rs.getDouble("SubVerteidigung"); //Defence
					thisHRFvalues[6] = rs.getDouble("Spielaufbau") + rs.getDouble("SubSpielaufbau"); //Playmaking
					thisHRFvalues[7] = rs.getDouble("Passpiel") + rs.getDouble("SubPasspiel"); // Passing
					thisHRFvalues[8] = rs.getDouble("Fluegel") + rs.getDouble("SubFluegel"); // Winger
					thisHRFvalues[9] = rs.getDouble("Torschuss") + rs.getDouble("SubTorschuss"); //Scoring
					thisHRFvalues[10] = rs.getDouble("Standards") + rs.getDouble("SubStandards"); //SetPieces
					thisHRFvalues[11] = rs.getDouble("Loyalty");
					thisHRFvalues[12] = rs.getDouble("Marktwert"); //TSI
					if (rs.getTimestamp("Datum").before(DBManager.TSIDATE)) {
						thisHRFvalues[12] /= 1000d;
					}
					thisHRFvalues[13] = rs.getDouble("Gehalt") / factor; // Wage
					//Initialisation
					if (lastHRFID == -1) {
						//Reset HRFID, necessary, if last record was -1
						lastHRFID = rs.getInt("HRF_ID");

						//initialze sum values
						Arrays.fill(allValues, 0.0d);
					}

					//New HRF begins
					if (lastHRFID != rs.getInt("HRF_ID")) {
						//sum values divided by number of players per HRF
						for (int i = nbColumnsHRF; i < (nbColumns - 1); i++) {
							allValues[i] = allValues[i - nbColumnsHRF] / nbPlayersInHRF;
						}

						//save sum values
						values.add(allValues);

						//---Prepared for new HRF----
						lastHRFID = rs.getInt("HRF_ID");

						allValues = new double[nbColumns];

						//initialze sum values
						Arrays.fill(allValues, 0.0d);

						nbPlayersInHRF = 0;
					}

					//Add all temp values to the total values
					for (int i = 0; i < nbColumnsHRF; i++) {
						allValues[i] += thisHRFvalues[i];
					}

					//Datum
					allValues[nbColumns - 1] = rs.getTimestamp("Datum").getTime();

					// Increase number of players per HRF
					nbPlayersInHRF++;
				}

				//take over the last values
				//sum values divided by number of players per HRF
				for (int i = 0; i < (allValues.length - 1); i++) {
					allValues[i] = allValues[i] / nbPlayersInHRF;
				}

				//summenwerte speichern
				values.add(allValues);

				returnValues = new double[nbColumns][values.size() - 1];

				for (int i = 0; i < values.size() - 1; i++) {
					final double[] werte = values.get(i);

					for (int j = 0; j < werte.length; j++) {
						returnValues[j][i] = werte[j];
					}
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(StatisticQuery.class, "DatenbankZugriff.getSpielerDaten4Statistik " + e);
		}
		return returnValues;
	}

	private static PreparedSelectINStatementBuilder getDataForClubStatisticsPanelStatementBuilder=null;
	private static PreparedStatement getGetDataForClubStatisticsPanelStatement(int anzahl){
		if ( getDataForClubStatisticsPanelStatementBuilder==null || getDataForClubStatisticsPanelStatementBuilder.getAnzahl()!=anzahl){
			getDataForClubStatisticsPanelStatementBuilder = new PreparedSelectINStatementBuilder(
					"SELECT * FROM VEREIN INNER JOIN HRF on VEREIN.HRF_ID = HRF.HRF_ID WHERE HRF.HRF_ID IN (" +
							DBManager.getPlaceholders(anzahl) +
							") ORDER BY HRF.DATUM ASC",
					anzahl
					);
		}
		return getDataForClubStatisticsPanelStatementBuilder.getStatement();
	}

	// The data returned by this function are displayed in the Club tab of the statistics module
	public static double[][] getDataForClubStatisticsPanel(int iNumberHRF) {
		final int iNumberColumns = 12;
		double[][] returnValues;
		Vector<double[]> values = new Vector<>();
		var hrflist = loadHrfIdPerWeekList(iNumberHRF);
		if (hrflist.size()< iNumberHRF) iNumberHRF = hrflist.size();

		try {
			var rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executePreparedQuery(getGetDataForClubStatisticsPanelStatement(iNumberHRF), hrflist.toArray());
			if (rs == null) return new double[0][0];
			double[] tempValues;
			while (rs.next()) {
				tempValues = new double[iNumberColumns];
				tempValues[0] = rs.getDouble("COTrainer");  // AssistantTrainerLevels
				tempValues[1] = rs.getDouble("Finanzberater"); // FinancialDirectorLevels
				tempValues[2] = rs.getDouble("FormAssist"); // FormCoachLevels
				tempValues[3] = rs.getDouble("Aerzte");  // DoctorLevel
				tempValues[4] = rs.getDouble("PRManager"); // SpokespersonLevel
				tempValues[5] = rs.getDouble("Pschyologen"); // SportPsychologistLevel
				tempValues[6] = rs.getDouble("TacticAssist");  // TacticalAssistantLevel
				tempValues[7] = rs.getDouble("Fans");  // FanClubSize
				tempValues[8] = rs.getDouble("globalranking"); // GlobalRanking
				tempValues[9] = rs.getDouble("leagueranking"); // LeagueRanking
				tempValues[10] = rs.getDouble("powerrating"); // PowerRating
				tempValues[11] = rs.getTimestamp("DATUM").getTime();

				//save values
				values.add(tempValues);
			}

			// copy values into returnValues
			returnValues = new double[iNumberColumns][values.size()];
			for (int i = 0; i < values.size(); i++) {
				final double[] werte = values.get(i);

				for (int j = 0; j < werte.length; j++) {
					returnValues[j][i] = werte[j];
				}
			}
		}
		catch (Exception e) {
			HOLogger.instance().log(StatisticQuery.class, e);
			return new double[0][0];
		}

		return returnValues;
	}


	private static final DBManager.PreparedStatementBuilder getDataForFinancesStatisticsPanelBuilder=new DBManager.PreparedStatementBuilder(
			"SELECT * FROM ECONOMY WHERE FetchedDate >= ? ORDER BY FetchedDate DESC"
	);

	// The data returned by this function are displayed in the Finance tab of the statistics module
	public static double[][] getDataForFinancesStatisticsPanel(int iNumberWeeks) {

		final int iNumberColumns = 18;
		final float fxRate = core.model.UserParameter.instance().FXrate;
		double[][] returnValues;
		Vector<double[]> values = new Vector<>();

		try {
			var from = HODateTime.now().minus(iNumberWeeks*7, ChronoUnit.DAYS);
			ResultSet rs = Objects.requireNonNull(DBManager.instance().getAdapter()).executePreparedQuery(
					getDataForFinancesStatisticsPanelBuilder.getStatement(),from.toDbTimestamp() );
			if (rs == null) return new double[0][0];
			double[] tempValues;
			while (rs.next()) {
				tempValues = new double[iNumberColumns];
				tempValues[0] = rs.getDouble("Cash") / fxRate;
				tempValues[1] = rs.getDouble("IncomeSponsors") / fxRate;
				tempValues[2] = rs.getDouble("CostsPlayers") / fxRate;
				tempValues[3] = rs.getDouble("IncomeSum") / fxRate;
				tempValues[4] = rs.getDouble("CostsSum") / fxRate;
				tempValues[5] = tempValues[4] - tempValues[3];
				tempValues[6] = tempValues[3] - ((rs.getDouble("IncomeSoldPlayers")+rs.getDouble("IncomeSoldPlayersCommission")) / fxRate);
				tempValues[7] = tempValues[4] - (rs.getDouble("CostsBoughtPlayers") / fxRate);
				tempValues[8] = tempValues[7] - tempValues[6];
				tempValues[9] = rs.getDouble("IncomeSpectators") / fxRate;
				tempValues[10] = rs.getDouble("IncomeSoldPlayers") / fxRate;
				tempValues[11] = rs.getDouble("IncomeSoldPlayersCommission") / fxRate;
				tempValues[12] = rs.getDouble("IncomeSum") / fxRate - (tempValues[10] + tempValues[11] + tempValues[1] + tempValues[9]); // Income Other
				tempValues[13] = rs.getDouble("CostsArena") / fxRate;
				tempValues[14] = rs.getDouble("CostsBoughtPlayers") / fxRate;
				tempValues[15] = rs.getDouble("CostsStaff") / fxRate;
				tempValues[16] = rs.getDouble("CostsSum") / fxRate - (tempValues[2] + tempValues[13] + tempValues[14] + tempValues[15]); // Costs Other
				tempValues[17] = rs.getTimestamp("FetchedDate").getTime(); // TODO: convert to String: HT Season - HTWeek

				//save values
				values.add(tempValues);
			}

			// copy values into returnValues
			returnValues = new double[iNumberColumns][values.size()];
			for (int i = 0; i < values.size(); i++) {
				final double[] werte = values.get(i);

				for (int j = 0; j < werte.length; j++) {
					returnValues[j][i] = werte[j];
				}
			}
		}
		catch (Exception e) {
			HOLogger.instance().log(StatisticQuery.class, e);
			return new double[0][0];
		}

		return returnValues;
	}

//	public static double[][] getSpielerFinanzDaten4Statistik(int spielerId, int anzahlHRF) {
//		final int anzahlSpalten = 3;
//		final float faktor = core.model.UserParameter.instance().FXrate;
//
//		double[][] returnWerte = new double[0][0];
//		final Vector<double[]> vWerte = new Vector<>();
//
//		ResultSet rs =
//			DBManager.instance().getAdapter().executeQuery("SELECT * FROM SPIELER WHERE SpielerID=" + spielerId + " AND HRF_ID IN (" + getHrfIdPerWeekList(anzahlHRF) + ") ORDER BY Datum DESC");
//
//		if (rs != null) {
//			try {
//				rs.beforeFirst();
//
//				while (rs.next()) {
//					final double[] tempwerte = new double[anzahlSpalten];
//
//					//faktor;
//					tempwerte[0] = rs.getDouble("Marktwert");
//					tempwerte[1] = rs.getDouble("Gehalt") / faktor;
//					tempwerte[2] = rs.getTimestamp("Datum").getTime();
//
//					//TSI, alle Marktwerte / 1000 teilen
//					if (rs.getTimestamp("Datum").before(DBManager.TSIDATE)) {
//						tempwerte[0] /= 1000d;
//					}
//
//					vWerte.add(tempwerte);
//				}
//
//				returnWerte = new double[anzahlSpalten][vWerte.size()];
//
//				for (int i = 0; i < vWerte.size(); i++) {
//					final double[] werte = vWerte.get(i);
//
//					for (int j = 0; j < werte.length; j++) {
//						returnWerte[j][i] = werte[j];
//					}
//				}
//			} catch (Exception e) {
//				HOLogger.instance().log(StatisticQuery.class, e);
//			}
//		}
//
//		return returnWerte;
//	}


	/**
	 * Get a list of HRF Ids of the last weeks
	 * only one id per week is returned
	 *
	 * @param nWeeks number of weeks
	 * @return comma separated list of hrf ids
	 */
	private static List<Integer> loadHrfIdPerWeekList(int nWeeks) {
		return DBManager.instance().loadHrfIdPerWeekList(nWeeks);
	}

}
