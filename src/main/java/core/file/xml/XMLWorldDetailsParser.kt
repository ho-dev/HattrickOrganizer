// %3205245740:de.hattrickorganizer.logik.xml%
/*
 * xmlLeagureFixturesMiniParser.java
 *
 * Created on 12. Januar 2004, 13:38
 */
package core.file.xml;

import core.model.HOVerwaltung;
import core.model.WorldDetailLeague;
import core.util.HODateTime;
import core.util.HOLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLWorldDetailsParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLWorldDetailsParser() {
	}

	public static Map<String, String> parseWorldDetailsFromString(String inputStream,
			String leagueID) {
		return parseDetails(XMLManager.parseString(inputStream), leagueID);
	}

	public static List<WorldDetailLeague> parseDetails(Document doc) {
		Element ele;
		Element root;
		List<WorldDetailLeague> detailsList = new ArrayList<>();
		NodeList list;
		if (doc == null) {
			return detailsList;
		}

		// Tabelle erstellen
		root = doc.getDocumentElement();

		try {
			root = (Element) root.getElementsByTagName("LeagueList").item(0);
			list = root.getElementsByTagName("League");

			for (int i = 0; i < list.getLength(); i++) {
				root = (Element) list.item(i);
				WorldDetailLeague tmp = new WorldDetailLeague();
				ele = (Element) root.getElementsByTagName("LeagueID").item(0);
				tmp.setLeagueId(Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) root.getElementsByTagName("EnglishName").item(0);
				tmp.setCountryName(XMLManager.getFirstChildNodeValue(ele));
				ele = (Element) root.getElementsByTagName("ActiveTeams").item(0);
				tmp.setActiveUsers(Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)));

				root = (Element) root.getElementsByTagName("Country").item(0);
				ele = (Element) root.getElementsByTagName("CountryID").item(0);
				if(ele != null) {
					tmp.setCountryId(Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)));
				} else {
					tmp.setCountryId(tmp.getLeagueId());
				}
				detailsList.add(tmp);
			}
		} catch (Exception e) {
			HOLogger.instance().log(XMLWorldDetailsParser.class, e);
		}

		return detailsList;
	}

	private static Map<String, String> initWorldDetailsMap(){
		var map = new MyHashtable();
		var model = HOVerwaltung.instance().getModel();
		if ( model != null ){
			var basics = model.getBasics();
			var xtra = model.getXtraDaten();
			if ( basics != null && xtra != null){
				var trainingDate = xtra.getNextTrainingDate();
				var economyDate = xtra.getEconomyDate();
				var seriesMatchDate = xtra.getSeriesMatchDate();
				var now = HODateTime.now();
				while (trainingDate.isBefore(now)) trainingDate = trainingDate.plusDaysAtSameLocalTime(7);
				while (economyDate.isBefore(now)) economyDate = economyDate.plusDaysAtSameLocalTime(7);
				while (seriesMatchDate.isBefore(now)) seriesMatchDate = seriesMatchDate.plusDaysAtSameLocalTime(7);

				map.put("LeagueID", "" + basics.getLiga());
				map.put("Season", "" + basics.getSeason());
				map.put("SeasonOffset", "" + basics.getSeasonOffset());
				map.put("MatchRound", "" + basics.getSpieltag());
				map.put("TrainingDate", trainingDate.toHT());
				map.put("EconomyDate", economyDate.toHT());
				map.put("SeriesMatchDate", seriesMatchDate.toHT());
				map.put("CountryID", "" + model.getXtraDaten().getCountryId());
			}
		}
		return map;
	}

	private static Map<String, String> parseDetails(Document doc, String leagueID) {
		Element ele;
		Element root;
		Map<String, String> map = initWorldDetailsMap();

		NodeList list;
		String tempLeagueID;

		if (doc == null) {
			return map;
		}

		// Tabelle erstellen
		root = doc.getDocumentElement();

		try {
			// Daten füllen
			root = (Element) root.getElementsByTagName("LeagueList").item(0);

			// Einträge adden
			list = root.getElementsByTagName("League");

			for (int i = 0; i < list.getLength(); i++) {
				tempLeagueID = XMLManager.getFirstChildNodeValue((Element) ((Element) list.item(i))
						.getElementsByTagName("LeagueID").item(0));

				// Liga suchen
				if (tempLeagueID.equals(leagueID)) {
					root = (Element) list.item(i);

					// Land
					ele = (Element) root.getElementsByTagName("LeagueID").item(0);
					map.put("LeagueID", (XMLManager.getFirstChildNodeValue(ele)));
					ele = (Element) root.getElementsByTagName("Season").item(0);
					map.put("Season", (XMLManager.getFirstChildNodeValue(ele)));
					ele = (Element) root.getElementsByTagName("SeasonOffset").item(0);
					map.put("SeasonOffset", (XMLManager.getFirstChildNodeValue(ele)));
					ele = (Element) root.getElementsByTagName("MatchRound").item(0);
					map.put("MatchRound", (XMLManager.getFirstChildNodeValue(ele)));

					// Dates
					ele = (Element) root.getElementsByTagName("TrainingDate").item(0);
					map.put("TrainingDate", (XMLManager.getFirstChildNodeValue(ele)));
					ele = (Element) root.getElementsByTagName("EconomyDate").item(0);
					map.put("EconomyDate", (XMLManager.getFirstChildNodeValue(ele)));
					ele = (Element) root.getElementsByTagName("SeriesMatchDate").item(0);
					map.put("SeriesMatchDate", (XMLManager.getFirstChildNodeValue(ele)));

					// Country
					root = (Element) root.getElementsByTagName("Country").item(0);
					ele = (Element) root.getElementsByTagName("CountryID").item(0);
					map.put("CountryID", (XMLManager.getFirstChildNodeValue(ele)));
					
					// Remove for ugly second team fix
					
					ele = (Element) root.getElementsByTagName("CurrencyRate").item(0);
					map.put("CurrencyRate", (XMLManager.getFirstChildNodeValue(ele)));

					// fertig
					break;
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(XMLWorldDetailsParser.class, e);
		}

		return map;
	}
	
	public static TeamInfo updateTeamInfoWithCurrency(TeamInfo info, String input) {
		
		Document doc = XMLManager.parseString(input);
		
		Element root = doc.getDocumentElement();
		root = (Element) root.getElementsByTagName("LeagueList").item(0);
		root = (Element) root.getElementsByTagName("Country").item(0);
		
		Element ele = (Element) root.getElementsByTagName("CurrencyRate").item(0);
		info.setCurrencyRate(XMLManager.getFirstChildNodeValue(ele));

		ele = (Element) root.getElementsByTagName("CountryID").item(0);
		info.setCountryId(XMLManager.getFirstChildNodeValue(ele));

		return info;
	}
	
}
