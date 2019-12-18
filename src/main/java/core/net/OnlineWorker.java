package core.net;

import core.db.DBManager;
import core.file.ExampleFileFilter;
import core.file.hrf.HRFStringParser;
import core.file.xml.*;
import core.gui.HOMainFrame;
import core.gui.InfoPanel;
import core.gui.model.AufstellungCBItem;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.Team;
import core.model.Tournament.TournamentDetails;
import core.model.UserParameter;
import core.model.match.*;
import core.model.misc.Regiondetails;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.net.login.LoginWaitDialog;
import core.training.TrainingManager;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import module.lineup.AufstellungsVergleichHistoryPanel;
import module.lineup.Lineup;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.Substitution;
import module.teamAnalyzer.vo.MatchRating;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author thomas.werth
 */
public class OnlineWorker {

	private final static SimpleDateFormat HT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static LoginWaitDialog waitDialog;

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private OnlineWorker() {
	}

	/**
	 * Get and optionally save HRF
	 *
	 */
	public static boolean getHrf(JDialog parent) {
		// Show wait dialog
		
		boolean ok = true;
		
		LoginWaitDialog waitDlg = new LoginWaitDialog(parent, false);
		waitDlg.setVisible(true);
		try {
			HOMainFrame homf = HOMainFrame.instance();
			HOVerwaltung hov = HOVerwaltung.instance();
			UserParameter up = core.model.UserParameter.instance();

			String hrf = null;
			try {
				hrf = ConvertXml2Hrf.createHrf(waitDlg);
				if (hrf == null) {
					return false;
				}
				
			} catch (IOException e) {
				waitDlg.setVisible(false);
				// Info
				String msg = getLangString("Downloadfehler")
						+ " : Error converting xml 2 HRF. Corrupt/Missing Data : ";
				setInfoMsg(msg, InfoPanel.FEHLERFARBE);
				Helper.showMessage(parent, msg + "\n" + e.toString() + "\n", getLangString("Fehler"),
						JOptionPane.ERROR_MESSAGE);
				ok = false;
			}

			if (hrf != null) {
				if (hrf.indexOf("playingMatch=true") > -1) {
					waitDlg.setVisible(false);
					JOptionPane.showMessageDialog(parent, getLangString("NO_HRF_Spiel"),
							getLangString("NO_HRF_ERROR"), 1);
				} else if (hrf.indexOf("NOT AVAILABLE") > -1) {
					waitDlg.setVisible(false);
					JOptionPane.showMessageDialog(parent, getLangString("NO_HRF_ERROR"),
							getLangString("NO_HRF_ERROR"), 1);
				} else {
					// Create HOModelo from the hrf data
					HOModel homodel = HRFStringParser.parse(hrf);
					if (homodel == null) {
						// Info
						setInfoMsg(getLangString("Importfehler"), InfoPanel.FEHLERFARBE);
						// Error
						Helper.showMessage(parent, getLangString("Importfehler"),
								getLangString("Fehler"), JOptionPane.ERROR_MESSAGE);
					} else {
						homodel.saveHRF();
						homodel.setSpielplan(hov.getModel().getSpielplan());

						// Add old players to the model
						homodel.setAllOldSpieler(DBManager.instance().getAllSpieler());
						// Only update when the model is newer than existing
						if (isNewModel(homodel)) {
							Date lastTrainingDate = Calendar.getInstance().getTime();
							Date lastEconomyDate = lastTrainingDate;
							if (hov.getModel().getXtraDaten().getTrainingDate() != null) {
								lastTrainingDate = new Date(hov.getModel().getXtraDaten()
										.getTrainingDate().getTime());
								lastEconomyDate = new Date(hov.getModel().getXtraDaten()
										.getEconomyDate().getTime());
							}
							// Reimport Skillup
							DBManager.instance().checkSkillup(homodel);
							// Show
							hov.setModel(homodel);
							// Recalculate Training
							// Training->Subskill calculation
							TrainingManager.instance().refreshTrainingWeeks();
							homodel.calcSubskills();
							AufstellungsVergleichHistoryPanel.setHRFAufstellung(
									homodel.getLineup(), homodel.getLastAufstellung());
							AufstellungsVergleichHistoryPanel
									.setAngezeigteAufstellung(new AufstellungCBItem(
											getLangString("AktuelleAufstellung"), homodel
													.getLineup()));
							homf.getAufstellungsPanel().getAufstellungsPositionsPanel()
									.exportOldLineup("Actual");
						}
						// Info
						setInfoMsg(getLangString("HRFErfolg"));

						try {
							waitDlg.setVisible(false);
							saveHRFToFile(parent,hrf);
						} catch (IOException e) {
							Helper.showMessage(HOMainFrame.instance(),
									"Failed to save downloaded file.\nError: " + e.getMessage(),
									getLangString("Fehler"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		} finally {
			waitDlg.setVisible(false);
		}
		return ok;
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
	public static List<MatchKurzInfo> getMatchArchive(int teamId, Date firstDate, boolean store) {

		List<MatchKurzInfo> allMatches = new ArrayList<MatchKurzInfo>();
		GregorianCalendar tempBeginn = new GregorianCalendar();
		tempBeginn.setTime(firstDate);
		GregorianCalendar tempEnd = new GregorianCalendar();
		tempEnd.setTimeInMillis(tempBeginn.getTimeInMillis());
		tempEnd.add(Calendar.MONTH, 3);

		GregorianCalendar endDate = new GregorianCalendar();
		if (!tempEnd.before(endDate)) {
			tempEnd.setTime(endDate.getTime());
		}

		// Show wait Dialog
		waitDialog = getWaitDialog();
		waitDialog.setVisible(true);
		try {
			String matchesString = "";

			while (tempBeginn.before(endDate)) {
				try {
					waitDialog.setValue(10);
					matchesString = MyConnector.instance().getMatchesArchive(teamId, tempBeginn.getTime(),
							tempEnd.getTime());
					waitDialog.setValue(20);
				} catch (Exception e) {
					// Info
					String msg = getLangString("Downloadfehler")
							+ " : Error fetching MatchArchiv : ";
					setInfoMsg(msg, InfoPanel.FEHLERFARBE);
					Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
							JOptionPane.ERROR_MESSAGE);
					waitDialog.setVisible(false);
					return null;
				}

				waitDialog.setValue(40);
				List<MatchKurzInfo> matches = XMLMatchArchivParser
						.parseMatchesFromString(matchesString);

				// Add the new matches to the list of all matches
				allMatches.addAll(matches);

				// Zeitfenster neu setzen
				tempBeginn.add(Calendar.MONTH, 3);
				tempEnd.add(Calendar.MONTH, 3);

				if (!tempEnd.before(endDate)) {
					tempEnd.setTime(endDate.getTime());
				}
			}

			// Store in the db if store is true
			if (store && (allMatches.size() > 0)) {

				waitDialog.setValue(80);
				DBManager.instance().storeMatchKurzInfos(allMatches.toArray(new MatchKurzInfo[0]));

				// Store full info for all matches
				for (MatchKurzInfo match : allMatches) {
					// if match is available and match is finished
					if ((DBManager.instance().isMatchVorhanden(match.getMatchID()))
							&& (match.getMatchStatus() == MatchKurzInfo.FINISHED)) {
						downloadMatchData(match.getMatchID(), match.getMatchTyp(), true);
					}
				}
			}
		} finally {
			waitDialog.setVisible(false);
		}
		return allMatches;
	}

	/**
	 * Downloads a match with the given criteria and stores it in the database.
	 * If a match is already in the db, and refresh is false, nothing is
	 * downloaded.
	 *
	 * @param matchid
	 *            ID for the match to be downloaded
	 * @param match
	 *            MatchKurzInfo of the match
	 * @param refresh
	 *            If true the match will always be downloaded.
	 *
	 * @return true if the match is in the db afterwards
	 */


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
	public static boolean downloadMatchData(int matchid, MatchType matchType, boolean refresh) {
		waitDialog = getWaitDialog();
		// Only download if not present in the database, or if refresh is true
		if (refresh || !DBManager.instance().isMatchVorhanden(matchid)
				|| DBManager.instance().hasUnsureWeatherForecast(matchid)
				|| !DBManager.instance().isMatchLineupInDB(matchid)
				|| !DBManager.instance().isDerbyInfoInDb(matchid)
		) {
			try {
				MatchKurzInfo info = null;
				Matchdetails details = null;

				// Check if teams IDs are stored somewhere
				if (DBManager.instance().isMatchVorhanden(matchid)) {
					info = DBManager.instance().getMatchesKurzInfoByMatchID(matchid);
				}
				else {
					// TODO: up to now this method is only called, when match id is stored in Db
					return false;
				}

				// If ids not found, download matchdetails to obtain them.
				// Highlights will be missing.
				// ArenaId==0 in division battles
				if ((info.getHeimID() == 0) || (info.getGastID() == 0) || info.getIsDerby() == null || !info.getWeatherForecast().isSure()) {
					waitDialog.setValue(10);
					details = fetchDetails(matchid, matchType, null, waitDialog);
					info.setHeimID(details.getHeimId());
					info.setGastID( details.getGastId());
					info.setArenaId( details.getArenaID());
					if (info.getMatchStatus() == MatchKurzInfo.FINISHED){
						info.setWeather(Weather.getById(details.getWetterId()));
						info.setWeatherForecast(Weather.Forecast.HAPPENED);
					}

					if ( info.getArenaId() > 0) {
						info.setRegionId(details.getRegionId());

						if ( info.getWeatherForecast().isSure() == false){
							Regiondetails regiondetails = getRegionDetails(info.getRegionId());
							SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
							java.sql.Timestamp matchDate = info.getMatchDateAsTimestamp();
							java.sql.Timestamp weatherDate = regiondetails.getFetchDatum();
							String wdate = fmt.format(weatherDate);
							String mdate = fmt.format(matchDate);
							if ( mdate.equals(wdate)) {
								info.setWeatherForecast(Weather.Forecast.TODAY);
								info.setWeather(regiondetails.getWeather());
							}
							else {
								Calendar c = Calendar.getInstance();
								c.setTime(fmt.parse(wdate));
								c.add(Calendar.DATE, 1);
								if ( fmt.format(c.getTime()).equals(mdate)){
									info.setWeatherForecast(Weather.Forecast.TOMORROW);
								}
								else {
									info.setWeatherForecast((Weather.Forecast.UNSURE));
								}
								info.setWeather(regiondetails.getWeatherTomorrow());
							}
						}
					}

					// get the other team
					int otherId;
					if ( info.isHomeMatch()){
						otherId = info.getGastID();
					}
					else {
						otherId = info.getHeimID();
					}
					Map<String, String> otherTeam = getTeam(otherId);
					info.setIsDerby( getRegionId(otherTeam) == HOVerwaltung.instance().getModel().getBasics().getRegionId());
					info.setIsNeutral( info.getArenaId() != HOVerwaltung.instance().getModel().getArena().getArenaId()
										&& info.getArenaId() != getArenaId(otherTeam));
				}

				MatchLineup lineup = null;
				boolean success;
				if ( info.getMatchStatus() == MatchKurzInfo.FINISHED) {
					lineup = getMatchlineup(matchid, matchType, info.getHeimID(), info.getGastID());

					if (lineup == null) {
						String msg = getLangString("Downloadfehler")
								+ " : Error fetching Matchlineup :";
						// Info
						setInfoMsg(msg, InfoPanel.FEHLERFARBE);
						Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
								JOptionPane.ERROR_MESSAGE);

						return false;
					}
					
					// Get details with highlights.
					waitDialog.setValue(10);
					details = fetchDetails(matchid, matchType, lineup, waitDialog);

					if (details == null) {
						HOLogger.instance().error(OnlineWorker.class,
								"Error downloading match. Details is null: " + matchid);
						return false;
					}
					info.setGastTore(details.getGuestGoals());
					info.setHeimTore(details.getHomeGoals());
					info.setGastID(lineup.getGastId());
					info.setGastName(lineup.getGastName());
					info.setHeimID(lineup.getHeimId());
					info.setHeimName(lineup.getHeimName());
					info.setMatchDate(lineup.getStringSpielDate());
					success = DBManager.instance().storeMatch(info, details, lineup);
				}
				else{
					// Update arena and region ids
					MatchKurzInfo[] matches = {info};
					DBManager.instance().storeMatchKurzInfos(matches);
					success = true;
				}
				if (!success) {
					waitDialog.setVisible(false);
					return false;
				}
			} catch (Exception ex) {
				HOLogger.instance().error(OnlineWorker.class,
						"downloadMatchData:  Error in downloading match: " + ex);
				waitDialog.setVisible(false);
				return false;
			}
		}
		waitDialog.setVisible(false);
		return true;
	}

	private static Map<String, String> getTeam(int teamId)
	{
		String str = MyConnector.instance().fetchTeamDetails(teamId);
		return XMLTeamDetailsParser.parseTeamdetailsFromString(str, teamId);
	}

	private static int getRegionId(Map<String, String> team) {
		String  str = team.get("RegionID");
		return Integer.parseInt(str);
	}

	private  static int getArenaId(Map<String, String> team)
	{
		String str = team.get("ArenaID");
		return Integer.parseInt(str);
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
	public static MatchKurzInfo updateMatch(int teamId, MatchKurzInfo match) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(match.getMatchDateAsTimestamp());
		cal.add(Calendar.MINUTE, 1);
		// At the moment, HT does not support getting a single match.
		List<MatchKurzInfo> matches = getMatches(teamId, cal.getTime());
		for (MatchKurzInfo m : matches) {
			if (m.getMatchID() == match.getMatchID()) {
				DBManager.instance().updateMatchKurzInfo(m);
				return m;
			}
		}
		return null;
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
	public static List<MatchKurzInfo> getMatches(int teamId, Date date) {
		String matchesString = null;
		try {
			matchesString = MyConnector.instance().getMatches(teamId, true, date);
		} catch (IOException e) {
			Helper.showMessage(HOMainFrame.instance(), getLangString("Downloadfehler")
					+ " : Error fetching matches : " + e, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().log(OnlineWorker.class, e);
		}

		if (!StringUtils.isEmpty(matchesString)) {
			return XMLMatchesParser.parseMatchesFromString(matchesString);
		}

		return new ArrayList<>();
	}

	/**
	 * Download information about a given tournament
	 */

	public static TournamentDetails getTournamentDetails(int tournamentId) {
		TournamentDetails oTournamentDetails = null;
		String tournamentString = "";

		try {
			tournamentString = MyConnector.instance().getTournamentDetails(tournamentId);
		} catch (IOException e) {
			Helper.showMessage(HOMainFrame.instance(), getLangString("Downloadfehler")
							+ " : Error fetching Tournament Details : " + e, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().log(OnlineWorker.class, e);
		}

		if (!StringUtils.isEmpty(tournamentString)) {
			oTournamentDetails = XMLTournamentDetailsParser.parseTournamentDetailsFromString(tournamentString);  // TODO: create function parseTournamentDetailsFromString
		}

		return oTournamentDetails;
	}

	/**
	 * saugt den Spielplan
	 *
	 * @param teamId
	 *            angabe der Saison ( optinal &lt; 1 für aktuelle
	 * @param forceRefresh
	 * @param store
	 *            true if the full match details are to be stored, false if not.
	 * @param upcoming
	 *            true if upcoming matches should be included
	 *
	 * @return The list of MatchKurzInfos found or null if an exception
	 *         occurred.
	 */
	public static List<MatchKurzInfo> getMatches(int teamId, boolean forceRefresh, boolean store,
			boolean upcoming) {
		String matchesString = "";
		List<MatchKurzInfo> matches = new ArrayList<MatchKurzInfo>();
		boolean bOK = false;
		waitDialog = getWaitDialog();
		waitDialog.setVisible(true);
		waitDialog.setValue(10);

		try {
			matchesString = MyConnector.instance().getMatches(teamId, forceRefresh, upcoming);
			bOK = (matchesString != null && matchesString.length() > 0);
			if (bOK)
				waitDialog.setValue(50);
			else
				waitDialog.setVisible(false);
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching matches: "
					+ e.getMessage();
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().log(OnlineWorker.class, e);
			waitDialog.setVisible(false);
			return null;
		}
		if (bOK) {
			matches.addAll(XMLMatchesParser.parseMatchesFromString(matchesString));

			// Store in DB if store is true
			if (store) {
				waitDialog.setValue(80);
				DBManager.instance().storeMatchKurzInfos(
						matches.toArray(new MatchKurzInfo[matches.size()]));

				waitDialog.setValue(100);

				// Automatically download additional match infos (lineup + arena)
				for (MatchKurzInfo match : matches) {
					int curMatchId = match.getMatchID();
					if (DBManager.instance().isMatchVorhanden(curMatchId)
							&& (match.getMatchStatus() == MatchKurzInfo.FINISHED
							&& !DBManager.instance().isMatchLineupInDB(curMatchId)
							|| !DBManager.instance().isDerbyInfoInDb(curMatchId))) {

						// No lineup or arenaId in DB
						boolean result = downloadMatchData(curMatchId, match.getMatchTyp(), false);
						if (!result) {
							bOK = false;
							break;
						}
					}
				}
			}
		}
		waitDialog.setVisible(false);
		return matches;
	}

	/**
	 * saugt das Matchlineup
	 *
	 * @param matchId
	 *            Die ID des Matches
	 * @param teamId1
	 *            Erste Teamid (pflicht)
	 * @param teamId2
	 *            Zweite Teamid (optional auch -1)
	 */
	private static MatchLineup getMatchlineup(int matchId, MatchType matchType, int teamId1,
			int teamId2) {
		boolean bOK = false;
		MatchLineup lineUp1 = null;
		MatchLineup lineUp2 = null;

		// Wait Dialog zeigen
		waitDialog = getWaitDialog();
		waitDialog.setVisible(true);
		waitDialog.setValue(10);

		// Lineups holen
		lineUp1 = fetchLineup(matchId, teamId1, matchType);
		if (lineUp1 != null) {
			bOK = true;
			waitDialog.setValue(50);
			if (teamId2 > 0)
				lineUp2 = fetchLineup(matchId, teamId2, matchType);

			// Merge the two
			if ((lineUp2 != null)) {
				if (lineUp1.getHeim() == null)
					lineUp1.setHeim((MatchLineupTeam) lineUp2.getHeim());
				else if (lineUp1.getGast() == null)
					lineUp1.setGast((MatchLineupTeam) lineUp2.getGast());
			} else {
				// Get the 2nd lineup
				if (lineUp1.getHeim() == null) {
					lineUp2 = fetchLineup(matchId, lineUp1.getHeimId(), matchType);
					if (lineUp2 != null)
						lineUp1.setHeim((MatchLineupTeam) lineUp2.getHeim());
				} else {
					lineUp2 = fetchLineup(matchId, lineUp1.getGastId(), matchType);
					if (lineUp2 != null)
						lineUp1.setGast((MatchLineupTeam) lineUp2.getGast());
				}
			}
		}
		waitDialog.setVisible(false);
		return lineUp1;
	}

	/**
	 * Get the Fixtures list
	 *
	 * @param season
	 *            - The season, -1 for current
	 * @param leagueID
	 *            - The ID of the league to get the fixtures for
	 *
	 * @return true on sucess, false on failure
	 */
	public static boolean getSpielplan(int season, int leagueID) {
		boolean bOK = false;
		String leagueFixtures = "";
		HOVerwaltung hov = HOVerwaltung.instance();
		waitDialog = getWaitDialog();
		waitDialog.setVisible(true);
		try {
			waitDialog.setValue(10);
			leagueFixtures = MyConnector.instance().getLeagueFixtures(season, leagueID);
			bOK = (leagueFixtures != null && leagueFixtures.length() > 0);
			waitDialog.setValue(50);
		} catch (Exception e) {
			HOLogger.instance().log(OnlineWorker.class, e);
			String msg = getLangString("Downloadfehler") + " : Error fetching leagueFixture: "
					+ e.getMessage();
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			waitDialog.setVisible(false);
			return false;
		}
		if (bOK) {
			HOModel hom = hov.getModel();
			hom.setSpielplan(XMLSpielplanParser.parseSpielplanFromString(leagueFixtures));
			waitDialog.setValue(70);
			// Save to DB
			hom.saveSpielplan2DB();
			waitDialog.setValue(90);
		}
		waitDialog.setVisible(false);
		return bOK;
	}

	protected static LoginWaitDialog getWaitDialog() {
		if (waitDialog != null) {
			return waitDialog;
		}
		return new LoginWaitDialog(HOMainFrame.instance(), false);
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

	public static String uploadMatchOrder(int matchId, MatchType matchType, Lineup lineup) {

		String result;
		// Tell the Connector that we will require match order rights.

		/*
		boolean bFirst = true;
		StringBuilder orders = new StringBuilder();

		orders.append("{\"positions\":[");
		for (int pos : IMatchRoleID.aFieldMatchRoleID)
		{
			if (bFirst)
			{orders.append(createPositionString(pos, lineup));
			bFirst = false;}
			else orders.append(',').append(createPositionString(pos, lineup));
		}

		orders.append("],\"bench\":[");
		bFirst = true;
		for (int pos : IMatchRoleID.aSubsAndBackupssMatchRoleID) {
			if (bFirst) {
				orders.append(createPositionString(pos, lineup));
				bFirst = false;}
		else orders.append(',').append(createPositionString(pos, lineup));
		}

		// penalty takers
		List<MatchRoleID> shooters = lineup.getPenaltyTakers();
		int penshooters = 0;

		orders.append("],\"kickers\":[");
		for (MatchRoleID pos : shooters) {
			if (penshooters > 0) {
				orders.append(',');
			}
			orders.append("{\"id\":\"").append(pos.getSpielerId());
			orders.append("\",\"behaviour\":\"0\"}");
			penshooters++;
		}
		// Always give 11 shooters. There is a CHPP error if the number given is not 0 or 11.
		for (int i = 0 ; i < 11-penshooters; i++) {
			if (penshooters > 0 || i > 0) {
				orders.append(',');
			}
			orders.append("{\"id\":\"0");
			orders.append("\",\"behaviour\":\"0\"}");
		}

		orders.append(String.format("],\"captain\": %s,",lineup.getKapitaen()));
		orders.append(String.format("\"setPieces\": %s,",lineup.getKicker()));

		orders.append("\"settings\":{\"tactic\":").append(lineup.getTacticType());
		orders.append(",\"speechLevel\":").append(lineup.getAttitude());
		orders.append(", \"newLineup\":\"\",");
		orders.append("\"coachModifier\":").append(lineup.getStyleOfPlay());
		orders.append("}, \"substitutions\":[");

		Iterator<Substitution> iter = lineup.getSubstitutionList().iterator();
		while (iter.hasNext()) {
			Substitution sub = iter.next();
			// playerout == playerin if its a behaviour change)
			if (sub.getOrderType() == MatchOrderType.NEW_BEHAVIOUR) {
				orders.append("{\"playerin\":").append(sub.getSubjectPlayerID()).append(",");
			} else {
				orders.append("{\"playerin\":").append(sub.getObjectPlayerID()).append(",");
			}
			orders.append("\"playerout\":").append(sub.getSubjectPlayerID()).append(",");
			orders.append("\"orderType\":").append(sub.getOrderType().getId()).append(",");
			orders.append("\"min\":").append(sub.getMatchMinuteCriteria()).append(",");

			// The uploaded position is not a RoleId
			byte pos = (byte) ((sub.getRoleId() == -1) ? -1 : sub.getRoleId() -100);
			orders.append("\"pos\":").append(pos).append(",");
			orders.append("\"beh\":").append(sub.getBehaviour()).append(",");
			orders.append("\"card\":").append(sub.getRedCardCriteria().getId()).append(",");
			orders.append("\"standing\":").append(sub.getStanding().getId()).append("}");
			if (iter.hasNext()) {
				orders.append(',');
			}
		}
		orders.append("]}");*/

		String orders = lineup.toJson();
		try {
			result = MyConnector.instance().setMatchOrder(matchId, HOVerwaltung.instance().getModel().getBasics().getTeamId(), matchType, orders);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		HOLogger.instance().debug(OnlineWorker.class, "Upload done:\n" + result);
		return result;
	}

	private static String createPositionString(int roleId, Lineup lineup) {

		int id = 0;
		int behaviour = 0;

		Player player = lineup.getPlayerByPositionID(roleId);
		if (player != null) {
			id = player.getSpielerID();
			behaviour = lineup.getTactic4PositionID(roleId);
		}

		return "{\"id\":" + id + ",\"behaviour\":" + behaviour + "}";
	}

	private static Matchdetails fetchDetails(int matchID, MatchType matchType, MatchLineup lineup, LoginWaitDialog waitDialog) {
		String matchDetails = "";
		Matchdetails details = null;

		try {
			matchDetails = MyConnector.instance().getMatchdetails(matchID, matchType);
			if (matchDetails.length() == 0) {
				HOLogger.instance().warning(OnlineWorker.class,
						"Unable to fetch details for match " + matchID);
				return null;
			}
			waitDialog.setValue(20);
			details = XMLMatchdetailsParser.parseMachtdetailsFromString(matchDetails, lineup);
			waitDialog.setValue(40);
			if (details == null) {
				HOLogger.instance().warning(OnlineWorker.class,
						"Unable to fetch details for match " + matchID);
				return null;
			}
			String arenaString = MyConnector.instance().getArena(details.getArenaID());
			waitDialog.setValue(50);
			String regionIdAsString = XMLArenaParser.parseArenaFromString(arenaString).get(
					"RegionID");
			details.setRegionId(Integer.parseInt(regionIdAsString));
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + ": Error fetching Matchdetails XML.: ";
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			waitDialog.setVisible(false);
			return null;
		}
		waitDialog.setVisible(false);
		return details;
	}

	public static MatchLineup fetchLineup(int matchID, int teamID, MatchType matchType) {
		String matchLineup = "";
		MatchLineup lineUp = null;
		boolean bOK = false;
		try {
			matchLineup = MyConnector.instance().getMatchLineup(matchID, teamID, matchType);
			bOK = (matchLineup != null && matchLineup.length() > 0);
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching Matchlineup :";
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (bOK) {
			lineUp = XMLMatchLineupParser.parseMatchLineupFromString(matchLineup);
		}
		return lineUp;
	}





	/**
	 * Get all lineups for MatchKurzInfos, if they're not there already
	 */
	public static void getAllLineups() {
		final MatchKurzInfo[] infos = DBManager.instance().getMatchesKurzInfo(-1);
		String haveLineups = "";
		boolean bOK = false;
		for (int i = 0; i < infos.length; i++) {
			int curMatchId = infos[i].getMatchID();
			if (!DBManager.instance().isMatchLineupInDB(curMatchId)) {
				// Check if the lineup is available
				if (infos[i].getMatchStatus() == MatchKurzInfo.FINISHED) {
					HOLogger.instance().debug(OnlineWorker.class, "Get Lineup : " + curMatchId);
					bOK = downloadMatchData(curMatchId, infos[i].getMatchTyp(), false);
					if (!bOK) {
						break;
					}
				} else
					HOLogger.instance().debug(OnlineWorker.class, "Not Played : " + curMatchId);
			} else {
				// Match lineup already available
				if (haveLineups.length() > 0)
					haveLineups += ", ";
				haveLineups += curMatchId;
			}
		}
		if (haveLineups.length() > 0)
			HOLogger.instance().debug(OnlineWorker.class, "Have Lineups : " + haveLineups);
	}

	/**
	 *
	 * @param matchId
	 *            The match ID for the match to download
	 * @param matchType
	 *            The matchTyp for the match to download
	 * @return The Lineup object with the downloaded match data
	 */
	public static Lineup getLineupbyMatchId(int matchId, MatchType matchType) {

		try {
			String xml = MyConnector.instance().getMatchOrder(matchId, matchType, HOVerwaltung.instance().getModel().getBasics().getTeamId());
			
			if (!StringUtils.isEmpty(xml)) {
				Map<String, String> map = XMLMatchOrderParser.parseMatchOrderFromString(xml);
				String trainerID = "-1";
				try
				{
					trainerID = String.valueOf(HOVerwaltung.instance().getModel().getTrainer()
						.getSpielerID());
					
				}
				catch (Exception e)
				{	
					//It is possible that NTs struggle here.
				}
				String lineupData = ConvertXml2Hrf.createLineUp(trainerID, map);
				return new Lineup(getProperties(lineupData));
			}
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching Matchorder :";
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().error(OnlineWorker.class, e.getMessage());
		}

		return null;
	}

	/**
	 *
	 * @param matchId
	 *            The match ID for the match to download
	 * @param matchType
	 *            The matchTyp for the match to download
	 * @return The Lineup object with the downloaded match data
	 */
	public static MatchRating getPredictionRatingbyMatchId(int matchId, MatchType matchType, int teamId) {

		try {
			String xml = MyConnector.instance().getRatingsPrediction(matchId, teamId, matchType);

			if (!StringUtils.isEmpty(xml)) {
				Map<String, String> map = XMLRatingParser.parsePredictionRatingFromString(xml);
				return new MatchRating(map);
			}
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching Prediction Rating :";
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().error(OnlineWorker.class, e.getMessage());
		}

		return null;
	}

	public static Regiondetails getRegionDetails(int regionId)
	{
		try {
			String xml = MyConnector.instance().getRegion(regionId);
			if ( !StringUtils.isEmpty(xml)){
				return new Regiondetails(XMLRegionParser.parseRegionDetailsFromString(xml));
			}
		}
		catch(Exception e){
			String msg = getLangString("Downloadfehler") + " : Error fetching region details :";
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().error(OnlineWorker.class, e.getMessage());
		}
		return null;
	}

	private static Properties getProperties(String data) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes("UTF-8"));
		InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
		BufferedReader hrfReader = new BufferedReader(isr);
		Properties properties = new Properties();

		// Lose the first line
		hrfReader.readLine();
		while (hrfReader.ready()) {
			String lineString = hrfReader.readLine();
			// Ignore empty lines
			if (!StringUtils.isEmpty(lineString)) {
				int indexEqualsSign = lineString.indexOf('=');
				if (indexEqualsSign > 0) {
					properties.setProperty(
							lineString.substring(0, indexEqualsSign).toLowerCase(
									java.util.Locale.ENGLISH),
							lineString.substring(indexEqualsSign + 1));
				}
			}
		}
		return properties;
	}

	private static boolean isNewModel(HOModel homodel) {
		return (homodel != null && ((HOVerwaltung.instance().getModel() == null) || (homodel
				.getBasics().getDatum().after(HOVerwaltung.instance().getModel().getBasics()
				.getDatum()))));
	}

	/**
	 * Shows a file chooser asking for the location for the HRF file and saves
	 * it to the location chosen by the user.
	 *
	 * @param hrfData
	 *            the HRF data as string
	 * @throws IOException
	 */
	private static void saveHRFToFile(JDialog parent, String hrfData) throws IOException {
		setInfoMsg(getLangString("HRFSave"));

		File path = new File(UserParameter.instance().hrfImport_HRFPath);
		File file = new File(path, getHRFFileName());
		// Show dialog if path not set or the file already exists
		if (UserParameter.instance().showHRFSaveDialog || !path.exists() || !path.isDirectory()
				|| file.exists()) {
			file = askForHRFPath(parent, file);
		}

		if ((file != null) && (file.getPath() != null)) {
			// Save Path
			UserParameter.instance().hrfImport_HRFPath = file.getParentFile().getAbsolutePath();

			// File exists?
			int value = JOptionPane.OK_OPTION;
			if (file.exists()) {
				value = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
						getLangString("overwrite"), HOVerwaltung.instance().getLanguageString("confirmation.title"), JOptionPane.YES_NO_OPTION);
			}

			// Save
			if (value == JOptionPane.OK_OPTION) {
				saveFile(file.getPath(), hrfData);
			} else {
				// Canceled
				setInfoMsg(getLangString("HRFAbbruch"), InfoPanel.FEHLERFARBE);
			}
		}

	}

	/**
	 * Gets a HRF file name, based on the current date.
	 *
	 * @return the HRF file name.
	 */
	private static String getHRFFileName() {
		GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
		StringBuilder builder = new StringBuilder();
		
		builder.append(HOVerwaltung.instance().getModel().getBasics().getTeamId());
		builder.append('-');
		
		builder.append(calendar.get(Calendar.YEAR));
		builder.append('-');
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 10) {
			builder.append('0');
		}
		builder.append(month);
		builder.append('-');
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			builder.append('0');
		}
		builder.append(day);
		builder.append(".hrf");
		String name = calendar.get(Calendar.YEAR) + "-" + month + "-" + day + ".hrf";
		return builder.toString();
	}

	/**
	 * Shows a file chooser dialog to ask the user for the location to save the
	 * HRF file.
	 *
	 * @param file
	 *            the recommendation for the file name/location.
	 * @return the file location choosen by the user or null if the canceled the
	 *         dialog.
	 */
	private static File askForHRFPath(JDialog parent, File file) {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle(getLangString("FileExport"));
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("hrf");
		filter.setDescription(HOVerwaltung.instance().getLanguageString("filetypedescription.hrf"));
		fileChooser.setFileFilter(filter);
		File path = file.getParentFile();
		if (path.exists() && path.isDirectory()) {
			fileChooser.setCurrentDirectory(path);
		}
		fileChooser.setSelectedFile(file);
		int returnVal = fileChooser.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			// File doesn't end with .hrf?
			if (!f.getPath().endsWith(".hrf")) {
				f = new java.io.File(file.getAbsolutePath() + ".hrf");
			}
			return f;
		}
		return null;
	}

	/**
	 * Convenience method for
	 * HOMainFrame.instance().getInfoPanel().setLangInfoText(msg);
	 *
	 * @param msg
	 *            the message to show
	 */
	private static void setInfoMsg(String msg) {
		HOMainFrame.instance().getInfoPanel().setLangInfoText(msg);
	}

	/**
	 * Convenience method for
	 * HOMainFrame.instance().getInfoPanel().setLangInfoText(msg, color);
	 *
	 * @param msg
	 *            the message to show
	 * @param color
	 *            the color
	 */
	private static void setInfoMsg(String msg, Color color) {
		HOMainFrame.instance().getInfoPanel().setLangInfoText(msg, color);
	}

	/**
	 * Convenience method for HOVerwaltung.instance().getLanguageString(key)
	 *
	 * @param key
	 *            the key for the language string
	 * @return the string for the current language
	 */
	private static String getLangString(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}

	/**
	 * Save the passed in data to the passed in file
	 *
	 * @param fileName
	 *            Name of the file to save the data to
	 * @param content
	 *            The content to write to the file
	 *
	 * @return The saved file
	 * @throws IOException
	 */
	private static File saveFile(String fileName, String content) throws IOException {
		File outFile = new File(fileName);
		if (outFile.exists()) {
			outFile.delete();
		}
		outFile.createNewFile();
		OutputStreamWriter outWrit = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
		BufferedWriter out = new BufferedWriter(outWrit);
		out.write(content);
		out.newLine();
		out.close();
		return outFile;
	}
}
