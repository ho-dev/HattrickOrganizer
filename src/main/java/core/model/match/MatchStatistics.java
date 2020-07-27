package core.model.match;

import core.db.DBManager;
import core.model.player.MatchRoleID;
import core.util.HOLogger;
import module.lineup.substitution.model.Substitution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static core.model.match.MatchEvent.MatchEventID;

public class MatchStatistics {

	private MatchLineupTeam teamLineup;
	private MatchKurzInfo match;

	public MatchStatistics(MatchKurzInfo match, MatchLineupTeam team) {
		this.match = match;
		teamLineup = team;

		if (isOldie()) {
			// Old match with no start lineup. Set start position like end
			// position for all players.
			Vector<MatchLineupPlayer> mlps = teamLineup.getAufstellung();
			for (MatchLineupPlayer mlp : mlps) {
				mlp.setStartPosition(mlp.getFieldPos());
			}
		}
	}

	/**
	 * Returns the minutes a player has played in the specified positions. For
	 * matches with no sub/start info, players get 90 minutes on the position
	 * they ended the match.
	 * 
	 * @param spielerId
	 *            the id of the player
	 * @param accepted
	 *            An array of integers specifying the positions which should be
	 *            accepted.
	 *            If null, all field positions are accepted. (used for experience increase)
	 * @return the number of minutes played in the specified positions.
	 * 			Special handling of walkover matches:
	 * 			 0 minutes is returned in case of walkover matches when all field positions are accepted
	 * 			(experience does not increase in such cases),
	 * 			90 minutes is returned if accepted field positions are given (training does increase)
	 */
	public int getTrainMinutesPlayedInPositions(int spielerId, int[] accepted) {
		if ( accepted!=null && accepted.length==0) return 0;	// NO positions are accepted
		boolean inPosition = false;
		MatchLineupPlayer player = teamLineup.getPlayerByID(spielerId);
		if (player == null) {
			return 0;
		}

		List<Substitution> substitutions = teamLineup.getSubstitutions();
		int enterMin = -1;
		int minPlayed = 0;

		// Those in the starting lineup entered at minute 0
		if (isInAcceptedFieldPositions(player.getStartPosition(), accepted)) {
			if ( match.isWalkoverMatch()) {
				// Opponent team did not appear
				if (accepted != null) {
					// Normal training
					return 90;
				}
				// No experience
				return 0;
			}

			enterMin = 0;
			inPosition = true;
		}

		// The substitutions are sorted on minute. Look for substitutions
		// involving the player, and check his position
		// after the substitution (on the substitution minute). Work through the
		// list, and add minutes depending on
		// entering/leaving the accepted position list.
		for (Substitution substitution : substitutions) {
			if (substitution == null) {
				HOLogger.instance().debug(getClass(),
						"getMinutesPlayedError, null in substitution list");
				break;
			}

			if ((substitution.getObjectPlayerID() == spielerId) || (substitution.getSubjectPlayerID() == spielerId)) {
				int newpos = getPlayerFieldPositionAtMinute(spielerId, substitution.getMatchMinuteCriteria());
				boolean newPosAccepted = isInAcceptedFieldPositions(newpos, accepted);
				if (inPosition && !newPosAccepted) {
					// He left a counting position.
					minPlayed += substitution.getMatchMinuteCriteria() - enterMin;
					inPosition = false;
				} else if (!inPosition && newPosAccepted) {
					// He entered a counting position
					enterMin = substitution.getMatchMinuteCriteria();
					inPosition = true;
				}
			}
		}
		// Done with substitutions, add end if necessary
		if (inPosition) {
			minPlayed += getMatchEndMinute(spielerId) - enterMin;
		}

		return minPlayed;
	}

	// All positions are accepted.
	public int getStaminaMinutesPlayedInPositions(int spielerId) {
		return getTrainMinutesPlayedInPositions(spielerId, null);
	}
	
	private boolean isInAcceptedFieldPositions(int pos, int[] accepted) {
		if ( MatchRoleID.isFieldMatchRoleId(pos) ) {
			if (accepted == null)
				return true; // all positions are accepted, use an empty array if NO position should be accepted

			for (int value : accepted) {
				if (value == pos) {
					return true;
				}
			}
		}
		return false;
	}

