package core.training.type;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.training.WeeklyTrainingType;

public final class DefendingWeeklyTraining extends WeeklyTrainingType {
	static DefendingWeeklyTraining m_ciInstance = null;
	private DefendingWeeklyTraining()
	{
		_Name = "Defending";
		_TrainingType = TrainingType.DEFENDING;
		_PrimaryTrainingSkill = PlayerSkill.DEFENDING;

		factorTrainingTypeKoeff = 2.88;
		osmosisKoeff = 1./6.;

		fullTrainingSectors.add(MatchRoleID.Sector.CentralDefence);
		fullTrainingSectors.add(MatchRoleID.Sector.Back);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Goal);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Forward);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Wing);
		osmosisTrainingSectors.add(MatchRoleID.Sector.InnerMidfield);

		_PrimaryTrainingSkillPositions = new int[]{ 
				IMatchRoleID.leftBack, IMatchRoleID.rightBack, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender};
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[]{
				IMatchRoleID.keeper, IMatchRoleID.rightWinger, IMatchRoleID.leftWinger,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftForward,
				IMatchRoleID.centralForward, IMatchRoleID.rightForward};
		_PrimaryTrainingBaseLength = (float) 5.4824; // old was 3.6
//		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_DEFENDING; // 100%
		//_PrimaryTrainingSkillSecondaryLengthRate = (float) 2; // 50% there are no secondary training positions
	}
	public static DefendingWeeklyTraining instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new DefendingWeeklyTraining();
        }
        return m_ciInstance;
    }
}
