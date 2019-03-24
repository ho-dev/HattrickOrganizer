// %597232359:de.hattrickorganizer.logik.xml%
/*
 * XMLMatchLineupParser.java
 *
 * Created on 20. Oktober 2003, 08:08
 */
package core.file.xml;

import core.model.match.MatchLineup;
import core.model.match.MatchLineupPlayer;
import core.model.match.MatchLineupTeam;
import core.model.match.MatchType;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.util.HOLogger;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author thomas.werth
 */
public class XMLMatchLineupParser {
	
	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLMatchLineupParser() {
	}

	public static MatchLineup parseMatchLineupFromString(String inputStream) {
		return createLineup(XMLManager.parseString(inputStream));
	}

	private static MatchLineup createLineup(Document doc) {
		MatchLineup ml = new MatchLineup();
		if (doc == null) {
			return ml;
		}

		try {
			Element root = doc.getDocumentElement();
			Element  ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
			ml.setFetchDatum(ele.getFirstChild().getNodeValue());
			ele = (Element) root.getElementsByTagName("MatchID").item(0);
			ml.setMatchID(Integer.parseInt(ele.getFirstChild().getNodeValue()));
			ele = (Element) root.getElementsByTagName("HomeTeam").item(0);
			ml.setHeimId(Integer.parseInt(ele.getElementsByTagName("HomeTeamID").item(0)
					.getFirstChild().getNodeValue()));
			ml.setHeimName(ele.getElementsByTagName("HomeTeamName").item(0).getFirstChild()
					.getNodeValue());
			ele = (Element) root.getElementsByTagName("AwayTeam").item(0);
			ml.setGastId(Integer.parseInt(ele.getElementsByTagName("AwayTeamID").item(0)
					.getFirstChild().getNodeValue()));
			ml.setGastName(ele.getElementsByTagName("AwayTeamName").item(0).getFirstChild()
					.getNodeValue());
			ele = (Element) root.getElementsByTagName("MatchType").item(0);
			ml.setMatchTyp(MatchType.getById(Integer.parseInt(ele.getFirstChild().getNodeValue())));

			if ((ml.getMatchTyp() != MatchType.TOURNAMENTGROUP)
					&& (ml.getMatchTyp() != MatchType.TOURNAMENTPLAYOFF)
					&& (ml.getMatchTyp() != MatchType.NONE)) { // HT bug
				ele = (Element) root.getElementsByTagName("Arena").item(0);
				ml.setArenaID(Integer.parseInt(ele.getElementsByTagName("ArenaID").item(0)
						.getFirstChild().getNodeValue()));
				ml.setArenaName(ele.getElementsByTagName("ArenaName").item(0).getFirstChild()
						.getNodeValue());
			}

			ele = (Element) root.getElementsByTagName("MatchDate").item(0);
			ml.setSpielDatum(ele.getFirstChild().getNodeValue());

			// team adden
			MatchLineupTeam team = createTeam((Element) root.getElementsByTagName("Team").item(0));

			if (team.getTeamID() == ml.getHeimId()) {
				ml.setHeim(team);
			} else {
				ml.setGast(team);
			}
		} catch (Exception e) {
			HOLogger.instance().log(XMLMatchLineupParser.class, e);
			ml = null;
		}

		return ml;
	}

