package core.training;

import java.util.List;

import core.constants.TrainingType;
import core.model.StaffMember;
import core.model.StaffType;
import core.model.UserParameter;
import core.model.player.Player;
import core.training.type.*;

public abstract class WeeklyTrainingType {
	protected String _Name = ""; 
	protected int _TrainingType = 0;
	protected int _PrimaryTrainingSkill = -1;
	protected int _SecondaryTrainingSkill = -1;
	protected int[] _PrimaryTrainingSkillPositions = new int[0];
	protected int[] _PrimaryTrainingSkillBonusPositions = new int[0];
	protected int[] _PrimaryTrainingSkillSecondaryTrainingPositions = new int[0];
	protected int[] _PrimaryTrainingSkillOsmosisTrainingPositions = new int[0];
	protected float _PrimaryTrainingSkillBonus = 0;
	protected float _PrimaryTrainingSkillBaseLength = 0;
	protected float _PrimaryTrainingSkillSecondaryLengthRate = 0;
	protected float _PrimaryTrainingSkillOsmosisLengthRate = (float) 100 / (OSMOSIS_BASE_PERCENTAGE + UserParameter.instance().TRAINING_OFFSET_OSMOSIS); // 16%
	/* secondary trainings position never differ from primary trainings positions
	protected int[] _SecondaryTrainingSkillPositions = new int[0];
	protected int[] _SecondaryTrainingSkillBonusPositions = new int[0];
	protected int[] _SecondaryTrainingSkillSecondaryTrainingPositions = new int[0];
	protected int[] _SecondaryTrainingSkillOsmosisTrainingPositions = new int[0];
	*/
	protected float _SecondaryTrainingSkillBonus = 0;
	protected float _SecondaryTrainingSkillBaseLength = 0;
	protected float _SecondaryTrainingSkillSecondaryLengthRate = 0;
	protected float _SecondaryTrainingSkillOsmosisLengthRate = 0;
	protected float _PrimaryTrainingBaseLength = 0;
	public static final float OSMOSIS_BASE_PERCENTAGE = (float) 16;
	public static final float BASE_AGE_FACTOR = (float) 1.0; // old was 0.9
	public static final float BASE_COACH_FACTOR = (float) 1.0;
	public static final float BASE_ASSISTANT_COACH_FACTOR = (float) 1.0;
	public static final float BASE_INTENSITY_FACTOR = (float) 1.0;
	
	// training speed constants in percentage
	private static final double ASSISTANTTRAININGSPEEDFACTOR = 3.5;
	private static final double TRAININGSPEEDBASE = 109;
	private static final double OLDASSISTANTTRAININGSPEEDMAX = 143;
	
	private static double assistantLevelEffect = 0.032;
	private static double[] skillFactorArray = {
		// Skill 1
		0.36,
		0.52,
		0.66,
		0.77,
		0.91,
		// Passable...
		1,
		1.11,
		1.20,
		1.43,
		// Outstanding
		1.72,
		1.98,
		2.18,
		2.43,
		2.64,
		// Titanic
		2.86,
		3.18,
		3,45,
		4.55,
		// Skill 19
		7.00,
		//  Random from here and down
		10,
		15,
		21};
	
	public static WeeklyTrainingType instance(int iTrainingType)
	{
		WeeklyTrainingType wt = null;
		switch (iTrainingType) {
	        case TrainingType.CROSSING_WINGER:
				wt = CrossingWeeklyTraining.instance();
				break;
			case TrainingType.DEF_POSITIONS:
				wt = DefensivePositionsWeeklyTraining.instance();
				break;
			case TrainingType.DEFENDING:
				wt = DefendingWeeklyTraining.instance();
				break;
			case TrainingType.GOALKEEPING:
				wt = GoalkeepingWeeklyTraining.instance();
				break;
			case TrainingType.PLAYMAKING:
				wt = PlaymakingWeeklyTraining.instance();
				break;
			case TrainingType.SCORING:
				wt = ScoringWeeklyTraining.instance();
				break;
			case TrainingType.SET_PIECES:
				wt = SetPiecesWeeklyTraining.instance();
				break;
			case TrainingType.SHOOTING:
				wt = ShootingWeeklyTraining.instance();
				break;
			case TrainingType.SHORT_PASSES:
				wt = ShortPassesWeeklyTraining.instance();
				break;
			case TrainingType.THROUGH_PASSES:
				wt = ThroughPassesWeeklyTraining.instance();
				break;
			case TrainingType.WING_ATTACKS:
				wt = WingAttacksWeeklyTraining.instance();
				break; 
        }
		return wt;
	}
	public String getName() {
		return _Name;
	}

	public int getTrainingType() {
		return _TrainingType;
	}
	
	public int getPrimaryTrainingSkill() {
		return _PrimaryTrainingSkill;
	}

