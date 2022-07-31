package core.training;

public class TrainingPoints {
	private TrainingWeekPlayer trainingDuration;
	private WeeklyTrainingType trainingtype;
	private double _Primary;
	private double _Secondary;

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
