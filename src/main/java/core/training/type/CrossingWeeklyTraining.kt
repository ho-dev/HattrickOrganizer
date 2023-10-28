package core.training.type;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.training.WeeklyTrainingType;

public class CrossingWeeklyTraining extends WeeklyTrainingType {
	protected static CrossingWeeklyTraining m_ciInstance = null;
	private CrossingWeeklyTraining()
	{
		_Name = "Crossing";

		_TrainingType = TrainingType.CROSSING_WINGER;
		_PrimaryTrainingSkill = PlayerSkill.WINGER;

		factorTrainingTypeKoeff = 4.8;
		osmosisKoeff = 1./8.;

		fullTrainingSectors.add(MatchRoleID.Sector.Wing);
		partlyTrainingSectors.add(MatchRoleID.Sector.Back);
		osmosisTrainingSectors.add(MatchRoleID.Sector.CentralDefence);
		osmosisTrainingSectors.add(MatchRoleID.Sector.InnerMidfield);
		osmosisTrainingSectors.add(MatchRoleID.Sector.Forward);

		_PrimaryTrainingSkillPositions = new int[]{ 
				IMatchRoleID.leftWinger, IMatchRoleID.rightWinger };
		_PrimaryTrainingSkillPartlyTrainingPositions = new int[]{
				IMatchRoleID.leftBack, IMatchRoleID.rightBack};
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[]{
				IMatchRoleID.keeper, IMatchRoleID.leftCentralDefender,
				IMatchRoleID.middleCentralDefender, IMatchRoleID.rightCentralDefender,
				IMatchRoleID.leftInnerMidfield, IMatchRoleID.centralInnerMidfield,
				IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftForward,
				IMatchRoleID.centralForward, IMatchRoleID.rightForward};
		_PrimaryTrainingBaseLength = (float)  3.2341; // old was 2.2
//		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_WINGER; // 100%
		_PrimaryTrainingSkillPartlyLengthRate = (float) 2; // 50%
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new CrossingWeeklyTraining();
        }
		return m_ciInstance;
    }

}
