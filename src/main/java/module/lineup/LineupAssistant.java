package module.lineup;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.match.Weather;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.model.player.SpielerPosition;

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
	/** gibt an wie auf Wettereinfluss reagiert werden soll in Prozent */
	private float m_fWetterBonus = 0.2f;
	/** gibt das Wetter an */
	private Weather weather = Weather.PARTIALLY_CLOUDY;

	/**
	 * gibt an ob der Spieler bereits aufgestellt ist auch ReserveBank zählt mit
	 */
	public final boolean isSpielerAufgestellt(int spielerId, List<ISpielerPosition> positions) {
		if (positions != null) {
			for (ISpielerPosition position : positions) {
				if (((SpielerPosition) position).getSpielerId() == spielerId) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * gibt an ob der Spieler von beginn an Spielt
	 */
	public final boolean isSpielerInAnfangsElf(int spielerId, Vector<ISpielerPosition> positionen) {
		for (int i = 0; (positionen != null) && (i < positionen.size()); i++) {
			if ((((SpielerPosition) positionen.elementAt(i)).getId() < ISpielerPosition.startReserves)
					&& (((SpielerPosition) positionen.elementAt(i)).getSpielerId() == spielerId)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * übernimmt das automatische Aufstellen
	 * 
	 * @param positionen
	 *            die zu besetzenden Positionen
	 * @param spieler
	 *            die aufszustellenden Spieler
	 * @param reihenfolge
	 *            Reihenfolge in der die Mannschaftsteile besetzt werden sollen
	 * @param mitForm
	 *            Formberücksichtigung
	 * @param idealPosFirst
	 *            IdealPosition berücksichtigen ?
	 * @param ignoreVerletzung
	 *            auch Verletzte aufstellen ?
	 * @param ignoreSperre
	 *            auch gesperrte aufstellen ?
	 * @param wetterBonus
	 *            Schwellwert der angibt an wie auf WetterEffekte reagiert
	 *            werden soll
	 * @param wetter
	 *            das aktuelle Wetter
	 */
	public final void doAufstellung(List<ISpielerPosition> positionen, List<Spieler> spieler,
			byte reihenfolge, boolean mitForm, boolean idealPosFirst, boolean ignoreVerletzung,
			boolean ignoreSperre, float wetterBonus, Weather weather) {
		// m_vPositionen = new Vector ( positionen );
		// m_vSpieler = new Vector( spieler );

		positionen = filterPositions(positionen);

		m_fWetterBonus = wetterBonus;
		this.weather = weather;

		// nur spieler auf idealpos aufstellen
		if (idealPosFirst) {
			// Wert speichern
			float backup = core.model.UserParameter.instance().MinIdealPosStk;

			// Maimum von beiden für Berechnung verwenden
			core.model.UserParameter.instance().MinIdealPosStk = Math.max(
					calcAveragePosValue(spieler),
					core.model.UserParameter.instance().MinIdealPosStk);

			doSpielerAufstellenIdealPos(ISpielerPosition.KEEPER, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.CENTRAL_DEFENDER, mitForm,
					ignoreVerletzung, ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.CENTRAL_DEFENDER_TOWING, mitForm,
					ignoreVerletzung, ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.CENTRAL_DEFENDER_OFF, mitForm,
					ignoreVerletzung, ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.BACK, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.BACK_TOMID, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.BACK_OFF, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.BACK_DEF, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.MIDFIELDER, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.MIDFIELDER_OFF, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.MIDFIELDER_DEF, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.MIDFIELDER_TOWING, mitForm,
					ignoreVerletzung, ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.WINGER, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.WINGER_OFF, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.WINGER_DEF, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.WINGER_TOMID, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.FORWARD, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.FORWARD_DEF, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
			doSpielerAufstellenIdealPos(ISpielerPosition.FORWARD_TOWING, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);

			// Wert wieder zurücksetzen
			core.model.UserParameter.instance().MinIdealPosStk = backup;
		}

		// falls tw unbesetzt
		doSpielerAufstellen(ISpielerPosition.KEEPER, mitForm, ignoreVerletzung, ignoreSperre,
				spieler, positionen);

		byte[] order = null;
		// nun reihenfolge beachten und unbesetzte füllen
		switch (reihenfolge) {
		case AW_MF_ST:

			order = new byte[18];
			// DEFENCE
			order[0] = ISpielerPosition.CENTRAL_DEFENDER;
			order[1] = ISpielerPosition.CENTRAL_DEFENDER_TOWING;
			order[2] = ISpielerPosition.CENTRAL_DEFENDER_OFF;
			order[3] = ISpielerPosition.BACK;
			order[4] = ISpielerPosition.BACK_DEF;
			order[5] = ISpielerPosition.BACK_OFF;
			order[6] = ISpielerPosition.BACK_TOMID;
			// MIDFIELD
			order[7] = ISpielerPosition.MIDFIELDER;
			order[8] = ISpielerPosition.MIDFIELDER_OFF;
			order[9] = ISpielerPosition.MIDFIELDER_DEF;
			order[10] = ISpielerPosition.MIDFIELDER_TOWING;
			order[11] = ISpielerPosition.WINGER;
			order[12] = ISpielerPosition.WINGER_DEF;
			order[13] = ISpielerPosition.WINGER_OFF;
			order[14] = ISpielerPosition.WINGER_TOMID;
			// FORWARD
			order[15] = ISpielerPosition.FORWARD;
			order[16] = ISpielerPosition.FORWARD_DEF;
			order[17] = ISpielerPosition.FORWARD_TOWING;
			break;

		case AW_ST_MF:

			order = new byte[18];
			// DEFENCE
			order[0] = ISpielerPosition.CENTRAL_DEFENDER;
			order[1] = ISpielerPosition.CENTRAL_DEFENDER_TOWING;
			order[2] = ISpielerPosition.CENTRAL_DEFENDER_OFF;
			order[3] = ISpielerPosition.BACK;
			order[4] = ISpielerPosition.BACK_DEF;
			order[5] = ISpielerPosition.BACK_OFF;
			order[6] = ISpielerPosition.BACK_TOMID;
			// FORWARD
			order[7] = ISpielerPosition.FORWARD;
			order[8] = ISpielerPosition.FORWARD_DEF;
			order[9] = ISpielerPosition.FORWARD_TOWING;

			// MIDFIELD
			order[10] = ISpielerPosition.MIDFIELDER;
			order[11] = ISpielerPosition.MIDFIELDER_OFF;
			order[12] = ISpielerPosition.MIDFIELDER_DEF;
			order[13] = ISpielerPosition.MIDFIELDER_TOWING;
			order[14] = ISpielerPosition.WINGER;
			order[15] = ISpielerPosition.WINGER_DEF;
			order[16] = ISpielerPosition.WINGER_OFF;
			order[17] = ISpielerPosition.WINGER_TOMID;

			break;

		case MF_AW_ST:

			order = new byte[18];

			// MIDFIELD
			order[0] = ISpielerPosition.MIDFIELDER;
			order[1] = ISpielerPosition.MIDFIELDER_OFF;
			order[2] = ISpielerPosition.MIDFIELDER_DEF;
			order[3] = ISpielerPosition.MIDFIELDER_TOWING;
			order[4] = ISpielerPosition.WINGER;
			order[5] = ISpielerPosition.WINGER_DEF;
			order[6] = ISpielerPosition.WINGER_OFF;
			order[7] = ISpielerPosition.WINGER_TOMID;
			// DEFENCE
			order[8] = ISpielerPosition.CENTRAL_DEFENDER;
			order[9] = ISpielerPosition.CENTRAL_DEFENDER_TOWING;
			order[10] = ISpielerPosition.CENTRAL_DEFENDER_OFF;
			order[11] = ISpielerPosition.BACK;
			order[12] = ISpielerPosition.BACK_DEF;
			order[13] = ISpielerPosition.BACK_OFF;
			order[14] = ISpielerPosition.BACK_TOMID;
			// FORWARD
			order[15] = ISpielerPosition.FORWARD;
			order[16] = ISpielerPosition.FORWARD_DEF;
			order[17] = ISpielerPosition.FORWARD_TOWING;
			break;

		case MF_ST_AW:

			order = new byte[18];

			// MIDFIELD
			order[0] = ISpielerPosition.MIDFIELDER;
			order[1] = ISpielerPosition.MIDFIELDER_OFF;
			order[2] = ISpielerPosition.MIDFIELDER_DEF;
			order[3] = ISpielerPosition.MIDFIELDER_TOWING;
			order[4] = ISpielerPosition.WINGER;
			order[5] = ISpielerPosition.WINGER_DEF;
			order[6] = ISpielerPosition.WINGER_OFF;
			order[7] = ISpielerPosition.WINGER_TOMID;
			// FORWARD
			order[8] = ISpielerPosition.FORWARD;
			order[9] = ISpielerPosition.FORWARD_DEF;
			order[10] = ISpielerPosition.FORWARD_TOWING;
			// DEFENCE
			order[11] = ISpielerPosition.CENTRAL_DEFENDER;
			order[12] = ISpielerPosition.CENTRAL_DEFENDER_TOWING;
			order[13] = ISpielerPosition.CENTRAL_DEFENDER_OFF;
			order[14] = ISpielerPosition.BACK;
			order[15] = ISpielerPosition.BACK_DEF;
			order[16] = ISpielerPosition.BACK_OFF;
			order[17] = ISpielerPosition.BACK_TOMID;

			break;

		case ST_MF_AW:

			order = new byte[18];

			// FORWARD
			order[0] = ISpielerPosition.FORWARD;
			order[1] = ISpielerPosition.FORWARD_DEF;
			order[2] = ISpielerPosition.FORWARD_TOWING;
			// MIDFIELD
			order[3] = ISpielerPosition.MIDFIELDER;
			order[4] = ISpielerPosition.MIDFIELDER_OFF;
			order[5] = ISpielerPosition.MIDFIELDER_DEF;
			order[6] = ISpielerPosition.MIDFIELDER_TOWING;
			order[7] = ISpielerPosition.WINGER;
			order[8] = ISpielerPosition.WINGER_DEF;
			order[9] = ISpielerPosition.WINGER_OFF;
			order[10] = ISpielerPosition.WINGER_TOMID;

			// DEFENCE
			order[11] = ISpielerPosition.CENTRAL_DEFENDER;
			order[12] = ISpielerPosition.CENTRAL_DEFENDER_TOWING;
			order[13] = ISpielerPosition.CENTRAL_DEFENDER_OFF;
			order[14] = ISpielerPosition.BACK;
			order[15] = ISpielerPosition.BACK_DEF;
			order[16] = ISpielerPosition.BACK_OFF;
			order[17] = ISpielerPosition.BACK_TOMID;
			break;

		case ST_AW_MF:

			order = new byte[18];
			// FORWARD
			order[0] = ISpielerPosition.FORWARD;
			order[1] = ISpielerPosition.FORWARD_DEF;
			order[2] = ISpielerPosition.FORWARD_TOWING;
			// DEFENCE
			order[3] = ISpielerPosition.CENTRAL_DEFENDER;
			order[4] = ISpielerPosition.CENTRAL_DEFENDER_TOWING;
			order[5] = ISpielerPosition.CENTRAL_DEFENDER_OFF;
			order[6] = ISpielerPosition.BACK;
			order[7] = ISpielerPosition.BACK_DEF;
			order[8] = ISpielerPosition.BACK_OFF;
			order[9] = ISpielerPosition.BACK_TOMID;
			// MIDFIELD
			order[10] = ISpielerPosition.MIDFIELDER;
			order[11] = ISpielerPosition.MIDFIELDER_OFF;
			order[12] = ISpielerPosition.MIDFIELDER_DEF;
			order[13] = ISpielerPosition.MIDFIELDER_TOWING;
			order[14] = ISpielerPosition.WINGER;
			order[15] = ISpielerPosition.WINGER_DEF;
			order[16] = ISpielerPosition.WINGER_OFF;
			order[17] = ISpielerPosition.WINGER_TOMID;

			break;

		default:
			return;

			// break;
		}

		if (order != null) {
			for (int i = 0; i < order.length; i++) {
				doSpielerAufstellen(order[i], mitForm, ignoreVerletzung, ignoreSperre, spieler,
						positionen);
			}
		}

		// ReserveSpieler besetzen
		// Reserve
		if (idealPosFirst) {
			// TW
			doReserveSpielerAufstellenIdealPos(ISpielerPosition.KEEPER, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);

			// abwehr
			doReserveSpielerAufstellenIdealPos(ISpielerPosition.CENTRAL_DEFENDER, mitForm,
					ignoreVerletzung, ignoreSperre, spieler, positionen);

			// mittelfeld
			doReserveSpielerAufstellenIdealPos(ISpielerPosition.MIDFIELDER, mitForm,
					ignoreVerletzung, ignoreSperre, spieler, positionen);
			doReserveSpielerAufstellenIdealPos(ISpielerPosition.WINGER, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);

			// sturm
			doReserveSpielerAufstellenIdealPos(ISpielerPosition.FORWARD, mitForm, ignoreVerletzung,
					ignoreSperre, spieler, positionen);
		}

		// und nochmal für alle unbesetzten
		// TW
		doReserveSpielerAufstellen(ISpielerPosition.KEEPER, mitForm, ignoreVerletzung,
				ignoreSperre, spieler, positionen);

		// abwehr
		doReserveSpielerAufstellen(ISpielerPosition.CENTRAL_DEFENDER, mitForm, ignoreVerletzung,
				ignoreSperre, spieler, positionen);

		// mittelfeld
		doReserveSpielerAufstellen(ISpielerPosition.MIDFIELDER, mitForm, ignoreVerletzung,
				ignoreSperre, spieler, positionen);
		doReserveSpielerAufstellen(ISpielerPosition.WINGER, mitForm, ignoreVerletzung,
				ignoreSperre, spieler, positionen);

		// sturm
		doReserveSpielerAufstellen(ISpielerPosition.FORWARD, mitForm, ignoreVerletzung,
				ignoreSperre, spieler, positionen);
	}

	/**
	 * resetet alle Verbindungen zwischen Position und Spieler
	 * 
	 * @param positionen
	 *            a vector of player positions
	 */
	public final void resetPositionsbesetzungen(Vector<ISpielerPosition> positionen) {
		for (int i = 0; (positionen != null) && (i < positionen.size()); i++) {
			((core.model.player.SpielerPosition) positionen.elementAt(i)).setSpielerId(0);
		}
	}
	
	/**
	 * Resets the orders for all positions to normal
	 * @param positions
	 * 		a vector of player positions
	 */
	public final void resetPositionOrders(Vector<ISpielerPosition> positions) {
		if (positions != null) {
			for (int i = 0; i < positions.size(); i++) {
				((core.model.player.SpielerPosition) positions.elementAt(i)).setTaktik((byte) 0);
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
		List<Spieler> players = HOVerwaltung.instance().getModel().getAllSpieler();
		for (Spieler player : players) {
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
	protected final core.model.player.Spieler getBestSpieler(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Spieler> vSpieler,
			List<ISpielerPosition> positionen) {
		core.model.player.Spieler spieler = null;
		core.model.player.Spieler bestSpieler = null;
		float bestStk = -1.0f;
		float aktuStk = 0.0f;

		for (int i = 0; (vSpieler != null) && (i < vSpieler.size()); i++) {
			spieler = (core.model.player.Spieler) vSpieler.get(i);

			// stk inklusive Wetter effekt errechnen
			aktuStk = spieler.calcPosValue(position, mitForm);
			aktuStk += (m_fWetterBonus * spieler.getWetterEffekt(this.weather) * aktuStk);

			if ((!isSpielerAufgestellt(spieler.getSpielerID(), positionen))
					&& ((bestSpieler == null) || (bestStk < aktuStk))
					&& ((ignoreSperre) || (!spieler.isGesperrt()))
					&& ((ignoreVerletzung) || (spieler.getVerletzt() < 1))
					&& (spieler.isSpielberechtigt())) {
				bestSpieler = spieler;
				bestStk = aktuStk;
			}
		}

		return bestSpieler;
	}

	/**
	 * liefert aus dem eigenen Vector mit Spielern den besten für die
	 * angefordertet Position der noch nicht aufgestellt ist
	 */
	protected final Spieler getBestSpielerIdealPosOnly(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Spieler> vSpieler,
			List<ISpielerPosition> positionen) {
		Spieler spieler = null;
		Spieler bestSpieler = null;
		float bestStk = -1.0f;
		float aktuStk = 0.0f;

		for (int i = 0; (vSpieler != null) && (i < vSpieler.size()); i++) {
			// Spieler holen
			spieler = (Spieler) vSpieler.get(i);

			// stk inklusive Wetter effekt errechnen
			aktuStk = spieler.calcPosValue(position, mitForm);
			aktuStk += (m_fWetterBonus * spieler.getWetterEffekt(this.weather) * aktuStk);

			// Idealpos STK muss > mindestwert sein
			if ((!isSpielerAufgestellt(spieler.getSpielerID(), positionen))
					&& (spieler.getIdealPosition() == position)
					&& ((bestSpieler == null) || (bestStk < aktuStk))
					&& ((ignoreSperre) || (!spieler.isGesperrt()))
					&& ((ignoreVerletzung) || (spieler.getVerletzt() < 1))
					&& (aktuStk > core.model.UserParameter.instance().MinIdealPosStk)
					&& (!spieler.isTrainer()) && (spieler.isSpielberechtigt())) {
				bestSpieler = spieler;
				bestStk = aktuStk;
			}
		}

		return bestSpieler;
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doReserveSpielerAufstellen(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Spieler> vSpieler,
			List<ISpielerPosition> positionen) {
		SpielerPosition pos = null;
		Spieler spieler = null;

		for (int i = 0; (positionen != null) && (vSpieler != null) && (i < positionen.size()); i++) {
			pos = (SpielerPosition) positionen.get(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() < ISpielerPosition.startReserves)) {
				continue;
			}

			// nur exacte Pos
			if (pos.getPosition() == position) {
				spieler = getBestSpieler(position, mitForm, ignoreVerletzung, ignoreSperre,
						vSpieler, positionen);

				// position besetzen
				if (spieler != null) {
					pos.setSpielerId(spieler.getSpielerID());
				}
			}
		}
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doReserveSpielerAufstellenIdealPos(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Spieler> vSpieler,
			List<ISpielerPosition> positionen) {
		SpielerPosition pos = null;
		Spieler spieler = null;

		for (int i = 0; (positionen != null) && (vSpieler != null) && (i < positionen.size()); i++) {
			pos = (core.model.player.SpielerPosition) positionen.get(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() < ISpielerPosition.startReserves)) {
				continue;
			}

			// nur exakte Position
			if (pos.getPosition() == position) {
				spieler = getBestSpielerIdealPosOnly(position, mitForm, ignoreVerletzung,
						ignoreSperre, vSpieler, positionen);

				// position besetzen
				if (spieler != null) {
					pos.setSpielerId(spieler.getSpielerID());
				}
			}
		}
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doSpielerAufstellen(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Spieler> vSpieler,
			List<ISpielerPosition> positionen) {
		SpielerPosition pos = null;
		Spieler spieler = null;
		final Vector<ISpielerPosition> zusPos = new Vector<ISpielerPosition>();

		for (int i = 0; (positionen != null) && (vSpieler != null) && (i < positionen.size()); i++) {
			pos = (SpielerPosition) positionen.get(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() >= ISpielerPosition.startReserves)) {
				continue;
			}

			// nur exacte Pos
			if (pos.getPosition() == position) {
				spieler = getBestSpieler(position, mitForm, ignoreVerletzung, ignoreSperre,
						vSpieler, positionen);

				// position besetzen
				if (spieler != null) {
					pos.setSpielerId(spieler.getSpielerID());
				}
			}
		}

		// nun die zus XYZ Positionen füllen
		for (int i = 0; (zusPos != null) && (vSpieler != null) && (i < zusPos.size()); i++) {
			pos = (SpielerPosition) zusPos.elementAt(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() >= ISpielerPosition.startReserves)) {
				continue;
			}

			// nur diese Pos
			if (pos.getPosition() == position) {
				spieler = getBestSpieler(position, mitForm, ignoreVerletzung, ignoreSperre,
						vSpieler, positionen);

				// position besetzen
				if (spieler != null) {
					pos.setSpielerId(spieler.getSpielerID());
				}
			}
		}
	}

	/**
	 * besetzt die Torwart Positionen im Vector m_vPositionen
	 */
	protected final void doSpielerAufstellenIdealPos(byte position, boolean mitForm,
			boolean ignoreVerletzung, boolean ignoreSperre, List<Spieler> vSpieler,
			List<ISpielerPosition> positionen) {
		SpielerPosition pos = null;
		Spieler spieler = null;
		final Vector<ISpielerPosition> zusPos = new Vector<ISpielerPosition>();

		for (int i = 0; (positionen != null) && (vSpieler != null) && (i < positionen.size()); i++) {
			pos = (SpielerPosition) positionen.get(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() >= ISpielerPosition.startReserves)) {
				continue;
			}

			// nur exakte Position
			if (pos.getPosition() == position) {
				spieler = getBestSpielerIdealPosOnly(position, mitForm, ignoreVerletzung,
						ignoreSperre, vSpieler, positionen);

				// position besetzen
				if (spieler != null) {
					pos.setSpielerId(spieler.getSpielerID());
				}
			}
		}

		// nun die zus XYZ Positionen füllen
		for (int i = 0; (zusPos != null) && (vSpieler != null) && (i < zusPos.size()); i++) {
			pos = (SpielerPosition) zusPos.elementAt(i);

			// bereits vergebene Positionen ignorieren und ReserveBank leer
			// lassen
			if ((pos.getSpielerId() > 0) || (pos.getId() >= ISpielerPosition.startReserves)) {
				continue;
			}

			// nur diese Pos
			if (pos.getPosition() == position) {
				spieler = getBestSpielerIdealPosOnly(position, mitForm, ignoreVerletzung,
						ignoreSperre, vSpieler, positionen);

				// position besetzen
				if (spieler != null) {
					pos.setSpielerId(spieler.getSpielerID());
				}
			}
		}
	}

	private float calcAveragePosValue(List<Spieler> spieler) {
		float average = 0.0f;
		Spieler player = null;

		if (spieler == null) {
			return 0.0f;
		}

		for (int i = 0; i < spieler.size(); i++) {
			player = ((Spieler) spieler.get(i));
			average += player.calcPosValue(player.getIdealPosition(), true);
		}

		average = core.util.Helper.round(average / spieler.size(), 2);

		return average;
	}

	private Vector<ISpielerPosition> filterPositions(List<ISpielerPosition> positions) {
		// Remove "red" positions from the position selection of the
		// AssistantPanel.
		Vector<ISpielerPosition> returnVec = new Vector<ISpielerPosition>();
		Map<Integer, Boolean> statusMap = HOMainFrame.instance()
				.getAufstellungsPanel().getAufstellungsAssistentPanel().getPositionStatuses();
		for (int i = 0; i < positions.size(); i++) {
			SpielerPosition pos = (SpielerPosition) positions.get(i);
			if ((!statusMap.containsKey(pos.getId())) || (statusMap.get(pos.getId()))) {
				returnVec.add(pos);
			} else {
			}
		}
		return returnVec;
	}

}
