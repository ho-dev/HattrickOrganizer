package core.training;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.StaffMember;
import core.model.UserParameter;
import core.model.player.FuturePlayer;
import core.model.player.ISkillup;
import core.model.player.Spieler;
import core.util.HelperWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that manages the prevision of training effect in the future
 *
 * @author Draghetto
 */
public class FutureTrainingManager {
	/** Actual Training sub */
	public double[] actual = new double[8];
		
	/** Maximum training sub after future training */
	public double[] finalSub = new double[8];

	/** Number of skill ups with maximum training */
	public int[] finalSkillup = new int[8];

	/** Active player */
	private Spieler player;
	private List<TrainingPerWeek> futureTrainings;
	private List<ISkillup> futureSkillups;
	private int weeksPassed = 0;
	private double trainingSpeed;

	private int coTrainer;
	private int trainer;
	private List<StaffMember> staff;

	/**
	* Calculates the effects of the future training for the provided player
	*
	* @param p The active player
	* @param trainings The future training
	*/
	public FutureTrainingManager(Spieler p, List<TrainingPerWeek> trainings, int cotrainer, 
										int trainerLvl, List<StaffMember> staff) {
		this.player = p;
		this.futureSkillups = new ArrayList<ISkillup>();
		this.coTrainer = cotrainer;
		this.trainer = trainerLvl;
		this.futureTrainings = trainings;
		this.staff = staff;
		previewPlayer(UserParameter.instance().futureWeeks);
	}

