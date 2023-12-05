package core.model.player;

import core.constants.player.PlayerSkill;
import core.util.HODateTime;

/**
 * Skill change
 */
public class SkillChange {

    /**
     * Date when the skill change was downloaded
     */
    private HODateTime date;

    /**
     * Skill type
     */
    private PlayerSkill type;

    /**
     * New value of skill
     */
    private double value;

    /**
     * Difference of new and old skill value
     */
    private int change;

    public void setDate(HODateTime date) {
        this.date = date;
    }

    public HODateTime getDate() {
        return date;
    }

    public int getHtSeason() {
        return date.toHTWeek().season;
    }

    public int getHtWeek() {
        return date.toHTWeek().week;
    }

    /**
     * Set training type
     *
     * @param type Player skill type
     */
    public void setType(PlayerSkill type) {
        this.type = type;
    }

    /**
     * Get Training type
     *
     * @return type
     */
    public PlayerSkill getType() {
        return type;
    }

    /**
     * Set the new value of the skill
     *
     * @param newValue Double
     */
    public void setValue(double newValue) {
        value = newValue;
    }

    /**
     * Set the new value of the skill
     *
     * @return value
     */
    public double getValue() {
        return value;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        if (change > 0) {
            buffer.append("Skillup["); //$NON-NLS-1$
        } else if (change < 0) {
            buffer.append("Skilldrop["); //$NON-NLS-1$
        }
        buffer.append(", type = ").append(type); //$NON-NLS-1$
        buffer.append(", value = ").append(value); //$NON-NLS-1$
        buffer.append(", htSeason = ").append(getHtSeason()); //$NON-NLS-1$
        buffer.append(", htWeek = ").append(getHtWeek()); //$NON-NLS-1$
        buffer.append("]"); //$NON-NLS-1$

        return buffer.toString();
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }
}