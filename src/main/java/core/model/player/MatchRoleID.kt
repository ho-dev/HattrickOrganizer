package core.model.player

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import core.constants.TrainingType
import core.datatype.CBItem
import core.db.AbstractTable.Storable
import core.model.HOVerwaltung
import core.util.HOLogger
import module.lineup.Lineup
import java.io.DataOutputStream
import java.io.IOException
import java.io.Serializable
import java.util.*

open class MatchRoleID : Storable, Serializable, Comparable<IMatchRoleID>, IMatchRoleID {
    enum class Sector {
        None,
        Goal,
        Back,
        CentralDefence,
        Wing,
        InnerMidfield,
        Forward,
        SetPiecesTaker
    }
    // ~ Instance fields
    // ----------------------------------------------------------------------------
    /**
     * Getter for property m_bTaktik.
     *
     * @return Value of property m_bTaktik.
     */
    /** TaktikAnweisungen  */
    @SerializedName("behaviour")
    @Expose
    var tactic: Byte = -1
        private set

    /** ID  */
    var id = -1
    val isFieldMatchRoleId: Boolean
        get() = isFieldMatchRoleId(id)
    val isSubstitutesMatchRoleId: Boolean
        get() = id >= IMatchRoleID.substGK1 && id <= IMatchRoleID.substXT1
    val isBackupsMatchRoleId: Boolean
        get() = id >= IMatchRoleID.substGK2 && id <= IMatchRoleID.substXT2
    val isPenaltyTakerMatchRoleId: Boolean
        get() = id >= IMatchRoleID.penaltyTaker1 && id <= IMatchRoleID.penaltyTaker11
    val isReplacedMatchRoleId: Boolean
        get() = id >= IMatchRoleID.FirstPlayerReplaced && id <= IMatchRoleID.ThirdPlayerReplaced
    // It is much safer to have "empty" as 0, as it appears temp-players may
    // get ID -1 - Blaghaid
    /**
     * Getter for property m_iSpielerId.
     *
     * @return Value of property m_iSpielerId.
     */
    /** welcher Player besetzt diese Position  */
    @SerializedName("id")
    @Expose
    var playerId = 0
    // ~ Constructors
    // -------------------------------------------------------------------------------
    /**
     * Creates a new instance of MatchRoleID
     */
    /* byte position, */
    constructor(id: Int, playerId: Int, behaviour: Byte) {
        // m_bPosition = position;
        if (id < IMatchRoleID.setPieces && id != -1 && id != 0) {
            HOLogger.instance().debug(javaClass, "Old RoleID found in lineup: $id")
        }
        this.id = id
        this.playerId = playerId
        tactic = behaviour
    }

    /**
     * Creates a new instance of MatchRoleID
     */
    constructor(sp: MatchRoleID) {
        // m_bPosition = position;
        id = sp.id
        playerId = sp.playerId
        tactic = sp.tactic
        if (id < IMatchRoleID.setPieces && id != -1) {
            HOLogger.instance().debug(javaClass, "Old RoleID found in lineup: $id")
        }
    }

    constructor()

    val position: Byte
        get() = getPosition(id, tactic)
    val sortId: Int
        /**
         * returns an ID that can be sorted ( e.g. Player Overview Table )
         *
         */
        get() {
            var id = position.toInt()
            if (id == IMatchRoleID.FORWARD_TOWING.toInt()) {
                id = 18
            }
            if (this.id >= IMatchRoleID.startReserves) {
                id += 20
            }
            return id
        }

    /**
     * Setter for property m_iSpielerId. This will fail if the current lineup of
     * the HO model would end up with 12 players or more.
     *
     * @param spielerId
     * New value of property m_iSpielerId.
     */
    fun setPlayerIdIfValidForLineup(spielerId: Int) {
        setPlayerIdIfValidForLineup(spielerId, HOVerwaltung.instance().model.getCurrentLineup())
    }

