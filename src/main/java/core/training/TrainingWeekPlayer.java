package core.training;

public class TrainingWeekPlayer {
	private String pName = "";
	private int _PrimarySkillPositionMinutes = 0;
	private int _PrimarySkillBonusPositionMinutes = 0;
	private int _PrimarySkillSecondaryPositionMinutes = 0;
	private int _PrimarySkillOsmosisPositionMinutes = 0;
	private int _SecondarySkillPositionMinutes = 0;
	private int _SecondarySkillBonusPositionMinutes = 0;
	private int _SecondarySkillSecondaryPositionMinutes = 0;
	private int _SecondarySkillOsmosisPositionMinutes = 0;
	private int _TotalMinutesPlayed = 0;

	public TrainingWeekPlayer()
	{
	}
	public void addPrimarySkillPositionMinutes(int minutes)
	{
		if ((_PrimarySkillPositionMinutes + minutes) > 90)
			_PrimarySkillPositionMinutes = 90;
		else
			_PrimarySkillPositionMinutes += minutes;
		
		_TotalMinutesPlayed += minutes;
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
		
		_TotalMinutesPlayed += minutes;
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
		
		_TotalMinutesPlayed += minutes;
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
		
		_TotalMinutesPlayed += minutes;
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
	public int getMinutesPlayed()
	{
		return _TotalMinutesPlayed;
	}
	public String Name()
	{
		return pName;
	}
	public void Name(String sName)
	{
		pName = sName;
	}
	public boolean PlayerHasPlayed()
	{
		return _TotalMinutesPlayed > 0;
	}
}
