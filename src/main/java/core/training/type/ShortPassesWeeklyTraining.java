package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.training.WeeklyTrainingType;

public class ShortPassesWeeklyTraining extends WeeklyTrainingType {
	protected static ShortPassesWeeklyTraining m_ciInstance = null;
	private ShortPassesWeeklyTraining()
	{
		_Name = "Short Passes";
		_TrainingType = TrainingType.SHORT_PASSES;
		_PrimaryTrainingSkill = PlayerSkill.PASSING;
		_PrimaryTrainingSkillPositions = new int[]{ 
				IMatchRoleID.rightWinger, IMatchRoleID.leftWinger,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftForward,
				IMatchRoleID.centralForward, IMatchRoleID.rightForward};
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[] { IMatchRoleID.keeper,
				IMatchRoleID.leftBack, IMatchRoleID.rightBack, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender };
		_PrimaryTrainingBaseLength = (float) 4.2989; // old was 2.8
		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_PASSING;
	}
	
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new ShortPassesWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Player player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
				intensity, stamina, player.getPasspiel(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Player player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}