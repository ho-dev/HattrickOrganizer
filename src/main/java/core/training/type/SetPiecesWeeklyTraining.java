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

public class SetPiecesWeeklyTraining extends WeeklyTrainingType {
	protected static SetPiecesWeeklyTraining m_ciInstance = null;
	private SetPiecesWeeklyTraining()
	{
		_Name = "Set Pieces";
		_TrainingType = TrainingType.SET_PIECES;
		_PrimaryTrainingSkill = PlayerSkill.SET_PIECES;
		_SecondaryTrainingSkill = 0;

		bonusTrainingSectors.add(MatchRoleID.Sector.Goal);
		bonusTrainingSectors.add(MatchRoleID.Sector.SetPiecesTaker);

		fullTrainingSectors.add(MatchRoleID.Sector.Goal);
		fullTrainingSectors.add(MatchRoleID.Sector.Back);
		fullTrainingSectors.add(MatchRoleID.Sector.CentralDefence);
		fullTrainingSectors.add(MatchRoleID.Sector.Wing);
		fullTrainingSectors.add(MatchRoleID.Sector.InnerMidfield);
		fullTrainingSectors.add(MatchRoleID.Sector.Forward);

		_PrimaryTrainingSkillPositions = new int[]{ IMatchRoleID.keeper,
				IMatchRoleID.leftBack, IMatchRoleID.rightBack, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender,
				IMatchRoleID.rightWinger, IMatchRoleID.leftWinger,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftForward,
				IMatchRoleID.centralForward, IMatchRoleID.rightForward};
		_PrimaryTrainingSkillBonusPositions = new int[]{ IMatchRoleID.keeper, IMatchRoleID.setPieces };
		_PrimaryTrainingBaseLength = (float) 0.9938; // old was 0.9
		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_SETPIECES; // 100%
		_PrimaryTrainingSkillBonus = (float) 0.25;
		_PrimaryTrainingSkillOsmosisLengthRate = 0;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new SetPiecesWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), trainerLevel,
				intensity, stamina, player.getSPskill(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return -1;
	}
}