	public FuturePlayer previewPlayer(int startWeekNumber,int finalWeekNumber) {

		this.futureSkillups = new ArrayList<ISkillup>();
				
		// Sets the actual training levels
		actual[0] = getOffset(PlayerSkill.KEEPER);
		actual[1] = getOffset(PlayerSkill.PLAYMAKING);
		actual[2] = getOffset(PlayerSkill.PASSING);
		actual[3] = getOffset(PlayerSkill.WINGER);
		actual[4] = getOffset(PlayerSkill.DEFENDING);
		actual[5] = getOffset(PlayerSkill.SCORING);
		actual[6] = getOffset(PlayerSkill.SET_PIECES);
		actual[7] = getOffset(PlayerSkill.STAMINA);
		
		// rest the other 4 arrays min and max level are equals to actual at beginning
		for (int i = 0; i < 8; i++) {
			finalSub[i] = actual[i];
			finalSkillup[i] = 0;
		}

		trainingSpeed = 0;
		weeksPassed = 0;
		int position = HelperWrapper.instance().getPosition(player.getIdealPosition());
		// Iterate thru all the future training weeks
		for (int index = startWeekNumber; index <= finalWeekNumber; index++) {
			weeksPassed++;
			TrainingPerWeek tw = this.futureTrainings.get(index-1);
			int trType = tw.getTrainingType();
			TrainingWeekPlayer tp = new TrainingWeekPlayer();
			tp.Name(player.getName());
			WeeklyTrainingType wt = WeeklyTrainingType.instance(trType);
			if (wt != null) {
				boolean bFound = false;
				for (int i = 0; i < wt.getPrimaryTrainingSkillPositions().length; i++)
				{
					if(wt.getPrimaryTrainingSkillPositions()[i] == position) {
						tp.addPrimarySkillPositionMinutes(90);
						trainingSpeed += 1.0;
						bFound = true;
						if (wt.getPrimaryTrainingSkillBonusPositions() != null) {
							for (int j = 0; j < wt.getPrimaryTrainingSkillBonusPositions().length; j++) {
								if (wt.getPrimaryTrainingSkillBonusPositions()[j] == position) {
									trainingSpeed += wt.getPrimaryTrainingSkillBonus();
									tp.addPrimarySkillBonusPositionMinutes(90);
									break;
								}
							}
						}
						break;
					}
				}
				if(!bFound) {
					if (wt.getPrimaryTrainingSkillSecondaryTrainingPositions() != null) {
						for (int i = 0; i < wt.getPrimaryTrainingSkillSecondaryTrainingPositions().length; i++)
						{
							if(wt.getPrimaryTrainingSkillSecondaryTrainingPositions()[i] == position) {
								tp.addPrimarySkillSecondaryPositionMinutes(90);
								trainingSpeed += 1.0 / wt.getPrimaryTrainingSkillSecondaryBaseLengthRate();
								bFound = true;
								break;
							}
						}
					}
				}
				if (!bFound) {
					if (wt.getPrimaryTrainingSkillOsmosisTrainingPositions() != null) {
						for (int i = 0; i < wt.getPrimaryTrainingSkillOsmosisTrainingPositions().length; i++)
						{
							if(wt.getPrimaryTrainingSkillOsmosisTrainingPositions()[i] == position) {
								tp.addPrimarySkillOsmosisPositionMinutes(90);
								trainingSpeed += 1.0 / wt.getPrimaryTrainingSkillOsmosisBaseLengthRate();
								bFound = true;
								break;
							}
						}
					}
				}
				bFound = false;
				if (wt.getSecondaryTrainingSkillPositions() != null) {
					for (int i = 0; i < wt.getSecondaryTrainingSkillPositions().length; i++)
					{
						if(wt.getSecondaryTrainingSkillPositions()[i] == position) {
							tp.addSecondarySkillPrimaryMinutes(90);
							bFound = true;
							if (wt.getSecondaryTrainingSkillBonusPositions() != null) {
								for (int j = 0; j < wt.getSecondaryTrainingSkillBonusPositions().length; j++) {
									if (wt.getSecondaryTrainingSkillBonusPositions()[j] == position) {
										tp.addSecondarySkillBonusMinutes(90);
										break;
									}
								}
							}
							break;
						}
					}
				}
				if(!bFound) {
					if (wt.getSecondaryTrainingSkillSecondaryTrainingPositions() != null) {
						for (int i = 0; i < wt.getSecondaryTrainingSkillSecondaryTrainingPositions().length; i++)
						{
							if(wt.getSecondaryTrainingSkillSecondaryTrainingPositions()[i] == position) {
								tp.addSecondarySkillSecondaryPositionMinutes(90);
								bFound = true;
								break;
							}
						}
					}
				}
				if (!bFound) {
					if (wt.getSecondaryTrainingSkillOsmosisTrainingPositions() != null) {
						for (int i = 0; i < wt.getSecondaryTrainingSkillOsmosisTrainingPositions().length; i++)
						{
							if(wt.getSecondaryTrainingSkillOsmosisTrainingPositions()[i] == position) {
								tp.addSecondarySkillOsmosisTrainingMinutes(90);
								bFound = true;
								break;
							}
						}
					}
				}
				TrainingPoints trp = new TrainingPoints(wt.getPrimaryTraining(tp), wt.getSecondaryTraining(tp));
				//System.out.println(wt.getName() + ", " + wt.getTrainingType() + ", Week: " + weeksPassed + ", " + player.getName() + ", Position: " + position + ", Primary: " + trp.getPrimary() + ", Secondary: " + trp.getSecondary());
		//			HOLogger.instance().log(getClass(),position + " " + point + " " + tw.getTyp());
				// Depending on the type of training, update the proper skill with the provided training points
							
				processTraining(wt, trp, tw);
			}
		}		
		trainingSpeed /= weeksPassed;
		FuturePlayer fp = new FuturePlayer();				
		fp.setAttack(getFinalValue(PlayerSkill.SCORING));		
		fp.setCross(getFinalValue(PlayerSkill.WINGER));
		fp.setDefense(getFinalValue(PlayerSkill.DEFENDING));
		fp.setGoalkeeping(getFinalValue(PlayerSkill.KEEPER));
		fp.setPassing(getFinalValue(PlayerSkill.PASSING));
		fp.setPlaymaking(getFinalValue(PlayerSkill.PLAYMAKING));
		fp.setSetpieces(getFinalValue(PlayerSkill.SET_PIECES));
		fp.setStamina(getFinalValue(PlayerSkill.STAMINA));
		fp.setAge(player.getAlter()+(int)(Math.floor((player.getAgeDays()+7*weeksPassed)/112d)));
		fp.setPlayerId(player.getSpielerID());
		return fp;
	}

	/**
	 * get the final value (including skillups and sub) for a specific skill
	 * 
	 * @param skillIndex	index of the skill
	 * @return				value for this skill
	 */
	private double getFinalValue(int skillIndex) {		
		double value = player.getValue4Skill4(skillIndex);
		int pos = getSkillPosition(skillIndex);
		value = value + finalSkillup[pos];
		value = value + finalSub[pos];		
		return value;
	}

	/**
	* Get the array of the actual training sub
	*
	* @return
	*/
	public double[] getActual() {
		return actual;
	}

	/**
	* Returns a list of all future skillups as predicted
	*
	* @return List of Skillups
	*/
	public List<ISkillup> getFutureSkillups() {
		return futureSkillups;
	}

	/**
	 * Returns training speed multiplier for training prediction sorting
	 */
	public int getTrainingSpeed()
	{
		return (int)(trainingSpeed * 100.0);
	}