	private static MatchLineupPlayer createPlayer(Element ele) {
		int roleID = -1;
		int behavior = 0;
		double rating = -1.0d;
		double ratingStarsEndOfMatch = -1.0d;
		String name = "";

		Element tmp = (Element) ele.getElementsByTagName("PlayerID").item(0);
		int spielerID = Integer.parseInt(tmp.getFirstChild().getNodeValue());
		tmp = (Element) ele.getElementsByTagName("RoleID").item(0);
		if (tmp != null) {
			roleID = Integer.parseInt(tmp.getFirstChild().getNodeValue());
		}

		// This is the right spot to wash the old role IDs if arrived by xml.
		// Position code is not include in 1.6 xml. It is not needed from the
		// older ones, what is necessary is to check for old reposition values in the
		// Behaviour.
		// We do move all repositions to central slot, and go happily belly up
		// if we find more than one repositioning to the same position 
		// (old setup where more than 3 forwards was possible)

		// if (roleID == 17 || roleID == 14) {
		// System.out.println("Give me somewhere to put a breakpoint");
		// }

		// HOLogger.instance().debug(getClass(),"RoleID in: " + roleID);

		// nur wenn Player existiert
		if (spielerID > 0) {
			tmp = (Element) ele.getElementsByTagName("FirstName").item(0);
			name = tmp.getFirstChild().getNodeValue();
			tmp = (Element) ele.getElementsByTagName("LastName").item(0);
			if (tmp.getFirstChild() != null) { // there are players without a lastname
				name = name + " " + tmp.getFirstChild().getNodeValue();
			}

			// tactic is only set for those in the lineup (and not for the keeper).
			if (roleID == IMatchRoleID.keeper || IMatchRoleID.oldKeeper.contains(roleID)) {
				// Diese Werte sind von HT vorgegeben aber nicht garantiert
				// mitgeliefert in xml, daher selbst setzen!
				behavior = 0;
				roleID = IMatchRoleID.keeper; // takes care of the old
													// keeper ID.
			} else if ((roleID >= 0)
					&& (roleID < IMatchRoleID.setPieces)
					|| ((roleID < IMatchRoleID.startReserves) && (roleID > IMatchRoleID.keeper))) {
				tmp = (Element) ele.getElementsByTagName("Behaviour").item(0);
				behavior = Integer.parseInt(tmp.getFirstChild().getNodeValue());

				// HOLogger.instance().debug(getClass(),"Behavior found: " +
				// behavior);

				switch (behavior) {
				case IMatchRoleID.OLD_EXTRA_DEFENDER:
					roleID = IMatchRoleID.middleCentralDefender;
					behavior = IMatchRoleID.NORMAL;
					break;
				case IMatchRoleID.OLD_EXTRA_MIDFIELD:
					roleID = IMatchRoleID.centralInnerMidfield;
					behavior = IMatchRoleID.NORMAL;
					break;
				case IMatchRoleID.OLD_EXTRA_FORWARD:
					roleID = IMatchRoleID.centralForward;
					behavior = IMatchRoleID.NORMAL;
					break;
				case IMatchRoleID.OLD_EXTRA_DEFENSIVE_FORWARD:
					roleID = IMatchRoleID.centralForward;
					behavior = IMatchRoleID.DEFENSIVE;
				}

				// Wash the remaining old positions
				if (roleID < IMatchRoleID.setPieces) {
					roleID = MatchRoleID.convertOldRoleToNew(roleID);
				}
			}

			// rating nur für leute die gespielt haben
			if ((roleID >= IMatchRoleID.startLineup)
					&& (roleID < IMatchRoleID.startReserves)
					|| ((roleID >= IMatchRoleID.FirstPlayerReplaced) && (roleID <= IMatchRoleID.ThirdPlayerReplaced))) {
				tmp = (Element) ele.getElementsByTagName("RatingStars").item(0);
				rating = Double
						.parseDouble(tmp.getFirstChild().getNodeValue().replaceAll(",", "."));
				tmp = (Element) ele.getElementsByTagName("RatingStarsEndOfMatch").item(0);
				ratingStarsEndOfMatch = Double.parseDouble(tmp.getFirstChild().getNodeValue()
						.replaceAll(",", "."));

			}
		}

		// HOLogger.instance().debug(getClass(),"RoleID out: " + roleID);
		// HOLogger.instance().debug(getClass(),"Behavior out: " + behavior);
		// HOLogger.instance().debug(getClass(),"--------------- Debug by XMLMatchLineupParse if you want it gone");

		MatchLineupPlayer player = new MatchLineupPlayer(roleID, behavior, spielerID, rating, name, 0);
		player.setRatingStarsEndOfMatch(ratingStarsEndOfMatch);
		return player;
	}


