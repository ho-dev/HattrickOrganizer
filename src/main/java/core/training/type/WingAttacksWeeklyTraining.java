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

public class WingAttacksWeeklyTraining extends WeeklyTrainingType {
	protected static WingAttacksWeeklyTraining m_ciInstance = null;
	private WingAttacksWeeklyTraining()
	{
		_Name = "Wing Attacks";
		_TrainingType = TrainingType.WING_ATTACKS;
		_PrimaryTrainingSkill = PlayerSkill.WINGER;

		factorTrainingTypeKoeff = 3.12;
		osmosisKoeff = 5./39.;

		fullTrainingSectors.add(MatchRoleID.Sector.Wing);
		fullTrainingSectors.add(MatchRoleID.Sector.Forward);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Goal);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Back);
		osmosisTrainingSectors.add(MatchRoleID.Sector.CentralDefence);
		osmosisTrainingSectors.add(MatchRoleID.Sector.InnerMidfield);

		_PrimaryTrainingSkillPositions = new int[]{ IMatchRoleID.rightWinger, IMatchRoleID.leftWinger,
				IMatchRoleID.leftForward, IMatchRoleID.centralForward, IMatchRoleID.rightForward };
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[] { IMatchRoleID.keeper, IMatchRoleID.leftBack,
				IMatchRoleID.rightBack, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield};
		_PrimaryTrainingBaseLength = CrossingWeeklyTraining.instance().getBaseTrainingLength(); // old was (float) 2.2 / (float) 0.6
		_PrimaryTrainingSkillBaseLength = (_PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_WINGER) / (float) 0.6 ;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new WingAttacksWeeklyTraining();
        }
        return m_ciInstance;
    }
}
