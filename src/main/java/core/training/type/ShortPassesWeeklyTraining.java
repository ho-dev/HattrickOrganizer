package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.WeeklyTrainingType;

public class ShortPassesWeeklyTraining extends WeeklyTrainingType {
	protected static ShortPassesWeeklyTraining m_ciInstance = null;
	private ShortPassesWeeklyTraining()
	{
		_Name = "Short Passes";
		_TrainingType = TrainingType.SHORT_PASSES;
		_PrimaryTrainingSkill = PlayerSkill.PASSING;

		fullTrainingSectors.add(MatchRoleID.Sector.Wing);
		fullTrainingSectors.add(MatchRoleID.Sector.InnerMidfield);
		fullTrainingSectors.add(MatchRoleID.Sector.Forward);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Goal);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Back);
		osmosisTrainingSectors.add(MatchRoleID.Sector.CentralDefence);

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
	public double getTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), trainerLevel,
				intensity, stamina, player.getPSskill(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return -1;
	}
}