package core.training;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.UserParameter;
import core.model.player.FuturePlayer;
import core.model.player.ISkillChange;
import core.model.player.Player;
import core.util.HOLogger;
import java.util.*;

public class FutureTrainingManager {
	/** Actual Training sub */
	public double[] actual = new double[8];
		
	/** Maximum training sub after future training */
	public double[] finalSub = new double[8];

	/** Number of skill ups with maximum training */
	public double[] finalSkill = new double[8];

	private static final PlayerSkill[] SKILL_INDEX = {
			PlayerSkill.KEEPER,
			PlayerSkill.PLAYMAKING,
			PlayerSkill.PASSING,
			PlayerSkill.WINGER,
			PlayerSkill.DEFENDING,
			PlayerSkill.SCORING,
			PlayerSkill.SETPIECES,
			PlayerSkill.STAMINA
	};

	/** Active player */
	private final Player player;
	private final List<TrainingPerWeek> futureTrainings;
	private List<ISkillChange> futureSkillups;
	private double trainingSpeed;

	/**
	* Calculates the effects of the future training for the provided player
	*
	* @param p The active player
	* @param trainings The future training
	*/
	public FutureTrainingManager(Player p, List<TrainingPerWeek> trainings) {
		player = p;
		futureSkillups = new ArrayList<>();
		futureTrainings = trainings;
		previewPlayer(UserParameter.instance().futureWeeks);
	}

	public FuturePlayer previewPlayer(int numberOfWeeks) {

		this.futureSkillups = new ArrayList<>();
				
		for (int i=0; i<8; i++) {
			// Sets the actual training levels
			actual[i] = getOffset(SKILL_INDEX[i]);
			// rest the other 4 arrays min and max level are equals to actual at beginning
			finalSub[i] = actual[i];
			finalSkill[i] = player.getSkill(SKILL_INDEX[i]);
		}

		trainingSpeed = 0;
		int weeksPassed = 0;

		var trainingPerPlayer = new TrainingPerPlayer(player);

		if (this.futureTrainings.size() >= numberOfWeeks) {
			// Iterate through all the future training weeks
			for (int week = 1; week <= numberOfWeeks; week++) {
				TrainingPerWeek trainingPerWeek = this.futureTrainings.get(week - 1);
				int trainingType = trainingPerWeek.getTrainingType();
				final WeeklyTrainingType weeklyTrainingType = WeeklyTrainingType.instance(trainingType);
				if (weeklyTrainingType != null) {
					final TrainingWeekPlayer trainingWeekPlayer = new TrainingWeekPlayer(player);
					var trainingPriority = trainingWeekPlayer.getFutureTrainingPrio(weeklyTrainingType, trainingPerWeek.getTrainingDate());

					// process skill drops
					int ageInYears = this.player.getAge() + (this.player.getAgeDays() + week * 7) / 112;
					for (int i = 0; i < SKILL_INDEX.length; i++) {
						var skill = SKILL_INDEX[i];
						finalSub[i] -= SkillDrops.instance().getSkillDrop((int) finalSkill[i], ageInYears, skill,
								trainingPriority != null &&
										trainingPriority != FuturePlayerTraining.Priority.NO_TRAINING &&
										weeklyTrainingType.isTraining(skill));
					}

					double trainingSpeed = 0;
					weeksPassed++;

					if (trainingPriority != null) {
						switch (trainingPriority) {
							case FULL_TRAINING:
								trainingWeekPlayer.addFullTrainingMinutes(90);
								trainingWeekPlayer.addBonusTrainingMinutes(90);
								trainingSpeed = 1;
								break;
							case PARTIAL_TRAINING:
								if (weeklyTrainingType.getTrainingType() == TrainingType.SET_PIECES) {
									trainingWeekPlayer.addFullTrainingMinutes(90);
									trainingSpeed = 1;
								} else {
									trainingWeekPlayer.addPartlyTrainingMinutes(90);
									trainingSpeed = 1.0 / weeklyTrainingType.getPrimaryTrainingSkillPartlyBaseLengthRate();
								}
								break;
							case OSMOSIS_TRAINING:
								trainingWeekPlayer.addOsmosisTrainingMinutes(90);
								trainingSpeed += 1.0 / weeklyTrainingType.getPrimaryTrainingSkillOsmosisBaseLengthRate();
								break;
							default:
								break;
						}
						if (this.trainingSpeed < trainingSpeed) {
							this.trainingSpeed = trainingSpeed;
						}

						trainingPerPlayer.setTrainingPair(new TrainingPoints(weeklyTrainingType, trainingWeekPlayer));
						trainingPerPlayer.setTrainingWeek(trainingPerWeek);
						int pos = getSkillPosition(weeklyTrainingType.getPrimaryTrainingSkill());
						finalSub[pos] += weeklyTrainingType.calculateSkillIncreaseOfTrainingWeek((int) finalSkill[pos], trainingPerPlayer);
						pos = getSkillPosition(weeklyTrainingType.getSecondaryTrainingSkill());
						if (pos != -1) {
							finalSub[pos] += weeklyTrainingType.calculateSkillIncreaseOfTrainingWeek( (int) finalSkill[pos], trainingPerPlayer);
						}

						for (int i = 0; i < SKILL_INDEX.length; i++) {
							int change = checkSkillChange(i);
							if (change != 0) {
								if (!UserParameter.instance().TRAINING_SHOW_SKILLDROPS && change < 0) continue;
								var trainingDate = trainingPerWeek.getTrainingDate();
								var htWeek = trainingDate.toLocaleHTWeek();
								PlayerSkillChange su = new PlayerSkillChange();
								su.setHtSeason(htWeek.season);
								su.setHtWeek(htWeek.week);
								su.setType(SKILL_INDEX[i]);
								su.setValue(finalSkill[i]);
								su.setTrainType(ISkillChange.SKILLUP_FUTURE);
								su.setDate(trainingDate);
								su.setAge(player.getAgeWithDaysAsString(su.getDate()));
								su.setChange(change);
								futureSkillups.add(su);
							}
						}
					}
				}
			}
		} else {
			HOLogger.instance().warning(FutureTrainingManager.class, "training weeks computed: "
					+ futureTrainings.size() + " not matching number of weeks: " + numberOfWeeks
			);
		}

		FuturePlayer fp = new FuturePlayer();
		fp.setAttack(getFinalValue(PlayerSkill.SCORING));		
		fp.setCross(getFinalValue(PlayerSkill.WINGER));
		fp.setDefense(getFinalValue(PlayerSkill.DEFENDING));
		fp.setGoalkeeping(getFinalValue(PlayerSkill.KEEPER));
		fp.setPassing(getFinalValue(PlayerSkill.PASSING));
		fp.setPlaymaking(getFinalValue(PlayerSkill.PLAYMAKING));
		fp.setSetpieces(getFinalValue(PlayerSkill.SETPIECES));
		fp.setAge(player.getAge()+(int)(Math.floor((player.getAgeDays()+7* weeksPassed)/112d)));
		fp.setPlayerId(player.getPlayerId());
		return fp;
	}

