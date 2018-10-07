package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.training.WeeklyTrainingType;

public class CrossingWeeklyTraining extends WeeklyTrainingType {
	protected static CrossingWeeklyTraining m_ciInstance = null;
	private CrossingWeeklyTraining()
	{
		_Name = "Crossing";
		_TrainingType = TrainingType.CROSSING_WINGER;
		_PrimaryTrainingSkill = PlayerSkill.WINGER;
		_PrimaryTrainingSkillPositions = new int[]{ 
				ISpielerPosition.leftWinger, ISpielerPosition.rightWinger };
		_PrimaryTrainingSkillSecondaryTrainingPositions = new int[]{
				ISpielerPosition.leftBack, ISpielerPosition.rightBack};
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[]{
				ISpielerPosition.keeper, ISpielerPosition.leftCentralDefender, 
				ISpielerPosition.middleCentralDefender, ISpielerPosition.rightCentralDefender,
				ISpielerPosition.leftInnerMidfield, ISpielerPosition.centralInnerMidfield,
				ISpielerPosition.rightInnerMidfield, ISpielerPosition.leftForward, 
				ISpielerPosition.centralForward, ISpielerPosition.rightForward};
		_PrimaryTrainingBaseLength = (float)  3.2341; // old was 2.2
		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_WINGER; // 100%
		_PrimaryTrainingSkillSecondaryLengthRate = (float) 2; // 50%
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new CrossingWeeklyTraining();
        }
        List<StaffMember> staff;
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, 
									int stamina, List<StaffMember> staff) {
        return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
        		intensity, stamina, player.getFluegelspiel(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}
