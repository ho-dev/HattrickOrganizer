package module.lineup;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.match.Weather;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class LineupAssistant {
	/** Order for lineup assistent */
	public static final byte AW_MF_ST = 0;
	public static final byte AW_ST_MF = 1;
	public static final byte MF_ST_AW = 2;
	public static final byte MF_AW_ST = 3;
	public static final byte ST_AW_MF = 4;
	public static final byte ST_MF_AW = 5;
	private float m_weatherBonus = 0.05f;
	private Weather weather = Weather.PARTIALLY_CLOUDY;

	/**
	 * indicates if the player is already installed. Also ReserveBank counts
	 */
	public final boolean isPlayerInLineup(int spielerId, List<IMatchRoleID> positions) {
		if (positions != null) {
			for (IMatchRoleID position : positions) {
				if (((MatchRoleID) position).getSpielerId() == spielerId) {
					return true;
				}
			}
		}

		return false;
	}

    public final boolean isPlayerInStartingEleven(int spielerId, Vector<IMatchRoleID> positionen) {
        for (int i = 0; (positionen != null) && (i < positionen.size()); i++) {
            if (IMatchRoleID.aFieldMatchRoleID.contains(((MatchRoleID) positionen.elementAt(i)).getId()) &&
                    (((MatchRoleID) positionen.elementAt(i)).getSpielerId() == spielerId)) {
                return true;
            }
        }

        return false;
    }

    public final boolean isPlayerASub(int spielerId, Vector<IMatchRoleID> positionen) {
        for (int i = 0; (positionen != null) && (i < positionen.size()); i++) {
            if (IMatchRoleID.aSubstitutesMatchRoleID.contains(((MatchRoleID) positionen.elementAt(i)).getId()) &&
                    (((MatchRoleID) positionen.elementAt(i)).getSpielerId() == spielerId)) {
                return true;
            }
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
	 * @param weatherBonus: Threshold indicating how to react to weather effects ??
	 * @param weather: Actual weather
	 */
	public final void doAufstellung(List<IMatchRoleID> lPositions, List<Player> lPlayers,
                                    byte sectorsStrengthPriority, boolean bForm, boolean idealPosFirst, boolean bInjured,
                                    boolean bSuspended, float weatherBonus, Weather weather) {

		lPositions = filterPositions(lPositions);

		m_weatherBonus = weatherBonus;
		this.weather = weather;

		// only setup player in ideal position
		if (idealPosFirst) {
			float backup = core.model.UserParameter.instance().MinIdealPosStk;

			// Maimum von beiden für Berechnung verwenden
			core.model.UserParameter.instance().MinIdealPosStk = Math.max(
					calcAveragePosValue(lPlayers),
					core.model.UserParameter.instance().MinIdealPosStk);

			doSpielerAufstellenIdealPos(IMatchRoleID.KEEPER, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.CENTRAL_DEFENDER, bForm,
					bInjured, bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.CENTRAL_DEFENDER_TOWING, bForm,
					bInjured, bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.CENTRAL_DEFENDER_OFF, bForm,
					bInjured, bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.BACK, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.BACK_TOMID, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.BACK_OFF, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.BACK_DEF, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.MIDFIELDER, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.MIDFIELDER_OFF, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.MIDFIELDER_DEF, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.MIDFIELDER_TOWING, bForm,
					bInjured, bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.WINGER, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.WINGER_OFF, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.WINGER_DEF, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.WINGER_TOMID, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.FORWARD, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.FORWARD_DEF, bForm, bInjured,
					bSuspended, lPlayers, lPositions);
			doSpielerAufstellenIdealPos(IMatchRoleID.FORWARD_TOWING, bForm, bInjured,
					bSuspended, lPlayers, lPositions);

			// Wert wieder zurücksetzen
			core.model.UserParameter.instance().MinIdealPosStk = backup;
		}

		// set Goalkeeper
		doSpielerAufstellen(IMatchRoleID.KEEPER, bForm, bInjured, bSuspended,
                lPlayers, lPositions);

		byte[] order = null;
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

		if (order != null) {
			for (int i = 0; i < order.length; i++) {
				doSpielerAufstellen(order[i], bForm, bInjured, bSuspended, lPlayers,
						lPositions);
			}
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
	public final void resetPositionsbesetzungen(Vector<IMatchRoleID> positionen) {
		for (int i = 0; (positionen != null) && (i < positionen.size()); i++) {
			((MatchRoleID) positionen.elementAt(i)).setSpielerId(0);
		}
	}
	
	/**
	 * Resets the orders for all positions to normal
	 * @param positions
	 * 		a vector of player positions
	 */
	public final void resetPositionOrders(Vector<IMatchRoleID> positions) {
		if (positions != null) {
			for (int i = 0; i < positions.size(); i++) {
				((MatchRoleID) positions.elementAt(i)).setTaktik((byte) 0);
			}
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
		List<Player> players = HOVerwaltung.instance().getModel().getAllSpieler();
		for (Player player : players) {
			if (player.getSpielerID() == playerID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * liefert aus dem eigenen Vector mit Spielern den besten für die
	 * angefordertet Position der noch nicht aufgestellt ist
	 */
	protected final Player getBestSpieler(byte position, boolean mitForm,
                                          boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
                                          List<IMatchRoleID> positionen) {
		Player player;
		Player bestPlayer = null;
		float bestStk = -1.0f;
		float aktuStk;

		for (int i = 0; (vPlayer != null) && (i < vPlayer.size()); i++) {
			player = vPlayer.get(i);

			// stk inklusive Wetter effekt errechnen
			aktuStk = player.calcPosValue(position, mitForm);
			aktuStk += m_weatherBonus * player.getWeatherEffect(this.weather) * aktuStk;

			if ((!isPlayerInLineup(player.getSpielerID(), positionen))
					&& ((bestPlayer == null) || (bestStk < aktuStk))
					&& ((ignoreSperre) || (!player.isGesperrt()))
					&& ((ignoreVerletzung) || (player.getVerletzt() < 1))
					&& (player.isSpielberechtigt())) {
				bestPlayer = player;
				bestStk = aktuStk;
			}
		}

		return bestPlayer;
	}

	/**
	 * liefert aus dem eigenen Vector mit Spielern den besten für die
	 * angefordertet Position der noch nicht aufgestellt ist
	 */
	protected final Player getBestSpielerIdealPosOnly(byte position, boolean mitForm,
                                                      boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
                                                      List<IMatchRoleID> positionen) {
		Player player = null;
		Player bestPlayer = null;
		float bestStk = -1.0f;
		float aktuStk = 0.0f;

		for (int i = 0; (vPlayer != null) && (i < vPlayer.size()); i++) {
			// Player holen
			player = (Player) vPlayer.get(i);

			// stk inklusive Wetter effekt errechnen
			aktuStk = player.calcPosValue(position, mitForm);
			aktuStk += m_weatherBonus * player.getWeatherEffect(this.weather) * aktuStk;

			// Idealpos STK muss > mindestwert sein
			if ((!isPlayerInLineup(player.getSpielerID(), positionen))
					&& (player.getIdealPosition() == position)
					&& ((bestPlayer == null) || (bestStk < aktuStk))
					&& ((ignoreSperre) || (!player.isGesperrt()))
					&& ((ignoreVerletzung) || (player.getVerletzt() < 1))
					&& (aktuStk > core.model.UserParameter.instance().MinIdealPosStk)
					&& (!player.isTrainer()) && (player.isSpielberechtigt())) {
				bestPlayer = player;
				bestStk = aktuStk;
			}
		}

		return bestPlayer;
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doReserveSpielerAufstellen(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
			List<IMatchRoleID> positionen) {
		MatchRoleID pos = null;
		Player player = null;

		for (int i = 0; (positionen != null) && (vPlayer != null) && (i < positionen.size()); i++) {
			pos = (MatchRoleID) positionen.get(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() < IMatchRoleID.startReserves)) {
				continue;
			}

			// nur exacte Pos
			if (pos.getPosition() == position) {
				player = getBestSpieler(position, mitForm, ignoreVerletzung, ignoreSperre,
                        vPlayer, positionen);

				// position besetzen
				if (player != null) {
					pos.setSpielerId(player.getSpielerID());
				}
			}
		}
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doReserveSpielerAufstellenIdealPos(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
			List<IMatchRoleID> positionen) {
		MatchRoleID pos = null;
		Player player = null;

		for (int i = 0; (positionen != null) && (vPlayer != null) && (i < positionen.size()); i++) {
			pos = (MatchRoleID) positionen.get(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() < IMatchRoleID.startReserves)) {
				continue;
			}

			// nur exakte Position
			if (pos.getPosition() == position) {
				player = getBestSpielerIdealPosOnly(position, mitForm, ignoreVerletzung,
						ignoreSperre, vPlayer, positionen);

				// position besetzen
				if (player != null) {
					pos.setSpielerId(player.getSpielerID());
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
			List<IMatchRoleID> positionen) {
		MatchRoleID pos;
		Player player;
//		final Vector<IMatchRoleID> zusPos = new Vector<IMatchRoleID>();

		for (int i = 0; (positionen != null) && (vPlayer != null) && (i < positionen.size()); i++) {
			pos = (MatchRoleID) positionen.get(i);

			//Ignore already assigned positions and substitutes
			if ((pos.getSpielerId() > 0) || (pos.getId() >= IMatchRoleID.startReserves)) {
				continue;
			}

			// position found => get the best player or that position
			if (pos.getPosition() == position) {
				player = getBestSpieler(position, mitForm, ignoreVerletzung, ignoreSperre,
                        vPlayer, positionen);

				// fill the position
				if (player != null) {
					pos.setSpielerId(player.getSpielerID());
				}
			}
		}

//		// Now fill the additional XYZ positions
//		for (int i = 0; (zusPos != null) && (vPlayer != null) && (i < zusPos.size()); i++) {
//			pos = (MatchRoleID) zusPos.elementAt(i);
//
//			// Ignore already assigned positions and substitutes
//			if ((pos.getSpielerId() > 0) || (pos.getId() >= IMatchRoleID.startReserves)) {
//				continue;
//			}
//
//			// nur diese Pos
//			if (pos.getPosition() == position) {
//				player = getBestSpieler(position, mitForm, ignoreVerletzung, ignoreSperre,
//                        vPlayer, positionen);
//
//				// position besetzen
//				if (player != null) {
//					pos.setSpielerId(player.getSpielerID());
//				}
//			}
//		}
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doSpielerAufstellenIdealPos(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Player> vPlayer,
			List<IMatchRoleID> positionen) {
		MatchRoleID pos = null;
		Player player = null;
		final Vector<IMatchRoleID> zusPos = new Vector<IMatchRoleID>();

		for (int i = 0; (positionen != null) && (vPlayer != null) && (i < positionen.size()); i++) {
			pos = (MatchRoleID) positionen.get(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() >= IMatchRoleID.startReserves)) {
				continue;
			}

			// nur exakte Position
			if (pos.getPosition() == position) {
				player = getBestSpielerIdealPosOnly(position, mitForm, ignoreVerletzung,
						ignoreSperre, vPlayer, positionen);

				// position besetzen
				if (player != null) {
					pos.setSpielerId(player.getSpielerID());
				}
			}
		}

		// nun die zus XYZ Positionen füllen
		for (int i = 0; (zusPos != null) && (vPlayer != null) && (i < zusPos.size()); i++) {
			pos = (MatchRoleID) zusPos.elementAt(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() >= IMatchRoleID.startReserves)) {
				continue;
			}

			// nur diese Pos
			if (pos.getPosition() == position) {
				player = getBestSpielerIdealPosOnly(position, mitForm, ignoreVerletzung,
						ignoreSperre, vPlayer, positionen);

				// position besetzen
				if (player != null) {
					pos.setSpielerId(player.getSpielerID());
				}
			}
		}
	}

	private float calcAveragePosValue(List<Player> spieler) {
		float average = 0.0f;
		Player player = null;

		if (spieler == null) {
			return 0.0f;
		}

		for (int i = 0; i < spieler.size(); i++) {
			player = ((Player) spieler.get(i));
			average += player.calcPosValue(player.getIdealPosition(), true);
		}

		average = core.util.Helper.round(average / spieler.size(), 2);

		return average;
	}

	private Vector<IMatchRoleID> filterPositions(List<IMatchRoleID> positions) {
		// Remove "red" positions from the position selection of the
		// AssistantPanel.
		Vector<IMatchRoleID> returnVec = new Vector<IMatchRoleID>();
		Map<Integer, Boolean> statusMap = HOMainFrame.instance()
				.getAufstellungsPanel().getAufstellungsAssistentPanel().getPositionStatuses();
		for (int i = 0; i < positions.size(); i++) {
			MatchRoleID pos = (MatchRoleID) positions.get(i);
			if ((!statusMap.containsKey(pos.getId())) || (statusMap.get(pos.getId()))) {
				returnVec.add(pos);
			} else {
			}
		}
		return returnVec;
	}

}
