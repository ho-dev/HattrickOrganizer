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

public final class PlaymakingWeeklyTraining extends WeeklyTrainingType {
	protected static PlaymakingWeeklyTraining m_ciInstance = null;
	private PlaymakingWeeklyTraining()
	{
		_Name = "Playmaking";
		_TrainingType = TrainingType.PLAYMAKING;
		_PrimaryTrainingSkill = PlayerSkill.PLAYMAKING;

		fullTrainingSectors.add(MatchRoleID.Sector.InnerMidfield);
		partlyTrainingSectors.add(MatchRoleID.Sector.Wing);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Back);
		osmosisTrainingSectors.add(MatchRoleID.Sector.CentralDefence);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Forward);

		_PrimaryTrainingSkillPositions = new int[]{ 
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.rightInnerMidfield,
				IMatchRoleID.centralInnerMidfield};
		_PrimaryTrainingSkillPartlyTrainingPositions = new int[]{
				IMatchRoleID.leftWinger, IMatchRoleID.rightWinger};
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[]{
				IMatchRoleID.keeper, IMatchRoleID.rightBack, IMatchRoleID.leftBack,
				IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender,
				IMatchRoleID.rightCentralDefender, IMatchRoleID.leftForward,
				IMatchRoleID.centralForward, IMatchRoleID.rightForward};
		_PrimaryTrainingBaseLength = (float) 4.6613; // old was 3.1
		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_PLAYMAKING; // 100%
		_PrimaryTrainingSkillPartlyLengthRate = (float) 2; // 50%
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new PlaymakingWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), trainerLevel,
				intensity, stamina, player.getPMskill(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return -1;
	}

}
