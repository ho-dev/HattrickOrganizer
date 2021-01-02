package core.model.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import core.constants.TrainingType;
import core.datatype.CBItem;
import core.model.HOVerwaltung;
import core.util.HOLogger;
import module.lineup.Lineup;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class MatchRoleID implements java.io.Serializable, Comparable<IMatchRoleID>,
		IMatchRoleID {

	public enum Sector {
		None,
		Goal,
		Back,
		CentralDefence,
		Wing,
		InnerMidfield,
		Forward
	}


	/**
	 * Array with the constants (CBItems) for the positions, Without Exchanged
	 */
	public static final CBItem[] POSITIONEN = {
			new CBItem(MatchRoleID.getNameForPosition(UNKNOWN), UNKNOWN),
			new CBItem(MatchRoleID.getNameForPosition(UNSELECTABLE), UNSELECTABLE),
			new CBItem(MatchRoleID.getNameForPosition(KEEPER), KEEPER),
			new CBItem(MatchRoleID.getNameForPosition(CENTRAL_DEFENDER), CENTRAL_DEFENDER),
			new CBItem(MatchRoleID.getNameForPosition(CENTRAL_DEFENDER_OFF), CENTRAL_DEFENDER_OFF),
			new CBItem(MatchRoleID.getNameForPosition(CENTRAL_DEFENDER_TOWING), CENTRAL_DEFENDER_TOWING),
			new CBItem(MatchRoleID.getNameForPosition(BACK), BACK),
			new CBItem(MatchRoleID.getNameForPosition(BACK_OFF), BACK_OFF),
			new CBItem(MatchRoleID.getNameForPosition(BACK_DEF), BACK_DEF),
			new CBItem(MatchRoleID.getNameForPosition(BACK_TOMID), BACK_TOMID),
			new CBItem(MatchRoleID.getNameForPosition(MIDFIELDER), MIDFIELDER),
			new CBItem(MatchRoleID.getNameForPosition(MIDFIELDER_OFF), MIDFIELDER_OFF),
			new CBItem(MatchRoleID.getNameForPosition(MIDFIELDER_DEF), MIDFIELDER_DEF),
			new CBItem(MatchRoleID.getNameForPosition(MIDFIELDER_TOWING), MIDFIELDER_TOWING),
			new CBItem(MatchRoleID.getNameForPosition(WINGER), WINGER),
			new CBItem(MatchRoleID.getNameForPosition(WINGER_OFF), WINGER_OFF),
			new CBItem(MatchRoleID.getNameForPosition(WINGER_DEF), WINGER_DEF),
			new CBItem(MatchRoleID.getNameForPosition(WINGER_TOMID), WINGER_TOMID),
			new CBItem(MatchRoleID.getNameForPosition(FORWARD), FORWARD),
			new CBItem(MatchRoleID.getNameForPosition(FORWARD_DEF), FORWARD_DEF),
			new CBItem(MatchRoleID.getNameForPosition(FORWARD_TOWING), FORWARD_TOWING) };


	// ~ Instance fields
	// ----------------------------------------------------------------------------

	/** TaktikAnweisungen */
	@SerializedName("behaviour")
	@Expose
	private byte m_bTaktik = -1;

	/** ID */
	private int m_iId = -1;

	public static boolean isFieldMatchRoleId(int pos){return pos>=keeper && pos <=leftForward;}
	public boolean isFieldMatchRoleId(){ return isFieldMatchRoleId(this.m_iId); }
	public boolean isSubstitutesMatchRoleID () { return m_iId>=substGK1 && m_iId<= substXT1;}
	public boolean isBackupsMatchRoleID(){ return m_iId>=substGK2 && m_iId<= substXT2;}

	// It is much safer to have "empty" as 0, as it appears temp-players may
	// get ID -1 - Blaghaid
	/** welcher Player besetzt diese Position */
	@SerializedName("id")
	@Expose
	private int m_iSpielerId = 0;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new instance of MatchRoleID
	 */

	/* byte position, */
	public MatchRoleID(int id, int spielerId, byte taktik) {
		// m_bPosition = position;

		if ((id < IMatchRoleID.setPieces) && (id != -1)) {
			HOLogger.instance().debug(getClass(), "Old RoleID found in lineup: " + id);
		}

		m_iId = id;
		m_iSpielerId = spielerId;
		m_bTaktik = taktik;
	}

	/**
	 * Creates a new instance of MatchRoleID
	 */
	public MatchRoleID(MatchRoleID sp) {
		// m_bPosition = position;
		m_iId = sp.getId();
		m_iSpielerId = sp.getPlayerId();
		m_bTaktik = sp.getTactic();

		if ((m_iId < IMatchRoleID.setPieces) && (m_iId != -1)) {
			HOLogger.instance().debug(getClass(), "Old RoleID found in lineup: " + m_iId);
		}
	}

	// //////////////////Load/Save/////////////////

	/**
	 * Konstruktor lädt die MatchRoleID aus einem InputStream
	 *
	 * @param dis
	 *            Der InputStream aus dem gelesen wird
	 */
	public MatchRoleID(DataInputStream dis) {
		// DataInputStream dis = null;
		// byte data[] = null;
		try {
			// Einzulesenden Strom konvertieren
			// bais = new ByteArrayInputStream(data);
			// dis = new DataInputStream (bais);
			// Daten auslesen
			m_iId = dis.readInt();
			m_iSpielerId = dis.readInt();
			m_bTaktik = dis.readByte();

			// Und wieder schliessen
			// dis.close ();
		} catch (IOException ioe) {
			HOLogger.instance().log(getClass(), ioe);
		}
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * Returns a possible HT position ID for a HO position ID. Use only for
	 * loading the position image
	 */
	public static int getHTPosidForHOPosition4Image(byte posId) {
		switch (posId) {
		case KEEPER:
			return keeper;

		case CENTRAL_DEFENDER:
		case CENTRAL_DEFENDER_TOWING:
		case CENTRAL_DEFENDER_OFF:
			return rightCentralDefender;

		case BACK:
		case BACK_TOMID:
		case BACK_OFF:
		case BACK_DEF:
			return rightBack;

		case MIDFIELDER:
		case MIDFIELDER_OFF:
		case MIDFIELDER_DEF:
		case MIDFIELDER_TOWING:
			return rightInnerMidfield;

		case WINGER:
		case WINGER_TOMID:
		case WINGER_OFF:
		case WINGER_DEF:
			return rightWinger;

		case FORWARD:
		case FORWARD_TOWING:
		case FORWARD_DEF:
			return rightForward;

		case SUBSTITUTED1:
		case SUBSTITUTED2:
		case SUBSTITUTED3:
			return FirstPlayerReplaced;

		default: {
			HOLogger.instance().log(MatchRoleID.class, "Position not recognized: " + posId);
			return FirstPlayerReplaced;
		}
		}
	}

	/**
	 * Gibt das Kürzel für den Namen zurück
	 */
	public static String getShortNameForPosition(byte posId) {

		// HOLogger.instance().log(getClass(), "Unbestimmte Position: " +
		// posId );
		return switch (posId) {
			case KEEPER -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.keeper");
			case CENTRAL_DEFENDER -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefender");
			case CENTRAL_DEFENDER_TOWING -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefendertowardswing");
			case CENTRAL_DEFENDER_OFF -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefenderoffensive");
			case BACK -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingback");
			case BACK_TOMID -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbacktowardsmiddle");
			case BACK_OFF -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbackoffensive");
			case BACK_DEF -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbackdefensive");
			case MIDFIELDER -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielder");
			case MIDFIELDER_OFF -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielderoffensive");
			case MIDFIELDER_DEF -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielderdefensive");
			case MIDFIELDER_TOWING -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfieldertowardswing");
			case WINGER -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.winger");
			case WINGER_TOMID -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingertowardsmiddle");
			case WINGER_OFF -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingeroffensive");
			case WINGER_DEF -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingerdefensive");
			case FORWARD -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.forward");
			case FORWARD_TOWING -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.forwardtowardswing");
			case FORWARD_DEF -> HOVerwaltung.instance().getLanguageString("ls.player.position_short.forwarddefensive");
			case SUBSTITUTED1, SUBSTITUTED2, SUBSTITUTED3 -> HOVerwaltung.instance().getLanguageString("Ausgewechselt");
			default -> HOVerwaltung.instance().getLanguageString("Unbestimmt");
		};
	}

	/**
	 * Gibt zu einer Positionsid den Namen zurück
	 */
	public static String getNameForID(int id) {
		return switch (id) {
			case keeper -> "keeper";
			case rightBack -> "rightBack";
			case rightCentralDefender -> "rightCentralDefender";
			case leftCentralDefender -> "leftCentralDefender";
			case middleCentralDefender -> "middleCentralDefender";
			case leftBack -> "leftBack";
			case rightWinger -> "rightWinger";
			case rightInnerMidfield -> "rightInnerMidfield";
			case centralInnerMidfield -> "centralInnerMidfield";
			case leftInnerMidfield -> "leftInnerMidfield";
			case leftWinger -> "leftWinger";
			case rightForward -> "rightForward";
			case centralForward -> "centralForward";
			case leftForward -> "leftForward";
			case substCD1, substCD2 -> "substDefender";
			case substWB1, substWB2 -> "substWingback";
			case substIM1, substIM2 -> "substInsideMid";
			case substWI1, substWI2 -> "substWinger";
			case substGK1, substGK2 -> "substKeeper";
			case substFW1, substFW2 -> "substForward";
			case substXT1, substXT2 -> "substExtra";
			default -> "";
		};

	}



	/**
	 * Returns the name of a positionsid
	 */
	public static String getNameForPosition(byte posId) {

		return switch (posId) {
			case KEEPER -> getLangStr("ls.player.position.keeper");
			case CENTRAL_DEFENDER -> getLangStr("ls.player.position.centraldefender");
			case CENTRAL_DEFENDER_TOWING -> getLangStr("ls.player.position.centraldefendertowardswing");
			case CENTRAL_DEFENDER_OFF -> getLangStr("ls.player.position.centraldefenderoffensive");
			case BACK -> getLangStr("ls.player.position.wingback");
			case BACK_TOMID -> getLangStr("ls.player.position.wingbacktowardsmiddle");
			case BACK_OFF -> getLangStr("ls.player.position.wingbackoffensive");
			case BACK_DEF -> getLangStr("ls.player.position.wingbackdefensive");
			case MIDFIELDER -> getLangStr("ls.player.position.innermidfielder");
			case MIDFIELDER_OFF -> getLangStr("ls.player.position.innermidfielderoffensive");
			case MIDFIELDER_DEF -> getLangStr("ls.player.position.innermidfielderdefensive");
			case MIDFIELDER_TOWING -> getLangStr("ls.player.position.innermidfieldertowardswing");
			case WINGER -> getLangStr("ls.player.position.winger");
			case WINGER_TOMID -> getLangStr("ls.player.position.wingertowardsmiddle");
			case WINGER_OFF -> getLangStr("ls.player.position.wingeroffensive");
			case WINGER_DEF -> getLangStr("ls.player.position.wingerdefensive");
			case FORWARD -> getLangStr("ls.player.position.forward");
			case FORWARD_DEF -> getLangStr("ls.player.position.forwarddefensive");
			case FORWARD_DEF_TECH -> getLangStr("ls.player.position.forwarddefensivetechnical");
			case FORWARD_TOWING -> getLangStr("ls.player.position.forwardtowardswing");
			case EXTRA -> getLangStr("ls.player.position.extra_substitute");
			case SUBSTITUTED1, SUBSTITUTED2, SUBSTITUTED3 -> getLangStr("Ausgewechselt");
			case UNSELECTABLE -> getLangStr("Unselectable");
			default -> getLangStr("Unbestimmt");
		};
	}

	public static String getNameForPositionWithoutTactic(byte posId) {

		return switch (posId) {
			case KEEPER -> getLangStr("ls.player.position.keeper");
			case CENTRAL_DEFENDER, CENTRAL_DEFENDER_TOWING, CENTRAL_DEFENDER_OFF -> getLangStr("ls.player.position.centraldefender");
			case BACK, BACK_TOMID, BACK_OFF, BACK_DEF -> getLangStr("ls.player.position.wingback");
			case MIDFIELDER, MIDFIELDER_OFF, MIDFIELDER_DEF, MIDFIELDER_TOWING -> getLangStr("ls.player.position.innermidfielder");
			case WINGER, WINGER_TOMID, WINGER_OFF, WINGER_DEF -> getLangStr("ls.player.position.winger");
			case FORWARD, FORWARD_DEF, FORWARD_DEF_TECH, FORWARD_TOWING -> getLangStr("ls.player.position.forward");
			case EXTRA -> getLangStr("ls.player.position.extra_substitute");
			case SUBSTITUTED1, SUBSTITUTED2, SUBSTITUTED3 -> getLangStr("Ausgewechselt");
			case UNSELECTABLE -> getLangStr("Unselectable");
			default -> getLangStr("Unbestimmt");
		};
	}


	/**
	 * Return if position is full train
	 */
	public static boolean isFullTrainPosition(byte posId, int train) {

		if (train == TrainingType.SET_PIECES ||
				train == TrainingType.SHOOTING)
			return true;

		switch (posId) {
			case KEEPER:
				if (train == TrainingType.GOALKEEPING ||
						train == TrainingType.DEF_POSITIONS)
					return true;
				break;
			case CENTRAL_DEFENDER:
			case CENTRAL_DEFENDER_TOWING:
			case CENTRAL_DEFENDER_OFF:
				if (train == TrainingType.DEFENDING ||
						train == TrainingType.DEF_POSITIONS ||
						train == TrainingType.THROUGH_PASSES)
					return true;
				break;
			case BACK:
			case BACK_TOMID:
			case BACK_OFF:
			case BACK_DEF:
				if (train == TrainingType.DEF_POSITIONS ||
						train == TrainingType.THROUGH_PASSES ||
						train == TrainingType.DEFENDING)
					return true;
				break;
			case MIDFIELDER:
			case MIDFIELDER_OFF:
			case MIDFIELDER_DEF:
			case MIDFIELDER_TOWING:
				if (train == TrainingType.DEF_POSITIONS ||
						train == TrainingType.PLAYMAKING ||
						train == TrainingType.THROUGH_PASSES ||
						train == TrainingType.SHORT_PASSES)
					return true;
				break;
			case WINGER:
			case WINGER_TOMID:
			case WINGER_OFF:
			case WINGER_DEF:
				if (train == TrainingType.WING_ATTACKS ||
						train == TrainingType.DEF_POSITIONS ||
						train == TrainingType.CROSSING_WINGER ||
						train == TrainingType.THROUGH_PASSES ||
						train == TrainingType.SHORT_PASSES)
					return true;
				break;
			case FORWARD:
			case FORWARD_DEF:
			case FORWARD_TOWING:
				if (train == TrainingType.SCORING ||
						train == TrainingType.WING_ATTACKS ||
						train == TrainingType.SHORT_PASSES)
					return true;
				break;
		}
		return false;
	}

	/**
	 * Return if position is partial train
	 */
	public static boolean isPartialTrainPosition(byte posId, int train) {

		if (train == TrainingType.CROSSING_WINGER || train == TrainingType.PLAYMAKING) {
			switch (posId) {
				case BACK:
				case BACK_TOMID:
				case BACK_OFF:
				case BACK_DEF:
					if (train == TrainingType.CROSSING_WINGER)
						return true;
					break;
				case WINGER:
				case WINGER_TOMID:
				case WINGER_OFF:
				case WINGER_DEF:
					if (train == TrainingType.PLAYMAKING)
						return true;
					break;
			}
		}
		return false;
	}

	public byte getPosition() {
		return MatchRoleID.getPosition(m_iId, m_bTaktik);
	}

	/**
	 * Getter for property m_bPosition.
	 *
	 * @return Value of property m_bPosition.
	 */
	public static byte getPosition(int id, byte taktik) {
		switch (id) {
		case keeper:
			return KEEPER;

		case rightBack:
		case leftBack: {
			if (taktik == TOWARDS_MIDDLE) {
				return BACK_TOMID;
			} else if (taktik == OFFENSIVE) {
				return BACK_OFF;
			} else if (taktik == DEFENSIVE) {
				return BACK_DEF;
			} else {
				return BACK;
			}
		}

		case middleCentralDefender:
		case rightCentralDefender:
		case leftCentralDefender: {
			if (taktik == TOWARDS_WING) {
				return CENTRAL_DEFENDER_TOWING;
			} else if (taktik == OFFENSIVE) {
				return CENTRAL_DEFENDER_OFF;
			} else {
				return CENTRAL_DEFENDER;
			}
		}

		case rightWinger:
		case leftWinger: {
			if (taktik == TOWARDS_MIDDLE) {
				return WINGER_TOMID;
			} else if (taktik == OFFENSIVE) {
				return WINGER_OFF;
			} else if (taktik == DEFENSIVE) {
				return WINGER_DEF;
			} else {
				return WINGER;
			}
		}

		case centralInnerMidfield:
		case rightInnerMidfield:
		case leftInnerMidfield: {
			if (taktik == TOWARDS_WING) {
				return MIDFIELDER_TOWING;
			} else if (taktik == OFFENSIVE) {
				return MIDFIELDER_OFF;
			} else if (taktik == DEFENSIVE) {
				return MIDFIELDER_DEF;
			} else {
				return MIDFIELDER;
			}
		}

		case centralForward:
		case rightForward:
		case leftForward: {
			if (taktik == DEFENSIVE) {
				return FORWARD_DEF;
			} else if (taktik == TOWARDS_WING) {
				return FORWARD_TOWING;
			} else {
				return FORWARD;
			}
		}

		case substCD1:
		case substCD2:
			return CENTRAL_DEFENDER;

		case substWB1:
		case substWB2:
			return BACK;

		case substIM1:
		case substIM2:
			return MIDFIELDER;

		case substWI1:
		case substWI2:
			return WINGER;

		case substGK1:
		case substGK2:
			return KEEPER;

		case substFW1:
		case substFW2:
			return FORWARD;

		case substXT1:
		case substXT2:
			return EXTRA;
		}

		return UNKNOWN;
	}

	/**
	 * Setter for property m_iId.
	 *
	 * @param m_iId
	 *            New value of property m_iId.
	 */
	public final void setId(int m_iId) {
		this.m_iId = m_iId;
	}

	/**
	 * Getter for property m_iId.
	 *
	 * @return Value of property m_iId.
	 */
	public final int getId() {
		return m_iId;
	}

	/**
	 * returns an ID that can be sorted ( e.g. Player Overview Table )
	 *
	 */
	public final int getSortId() {
		int id = this.getPosition();

		if (id == IMatchRoleID.FORWARD_TOWING) {
			id = 18;
		}

		if (this.getId() >= IMatchRoleID.startReserves) {
			id += 20;
		}

		return id;
	}

	/**
	 * returns an ID that can be sorted (e.g.  * Player overview table
	 */
	public static int getSortId(byte position, boolean reserve) {
		int id = position;

		if (reserve) {
			id += 20;
		}

		return id;
	}

	/**
	 * Setter for property m_iSpielerId. This will fail if the current lineup of
	 * the HO model would end up with 12 players or more.
	 *
	 * @param spielerId
	 *            New value of property m_iSpielerId.
	 */
	public final void setSpielerId(int spielerId) {
		setSpielerId(spielerId, HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc());
	}


	/**
	 * Setter for property m_iSpielerId. This setter will fail if the provided
	 * lineup would end up with 12 players or more.
	 *
	 * @param spielerId
	 *            New value of property m_iSpielerId.
	 * @param lineup
	 *            The lineup that will be used to check for available space.
	 */
	public final void setSpielerId(int spielerId, Lineup lineup) {

		boolean containsPlayer = (m_iSpielerId > 0) || (m_iSpielerId < -10);
		boolean incomingEmpty = (spielerId < 1) && (spielerId > -10);

		// We don't want another player in the starting lineup if there are
		// already 11 on the field.

		if (!incomingEmpty && !containsPlayer && m_iId >= IMatchRoleID.startLineup
				&& m_iId < IMatchRoleID.startReserves && !lineup.hasFreePosition()) {
			HOLogger.instance().debug(getClass(),
					"Blocked from setting player at position: " + m_iSpielerId + " " + m_iId);
		} else {
			this.m_iSpielerId = spielerId;
		}
	}

	/**
	 * Getter for property m_iSpielerId.
	 *
	 * @return Value of property m_iSpielerId.
	 */
	public final int getPlayerId() {
		return m_iSpielerId;
	}

	/**
	 * Setter for property m_bTaktik.
	 *
	 * @param m_bTaktik
	 *            New value of property m_bTaktik.
	 */
	public final void setTaktik(byte m_bTaktik) {
		this.m_bTaktik = m_bTaktik;
	}

	/**
	 * Getter for property m_bTaktik.
	 *
	 * @return Value of property m_bTaktik.
	 */
	public final byte getTactic() {
		return m_bTaktik;
	}

	@Override
	public final int compareTo(IMatchRoleID obj) {
		if (obj instanceof MatchRoleID) {
			final MatchRoleID position = (MatchRoleID) obj;

			// Beide aufgestellt ?
			if ((this.getId() < IMatchRoleID.startReserves)
					&& (position.getId() < IMatchRoleID.startReserves)) {
				if (this.getPosition() < position.getPosition()) {
					return -1;
				} else if (this.getPosition() == position.getPosition()) {
					return 0;
				} else {
					return 1;
				}
			}
			// this aufgestellt ?
			else if ((this.getId() < IMatchRoleID.startReserves)
					&& (position.getId() >= IMatchRoleID.startReserves)) {
				return -1;
			}
			// position aufgestellt
			else if ((this.getId() >= IMatchRoleID.startReserves)
					&& (position.getId() < IMatchRoleID.startReserves)) {
				return 1;
			}
			// keiner aufgestellt
			else {
				if (this.getPosition() < position.getPosition()) {
					return -1;
				} else if (this.getPosition() == position.getPosition()) {
					return 0;
				} else {
					return 1;
				}
			}
		}

		return 0;
	}

	/*
	 * saved den Serverspieler
	 *
	 * @param baos Der Outputstream in den gesaved werden soll
	 *
	 * @return Byte Array der Daten die in den Output geschireben wurden
	 */
	public final void save(DataOutputStream das) {
		// ByteArrayOutputStream baos = null;
		// DataOutputStream das = null;
		// Byte Array
		// byte[] data = null;
		try {
			// Instanzen erzeugen
			// baos = new ByteArrayOutputStream();
			// das = new DataOutputStream(baos);
			// Daten schreiben in Strom
			das.writeInt(m_iId);
			das.writeInt(m_iSpielerId);
			das.writeByte(m_bTaktik);

			/*
			 * //Strom konvertieren in Byte data = baos.toByteArray();
			 * //Hilfsstrom schließen das.close ();
			 *
			 * return data;
			 */
		} catch (IOException ioe) {
			HOLogger.instance().log(getClass(), ioe);
		}

		// return data;
	}

	public static int convertOldRoleToNew(int roleID) {
		if(IMatchRoleID.oldKeeper.contains(roleID)) return IMatchRoleID.keeper;
		else if (IMatchRoleID.oldRightBack.contains(roleID)) return IMatchRoleID.rightBack;
		else if (IMatchRoleID.oldLeftCentralDefender.contains(roleID))return IMatchRoleID.leftCentralDefender;
		else if (IMatchRoleID.oldRightCentralDefender.contains(roleID))	return IMatchRoleID.rightCentralDefender;
		else if (IMatchRoleID.oldLeftBack.contains(roleID))	return IMatchRoleID.leftBack;
		else if (IMatchRoleID.oldRightWinger.contains(roleID)) return IMatchRoleID.rightWinger;
		else if (IMatchRoleID.oldRightInnerMidfielder.contains(roleID))	return IMatchRoleID.rightInnerMidfield;
		else if (IMatchRoleID.oldLeftInnerMidfielder.contains(roleID))return IMatchRoleID.leftInnerMidfield;
		else if (IMatchRoleID.oldLeftWinger.contains(roleID))return IMatchRoleID.leftWinger;
		else if (IMatchRoleID.oldRightForward.contains(roleID))return IMatchRoleID.rightForward;
		else if (IMatchRoleID.oldLeftForward.contains(roleID))return IMatchRoleID.leftForward;
		else if (IMatchRoleID.oldSubstKeeper.contains(roleID))return IMatchRoleID.substGK1;
		else if (IMatchRoleID.oldSubstDefender.contains(roleID))return IMatchRoleID.substCD1;
		else if (IMatchRoleID.oldSubstMidfielder.contains(roleID))return IMatchRoleID.substIM1;
		else if (IMatchRoleID.oldSubstWinger.contains(roleID))return IMatchRoleID.substWI1;
		else if (IMatchRoleID.oldSubstForward.contains(roleID))	return IMatchRoleID.substFW1;
		else return roleID;
	}

	public static Properties convertOldRoleToNew(Properties oldLineupProperties) {
		Properties result = new Properties();
		String sKey;

		// mapping conversion OldRole -> New Rolw
		HashMap<String, String> mappingTable = new HashMap<>();
		mappingTable.put("behrightback","order_rightback");
		mappingTable.put("behleftback","order_leftback");
		mappingTable.put("insideback1","rightcentraldefender");
		mappingTable.put("behinsideback1","order_rightcentraldefender");
		mappingTable.put("insideback2","leftcentraldefender");
		mappingTable.put("behinsideback2","order_leftcentraldefender");
		mappingTable.put("insideback3","middlecentraldefender");
		mappingTable.put("behinsideback3","order_middlecentraldefender");
		mappingTable.put("behrightwinger","order_rightwinger");
		mappingTable.put("behleftwinger","order_leftwinger");
		mappingTable.put("insidemid1","rightinnermidfield");
		mappingTable.put("behinsidemid1","order_rightinnermidfield");
		mappingTable.put("insidemid2","leftinnermidfield");
		mappingTable.put("behinsidemid2","order_leftinnermidfield");
		mappingTable.put("insidemid3","middleinnermidfield");
		mappingTable.put("behinsidemid3","order_centralinnermidfield");
		mappingTable.put("forward1","rightforward");
		mappingTable.put("behforward1","order_rightforward");
		mappingTable.put("forward2","leftforward");
		mappingTable.put("behforward2","order_leftforward");
		mappingTable.put("forward3","centralforward");
		mappingTable.put("behforward3","order_centralforward");
		mappingTable.put("substback","substcd1");
		mappingTable.put("substinsidemid","substim1");
		mappingTable.put("substwinger","substwi1");
		mappingTable.put("substkeeper","substgk1");
		mappingTable.put("substforward","substfw1");


		for (Map.Entry<Object, Object> entry : oldLineupProperties.entrySet()) {
			sKey = entry.getKey().toString();
			result.setProperty(mappingTable.getOrDefault(sKey, sKey), entry.getValue().toString());
			}

		return result;

	}

	private static String getLangStr(String key) {return HOVerwaltung.instance().getLanguageString(key);}

	public Sector getSector(){
		return getSector(this.m_iId);
	}
	public static Sector getSector(int roleId) {
		return switch (roleId) {
			case keeper -> Sector.Goal;
			case leftBack, rightBack -> Sector.Back;
			case leftCentralDefender, rightCentralDefender, middleCentralDefender -> Sector.CentralDefence;
			case leftWinger, rightWinger -> Sector.Wing;
			case leftInnerMidfield, rightInnerMidfield, centralInnerMidfield -> Sector.InnerMidfield;
			case leftForward, rightForward, centralForward -> Sector.Forward;
			default -> Sector.None;
		};
	}
	
}
