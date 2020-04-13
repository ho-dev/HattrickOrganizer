// %3284418725:hoplugins.trainingExperience.vo%
package module.training;

import core.model.player.ISkillChange;
import core.model.player.Player;


/**
 * This value object represent a change in skill for a player.
 *
 * @author NetHyperon
 */
public class PlayerSkillChange {
    //~ Instance fields ----------------------------------------------------------------------------

    private ISkillChange skillup;
    private Player player;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SkillChange object.
     *
     * @param player Player
     * @param skillup Skillup
     */
    public PlayerSkillChange(Player player, ISkillChange skillup) {
        this.player = player;
        this.skillup = skillup;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the player
     *
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the skillup
     *
     * @return Skillup
     */
    public ISkillChange getSkillup() {
        return skillup;
    }
}
