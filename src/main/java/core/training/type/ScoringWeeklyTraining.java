package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.training.WeeklyTrainingType;

public class ScoringWeeklyTraining extends WeeklyTrainingType {
	protected static ScoringWeeklyTraining m_ciInstance = null;
	private ScoringWeeklyTraining()
	{
		_Name = "Scoring";
		_TrainingType = TrainingType.SCORING;
		_PrimaryTrainingSkill = PlayerSkill.SCORING;
		_PrimaryTrainingSkillPositions = new int[]{ 
				ISpielerPosition.leftForward, ISpielerPosition.rightForward, ISpielerPosition.centralForward };
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[]{
				ISpielerPosition.keeper, ISpielerPosition.leftBack, ISpielerPosition.rightBack,
				ISpielerPosition.leftCentralDefender, ISpielerPosition.middleCentralDefender,
				ISpielerPosition.rightCentralDefender, ISpielerPosition.rightWinger, 
				ISpielerPosition.leftWinger, ISpielerPosition.leftInnerMidfield, 
				ISpielerPosition.centralInnerMidfield, ISpielerPosition.rightInnerMidfield};
		_PrimaryTrainingBaseLength = (float) 4.8536; // old was 3.2
		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_SCORING; // 100%
		//_PrimaryTrainingSkillSecondaryLengthRate = (float) 2; // 50% there are no secondary training positions
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new ScoringWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff) 
	{
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
				intensity, stamina, player.getTorschuss(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}
