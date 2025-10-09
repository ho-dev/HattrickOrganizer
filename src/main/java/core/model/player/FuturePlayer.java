package core.model.player;



public class FuturePlayer {

	private double passing;
	private double playmaking;
	private double stamina;
	private double form;
	private double setpieces;
	private int age;
	private double defense;
	private double attack;
	private double cross;
	private double goalkeeping;

	public double getExperience() {
		return experience;
	}

	public void setExperience(double experience) {
		this.experience = experience;
	}

	private double experience;
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

	public double getForm() {
		return form;
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

	public void setForm(double i) {
		form = i;
	}

	/**
	 * toString methode: creates a String representation of the object
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	
	 */
	@Override
	public String toString() {
        return "FuturePlayer[" +
                "passing = " + passing +
                ", playmaking = " + playmaking +
                ", stamina = " + stamina +
                ", form = " + form +
                ", setpieces = " + setpieces +
                ", age = " + age +
                ", defense = " + defense +
                ", attack = " + attack +
                ", cross = " + cross +
                ", goalkeeping = " + goalkeeping +
                "]";
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int i) {
		playerId = i;
	}

}
