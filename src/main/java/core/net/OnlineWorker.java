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
import core.model.Tournament.TournamentDetails;
import core.model.UserParameter;
import core.model.match.*;
import core.model.misc.Regiondetails;
import core.model.misc.TrainingEvent;
import core.model.player.Player;
import core.training.TrainingManager;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import module.lineup.AufstellungsVergleichHistoryPanel;
import module.lineup.Lineup;
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
	//private static LoginWaitDialog waitDialog;

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

		try {
			HOMainFrame homf = HOMainFrame.instance();
			HOVerwaltung hov = HOVerwaltung.instance();

			UserParameter up = core.model.UserParameter.instance();

			String hrf = null;
			try {
				hrf = ConvertXml2Hrf.createHrf();
				if (hrf == null) {
					return false;
				}
				
			} catch (IOException e) {
				// Info
				String msg = getLangString("Downloadfehler")
						+ " : Error converting xml 2 HRF. Corrupt/Missing Data : ";
				setInfoMsg(msg, InfoPanel.FEHLERFARBE);
				Helper.showMessage(parent, msg + "\n" + e.toString() + "\n", getLangString("Fehler"),
						JOptionPane.ERROR_MESSAGE);
				ok = false;
			}

			if (hrf != null) {
				if (hrf.contains("playingMatch=true")) {
					HOMainFrame.instance().resetInformation();
					JOptionPane.showMessageDialog(parent, getLangString("NO_HRF_Spiel"),
							getLangString("NO_HRF_ERROR"), 1);
				} else if (hrf.contains("NOT AVAILABLE")) {
					HOMainFrame.instance().resetInformation();
					JOptionPane.showMessageDialog(parent, getLangString("NO_HRF_ERROR"),
							getLangString("NO_HRF_ERROR"), 1);
				} else {
					// Create HOModel from the hrf data
					HOModel homodel = HRFStringParser.parse(hrf);
					if (homodel == null) {
						// Info
						setInfoMsg(getLangString("Importfehler"), InfoPanel.FEHLERFARBE);
						// Error
						Helper.showMessage(parent, getLangString("Importfehler"),
								getLangString("Fehler"), JOptionPane.ERROR_MESSAGE);
					} else {
						// save the model in the database
						homodel.saveHRF();

						homodel.setFixtures(hov.getModel().getFixtures());

						// Add old players to the model
						homodel.setFormerPlayers(DBManager.instance().getAllSpieler());
						// Only update when the model is newer than existing
						if (isNewModel(homodel)) {
							// Reimport Skillup
							DBManager.instance().checkSkillup(homodel);
							// Show
							hov.setModel(homodel);
							// Recalculate Training
							// Training->Subskill calculation
							TrainingManager.instance().refreshTrainingWeeks();
							homodel.calcSubskills();
							AufstellungsVergleichHistoryPanel.setHRFAufstellung(
									homodel.getLineup(), homodel.getPreviousLineup());
							AufstellungsVergleichHistoryPanel
									.setAngezeigteAufstellung(new AufstellungCBItem(
											getLangString("AktuelleAufstellung"), homodel
													.getLineup()));
						}
						// Info
						setInfoMsg(getLangString("HRFErfolg"));

						saveHRFToFile(parent,hrf);
					}
				}
			}
		} finally {
			HOMainFrame.instance().resetInformation();
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

		List<MatchKurzInfo> allMatches = new ArrayList<>();
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
		showWaitInformation(1);

		try {
			String matchesString = "";

			while (tempBeginn.before(endDate)) {
				try {
					showWaitInformation(10);
					matchesString = MyConnector.instance().getMatchesArchive(teamId, tempBeginn.getTime(),
							tempEnd.getTime());
					showWaitInformation(20);
				} catch (Exception e) {
					// Info
					String msg = getLangString("Downloadfehler")
							+ " : Error fetching MatchArchiv : ";
					setInfoMsg(msg, InfoPanel.FEHLERFARBE);
					Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
							JOptionPane.ERROR_MESSAGE);
					showWaitInformation(0);
					return null;
				}

				showWaitInformation(40);
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

				showWaitInformation(80);
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
			showWaitInformation(0);
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
	 * @param matchType
	 *            matchType for the match to be downloaded.
	 * @param refresh
	 *            If true the match will always be downloaded.
	 *
	 * @return true if the match is in the db afterwards
	 */
	public static boolean downloadMatchData(int matchid, MatchType matchType, boolean refresh) {
		MatchKurzInfo info;
		if (DBManager.instance().isMatchVorhanden(matchid)) {
			info = DBManager.instance().getMatchesKurzInfoByMatchID(matchid);
		}
		else {
			info = new MatchKurzInfo();
			info.setMatchID(matchid);
			info.setMatchType(matchType);
		}
		return downloadMatchData(info, refresh);
	}

	public static boolean downloadMatchData( MatchKurzInfo info, boolean refresh)
	{
		int matchid = info.getMatchID();
		if (matchid < 0) {
			return false;
		}

		//waitDialog = getWaitDialog();
		showWaitInformation(1);
		// Only download if not present in the database, or if refresh is true
		if (refresh || !DBManager.instance().isMatchVorhanden(matchid)
				|| DBManager.instance().hasUnsureWeatherForecast(matchid)
				|| !DBManager.instance().isMatchLineupInDB(SourceSystem.HATTRICK.getId(), matchid)
		) {
			try {
				Matchdetails details = null;

				// If ids not found, download matchdetails to obtain them.
				// Highlights will be missing.
				// ArenaId==0 in division battles
				boolean newInfo = info.getHeimID()<=0 || info.getGastID()<=0;
				Weather.Forecast weatherDetails = info.getWeatherForecast();
				Boolean bWeatherKnown = ((weatherDetails != null) && weatherDetails.isSure());
				if ( newInfo || !bWeatherKnown) {

					showWaitInformation(10);
					details = fetchDetails(matchid, info.getMatchTyp(), null);
					if ( details != null) {
						info.setHeimID(details.getHeimId());
						info.setGastID(details.getGastId());
						info.setArenaId(details.getArenaID());
						info.setMatchDate(details.getSpielDatum().toString());
						int wetterId = details.getWetterId();
						if (wetterId != -1) {
							info.setMatchStatus(MatchKurzInfo.FINISHED);
							info.setWeather(Weather.getById(details.getWetterId()));
							info.setWeatherForecast(Weather.Forecast.HAPPENED);
						} else if (info.getArenaId() > 0) {
							info.setRegionId(details.getRegionId());

							if (!info.getWeatherForecast().isSure()) {
								Regiondetails regiondetails = getRegionDetails(info.getRegionId());
								if ( regiondetails != null) {
									SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
									java.sql.Timestamp matchDate = info.getMatchDateAsTimestamp();
									java.sql.Timestamp weatherDate = regiondetails.getFetchDatum();
									String wdate = fmt.format(weatherDate);
									String mdate = fmt.format(matchDate);
									if (mdate.equals(wdate)) {
										info.setWeatherForecast(Weather.Forecast.TODAY);
										info.setWeather(regiondetails.getWeather());
									} else {
										Calendar c = Calendar.getInstance();
										c.setTime(fmt.parse(wdate));
										c.add(Calendar.DATE, 1);
										if (fmt.format(c.getTime()).equals(mdate)) {
											info.setWeatherForecast(Weather.Forecast.TOMORROW);
										} else {
											info.setWeatherForecast((Weather.Forecast.UNSURE));
										}
										info.setWeather(regiondetails.getWeatherTomorrow());
									}
								}
							}
						}

						// get the other team
						int otherId;
						if (info.isHomeMatch()) {
							otherId = info.getGastID();
						} else {
							otherId = info.getHeimID();
						}
						if (otherId > 0) {
							Map<String, String> otherTeam = getTeam(otherId);
							info.setIsDerby(getRegionId(otherTeam) == HOVerwaltung.instance().getModel().getBasics().getRegionId());
							info.setIsNeutral(info.getArenaId() != HOVerwaltung.instance().getModel().getStadium().getArenaId()
									&& info.getArenaId() != getArenaId(otherTeam));
						} else {
							// Verlegenheitstruppe 08/15
							info.setIsDerby(false);
							info.setIsNeutral(false);
						}
					}
				}

				MatchLineup lineup = null;
				boolean success;
				if ( info.getMatchStatus() == MatchKurzInfo.FINISHED) {
					lineup = getMatchlineup(matchid, info.getMatchTyp(), info.getHeimID(), info.getGastID());

					if (lineup == null) {
						if ( !isSilentDownload()) {
							String msg = getLangString("Downloadfehler")
									+ " : Error fetching Matchlineup :";
							// Info
							setInfoMsg(msg, InfoPanel.FEHLERFARBE);
							Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
									JOptionPane.ERROR_MESSAGE);
						}
						return false;
					}
					
					// Get details with highlights.
					showWaitInformation(10);
					details = fetchDetails(matchid, info.getMatchTyp(), lineup);

					if (details == null) {
						HOLogger.instance().error(OnlineWorker.class,
								"Error downloading match. Details is null: " + matchid);
						return false;
					}
					info.setDuration(details.getLastMinute());
					info.setGastTore(details.getGuestGoals());
					info.setHeimTore(details.getHomeGoals());
					info.setGastID(lineup.getGastId());
					info.setGastName(lineup.getGastName());
					info.setHeimID(lineup.getHeimId());
					info.setHeimName(lineup.getHeimName());
					success = DBManager.instance().storeMatch(info, details, lineup);
				}
				else{
					// Update arena and region ids
					MatchKurzInfo[] matches = {info};
					DBManager.instance().storeMatchKurzInfos(matches);
					success = true;
				}
				if (!success) {
					showWaitInformation(0);
					return false;
				}
			} catch (Exception ex) {
				HOLogger.instance().error(OnlineWorker.class,
						"downloadMatchData:  Error in downloading match: " + ex);
				showWaitInformation(0);
				return false;
			}
		}
		showWaitInformation(0);
		return true;
	}

	private static Map<String, String> getTeam(int teamId)
	{
		String str = MyConnector.instance().fetchTeamDetails(teamId);
		return XMLTeamDetailsParser.parseTeamdetailsFromString(str, teamId);
	}

	private static int getRegionId(Map<String, String> team) {
		String  str = team.get("RegionID");
		if ( str != null ) return Integer.parseInt(str);
		return 0;
	}

	private  static int getArenaId(Map<String, String> team)
	{
		String str = team.get("ArenaID");
		if ( str != null ) return Integer.parseInt(str);
		return 0;
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
		List<MatchKurzInfo> matches = new ArrayList<>();
		boolean bOK = false;
		showWaitInformation(10);

		try {
			matchesString = MyConnector.instance().getMatches(teamId, forceRefresh, upcoming);
			bOK = (matchesString != null && matchesString.length() > 0);
			if (bOK)
				showWaitInformation(50);
			else
				showWaitInformation(0);
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + " : Error fetching matches: "
					+ e.getMessage();
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			HOLogger.instance().log(OnlineWorker.class, e);
			showWaitInformation(0);
			return null;
		}
		if (bOK) {
			matches.addAll(XMLMatchesParser.parseMatchesFromString(matchesString));

			// Store in DB if store is true
			if (store) {
				showWaitInformation(80);

				matches = FilterUserSelection(matches);
				DBManager.instance().storeMatchKurzInfos(
						matches.toArray(new MatchKurzInfo[matches.size()]));

				showWaitInformation(100);

				// Automatically download additional match infos (lineup + arena)
				for (MatchKurzInfo match : matches) {
					int curMatchId = match.getMatchID();
					boolean refresh = !DBManager.instance().isMatchVorhanden(curMatchId)
							|| DBManager.instance().hasUnsureWeatherForecast(curMatchId)
							|| !DBManager.instance().isMatchLineupInDB(SourceSystem.HATTRICK.getId(), curMatchId);

					if (refresh) {
						// No lineup or arenaId in DB
						boolean result = downloadMatchData(curMatchId, match.getMatchTyp(), refresh);
						if (!result) {
							break;
						}
					}
				}
			}
		}
		showWaitInformation(0);
		return matches;
	}

	public static List<MatchKurzInfo> FilterUserSelection(List<MatchKurzInfo> matches) {

		ArrayList<MatchKurzInfo> ret = new ArrayList<>();
		for (MatchKurzInfo m: matches) {
			switch (m.getMatchTyp()) {
				case INTSPIEL:
				case NATIONALCOMPNORMAL:
				case NATIONALCOMPCUPRULES:
				case NATIONALFRIENDLY:
				case PREPARATION:
				case EMERALDCUP:
				case RUBYCUP:
				case SAPPHIRECUP:
				case CONSOLANTECUP:
				case LEAGUE:
				case QUALIFICATION:
				case CUP:
				case FRIENDLYNORMAL:
				case FRIENDLYCUPRULES:
				case INTFRIENDLYNORMAL:
				case INTFRIENDLYCUPRULES:
				case MASTERS:
					if (UserParameter.instance().downloadCurrentMatchlist) {
						ret.add(m);
					}
					break;
				case TOURNAMENTGROUP:
					if (UserParameter.instance().downloadTournamentGroupMatches) {
						ret.add(m);
					}
					break;
				case TOURNAMENTPLAYOFF:
					if (UserParameter.instance().downloadTournamentPlayoffMatches) {
						ret.add(m);
					}
					break;
				case SINGLE:
					if (UserParameter.instance().downloadSingleMatches) {
						ret.add(m);
					}
					break;
				case LADDER:
					if (UserParameter.instance().downloadLadderMatches) {
						ret.add(m);
					}
					break;
				case DIVISIONBATTLE:
					if (UserParameter.instance().downloadDivisionBattleMatches) {
						ret.add(m);
					}
					break;
				default:
					HOLogger.instance().warning(OnlineWorker.class, "Unknown Matchtyp:" + m.getMatchTyp() + ". Is not downloaded!");
					break;
			}
		}
		return ret;
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
		showWaitInformation(10);

		// Lineups holen
		lineUp1 = fetchLineup(matchId, teamId1, matchType);
		if (lineUp1 != null) {
			showWaitInformation(50);
			if (teamId2 > 0)
				lineUp2 = fetchLineup(matchId, teamId2, matchType);

			// Merge the two
			if ((lineUp2 != null)) {
				if (lineUp1.getHeim() == null)
					lineUp1.setHeim(lineUp2.getHeim());
				else if (lineUp1.getGast() == null)
					lineUp1.setGast(lineUp2.getGast());
			} else {
				// Get the 2nd lineup
				if (lineUp1.getHeim() == null) {
					lineUp2 = fetchLineup(matchId, lineUp1.getHeimId(), matchType);
					if (lineUp2 != null)
						lineUp1.setHeim(lineUp2.getHeim());
				} else {
					lineUp2 = fetchLineup(matchId, lineUp1.getGastId(), matchType);
					if (lineUp2 != null)
						lineUp1.setGast(lineUp2.getGast());
				}
			}
		}
		showWaitInformation(0);
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
		try {
			showWaitInformation(10);
			leagueFixtures = MyConnector.instance().getLeagueFixtures(season, leagueID);
			bOK = (leagueFixtures != null && leagueFixtures.length() > 0);
			showWaitInformation(50);
		} catch (Exception e) {
			HOLogger.instance().log(OnlineWorker.class, e);
			String msg = getLangString("Downloadfehler") + " : Error fetching leagueFixture: "
					+ e.getMessage();
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			showWaitInformation(0);
			return false;
		}
		if (bOK) {
			HOModel hom = hov.getModel();
			hom.setFixtures(XMLSpielplanParser.parseSpielplanFromString(leagueFixtures));
			showWaitInformation(70);
			// Save to DB
			hom.saveFixtures();
			showWaitInformation(90);
		}
		showWaitInformation(0);
		return bOK;
	}

	protected static void showWaitInformation(int i) {
		if (HOMainFrame.launching.get()) return;
		String info;
		if ( i <=0 ){
			HOMainFrame.instance().resetInformation();
		}
		else{
			HOMainFrame.instance().setWaitInformation(i);
		}
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

	private static Matchdetails fetchDetails(int matchID, MatchType matchType, MatchLineup lineup) {
		String matchDetails = "";
		Matchdetails details = null;

		try {
			matchDetails = MyConnector.instance().getMatchdetails(matchID, matchType);
			if (matchDetails.length() == 0) {
				HOLogger.instance().warning(OnlineWorker.class,
						"Unable to fetch details for match " + matchID);
				return null;
			}
			showWaitInformation(20);
			details = XMLMatchdetailsParser.parseMachtdetailsFromString(matchDetails, lineup);
			showWaitInformation(40);
			if (details == null) {
				HOLogger.instance().warning(OnlineWorker.class,
						"Unable to fetch details for match " + matchID);
				return null;
			}
			String arenaString = MyConnector.instance().getArena(details.getArenaID());
			showWaitInformation(50);
			String regionIdAsString = XMLArenaParser.parseArenaFromString(arenaString).get(
					"RegionID");
			details.setRegionId(Integer.parseInt(regionIdAsString));
		} catch (Exception e) {
			String msg = getLangString("Downloadfehler") + ": Error fetching Matchdetails XML.: ";
			// Info
			setInfoMsg(msg, InfoPanel.FEHLERFARBE);
			Helper.showMessage(HOMainFrame.instance(), msg, getLangString("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			showWaitInformation(0);
			return null;
		}
		showWaitInformation(0);
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
		for (MatchKurzInfo info : infos) {
			int curMatchId = info.getMatchID();
			if (!DBManager.instance().isMatchLineupInDB(SourceSystem.HATTRICK.getId(), curMatchId)) {
				// Check if the lineup is available
				if (info.getMatchStatus() == MatchKurzInfo.FINISHED) {
					HOLogger.instance().debug(OnlineWorker.class, "Get Lineup : " + curMatchId);
					bOK = downloadMatchData(curMatchId, info.getMatchTyp(), false);
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

	public static List<TrainingEvent> getTrainingEvents(int playerId ){
		try{
			String xml = MyConnector.instance().getTrainingEvents(playerId);
			return XMLTrainingEventsParser.parseTrainingEvents(xml);
		}
		catch(Exception e){
			String msg = getLangString("Downloadfehler") + " : Error fetching training events :";
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
	private static void saveHRFToFile(JDialog parent, String hrfData) {
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
				try {
					saveFile(file.getPath(), hrfData);
				} catch (IOException e) {
					Helper.showMessage(HOMainFrame.instance(),
							HOVerwaltung.instance().getLanguageString("Show_SaveHRF_Failed") + " " + file.getParentFile()  +".\nError: " + e.getMessage(),
							getLangString("Fehler"), JOptionPane.ERROR_MESSAGE);
				}
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
		HOMainFrame.instance().setInformation(msg);
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
		HOMainFrame.instance().setInformation(msg, color);
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

	public static boolean isSilentDownload() {
		return MyConnector.instance().isSilentDownload();
	}

	public static void setSilentDownload(boolean silentDownload) {
		MyConnector.instance().setSilentDownload(silentDownload);
	}
}