    /**
     * Setter for property m_iSpielerId. This setter will fail if the provided
     * lineup would end up with 12 players or more.
     *
     * @param spielerId
     * New value of property m_iSpielerId.
     * @param lineup
     * The lineup that will be used to check for available space.
     */
    fun setPlayerIdIfValidForLineup(spielerId: Int, lineup: Lineup) {
        val containsPlayer = playerId > 0 || playerId < -10
        val incomingEmpty = spielerId < 1 && spielerId > -10

        // We don't want another player in the starting lineup if there are
        // already 11 on the field.
        if (!incomingEmpty && !containsPlayer && id >= IMatchRoleID.startLineup && id < IMatchRoleID.startReserves && !lineup.hasFreePosition()) {
            HOLogger.instance().debug(
                javaClass,
                "Blocked from setting player at position: $playerId $id"
            )
        } else {
            playerId = spielerId
        }
    }

    /**
     * Setter for property m_bTaktik.
     *
     * @param taktik
     * New value of property m_bTaktik.
     */
    fun setBehaviour(taktik: Byte) {
        tactic = taktik
    }

    override fun compareTo(other: IMatchRoleID): Int {
        return if (other is MatchRoleID) {

            // Beide aufgestellt ?
            if (id < IMatchRoleID.startReserves && other.id < IMatchRoleID.startReserves) {
                position.compareTo(other.position)
            } else if (id < IMatchRoleID.startReserves) {
                -1
            } else if (other.id < IMatchRoleID.startReserves) {
                1
            } else {
                position.compareTo(other.position)
            }
        } else 0
    }

    /*
	 * saved den Serverspieler
	 *
	 * @param baos Der Outputstream in den gesaved werden soll
	 *
	 * @return Byte Array der Daten die in den Output geschireben wurden
	 */
    fun save(das: DataOutputStream) {
        // ByteArrayOutputStream baos = null;
        // DataOutputStream das = null;
        // Byte Array
        // byte[] data = null;
        try {
            // Instanzen erzeugen
            // baos = new ByteArrayOutputStream();
            // das = new DataOutputStream(baos);
            // Daten schreiben in Strom
            das.writeInt(id)
            das.writeInt(playerId)
            das.writeByte(tactic.toInt())

            /*
			 * //Strom konvertieren in Byte data = baos.toByteArray();
			 * //Hilfsstrom schließen das.close ();
			 *
			 * return data;
			 */
        } catch (ioe: IOException) {
            HOLogger.instance().log(javaClass, ioe)
        }

        // return data;
    }

    val sector: Sector
        get() = getSector(id)

