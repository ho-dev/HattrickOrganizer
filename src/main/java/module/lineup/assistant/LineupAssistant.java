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
	 * @param ignoreInjured:                whether or not to consider injured player
	 * @param ignoreSuspended:              whether or not to advanced suspended player
	 * @param weather:                 Actual weather
	 */
	public final void doLineup(List<MatchLineupPosition> lPositions, List<Player> lPlayers,
							   byte sectorsStrengthPriority, boolean bForm, boolean idealPosFirst, boolean ignoreInjured,
							   boolean ignoreSuspended, Weather weather) {

		lPositions = filterPositions(lPositions);

		// only setup player in ideal position
		if (idealPosFirst) {
			doPlayerLineupIdealPosition(IMatchRoleID.KEEPER, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.CENTRAL_DEFENDER, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.CENTRAL_DEFENDER_TOWING, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.CENTRAL_DEFENDER_OFF, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.BACK, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.BACK_TOMID, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.BACK_OFF, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.BACK_DEF, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.MIDFIELDER, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.MIDFIELDER_OFF, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.MIDFIELDER_DEF, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.MIDFIELDER_TOWING, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.WINGER, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.WINGER_OFF, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.WINGER_DEF, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.WINGER_TOMID, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.FORWARD, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.FORWARD_DEF, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doPlayerLineupIdealPosition(IMatchRoleID.FORWARD_TOWING, bForm, ignoreInjured, ignoreSuspended, lPlayers, lPositions);
		}

		var fieldPlayerPositionOrder = new ArrayList<List<Byte>>();
		var reservePositionOrder = new ArrayList<List<Byte>>();
		fieldPlayerPositionOrder.add(List.of(IMatchRoleID.KEEPER));
		reservePositionOrder.add(List.of(IMatchRoleID.KEEPER));
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

		ArrayList<Player> players = new ArrayList<>(lPlayers.stream().filter(i->(i.getInjuryWeeks()<1 || ignoreInjured) && (!i.isRedCarded() || ignoreSuspended)).toList());
		for (var playerPositions : fieldPlayerPositionOrder) {
			optimizeLineup(playerPositions, players, lPositions);
		}

		// Fill subs ========
		// Ideal position first
		if (idealPosFirst) {
			// TW
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.KEEPER, bForm, ignoreInjured,
					ignoreSuspended, lPlayers, lPositions);

			// abwehr
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.CENTRAL_DEFENDER, bForm,
					ignoreInjured, ignoreSuspended, lPlayers, lPositions);

			// WB
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.BACK, bForm,
					ignoreInjured, ignoreSuspended, lPlayers, lPositions);

			// mittelfeld
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.MIDFIELDER, bForm,
					ignoreInjured, ignoreSuspended, lPlayers, lPositions);
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.WINGER, bForm, ignoreInjured,
					ignoreSuspended, lPlayers, lPositions);

			// sturm
			doReserveSpielerAufstellenIdealPos(IMatchRoleID.FORWARD, bForm, ignoreInjured,
					ignoreSuspended, lPlayers, lPositions);
		}

		// fill remaining seats
		for ( var b : reservePositionOrder) {
			optimizeLineup(b,  players, lPositions);
		}
	}

	private void optimizeLineup(List<Byte> playerPositions, ArrayList<Player> players, List<MatchLineupPosition> lineupPositions) {
		if ((lineupPositions == null) || (players == null)  ) return;

		for (var pos : lineupPositions) {

			//Ignore already assigned positions and substitutes
			if ((pos.getPlayerId() > 0) || (pos.getId() >= IMatchRoleID.startReserves || !playerPositions.contains(pos.getPosition()))) {
				continue;
			}

			var behaviours = MatchLineupPosition.getBehaviours(pos.getRoleId());
			var maxRating = -1.0;
			Player bestPlayer = null;
			byte bestBehaviour = 0;
			for (var behaviour : behaviours) {
				for (var player : players) {
					var r = player.getMatchAverageRating(pos.getRoleId(), behaviour);
					if (r > maxRating) {
						maxRating = r;
						bestPlayer = player;
						bestBehaviour = behaviour;
					}
				}
			}

			// fill the position
			if (bestPlayer != null) {
				pos.setPlayerIdIfValidForLineup(bestPlayer.getPlayerId());
				pos.setBehaviour(bestBehaviour);
				players.remove(bestPlayer);
			}
		}
	}

	private void addForward(List<List<Byte>> fieldPlayerPositionOrder, List<List<Byte>> reservePositionOrder) {
		// FORWARD
		var playerPositionOrder = new ArrayList<Byte>();
		playerPositionOrder.add(IMatchRoleID.FORWARD);
		playerPositionOrder.add(IMatchRoleID.FORWARD_DEF);
		playerPositionOrder.add(IMatchRoleID.FORWARD_TOWING);
		fieldPlayerPositionOrder.add(playerPositionOrder);

		var reservePlayerPositionOrder = new ArrayList<Byte>();
		reservePlayerPositionOrder.add(IMatchRoleID.FORWARD);
		reservePositionOrder.add(reservePlayerPositionOrder);
	}

	private void addMidfield(List<List<Byte>> fieldPlayerPositionOrder, List<List<Byte>> reservePositionOrder) {
		// MIDFIELD
		var playerPositionOrder = new ArrayList<Byte>();
		playerPositionOrder.add(IMatchRoleID.MIDFIELDER);
		playerPositionOrder.add(IMatchRoleID.MIDFIELDER_OFF);
		playerPositionOrder.add(IMatchRoleID.MIDFIELDER_DEF);
		playerPositionOrder.add(IMatchRoleID.MIDFIELDER_TOWING);
		playerPositionOrder.add(IMatchRoleID.WINGER);
		playerPositionOrder.add(IMatchRoleID.WINGER_DEF);
		playerPositionOrder.add(IMatchRoleID.WINGER_OFF);
		playerPositionOrder.add(IMatchRoleID.WINGER_TOMID);
		fieldPlayerPositionOrder.add(playerPositionOrder);

		var reservePlayerPositionOrder = new ArrayList<Byte>();
		reservePlayerPositionOrder.add(IMatchRoleID.MIDFIELDER);
		reservePlayerPositionOrder.add(IMatchRoleID.WINGER);
		reservePositionOrder.add(reservePlayerPositionOrder);
	}

	private void addDefence(List<List<Byte>> fieldPlayerPositionOrder, List<List<Byte>> reservePositionOrder) {
		// DEFENCE
		var playerPositionOrder = new ArrayList<Byte>();
		playerPositionOrder.add(IMatchRoleID.CENTRAL_DEFENDER);
		playerPositionOrder.add(IMatchRoleID.CENTRAL_DEFENDER_TOWING);
		playerPositionOrder.add(IMatchRoleID.CENTRAL_DEFENDER_OFF);
		playerPositionOrder.add(IMatchRoleID.BACK);
		playerPositionOrder.add(IMatchRoleID.BACK_DEF);
		playerPositionOrder.add(IMatchRoleID.BACK_OFF);
		playerPositionOrder.add(IMatchRoleID.BACK_TOMID);
		fieldPlayerPositionOrder.add(playerPositionOrder);

		var reservePlayerPositionOrder = new ArrayList<Byte>();
		reservePlayerPositionOrder.add(IMatchRoleID.CENTRAL_DEFENDER);
		reservePlayerPositionOrder.add(IMatchRoleID.BACK);
		reservePositionOrder.add(reservePlayerPositionOrder);
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
		return true;
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
		Player player;
		if ((positionen == null) || (vPlayer == null)  ) return;

		for (var pos : positionen) {

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
