package core.training;

import core.constants.TrainingType;
import core.constants.player.PlayerSkill;
import core.model.UserParameter;
import core.model.player.FuturePlayer;
import core.model.player.ISkillChange;
import core.model.player.Player;
import core.util.HelperWrapper;
import module.training.Skills;
import java.util.*;


public class FutureTrainingManager {
	/** Actual Training sub */
	public double[] actual = new double[8];
		
	/** Maximum training sub after future training */
	public double[] finalSub = new double[8];

	/** Number of skill ups with maximum training */
	public double[] finalSkill = new double[8];

	/** Active player */
	private Player player;
	private List<TrainingPerWeek> futureTrainings;
	private List<ISkillChange> futureSkillups;
	private int weeksPassed = 0;
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

	private static int[] skillIndex = {
			PlayerSkill.KEEPER,
			PlayerSkill.PLAYMAKING,
			PlayerSkill.PASSING,
			PlayerSkill.WINGER,
			PlayerSkill.DEFENDING,
			PlayerSkill.SCORING,
			PlayerSkill.SET_PIECES,
			PlayerSkill.STAMINA
	};

	public FuturePlayer previewPlayer(int numberOfWeeks) {

		this.futureSkillups = new ArrayList<>();
				
		for ( int i=0; i<8; i++){
			// Sets the actual training levels
			actual[i] = getOffset(skillIndex[i]);
			// rest the other 4 arrays min and max level are equals to actual at beginning
			finalSub[i] = actual[i];
			finalSkill[i] = Skills.getSkillValue(this.player,skillIndex[i]);
		}

		trainingSpeed = 0;
		weeksPassed = 0;
		int position = HelperWrapper.instance().getPosition(player.getIdealPosition());
		var trainingPerPlayer = new TrainingPerPlayer(player);
		// Iterate thru all the future training weeks
		for (int week = 1; week <= numberOfWeeks; week++) {

			// process skill drops
			int age = this.player.getAlter() + (this.player.getAgeDays() + week*7)/112;
			for ( int i=0; i<8; i++){
				finalSub[i] -= SkillDrops.instance().getSkillDrop((int)finalSkill[i], age, skillIndex[i])/100;
			}

			double trainingSpeed=0;
			weeksPassed++;
			TrainingPerWeek tw = this.futureTrainings.get(week-1);
			int trType = tw.getTrainingType();
			TrainingWeekPlayer tp = new TrainingWeekPlayer(player);

			WeeklyTrainingType wt = WeeklyTrainingType.instance(trType);
			if (wt != null) {

				var trainingPrio = tp.getFutureTrainingPrio(wt, tw.getTrainingDate());

				if ( trainingPrio != null ) {
					switch (trainingPrio) {
						case FULL_TRAINING:
							tp.addFullTrainingMinutes(90);
							tp.addBonusTrainingMinutes(90);
							trainingSpeed = 1;
							break;
						case PARTIAL_TRAINING:
							if ( wt.getTrainingType() == TrainingType.SET_PIECES){
								tp.addFullTrainingMinutes(90);
								trainingSpeed = 1;
							}
							else {
								tp.addPartlyTrainingMinutes(90);
								trainingSpeed = 1.0 / wt.getPrimaryTrainingSkillPartlyBaseLengthRate();
							}
							break;
						case OSMOSIS_TRAINING:
							tp.addOsmosisTrainingMinutes(90);
							trainingSpeed += 1.0 / wt.getPrimaryTrainingSkillOsmosisBaseLengthRate();
							break;
						default:
							break;
					}
					if (this.trainingSpeed < trainingSpeed) {
						this.trainingSpeed = trainingSpeed;
					}

					trainingPerPlayer.setTrainingPair(new TrainingPoints(wt,tp));
					trainingPerPlayer.setTrainingWeek(tw);
					int pos = getSkillPosition(wt.getPrimaryTrainingSkill());
					finalSub[pos]+= wt.calculateSkillIncreaseOfTrainingWeek((int)finalSkill[pos], trainingPerPlayer);
					pos = getSkillPosition(wt.getSecondaryTrainingSkill());
					if ( pos != -1 ){
						finalSub[pos]+= wt.calculateSkillIncreaseOfTrainingWeek((int)finalSkill[pos], trainingPerPlayer);
					}

					for (int i = 0; i < 8; i++) {
						int change = checkSkillChange(i);
						if (change != 0) {
							if (!UserParameter.instance().TRAINING_SHOW_SKILLDROPS && change < 0) continue;
							var trainingDate = tw.getTrainingDate();
							var hattrickDate = HattrickDate.fromInstant(trainingDate);
							PlayerSkillChange su = new PlayerSkillChange();
							su.setHtSeason(hattrickDate.getLocalSeason());
							su.setHtWeek(hattrickDate.getWeek());
							su.setType(skillIndex[i]);
							su.setValue(finalSkill[i]);
							su.setTrainType(ISkillChange.SKILLUP_FUTURE);
							su.setDate(Date.from(trainingDate));
							su.setAge(player.getAgeWithDaysAsString(su.getDate()));
							su.setChange(change);
							futureSkillups.add(su);
						}
					}
				}
			}
		}
		FuturePlayer fp = new FuturePlayer();
		fp.setAttack(getFinalValue(PlayerSkill.SCORING));		
		fp.setCross(getFinalValue(PlayerSkill.WINGER));
		fp.setDefense(getFinalValue(PlayerSkill.DEFENDING));
		fp.setGoalkeeping(getFinalValue(PlayerSkill.KEEPER));
		fp.setPassing(getFinalValue(PlayerSkill.PASSING));
		fp.setPlaymaking(getFinalValue(PlayerSkill.PLAYMAKING));
		fp.setSetpieces(getFinalValue(PlayerSkill.SET_PIECES));
		fp.setAge(player.getAlter()+(int)(Math.floor((player.getAgeDays()+7*weeksPassed)/112d)));
		fp.setPlayerId(player.getPlayerID());
		return fp;
	}