	/**
	* Get the array of the maximum training sub
	*
	* @return
	*/
	public double[] getMax() {
		return finalSub;
	}

	/**
	* Get the array of the maximum number of skillup
	*
	* @return
	*/
	public int[] getMaxup() {
		return finalSkillup;
	}

	/**
	* Return the offset and sub for the skill
	*
	* @param skillIndex the skill index to analyze
	*
	* @return the sub with offset of a player
	*/
	private double getOffset(int skillIndex) {
		double offset = player.getSubskill4Pos(skillIndex);
		return offset;
	}

	/**
	* Calculates the number of weeks needed for a future skillup
	* 
	* @param skillIndex		skill to use (from ISpieler.SKILL*)
	* @param	intensity	training intensity
	* @param	staminaTrainingPart	stamina share
	*
	* @return	the predicted length
	*/
	//private double getTrainingLength(int trType, int skillIndex, int intensity, int staminaTrainingPart) {
	private double getTrainingLength(WeeklyTrainingType wt, TrainingPerWeek tw) {
		int pos = getSkillPosition(wt.getPrimaryTrainingSkill());
		int curSkillUps = finalSkillup[pos];
		int age = player.getAlter();
		int ageDays = player.getAgeDays();
		int realSkill = player.getValue4Skill4(wt.getPrimaryTrainingSkill());
		// Set age and skill for simulation
		player.setAlter (age + (int)Math.floor((ageDays + 7*weeksPassed)/112d));
		player.setValue4Skill4 (wt.getPrimaryTrainingSkill(), realSkill+curSkillUps);
		double limit = wt.getTrainingLength(player, coTrainer, trainer, tw.getTrainingIntensity(), tw.getStaminaPart(), staff);
//		HOLogger.instance().debug(getClass(), "getTrLen for "+player.getName()+": weeksPassed="+weeksPassed+", age="+player.getAlter()+", skill="+getSkillValue(player, skillIndex)+", limit="+limit);
		// Undo simulation changes on player
		player.setAlter(age);
		player.setValue4Skill4 (wt.getPrimaryTrainingSkill(), realSkill);
		return limit;
	}

	private double getSecondaryTrainingLength(WeeklyTrainingType wt, TrainingPerWeek tw) {
		int pos = getSkillPosition(wt.getSecondaryTrainingSkill());
		int curSkillUps = finalSkillup[pos];
		int age = player.getAlter();
		int ageDays = player.getAgeDays();
		int realSkill = player.getValue4Skill4(wt.getSecondaryTrainingSkill());
		// Set age and skill for simulation
		player.setAlter (age + (int)Math.floor((ageDays + 7*weeksPassed)/112d));
		player.setValue4Skill4 (wt.getSecondaryTrainingSkill(), realSkill+curSkillUps);
		double limit = wt.getSecondaryTrainingLength(player, coTrainer, trainer, tw.getTrainingIntensity(), tw.getStaminaPart(), staff);
//		HOLogger.instance().debug(getClass(), "getTrLen for "+player.getName()+": weeksPassed="+weeksPassed+", age="+player.getAlter()+", skill="+getSkillValue(player, skillIndex)+", limit="+limit);
		// Undo simulation changes on player
		player.setAlter(age);
		player.setValue4Skill4 (wt.getSecondaryTrainingSkill(), realSkill);
		return limit;
	}
	/**
	* Checks if a skillup has happened
	*
	* @param skillIndex the skill to consider
	*
	* @return true if skillup happened
	*/
	private boolean checkSkillup(int pos) {
		if (finalSub[pos] >= 1) {
//			Alternative 1: Set sub=0 after a skillup 
//			(We will use this, until the training speed formula is optimized)
//			finalSub[pos] = 0;

//			TODO flattermann
//			Alternative 2: Use overflow sub after a skillup
//			(This would be more accurate. But only if the underlaying formula is exact) 
			finalSub[pos] -= 1;

			finalSkillup[pos]++;

			return true;
		}

		return false;
	}

