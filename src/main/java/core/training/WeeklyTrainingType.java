package core.training;

import java.util.ArrayList;
import java.util.List;

import core.constants.TrainingType;
import core.model.UserParameter;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.type.*;
import core.util.HOLogger;

public abstract class WeeklyTrainingType {
	protected String _Name = "";
	protected int _TrainingType = 0;
	protected int _PrimaryTrainingSkill = -1;
	protected int _SecondaryTrainingSkill = -1;

	protected List<MatchRoleID.Sector> bonusTrainingSectors = new ArrayList<>();
	protected List<MatchRoleID.Sector> fullTrainingSectors = new ArrayList<>();
	protected List<MatchRoleID.Sector> partlyTrainingSectors = new ArrayList<>();
	protected List<MatchRoleID.Sector> osmosisTrainingSectors = new ArrayList<>();

	protected int[] _PrimaryTrainingSkillPositions = new int[0];
	protected int[] _PrimaryTrainingSkillBonusPositions = new int[0];
	protected int[] _PrimaryTrainingSkillPartlyTrainingPositions = new int[0];
	protected int[] _PrimaryTrainingSkillOsmosisTrainingPositions = new int[0];
	protected float _PrimaryTrainingSkillBonus = 0;
	protected float _PrimaryTrainingSkillBaseLength = 0;
	protected float _PrimaryTrainingSkillPartlyLengthRate = 0;
	protected float _PrimaryTrainingSkillOsmosisLengthRate = (float) 100 / (OSMOSIS_BASE_PERCENTAGE + UserParameter.instance().TRAINING_OFFSET_OSMOSIS); // 16%
	/* secondary trainings position never differ from primary trainings positions
	protected int[] _SecondaryTrainingSkillPositions = new int[0];
	protected int[] _SecondaryTrainingSkillBonusPositions = new int[0];
	protected int[] _SecondaryTrainingSkillSecondaryTrainingPositions = new int[0];
	protected int[] _SecondaryTrainingSkillOsmosisTrainingPositions = new int[0];
	*/
	protected float _SecondaryTrainingSkillBonus = 0;
	protected float _SecondaryTrainingSkillBaseLength = 0;
	//protected float _SecondaryTrainingSkillPartlyLengthRate = 0;
	//protected float _SecondaryTrainingSkillOsmosisLengthRate = 0;
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
			3, 45,
			4.55,
			// Skill 19
			7.00,
			//  Random from here and down
			10,
			15,
			21
	};

	protected double factorTrainingTypeKoeff;
	protected double osmosisKoeff;

	public static WeeklyTrainingType instance(int iTrainingType) {
		return switch (iTrainingType) {
			case TrainingType.CROSSING_WINGER -> CrossingWeeklyTraining.instance();
			case TrainingType.DEF_POSITIONS -> DefensivePositionsWeeklyTraining.instance();
			case TrainingType.DEFENDING -> DefendingWeeklyTraining.instance();
			case TrainingType.GOALKEEPING -> GoalkeepingWeeklyTraining.instance();
			case TrainingType.PLAYMAKING -> PlaymakingWeeklyTraining.instance();
			case TrainingType.SCORING -> ScoringWeeklyTraining.instance();
			case TrainingType.SET_PIECES -> SetPiecesWeeklyTraining.instance();
			case TrainingType.SHOOTING -> ShootingWeeklyTraining.instance();
			case TrainingType.SHORT_PASSES -> ShortPassesWeeklyTraining.instance();
			case TrainingType.THROUGH_PASSES -> ThroughPassesWeeklyTraining.instance();
			case TrainingType.WING_ATTACKS -> WingAttacksWeeklyTraining.instance();
			default -> null;
		};
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

	public float getPrimaryTrainingSkillPartlyBaseLengthRate() {
		return _PrimaryTrainingSkillPartlyLengthRate;
	}

	public float getPrimaryTrainingSkillOsmosisBaseLengthRate() {
		return _PrimaryTrainingSkillOsmosisLengthRate;
	}

	public float getSecondaryTrainingSkillBaseLength() {
		return _SecondaryTrainingSkillBaseLength;
	}

	public int[] getTrainingSkillPositions() {
		return _PrimaryTrainingSkillPositions;
	}

	public int[] getTrainingSkillBonusPositions() {
		return _PrimaryTrainingSkillBonusPositions;
	}

	public int[] getTrainingSkillPartlyTrainingPositions() {
		return _PrimaryTrainingSkillPartlyTrainingPositions;
	}

	public int[] getTrainingSkillOsmosisTrainingPositions() {
		return _PrimaryTrainingSkillOsmosisTrainingPositions;
	}

	public double getPrimaryTraining(TrainingWeekPlayer tp) {
		double dPrimaryTraining = 0;
		int iMinutes = 0;
		int tmp = tp.getFullTrainingMinutes();
		if (tmp > 0) {
			if (tmp > 90) {
				tmp = 90;
			}
			iMinutes = tmp;
			dPrimaryTraining = (double) tmp / (double) 90;
		}
		if (iMinutes > 0 && _PrimaryTrainingSkillBonus > 0) {
			tmp = tp.getBonusTrainingMinutes();
			if (tmp > 0) {
				dPrimaryTraining += ((double) tmp / (double) 90) * _PrimaryTrainingSkillBonus;
			}
		}
		if (iMinutes < 90) {
			if (_PrimaryTrainingSkillPartlyLengthRate > 0) {
				tmp = tp.getPartlyTrainingMinutes();
				if (tmp > 0) {
					if (iMinutes + tmp > 90) {
						tmp = 90 - iMinutes;
					}
					iMinutes += tmp;
					dPrimaryTraining += ((double) tmp / (double) 90) / _PrimaryTrainingSkillPartlyLengthRate;
				}
			}
			if (iMinutes < 90) {
				if (_PrimaryTrainingSkillOsmosisLengthRate > 0) {
					tmp = tp.getOsmosisTrainingMinutes();
					if (tmp > 0) {
						if (iMinutes + tmp > 90) {
							tmp = 90 - iMinutes;
						}
						//iMinutes += tmp;
						dPrimaryTraining += ((double) tmp / (double) 90) / _PrimaryTrainingSkillOsmosisLengthRate;
					}
				}
			}
		}
		return dPrimaryTraining;
	}

	public double getSecondaryTraining(TrainingWeekPlayer tp) {
		double dSecondaryTraining = 0;
		if (_SecondaryTrainingSkill > 0) {
			int iMinutes = 0;
			int tmp = tp.getFullTrainingMinutes();
			if (tmp > 0) {
				if (tmp >= 90) {
					tmp = 90;
				}
				iMinutes = tmp;
				dSecondaryTraining = (double) tmp / (double) 90;
			}
			if (iMinutes > 0 && _SecondaryTrainingSkillBonus > 0) {
				tmp = tp.getBonusTrainingMinutes();
				if (tmp > 0) {
					dSecondaryTraining += ((double) tmp / (double) 90) * _SecondaryTrainingSkillBonus;
				}
			}
		}
		return dSecondaryTraining;
	}

	public static double calcTraining(double baseLength, int age, int trainerLevel, int intensity,
									  int stamina, int curSkill, int assistantLevel) {
		double ageFactor = Math.pow(1.0404, age - 17) * (UserParameter.instance().TRAINING_OFFSET_AGE + BASE_AGE_FACTOR);
		double skillFactor = -1.4595 * Math.pow((curSkill + 1d) / 20, 2) + 3.7535 * (curSkill + 1d) / 20 - 0.1349d;
		if (skillFactor < 0) {
			skillFactor = 0;
		}

		double trainerFactor = (1 + (7 - Math.min(trainerLevel, 7.5)) * 0.091) * (UserParameter.instance().TrainerFaktor + BASE_COACH_FACTOR);
		double coFactor = getAssistantFactor(assistantLevel);
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

	public static double getAssistantFactor(int assistantLevels) {
		//factor = 1/((1/1.2863) * (1.09 + (0.02 * assistantLevels)));
		double factor = 1 / ((TRAININGSPEEDBASE + assistantLevels * ASSISTANTTRAININGSPEEDFACTOR) / OLDASSISTANTTRAININGSPEEDMAX);

		factor *= (UserParameter.instance().TRAINING_OFFSET_ASSISTANTS + BASE_ASSISTANT_COACH_FACTOR);
		//}

		return factor;
	}

	static double[] coachKoeff = {0.7343, 0.8324, 0.92, 1, 1.0375};
	static double[] assistantKoeff = {1, 1.035, 1.07, 1.105, 1.14, 1.175, 1.21, 1.245, 1.28, 1.315, 1.35};

	/**
	 * Calculate skill increase of training week (Schum's formula)
	 *
	 * Source:
	 * https://github.com/akasolace/HO/issues/250#issuecomment-541170338
	 * https://www87.hattrick.org/Forum/Read.aspx?t=17024376&v=4&a=1&n=5
	 * https://www87.hattrick.org/Club/Manager/?userId=5176908 (Schum)
	 *
	 * @param skillLevel int skill level [0..20]
	 * @param trForPlayer training information
	 * @return skill increase [0..1]
	 */
	public double calculateSkillIncreaseOfTrainingWeek(int skillLevel, TrainingPerPlayer trForPlayer) {

		if (trForPlayer.getTrainingPair() == null
				|| trForPlayer.getTrainingPair().getTrainingDuration() == null
				|| !trForPlayer.getTrainingPair().getTrainingDuration().hasTrainingMinutes()) {
			return 0;
		}

		return calculateSkillIncreaseOfTrainingWeek(
				skillLevel,
				trForPlayer.getTrainingWeek().getCoachLevel(),
				trForPlayer.getTrainingWeek().getTrainingAssistantsLevel(),
				trForPlayer.getTrainingWeek().getTrainingIntensity(),
				trForPlayer.getTrainingWeek().getStaminaShare(),
				trForPlayer.getPlayerAgeAtTrainingDate(),
				trForPlayer.getTrainingPair().getTrainingDuration().getFullTrainingMinutes(),
				trForPlayer.getTrainingPair().getTrainingDuration().getPartlyTrainingMinutes(),
				trForPlayer.getTrainingPair().getTrainingDuration().getOsmosisTrainingMinutes(),
				trForPlayer.getTrainingPair().getTrainingDuration().getBonusTrainingMinutes()
		);
	}

	/**
	 * Calculate skill increase of training week (Schum's formula)
	 * based on training specific factorTrainingTypeKoeff and osmosisKoeff
	 *
	 * @param skillLevel skill level [0..20]
	 * @param coachLevel Coach Level [4..8]
	 * @param assistantLevel Assistant level sum [0..10]
	 * @param trainingIntensity training intensity [0..100]
	 * @param staminaShare stamina share [10..100]
	 * @param age age years [17..]
	 * @param fullTrainingMinutes minutes with full training
	 * @param partlyTrainingMinutes partly training
	 * @param osmosisTrainingMinutes osmosis training
	 * @return skill increase [0..1]
	 */
	public double calculateSkillIncreaseOfTrainingWeek(int skillLevel,
													   int coachLevel,
													   int assistantLevel,
													   int trainingIntensity,
													   int staminaShare,
													   int age,
													   int fullTrainingMinutes,
													   int partlyTrainingMinutes,
													   int osmosisTrainingMinutes,
													   int bonusTrainingMinutes) {
		/*
		at lvl<9
		f(lvl) = 16.289 * EXP (-0.1396 * lvl)

		at lvl>=9
		f(lvl) = 54.676 / lvl - 1.438
		*/

		double factorSkillLevel;
		if (skillLevel < 9) {
			factorSkillLevel = 16.289 * Math.exp(-.1396 * skillLevel);
		} else {
			factorSkillLevel = 54.676 / skillLevel - 1.438;
		}

		/*
		further coefficients:

		Coach Koeff
		8 1.040
		7 1.000
		6 0.920
		5 0.834
		4 0.734
		*/

		if (coachLevel < 4 || coachLevel > 8) {
			return trainingCalcError("Trainerlevel out of range [4,8]: " + coachLevel);    // dummy return
		}
		var factorCoach = coachKoeff[coachLevel - 4];

		/*
		K(assist)

		Assists Koeff
		10 1.350
		9 1.315
		8 1.280
		7 1.245
		6 1.210
		5 1.175
		4 1.140
		3 1.105
		2 1.070
		1 1.035
		0 1.000
		*/
		if (assistantLevel < 0 || assistantLevel > 10) {
			return trainingCalcError("AssistantLevel out of range [0,10]: " + assistantLevel);    // dummy return
		}
		var factorAssistants = assistantKoeff[assistantLevel];

		/*
		K(int)
		It's kind of simple here. The intensity of training is the multiplier,
		that is, 100% is 1.0.
		90% is 0.9
		*/
		double factorIntensity = trainingIntensity / 100.0;

		/*
		K(stam)
		Here we deduct the percentage of the stamen from 1, i.e.
		at 5 % of the stadium 1.0 - 5% = 0.95
		at 15% of the stadium 1.0 - 15% = 0.85
		*/
		double factorStamina = 1. - (double) staminaShare / 100.;

		/*
		K(age) = 54 / (Age+37)

		Age - Koeff
		17 - 1.000
		18 - 0.9818
		19 - 0.9643
		20 - 0.9474
		21 - 0.9310
		22 - 0.9153
		23 - 0.9000
		24 - 0.8852
		25 - 0.8710
		26 - 0.8571
		27 - 0.8438
		28 - 0.8308
		29 - 0.8182
		30 - 0.8060
		31 - 0.7941
		32 - 0.7826
		33 - 0.7714
		34 - 0.7606
		35 - 0.7500
		36 - 0.7397
		37 - 0.7297
		*/

		double factorAge = 54.0 / (age + 37);

		/*
		K(time)
		If a player has played the full slot for 90 minutes, then K(time)=1.0
		If the slot is 90 minutes, then K(time)=0.5
		if, for example, 36 minutes on the full slot and 90 minutes on the half-slot,
		we think, remembering that the total amount of training can not be taken into account more than 90 minutes of flirtation:
		K(time)=(1.0*36+(90-36)*0.5)/90=0.7
		*/

		var minutes = Math.min(90,fullTrainingMinutes);
		double factorTime = minutes/90.;
		if (bonusTrainingMinutes > 0 && this.getPrimaryTrainingSkillBonus() > 0) {
			var bonusTraining = Math.min(90, bonusTrainingMinutes);
			factorTime += this.getPrimaryTrainingSkillBonus() * bonusTraining / 90.;
		}
		if (minutes < 90) {
			var partlyMinutes = Math.min(90 - minutes, partlyTrainingMinutes);
			factorTime += .5 * partlyMinutes / 90.;
			minutes += partlyMinutes;
			if (minutes < 90) {
				var osmosisMinutes = Math.min(90 - minutes, osmosisTrainingMinutes);
				factorTime += osmosisKoeff * osmosisMinutes / 90.;
			}
		}

		/*
			K(train)
			Basic training

			Train Koeff
			GK 5.10%
			Df 2.88%
			PM 3.36%
			Wg 4.80%
			Ps 3.60%.
			Sc 3.24%
			SP 14.70%.

			Other training sessions

			Train Koeff
			Shoot - Sc 1.50%
			Shoot - SP 1.50%
			First Pass 3.15%.
			Zone Df 1.38%
			Wing Att 3.12%

			Background training
			The ratio of background workout speed to full workout speed is different for different types of workouts:
			For Def, Sc, Pass, First Pass and Zone Def, it's 1/6,
			for Wing, PM is 1/8, for Wing Att is 5/39.

			Correspondingly, the background workout speed coefficients will be as follows:
			Df_phone - 0.48%
			PM_phone - 0.42%
			Wg_phone-- 0.60%.
			Ps_phone-- 0.60%.
			Sc_phone-- 0.54%.
			First Ps_phone - 0.525%
			Zone Df_phone - 0.23%
			Wing Att_phone - 0.40%
		*/

		return Math.min(1, factorTrainingTypeKoeff * factorTime * factorAge * factorAssistants * factorCoach * factorStamina * factorIntensity * factorSkillLevel * .01);
	}

	protected double trainingCalcError(String s) {
		HOLogger.instance().error(this.getClass(), s);
		return 1;
	}

	/**
	 * Training effect of youth bonus training per minute
	 *
	 * @param skillId      Skill id
	 * @param currentValue current skill value
	 * @param ageYears     current age of the player
	 * @return training skill increment of bonus training
	 */
	public double getBonusYouthTrainingPerMinute(int skillId, int currentValue, int ageYears) {
		return getFullYouthTrainingPerMinute(skillId, currentValue, ageYears) * (1 + this.getPrimaryTrainingSkillBonus());
	}

	/**
	 * Training effect of youth full training per minute
	 *
	 * @param skillId      Skill id
	 * @param currentValue current skill value
	 * @param ageYears     current age of the player
	 * @return training skill increment of full training
	 */
	public double getFullYouthTrainingPerMinute(int skillId, int currentValue, int ageYears) {
		double nweeks = (0.5 + 0.1 * currentValue) * (1 + (ageYears - 15) * 0.14);    // approximation
		if (skillId == _PrimaryTrainingSkill) {
			nweeks *= this.getPrimaryTrainingSkillBaseLength();
		} else if (skillId == _SecondaryTrainingSkill) {
			nweeks *= this.getSecondaryTrainingSkillBaseLength();
		} else {
			return 0; // skill is not trained
		}
		if (nweeks > 0) return 1. / nweeks / 90.;
		return 0; // should not happen
	}

	/**
	 * Training effect of youth partly training per minute
	 *
	 * @param skillId      Skill id
	 * @param currentValue current skill value
	 * @param ageYears     current age of the player
	 * @return training skill increment of partly training
	 */
	public double getPartlyYouthTrainingPerMinute(int skillId, int currentValue, int ageYears) {
		return getFullYouthTrainingPerMinute(skillId, currentValue, ageYears) / this.getPrimaryTrainingSkillPartlyBaseLengthRate();
	}

	/**
	 * Training effect of youth osmosis training per minute
	 *
	 * @param skillId      Skill id
	 * @param currentValue current skill value
	 * @param ageYears     current age of the player
	 * @return training skill increment of osmosis training
	 */
	public double getOsmosisYouthTrainingPerMinute(int skillId, int currentValue, int ageYears) {
		return getFullYouthTrainingPerMinute(skillId, currentValue, ageYears) / this.getPrimaryTrainingSkillOsmosisBaseLengthRate();
	}

	public List<MatchRoleID.Sector> getBonusTrainingSectors() {
		return this.bonusTrainingSectors;
	}

	public List<MatchRoleID.Sector> getFullTrainingSectors() {
		return this.fullTrainingSectors;
	}

	public List<MatchRoleID.Sector> getPartlyTrainingSectors() {
		return this.partlyTrainingSectors;
	}

	public List<MatchRoleID.Sector> getOsmosisTrainingSectors() {
		return this.osmosisTrainingSectors;
	}
}