	/**
	 * get the final value (including skillups and sub) for a specific skill
	 * 
	 * @param skillIndex	index of the skill
	 * @return				value for this skill
	 */
	private double getFinalValue(int skillIndex) {
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
	private double getOffset(int skill) {
		return player.getSub4Skill(skill);
	}

	/**
	* Checks if a skillup has happened
	*
	*
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

	/**
	 * Gets the primary training for a specific skill
	 * (e.g. ISpieler.SKILL_SPIELAUFBAU -> ITeam.TA_SPIELAUFBAU)
	 *  
	 * @param skillIndex	the skill to use
	 * @return				the primary training type
	 */
	private int getPrimaryTrainingForSkill (int skillIndex) {
		return switch (skillIndex) {
			case PlayerSkill.KEEPER -> TrainingType.GOALKEEPING;
			case PlayerSkill.PLAYMAKING -> TrainingType.PLAYMAKING;
			case PlayerSkill.PASSING -> TrainingType.SHORT_PASSES;
			case PlayerSkill.WINGER -> TrainingType.CROSSING_WINGER;
			case PlayerSkill.DEFENDING -> TrainingType.DEFENDING;
			case PlayerSkill.SCORING -> TrainingType.SCORING;
			case PlayerSkill.SET_PIECES -> TrainingType.SET_PIECES;
			default -> 0;
		};

	}

	private int getSkillPosition(int skillIndex) {
		return switch (skillIndex) {
			case PlayerSkill.KEEPER -> 0;
			case PlayerSkill.PLAYMAKING -> 1;
			case PlayerSkill.PASSING -> 2;
			case PlayerSkill.WINGER -> 3;
			case PlayerSkill.DEFENDING -> 4;
			case PlayerSkill.SCORING -> 5;
			case PlayerSkill.SET_PIECES -> 6;
			case PlayerSkill.STAMINA -> 7;
			default -> -1;
		};

	}

	public Player getPlayer() {
		return this.player;
	}
}
