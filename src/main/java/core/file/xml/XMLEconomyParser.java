package core.file.xml;


import core.util.HOLogger;
import java.util.Map;
import core.util.HTDatetime;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static core.net.MyConnector.VERSION_ECONOMY;


public class XMLEconomyParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLEconomyParser() {
	}

	public static Map<String, String> parseEconomyFromString(String inputStream) {
		var mapEconomy = parseDetails(XMLManager.parseString(inputStream));

		// Manually add SponsorsBonus which is missing in version <= 1.3
		HTDatetime fetchedDate = new HTDatetime(mapEconomy.get("FetchedDate"));
		var season = fetchedDate.getHTSeason();
		if ((Float.parseFloat(VERSION_ECONOMY) >= 1.3f ) && (season >= 80)){
			var iSponsorBonusIncome = assessSponsorBonusIncome(mapEconomy, new String[]{"LastIncomeSpectators", "LastIncomeSponsors", "LastIncomeFinancial", "LastIncomeTemporary", "LastIncomeSoldPlayers",
					"LastIncomeSoldPlayersCommission"}, "IncomeSum");
			mapEconomy.put("IncomeSponsorsBonus", iSponsorBonusIncome.toString());
			var iLastSponsorBonusIncome = assessSponsorBonusIncome(mapEconomy, new String[]{"LastIncomeSpectators", "LastIncomeSponsors", "LastIncomeFinancial", "LastIncomeTemporary", "LastIncomeSoldPlayers",
					"LastIncomeSoldPlayersCommission"}, "LastIncomeSum");
			mapEconomy.put("IncomeSponsorsBonus", iSponsorBonusIncome.toString());
		}
		return mapEconomy;
	}

	/**
	 * A method to fix the issue that as of January 2022, the economy CHPP file is still
	 * in version 1.3 and does not include yet the newly introduced (season 80) sponsors bonus information
	 * This method will derive the bonus information by spotting the mismatch between the total income and the individual elements
	 *
	 * @return the assessed sponsor bonus income
	 */
	private static Integer assessSponsorBonusIncome(Map<String, String> mapEconomy, String[] incomeSources, String totalIncomeSources){
		var calculatedTotalIncome = 0;
		for (String incomeSource : incomeSources)
		{
			calculatedTotalIncome += Integer.parseInt(mapEconomy.get(incomeSource));
//			System.out.println("%s: %s".formatted(incomeSource, Integer.parseInt(mapEconomy.get(incomeSource))));
		}
		return Integer.parseInt(mapEconomy.get(totalIncomeSources)) - calculatedTotalIncome;
	}

	private static Map<String, String> parseDetails(@Nullable Document doc) {
		Map<String, String> map = new MyHashtable();

		if (doc == null) {
			return map;
		}

		try {
			Element root = doc.getDocumentElement();
			Element ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
			map.put("FetchedDate", (XMLManager.getFirstChildNodeValue(ele)));

			// change root => Team node
			root = (Element) root.getElementsByTagName("Team").item(0);

			ele = (Element) root.getElementsByTagName("TeamID").item(0);
			map.put("TeamID", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("TeamName").item(0);
			map.put("TeamName", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("Cash").item(0);
			map.put("Cash", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("ExpectedCash").item(0);
			map.put("ExpectedCash", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("SponsorsPopularity").item(0);
			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				map.put("SponsorsPopularity",(XMLManager.getFirstChildNodeValue(ele)));
			}

			ele = (Element) root.getElementsByTagName("SupportersPopularity").item(0);
			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				map.put("SupportersPopularity",(XMLManager.getFirstChildNodeValue(ele)));
			}

			ele = (Element) root.getElementsByTagName("FanClubSize").item(0);
			map.put("FanClubSize", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("IncomeSpectators").item(0);
			map.put("IncomeSpectators",	(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("IncomeSponsors").item(0);
			map.put("IncomeSponsors", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("IncomeFinancial").item(0);
			map.put("IncomeFinancial",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("IncomeTemporary").item(0);
			map.put("IncomeTemporary",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("IncomeSoldPlayers").item(0);
			map.put("IncomeSoldPlayers",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("IncomeSoldPlayersCommission").item(0);
			map.put("IncomeSoldPlayersCommission",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("IncomeSum").item(0);
			map.put("IncomeSum", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("CostsArena").item(0);
			map.put("CostsArena", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("CostsPlayers").item(0);
			map.put("CostsPlayers", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("CostsFinancial").item(0);
			map.put("CostsFinancial", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("CostsStaff").item(0);
			map.put("CostsStaff", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("CostsBoughtPlayers").item(0);
			map.put("CostsBoughtPlayers", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("CostsArenaBuilding").item(0);
			map.put("CostsArenaBuilding", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("CostsYouth").item(0);
			map.put("CostsYouth", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("CostsSum").item(0);
			map.put("CostsSum", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("ExpectedWeeksTotal").item(0);
			map.put("ExpectedWeeksTotal",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastIncomeSpectators").item(0);
			map.put("LastIncomeSpectators",	(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastIncomeSponsors").item(0);
			map.put("LastIncomeSponsors",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastIncomeFinancial").item(0);
			map.put("LastIncomeFinancial",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastIncomeSoldPlayers").item(0);
			map.put("LastIncomeSoldPlayers",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastIncomeSoldPlayersCommission").item(0);
			map.put("LastIncomeSoldPlayersCommission",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastIncomeTemporary").item(0);
			map.put("LastIncomeTemporary",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastIncomeSum").item(0);
			map.put("LastIncomeSum", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastCostsArena").item(0);
			map.put("LastCostsArena", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastCostsPlayers").item(0);
			map.put("LastCostsPlayers",	(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastCostsFinancial").item(0);
			map.put("LastCostsFinancial",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastCostsStaff").item(0);
			map.put("LastCostsStaff",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastCostsBoughtPlayers").item(0);
			map.put("LastCostsBoughtPlayers",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastCostsArenaBuilding").item(0);
			map.put("LastCostsArenaBuilding",(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastCostsYouth").item(0);
			map.put("LastCostsYouth", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastCostsSum").item(0);
			map.put("LastCostsSum", (XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) root.getElementsByTagName("LastWeeksTotal").item(0);
			map.put("LastWeeksTotal", (XMLManager.getFirstChildNodeValue(ele)));
		} catch (Exception e) {
			HOLogger.instance().log(XMLEconomyParser.class, e);
		}

		return map;
	}
}
