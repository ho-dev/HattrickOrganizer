package core.training;

import core.model.player.Player;
import core.util.HelperWrapper;

public class TrainingWeekPlayer {
	private Player player;
	private int _PrimarySkillPositionMinutes = 0;
	private int _PrimarySkillBonusPositionMinutes = 0;
	private int _PrimarySkillSecondaryPositionMinutes = 0;
	private int _PrimarySkillOsmosisPositionMinutes = 0;
	private int _SecondarySkillPositionMinutes = 0;
	private int _SecondarySkillBonusPositionMinutes = 0;
	private int _SecondarySkillSecondaryPositionMinutes = 0;
	private int _SecondarySkillOsmosisPositionMinutes = 0;
	private int _TotalMinutesPlayed = 0;

	public TrainingWeekPlayer(Player player)
	{
		this.player = player;
	}
	public void addPrimarySkillPositionMinutes(int minutes)
	{
		if ((_PrimarySkillPositionMinutes + minutes) > 90)
			_PrimarySkillPositionMinutes = 90;
		else
			_PrimarySkillPositionMinutes += minutes;
	}
	public int getPrimarySkillPositionMinutes()
	{
		return _PrimarySkillPositionMinutes;
	}
	public void addPrimarySkillBonusPositionMinutes(int minutes)
	{
		if ((_PrimarySkillBonusPositionMinutes + minutes) > 90)
			_PrimarySkillBonusPositionMinutes = 90;
		else
			_PrimarySkillBonusPositionMinutes += minutes;
	}
	public int getPrimarySkillBonusPositionMinutes()
	{
		return _PrimarySkillBonusPositionMinutes;
	}
	public void addPrimarySkillSecondaryPositionMinutes(int minutes)
	{
		if ((_PrimarySkillSecondaryPositionMinutes + minutes) > 90)
			_PrimarySkillSecondaryPositionMinutes = 90;
		else
			_PrimarySkillSecondaryPositionMinutes += minutes;
	}
	public int getPrimarySkillSecondaryPositionMinutes()
	{
		return _PrimarySkillSecondaryPositionMinutes;
	}
	public void addPrimarySkillOsmosisPositionMinutes(int minutes)
	{
		if ((_PrimarySkillOsmosisPositionMinutes + minutes) > 90)
			_PrimarySkillOsmosisPositionMinutes = 90;
		else
			_PrimarySkillOsmosisPositionMinutes += minutes;
	}
	public int getPrimarySkillOsmosisPositionMinutes()
	{
		return _PrimarySkillOsmosisPositionMinutes;
	}
	public void addSecondarySkillPrimaryMinutes(int minutes)
	{
		if ((_SecondarySkillPositionMinutes + minutes) > 90)
			_SecondarySkillPositionMinutes = 90;
		else
			_SecondarySkillPositionMinutes += minutes;
	}
	public int getSecondarySkillPrimaryMinutes()
	{
		return _SecondarySkillPositionMinutes;
	}
	public void addSecondarySkillBonusMinutes(int minutes)
	{
		if ((_SecondarySkillBonusPositionMinutes + minutes) > 90)
			_SecondarySkillBonusPositionMinutes = 90;
		else
			_SecondarySkillBonusPositionMinutes += minutes;
	}
	public int getSecondarySkillBonusMinutes()
	{
		return _SecondarySkillBonusPositionMinutes;
	}
	public void addSecondarySkillSecondaryPositionMinutes(int minutes)
	{
		if ((_SecondarySkillSecondaryPositionMinutes + minutes) > 90)
			_SecondarySkillSecondaryPositionMinutes = 90;
		else
			_SecondarySkillSecondaryPositionMinutes += minutes;
	}
	public int getSecondarySkillSecondaryPositionMinutes()
	{
		return _SecondarySkillSecondaryPositionMinutes;
	}
	public void addSecondarySkillOsmosisTrainingMinutes(int minutes)
	{
		if ((_SecondarySkillOsmosisPositionMinutes + minutes) > 90)
			_SecondarySkillOsmosisPositionMinutes = 90;
		else
			_SecondarySkillOsmosisPositionMinutes += minutes;
	}
	public int getSecondarySkillOsmosisPositionMinutes()
	{
		return _SecondarySkillOsmosisPositionMinutes;
	}

	public void addTotalMinutesPlayed(int minutes){
		this._TotalMinutesPlayed += minutes;
	}
	public int getTotalMinutesPlayed()
	{
		return _TotalMinutesPlayed;
	}
	public String Name()
	{
		return this.player.getFullName();
	}
	public boolean PlayerHasPlayed()
	{
		return _TotalMinutesPlayed > 0;
	}

	public FuturePlayerTraining.Priority getFutureTrainingPrio(WeeklyTrainingType wt, HattrickDate hattrickWeek) {

		// get Prio from user plan
		FuturePlayerTraining.Priority prio = player.getTrainingPriority(hattrickWeek);
		if (prio != null) return prio;

		// get Prio from best position
		int position = HelperWrapper.instance().getPosition(player.getIdealPosition());

		for ( var p: wt.getPrimaryTrainingSkillBonusPositions()){
			if ( p == position) return FuturePlayerTraining.Priority.BONUS_TRAINING;
		}
		for ( var p: wt.getPrimaryTrainingSkillPositions()){
			if ( p == position) return FuturePlayerTraining.Priority.FULL_TRAINING;
		}
		for ( var p: wt.getPrimaryTrainingSkillSecondaryTrainingPositions()){
			if ( p == position) return FuturePlayerTraining.Priority.PARTIAL_TRAINING;
		}
		for ( var p: wt.getPrimaryTrainingSkillOsmosisTrainingPositions()){
			if ( p == position) return FuturePlayerTraining.Priority.OSMOSIS_TRAINING;
		}

		return null; // No training
	}
}
