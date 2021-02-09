package core.training;

public class TrainingPoints {
	private TrainingWeekPlayer trainingDuration;
	private WeeklyTrainingType trainingtype;
	private double _Primary = 0;
	private double _Secondary = 0;

	public TrainingPoints(double dPrimary, double dSecondary)
	{
		_Primary = dPrimary;
		_Secondary = dSecondary;	
	}

	public TrainingPoints(WeeklyTrainingType wt, TrainingWeekPlayer tp) {
		_Primary = wt.getPrimaryTraining(tp);
		_Secondary = wt.getSecondaryTraining(tp);
		trainingtype=wt;
		trainingDuration=tp;
	}

	public double getPrimary()
	{
		return _Primary;
	}
	public double getSecondary()
	{
		return _Secondary;
	}
	public void addPrimary(double Points)
	{
		_Primary += Points;
	}
	public void addSecondary(double Points)
	{
		_Secondary += Points;
	}

	public TrainingWeekPlayer getTrainingDuration() {
		return trainingDuration;
	}

	public WeeklyTrainingType getTrainingtype() {
		return trainingtype;
	}
}
