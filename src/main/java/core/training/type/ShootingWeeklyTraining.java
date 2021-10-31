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

public class ShootingWeeklyTraining extends WeeklyTrainingType {
	protected static ShootingWeeklyTraining m_ciInstance = null;
	private ShootingWeeklyTraining()
	{
		_Name = "Shooting";
		_TrainingType = TrainingType.SHOOTING;
		_PrimaryTrainingSkill = PlayerSkill.SCORING;
		_SecondaryTrainingSkill = PlayerSkill.SET_PIECES;

		factorTrainingTypeKoeff = 1.5;

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
		_PrimaryTrainingBaseLength = ScoringWeeklyTraining.instance().getBaseTrainingLength(); // old was (float) 3.2 / (float) 0.45
		_PrimaryTrainingSkillBaseLength = (_PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_SCORING) * 100 / 60;
		_PrimaryTrainingSkillOsmosisLengthRate = 0;
		_SecondaryTrainingSkillBaseLength = (SetPiecesWeeklyTraining.instance().getBaseTrainingLength() + UserParameter.instance().TRAINING_OFFSET_SETPIECES) / (float) 0.16;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new ShootingWeeklyTraining();
        }
        return m_ciInstance;
    }
}
