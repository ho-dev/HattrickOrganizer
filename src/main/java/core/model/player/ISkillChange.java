package core.model.player;

import core.constants.player.PlayerSkill;
import core.util.HODateTime;

public interface ISkillChange {
	
	/** Real Skillup happened in the past */
	int SKILLUP_REAL = 0;

	/** PRedicted Skillup at maximum training */
	int SKILLUP_FUTURE = 1;
	HODateTime getDate();
	int getHtSeason();
	int getHtWeek();

	int getTrainType();

	String getAge();

	/**
     * Get Training type
     *
     * @return type
     */
	PlayerSkill getType();

	/**
	* Set the new value of the skill
	*
	* @return value
	*/
	double getValue();

	int getChange();
}