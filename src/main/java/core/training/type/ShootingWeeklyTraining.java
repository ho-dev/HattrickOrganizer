package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.training.WeeklyTrainingType;

public class ShootingWeeklyTraining extends WeeklyTrainingType {
	protected static ShootingWeeklyTraining m_ciInstance = null;
	private ShootingWeeklyTraining()
	{
		_Name = "Shooting";
		_TrainingType = TrainingType.SHOOTING;
		_PrimaryTrainingSkill = PlayerSkill.SCORING;
		_SecondaryTrainingSkill = PlayerSkill.SET_PIECES;
		_PrimaryTrainingSkillPositions = new int[]{ IMatchRoleID.keeper,
				IMatchRoleID.leftBack, IMatchRoleID.rightBack, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender,
				IMatchRoleID.rightWinger, IMatchRoleID.leftWinger,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftForward,
				IMatchRoleID.centralForward, IMatchRoleID.rightForward};
		_PrimaryTrainingBaseLength = ScoringWeeklyTraining.instance().getBaseTrainingLength(); // old was (float) 3.2 / (float) 0.45
		_PrimaryTrainingSkillBaseLength = (_PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_SCORING) * 100 / 60;
		_PrimaryTrainingSkillOsmosisLengthRate = 0;
		/*_SecondaryTrainingSkillPositions = new int[]{ IMatchRoleID.keeper,
				IMatchRoleID.leftBack, IMatchRoleID.rightBack, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender,
				IMatchRoleID.rightWinger, IMatchRoleID.leftWinger,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftForward,
				IMatchRoleID.centralForward, IMatchRoleID.rightForward};*/
		_SecondaryTrainingSkillBaseLength = (SetPiecesWeeklyTraining.instance().getBaseTrainingLength() + UserParameter.instance().TRAINING_OFFSET_SETPIECES) / (float) 0.16;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new ShootingWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Player player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
				intensity, stamina, player.getSCskill(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Player player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return calcTraining(getSecondaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
				intensity, stamina, player.getSPskill(), staff);
	}
}
