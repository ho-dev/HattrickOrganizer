package module.opponentspy;


import core.constants.player.PlayerSkill;


public class OppPlayerSkillEstimator {
	
	private CalcPlayerBaseProvider calcPlayerBaseProvider;
	private SkillAdjuster skillAdjuster;
	
	public OppPlayerSkillEstimator() {
		calcPlayerBaseProvider = new CalcPlayerBaseProvider();
		skillAdjuster = new SkillAdjuster();
	}

	public OpponentPlayer calcPlayer(int age, int wage, int tsi, double form, double stamina, int spec, int role, int injury_status) {
       			
    	CalcVariables calcPlayer = calcPlayerBaseProvider.getCalcPlayerBase(age, wage, tsi, form, stamina, spec, role, injury_status);
        skillAdjuster.AdjustPlayer(calcPlayer);
    	
        return createPlayer(null, role, calcPlayer);
    }
    
    private OpponentPlayer createPlayer(OpponentPlayer player, int role, CalcVariables calcPlayer) { 
		
    	if (player == null)
    		player = new OpponentPlayer();
    	
    	player.setPosition(role);
		
    	player.setGoalkeeperSkill((int)Math.floor(calcPlayer.getGoalkeeping()));
    	player.setSubskill4PlayerSkill(PlayerSkill.KEEPER, getSubskillFromSkill(calcPlayer.getGoalkeeping()));
    	player.setDefendingSkill((int)Math.floor(calcPlayer.getDefending()));
    	player.setSubskill4PlayerSkill(PlayerSkill.DEFENDING, getSubskillFromSkill(calcPlayer.getDefending()));
    	player.setPlaymakingSkill((int)Math.floor(calcPlayer.getPlaymaking()));
    	player.setSubskill4PlayerSkill(PlayerSkill.PLAYMAKING, getSubskillFromSkill(calcPlayer.getPlaymaking()));
    	player.setPassingSkill((int)Math.floor(calcPlayer.getPassing()));
    	player.setSubskill4PlayerSkill(PlayerSkill.PASSING, getSubskillFromSkill(calcPlayer.getPassing()));
    	player.setWingerSkill((int)Math.floor(calcPlayer.getWing()));
    	player.setSubskill4PlayerSkill(PlayerSkill.WINGER, getSubskillFromSkill(calcPlayer.getWing()));
    	player.setScoringSkill((int)Math.floor(calcPlayer.getScoring()));
    	player.setSubskill4PlayerSkill(PlayerSkill.SCORING, getSubskillFromSkill(calcPlayer.getScoring()));
    	player.setSetPiecesSkill((int)Math.floor(calcPlayer.getSetPieces()));
    	player.setSubskill4PlayerSkill(PlayerSkill.SET_PIECES, getSubskillFromSkill(calcPlayer.getSetPieces()));
    	player.setStamina((int) Math.floor(calcPlayer.getStamina()));
    	
    	return player;
    }
	
    private int getSubskillFromSkill(double skill)	{
		
		return (int)( (skill - Math.floor(skill)) * 100 );
	}
}
