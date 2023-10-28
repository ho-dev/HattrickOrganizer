package core.model.player;

import core.util.HODateTime;

import java.util.Date;

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
	int getType();

	/**
	* Set the new value of the skill
	*
	* @return value
	*/
	double getValue();

	int getChange();
}