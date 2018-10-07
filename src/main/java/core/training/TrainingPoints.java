package core.training;

public class TrainingPoints {
	private double _Primary = 0;
	private double _Secondary = 0;
	public TrainingPoints()
	{
	}
	public TrainingPoints(double dPrimary, double dSecondary)
	{
		_Primary = dPrimary;
		_Secondary = dSecondary;	
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
}
