package core.training;

import core.model.player.Player;

import java.time.Instant;

public class TrainingWeekPlayer {
	private Player player;
	private int fullTrainingMinutes = 0;
	private int bonusTrainingMinutes = 0;
	private int partlyTrainingMinutes = 0;
	private int osmosisTrainingMinutes = 0;
	/*	private int _SecondarySkillPositionMinutes = 0;
		private int _SecondarySkillBonusPositionMinutes = 0;
		private int _SecondarySkillSecondaryPositionMinutes = 0;
		private int _SecondarySkillOsmosisPositionMinutes = 0;*/
	private int playedMinutes = 0;

	public TrainingWeekPlayer(Player player) {
		this.player = player;
	}

	public void addFullTrainingMinutes(int minutes) {
		if ((fullTrainingMinutes + minutes) > 90)
			fullTrainingMinutes = 90;
		else
			fullTrainingMinutes += minutes;
	}

	public int getFullTrainingMinutes() {
		return fullTrainingMinutes;
	}

	public void addBonusTrainingMinutes(int minutes) {
		if ((bonusTrainingMinutes + minutes) > 90)
			bonusTrainingMinutes = 90;
		else
			bonusTrainingMinutes += minutes;
	}

	public int getBonusTrainingMinutes() {
		return bonusTrainingMinutes;
	}

	public void addPartlyTrainingMinutes(int minutes) {
		if ((partlyTrainingMinutes + minutes) > 90)
			partlyTrainingMinutes = 90;
		else
			partlyTrainingMinutes += minutes;
	}

	public int getPartlyTrainingMinutes() {
		return partlyTrainingMinutes;
	}

	public void addOsmosisTrainingMinutes(int minutes) {
		if ((osmosisTrainingMinutes + minutes) > 90)
			osmosisTrainingMinutes = 90;
		else
			osmosisTrainingMinutes += minutes;
	}

	public int getOsmosisTrainingMinutes() {
		return osmosisTrainingMinutes;
	}
	/*
	public void addSecondarySkillMinutes(int minutes)
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
	}*/

	public void addPlayedMinutes(int minutes) {
		this.playedMinutes += minutes;
	}

	public int getPlayedMinutes() {
		return playedMinutes;
	}

	public String Name() {
		return this.player.getFullName();
	}

	public FuturePlayerTraining.Priority getFutureTrainingPrio(WeeklyTrainingType wt, Instant trainingDate) {
		return player.getTrainingPriority(wt, trainingDate);
	}
}