	/**
	 * get the final value (including skillups and sub) for a specific skill
	 * 
	 * @param skillIndex	index of the skill
	 * @return				value for this skill
	 */
	private double getFinalValue(PlayerSkill skillIndex) {
		int pos = getSkillPosition(skillIndex);
		return finalSkill[pos];
	}

	/**
	* Get the array of the actual training sub
	*
	* @return current training subs
	*/
	public double[] getActual() {
		return actual;
	}

	/**
	* Returns a list of all future skillups as predicted
	*
	* @return List of Skillups
	*/
	public List<ISkillChange> getFutureSkillups() {
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
	* Return the offset and sub for the skill
	*
	* @param skill  the skill index to analyze
	*
	* @return the sub with offset of a player
	*/
	private double getOffset(PlayerSkill skill) {
		return player.getSub4Skill(skill);
	}

	/**
	* Checks if a skillup has happened
	* @return
	 * 1 if skillup happened
	 * -1 if skilldrop
	 * 0 no change
	*/
	private int checkSkillChange(int pos) {
		if (finalSub[pos] >= 1) {
			finalSub[pos] -= 1;
			int v = (int)finalSkill[pos]+1;
			finalSkill[pos] = finalSub[pos]+v;
			return 1;
		} else if (finalSub[pos] < -.005) { // subs between 0 and -.005 are rounded up in display
			if (finalSkill[pos] <= 0) {
				finalSkill[pos] = 0;
				finalSub[pos] = 0;
				return 0;
			}
			finalSub[pos] += 1;
			int v = (int)finalSkill[pos]-1;
			if ( v < 0){
				finalSkill[pos]=0;
			}
			else {
				finalSkill[pos] = finalSub[pos] + v;
			}
			return -1;
		}
		return 0;
	}

	private int getSkillPosition(PlayerSkill skillIndex) {
		if (skillIndex==null) return -1;
		return switch (skillIndex) {
			case KEEPER -> 0;
			case PLAYMAKING -> 1;
			case PASSING -> 2;
			case WINGER -> 3;
			case DEFENDING -> 4;
			case SCORING -> 5;
			case SETPIECES -> 6;
			case STAMINA -> 7;
			default -> -1;
		};

	}

	public Player getPlayer() {
		return this.player;
	}
}
