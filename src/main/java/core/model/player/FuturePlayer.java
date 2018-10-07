package core.model.player;



public class FuturePlayer {

	private double passing;
	private double playmaking;
	private double stamina;
	private double setpieces;
	private int age;
	private double defense;
	private double attack;
	private double cross;
	private double goalkeeping;
	private int playerId;
	
	public int getAge() {
		return age;
	}

	public double getAttack() {
		return attack;
	}

	public double getCross() {
		return cross;
	}

	public double getDefense() {
		return defense;
	}

	public double getGoalkeeping() {
		return goalkeeping;
	}

	public double getPassing() {
		return passing;
	}

	public double getPlaymaking() {
		return playmaking;
	}

	public double getSetpieces() {
		return setpieces;
	}

	public double getStamina() {
		return stamina;
	}

	public void setAge(int i) {
		age = i;
	}

	public void setAttack(double i) {
		attack = i;
	}

	public void setCross(double i) {
		cross = i;
	}

	public void setDefense(double i) {
		defense = i;
	}

	public void setGoalkeeping(double i) {
		goalkeeping = i;
	}

	public void setPassing(double i) {
		passing = i;
	}

	public void setPlaymaking(double i) {
		playmaking = i;
	}

	public void setSetpieces(double i) {
		setpieces = i;
	}

	public void setStamina(double i) {
		stamina = i;
	}

	/**
	 * toString methode: creates a String representation of the object
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("FuturePlayer[");
		buffer.append("passing = "+passing);
		buffer.append(", playmaking = "+playmaking);
		buffer.append(", stamina = "+stamina);
		buffer.append(", setpieces = "+setpieces);
		buffer.append(", age = "+age);
		buffer.append(", defense = "+defense);
		buffer.append(", attack = "+attack);
		buffer.append(", cross = "+cross);
		buffer.append(", goalkeeping = "+goalkeeping);
		buffer.append("]");
		return buffer.toString();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int i) {
		playerId = i;
	}

}
