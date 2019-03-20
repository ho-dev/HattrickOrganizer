package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.training.WeeklyTrainingType;

public class ThroughPassesWeeklyTraining extends WeeklyTrainingType {
	protected static ThroughPassesWeeklyTraining m_ciInstance = null;
	private ThroughPassesWeeklyTraining()
	{
		_Name = "Through Passes";
		_TrainingType = TrainingType.THROUGH_PASSES;
		_PrimaryTrainingSkill = PlayerSkill.PASSING;
		_PrimaryTrainingSkillPositions = new int[]{ IMatchRoleID.leftBack,
				IMatchRoleID.rightBack, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender,
				IMatchRoleID.rightWinger, IMatchRoleID.leftWinger,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield };
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[] { IMatchRoleID.keeper,
				IMatchRoleID.leftForward, IMatchRoleID.centralForward, IMatchRoleID.rightForward};
		_PrimaryTrainingBaseLength = (float) ShortPassesWeeklyTraining.instance().getBaseTrainingLength(); // old was (float) 2.8 / (float) 0.85
		_PrimaryTrainingSkillBaseLength = (_PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_PASSING) / (float) 0.85;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new ThroughPassesWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Player player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff) {
		 return calcTraining(ThroughPassesWeeklyTraining.instance().getPrimaryTrainingSkillBaseLength(), 
	                		player.getAlter(), assistants, trainerLevel, intensity, stamina, player.getPasspiel(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Player player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}