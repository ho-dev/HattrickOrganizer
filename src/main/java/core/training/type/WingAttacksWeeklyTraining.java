package core.training.type;

import java.util.List;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.training.WeeklyTrainingType;

public class WingAttacksWeeklyTraining extends WeeklyTrainingType {
	protected static WingAttacksWeeklyTraining m_ciInstance = null;
	private WingAttacksWeeklyTraining()
	{
		_Name = "Wing Attacks";
		_TrainingType = TrainingType.WING_ATTACKS;
		_PrimaryTrainingSkill = PlayerSkill.WINGER;
		_PrimaryTrainingSkillPositions = new int[]{ ISpielerPosition.rightWinger, ISpielerPosition.leftWinger, 
				ISpielerPosition.leftForward, ISpielerPosition.centralForward, ISpielerPosition.rightForward };
		_PrimaryTrainingSkillOsmosisTrainingPositions = new int[] { ISpielerPosition.keeper, ISpielerPosition.leftBack, 
				ISpielerPosition.rightBack, ISpielerPosition.leftCentralDefender,
				ISpielerPosition.middleCentralDefender, ISpielerPosition.rightCentralDefender,
				ISpielerPosition.leftInnerMidfield, ISpielerPosition.centralInnerMidfield, 
				ISpielerPosition.rightInnerMidfield}; 
		_PrimaryTrainingBaseLength = CrossingWeeklyTraining.instance().getBaseTrainingLength(); // old was (float) 2.2 / (float) 0.6
		_PrimaryTrainingSkillBaseLength = (_PrimaryTrainingBaseLength + UserParameter.instance().TRAINING_OFFSET_WINGER) / (float) 0.6 ;
	}
	public static WeeklyTrainingType instance() {
        if (m_ciInstance == null) {
        	m_ciInstance = new WingAttacksWeeklyTraining();
        }
        return m_ciInstance;
    }
	@Override
	public double getTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff) {
		return calcTraining(getPrimaryTrainingSkillBaseLength(), player.getAlter(), assistants, trainerLevel, 
				intensity, stamina, player.getFluegelspiel(), staff);
	}
	@Override
	public double getSecondaryTrainingLength(Spieler player, int assistants, int trainerLevel, int intensity, int stamina, List<StaffMember> staff)
	{
		return (double) -1;
	}
}