	private static MatchLineupTeam createTeam(Element ele) {
		Element tmp = (Element) ele.getElementsByTagName("TeamID").item(0);
		int teamId = Integer.parseInt(tmp.getFirstChild().getNodeValue());
		tmp = (Element) ele.getElementsByTagName("ExperienceLevel").item(0);
		int erfahrung = Integer.parseInt(tmp.getFirstChild().getNodeValue());
		tmp = (Element) ele.getElementsByTagName("StyleOfPlay").item(0);
		int styleOfPlay = Integer.parseInt(tmp.getFirstChild().getNodeValue());
		tmp = (Element) ele.getElementsByTagName("TeamName").item(0);
		String teamName = tmp.getFirstChild().getNodeValue();
		MatchLineupTeam team = new MatchLineupTeam(teamName, teamId, erfahrung, styleOfPlay);

		Element starting = (Element) ele.getElementsByTagName("StartingLineup").item(0);
		Element subs = (Element) ele.getElementsByTagName("Substitutions").item(0);

		tmp = (Element) ele.getElementsByTagName("Lineup").item(0);

		// The normal end of match report
		// Einträge adden
		NodeList list = tmp.getElementsByTagName("Player");

		for (int i = 0; (list != null) && (i < list.getLength()); i++) {

			// We want to stop an api error that has repositioned players as
			// substituted.
			// They are both shown as substituted and in a position. (hopefully)
			// substituted
			// players are always last in the API, there are at least signs of a
			// fixed order.
			MatchLineupPlayer player = createPlayer((Element) list.item(i));
			if (team.getPlayerByID(player.getSpielerId()) != null) {
				if ((player.getId() >= IMatchRoleID.FirstPlayerReplaced)
						&& (player.getId() <= IMatchRoleID.ThirdPlayerReplaced)) {

					// MatchLineup API bug, he is still on the pitch, so skip
					continue;
				}
			}

			team.add2Aufstellung(player);
		}

		// The starting lineup
		list = starting.getElementsByTagName("Player");

		for (int i = 0; (list != null) && (i < list.getLength()); i++) {
			MatchLineupPlayer startPlayer = createStartPlayer((Element) list.item(i));

			// Merge with the existing player, but ignore captain and set piece
			// position
			if (startPlayer.getStartPosition() >= IMatchRoleID.startLineup) {
				MatchLineupPlayer lineupPlayer = (MatchLineupPlayer) team.getPlayerByID(startPlayer
						.getSpielerId());
				if (lineupPlayer != null) {
					lineupPlayer.setStartPosition(startPlayer.getStartPosition());
					lineupPlayer.setStartBehavior(startPlayer.getStartBehavior());
				} else {
					// He was not already in the lineup, so add him
					team.add2Aufstellung(startPlayer);
				}
			}
		}

		// Substitutions

		list = subs.getElementsByTagName("Substitution");
		List<Substitution> substitutions = new ArrayList<Substitution>();

		for (int i = 0; (list != null) && (i < list.getLength()); i++) {

			Substitution s = createSubstitution((Element) list.item(i), i);
			substitutions.add(s);
			// We need to make sure the players involved are in the team lineup
			// If missing, we only know the ID
			if ((s.getObjectPlayerID() > 0) && (team.getPlayerByID(s.getObjectPlayerID()) == null)) {
				team.add2Aufstellung(new MatchLineupPlayer(-1, -1, s.getObjectPlayerID(), -1d, "",
						-1));
			}
			if ((s.getSubjectPlayerID() > 0)
					&& (team.getPlayerByID(s.getSubjectPlayerID()) == null)) {
				team.add2Aufstellung(new MatchLineupPlayer(-1, -1, s.getSubjectPlayerID(), -1d, "",
						-1));
			}
		}
		team.setSubstitutions(substitutions);

		return team;
	}

