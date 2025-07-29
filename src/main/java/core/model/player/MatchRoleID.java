package core.model.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import core.constants.TrainingType;
import core.datatype.CBItem;
import core.db.AbstractTable;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.util.HOLogger;
import module.lineup.Lineup;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class MatchRoleID extends AbstractTable.Storable implements java.io.Serializable, Comparable<IMatchRoleID>,
		IMatchRoleID {

	public enum Sector {
		None,
		Goal,
		Back,
		CentralDefence,
		Wing,
		InnerMidfield,
		Forward,
		SetPiecesTaker
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
	private byte behaviour = -1;

	/** ID */
	private int m_iId = -1;

	public static boolean isFieldMatchRoleId(int pos){return pos>=keeper && pos <=leftForward;}
	public boolean isFieldMatchRoleId(){ return isFieldMatchRoleId(this.m_iId); }
	public boolean isSubstitutesMatchRoleId() { return m_iId>=substGK1 && m_iId<= substXT1;}
	public boolean isBackupsMatchRoleId(){ return m_iId>=substGK2 && m_iId<= substXT2;}
	public boolean isPenaltyTakerMatchRoleId(){ return m_iId>=penaltyTaker1 && m_iId<=penaltyTaker11;}
	public boolean isReplacedMatchRoleId() { return m_iId>=FirstPlayerReplaced && m_iId<=ThirdPlayerReplaced; }
	public boolean isRedCardedMatchRoleId() { return m_iId>=redCardedPlayer1 && m_iId<=redCardedPlayer3; }

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
	public MatchRoleID(int id, int playerId, byte behaviour) {
		// m_bPosition = position;

		if ((id < IMatchRoleID.setPieces) && (id != -1) && (id != 0)) {
			HOLogger.instance().debug(getClass(), "Old RoleID found in lineup: " + id);
		}

		m_iId = id;
		m_iSpielerId = playerId;
		this.behaviour = behaviour;
	}

	/**
	 * Creates a new instance of MatchRoleID
	 */
	public MatchRoleID(MatchRoleID sp) {
		// m_bPosition = position;
		m_iId = sp.getId();
		m_iSpielerId = sp.getPlayerId();
		behaviour = sp.getTactic();

		if ((m_iId < IMatchRoleID.setPieces) && (m_iId != -1)) {
			HOLogger.instance().debug(getClass(), "Old RoleID found in lineup: " + m_iId);
		}
	}

	public MatchRoleID(){}

	/**
	 * Returns a possible HT position ID for a HO position ID. Use only for
	 * loading the position image
	 */
	public static int getHTPosidForHOPosition4Image(byte posId) {
        switch (posId) {
            case KEEPER -> {
                return keeper;
            }
            case CENTRAL_DEFENDER, CENTRAL_DEFENDER_TOWING, CENTRAL_DEFENDER_OFF -> {
                return rightCentralDefender;
            }
            case BACK, BACK_TOMID, BACK_OFF, BACK_DEF -> {
                return rightBack;
            }
            case MIDFIELDER, MIDFIELDER_OFF, MIDFIELDER_DEF, MIDFIELDER_TOWING -> {
                return rightInnerMidfield;
            }
            case WINGER, WINGER_TOMID, WINGER_OFF, WINGER_DEF -> {
                return rightWinger;
            }
            case FORWARD, FORWARD_TOWING, FORWARD_DEF -> {
                return rightForward;
            }
			case SUBSTITUTED1, SUBSTITUTED2, SUBSTITUTED3 -> {
				return FirstPlayerReplaced;
			}
			case REDCARDED1, REDCARDED2, REDCARDED3 -> {
				return redCardedPlayer1;
			}
            default -> {
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
			case KEEPER -> TranslationFacility.tr("ls.player.position_short.keeper");
			case CENTRAL_DEFENDER -> TranslationFacility.tr("ls.player.position_short.centraldefender");
			case CENTRAL_DEFENDER_TOWING -> TranslationFacility.tr("ls.player.position_short.centraldefendertowardswing");
			case CENTRAL_DEFENDER_OFF -> TranslationFacility.tr("ls.player.position_short.centraldefenderoffensive");
			case BACK -> TranslationFacility.tr("ls.player.position_short.wingback");
			case BACK_TOMID -> TranslationFacility.tr("ls.player.position_short.wingbacktowardsmiddle");
			case BACK_OFF -> TranslationFacility.tr("ls.player.position_short.wingbackoffensive");
			case BACK_DEF -> TranslationFacility.tr("ls.player.position_short.wingbackdefensive");
			case MIDFIELDER -> TranslationFacility.tr("ls.player.position_short.innermidfielder");
			case MIDFIELDER_OFF -> TranslationFacility.tr("ls.player.position_short.innermidfielderoffensive");
			case MIDFIELDER_DEF -> TranslationFacility.tr("ls.player.position_short.innermidfielderdefensive");
			case MIDFIELDER_TOWING -> TranslationFacility.tr("ls.player.position_short.innermidfieldertowardswing");
			case WINGER -> TranslationFacility.tr("ls.player.position_short.winger");
			case WINGER_TOMID -> TranslationFacility.tr("ls.player.position_short.wingertowardsmiddle");
			case WINGER_OFF -> TranslationFacility.tr("ls.player.position_short.wingeroffensive");
			case WINGER_DEF -> TranslationFacility.tr("ls.player.position_short.wingerdefensive");
			case FORWARD -> TranslationFacility.tr("ls.player.position_short.forward");
			case FORWARD_TOWING -> TranslationFacility.tr("ls.player.position_short.forwardtowardswing");
			case FORWARD_DEF -> TranslationFacility.tr("ls.player.position_short.forwarddefensive");
			case SUBSTITUTED1, SUBSTITUTED2, SUBSTITUTED3 -> TranslationFacility.tr("Ausgewechselt");
			case REDCARDED1, REDCARDED2, REDCARDED3 -> TranslationFacility.tr("ls.player.position.short.red_carded");
			default -> TranslationFacility.tr("Unbestimmt");
		};
	}

	/**
	 * Returns the name of a positionsid
	 */
	public static String getNameForPosition(byte posId) {

		return switch (posId) {
			case KEEPER -> TranslationFacility.tr("ls.player.position.keeper");
			case CENTRAL_DEFENDER -> TranslationFacility.tr("ls.player.position.centraldefender");
			case CENTRAL_DEFENDER_TOWING -> TranslationFacility.tr("ls.player.position.centraldefendertowardswing");
			case CENTRAL_DEFENDER_OFF -> TranslationFacility.tr("ls.player.position.centraldefenderoffensive");
			case BACK -> TranslationFacility.tr("ls.player.position.wingback");
			case BACK_TOMID -> TranslationFacility.tr("ls.player.position.wingbacktowardsmiddle");
			case BACK_OFF -> TranslationFacility.tr("ls.player.position.wingbackoffensive");
			case BACK_DEF -> TranslationFacility.tr("ls.player.position.wingbackdefensive");
			case MIDFIELDER -> TranslationFacility.tr("ls.player.position.innermidfielder");
			case MIDFIELDER_OFF -> TranslationFacility.tr("ls.player.position.innermidfielderoffensive");
			case MIDFIELDER_DEF -> TranslationFacility.tr("ls.player.position.innermidfielderdefensive");
			case MIDFIELDER_TOWING -> TranslationFacility.tr("ls.player.position.innermidfieldertowardswing");
			case WINGER -> TranslationFacility.tr("ls.player.position.winger");
			case WINGER_TOMID -> TranslationFacility.tr("ls.player.position.wingertowardsmiddle");
			case WINGER_OFF -> TranslationFacility.tr("ls.player.position.wingeroffensive");
			case WINGER_DEF -> TranslationFacility.tr("ls.player.position.wingerdefensive");
			case FORWARD -> TranslationFacility.tr("ls.player.position.forward");
			case FORWARD_DEF -> TranslationFacility.tr("ls.player.position.forwarddefensive");
			case FORWARD_DEF_TECH -> TranslationFacility.tr("ls.player.position.forwarddefensivetechnical");
			case FORWARD_TOWING -> TranslationFacility.tr("ls.player.position.forwardtowardswing");
			case EXTRA -> TranslationFacility.tr("ls.player.position.extra_substitute");
			case SUBSTITUTED1, SUBSTITUTED2, SUBSTITUTED3 -> TranslationFacility.tr("Ausgewechselt");
			case REDCARDED1, REDCARDED2, REDCARDED3 -> TranslationFacility.tr("ls.player.position.red_carded");
			case UNSELECTABLE -> TranslationFacility.tr("Unselectable");
			default -> TranslationFacility.tr("ls.player.position.no_override");
		};
	}

	public static String getNameForPositionWithoutTactic(byte posId) {

		return switch (posId) {
			case KEEPER -> TranslationFacility.tr("ls.player.position.keeper");
			case CENTRAL_DEFENDER, CENTRAL_DEFENDER_TOWING, CENTRAL_DEFENDER_OFF -> TranslationFacility.tr("ls.player.position.centraldefender");
			case BACK, BACK_TOMID, BACK_OFF, BACK_DEF -> TranslationFacility.tr("ls.player.position.wingback");
			case MIDFIELDER, MIDFIELDER_OFF, MIDFIELDER_DEF, MIDFIELDER_TOWING -> TranslationFacility.tr("ls.player.position.innermidfielder");
			case WINGER, WINGER_TOMID, WINGER_OFF, WINGER_DEF -> TranslationFacility.tr("ls.player.position.winger");
			case FORWARD, FORWARD_DEF, FORWARD_DEF_TECH, FORWARD_TOWING -> TranslationFacility.tr("ls.player.position.forward");
			case EXTRA -> TranslationFacility.tr("ls.player.position.extra_substitute");
			case SUBSTITUTED1, SUBSTITUTED2, SUBSTITUTED3 -> TranslationFacility.tr("Ausgewechselt");
			case UNSELECTABLE -> TranslationFacility.tr("Unselectable");
			default -> TranslationFacility.tr("Unbestimmt");
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
            case KEEPER -> {
                if (train == TrainingType.GOALKEEPING ||
                        train == TrainingType.DEF_POSITIONS)
                    return true;
            }
            case CENTRAL_DEFENDER, CENTRAL_DEFENDER_TOWING, CENTRAL_DEFENDER_OFF -> {
                if (train == TrainingType.DEFENDING ||
                        train == TrainingType.DEF_POSITIONS ||
                        train == TrainingType.THROUGH_PASSES)
                    return true;
            }
            case BACK, BACK_TOMID, BACK_OFF, BACK_DEF -> {
                if (train == TrainingType.DEF_POSITIONS ||
                        train == TrainingType.THROUGH_PASSES ||
                        train == TrainingType.DEFENDING)
                    return true;
            }
            case MIDFIELDER, MIDFIELDER_OFF, MIDFIELDER_DEF, MIDFIELDER_TOWING -> {
                if (train == TrainingType.DEF_POSITIONS ||
                        train == TrainingType.PLAYMAKING ||
                        train == TrainingType.THROUGH_PASSES ||
                        train == TrainingType.SHORT_PASSES)
                    return true;
            }
            case WINGER, WINGER_TOMID, WINGER_OFF, WINGER_DEF -> {
                if (train == TrainingType.WING_ATTACKS ||
                        train == TrainingType.DEF_POSITIONS ||
                        train == TrainingType.CROSSING_WINGER ||
                        train == TrainingType.THROUGH_PASSES ||
                        train == TrainingType.SHORT_PASSES)
                    return true;
            }
            case FORWARD, FORWARD_DEF, FORWARD_TOWING -> {
                if (train == TrainingType.SCORING ||
                        train == TrainingType.WING_ATTACKS ||
                        train == TrainingType.SHORT_PASSES)
                    return true;
            }
        }
		return false;
	}

	/**
	 * Return if position is partial train
	 */
	public static boolean isPartialTrainPosition(byte posId, int train) {

		if (train == TrainingType.CROSSING_WINGER || train == TrainingType.PLAYMAKING) {
            switch (posId) {
                case BACK, BACK_TOMID, BACK_OFF, BACK_DEF -> {
                    if (train == TrainingType.CROSSING_WINGER)
                        return true;
                }
                case WINGER, WINGER_TOMID, WINGER_OFF, WINGER_DEF -> {
                    if (train == TrainingType.PLAYMAKING)
                        return true;
                }
            }
		}
		return false;
	}

	public byte getPosition() {
		return MatchRoleID.getPosition(m_iId, behaviour);
	}

	/**
	 * Getter for property m_bPosition.
	 *
	 * @return Value of property m_bPosition.
	 */
	public static byte getPosition(int id, byte taktik) {
        switch (id) {
            case keeper -> {
                return KEEPER;
            }
            case rightBack, leftBack -> {
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
            case middleCentralDefender, rightCentralDefender, leftCentralDefender -> {
                if (taktik == TOWARDS_WING) {
                    return CENTRAL_DEFENDER_TOWING;
                } else if (taktik == OFFENSIVE) {
                    return CENTRAL_DEFENDER_OFF;
                } else {
                    return CENTRAL_DEFENDER;
                }
            }
            case rightWinger, leftWinger -> {
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
            case centralInnerMidfield, rightInnerMidfield, leftInnerMidfield -> {
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
            case centralForward, rightForward, leftForward -> {
                if (taktik == DEFENSIVE) {
                    return FORWARD_DEF;
                } else if (taktik == TOWARDS_WING) {
                    return FORWARD_TOWING;
                } else {
                    return FORWARD;
                }
            }
            case substCD1, substCD2 -> {
                return CENTRAL_DEFENDER;
            }
            case substWB1, substWB2 -> {
                return BACK;
            }
            case substIM1, substIM2 -> {
                return MIDFIELDER;
            }
            case substWI1, substWI2 -> {
                return WINGER;
            }
            case substGK1, substGK2 -> {
                return KEEPER;
            }
            case substFW1, substFW2 -> {
                return FORWARD;
            }
            case substXT1, substXT2 -> {
                return EXTRA;
            }
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
	public final boolean setPlayerIdIfValidForLineup(int spielerId) {
		return setPlayerIdIfValidForLineup(spielerId, HOVerwaltung.instance().getModel().getCurrentLineup());
	}

	public final void setPlayerId(int id){
		this.m_iSpielerId = id;
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
	public final boolean setPlayerIdIfValidForLineup(int spielerId, Lineup lineup) {

		boolean containsPlayer = (m_iSpielerId > 0) || (m_iSpielerId < -10);
		boolean incomingEmpty = (spielerId < 1) && (spielerId > -10);

		// We don't want another player in the starting lineup if there are
		// already 11 on the field.

		if (!incomingEmpty && !containsPlayer && m_iId >= IMatchRoleID.startLineup
				&& m_iId < IMatchRoleID.startReserves && !lineup.hasFreePosition()) {
			HOLogger.instance().debug(getClass(),
					"Blocked from setting player at position: " + m_iSpielerId + " " + m_iId);
			return false;
		} else {
			this.m_iSpielerId = spielerId;
		}
		return true;
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
	public final void setBehaviour(byte m_bTaktik) {
		this.behaviour = m_bTaktik;
	}

	/**
	 * Getter for property m_bTaktik.
	 *
	 * @return Value of property m_bTaktik.
	 */
	public final byte getTactic() {
		return behaviour;
	}

	public static List<Byte> getBehaviours(int roleId){
		switch (roleId){
			case keeper -> {
				return List.of(NORMAL);
			}
			case leftBack, rightBack, leftWinger, rightWinger -> {
				return List.of(NORMAL, OFFENSIVE, DEFENSIVE, TOWARDS_MIDDLE);
			}
			case leftCentralDefender, rightCentralDefender -> {
				return List.of(NORMAL, OFFENSIVE, TOWARDS_WING);
			}
			case middleCentralDefender -> {
				return List.of(NORMAL, OFFENSIVE);
			}
			case leftInnerMidfield, rightInnerMidfield -> {
				return List.of(NORMAL, OFFENSIVE, DEFENSIVE, TOWARDS_WING);
			}
			case centralInnerMidfield -> {
				return List.of(NORMAL, OFFENSIVE, DEFENSIVE);
			}
			case leftForward, rightForward -> {
				return List.of(NORMAL, DEFENSIVE, TOWARDS_WING);
			}
			case centralForward -> {
				return List.of(NORMAL, DEFENSIVE);
			}
		}
        return List.of();
    }

	@Override
	public final int compareTo(@NotNull IMatchRoleID obj) {
		if (obj instanceof final MatchRoleID position) {

			// Beide aufgestellt ?
			if ((this.getId() < IMatchRoleID.startReserves)
					&& (position.getId() < IMatchRoleID.startReserves)) {
				return Byte.compare(this.getPosition(), position.getPosition());
			}
			// this aufgestellt ?
			else if (this.getId() < IMatchRoleID.startReserves) {
				return -1;
			}
			// position aufgestellt
			else if (position.getId() < IMatchRoleID.startReserves) {
				return 1;
			}
			// keiner aufgestellt
			else {
				return Byte.compare(this.getPosition(), position.getPosition());
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
			das.writeByte(behaviour);

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
			case setPieces -> Sector.SetPiecesTaker;
			default -> Sector.None;
		};
	}
	
}
