package core.file.xml


import core.util.HODateTime
import core.util.HOLogger

import org.w3c.dom.Document
import org.w3c.dom.Element

object XMLEconomyParser {
	
	fun parseEconomyFromString(inputStream: String): Map<String, String> {
		val mapEconomy = parseDetails(XMLManager.parseString(inputStream))
		val economyDetails = mutableMapOf<String, String>()
		economyDetails.putAll(mapEconomy)

		// Manually add SponsorsBonus which is missing in version <= 1.3
		val fetchedDate = HODateTime.fromHT(mapEconomy["FetchedDate"])
		val htWeek = fetchedDate.toHTWeek()
		val season = htWeek.season
		if (season >= 80){
			val iSponsorBonusIncome = assessSponsorBonusIncome(mapEconomy, arrayOf(
				"IncomeSpectators", "IncomeSponsors", "IncomeFinancial", "IncomeTemporary", "IncomeSoldPlayers",
					"IncomeSoldPlayersCommission"), "IncomeSum")
			economyDetails["IncomeSponsorsBonus"] = iSponsorBonusIncome.toString()
			val iLastSponsorBonusIncome = assessSponsorBonusIncome(mapEconomy, arrayOf(
				"LastIncomeSpectators", "LastIncomeSponsors", "LastIncomeFinancial", "LastIncomeTemporary", "LastIncomeSoldPlayers",
					"LastIncomeSoldPlayersCommission"), "LastIncomeSum")
			economyDetails["LastIncomeSponsorsBonus"] = iLastSponsorBonusIncome.toString()
		}
		return economyDetails
	}

	/**
	 * A method to fix the issue that as of January 2022, the economy CHPP file is still
	 * in version 1.3 and does not include yet the newly introduced (season 80) sponsors bonus information
	 * This method will derive the bonus information by spotting the mismatch between the total income and the individual elements
	 *
	 * @return the assessed sponsor bonus income
	 */
	private fun assessSponsorBonusIncome(mapEconomy: Map<String, String>, incomeSources: Array<String>, totalIncomeSources: String): Int {
		var calculatedTotalIncome = 0
		for (incomeSource in incomeSources) {
			calculatedTotalIncome += Integer.parseInt(mapEconomy[incomeSource])
		}

		return Integer.parseInt(mapEconomy.get(totalIncomeSources)) - calculatedTotalIncome
	}

	private fun parseDetails(doc: Document?): Map<String, String> {
		val map = SafeInsertMap()

		if (doc == null) {
			return map
		}

		try {
			var root = doc.documentElement
			var ele = root.getElementsByTagName("FetchedDate").item(0) as Element
			map.insert("FetchedDate", (XMLManager.getFirstChildNodeValue(ele)))

			// change root => Team node
			root = root.getElementsByTagName("Team").item(0) as Element

			ele = root.getElementsByTagName("TeamID").item(0) as Element
			map.insert("TeamID", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("TeamName").item(0) as Element
			map.insert("TeamName", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("Cash").item(0) as Element
			map.insert("Cash", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("ExpectedCash").item(0) as Element
			map.insert("ExpectedCash", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("SponsorsPopularity").item(0) as Element
			if (XMLManager.getAttributeValue(ele, "Available").trim().equals("true", true)) {
				map.insert("SponsorsPopularity",(XMLManager.getFirstChildNodeValue(ele)))
			}

			ele = root.getElementsByTagName("SupportersPopularity").item(0) as Element
			if (XMLManager.getAttributeValue(ele, "Available").trim().equals("true", true)) {
				map.insert("SupportersPopularity",(XMLManager.getFirstChildNodeValue(ele)))
			}

			ele = root.getElementsByTagName("FanClubSize").item(0) as Element
			map.insert("FanClubSize", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("IncomeSpectators").item(0) as Element
			map.insert("IncomeSpectators",	(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("IncomeSponsors").item(0) as Element
			map.insert("IncomeSponsors", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("IncomeFinancial").item(0) as Element
			map.insert("IncomeFinancial",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("IncomeTemporary").item(0) as Element
			map.insert("IncomeTemporary",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("IncomeSoldPlayers").item(0) as Element
			map.insert("IncomeSoldPlayers",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("IncomeSoldPlayersCommission").item(0) as Element
			map.insert("IncomeSoldPlayersCommission",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("IncomeSum").item(0) as Element
			map.insert("IncomeSum", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("CostsArena").item(0) as Element
			map.insert("CostsArena", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("CostsPlayers").item(0) as Element
			map.insert("CostsPlayers", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("CostsFinancial").item(0) as Element
			map.insert("CostsFinancial", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("CostsStaff").item(0) as Element
			map.insert("CostsStaff", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("CostsBoughtPlayers").item(0) as Element
			map.insert("CostsBoughtPlayers", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("CostsArenaBuilding").item(0) as Element
			map.insert("CostsArenaBuilding", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("CostsYouth").item(0) as Element
			map.insert("CostsYouth", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("CostsSum").item(0) as Element
			map.insert("CostsSum", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("ExpectedWeeksTotal").item(0) as Element
			map.insert("ExpectedWeeksTotal",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastIncomeSpectators").item(0) as Element
			map.insert("LastIncomeSpectators",	(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastIncomeSponsors").item(0) as Element
			map.insert("LastIncomeSponsors",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastIncomeFinancial").item(0) as Element
			map.insert("LastIncomeFinancial",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastIncomeSoldPlayers").item(0) as Element
			map.insert("LastIncomeSoldPlayers",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastIncomeSoldPlayersCommission").item(0) as Element
			map.insert("LastIncomeSoldPlayersCommission",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastIncomeTemporary").item(0) as Element
			map.insert("LastIncomeTemporary",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastIncomeSum").item(0) as Element
			map.insert("LastIncomeSum", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastCostsArena").item(0) as Element
			map.insert("LastCostsArena", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastCostsPlayers").item(0) as Element
			map.insert("LastCostsPlayers",	(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastCostsFinancial").item(0) as Element
			map.insert("LastCostsFinancial",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastCostsStaff").item(0) as Element
			map.insert("LastCostsStaff",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastCostsBoughtPlayers").item(0) as Element
			map.insert("LastCostsBoughtPlayers",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastCostsArenaBuilding").item(0) as Element
			map.insert("LastCostsArenaBuilding",(XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastCostsYouth").item(0) as Element
			map.insert("LastCostsYouth", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastCostsSum").item(0) as Element
			map.insert("LastCostsSum", (XMLManager.getFirstChildNodeValue(ele)))

			ele = root.getElementsByTagName("LastWeeksTotal").item(0) as Element
			map.insert("LastWeeksTotal", (XMLManager.getFirstChildNodeValue(ele)))
		} catch (e: Exception) {
			HOLogger.instance().log(XMLEconomyParser.javaClass, e)
		}

		return map
	}
}