	private static Substitution createSubstitution(Element ele, int num) {

		int playerOrderID = num; // We use our own
		int playerIn = -1;
		int playerOut = -1;
		byte orderTypeId = -1;
		byte matchMinuteCriteria = -1;
		byte pos = -1;
		byte behaviour = -1;
		byte card = -1;
		byte standing = -1;

		Element tmp = (Element) ele.getElementsByTagName("MatchMinute").item(0);
		if (tmp != null) {
			matchMinuteCriteria = Byte.parseByte(XMLManager.getFirstChildNodeValue(tmp));
		}

		tmp = (Element) ele.getElementsByTagName("GoalDiffCriteria").item(0);
		if (tmp != null) {
			standing = Byte.parseByte(XMLManager.getFirstChildNodeValue(tmp));
		}
		tmp = (Element) ele.getElementsByTagName("RedCardCriteria").item(0);
		if (tmp != null) {
			card = Byte.parseByte(XMLManager.getFirstChildNodeValue(tmp));
		}
		tmp = (Element) ele.getElementsByTagName("SubjectPlayerID").item(0);
		if (tmp != null) {
			playerOut = Integer.parseInt(XMLManager.getFirstChildNodeValue(tmp));
		}
		tmp = (Element) ele.getElementsByTagName("ObjectPlayerID").item(0);
		if (tmp != null) {
			playerIn = Integer.parseInt(XMLManager.getFirstChildNodeValue(tmp));
		}

		tmp = (Element) ele.getElementsByTagName("OrderType").item(0);
		if (tmp != null) {
			orderTypeId = Byte.parseByte(XMLManager.getFirstChildNodeValue(tmp));
		}
		tmp = (Element) ele.getElementsByTagName("NewPositionId").item(0);
		if (tmp != null) {
			pos = Byte.parseByte(XMLManager.getFirstChildNodeValue(tmp));
		}
		tmp = (Element) ele.getElementsByTagName("NewPositionBehaviour").item(0);
		if (tmp != null) {
			behaviour = Byte.parseByte(XMLManager.getFirstChildNodeValue(tmp));
		}

		MatchOrderType matchOrderType;
		if (orderTypeId == 3) {
			matchOrderType = MatchOrderType.POSITION_SWAP;
		} else {
			if (playerIn == playerOut) {
				matchOrderType = MatchOrderType.NEW_BEHAVIOUR;
			} else {
				matchOrderType = MatchOrderType.SUBSTITUTION;
			}
		}
		return new Substitution(playerOrderID, playerIn, playerOut, matchOrderType,
				matchMinuteCriteria, pos, behaviour, RedCardCriteria.getById(card),
				GoalDiffCriteria.getById(standing));
	}

	private static MatchLineupPlayer createStartPlayer(Element ele) {
		MatchLineupPlayer player = null;
		int roleID = -1;
		int behavior = 0;
		String name = "";

		Element tmp = (Element) ele.getElementsByTagName("PlayerID").item(0);
		int spielerID = Integer.parseInt(tmp.getFirstChild().getNodeValue());
		tmp = (Element) ele.getElementsByTagName("RoleID").item(0);
		if (tmp != null) {
			roleID = Integer.parseInt(tmp.getFirstChild().getNodeValue());
		}

		// nur wenn Player existiert
		if (spielerID > 0) {
			tmp = (Element) ele.getElementsByTagName("FirstName").item(0);
			name = tmp.getFirstChild().getNodeValue();
			tmp = (Element) ele.getElementsByTagName("LastName").item(0);
			if (tmp.getFirstChild() != null) { // there are players without a lastname
				name = name + " " + tmp.getFirstChild().getNodeValue();
			}

			// tactic is only set for those in the lineup (and not for the
			// keeper).
			if (roleID == IMatchRoleID.keeper) {
				// Diese Werte sind von HT vorgegeben aber nicht garantiert
				// mitgeliefert in xml, daher selbst setzen!
				behavior = 0;

			} else if ((roleID < IMatchRoleID.startReserves)
					&& (roleID > IMatchRoleID.keeper)) {
				tmp = (Element) ele.getElementsByTagName("Behaviour").item(0);
				behavior = Integer.parseInt(tmp.getFirstChild().getNodeValue());

			}
		}

		player = new MatchLineupPlayer(-1, -1, spielerID, 0, name, 0);
		player.setStartBehavior(behavior);
		player.setStartPosition(roleID);
		return player;
	}
}
