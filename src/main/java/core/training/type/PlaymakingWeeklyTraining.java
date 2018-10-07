package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.training.WeeklyTrainingType;

public final class PlaymakingWeeklyTraining extends WeeklyTrainingType {
	protected static PlaymakingWeeklyTraining m_ciInstance = null;
	private PlaymakingWeeklyTraining()
	{
		_Name = "Playmaking";
		_TrainingType = TrainingType.PLAYMAKING;
		_PrimaryTrainingSkill = PlayerSkill.PLAYMAKING;
		_PrimaryTrainingSkillPositions = new int[]{ 
				ISpielerPosition.leftInnerMidfield, ISpielerPosition.rightInnerMidfield, 
				ISpielerPosition.centralInnerMidfield};
		_PrimaryTrainingSkillSecondaryTrainingPositions = new int[]{
				ISpielerPosition.leftWinger, ISpielerPosition.rightWinger};
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[]{
				ISpielerPosition.keeper, ISpielerPosition.rightBack, ISpielerPosition.leftBack, 
				ISpielerPosition.leftCentralDefender, ISpielerPosition.middleCentralDefender, 
				ISpielerPosition.rightCentralDefender, ISpielerPosition.leftForward, 
				ISpielerPosition.centralForward, ISpielerPosition.rightForward};
		_PrimaryTrainingBaseLength = (float) 4.6613; // old was 3.1
		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_PLAYMAKING; // 100%
		_PrimaryTrainingSkillSecondaryLengthRate = (float) 2; // 50%
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new PlaymakingWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff) 
	{
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
				intensity, stamina, player.getSpielaufbau(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}
