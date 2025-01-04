package module.lineup.assistant;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.match.MatchLineupPosition;
import core.model.match.Weather;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.rating.RatingPredictionModel;

import java.util.*;
import java.util.stream.Collectors;

import static core.rating.RatingPredictionModel.getBehaviour;

public class LineupAssistant {
	/**
	 * Order for lineup assistent
	 */
	public static final byte AW_MF_ST = 0;
	public static final byte AW_ST_MF = 1;
	public static final byte MF_ST_AW = 2;
	public static final byte MF_AW_ST = 3;
	public static final byte ST_AW_MF = 4;
	public static final byte ST_MF_AW = 5;

	public LineupAssistant() {
	}

	/**
	 * indicates if the player is already installed. Also ReserveBank counts
	 */
	public final boolean isPlayerInLineup(int spielerId, List<MatchLineupPosition> positions) {
		if (positions != null) {
			for (var position : positions) {
				if (position.getPlayerId() == spielerId) {
					return true;
				}
			}
		}

		return false;
	}

	public final boolean isPlayerInStartingEleven(int spielerId, Vector<MatchLineupPosition> lineupPositions) {
		for (var pos : lineupPositions) {
			if (pos.getPlayerId() == spielerId) return true;
		}
		return false;
	}

	/**
	 * Assistant to create automatic lineup
	 *
	 * @param lPositions:              list of positions to be filled
	 * @param lPlayers:                list of available players
	 * @param sectorsStrengthPriority: priority in sector strength (e.g. MID-FOR-DE)
	 * @param bForm:                   whether or not to consider the form
	 * @param idealPosFirst:           whether or not to consider best position first
	 * @param bInjured:                whether or not to consider injured player
	 * @param bSuspended:              whether or not to advanced suspended player
	 * @param weather:                 Actual weather
	 */
	public final void doLineup(List<MatchLineupPosition> lPositions, List<Player> lPlayers,
							   byte sectorsStrengthPriority, boolean bForm, boolean idealPosFirst, boolean bInjured,
							   boolean bSuspended, Weather weather) {

		lPositions = filterPositions(lPositions);

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

		var fieldPlayerPositionOrder = new ArrayList<Byte>();
		var reservePositionOrder = new ArrayList<Byte>();
		fieldPlayerPositionOrder.add(IMatchRoleID.KEEPER);
		reservePositionOrder.add(IMatchRoleID.KEEPER);
		// nun reihenfolge beachten und unbesetzte füllen
		switch (sectorsStrengthPriority) {
			case AW_MF_ST -> {
				addDefence(fieldPlayerPositionOrder, reservePositionOrder);
				addMidfield(fieldPlayerPositionOrder, reservePositionOrder);
				addForward(fieldPlayerPositionOrder, reservePositionOrder);
			}
			case AW_ST_MF -> {
				addDefence(fieldPlayerPositionOrder, reservePositionOrder);
				addForward(fieldPlayerPositionOrder, reservePositionOrder);
				addMidfield(fieldPlayerPositionOrder, reservePositionOrder);
			}
			case MF_AW_ST -> {
				addMidfield(fieldPlayerPositionOrder, reservePositionOrder);
				addDefence(fieldPlayerPositionOrder, reservePositionOrder);
				addForward(fieldPlayerPositionOrder, reservePositionOrder);
			}
			case MF_ST_AW -> {
				addMidfield(fieldPlayerPositionOrder, reservePositionOrder);
				addForward(fieldPlayerPositionOrder, reservePositionOrder);
				addDefence(fieldPlayerPositionOrder, reservePositionOrder);
			}
			case ST_MF_AW -> {
				addForward(fieldPlayerPositionOrder, reservePositionOrder);
				addMidfield(fieldPlayerPositionOrder, reservePositionOrder);
				addDefence(fieldPlayerPositionOrder, reservePositionOrder);
			}
			case ST_AW_MF -> {
				addForward(fieldPlayerPositionOrder, reservePositionOrder);
				addDefence(fieldPlayerPositionOrder, reservePositionOrder);
				addMidfield(fieldPlayerPositionOrder, reservePositionOrder);
			}
			default -> {
				return;
			}

			// break;
		}

		for (byte b : fieldPlayerPositionOrder) {
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
		for ( byte b : reservePositionOrder) {
			doReserveSpielerAufstellen(b, bForm, bInjured, bSuspended, lPlayers, lPositions);
		}
	}

	private void addForward(ArrayList<Byte> fieldPlayerPositionOrder, ArrayList<Byte> reservePositionOrder) {
		// FORWARD
		fieldPlayerPositionOrder.add(IMatchRoleID.FORWARD);
		fieldPlayerPositionOrder.add(IMatchRoleID.FORWARD_DEF);
		fieldPlayerPositionOrder.add(IMatchRoleID.FORWARD_TOWING);

		reservePositionOrder.add(IMatchRoleID.FORWARD);
	}

	private void addMidfield(ArrayList<Byte> fieldPlayerPositionOrder, ArrayList<Byte> reservePositionOrder) {
		// MIDFIELD
		fieldPlayerPositionOrder.add(IMatchRoleID.MIDFIELDER);
		fieldPlayerPositionOrder.add(IMatchRoleID.MIDFIELDER_OFF);
		fieldPlayerPositionOrder.add(IMatchRoleID.MIDFIELDER_DEF);
		fieldPlayerPositionOrder.add(IMatchRoleID.MIDFIELDER_TOWING);
		fieldPlayerPositionOrder.add(IMatchRoleID.WINGER);
		fieldPlayerPositionOrder.add(IMatchRoleID.WINGER_DEF);
		fieldPlayerPositionOrder.add(IMatchRoleID.WINGER_OFF);
		fieldPlayerPositionOrder.add(IMatchRoleID.WINGER_TOMID);

		reservePositionOrder.add(IMatchRoleID.MIDFIELDER);
		reservePositionOrder.add(IMatchRoleID.WINGER);
	}

	private void addDefence(ArrayList<Byte> fieldPlayerPositionOrder, ArrayList<Byte> reservePositionOrder) {
		// DEFENCE
		fieldPlayerPositionOrder.add(IMatchRoleID.CENTRAL_DEFENDER);
		fieldPlayerPositionOrder.add(IMatchRoleID.CENTRAL_DEFENDER_TOWING);
		fieldPlayerPositionOrder.add(IMatchRoleID.CENTRAL_DEFENDER_OFF);
		fieldPlayerPositionOrder.add(IMatchRoleID.BACK);
		fieldPlayerPositionOrder.add(IMatchRoleID.BACK_DEF);
		fieldPlayerPositionOrder.add(IMatchRoleID.BACK_OFF);
		fieldPlayerPositionOrder.add(IMatchRoleID.BACK_TOMID);

		reservePositionOrder.add(IMatchRoleID.CENTRAL_DEFENDER);
		reservePositionOrder.add(IMatchRoleID.BACK);
	}

	/**
	 * resets all connections between position and player
	 *
	 * @param positionen a vector of player positions
	 */
	public final void resetPositionsbesetzungen(Vector<MatchLineupPosition> positionen) {
		for (var pos : positionen) {
			pos.setPlayerIdIfValidForLineup(0);
		}
	}

	/**
	 * Resets the orders for all positions to normal
	 *
	 * @param positions a vector of player positions
	 */
	public final void resetPositionOrders(Vector<MatchLineupPosition> positions) {
		if (positions == null) return;
		for (var pos : positions) {
			pos.setBehaviour((byte) 0);
		}
	}

	/**
	 * Checks if there is a player with a specified id in the current team and not disabled for lineup.
	 *
	 * @param playerID the id of the player
	 * @return <code>true</code> if there is not disabled player with the specified id in the
	 * team, <code>false</code> otherwise.
	 */
	public static boolean isPlayerEnabledForLineup(int playerID) {
		List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		for (Player player : players) {
			if (player.getPlayerId() == playerID) {
				return player.getCanBeSelectedByAssistant();
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
		Player bestPlayer = null;
		double maxRating = -1.0f;

		var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();

		if (players != null) {
			for (var player : players) {
				var r = ratingPredictionModel.getPlayerMatchAverageRating(player, RatingPredictionModel.getPlayerRatingPosition(position), getBehaviour(position));
				if ((!isPlayerInLineup(player.getPlayerId(), positions))
						&& ((bestPlayer == null) || (maxRating < r))
						&& ((ignoreRedCarded) || (!player.isRedCarded()))
						&& ((ignoredInjury) || (player.getInjuryWeeks() < 1))
						&& (player.getCanBeSelectedByAssistant())) {
					bestPlayer = player;
					maxRating = r;
				}
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
			if ((pos.getPlayerId() > 0) || !IMatchRoleID.aSubstitutesMatchRoleID.contains(pos.getId())) {
				continue;
			}

			// nur exacte Pos
			if (pos.getPosition() == position) {
				player = getBestPlayerForPosition(position, mitForm, ignoreVerletzung, ignoreSperre,
						vPlayer, positionen);

				// position besetzen
				if (player != null) {
					pos.setPlayerIdIfValidForLineup(player.getPlayerId());
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
					pos.setPlayerIdIfValidForLineup(player.getPlayerId());
				}
			}
		}
	}

	/**
	 * automatic lineup
	 *
	 * @param position:         current position being optimized
	 * @param mitForm:          whether or not to consider the form
	 * @param ignoreVerletzung: whether or not to align the injured player
	 * @param ignoreSperre:     whether or not to align the red-carded player
	 * @param vPlayer:          current position being optimized
	 * @param positionen:       list of position to be filled
	 */
	protected final void doSpielerAufstellen(byte position, boolean mitForm,
											 boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
											 List<MatchLineupPosition> positionen) {
		MatchRoleID pos;
		Player player;

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
					pos.setPlayerIdIfValidForLineup(player.getPlayerId());
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
					pos.setPlayerIdIfValidForLineup(player.getPlayerId());
				}
			}
		}
	}

	private Vector<MatchLineupPosition> filterPositions(List<MatchLineupPosition> positions) {
		// Remove "red" positions from the position selection of the AssistantPanel.
		Vector<MatchLineupPosition> returnVec = new Vector<>();
		Map<Integer, Boolean> statusMap = Objects.requireNonNull(HOMainFrame.instance().getLineupPanel()).getAssistantPositionsStatus();
		for (var pos : positions) {
			if ((!statusMap.containsKey(pos.getId())) || (statusMap.get(pos.getId()))) {
				returnVec.add(pos);
			}
		}
		return returnVec;
	}

}
