package module.ifa;

import core.db.DBManager;
import core.file.xml.TeamInfo;
import core.file.xml.XMLManager;
import core.file.xml.XMLTeamDetailsParser;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.WorldDetailLeague;
import core.model.WorldDetailsManager;
import core.model.match.MatchType;
import core.net.DownloadDialog;
import core.net.MyConnector;
import core.util.DateTimeUtils;
import core.util.HOLogger;
import module.ifa.gif.Quantize;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.JWindow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PluginIfaUtils {
	public static final boolean HOME = true;
	public static final boolean AWAY = false;

	private static String getTeamDetails(int teamID) throws Exception {
		return MyConnector.instance().getTeamdetails(teamID);
	}

	private static String parseXmlElement(Document doc, String element, int i, String eleText) {
		String value = "";
		try {
			Element ele = doc.getDocumentElement();
			Element tmpEle = (Element) ele.getElementsByTagName(eleText).item(i);
			tmpEle = (Element) tmpEle.getElementsByTagName(element).item(0);
			value = XMLManager.getFirstChildNodeValue(tmpEle);
		} catch (Exception e) {
			HOLogger.instance().error(PluginIfaUtils.class, e);
		}
		return value;
	}

	public static boolean updateMatchesTable() {
		Timestamp time;
		boolean retry = true;

		do {
			time = HOVerwaltung.instance().getModel().getBasics().getActivationDate();

			if (time != null && time.getTime() > 100) {
				break;
			}
			new DownloadDialog();
		} while (retry == true && !(retry = false));

		//JWindow waitWindow = new LoginWaitDialog(HOMainFrame.instance());
		try {
			//waitWindow.setVisible(true);
			HOMainFrame.instance().setWaitInformation(0);
			if(time != null)
			{
				Date from = DateHelper.getDate(DBManager.instance()
						.getLastIFAMatchDate(time.toString()));
				try {
					List<Date[]> times = getTimeIntervalsForRetrieval(from);
					for (Iterator<Date[]> i = times.iterator(); i.hasNext();) {
						Date[] fromTo = i.next();
						insertMatches(fromTo[0], fromTo[1]);
					}
				} catch (Exception e) {
					insertMatches(from, new Date());
				}
			}
			HOMainFrame.instance().resetInformation();
		} catch (Exception e) {
			HOMainFrame.instance().resetInformation();
			HOLogger.instance().error(PluginIfaUtils.class, e);
			return false;
		}
		return true;
	}

	static BufferedImage quantizeBufferedImage(BufferedImage bufferedImage) throws IOException {
		int[][] pixels = getPixels(bufferedImage);
		int[] palette = Quantize.quantizeImage(pixels, 256);
		int w = pixels.length;
		int h = pixels[0].length;
		int[] pix = new int[w * h];

		BufferedImage bufIma = new BufferedImage(w, h, 1);

		for (int x = w; x-- > 0;) {
			for (int y = h; y-- > 0;) {
				pix[(y * w + x)] = palette[pixels[x][y]];
				bufIma.setRGB(x, y, palette[pixels[x][y]]);
			}
		}
		return bufIma;
	}

	private static int[][] getPixels(Image image) throws IOException {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		int[] pix = new int[w * h];
		PixelGrabber grabber = new PixelGrabber(image, 0, 0, w, h, pix, 0, w);
		try {
			if (!grabber.grabPixels())
				throw new IOException("Grabber returned false: " + grabber.status());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int[][] pixels = new int[w][h];
		for (int x = w; x-- > 0;) {
			for (int y = h; y-- > 0;) {
				pixels[x][y] = pix[(y * w + x)];
			}
		}

		return pixels;
	}

	public static double getCoolness(int countryId) {
		WorldDetailLeague league = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(
				countryId);
		return (double) WorldDetailsManager.instance().getTotalUsers()
				/ (double) league.getActiveUsers();
	}

	@SuppressWarnings("deprecation")
	private static void insertMatches(Date from, Date to) throws Exception {
		StringBuilder errors = new StringBuilder();
		String matchDate = null;
		String matchesArchive = MyConnector.instance().getMatchesArchive(HOVerwaltung.instance().getModel().getBasics().getTeamId(), from, to);
		Document doc = XMLManager.parseString(matchesArchive);

		int matchesCount = ((Element) doc.getDocumentElement().getElementsByTagName("MatchList")
				.item(0)).getElementsByTagName("Match").getLength();
		
		int ownLeague = HOVerwaltung.instance().getModel().getBasics().getLiga();
		int ownId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		int opponentId;
		int opponentLeague = 0;
		
		for (int i = 0; i < matchesCount; i++) {
			IfaMatch match = new IfaMatch();

			int matchTypeId = Integer.parseInt(parseXmlElement(doc, "MatchType", i, "Match"));
			MatchType matchType = MatchType.getById(matchTypeId);
			matchDate = parseXmlElement(doc, "MatchDate", i, "Match");
			if (matchType == MatchType.FRIENDLYCUPRULES || matchType == MatchType.FRIENDLYNORMAL
					|| matchType == MatchType.INTFRIENDLYCUPRULES
					|| matchType == MatchType.INTFRIENDLYNORMAL
					|| matchType == MatchType.NATIONALFRIENDLY) {
				int homeTeamID = Integer
						.parseInt(parseXmlElement(doc, "HomeTeamID", i, "HomeTeam"));
				int awayTeamID = Integer
						.parseInt(parseXmlElement(doc, "AwayTeamID", i, "AwayTeam"));
				int matchID = Integer.parseInt(parseXmlElement(doc, "MatchID", i, "Match"));
				if (!DBManager.instance().isIFAMatchinDB(matchID)) {
					int homeTeamGoals = Integer.parseInt(parseXmlElement(doc, "HomeGoals", i,
							"Match"));
					int awayTeamGoals = Integer.parseInt(parseXmlElement(doc, "AwayGoals", i,
							"Match"));
					try {
						
						int homeLeagueIndex = 0;
						int awayLeagueIndex = 0;
						
						// Some ifs inserted to avoid downloading own team info for every match
						
						if (homeTeamID == ownId) {
							opponentId = awayTeamID;
						} else if (awayTeamID == ownId) {
							opponentId = homeTeamID;
						} else {
							HOLogger.instance().error(null, "IFA: Owner team not involved in match");
							continue;
						}
						
						List<TeamInfo> opp = XMLTeamDetailsParser.getTeamInfoFromString(getTeamDetails(opponentId));
						for (TeamInfo o : opp) {
							if (o.getTeamId() == opponentId) {
								opponentLeague = o.getLeagueId();
								break;
							}
						}
						
						if (homeTeamID == ownId) {
							homeLeagueIndex = ownLeague;
							awayLeagueIndex = opponentLeague;
						} else {
							awayLeagueIndex = ownLeague;
							homeLeagueIndex = opponentLeague;
						}
						
						match.setMatchId(matchID);
						match.setPlayedDateString(matchDate);
						match.setHomeLeagueId(homeLeagueIndex);
						match.setHomeTeamId(homeTeamID);
						match.setAwayLeagueId(awayLeagueIndex);
						match.setAwayTeamId(awayTeamID);
						match.setHomeTeamGoals(homeTeamGoals);
						match.setAwayTeamGoals(awayTeamGoals);

						DBManager.instance().insertIFAMatch(match);
					} catch (Exception e) {
						errors.append("Error 1 getting data for match " + matchID + " ("
								+ matchDate + " / HomeTeam " + homeTeamID + " vs. AwayTeam "
								+ awayTeamID + ")<br>");
					}
				}
			}
		}

		if (errors.length() > 0) {
			HOLogger.instance().error(PluginIfaUtils.class, errors.toString());
		}

		if (matchesCount == 50) {
			insertMatches(DateHelper.getDate(matchDate), to);
		}
	}

	private static List<Date[]> getTimeIntervalsForRetrieval(Date from) {
		List<Date[]> ret = new ArrayList<Date[]>();
		Date start = DateTimeUtils.getDateWithMinTime(from);
		Calendar end = new GregorianCalendar();
		end.setLenient(true);
		end.add(5, 1);

		Calendar tmpF = new GregorianCalendar();
		tmpF.setTime(start);
		Calendar tmpT = new GregorianCalendar();
		tmpT.setTime(tmpF.getTime());
		tmpF.setLenient(true);
		tmpT.setLenient(true);
		while (tmpT.before(end)) {
			tmpT.add(2, 2);
			if (tmpT.after(end)) {
				tmpT = end;
			}
			ret.add(new Date[] { tmpF.getTime(), tmpT.getTime() });
			tmpF.setTime(tmpT.getTime());
		}
		return ret;
	}
}
