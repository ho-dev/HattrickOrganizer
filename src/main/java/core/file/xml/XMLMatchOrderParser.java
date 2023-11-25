/*
 * xmlMatchOrderParser.java
 *
 * Created on 14. Juni 2004, 18:18
 */
package core.file.xml;

import core.model.player.IMatchRoleID;
import core.util.HOLogger;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parser for the matchorders.
 * 
 * @author TheTom
 */
public class XMLMatchOrderParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLMatchOrderParser() {
	}

	public static Map<String, String> parseMatchOrderFromString(String xmlData) {
		return parseDetails(XMLManager.parseString(xmlData));
	}

	/**
	 * Create a player from the given XML.
	 */
	private static void addPlayer(Element ele, Map<String, String> map) {
		Element tmp;
		int roleID = -1;
		String behavior = "-1";
		String name;

		tmp = (Element) ele.getElementsByTagName("PlayerID").item(0);
		String spielerID = XMLManager.getFirstChildNodeValue(tmp);

		if (spielerID.trim().isEmpty()) {spielerID = "-1";}

		tmp = (Element) ele.getElementsByTagName("RoleID").item(0);
		if (tmp != null) {
			roleID = Integer.parseInt(XMLManager.getFirstChildNodeValue(tmp));
		}
		else if (ele.getTagName().equalsIgnoreCase("SetPieces"))
		{
			roleID = IMatchRoleID.setPieces;
		}
		else if (ele.getTagName().equalsIgnoreCase("Captain"))
		{
			roleID = IMatchRoleID.captain;
		}

		tmp = (Element) ele.getElementsByTagName("LastName").item(0);
		name = XMLManager.getFirstChildNodeValue(tmp);

		// individual orders only for the 10 players in the lineup (i.e. starting11 excluding keeper)
		if((roleID != IMatchRoleID.keeper)  && (IMatchRoleID.aFieldMatchRoleID.contains(roleID))) {
			tmp = (Element) ele.getElementsByTagName("Behaviour").item(0);
			behavior = XMLManager.getFirstChildNodeValue(tmp);
		}

		switch (roleID) {
			case IMatchRoleID.keeper -> {
				map.put("KeeperID", spielerID);
				map.put("KeeperName", name);
				map.put("KeeperOrder", "0");
			}
			case IMatchRoleID.rightBack -> {
				map.put("RightBackID", spielerID);
				map.put("RightBackName", name);
				map.put("RightBackOrder", behavior);
			}
			case IMatchRoleID.rightCentralDefender -> {
				if (!map.containsKey("RightCentralDefenderID")) {
					map.put("RightCentralDefenderID", spielerID);
					map.put("RightCentralDefenderName", name);
					map.put("RightCentralDefenderOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.middleCentralDefender -> {
				if (!map.containsKey("MiddleCentralDefenderID")) {
					map.put("MiddleCentralDefenderID", spielerID);
					map.put("MiddleCentralDefenderName", name);
					map.put("MiddleCentralDefenderOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.leftCentralDefender -> {
				map.put("LeftCentralDefenderID", spielerID);
				map.put("LeftCentralDefenderName", name);
				map.put("LeftCentralDefenderOrder", behavior);
			}
			case IMatchRoleID.leftBack -> {
				map.put("LeftBackID", spielerID);
				map.put("LeftBackName", name);
				map.put("LeftBackOrder", behavior);
			}
			case IMatchRoleID.leftWinger -> {
				if (!map.containsKey("LeftWingerID")) {
					map.put("LeftWingerID", spielerID);
					map.put("LeftWingerName", name);
					map.put("LeftWingerOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.leftInnerMidfield -> {
				if (!map.containsKey("LeftInnerMidfieldID")) {
					map.put("LeftInnerMidfieldID", spielerID);
					map.put("LeftInnerMidfieldName", name);
					map.put("LeftInnerMidfieldOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.centralInnerMidfield -> {
				if (!map.containsKey("CentralInnerMidfieldID")) {
					map.put("CentralInnerMidfieldID", spielerID);
					map.put("CentralInnerMidfieldName", name);
					map.put("CentralInnerMidfieldOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.rightInnerMidfield -> {
				map.put("RightInnerMidfieldID", spielerID);
				map.put("RightInnerMidfieldName", name);
				map.put("RightInnerMidfieldOrder", behavior);
			}
			case IMatchRoleID.rightWinger -> {
				if (!map.containsKey("RightWingerID")) {
					map.put("RightWingerID", spielerID);
					map.put("RightWingerName", name);
					map.put("RightWingerOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.rightForward -> {
				if (!map.containsKey("RightForward")) {
					map.put("RightForwardID", spielerID);
					map.put("RightForwardName", name);
					map.put("RightForwardOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.centralForward -> {
				if (!map.containsKey("CentralForward")) {
					map.put("CentralForwardID", spielerID);
					map.put("CentralForwardName", name);
					map.put("CentralForwardOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.leftForward -> {
				if (!map.containsKey("LeftForward")) {
					map.put("LeftForwardID", spielerID);
					map.put("LeftForwardName", name);
					map.put("LeftForwardOrder", behavior);
				} else {
					addAdditionalPlayer(map, roleID, spielerID, name, behavior);
				}
			}
			case IMatchRoleID.substGK1 -> {
				map.put("substGK1ID", spielerID);
				map.put("substGK1Name", name);
			}
			case IMatchRoleID.substGK2 -> {
				map.put("substGK2ID", spielerID);
				map.put("substGK2Name", name);
			}
			case IMatchRoleID.substCD1 -> {
				map.put("substCD1ID", spielerID);
				map.put("substCD1Name", name);
			}
			case IMatchRoleID.substCD2 -> {
				map.put("substCD2ID", spielerID);
				map.put("substCD2Name", name);
			}
			case IMatchRoleID.substWB1 -> {
				map.put("substWB1ID", spielerID);
				map.put("substWB1Name", name);
			}
			case IMatchRoleID.substWB2 -> {
				map.put("substWB2ID", spielerID);
				map.put("substWB2Name", name);
			}
			case IMatchRoleID.substIM1 -> {
				map.put("substIM1ID", spielerID);
				map.put("substIM1Name", name);
			}
			case IMatchRoleID.substIM2 -> {
				map.put("substIM2ID", spielerID);
				map.put("substIM2Name", name);
			}
			case IMatchRoleID.substWI1 -> {
				map.put("substWI1ID", spielerID);
				map.put("substWI1Name", name);
			}
			case IMatchRoleID.substWI2 -> {
				map.put("substWI2ID", spielerID);
				map.put("substWI2Name", name);
			}
			case IMatchRoleID.substFW1 -> {
				map.put("substFW1ID", spielerID);
				map.put("substFW1Name", name);
			}
			case IMatchRoleID.substFW2 -> {
				map.put("substFW2ID", spielerID);
				map.put("substFW2Name", name);
			}
			case IMatchRoleID.substXT1 -> {
				map.put("substXT1ID", spielerID);
				map.put("substXT1Name", name);
			}
			case IMatchRoleID.substXT2 -> {
				map.put("substXT2ID", spielerID);
				map.put("substXT2Name", name);
			}
			case IMatchRoleID.setPieces -> {
				map.put("KickerID", spielerID);
				map.put("KickerName", name);
			}
			case IMatchRoleID.captain -> {
				map.put("CaptainID", spielerID);
				map.put("CaptainName", name);
			}
		}
		
		// Penalty positions
		for (int i = IMatchRoleID.penaltyTaker1; i <= IMatchRoleID.penaltyTaker11 ; i++) {
			if (roleID == i) {
				map.put("PenaltyTaker" + (i - IMatchRoleID.penaltyTaker1 ), spielerID);
			}
		}
	}

	private static void addAdditionalPlayer(Map<String, String> map, int roleID, String spielerID,
			String name, String behavior) {
		String key = "Additional1";
		if (!map.containsKey(key + "ID")) {
			map.put(key + "ID", spielerID);
			map.put(key + "Role", String.valueOf(roleID));
			map.put(key + "Name", name);
			map.put(key + "Behaviour", behavior);
			return;
		}
		key = "Additional2";
		if (!map.containsKey(key + "ID")) {
			map.put(key + "ID", spielerID);
			map.put(key + "Role", String.valueOf(roleID));
			map.put(key + "Name", name);
			map.put(key + "Behaviour", behavior);
			return;
		}
		key = "Additional3";
		if (!map.containsKey(key + "ID")) {
			map.put(key + "ID", spielerID);
			map.put(key + "Role", String.valueOf(roleID));
			map.put(key + "Name", name);
			map.put(key + "Behaviour", behavior);
			return;
		}
		key = "Additional4";
		if (!map.containsKey(key + "ID")) {
			map.put(key + "ID", spielerID);
			map.put(key + "Role", String.valueOf(roleID));
			map.put(key + "Name", name);
			map.put(key + "Behaviour", behavior);
		}
		// max. 4 additional/repositioned players in the new lineup?
	}

	private static void addPlayerOrder(Element ele, Map<String, String> map, int num) {
		Element tmp;
		String playerOrderID = "" + num;
		String playerIn = "-1";
		String playerOut = "-1";
		String orderType = "-1";
		String minute = "-1";
		String matchMinuteCriteria = "-1";
		String pos = "-1";
		String behaviour = "-1";
		String card = "-1";
		String standing = "-1";

		tmp = (Element) ele.getElementsByTagName("MatchMinuteCriteria").item(0);
		if (tmp != null) {
			matchMinuteCriteria = XMLManager.getFirstChildNodeValue(tmp);
		}

		tmp = (Element) ele.getElementsByTagName("GoalDiffCriteria").item(0);
		if (tmp != null) {
			standing = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("RedCardCriteria").item(0);
		if (tmp != null) {
			card = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("SubjectPlayerID").item(0);
		if (tmp != null) {
			playerOut = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("ObjectPlayerID").item(0);
		if (tmp != null) {
			playerIn = XMLManager.getFirstChildNodeValue(tmp);
		}

		tmp = (Element) ele.getElementsByTagName("OrderType").item(0);
		if (tmp != null) {
			orderType = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("NewPositionId").item(0);
		if (tmp != null) {
			pos = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("NewPositionBehaviour").item(0);
		if (tmp != null) {
			behaviour = XMLManager.getFirstChildNodeValue(tmp);
		}

		map.put("subst" + num + "playerOrderID", playerOrderID);
		map.put("subst" + num + "playerIn", playerIn);
		map.put("subst" + num + "playerOut", playerOut);
		map.put("subst" + num + "orderType", orderType);
		map.put("subst" + num + "minute", minute);
		map.put("subst" + num + "matchMinuteCriteria", matchMinuteCriteria);
		map.put("subst" + num + "pos", pos);
		map.put("subst" + num + "behaviour", behaviour);
		map.put("subst" + num + "card", card);
		map.put("subst" + num + "standing", standing);

	}

	/**
	 * Creates the Matchlineup object
	 * parsing of xml adapted to version 3.0 of match orders
	 */
	private static HashMap<String, String> parseDetails(Document doc) {
		Element ele;
		Element root;
		final SafeInsertMap hash = new SafeInsertMap();
		NodeList list;

		if (doc == null) {
			return hash;
		}

		root = doc.getDocumentElement();

		try {
			ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
			hash.put("FetchedDate", XMLManager.getFirstChildNodeValue(ele));
			ele = (Element) root.getElementsByTagName("MatchID").item(0);
			hash.put("MatchID", XMLManager.getFirstChildNodeValue(ele));

			// change root to Match data
			root = (Element) root.getElementsByTagName("MatchData").item(0);

			if (!XMLManager.getAttributeValue(root, "Available").trim().equalsIgnoreCase("true")) {
				return hash;
			}

			ele = (Element) root.getElementsByTagName("Attitude").item(0);

			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				hash.put("Attitude", XMLManager.getFirstChildNodeValue(ele));
			} else {
				// in case attitude is not available TODO: check if this is really mandatory
				hash.put("Attitude", "0");
			}
			
			ele = (Element) root.getElementsByTagName("CoachModifier").item(0);
			
			// 'coachModifier' is called 'styleOfPlay' in HT
			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				hash.put("StyleOfPlay", XMLManager.getFirstChildNodeValue(ele));
			} else {
				hash.put("StyleOfPlay", "0");
			}
			

			ele = (Element) root.getElementsByTagName("HomeTeamID").item(0);
			hash.put("HomeTeamID", XMLManager.getFirstChildNodeValue(ele));
			ele = (Element) root.getElementsByTagName("HomeTeamName").item(0);
			hash.put("HomeTeamName", XMLManager.getFirstChildNodeValue(ele));
			ele = (Element) root.getElementsByTagName("AwayTeamID").item(0);
			hash.put("AwayTeamID", XMLManager.getFirstChildNodeValue(ele));
			ele = (Element) root.getElementsByTagName("AwayTeamName").item(0);
			hash.put("AwayTeamName", XMLManager.getFirstChildNodeValue(ele));
			ele = (Element) root.getElementsByTagName("MatchDate").item(0);
			hash.put("MatchDate", XMLManager.getFirstChildNodeValue(ele));
			ele = (Element) root.getElementsByTagName("MatchType").item(0);
			hash.put("MatchType", XMLManager.getFirstChildNodeValue(ele));
			ele = (Element) root.getElementsByTagName("ArenaID").item(0);
			hash.put("ArenaID", XMLManager.getFirstChildNodeValue(ele));
			ele = (Element) root.getElementsByTagName("ArenaName").item(0);
			hash.put("ArenaName", XMLManager.getFirstChildNodeValue(ele));
			
			ele = (Element) root.getElementsByTagName("TacticType").item(0);

			hash.put("TacticType", XMLManager.getFirstChildNodeValue(ele));

			// Treatment of Players in starting Lineup
			Element Positions = (Element) doc.getElementsByTagName("Positions").item(0);
			list = Positions.getElementsByTagName("Player");
			for (int i = 0; i < list.getLength(); i++) {
				addPlayer((Element) list.item(i), hash);
			}

			// Treatment of Players on the bench
			Element Bench = (Element) doc.getElementsByTagName("Bench").item(0);
			list = Bench.getElementsByTagName("Player");
			for (int i = 0; i < list.getLength(); i++) {
				addPlayer((Element) list.item(i), hash);
			}

			// Treatment of Penalty Takers
			Element Kickers = (Element) doc.getElementsByTagName("Kickers").item(0);
			list = Kickers.getElementsByTagName("Player");
			for (int i = 0; i < list.getLength(); i++) {
				addPlayer((Element) list.item(i), hash);
			}

			// Treatment of SP taker and Captain
			Element SetPieces = (Element) doc.getElementsByTagName("SetPieces").item(0);
			if (SetPieces != null) {
				addPlayer(SetPieces, hash);
			}

			Element Captain = (Element) doc.getElementsByTagName("Captain").item(0);
			if (Captain != null) {
				addPlayer(Captain, hash);
			}

			// Treatment of Players Orders
			Element child = (Element) root.getElementsByTagName("PlayerOrders").item(0);

			list = child.getElementsByTagName("PlayerOrder");
			for (int i = 0; i < list.getLength(); i++) {
				addPlayerOrder((Element) list.item(i), hash, i);
			}

		} catch (Exception e) {
			HOLogger.instance().log(XMLMatchOrderParser.class,
					"XMLMatchOrderParser.parseDetails Exception gefangen: " + e);
			HOLogger.instance().log(XMLMatchOrderParser.class, e);
		}

		return hash;
	}
}
