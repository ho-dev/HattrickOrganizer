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

public class ScoringWeeklyTraining extends WeeklyTrainingType {
	protected static ScoringWeeklyTraining m_ciInstance = null;
	private ScoringWeeklyTraining()
	{
		_Name = "Scoring";
		_TrainingType = TrainingType.SCORING;
		_PrimaryTrainingSkill = PlayerSkill.SCORING;

		factorTrainingTypeKoeff = 3.24;
		osmosisKoeff = 1./6.;

		fullTrainingSectors.add(MatchRoleID.Sector.Forward);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Goal);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Back);
		osmosisTrainingSectors.add(MatchRoleID.Sector.CentralDefence);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Wing);
		osmosisTrainingSectors.add(MatchRoleID.Sector.InnerMidfield);

		_PrimaryTrainingSkillPositions = new int[]{ 
				IMatchRoleID.leftForward, IMatchRoleID.rightForward, IMatchRoleID.centralForward };
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[]{
				IMatchRoleID.keeper, IMatchRoleID.leftBack, IMatchRoleID.rightBack,
				IMatchRoleID.leftCentralDefender, IMatchRoleID.middleCentralDefender,
				IMatchRoleID.rightCentralDefender, IMatchRoleID.rightWinger,
				IMatchRoleID.leftWinger, IMatchRoleID.leftInnerMidfield,
				IMatchRoleID.centralInnerMidfield, IMatchRoleID.rightInnerMidfield};
		_PrimaryTrainingBaseLength = (float) 4.8536; // old was 3.2
		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_SCORING; // 100%
		//_PrimaryTrainingSkillSecondaryLengthRate = (float) 2; // 50% there are no secondary training positions
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new ScoringWeeklyTraining();
        }
        return m_ciInstance;
    }
}