	public int getSecondaryTrainingSkill() {
		return _SecondaryTrainingSkill;
	}

	public float getBaseTrainingLength() {
		return _PrimaryTrainingBaseLength;
	}
	public float getPrimaryTrainingSkillBaseLength() {
		return _PrimaryTrainingSkillBaseLength;
	}

	public float getPrimaryTrainingSkillBonus() {
		return _PrimaryTrainingSkillBonus;
	}

	public float getPrimaryTrainingSkillSecondaryBaseLengthRate() {
		return _PrimaryTrainingSkillSecondaryLengthRate;
	}
	
	public float getPrimaryTrainingSkillOsmosisBaseLengthRate() {
		return _PrimaryTrainingSkillOsmosisLengthRate;
	}

	public float getSecondaryTrainingSkillBaseLength() {
		return _SecondaryTrainingSkillBaseLength;
	}

	public float getSecondaryTrainingSkillSecondaryLengthRate() {
		return _SecondaryTrainingSkillSecondaryLengthRate;
	}
	
	public float getSecondaryTrainingSkillOsmosisLengthRate() {
		return _SecondaryTrainingSkillOsmosisLengthRate;
	}
	
	public int[] getTrainingSkillPositions() {
	 return _PrimaryTrainingSkillPositions;
	}
	public int[] getTrainingSkillBonusPositions() {
		return _PrimaryTrainingSkillBonusPositions;
	}
	public int[] getTrainingSkillSecondaryTrainingPositions() {
		return _PrimaryTrainingSkillSecondaryTrainingPositions;
	}
	public int[] getTrainingSkillOsmosisTrainingPositions() {
		return _PrimaryTrainingSkillOsmosisTrainingPositions;
	}
/*	public int[] getSecondaryTrainingSkillPositions() {
		return _SecondaryTrainingSkillPositions;
	}
	public int[] getSecondaryTrainingSkillBonusPositions() {
		return _SecondaryTrainingSkillBonusPositions;
	}
	public int[] getSecondaryTrainingSkillSecondaryTrainingPositions() {
		return _SecondaryTrainingSkillSecondaryTrainingPositions;
	}
	public int[] getSecondaryTrainingSkillOsmosisTrainingPositions() {
		return _SecondaryTrainingSkillOsmosisTrainingPositions;
	}
*/
	public double getPrimaryTraining(TrainingWeekPlayer tp)
	{
		double dPrimaryTraining = 0;
		int iMinutes = 0;
		int tmp = tp.getPrimarySkillPositionMinutes();
		if (tmp > 0)
		{
			if (tmp > 90) {
				tmp = 90;
			}
			iMinutes = tmp;
			dPrimaryTraining = (double) tmp / (double) 90;
		}
		if (iMinutes > 0 && _PrimaryTrainingSkillBonus > 0) {
			tmp = tp.getPrimarySkillBonusPositionMinutes();
			if (tmp > 0) {
				dPrimaryTraining += ((double) tmp / (double) 90) * _PrimaryTrainingSkillBonus; 
			}
		}
		if (iMinutes < 90) {
			if (_PrimaryTrainingSkillSecondaryLengthRate > 0) {
				 tmp = tp.getPrimarySkillSecondaryPositionMinutes();
				 if (tmp > 0) {
					 if (iMinutes + tmp > 90) {
						 tmp = 90 - iMinutes;
					 }
					 iMinutes += tmp;
					 dPrimaryTraining += ((double) tmp / (double) 90) / _PrimaryTrainingSkillSecondaryLengthRate;
				 }
			}
			if (iMinutes < 90) {
				if (_PrimaryTrainingSkillOsmosisLengthRate > 0) {
					tmp = tp.getPrimarySkillOsmosisPositionMinutes();
					if (tmp > 0) {
						if (iMinutes + tmp > 90) {
							tmp = 90 - iMinutes;
						}
						iMinutes += tmp;
						dPrimaryTraining += ((double) tmp / (double) 90) / _PrimaryTrainingSkillOsmosisLengthRate;
					}
				}
			}	 
		}
		return dPrimaryTraining;
	}
	public double getSecondaryTraining(TrainingWeekPlayer tp)
	{
		double dSecondaryTraining = 0;
		if (_SecondaryTrainingSkill > 0) {
			int iMinutes = 0;
			int tmp = tp.getSecondarySkillPrimaryMinutes();
			if (tmp > 0)
			{
				if (tmp >= 90) {
					tmp = 90;
				}
				iMinutes = tmp;
				dSecondaryTraining = (double) tmp / (double) 90;
			}
			if (iMinutes > 0 && _SecondaryTrainingSkillBonus > 0) {
				tmp = tp.getSecondarySkillBonusMinutes();
				if (tmp > 0) {
					dSecondaryTraining += ((double) tmp / (double) 90) * _SecondaryTrainingSkillBonus; 
				}
			}
			if (iMinutes < 90) {
				if (_SecondaryTrainingSkillSecondaryLengthRate > 0)
				{
					tmp = tp.getSecondarySkillSecondaryPositionMinutes();
					if (tmp > 0) {
						if (iMinutes + tmp > 90) {
							tmp = 90 - iMinutes;
						}
						iMinutes += tmp;
						dSecondaryTraining += ((double) tmp / (double) 90) / _SecondaryTrainingSkillSecondaryLengthRate;
					}
				}
				if (iMinutes < 90) {
					if (_SecondaryTrainingSkillOsmosisLengthRate > 0) {
						tmp = tp.getSecondarySkillOsmosisPositionMinutes();
						if (tmp > 0) {
							if (iMinutes + tmp > 90) {
								tmp = 90 - iMinutes;
							}
							iMinutes += tmp;
							dSecondaryTraining += ((double) tmp / (double) 90) / _SecondaryTrainingSkillOsmosisLengthRate;
						}
					}
				}	 
			}
		}
		return dSecondaryTraining;
	}
	 public static double calcTraining(double baseLength, int age, int trainerLevel, int intensity,
			 							int stamina, int curSkill, List<StaffMember> staff)
	 {
		double ageFactor = Math.pow(1.0404, age - 17) * (UserParameter.instance().TRAINING_OFFSET_AGE + BASE_AGE_FACTOR);
		double skillFactor = - 1.4595 * Math.pow((curSkill+1d)/20, 2) + 3.7535 * (curSkill + 1d) / 20 - 0.1349d;
		if (skillFactor < 0) {
			skillFactor = 0;
		}
		
		// skill between 1 and 22
/*		curSkill = Math.max(1, curSkill);
		curSkill = Math.min(22, curSkill);
		double skillFactor = skillFactorArray[curSkill - 1];
*/	
		// age between 17 and 30
//		age = Math.max(17, age);
//		age = Math.min(30, age);
////		double ageFactor = ageFactorArray[age - 17];
		
		
		double trainerFactor = (1 + (7 - Math.min(trainerLevel, 7.5)) * 0.091) * (UserParameter.instance().TrainerFaktor + BASE_COACH_FACTOR);
		double coFactor = getAssistantFactor(staff);
		double tiFactor = Double.MAX_VALUE;
		if (intensity > 0) {
			tiFactor = (1 / (intensity / 100d)) * (UserParameter.instance().TRAINING_OFFSET_INTENSITY + BASE_INTENSITY_FACTOR);
		}
		double staminaFactor = Double.MAX_VALUE;
		if (stamina < 100) {
			staminaFactor = 1 / (1 - stamina / 100d);
		}
		
		// faster training from 2010 up to 9 formidable, then gets linearly smaller
		double fasterTraining = 0.83746 * Math.log(age) - 1.87426;
		if (curSkill > 8) {
			fasterTraining -= (curSkill - 8) * 0.0933;
		}
		
		// invert so that you can multiply for trainLength
		double fasterTrainingFactor = 1. / (1. + fasterTraining);
		
		double trainLength = baseLength * ageFactor * skillFactor * trainerFactor * coFactor * tiFactor * staminaFactor * fasterTrainingFactor;
		if (trainLength < 1) {
			trainLength = 1;
		}
		return trainLength;
	 }
	 
