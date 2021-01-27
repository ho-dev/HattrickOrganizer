package core.file.xml;

import core.db.DBManager;
import core.model.Tournament.TournamentDetails;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.model.misc.Basics;
import core.util.HOLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static core.net.OnlineWorker.getTournamentDetails;

public class XMLTournamentDetailsParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLTournamentDetailsParser() {
	}

	public static TournamentDetails parseTournamentDetailsFromString(String input) {
		return createTournamentDetails(XMLManager.parseString(input));
	}


	private static TournamentDetails createTournamentDetails(Document doc) {
		TournamentDetails oTournamentDetails = new TournamentDetails();
		//DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (doc != null) {
			Element root = doc.getDocumentElement();
			try {
				NodeList list = root.getElementsByTagName("Tournament");

				Element ele = (Element) list.item(0);
				Element tmp;
				Date tempDate;

				tmp = (Element) ele.getElementsByTagName("TournamentId").item(0);
				oTournamentDetails.setTournamentId(Integer.parseInt(tmp.getFirstChild().getNodeValue()));

				tmp = (Element) ele.getElementsByTagName("Name").item(0);
				oTournamentDetails.setName(tmp.getFirstChild().getNodeValue());

				tmp = (Element) ele.getElementsByTagName("TournamentType").item(0);
				oTournamentDetails.setTournamentType(Integer.parseInt(tmp.getFirstChild().getNodeValue()));

				tmp = (Element) ele.getElementsByTagName("Season").item(0);
				oTournamentDetails.setSeason((short) Integer.parseInt(tmp.getFirstChild().getNodeValue()));

				tmp = (Element) ele.getElementsByTagName("LogoUrl").item(0);
				if (tmp.getFirstChild() != null) {
					oTournamentDetails.setLogoUrl(tmp.getFirstChild().getNodeValue());
				}

				tmp = (Element) ele.getElementsByTagName("TrophyType").item(0);
				oTournamentDetails.setTrophyType(Integer.parseInt(tmp.getFirstChild().getNodeValue()));

				tmp = (Element) ele.getElementsByTagName("NumberOfTeams").item(0);
				oTournamentDetails.setNumberOfTeams(Integer.parseInt(tmp.getFirstChild().getNodeValue()));

				tmp = (Element) ele.getElementsByTagName("NumberOfGroups").item(0);
				oTournamentDetails.setNumberOfGroups(Integer.parseInt(tmp.getFirstChild().getNodeValue()));

				tmp = (Element) ele.getElementsByTagName("LastMatchRound").item(0);
				oTournamentDetails.setLastMatchRound((short) Integer.parseInt(tmp.getFirstChild().getNodeValue()));

				tmp = (Element) ele.getElementsByTagName("FirstMatchRoundDate").item(0);
				tempDate = Basics.parseHattrickDate(tmp.getFirstChild().getNodeValue());
				oTournamentDetails.setFirstMatchRoundDate(tempDate);

				tmp = (Element) ele.getElementsByTagName("NextMatchRoundDate").item(0);
				tempDate = Basics.parseHattrickDate(tmp.getFirstChild().getNodeValue());
				oTournamentDetails.setNextMatchRoundDate(tempDate);

				tmp = (Element) ele.getElementsByTagName("IsMatchesOngoing").item(0);
				oTournamentDetails.setMatchesOngoing("1".equals(tmp.getFirstChild().getNodeValue()));

				// Creator info
				list = root.getElementsByTagName("Creator");
				ele = (Element) list.item(0);

				tmp = (Element) ele.getElementsByTagName("UserId").item(0);
				oTournamentDetails.setCreator_UserId(Integer.parseInt(tmp.getFirstChild().getNodeValue()));

				tmp = (Element) ele.getElementsByTagName("Loginname").item(0);
				if (tmp.getFirstChild() != null) {
					oTournamentDetails.setCreator_Loginname(tmp.getFirstChild().getNodeValue());
				}

			} catch (Exception e) {
				HOLogger.instance().log(XMLTournamentDetailsParser.class, e);
			}
			}
			return oTournamentDetails;
		}
	}
