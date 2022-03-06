// %3623758565:de.hattrickorganizer.logik.xml%
/*
 * XMLSpielplanParser.java
 *
 * Created on 7. Oktober 2003, 13:42
 */
package core.file.xml;

import core.model.series.Paarung;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.Helper;
import module.series.Spielplan;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author thomas.werth
 */
public class XMLSpielplanParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLSpielplanParser() {
	}

	public static Spielplan parseSpielplanFromString(String input) {
		Spielplan plan;
		try {
			plan = createSpielplan(XMLManager.parseString(input));
		} catch (RuntimeException e) {
			HOLogger.instance().error(XMLSpielplanParser.class,
					"parseSpielplanFromString: " + e + "\ninput xml was:\n " + input);
			throw e;
		}
		return plan;
	}

	private static Paarung createPaarung(Element ele) {
		Element tmp;
		Paarung spiel = new Paarung();

		tmp = (Element) ele.getElementsByTagName("MatchID").item(0);
		spiel.setMatchId(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
		tmp = (Element) ele.getElementsByTagName("MatchRound").item(0);
		spiel.setSpieltag(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
		tmp = (Element) ele.getElementsByTagName("HomeTeamID").item(0);
		spiel.setHeimId(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
		tmp = (Element) ele.getElementsByTagName("AwayTeamID").item(0);
		spiel.setGastId(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
		tmp = (Element) ele.getElementsByTagName("HomeTeamName").item(0);
		spiel.setHeimName(tmp.getFirstChild().getNodeValue());
		tmp = (Element) ele.getElementsByTagName("AwayTeamName").item(0);
		spiel.setGastName(tmp.getFirstChild().getNodeValue());
		tmp = (Element) ele.getElementsByTagName("MatchDate").item(0);
		spiel.setDatum(tmp.getFirstChild().getNodeValue());

		// Zum Schluss weil nicht immer vorhanden
		if (ele.getElementsByTagName("AwayGoals").getLength() > 0) {
			tmp = (Element) ele.getElementsByTagName("AwayGoals").item(0);
			spiel.setToreGast(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
			tmp = (Element) ele.getElementsByTagName("HomeGoals").item(0);
			spiel.setToreHeim(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
		}

		return spiel;
	}

	private static Spielplan createSpielplan(Document doc) {
		Spielplan plan = new Spielplan();
		Element ele;
		Element root;
		NodeList list;

		if (doc == null) {
			return plan;
		}

		// Tabelle erstellen
		root = doc.getDocumentElement();

		try {
			// Daten füllen
			ele = (Element) root.getElementsByTagName("LeagueLevelUnitID").item(0);
			plan.setLigaId(Integer.parseInt(ele.getFirstChild().getNodeValue()));

			try {
				ele = (Element) root.getElementsByTagName("LeagueLevelUnitName").item(0);
				plan.setLigaName(ele.getFirstChild().getNodeValue());
			} catch (Exception e) {
				plan.setLigaName("");
			}

			ele = (Element) root.getElementsByTagName("Season").item(0);
			plan.setSaison(Integer.parseInt(ele.getFirstChild().getNodeValue()));
			ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
			plan.setFetchDate(HODateTime.fromHT(ele.getFirstChild().getNodeValue()));

			// Einträge adden
			list = root.getElementsByTagName("Match");

			for (int i = 0; (list != null) && (i < list.getLength()); i++) {
				plan.addEintrag(createPaarung((Element) list.item(i)));
			}
		} catch (Exception e) {
			HOLogger.instance().log(XMLSpielplanParser.class, e);
			plan = null;
		}

		return plan;
	}
}
