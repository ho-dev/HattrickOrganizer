package core.epv;

import core.constants.player.PlayerSkill;
import core.model.player.Player;


/**
 * Holder Class for data needed by EPV function
 *
 * @author draghetto
 */
public class EPVData implements Cloneable {
    
	public static final int KEEPER = 1;
	public static final int DEFENDER = 2;
	public static final int MIDFIELDER = 3;
	public static final int WINGER = 4;
	public static final int ATTACKER = 5;
	
	//~ Instance fields ----------------------------------------------------------------------------
	
	private String playerName;
	private int playerId;	

	private int age;
	private int ageDays;
	
	private int form;
	private int TSI;

	private int experience;
	private int leadership;

	private double attack;
	private double defense;
	private double goalKeeping;
	private double passing;
	private double playMaking;
	private double setPieces;
	private double stamina;
	private double wing;

	private int price;

	private int speciality;
	private int week;
	
	private int aggressivity;
	private int honesty;
	private int popularity;
	
	private double maxSkill;


    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new EPVData object.
     */
    public EPVData(Player s) {
        setPlayerName(s.getFullName());
        setAge(s.getAlter());
        setAgeDays(s.getAgeDays());
        setTSI(s.getTSI());
        setForm(s.getForm());
        setStamina(s.getKondition() + s.getSubskill4Pos(PlayerSkill.STAMINA));
        setGoalKeeping(s.getGKskill() + s.getSubskill4Pos(PlayerSkill.KEEPER));
        setPlayMaking(s.getPMskill()
                      + s.getSubskill4Pos(PlayerSkill.PLAYMAKING));
        setPassing(s.getPSskill() + s.getSubskill4Pos(PlayerSkill.PASSING));
        setWing(s.getWIskill() + s.getSubskill4Pos(PlayerSkill.WINGER));
        setDefense(s.getDEFskill() + s.getSubskill4Pos(PlayerSkill.DEFENDING));
        setAttack(s.getSCskill() + s.getSubskill4Pos(PlayerSkill.SCORING));
        setSetPieces(s.getSPskill() + s.getSubskill4Pos(PlayerSkill.SET_PIECES));

        setExperience(s.getErfahrung());
        setLeadership(s.getFuehrung());
        setAggressivity(s.getAgressivitaet());
        setHonesty(s.getCharakter());
        setPopularity(s.getAnsehen());
        setSpeciality(s.getPlayerSpecialty());
        
        setPlayerId(s.getSpielerID());
        
        normalizeSkill();
    }
    
  


	//~ Methods ------------------------------------------------------------------------------------

	public void setAge(int i) {
		age = i;
	}

	//	private String aggressivness;
	//	private String gentleness;
	//	private String character;
	public int getAge() {
		return age;
	}

	public int getAgeDays() {
		return ageDays;
	}

	public void setAgeDays(int ageDays) {
		this.ageDays = ageDays;
	}
	
	public void setAttack(double i) {
		attack = i;
		checkMaxSkill(i);
	}

	public double getAttack() {
		return attack;
	}

	public void setDefense(double i) {
		defense = i;
		checkMaxSkill(i);		
	}

	public double getDefense() {
		return defense;
	}

	public void setExperience(int i) {
		experience = i;
	}

	public int getExperience() {
		return experience;
	}

	public void setForm(int i) {
		form = i;
	}

	public int getForm() {
		return form;
	}

	public void setGoalKeeping(double i) {
		goalKeeping = i;
		checkMaxSkill(i);		
	}

	public double getGoalKeeping() {
		return goalKeeping;
	}

	public void setId(int i) {
		playerId = i;
	}

	public int getId() {
		return playerId;
	}

	public void setLeadership(int i) {
		leadership = i;
	}

	public int getLeadership() {
		return leadership;
	}

	public void setPassing(double i) {
		passing = i;
		checkMaxSkill(i);		
	}

	public double getPassing() {
		return passing;
	}

	public void setPlayMaking(double i) {
		playMaking = i;
		checkMaxSkill(i);		
	}

	public double getPlayMaking() {
		return playMaking;
	}

	public void setPlayerName(String string) {
		playerName = string;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPrice(int i) {
		price = i;
	}

	public int getPrice() {
		return price;
	}

	public void setStamina(double i) {
		stamina = i;
	}

	public double getStamina() {
		return stamina;
	}

	public void setSetPieces(double i) {
		setPieces = i;
	}

	public double getSetPieces() {
		return setPieces;
	}

	public void setSpeciality(int se) {
		speciality = se;
	}

	public int getSpeciality() {
		return speciality;
	}

	public void setTSI(int i) {
		TSI = i;
	}

	public int getTSI() {
		return TSI;
	}

	public void setWing(double i) {
		wing = i;
		checkMaxSkill(i);		
	}

	public double getWing() {
		return wing;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int i) {
		playerId = i;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int i) {
		week = i;
	}

	public int getAggressivity() {
		return aggressivity;
	}

	public void setAggressivity(int aggressivity) {
		this.aggressivity = aggressivity;
	}

	public int getHonesty() {
		return honesty;
	}

	public void setHonesty(int honesty) {
		this.honesty = honesty;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	/**
	 * toString methode: creates a String representation of the object
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("EPVData[");
		buffer.append("playerName = "+playerName);
		buffer.append(", speciality = "+speciality);
		buffer.append(", TSI = "+TSI);
		buffer.append(", age = "+age);
		buffer.append(", attack = "+attack);
		buffer.append(", defense = "+defense);
		buffer.append(", experience = "+experience);
		buffer.append(", form = "+form);
		buffer.append(", goalKeeping = "+goalKeeping);
		buffer.append(", leadership = "+leadership);
		buffer.append(", passing = "+passing);
		buffer.append(", playMaking = "+playMaking);
		buffer.append(", playerId = "+playerId);
		buffer.append(", price = "+price);
		buffer.append(", setPieces = "+setPieces);
		buffer.append(", stamina = "+stamina);
		buffer.append(", wing = "+wing);
		buffer.append(", week = "+week);
		buffer.append("]");
		return buffer.toString();
	}

	private void checkMaxSkill(double value) {
		if (value>maxSkill) {
			maxSkill = value;		
		}
	}
	
	public int getPlayerType() {
		if ( (playMaking>=defense) && (playMaking>=wing) &&(playMaking>=attack) &&(playMaking>=goalKeeping) ){
			return MIDFIELDER;
		}
		if ( (defense>=wing) &&(defense>=attack) &&(defense>=goalKeeping) ){
			return DEFENDER;
		}
		if ( (wing>=attack) &&(wing>=goalKeeping) ){
			return WINGER;
		}
		if ( attack>=goalKeeping) {
			return ATTACKER;
		}
		return KEEPER;		
	}
	
	public double getMaxSkill() {
		return maxSkill;
	}


	public void normalizeSkill() {
		// Removed Disastrous since cause problems in calculation
		if (wing<2) { wing = 2; }
		if (passing<2) { passing = 2; }
		if (playMaking<2) { playMaking = 2; }
		if (defense<2) { defense = 2; }
		if (setPieces<2) { setPieces = 2; }
		if (attack<2) { attack = 2; }						
		if (goalKeeping<2) { goalKeeping = 2; }
		if (stamina<2) { stamina = 2; }				
	}

}
