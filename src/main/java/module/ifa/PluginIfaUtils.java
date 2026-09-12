package module.ifa;

import core.db.DBManager;
import core.file.xml.TeamInfo;
import core.file.xml.XMLManager;
import core.file.xml.XMLTeamDetailsParser;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.WorldDetailLeague;
import core.model.WorldDetailsManager;
import core.model.enums.MatchType;
import core.net.Connector;
import core.net.DownloadDialog;
import core.util.HODateTime;
import core.util.HOLogger;
import module.ifa.gif.Quantize;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PluginIfaUtils {

    private static final int MAXIMUM_NUMBER_OF_DOWNLOADED_MATCHES = 50;

    private PluginIfaUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static String getTeamDetails(int teamID) {
        return Connector.instance().getTeamDetails(teamID);
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

    public static void updateMatchesTable() {
        int retries = 1;
        HODateTime time;
        do {
            time = HOVerwaltung.instance().getModel().getBasics().getActivationDate();
            if (time != null && !time.isBefore(HODateTime.HT_START)) {
                break;
            }
            DownloadDialog.instance();
        } while (retries-- > 0);

        try {
            HOMainFrame.instance().resetInformation();
            if (time != null) {
                var from = HODateTime.fromDbTimestamp(DBManager.instance().getLastIFAMatchDate());
                if (from == null) {
                    from = time;
                }
                var today = HODateTime.now();
                while (from.isBefore(today)) {
                    var to = from.plus(60, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);
                    if (to.isAfter(today)) {
                        to = today;
                    }
                    insertMatches(from, to);
                    from = from.plus(60, ChronoUnit.DAYS);
                }
            }
            HOMainFrame.instance().setInformationCompleted();
        } catch (Exception e) {
            HOMainFrame.instance().resetInformation();
            HOLogger.instance().error(PluginIfaUtils.class, e);
        }
    }

    static BufferedImage quantizeBufferedImage(BufferedImage bufferedImage) throws IOException {
        int[][] pixels = getPixels(bufferedImage);
        int[] palette = Quantize.quantizeImage(pixels, 256);
        int w = pixels.length;
        int h = pixels[0].length;

        BufferedImage bufIma = new BufferedImage(w, h, 1);

        for (int x = w; x-- > 0; ) {
            for (int y = h; y-- > 0; ) {
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
        for (int x = w; x-- > 0; ) {
            for (int y = h; y-- > 0; ) {
                pixels[x][y] = pix[(y * w + x)];
            }
        }

        return pixels;
    }

    public static double getCoolness(int countryId) {
        WorldDetailLeague league = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(countryId);
        if (league == null || league.getActiveUsers() == 0) return 0;
        return (double) WorldDetailsManager.instance().getTotalUsers()
            / (double) league.getActiveUsers();
    }

    public static void downloadMatch(IfaMatch match) {
        var matchesArchive = Connector.instance().getMatchesArchive(HOVerwaltung.instance().getModel().getBasics().getTeamId(), match.getPlayedDate(), match.getPlayedDate());
        var xmlDoc = XMLManager.parseString(matchesArchive);
        if (xmlDoc == null) {
            return;
        }
        final var basics = HOVerwaltung.instance().getModel().getBasics();
        final int ownLeague = basics.getLiga();
        final int ownId = basics.getTeamId();
        final int ownCountryId = basics.getCountryId();
        readMatch(0, xmlDoc, match, ownId, ownLeague, ownCountryId);
    }

    private static boolean readMatch(int index, Document xmlDoc, IfaMatch match, int ownId, int ownCountryId, int ownLeague) {
        int matchTypeId = Integer.parseInt(parseXmlElement(xmlDoc, "MatchType", index, "Match"));
        var matchDateString = parseXmlElement(xmlDoc, "MatchDate", index, "Match");
        var matchDate = HODateTime.fromHT(matchDateString);
        int homeTeamID = Integer
            .parseInt(parseXmlElement(xmlDoc, "HomeTeamID", index, "HomeTeam"));
        int awayTeamID = Integer
            .parseInt(parseXmlElement(xmlDoc, "AwayTeamID", index, "AwayTeam"));
        int matchID = Integer.parseInt(parseXmlElement(xmlDoc, "MatchID", index, "Match"));
        int homeTeamGoals = Integer.parseInt(parseXmlElement(xmlDoc, "HomeGoals", index,
            "Match"));
        int awayTeamGoals = Integer.parseInt(parseXmlElement(xmlDoc, "AwayGoals", index,
            "Match"));
        try {
            int opponentId;
            // Some ifs inserted to avoid downloading own team info for every match
            if (homeTeamID == ownId) {
                opponentId = awayTeamID;
            } else if (awayTeamID == ownId) {
                opponentId = homeTeamID;
            } else {
                HOLogger.instance().error(null, "IFA: Owner team not involved in match");
                return false;
            }

            List<TeamInfo> opp = XMLTeamDetailsParser.getTeamInfoFromString(getTeamDetails(opponentId));
            int opponentLeagueId = 0;
            int opponentCountryId = 0;
            int homeLeagueId;
            int awayLeagueId;
            int homeCountryId;
            int awayCountryId;

            for (TeamInfo o : opp) {
                if (o.getTeamId() == opponentId) {
                    opponentLeagueId = o.getLeagueId();
                    opponentCountryId = o.getCountryId();
                    break;
                }
            }
            if (homeTeamID == ownId) {
                homeLeagueId = ownLeague;
                homeCountryId = ownCountryId;
                awayLeagueId = opponentLeagueId;
                awayCountryId = opponentCountryId;
            } else {
                awayLeagueId = ownLeague;
                awayCountryId = ownCountryId;
                homeLeagueId = opponentLeagueId;
                homeCountryId = opponentCountryId;
            }

            match.setMatchTyp(matchTypeId);
            match.setMatchId(matchID);
            match.setPlayedDate(matchDate);
            match.setHomeLeagueId(homeLeagueId);
            match.setHomeCountryId(homeCountryId);
            match.setHomeTeamId(homeTeamID);
            match.setAwayLeagueId(awayLeagueId);
            match.setAwayCountryId(awayCountryId);
            match.setAwayTeamId(awayTeamID);
            match.setHomeTeamGoals(homeTeamGoals);
            match.setAwayTeamGoals(awayTeamGoals);
        } catch (Exception e) {
            HOLogger.instance().error(PluginIfaUtils.class, "Error getting data for match " + matchID + " (" + matchDateString + " / HomeTeam " + homeTeamID + " vs. AwayTeam " + awayTeamID + ")");
            return false;
        }
        return true;
    }

    private static void insertMatches(HODateTime from, HODateTime to) {
        HODateTime matchDate = null;
        String matchesArchive = Connector.instance().getMatchesArchive(HOVerwaltung.instance().getModel().getBasics().getTeamId(), from, to);
        Document doc = XMLManager.parseString(matchesArchive);
        // Container for the matches. If more than 50 matches have occured between FirstMatchDate and LastMatchDate, only the 50 first will be returned.
        int matchesCount = ((Element) doc.getDocumentElement().getElementsByTagName("MatchList")
            .item(0)).getElementsByTagName("Match").getLength();

        final var basics = HOVerwaltung.instance().getModel().getBasics();
        final int ownLeague = basics.getLiga();
        final int ownId = basics.getTeamId();
        final int ownCountryId = basics.getCountryId();
        for (int i = 0; i < matchesCount; i++) {
            int matchTypeId = Integer.parseInt(parseXmlElement(doc, "MatchType", i, "Match"));
            IfaMatch match = new IfaMatch(matchTypeId);
            if (!readMatch(i, doc, match, ownId, ownCountryId, ownLeague)) {
                continue;
            }

            var matchType = MatchType.getById(match.getMatchTyp());
            if (matchType.isFriendly() || matchType.isFriendlyOfNtTeam()) {
                if (!DBManager.instance().isIFAMatchinDB(match.getMatchId(), matchTypeId)) {
                    DBManager.instance().insertIFAMatch(match);
                }
            }
        }
        if (matchesCount == MAXIMUM_NUMBER_OF_DOWNLOADED_MATCHES) {
            insertMatches(matchDate, to);
        }
    }
}
