package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.training.WeeklyTrainingType;

public final class GoalkeepingWeeklyTraining extends WeeklyTrainingType {
	protected static GoalkeepingWeeklyTraining m_ciInstance = null;
	private GoalkeepingWeeklyTraining()
	{
		_Name = "Goalkeeping";
		_TrainingType = TrainingType.GOALKEEPING;
		_PrimaryTrainingSkill = PlayerSkill.KEEPER;
		_PrimaryTrainingSkillPositions = new int[]{ ISpielerPosition.keeper };
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
	@Override
	public double getTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff) {
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
				intensity, stamina, player.getTorwart(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}
