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

public final class GoalkeepingWeeklyTraining extends WeeklyTrainingType {
	static GoalkeepingWeeklyTraining m_ciInstance = null;
	private GoalkeepingWeeklyTraining()
	{
		_Name = "Goalkeeping";
		_TrainingType = TrainingType.GOALKEEPING;
		_PrimaryTrainingSkill = PlayerSkill.KEEPER;

		factorTrainingTypeKoeff = 5.1;

		fullTrainingSectors.add(MatchRoleID.Sector.Goal);

		_PrimaryTrainingSkillPositions = new int[]{ IMatchRoleID.keeper };
		_PrimaryTrainingBaseLength = (float) 3.0206; // old was 2
		_PrimaryTrainingSkillBaseLength = _PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_GOALKEEPING; // 100%
		_PrimaryTrainingSkillOsmosisLengthRate = 0;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new GoalkeepingWeeklyTraining();
        }
        return m_ciInstance;
    }
}
