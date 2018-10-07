package module.opponentspy;


import core.constants.player.PlayerSkill;


public class OppPlayerSkillEstimator {
	
	private CalcPlayerBaseProvider calcPlayerBaseProvider = null;
	private SkillAdjuster skillAdjuster = null;
	
	public OppPlayerSkillEstimator() {
		calcPlayerBaseProvider = new CalcPlayerBaseProvider();
		skillAdjuster = new SkillAdjuster();
	}
	
	public void CalculateSkillsForPlayer(OpponentPlayer player) {
		
		if (player == null)
			return;
		
		CalcVariables calcPlayer = CreateCalcPlayerFromOpponent(player);
		calcPlayerBaseProvider.setBaseSkills(calcPlayer);
		
		skillAdjuster.AdjustPlayer(calcPlayer);
		
		createPlayer(player, player.getCalculationRole(), calcPlayer);
		
	}
	
	private CalcVariables CreateCalcPlayerFromOpponent(OpponentPlayer player) {
		CalcVariables calcPlayer = new CalcVariables();
		
		calcPlayer.age = player.getAlter();
		calcPlayer.ageDays = player.getAgeDays();
		calcPlayer.form = player.getForm();
		calcPlayer.injuryStatus = player.getVerletzt();
		calcPlayer.isPlayingAbroad = player.isPlayingAbroad();
		calcPlayer.specialty = player.getSpezialitaet();
		calcPlayer.stamina = player.getKondition();
		calcPlayer.tsi = player.getTSI();
		calcPlayer.wage = player.getGehalt();
		calcPlayer.role = player.getCalculationRole();
		
		return calcPlayer;
	}
	
    
    public OpponentPlayer calcPlayer(int age, int wage, int tsi, double form, double stamina, int spec, int role) {
       			
    	CalcVariables calcPlayer = calcPlayerBaseProvider.getCalcPlayerBase(age, wage, tsi, form, stamina, spec, role);
        skillAdjuster.AdjustPlayer(calcPlayer);
    	
        return createPlayer(null, role, calcPlayer);
    }
    
    
    private OpponentPlayer createPlayer(OpponentPlayer player, int role, CalcVariables calcPlayer) { 
		
    	if (player == null)
    		player = new OpponentPlayer();
    	
    	player.setPosition(role);
		
    	player.setTorwart((int)Math.floor(calcPlayer.getGoalkeeping()));	
    	player.setSubskill4Pos(PlayerSkill.KEEPER, getSubskillFromSkill(calcPlayer.getGoalkeeping()));
    	player.setVerteidigung ((int)Math.floor(calcPlayer.getDefending()));
    	player.setSubskill4Pos(PlayerSkill.DEFENDING, getSubskillFromSkill(calcPlayer.getDefending()));
    	player.setSpielaufbau ((int)Math.floor(calcPlayer.getPlaymaking()));
    	player.setSubskill4Pos(PlayerSkill.PLAYMAKING, getSubskillFromSkill(calcPlayer.getPlaymaking()));
    	player.setPasspiel ((int)Math.floor(calcPlayer.getPassing()));
    	player.setSubskill4Pos(PlayerSkill.PASSING, getSubskillFromSkill(calcPlayer.getPassing()));
    	player.setFluegelspiel ((int)Math.floor(calcPlayer.getWing()));
    	player.setSubskill4Pos(PlayerSkill.WINGER, getSubskillFromSkill(calcPlayer.getWing()));
    	player.setTorschuss ((int)Math.floor(calcPlayer.getScoring()));
    	player.setSubskill4Pos(PlayerSkill.SCORING, getSubskillFromSkill(calcPlayer.getScoring()));
    	player.setStandards ((int)Math.floor(calcPlayer.getSetPieces()));
    	player.setSubskill4Pos(PlayerSkill.SET_PIECES, getSubskillFromSkill(calcPlayer.getSetPieces()));
    	
    	return player;
    }
	
    private int getSubskillFromSkill(double skill)	{
		
		return (int)( (skill - Math.floor(skill)) * 100 );
	}
}
