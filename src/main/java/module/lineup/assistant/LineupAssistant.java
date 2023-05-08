package module.lineup.assistant;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.match.MatchLineupPosition;
import core.model.match.Weather;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

public class LineupAssistant {
	/** Order for lineup assistent */
	public static final byte AW_MF_ST = 0;
	public static final byte AW_ST_MF = 1;
	public static final byte MF_ST_AW = 2;
	public static final byte MF_AW_ST = 3;
	public static final byte ST_AW_MF = 4;
	public static final byte ST_MF_AW = 5;
	private Weather weather = Weather.PARTIALLY_CLOUDY;

	public LineupAssistant() {
	}

	/**
	 * indicates if the player is already installed. Also ReserveBank counts
	 */
	public final boolean isPlayerInLineup(int spielerId, List<MatchLineupPosition> positions) {
		if (positions != null) {
			for (var position : positions) {
				if ( position.getPlayerId() == spielerId) {
					return true;
				}
			}
		}

		return false;
	}

    public final boolean isPlayerInStartingEleven(int spielerId, Vector<MatchLineupPosition> lineupPositions) {
		for ( var pos : lineupPositions){
			if ( pos.getPlayerId() == spielerId ) return true;
		}
        return false;
    }

	/**
	 * Assitant to create automatic lineup
	 * 
	 * @param lPositions: list of positions to be filled
	 * @param lPlayers: list of available players
	 * @param sectorsStrengthPriority: priority in sector strength (e.g. MID-FOR-DE)
	 * @param bForm: whether or not to consider the form
	 * @param idealPosFirst: whether or not to consider best position first
	 * @param bInjured: whether or not to consider injured player
	 * @param bSuspended: whether or not to advanced suspended player
	 * @param weather: Actual weather
	 */
	public final void doLineup(List<MatchLineupPosition> lPositions, List<Player> lPlayers,
							   byte sectorsStrengthPriority, boolean bForm, boolean idealPosFirst, boolean bInjured,
							   boolean bSuspended, Weather weather) {

		lPositions = filterPositions(lPositions);
		this.weather = weather;

		// only setup player in ideal position
		if (idealPosFirst) {
			doPlayerLineupIdealPosition(IMatchRoleID.KEEPER, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.CENTRAL_DEFENDER, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.CENTRAL_DEFENDER_TOWING, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.CENTRAL_DEFENDER_OFF, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.BACK, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.BACK_TOMID, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.BACK_OFF, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.BACK_DEF, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.MIDFIELDER, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.MIDFIELDER_OFF, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.MIDFIELDER_DEF, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.MIDFIELDER_TOWING, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.WINGER, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.WINGER_OFF, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.WINGER_DEF, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.WINGER_TOMID, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.FORWARD, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.FORWARD_DEF, bForm, bInjured, bSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.FORWARD_TOWING, bForm, bInjured, bSuspended, lPlayers, lPositions);
		}

		// set Goalkeeper
		doSpielerAufstellen(IMatchRoleID.KEEPER, bForm, bInjured, bSuspended,lPlayers, lPositions);

		byte[] order;
		// nun reihenfolge beachten und unbesetzte füllen
		switch (sectorsStrengthPriority) {
		case AW_MF_ST:

			order = new byte[18];
			// DEFENCE
			order[0] = IMatchRoleID.CENTRAL_DEFENDER;
			order[1] = IMatchRoleID.CENTRAL_DEFENDER_TOWING;
			order[2] = IMatchRoleID.CENTRAL_DEFENDER_OFF;
			order[3] = IMatchRoleID.BACK;
			order[4] = IMatchRoleID.BACK_DEF;
			order[5] = IMatchRoleID.BACK_OFF;
			order[6] = IMatchRoleID.BACK_TOMID;
			// MIDFIELD
			order[7] = IMatchRoleID.MIDFIELDER;
			order[8] = IMatchRoleID.MIDFIELDER_OFF;
			order[9] = IMatchRoleID.MIDFIELDER_DEF;
			order[10] = IMatchRoleID.MIDFIELDER_TOWING;
			order[11] = IMatchRoleID.WINGER;
			order[12] = IMatchRoleID.WINGER_DEF;
			order[13] = IMatchRoleID.WINGER_OFF;
			order[14] = IMatchRoleID.WINGER_TOMID;
			// FORWARD
			order[15] = IMatchRoleID.FORWARD;
			order[16] = IMatchRoleID.FORWARD_DEF;
			order[17] = IMatchRoleID.FORWARD_TOWING;
			break;

		case AW_ST_MF:

			order = new byte[18];
			// DEFENCE
			order[0] = IMatchRoleID.CENTRAL_DEFENDER;
			order[1] = IMatchRoleID.CENTRAL_DEFENDER_TOWING;
			order[2] = IMatchRoleID.CENTRAL_DEFENDER_OFF;
			order[3] = IMatchRoleID.BACK;
			order[4] = IMatchRoleID.BACK_DEF;
			order[5] = IMatchRoleID.BACK_OFF;
			order[6] = IMatchRoleID.BACK_TOMID;
			// FORWARD
			order[7] = IMatchRoleID.FORWARD;
			order[8] = IMatchRoleID.FORWARD_DEF;
			order[9] = IMatchRoleID.FORWARD_TOWING;

			// MIDFIELD
			order[10] = IMatchRoleID.MIDFIELDER;
			order[11] = IMatchRoleID.MIDFIELDER_OFF;
			order[12] = IMatchRoleID.MIDFIELDER_DEF;
			order[13] = IMatchRoleID.MIDFIELDER_TOWING;
			order[14] = IMatchRoleID.WINGER;
			order[15] = IMatchRoleID.WINGER_DEF;
			order[16] = IMatchRoleID.WINGER_OFF;
			order[17] = IMatchRoleID.WINGER_TOMID;

			break;

		case MF_AW_ST:

			order = new byte[18];

			// MIDFIELD
			order[0] = IMatchRoleID.MIDFIELDER;
			order[1] = IMatchRoleID.MIDFIELDER_OFF;
			order[2] = IMatchRoleID.MIDFIELDER_DEF;
			order[3] = IMatchRoleID.MIDFIELDER_TOWING;
			order[4] = IMatchRoleID.WINGER;
			order[5] = IMatchRoleID.WINGER_DEF;
			order[6] = IMatchRoleID.WINGER_OFF;
			order[7] = IMatchRoleID.WINGER_TOMID;
			// DEFENCE
			order[8] = IMatchRoleID.CENTRAL_DEFENDER;
			order[9] = IMatchRoleID.CENTRAL_DEFENDER_TOWING;
			order[10] = IMatchRoleID.CENTRAL_DEFENDER_OFF;
			order[11] = IMatchRoleID.BACK;
			order[12] = IMatchRoleID.BACK_DEF;
			order[13] = IMatchRoleID.BACK_OFF;
			order[14] = IMatchRoleID.BACK_TOMID;
			// FORWARD
			order[15] = IMatchRoleID.FORWARD;
			order[16] = IMatchRoleID.FORWARD_DEF;
			order[17] = IMatchRoleID.FORWARD_TOWING;
			break;

		case MF_ST_AW:

			order = new byte[18];

			// MIDFIELD
			order[0] = IMatchRoleID.MIDFIELDER;
			order[1] = IMatchRoleID.MIDFIELDER_OFF;
			order[2] = IMatchRoleID.MIDFIELDER_DEF;
			order[3] = IMatchRoleID.MIDFIELDER_TOWING;
			order[4] = IMatchRoleID.WINGER;
			order[5] = IMatchRoleID.WINGER_DEF;
			order[6] = IMatchRoleID.WINGER_OFF;
			order[7] = IMatchRoleID.WINGER_TOMID;
			// FORWARD
			order[8] = IMatchRoleID.FORWARD;
			order[9] = IMatchRoleID.FORWARD_DEF;
			order[10] = IMatchRoleID.FORWARD_TOWING;
			// DEFENCE
			order[11] = IMatchRoleID.CENTRAL_DEFENDER;
			order[12] = IMatchRoleID.CENTRAL_DEFENDER_TOWING;
			order[13] = IMatchRoleID.CENTRAL_DEFENDER_OFF;
			order[14] = IMatchRoleID.BACK;
			order[15] = IMatchRoleID.BACK_DEF;
			order[16] = IMatchRoleID.BACK_OFF;
			order[17] = IMatchRoleID.BACK_TOMID;

			break;

		case ST_MF_AW:

			order = new byte[18];

			// FORWARD
			order[0] = IMatchRoleID.FORWARD;
			order[1] = IMatchRoleID.FORWARD_DEF;
			order[2] = IMatchRoleID.FORWARD_TOWING;
			// MIDFIELD
			order[3] = IMatchRoleID.MIDFIELDER;
			order[4] = IMatchRoleID.MIDFIELDER_OFF;
			order[5] = IMatchRoleID.MIDFIELDER_DEF;
			order[6] = IMatchRoleID.MIDFIELDER_TOWING;
			order[7] = IMatchRoleID.WINGER;
			order[8] = IMatchRoleID.WINGER_DEF;
			order[9] = IMatchRoleID.WINGER_OFF;
			order[10] = IMatchRoleID.WINGER_TOMID;

			// DEFENCE
			order[11] = IMatchRoleID.CENTRAL_DEFENDER;
			order[12] = IMatchRoleID.CENTRAL_DEFENDER_TOWING;
			order[13] = IMatchRoleID.CENTRAL_DEFENDER_OFF;
			order[14] = IMatchRoleID.BACK;
			order[15] = IMatchRoleID.BACK_DEF;
			order[16] = IMatchRoleID.BACK_OFF;
			order[17] = IMatchRoleID.BACK_TOMID;
			break;

		case ST_AW_MF:

			order = new byte[18];
			// FORWARD
			order[0] = IMatchRoleID.FORWARD;
			order[1] = IMatchRoleID.FORWARD_DEF;
			order[2] = IMatchRoleID.FORWARD_TOWING;
			// DEFENCE
			order[3] = IMatchRoleID.CENTRAL_DEFENDER;
			order[4] = IMatchRoleID.CENTRAL_DEFENDER_TOWING;
			order[5] = IMatchRoleID.CENTRAL_DEFENDER_OFF;
			order[6] = IMatchRoleID.BACK;
			order[7] = IMatchRoleID.BACK_DEF;
			order[8] = IMatchRoleID.BACK_OFF;
			order[9] = IMatchRoleID.BACK_TOMID;
			// MIDFIELD
			order[10] = IMatchRoleID.MIDFIELDER;
			order[11] = IMatchRoleID.MIDFIELDER_OFF;
			order[12] = IMatchRoleID.MIDFIELDER_DEF;
			order[13] = IMatchRoleID.MIDFIELDER_TOWING;
			order[14] = IMatchRoleID.WINGER;
			order[15] = IMatchRoleID.WINGER_DEF;
			order[16] = IMatchRoleID.WINGER_OFF;
			order[17] = IMatchRoleID.WINGER_TOMID;

			break;

		default:
			return;

			// break;
		}

		for (byte b : order) {
			doSpielerAufstellen(b, bForm, bInjured, bSuspended, lPlayers, lPositions);
		}

		// Fill subs ========
		// Ideal position first
		if (idealPosFirst) {
			// TW
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.KEEPER, bForm, bInjured,
					bSuspended, lPlayers, lPositions);

			// abwehr
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.CENTRAL_DEFENDER, bForm,
					bInjured, bSuspended, lPlayers, lPositions);