	 public static double getAssistantFactor(List<StaffMember> staff) {
		 
		 double factor = 1;

		 
		/* if ((staff == null) || (staff.size() == 0)) {
			
			 // More than 2?!
			 factor = (1 + (Math.log(11)/Math.log(10) - Math.log(assistants+1)/Math.log(10)) * 0.2749) * (UserParameter.instance().TRAINING_OFFSET_ASSISTANTS + BASE_ASSISTANT_COACH_FACTOR);
		 } else {
		*/	
			 // Compared to old 0 assistants: 9% increase for all, 3,2% each level of assistants.
			 int assistantLevels = 0;
			 
			 
			 for (StaffMember staffer : staff) {
				 if (staffer.getStaffType() == StaffType.ASSISTANTTRAINER) {
					 
					 assistantLevels += staffer.getLevel();
					 }
			 }
			 
			 //factor = 1/((1/1.2863) * (1.09 + (0.02 * assistantLevels))); 
			 factor = 1 / ((TRAININGSPEEDBASE + assistantLevels * ASSISTANTTRAININGSPEEDFACTOR) / OLDASSISTANTTRAININGSPEEDMAX);
			 
			 factor *= (UserParameter.instance().TRAINING_OFFSET_ASSISTANTS + BASE_ASSISTANT_COACH_FACTOR);
		 //}
		 
		 return factor;
	 }
	 
	 
	 public abstract double getTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff);
	 public abstract double getSecondaryTrainingLength(Player player, int trainerLevel, int intensity, int stamina, List<StaffMember> staff);
}