    companion object {
        /**
         * Array with the constants (CBItems) for the positions, Without Exchanged
         */
        @JvmField
        val POSITIONEN = arrayOf(
            CBItem(getNameForPosition(IMatchRoleID.UNKNOWN), IMatchRoleID.UNKNOWN.toInt()),
            CBItem(
                getNameForPosition(IMatchRoleID.UNSELECTABLE),
                IMatchRoleID.UNSELECTABLE.toInt()
            ),
            CBItem(getNameForPosition(IMatchRoleID.KEEPER), IMatchRoleID.KEEPER.toInt()),
            CBItem(
                getNameForPosition(IMatchRoleID.CENTRAL_DEFENDER),
                IMatchRoleID.CENTRAL_DEFENDER.toInt()
            ),
            CBItem(
                getNameForPosition(IMatchRoleID.CENTRAL_DEFENDER_OFF),
                IMatchRoleID.CENTRAL_DEFENDER_OFF.toInt()
            ),
            CBItem(
                getNameForPosition(IMatchRoleID.CENTRAL_DEFENDER_TOWING),
                IMatchRoleID.CENTRAL_DEFENDER_TOWING.toInt()
            ),
            CBItem(getNameForPosition(IMatchRoleID.BACK), IMatchRoleID.BACK.toInt()),
            CBItem(getNameForPosition(IMatchRoleID.BACK_OFF), IMatchRoleID.BACK_OFF.toInt()),
            CBItem(getNameForPosition(IMatchRoleID.BACK_DEF), IMatchRoleID.BACK_DEF.toInt()),
            CBItem(getNameForPosition(IMatchRoleID.BACK_TOMID), IMatchRoleID.BACK_TOMID.toInt()),
            CBItem(getNameForPosition(IMatchRoleID.MIDFIELDER), IMatchRoleID.MIDFIELDER.toInt()),
            CBItem(
                getNameForPosition(IMatchRoleID.MIDFIELDER_OFF),
                IMatchRoleID.MIDFIELDER_OFF.toInt()
            ),
            CBItem(
                getNameForPosition(IMatchRoleID.MIDFIELDER_DEF),
                IMatchRoleID.MIDFIELDER_DEF.toInt()
            ),
            CBItem(
                getNameForPosition(IMatchRoleID.MIDFIELDER_TOWING),
                IMatchRoleID.MIDFIELDER_TOWING.toInt()
            ),
            CBItem(getNameForPosition(IMatchRoleID.WINGER), IMatchRoleID.WINGER.toInt()),
            CBItem(getNameForPosition(IMatchRoleID.WINGER_OFF), IMatchRoleID.WINGER_OFF.toInt()),
            CBItem(getNameForPosition(IMatchRoleID.WINGER_DEF), IMatchRoleID.WINGER_DEF.toInt()),
            CBItem(
                getNameForPosition(IMatchRoleID.WINGER_TOMID),
                IMatchRoleID.WINGER_TOMID.toInt()
            ),
            CBItem(getNameForPosition(IMatchRoleID.FORWARD), IMatchRoleID.FORWARD.toInt()),
            CBItem(getNameForPosition(IMatchRoleID.FORWARD_DEF), IMatchRoleID.FORWARD_DEF.toInt()),
            CBItem(
                getNameForPosition(IMatchRoleID.FORWARD_TOWING),
                IMatchRoleID.FORWARD_TOWING.toInt()
            )
        )

        @JvmStatic
        fun isFieldMatchRoleId(pos: Int): Boolean {
            return pos >= IMatchRoleID.keeper && pos <= IMatchRoleID.leftForward
        }
        // ~ Methods
        // ------------------------------------------------------------------------------------
        /**
         * Returns a possible HT position ID for a HO position ID. Use only for
         * loading the position image
         */
        @JvmStatic
        fun getHTPosidForHOPosition4Image(posId: Byte): Int {
            return when (posId) {
                IMatchRoleID.KEEPER -> {
                    IMatchRoleID.keeper
                }

                IMatchRoleID.CENTRAL_DEFENDER, IMatchRoleID.CENTRAL_DEFENDER_TOWING, IMatchRoleID.CENTRAL_DEFENDER_OFF -> {
                    IMatchRoleID.rightCentralDefender
                }

                IMatchRoleID.BACK, IMatchRoleID.BACK_TOMID, IMatchRoleID.BACK_OFF, IMatchRoleID.BACK_DEF -> {
                    IMatchRoleID.rightBack
                }

                IMatchRoleID.MIDFIELDER, IMatchRoleID.MIDFIELDER_OFF, IMatchRoleID.MIDFIELDER_DEF, IMatchRoleID.MIDFIELDER_TOWING -> {
                    IMatchRoleID.rightInnerMidfield
                }

                IMatchRoleID.WINGER, IMatchRoleID.WINGER_TOMID, IMatchRoleID.WINGER_OFF, IMatchRoleID.WINGER_DEF -> {
                    IMatchRoleID.rightWinger
                }

                IMatchRoleID.FORWARD, IMatchRoleID.FORWARD_TOWING, IMatchRoleID.FORWARD_DEF -> {
                    IMatchRoleID.rightForward
                }

                IMatchRoleID.SUBSTITUTED1, IMatchRoleID.SUBSTITUTED2, IMatchRoleID.SUBSTITUTED3 -> {
                    IMatchRoleID.FirstPlayerReplaced
                }

                else -> {
                    HOLogger.instance().log(MatchRoleID::class.java, "Position not recognized: $posId")
                    IMatchRoleID.FirstPlayerReplaced
                }
            }
        }

        /**
         * Gibt das Kürzel für den Namen zurück
         */
        @JvmStatic
        fun getShortNameForPosition(posId: Byte): String {
            return when (posId) {
                IMatchRoleID.KEEPER -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.keeper")

                IMatchRoleID.CENTRAL_DEFENDER -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.centraldefender")

                IMatchRoleID.CENTRAL_DEFENDER_TOWING -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.centraldefendertowardswing")

                IMatchRoleID.CENTRAL_DEFENDER_OFF -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.centraldefenderoffensive")

                IMatchRoleID.BACK -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.wingback")

                IMatchRoleID.BACK_TOMID -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.wingbacktowardsmiddle")

                IMatchRoleID.BACK_OFF -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.wingbackoffensive")

                IMatchRoleID.BACK_DEF -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.wingbackdefensive")

                IMatchRoleID.MIDFIELDER -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.innermidfielder")

                IMatchRoleID.MIDFIELDER_OFF -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.innermidfielderoffensive")

                IMatchRoleID.MIDFIELDER_DEF -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.innermidfielderdefensive")

                IMatchRoleID.MIDFIELDER_TOWING -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.innermidfieldertowardswing")

                IMatchRoleID.WINGER -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.winger")

                IMatchRoleID.WINGER_TOMID -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.wingertowardsmiddle")

                IMatchRoleID.WINGER_OFF -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.wingeroffensive")

                IMatchRoleID.WINGER_DEF -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.wingerdefensive")

                IMatchRoleID.FORWARD -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.forward")

                IMatchRoleID.FORWARD_TOWING -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.forwardtowardswing")

                IMatchRoleID.FORWARD_DEF -> HOVerwaltung.instance()
                    .getLanguageString("ls.player.position_short.forwarddefensive")

                IMatchRoleID.SUBSTITUTED1, IMatchRoleID.SUBSTITUTED2, IMatchRoleID.SUBSTITUTED3 -> HOVerwaltung.instance()
                    .getLanguageString("Ausgewechselt")

                else -> HOVerwaltung.instance().getLanguageString("Unbestimmt")
            }
        }

        /**
         * Returns the name of a positionsid
         */
        @JvmStatic
        fun getNameForPosition(posId: Byte): String {
            return when (posId) {
                IMatchRoleID.KEEPER -> getLangStr("ls.player.position.keeper")
                IMatchRoleID.CENTRAL_DEFENDER -> getLangStr("ls.player.position.centraldefender")
                IMatchRoleID.CENTRAL_DEFENDER_TOWING -> getLangStr("ls.player.position.centraldefendertowardswing")
                IMatchRoleID.CENTRAL_DEFENDER_OFF -> getLangStr("ls.player.position.centraldefenderoffensive")
                IMatchRoleID.BACK -> getLangStr("ls.player.position.wingback")
                IMatchRoleID.BACK_TOMID -> getLangStr("ls.player.position.wingbacktowardsmiddle")
                IMatchRoleID.BACK_OFF -> getLangStr("ls.player.position.wingbackoffensive")
                IMatchRoleID.BACK_DEF -> getLangStr("ls.player.position.wingbackdefensive")
                IMatchRoleID.MIDFIELDER -> getLangStr("ls.player.position.innermidfielder")
                IMatchRoleID.MIDFIELDER_OFF -> getLangStr("ls.player.position.innermidfielderoffensive")
                IMatchRoleID.MIDFIELDER_DEF -> getLangStr("ls.player.position.innermidfielderdefensive")
                IMatchRoleID.MIDFIELDER_TOWING -> getLangStr("ls.player.position.innermidfieldertowardswing")
                IMatchRoleID.WINGER -> getLangStr("ls.player.position.winger")
                IMatchRoleID.WINGER_TOMID -> getLangStr("ls.player.position.wingertowardsmiddle")
                IMatchRoleID.WINGER_OFF -> getLangStr("ls.player.position.wingeroffensive")
                IMatchRoleID.WINGER_DEF -> getLangStr("ls.player.position.wingerdefensive")
                IMatchRoleID.FORWARD -> getLangStr("ls.player.position.forward")
                IMatchRoleID.FORWARD_DEF -> getLangStr("ls.player.position.forwarddefensive")
                IMatchRoleID.FORWARD_DEF_TECH -> getLangStr("ls.player.position.forwarddefensivetechnical")
                IMatchRoleID.FORWARD_TOWING -> getLangStr("ls.player.position.forwardtowardswing")
                IMatchRoleID.EXTRA -> getLangStr("ls.player.position.extra_substitute")
                IMatchRoleID.SUBSTITUTED1, IMatchRoleID.SUBSTITUTED2, IMatchRoleID.SUBSTITUTED3 -> getLangStr(
                    "Ausgewechselt"
                )

                IMatchRoleID.UNSELECTABLE -> getLangStr("Unselectable")
                else -> getLangStr("ls.player.position.no_override")
            }
        }

        @JvmStatic
        fun getNameForPositionWithoutTactic(posId: Byte): String {
            return when (posId) {
                IMatchRoleID.KEEPER -> getLangStr("ls.player.position.keeper")
                IMatchRoleID.CENTRAL_DEFENDER, IMatchRoleID.CENTRAL_DEFENDER_TOWING, IMatchRoleID.CENTRAL_DEFENDER_OFF -> getLangStr(
                    "ls.player.position.centraldefender"
                )

                IMatchRoleID.BACK, IMatchRoleID.BACK_TOMID, IMatchRoleID.BACK_OFF, IMatchRoleID.BACK_DEF -> getLangStr(
                    "ls.player.position.wingback"
                )

                IMatchRoleID.MIDFIELDER, IMatchRoleID.MIDFIELDER_OFF, IMatchRoleID.MIDFIELDER_DEF, IMatchRoleID.MIDFIELDER_TOWING -> getLangStr(
                    "ls.player.position.innermidfielder"
                )

                IMatchRoleID.WINGER, IMatchRoleID.WINGER_TOMID, IMatchRoleID.WINGER_OFF, IMatchRoleID.WINGER_DEF -> getLangStr(
                    "ls.player.position.winger"
                )

                IMatchRoleID.FORWARD, IMatchRoleID.FORWARD_DEF, IMatchRoleID.FORWARD_DEF_TECH, IMatchRoleID.FORWARD_TOWING -> getLangStr(
                    "ls.player.position.forward"
                )

                IMatchRoleID.EXTRA -> getLangStr("ls.player.position.extra_substitute")
                IMatchRoleID.SUBSTITUTED1, IMatchRoleID.SUBSTITUTED2, IMatchRoleID.SUBSTITUTED3 -> getLangStr(
                    "Ausgewechselt"
                )

                IMatchRoleID.UNSELECTABLE -> getLangStr("Unselectable")
                else -> getLangStr("Unbestimmt")
            }
        }

        /**
         * Return if position is full train
         */
        @JvmStatic
        fun isFullTrainPosition(posId: Byte, train: Int): Boolean {
            if (train == TrainingType.SET_PIECES ||
                train == TrainingType.SHOOTING
            ) return true
            when (posId) {
                IMatchRoleID.KEEPER -> {
                    if (train == TrainingType.GOALKEEPING ||
                        train == TrainingType.DEF_POSITIONS
                    ) return true
                }

                IMatchRoleID.CENTRAL_DEFENDER, IMatchRoleID.CENTRAL_DEFENDER_TOWING, IMatchRoleID.CENTRAL_DEFENDER_OFF -> {
                    if (train == TrainingType.DEFENDING || train == TrainingType.DEF_POSITIONS || train == TrainingType.THROUGH_PASSES) return true
                }

                IMatchRoleID.BACK, IMatchRoleID.BACK_TOMID, IMatchRoleID.BACK_OFF, IMatchRoleID.BACK_DEF -> {
                    if (train == TrainingType.DEF_POSITIONS || train == TrainingType.THROUGH_PASSES || train == TrainingType.DEFENDING) return true
                }

                IMatchRoleID.MIDFIELDER, IMatchRoleID.MIDFIELDER_OFF, IMatchRoleID.MIDFIELDER_DEF, IMatchRoleID.MIDFIELDER_TOWING -> {
                    if (train == TrainingType.DEF_POSITIONS || train == TrainingType.PLAYMAKING || train == TrainingType.THROUGH_PASSES || train == TrainingType.SHORT_PASSES) return true
                }

                IMatchRoleID.WINGER, IMatchRoleID.WINGER_TOMID, IMatchRoleID.WINGER_OFF, IMatchRoleID.WINGER_DEF -> {
                    if (train == TrainingType.WING_ATTACKS || train == TrainingType.DEF_POSITIONS || train == TrainingType.CROSSING_WINGER || train == TrainingType.THROUGH_PASSES || train == TrainingType.SHORT_PASSES) return true
                }

                IMatchRoleID.FORWARD, IMatchRoleID.FORWARD_DEF, IMatchRoleID.FORWARD_TOWING -> {
                    if (train == TrainingType.SCORING || train == TrainingType.WING_ATTACKS || train == TrainingType.SHORT_PASSES) return true
                }
            }
            return false
        }

        /**
         * Return if position is partial train
         */
        @JvmStatic
        fun isPartialTrainPosition(posId: Byte, train: Int): Boolean {
            if (train == TrainingType.CROSSING_WINGER || train == TrainingType.PLAYMAKING) {
                when (posId) {
                    IMatchRoleID.BACK, IMatchRoleID.BACK_TOMID, IMatchRoleID.BACK_OFF, IMatchRoleID.BACK_DEF -> {
                        if (train == TrainingType.CROSSING_WINGER) return true
                    }

                    IMatchRoleID.WINGER, IMatchRoleID.WINGER_TOMID, IMatchRoleID.WINGER_OFF, IMatchRoleID.WINGER_DEF -> {
                        if (train == TrainingType.PLAYMAKING) return true
                    }
                }
            }
            return false
        }

        /**
         * Getter for property m_bPosition.
         *
         * @return Value of property m_bPosition.
         */
        @JvmStatic
        fun getPosition(id: Int, taktik: Byte): Byte {
            when (id) {
                IMatchRoleID.keeper, IMatchRoleID.substGK1, IMatchRoleID.substGK2 -> {
                    return IMatchRoleID.KEEPER
                }

                IMatchRoleID.rightBack, IMatchRoleID.leftBack -> {
                    return when (taktik) {
                        IMatchRoleID.TOWARDS_MIDDLE -> {
                            IMatchRoleID.BACK_TOMID
                        }

                        IMatchRoleID.OFFENSIVE -> {
                            IMatchRoleID.BACK_OFF
                        }

                        IMatchRoleID.DEFENSIVE -> {
                            IMatchRoleID.BACK_DEF
                        }

                        else -> {
                            IMatchRoleID.BACK
                        }
                    }
                }

                IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender, IMatchRoleID.leftCentralDefender -> {
                    return when (taktik) {
                        IMatchRoleID.TOWARDS_WING -> {
                            IMatchRoleID.CENTRAL_DEFENDER_TOWING
                        }

                        IMatchRoleID.OFFENSIVE -> {
                            IMatchRoleID.CENTRAL_DEFENDER_OFF
                        }

                        else -> {
                            IMatchRoleID.CENTRAL_DEFENDER
                        }
                    }
                }

                IMatchRoleID.rightWinger, IMatchRoleID.leftWinger -> {
                    return when (taktik) {
                        IMatchRoleID.TOWARDS_MIDDLE -> {
                            IMatchRoleID.WINGER_TOMID
                        }

                        IMatchRoleID.OFFENSIVE -> {
                            IMatchRoleID.WINGER_OFF
                        }

                        IMatchRoleID.DEFENSIVE -> {
                            IMatchRoleID.WINGER_DEF
                        }

                        else -> {
                            IMatchRoleID.WINGER
                        }
                    }
                }

                IMatchRoleID.centralInnerMidfield, IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftInnerMidfield -> {
                    return when (taktik) {
                        IMatchRoleID.TOWARDS_WING -> {
                            IMatchRoleID.MIDFIELDER_TOWING
                        }

                        IMatchRoleID.OFFENSIVE -> {
                            IMatchRoleID.MIDFIELDER_OFF
                        }

                        IMatchRoleID.DEFENSIVE -> {
                            IMatchRoleID.MIDFIELDER_DEF
                        }

                        else -> {
                            IMatchRoleID.MIDFIELDER
                        }
                    }
                }

                IMatchRoleID.centralForward, IMatchRoleID.rightForward, IMatchRoleID.leftForward -> {
                    return when (taktik) {
                        IMatchRoleID.DEFENSIVE -> {
                            IMatchRoleID.FORWARD_DEF
                        }

                        IMatchRoleID.TOWARDS_WING -> {
                            IMatchRoleID.FORWARD_TOWING
                        }

                        else -> {
                            IMatchRoleID.FORWARD
                        }
                    }
                }

                IMatchRoleID.substCD1, IMatchRoleID.substCD2 -> {
                    return IMatchRoleID.CENTRAL_DEFENDER
                }

                IMatchRoleID.substWB1, IMatchRoleID.substWB2 -> {
                    return IMatchRoleID.BACK
                }

                IMatchRoleID.substIM1, IMatchRoleID.substIM2 -> {
                    return IMatchRoleID.MIDFIELDER
                }

                IMatchRoleID.substWI1, IMatchRoleID.substWI2 -> {
                    return IMatchRoleID.WINGER
                }

                IMatchRoleID.substFW1, IMatchRoleID.substFW2 -> {
                    return IMatchRoleID.FORWARD
                }

                IMatchRoleID.substXT1, IMatchRoleID.substXT2 -> {
                    return IMatchRoleID.EXTRA
                }
            }
            return IMatchRoleID.UNKNOWN
        }

        /**
         * returns an ID that can be sorted (e.g.  * Player overview table
         */
        @JvmStatic
        fun getSortId(position: Byte, reserve: Boolean): Int {
            var id = position.toInt()
            if (reserve) {
                id += 20
            }
            return id
        }

        fun getBehaviours(roleId: Int): List<Byte> {
            when (roleId) {
                IMatchRoleID.keeper -> {
                    return listOf(IMatchRoleID.NORMAL)
                }

                IMatchRoleID.leftBack, IMatchRoleID.rightBack, IMatchRoleID.leftWinger, IMatchRoleID.rightWinger -> {
                    return listOf(
                        IMatchRoleID.NORMAL,
                        IMatchRoleID.OFFENSIVE,
                        IMatchRoleID.DEFENSIVE,
                        IMatchRoleID.TOWARDS_MIDDLE
                    )
                }

                IMatchRoleID.leftCentralDefender, IMatchRoleID.rightCentralDefender -> {
                    return listOf(
                        IMatchRoleID.NORMAL,
                        IMatchRoleID.OFFENSIVE,
                        IMatchRoleID.TOWARDS_WING
                    )
                }

                IMatchRoleID.middleCentralDefender -> {
                    return listOf(IMatchRoleID.NORMAL, IMatchRoleID.OFFENSIVE)
                }

                IMatchRoleID.leftInnerMidfield, IMatchRoleID.rightInnerMidfield -> {
                    return listOf(
                        IMatchRoleID.NORMAL,
                        IMatchRoleID.OFFENSIVE,
                        IMatchRoleID.DEFENSIVE,
                        IMatchRoleID.TOWARDS_WING
                    )
                }

                IMatchRoleID.centralInnerMidfield -> {
                    return listOf(
                        IMatchRoleID.NORMAL,
                        IMatchRoleID.OFFENSIVE,
                        IMatchRoleID.DEFENSIVE
                    )
                }

                IMatchRoleID.leftForward, IMatchRoleID.rightForward -> {
                    return listOf(
                        IMatchRoleID.NORMAL,
                        IMatchRoleID.DEFENSIVE,
                        IMatchRoleID.TOWARDS_WING
                    )
                }

                IMatchRoleID.centralForward -> {
                    return listOf(IMatchRoleID.NORMAL, IMatchRoleID.DEFENSIVE)
                }
            }
            return listOf()
        }

        fun convertOldRoleToNew(roleID: Int): Int {
            return if (IMatchRoleID.oldKeeper.contains(roleID)) IMatchRoleID.keeper else if (IMatchRoleID.oldRightBack.contains(
                    roleID
                )
            ) IMatchRoleID.rightBack else if (IMatchRoleID.oldLeftCentralDefender.contains(roleID)) IMatchRoleID.leftCentralDefender else if (IMatchRoleID.oldRightCentralDefender.contains(
                    roleID
                )
            ) IMatchRoleID.rightCentralDefender else if (IMatchRoleID.oldLeftBack.contains(roleID)) IMatchRoleID.leftBack else if (IMatchRoleID.oldRightWinger.contains(
                    roleID
                )
            ) IMatchRoleID.rightWinger else if (IMatchRoleID.oldRightInnerMidfielder.contains(roleID)) IMatchRoleID.rightInnerMidfield else if (IMatchRoleID.oldLeftInnerMidfielder.contains(
                    roleID
                )
            ) IMatchRoleID.leftInnerMidfield else if (IMatchRoleID.oldLeftWinger.contains(roleID)) IMatchRoleID.leftWinger else if (IMatchRoleID.oldRightForward.contains(
                    roleID
                )
            ) IMatchRoleID.rightForward else if (IMatchRoleID.oldLeftForward.contains(roleID)) IMatchRoleID.leftForward else if (IMatchRoleID.oldSubstKeeper.contains(
                    roleID
                )
            ) IMatchRoleID.substGK1 else if (IMatchRoleID.oldSubstDefender.contains(roleID)) IMatchRoleID.substCD1 else if (IMatchRoleID.oldSubstMidfielder.contains(
                    roleID
                )
            ) IMatchRoleID.substIM1 else if (IMatchRoleID.oldSubstWinger.contains(roleID)) IMatchRoleID.substWI1 else if (IMatchRoleID.oldSubstForward.contains(
                    roleID
                )
            ) IMatchRoleID.substFW1 else roleID
        }

        fun convertOldRoleToNew(oldLineupProperties: Properties): Properties {
            val result = Properties()
            var sKey: String

            // mapping conversion OldRole -> New Rolw
            val mappingTable = HashMap<String, String>()
            mappingTable["behrightback"] = "order_rightback"
            mappingTable["behleftback"] = "order_leftback"
            mappingTable["insideback1"] = "rightcentraldefender"
            mappingTable["behinsideback1"] = "order_rightcentraldefender"
            mappingTable["insideback2"] = "leftcentraldefender"
            mappingTable["behinsideback2"] = "order_leftcentraldefender"
            mappingTable["insideback3"] = "middlecentraldefender"
            mappingTable["behinsideback3"] = "order_middlecentraldefender"
            mappingTable["behrightwinger"] = "order_rightwinger"
            mappingTable["behleftwinger"] = "order_leftwinger"
            mappingTable["insidemid1"] = "rightinnermidfield"
            mappingTable["behinsidemid1"] = "order_rightinnermidfield"
            mappingTable["insidemid2"] = "leftinnermidfield"
            mappingTable["behinsidemid2"] = "order_leftinnermidfield"
            mappingTable["insidemid3"] = "middleinnermidfield"
            mappingTable["behinsidemid3"] = "order_centralinnermidfield"
            mappingTable["forward1"] = "rightforward"
            mappingTable["behforward1"] = "order_rightforward"
            mappingTable["forward2"] = "leftforward"
            mappingTable["behforward2"] = "order_leftforward"
            mappingTable["forward3"] = "centralforward"
            mappingTable["behforward3"] = "order_centralforward"
            mappingTable["substback"] = "substcd1"
            mappingTable["substinsidemid"] = "substim1"
            mappingTable["substwinger"] = "substwi1"
            mappingTable["substkeeper"] = "substgk1"
            mappingTable["substforward"] = "substfw1"
            for ((key, value) in oldLineupProperties) {
                sKey = key.toString()
                result.setProperty(mappingTable.getOrDefault(sKey, sKey), value.toString())
            }
            return result
        }

        private fun getLangStr(key: String): String {
            return HOVerwaltung.instance().getLanguageString(key)
        }

        @JvmStatic
        fun getSector(roleId: Int): Sector {
            return when (roleId) {
                IMatchRoleID.keeper -> Sector.Goal
                IMatchRoleID.leftBack, IMatchRoleID.rightBack -> Sector.Back
                IMatchRoleID.leftCentralDefender, IMatchRoleID.rightCentralDefender, IMatchRoleID.middleCentralDefender -> Sector.CentralDefence
                IMatchRoleID.leftWinger, IMatchRoleID.rightWinger -> Sector.Wing
                IMatchRoleID.leftInnerMidfield, IMatchRoleID.rightInnerMidfield, IMatchRoleID.centralInnerMidfield -> Sector.InnerMidfield
                IMatchRoleID.leftForward, IMatchRoleID.rightForward, IMatchRoleID.centralForward -> Sector.Forward
                IMatchRoleID.setPieces -> Sector.SetPiecesTaker
                else -> Sector.None
            }
        }
    }
}