			// WB
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.BACK, bForm,
					bInjured, bSuspended, lPlayers, lPositions);

			// mittelfeld
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.MIDFIELDER, bForm,
					bInjured, bSuspended, lPlayers, lPositions);
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.WINGER, bForm, bInjured,
					bSuspended, lPlayers, lPositions);

			// sturm
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.FORWARD, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
		}

		// fill remaining seats
		// TW
		doReserveSpielerAufstellen(IMatchRoleID.KEEPER, bForm, bInjured,
				bSuspended, lPlayers, lPositions);

		// abwehr
		doReserveSpielerAufstellen(IMatchRoleID.CENTRAL_DEFENDER, bForm, bInjured,
				bSuspended, lPlayers, lPositions);

		// WB
		doReserveSpielerAufstellen(IMatchRoleID.BACK, bForm, bInjured,
				bSuspended, lPlayers, lPositions);

		// mittelfeld
		doReserveSpielerAufstellen(IMatchRoleID.MIDFIELDER, bForm, bInjured,
				bSuspended, lPlayers, lPositions);
		doReserveSpielerAufstellen(IMatchRoleID.WINGER, bForm, bInjured,
				bSuspended, lPlayers, lPositions);

		// sturm
		doReserveSpielerAufstellen(IMatchRoleID.FORWARD, bForm, bInjured,
				bSuspended, lPlayers, lPositions);
	}

	/**
	 * resets all connections between position and player
	 * 
	 * @param positionen
	 *            a vector of player positions
	 */
	public final void resetPositionsbesetzungen(Vector<MatchLineupPosition> positionen) {
		for ( var pos: positionen){
			pos.setPlayerIdIfValidForLineup(0);
		}
	}
	
	/**
	 * Resets the orders for all positions to normal
	 * @param positions
	 * 		a vector of player positions
	 */
	public final void resetPositionOrders(Vector<MatchLineupPosition> positions) {
		if ( positions==null) return;
		for  ( var pos : positions){
			pos.setTaktik((byte)0);
		}
	}

	/**
	 * Checks if there is a player with a specified id in the current team.
	 * 
	 * @param playerID
	 *            the id of the player
	 * @return <code>true</code> if there is player with the specified id in the
	 *         team, <code>false</code> otherwise.
	 */
	public static boolean isPlayerInTeam(int playerID) {
		List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		for (Player player : players) {
			if (player.getPlayerID() == playerID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns the best player for the requested position
	 */
	protected final Player getBestPlayerForPosition(byte position, boolean considerForm,
													boolean ignoredInjury, boolean ignoreRedCarded, List<Player> players,
													List<MatchLineupPosition> positions) {
		Player player;
		Player bestPlayer = null;
		float maxRating = -1.0f;
		float currentRating;

		for (int i = 0; (players != null) && (i < players.size()); i++) {
			player = players.get(i);

			// stk inklusive Wetter effekt errechnen
			currentRating = player.calcPosValue(position, considerForm, weather, true);

			if ((!isPlayerInLineup(player.getPlayerID(), positions))
					&& ((bestPlayer == null) || (maxRating < currentRating))
					&& ((ignoreRedCarded) || (!player.isRedCarded()))
					&& ((ignoredInjury) || (player.getInjuryWeeks() < 1))
					&& (player.getCanBeSelectedByAssistant())) {
				bestPlayer = player;
				maxRating = currentRating;
			}
		}

		return bestPlayer;
	}

	/**
	 * returns the best player one for the requested position who is best suited for that position
	 */
	protected final Player getBestPlayerIdealPosOnly(byte position, boolean considerForm,
													 boolean ignoredInjury, boolean ignoreRedCarded, List<Player> players,
													 List<MatchLineupPosition> positions) {

		List<Player> playersIdealPositionOnly = players.stream().filter(p -> p.isAnAlternativeBestPosition(position)).collect(Collectors.toList());
		return getBestPlayerForPosition(position, considerForm, ignoredInjury, ignoreRedCarded, playersIdealPositionOnly, positions);
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doReserveSpielerAufstellen(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
			List<MatchLineupPosition> positionen) {
		MatchRoleID pos;
		Player player;

		for (int i = 0; (positionen != null) && (vPlayer != null) && (i < positionen.size()); i++) {
			pos = positionen.get(i);

			// Ignore already assigned positions and non substitute position
			if ((pos.getPlayerId() > 0) || ! IMatchRoleID.aSubstitutesMatchRoleID.contains(pos.getId())) {
				continue;
			}

			// nur exacte Pos
			if (pos.getPosition() == position) {
				player = getBestPlayerForPosition(position, mitForm, ignoreVerletzung, ignoreSperre,
                        vPlayer, positionen);

				// position besetzen
				if (player != null) {
					pos.setPlayerIdIfValidForLineup(player.getPlayerID());
				}
			}
		}
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doReserveSpielerAufstellenIdealPos(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
			List<MatchLineupPosition> positionen) {
		MatchRoleID pos;
		Player player;

		for (int i = 0; (positionen != null) && (vPlayer != null) && (i < positionen.size()); i++) {
			pos = positionen.get(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getPlayerId() > 0) || (pos.getId() < IMatchRoleID.startReserves)) {
				continue;
			}

			// nur exakte Position
			if (pos.getPosition() == position) {
				player = getBestPlayerIdealPosOnly(position, mitForm, ignoreVerletzung,
						ignoreSperre, vPlayer, positionen);

				// position besetzen
				if (player != null) {
					pos.setPlayerIdIfValidForLineup(player.getPlayerID());
				}
			}
		}
	}

	/**
	 * automatic lineup
	 * @param position: current position being optimized
	 * @param mitForm: whether or not to consider the form
	 * @param ignoreVerletzung: whether or not to align the injured player
	 * @param ignoreSperre:  whether or not to align the red-carded player
	 * @param vPlayer: current position being optimized
	 * @param positionen: list of position to be filled
	 */
	protected final void doSpielerAufstellen(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
			List<MatchLineupPosition> positionen) {
		MatchRoleID pos;
		Player player;
//		final Vector<IMatchRoleID> zusPos = new Vector<IMatchRoleID>();

		for (int i = 0; (positionen != null) && (vPlayer != null) && (i < positionen.size()); i++) {
			pos = positionen.get(i);

			//Ignore already assigned positions and substitutes
			if ((pos.getPlayerId() > 0) || (pos.getId() >= IMatchRoleID.startReserves)) {
				continue;
			}

			// position found => get the best player or that position
			if (pos.getPosition() == position) {
				player = getBestPlayerForPosition(position, mitForm, ignoreVerletzung, ignoreSperre,
                        vPlayer, positionen);

				// fill the position
				if (player != null) {
					pos.setPlayerIdIfValidForLineup(player.getPlayerID());
				}
			}
		}
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doPlayerLineupIdealPosition(byte position, boolean considerForm,
													 boolean ignoreInjury, boolean ignoreRedCarded, List<Player> players,
													 List<MatchLineupPosition> positions) {
		MatchRoleID pos;
		Player player;
		final Vector<IMatchRoleID> zusPos = new Vector<>();

		for (int i = 0; (positions != null) && (players != null) && (i < positions.size()); i++) {
			pos = positions.get(i);

			//ignore already assigned positions and leave ReserveBank empty
			if ((pos.getPlayerId() > 0) || (pos.getId() >= IMatchRoleID.startReserves)) {
				continue;
			}

			// only exact position
			if (pos.getPosition() == position) {
				player = getBestPlayerIdealPosOnly(position, considerForm, ignoreInjury,
						ignoreRedCarded, players, positions);

				// occupy position
				if (player != null) {
					pos.setPlayerIdIfValidForLineup(player.getPlayerID());
				}
			}
		}

		// now fill the additional XYZ positions
		for (int i = 0; (zusPos != null) && (players != null) && (i < zusPos.size()); i++) {
			pos = (MatchRoleID) zusPos.elementAt(i);

			// ignore already assigned positions and leave ReserveBank empty
			if ((pos.getPlayerId() > 0) || (pos.getId() >= IMatchRoleID.startReserves)) {
				continue;
			}

			// only exact position
			if (pos.getPosition() == position) {
				player = getBestPlayerIdealPosOnly(position, considerForm, ignoreInjury,
						ignoreRedCarded, players, positions);

				// occupy position
				if (player != null) {
					pos.setPlayerIdIfValidForLineup(player.getPlayerID());
				}
			}
		}
	}

	private Vector<MatchLineupPosition> filterPositions(List<MatchLineupPosition> positions) {
		// Remove "red" positions from the position selection of the AssistantPanel.
		Vector<MatchLineupPosition> returnVec = new Vector<>();
		Map<Integer, Boolean> statusMap = HOMainFrame.instance().getLineupPanel().getAssistantPositionsStatus();
		for (var pos : positions) {
			if ((!statusMap.containsKey(pos.getId())) || (statusMap.get(pos.getId()))) {
				returnVec.add(pos);
			}
		}
		return returnVec;
	}

}
