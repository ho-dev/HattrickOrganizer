package module.training;

import core.model.player.Player;
import core.model.player.SkillChange;

/**
 * This value object represent a change in skill for a player.
 *
 * @author NetHyperon
 */
public class PlayerSkillChange {

    private final SkillChange skillChange;
    private final Player player;

    /**
     * Creates a new SkillChange object.
     *
     * @param player Player
     * @param skillup Skillup
     */
    public PlayerSkillChange(Player player, SkillChange skillup) {
        this.player = player;
        this.skillChange = skillup;
    }

    /**
     * Get the player
     *
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the skill change
     *
     * @return SkillChange
     */
    public SkillChange getSkillChange() {
        return skillChange;
    }
}
