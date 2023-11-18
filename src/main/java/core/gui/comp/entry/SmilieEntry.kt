package core.gui.comp.entry

import core.gui.theme.GroupTeamFactory
import core.gui.theme.ImageUtilities
import core.model.HOVerwaltung
import core.model.player.MatchRoleID
import core.model.player.Player
import javax.swing.SwingConstants

/**
 * Displays the warnings and violations.
 */
class SmilieEntry : DoubleLabelEntries() {
    //~ Instance fields ----------------------------------------------------------------------------
    private val manuell = ColorLabelEntry(
        "", ColorLabelEntry.FG_STANDARD,
        ColorLabelEntry.BG_STANDARD,
        SwingConstants.RIGHT
    )
    private val team = ColorLabelEntry(
        "", ColorLabelEntry.FG_STANDARD,
        ColorLabelEntry.BG_STANDARD,
        SwingConstants.LEFT
    )
    private var player: Player? = null
    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new SmilieEntry object.
     */
    init {
        setLabels(team, manuell)
    }

    fun setPlayer(player: Player?) {
        this.player = player
        updateComponent()
    }

    fun getPlayer(): Player? {
        return player
    }

    override fun compareTo(other: IHOTableEntry): Int {
        if (other is SmilieEntry) {
            if (other.getPlayer() != null && getPlayer() != null) {
                var result:Int

                //Beide null -> Der ManuelleSmilie entscheidet
                result = if (other.getPlayer()?.getTeamGroup().isNullOrEmpty() &&
                    getPlayer()?.getTeamGroup().isNullOrEmpty()) {
                    0
                } else if (other.getPlayer()!!.getTeamGroup() == null || other.getPlayer()!!.getTeamGroup() == "") {
                    1
                } else if (getPlayer()!!.getTeamGroup() == null || getPlayer()!!.getTeamGroup() == "") {
                    -1
                } else {
                    other.getPlayer()!!.getTeamGroup()!!.compareTo(getPlayer()?.getTeamGroup()!!)
                }

                // if equal check lineup
                if (result == 0) {
                    val team = HOVerwaltung.instance().model.getCurrentLineupTeam()
                    val entrySort: MatchRoleID? = team.getLineup().getPositionByPlayerId(other.getPlayer()!!.playerId)
                    val sort: MatchRoleID? = team.getLineup().getPositionByPlayerId(getPlayer()!!.playerId)
                    result = if (sort == null && entrySort == null) {
                        0
                    } else if (sort == null) {
                        -1
                    } else entrySort?.sortId?.compareTo(sort.sortId) ?: 1
                }
                return result
            }
        }
        return 0
    }

    override fun updateComponent() {
        if (player != null) {
            if (player!!.getTeamGroup() != null && player!!.getTeamGroup() != "") {
                team.setIcon(GroupTeamFactory.instance().getActiveGroupIcon(player!!.getTeamGroup()))
            } else {
                team.clear()
            }
            if (player!!.getInfoSmiley() != null && player!!.getInfoSmiley() != "") {
                manuell.setIcon(ImageUtilities.getSmileyIcon(player!!.getInfoSmiley()))
            } else {
                manuell.clear()
            }
        } else {
            team.clear()
            manuell.clear()
        }
    }
}
