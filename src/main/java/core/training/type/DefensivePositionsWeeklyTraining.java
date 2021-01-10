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

public class DefensivePositionsWeeklyTraining extends WeeklyTrainingType {
	protected static DefensivePositionsWeeklyTraining m_ciInstance = null;
	private DefensivePositionsWeeklyTraining()
	{
		_Name = "Defensive Positions";
		_TrainingType = TrainingType.DEF_POSITIONS;
		_PrimaryTrainingSkill = PlayerSkill.DEFENDING;

		fullTrainingSectors.add(MatchRoleID.Sector.Goal);
		fullTrainingSectors.add(MatchRoleID.Sector.Back);
		fullTrainingSectors.add(MatchRoleID.Sector.CentralDefence);
		fullTrainingSectors.add(MatchRoleID.Sector.Wing);
		fullTrainingSectors.add(MatchRoleID.Sector.InnerMidfield);

		osmosisTrainingSectors.add(MatchRoleID.Sector.Forward);

		_PrimaryTrainingSkillPositions = new int[]{ IMatchRoleID.keeper, IMatchRoleID.leftBack,
				IMatchRoleID.rightBack, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender,
				IMatchRoleID.rightWinger, IMatchRoleID.leftWinger,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield };
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[] { IMatchRoleID.leftForward,
				IMatchRoleID.centralForward, IMatchRoleID.rightForward};
		_PrimaryTrainingBaseLength = DefendingWeeklyTraining.instance().getBaseTrainingLength(); // old was (float) 7.2
		_PrimaryTrainingSkillBaseLength = (_PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_DEFENDING) * 2;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new DefensivePositionsWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		 return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), trainerLevel,
				 intensity, stamina, player.getDEFskill(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return -1;
	}
}
