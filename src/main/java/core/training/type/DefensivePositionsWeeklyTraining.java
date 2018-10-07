package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.training.WeeklyTrainingType;

public class DefensivePositionsWeeklyTraining extends WeeklyTrainingType {
	protected static DefensivePositionsWeeklyTraining m_ciInstance = null;
	private DefensivePositionsWeeklyTraining()
	{
		_Name = "Defensive Positions";
		_TrainingType = TrainingType.DEF_POSITIONS;
		_PrimaryTrainingSkill = PlayerSkill.DEFENDING;
		_PrimaryTrainingSkillPositions = new int[]{ ISpielerPosition.keeper, ISpielerPosition.leftBack, 
				ISpielerPosition.rightBack, ISpielerPosition.leftCentralDefender,
				ISpielerPosition.middleCentralDefender, ISpielerPosition.rightCentralDefender,
				ISpielerPosition.rightWinger, ISpielerPosition.leftWinger, 
				ISpielerPosition.leftInnerMidfield, ISpielerPosition.centralInnerMidfield, 
				ISpielerPosition.rightInnerMidfield };
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[] { ISpielerPosition.leftForward, 
				ISpielerPosition.centralForward, ISpielerPosition.rightForward}; 
		_PrimaryTrainingBaseLength = (float) DefendingWeeklyTraining.instance().getBaseTrainingLength(); // old was (float) 7.2
		_PrimaryTrainingSkillBaseLength = (_PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_DEFENDING) * 2;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new DefensivePositionsWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff) 
	{
		 return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
				 intensity, stamina, player.getVerteidigung(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}
