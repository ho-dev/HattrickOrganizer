package module.opponentspy;

import java.util.ArrayList;
import java.util.List;

import core.constants.player.PlayerSkill;

public class CalcVariables {

	public int age;
	public int ageDays;
	public int injuryStatus;
	public double form;
	public double stamina;
	public int tsi;
	public int wage;
	public boolean isPlayingAbroad;
	public int role;
	public int specialty;

	private Skill 	goalkeeping	= new Skill(PlayerSkill.KEEPER);
	private Skill 	playmaking	= new Skill(PlayerSkill.PLAYMAKING);
	private Skill 	defending	= new Skill(PlayerSkill.DEFENDING);
	private Skill 	wing		= new Skill(PlayerSkill.WINGER);
	private Skill 	scoring		= new Skill(PlayerSkill.SCORING);
	private Skill 	passing		= new Skill(PlayerSkill.PASSING);
	private Skill 	setPieces	= new Skill(PlayerSkill.SET_PIECES);	
	
	private List<Skill> skills;
	
	public int 		calculatedTSI;
	public int		calculatedWage;
	public int 		goalkeeperCalculatedWage;

	public CalcVariables() {
		skills = new ArrayList<>();
		skills.add(goalkeeping);
		skills.add(playmaking);
		skills.add(defending);
		skills.add(wing);
		skills.add(scoring);
		skills.add(passing);
		skills.add(setPieces);
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public double getGoalkeeping() {
		return goalkeeping.skillValue;
	}
	
	public void setGoalkeeping(double goalkeeping) {
		this.goalkeeping.skillValue = goalkeeping;
		this.goalkeeping.priority = 1;
	}
	
	public void setGoalkeeping(double skillValue, double priority) {
		this.goalkeeping.priority = priority;
		this.goalkeeping.skillValue = skillValue;
	}

	public double getPlaymaking() {
		return playmaking.skillValue;
	}
	

	public void setPlaymaking(double skillValue, double priority) {
		this.playmaking.priority = priority;
		this.playmaking.skillValue = skillValue;
	}

	public void setPlaymaking(double playmaking) {
		this.playmaking.skillValue = playmaking;
		this.playmaking.priority = 1;
	}

	public double getDefending() {
		return defending.skillValue;
	}
	
	public void setDefending(double skillValue, double priority) {
		this.defending.priority = priority;
		this.defending.skillValue = skillValue;
	}

	public void setDefending(double defending) {
		this.defending.skillValue = defending;
		this.defending.priority = 1;
	}

	public double getWing() {
		return wing.skillValue;
	}
	
	public void setWing(double skillValue, double priority) {
		this.wing.priority = priority;
		this.wing.skillValue = skillValue;
	}
	
	public void setWing(double wing) {
		this.wing.skillValue = wing;
		this.wing.priority = 1;
	}

	public double getScoring() {
		return scoring.skillValue;
	}
	
	public void setScoring(double skillValue, double priority) {
		this.scoring.priority = priority;
		this.scoring.skillValue = skillValue;
	}

	public void setScoring(double scoring) {
		this.scoring.skillValue = scoring;
		this.scoring.priority = 1;
	}

	public double getPassing() {
		return passing.skillValue;
	}
	
	public void setPassing(double skillValue, double priority) {
		this.passing.priority = priority;
		this.passing.skillValue = skillValue;
	}
	
	public void setPassing(double passing) {
		this.passing.skillValue = passing;
		this.passing.priority = 1;
	}

	public double getSetPieces() {
		return setPieces.skillValue;
	}
	
	public void setSetPieces(double skillValue, double priority) {
		this.setPieces.priority = priority;
		this.setPieces.skillValue = skillValue;
	}

	public void setSetPieces(double setPieces) {
		this.setPieces.skillValue = setPieces;
		this.setPieces.priority = 1;
	}

	public double getStamina(){
		return stamina;
	}
	
	public class Skill {
		Skill(int skillType) {
			this.skillType = skillType;
		}
		
		public double skillValue = 0;
		public int skillType = 0;
		public double priority = 0;
		public boolean isMainSkill = false;
	}
}
