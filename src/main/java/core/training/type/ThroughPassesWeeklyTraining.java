package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.training.WeeklyTrainingType;

public class ThroughPassesWeeklyTraining extends WeeklyTrainingType {
	protected static ThroughPassesWeeklyTraining m_ciInstance = null;
	private ThroughPassesWeeklyTraining()
	{
		_Name = "Through Passes";
		_TrainingType = TrainingType.THROUGH_PASSES;
		_PrimaryTrainingSkill = PlayerSkill.PASSING;
		_PrimaryTrainingSkillPositions = new int[]{ ISpielerPosition.leftBack, 
				ISpielerPosition.rightBack, ISpielerPosition.leftCentralDefender,
				ISpielerPosition.middleCentralDefender, ISpielerPosition.rightCentralDefender,
				ISpielerPosition.rightWinger, ISpielerPosition.leftWinger, 
				ISpielerPosition.leftInnerMidfield, ISpielerPosition.centralInnerMidfield, 
				ISpielerPosition.rightInnerMidfield };
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[] { ISpielerPosition.keeper,
				ISpielerPosition.leftForward, ISpielerPosition.centralForward, ISpielerPosition.rightForward}; 
		_PrimaryTrainingBaseLength = (float) ShortPassesWeeklyTraining.instance().getBaseTrainingLength(); // old was (float) 2.8 / (float) 0.85
		_PrimaryTrainingSkillBaseLength = (_PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_PASSING) / (float) 0.85;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new ThroughPassesWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff) {
		 return calcTraining(ThroughPassesWeeklyTraining.instance().getPrimaryTrainingSkillBaseLength(), 
	                		player.getAlter(), assistants, trainerLevel, intensity, stamina, player.getPasspiel(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}