	private int getPlayerFieldPostitionAfterSubstitution(int spielerId, int arrIndex,
			List<Substitution> substitutions) {
		// arrIndex should be the index of the sub in the substitution vector.
		// We have 100%
		// trust in our caller (this is a private method), and never verify
		// that.

		// This is the api logic:
		// The sub order contains a position, pos.
		// - Player swap: Pos contains data for the new playerOut position
		// - Normal sub: Pos contains data for the new playerIn position
		// - Red card: Pos contains data (0) for playerOut position
		// PlayerIn is empty
		// - Repositioning: The player is both playerIn and playerOut,
		// pos contains is his new one.

		if (arrIndex < 0) {
			// We have run out of substitutions. Start lineup got answer
			return (teamLineup.getPlayerByID(spielerId)).getStartPosition();
		}

		Substitution tmpSub = substitutions.get(arrIndex);

		if ((tmpSub.getObjectPlayerID() != spielerId) && (tmpSub.getSubjectPlayerID() != spielerId)) {
			// This substitution is not exciting, check the next one
			return getPlayerFieldPostitionAfterSubstitution(spielerId, arrIndex - 1, substitutions);
		}

		for (int i = arrIndex; i >= 0; i--) {
			tmpSub = substitutions.get(i);
			if (tmpSub.getSubjectPlayerID() == spielerId) {

				if (tmpSub.getObjectPlayerID() == spielerId) {
					// Repositioning
					return tmpSub.getRoleId();
				}

				if ((tmpSub.getObjectPlayerID() == 0) && (tmpSub.getRoleId() == 0)) {
					// Sent off or no sub after injury
					return -1;
				}

				if (tmpSub.getOrderType().getId() == 1) {
					// Normal substitution and he left the field
					return -1;
				}

				if (tmpSub.getOrderType().getId() == 3) {
					// Player swap
					// The sub object got his new position
					return tmpSub.getRoleId();
				}

				HOLogger.instance().debug(
						getClass(),
						"getPlayerFieldPostitionAfterSubstitution had a playerOut fall through. "
								+ match.getMatchID() + " " + spielerId + " " + tmpSub.getPlayerOrderId());
			}

			if (tmpSub.getObjectPlayerID() == spielerId) {
				// Repositioning is already caught.
				// Sent off does not exist here.

				if (tmpSub.getOrderType().getId() == 1) {
					// A sub entering. His position is in the sub object.
					return tmpSub.getRoleId();
				}

				if (tmpSub.getOrderType().getId() == 3) {
					// A player swap. We need to know where the other player
					// came from.
					// We figure this out by asking where he was at the end of
					// the previous sub
					// object (it is safe no matter the value of i).
					return getPlayerFieldPostitionAfterSubstitution(tmpSub.getSubjectPlayerID(), i - 1,
							substitutions);
				}

				HOLogger.instance().debug(
						getClass(),
						"getPlayerFieldPostitionAfterSubstitution had a playerIn fall through. "
								+ match.getMatchID() + " " + spielerId + " " + tmpSub.getPlayerOrderId());
			}
		} // End for loop

		HOLogger.instance().debug(
				getClass(),
				"getPlayerFieldPostitionAfterSubstitution reached the end, which should never happen "
						+ match.getMatchID() + " " + spielerId + " " + tmpSub.getPlayerOrderId());
		return -1;
	}

	public int getPlayerFieldPositionAtMinute(int spielerId, int minute) {

		List<Substitution> substitutions = teamLineup.getSubstitutions();

		// Captain and set piece taker don't count...

		if ((minute >= getMatchEndMinute(spielerId)) || (minute < 0)) {
			// The player is at home (they travel fast)...
			return -1;
		}

		if (teamLineup.getPlayerByID(spielerId) == null) {
			// Was never on the field
			return -1;
		}

		// Look for the last substitution before the given minute
		// Check if the player is involved. If not keep checking back in time
		// until match start.

		Substitution tmpSub;
		for (int i = substitutions.size() - 1; i >= 0; i--) {

			tmpSub = substitutions.get(i);
			if (tmpSub.getMatchMinuteCriteria() > minute) {
				// This is after our minute. Next, please.
				continue;
			}

			if ((tmpSub.getSubjectPlayerID() == spielerId) || (tmpSub.getObjectPlayerID() == spielerId)) {
				return getPlayerFieldPostitionAfterSubstitution(spielerId, i, substitutions);
			}
		}

		// We survived all the subs, lets see if we found him in the starting
		// lineup.
		return (teamLineup.getPlayerByID(spielerId)).getStartPosition();
	}

	/**
	 * Returns the last minute of the match. Usually 90, but there could be
	 * overtime.
	 * 
	 * @return the last minute or -0 if not found
	 */
	public int getMatchEndMinute(int spielerId) {
		var hls = match.getMatchdetails().getHighlights(); // DBManager.instance().getMatchDetails(matchId).getHighlights();
		for (MatchEvent hl : hls) {
			MatchEventID me = MatchEventID.fromMatchEventID(hl.getiMatchEventID());
			if (me == MatchEventID.MATCH_FINISHED) {
				return hl.getMinute();
			} else if (hl.getSpielerID() == spielerId) {
				if (hl.isInjured() || hl.isRedCard()) {
					return hl.getMinute();
				}
			}
		}
		return 0;
	}

	private boolean isOldie() {
		Vector<MatchLineupPlayer> mlps = teamLineup.getAufstellung();
		for (MatchLineupPlayer mlp : mlps) {
			if (mlp.getStartPosition() > 0) {
				return false;
			}
		}
		return true;
	}
}
