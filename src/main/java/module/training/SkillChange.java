// %3284418725:hoplugins.trainingExperience.vo%
package module.training;

import core.model.player.ISkillup;
import core.model.player.Player;


/**
 * This value object represent a change in skill for a player.
 *
 * @author NetHyperon
 */
public class SkillChange {
    //~ Instance fields ----------------------------------------------------------------------------

    private ISkillup skillup;
    private Player player;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SkillChange object.
     *
     * @param player Player
     * @param skillup Skillup
     */
    public SkillChange(Player player, ISkillup skillup) {
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
    public ISkillup getSkillup() {
        return skillup;
    }
}