	/**
	* Updates the training situation
	*
	* @param primarySkillIndex The skill to be updated
	* @param primaryPoints The points to be added
	* @param tw the training week settings for the considered week
	*/
	private void processTraining(WeeklyTrainingType wt, TrainingPoints trp, TrainingPerWeek tw) {
		// number of weeks necessary to have a skillup
		double primaryTrainLength = getTrainingLength(wt, tw);
		// If invalid training (trType does not train this skill)
		if (primaryTrainLength == -1)
			return;
		// calculate increase in sub
		double primarySubForThisWeek = trp.getPrimary()/ primaryTrainLength;
		int primaryPos = getSkillPosition(wt.getPrimaryTrainingSkill());
		double secondaryTrainLength = 0;
		double secondarySubForThisWeek = 0;
		int secondaryPos = -1;
		if (trp.getSecondary()> 0) {
			secondaryTrainLength = getSecondaryTrainingLength(wt, tw);
			secondarySubForThisWeek = trp.getSecondary() / secondaryTrainLength;
			secondaryPos = getSkillPosition(wt.getSecondaryTrainingSkill());
		}
		
		// add sub to skill
		finalSub[primaryPos] += Math.min(1.0f, primarySubForThisWeek);
		if (checkSkillup(primaryPos)) {
			PlayerSkillup su = new PlayerSkillup();
			su.setHtSeason(tw.getHattrickSeason());
			su.setHtWeek(tw.getHattrickWeek());
			su.setType(wt.getPrimaryTrainingSkill());
			su.setValue(player.getValue4Skill4(wt.getPrimaryTrainingSkill()) + finalSkillup[primaryPos]);
			su.setTrainType(ISkillup.SKILLUP_FUTURE);
			futureSkillups.add(su);
		}
		if (secondarySubForThisWeek > 0) {
			finalSub[secondaryPos] += Math.min(1.0f, secondarySubForThisWeek);
			if (checkSkillup(secondaryPos)) {
				PlayerSkillup su = new PlayerSkillup();
				su.setHtSeason(tw.getHattrickSeason());
				su.setHtWeek(tw.getHattrickWeek());
				su.setType(wt.getSecondaryTrainingSkill());
				su.setValue(player.getValue4Skill4(wt.getSecondaryTrainingSkill()) + finalSkillup[secondaryPos]);
				su.setTrainType(ISkillup.SKILLUP_FUTURE);
				futureSkillups.add(su);
			}
		}
	}

	/**
	 * Gets the primary training for a specific skill
	 * (e.g. ISpieler.SKILL_SPIELAUFBAU -> ITeam.TA_SPIELAUFBAU)
	 *  
	 * @param skillIndex	the skill to use
	 * @return				the primary training type
	 */
	private int getPrimaryTrainingForSkill (int skillIndex) {
		switch (skillIndex) {
			case PlayerSkill.KEEPER :
				return TrainingType.GOALKEEPING;

			case PlayerSkill.PLAYMAKING :
				return TrainingType.PLAYMAKING;

			case PlayerSkill.PASSING :
				return TrainingType.SHORT_PASSES;

			case PlayerSkill.WINGER :
				return TrainingType.CROSSING_WINGER;

			case PlayerSkill.DEFENDING :
				return TrainingType.DEFENDING;

			case PlayerSkill.SCORING :
				return TrainingType.SCORING;

			case PlayerSkill.SET_PIECES :
				return TrainingType.SET_PIECES;

		}

		return 0;
	}

	/**
	 * Gets the skill trained by a specific training type
	 * (ITeam.TA_* -> ISpieler.SKILL_*)
	 * 
	 * @param trType	training type
	 * @return			the trained skill
	 */
	private int getSkillForTraining (int trType) {
		switch (trType) {
			case TrainingType.GOALKEEPING:
				return PlayerSkill.KEEPER;

			case TrainingType.PLAYMAKING:
				return PlayerSkill.PLAYMAKING;

			case TrainingType.SHORT_PASSES:
			case TrainingType.THROUGH_PASSES:
				return PlayerSkill.PASSING;

			case TrainingType.CROSSING_WINGER:
			case TrainingType.WING_ATTACKS:
				return PlayerSkill.WINGER;

			case TrainingType.DEFENDING:
			case TrainingType.DEF_POSITIONS:
				return PlayerSkill.DEFENDING;

			case TrainingType.SCORING:
			case TrainingType.SHOOTING:
				return PlayerSkill.SCORING;

			case TrainingType.SET_PIECES:
				return PlayerSkill.SET_PIECES;

		}

		return 0;
	}

	private int getSkillPosition(int skillIndex) {
		switch (skillIndex) {
			case PlayerSkill.KEEPER :
				return 0;

			case PlayerSkill.PLAYMAKING :
				return 1;

			case PlayerSkill.PASSING :
				return 2;

			case PlayerSkill.WINGER :
				return 3;

			case PlayerSkill.DEFENDING :
				return 4;

			case PlayerSkill.SCORING :
				return 5;

			case PlayerSkill.SET_PIECES :
				return 6;

			case PlayerSkill.STAMINA :
				return 7;
		}
		return 0;

	}

	public FuturePlayer previewPlayer(int weekNumber) {
		return previewPlayer(1,weekNumber);
	}

}
