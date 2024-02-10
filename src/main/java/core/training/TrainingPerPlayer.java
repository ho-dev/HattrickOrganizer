package core.training;

import core.constants.player.PlayerSkill;
import core.model.player.Player;
import core.util.HODateTime;
import core.util.HOLogger;

import java.time.temporal.ChronoUnit;

import static java.lang.Math.max;

/**
 * Holds and calculates how much skill training a player received
 */
public class TrainingPerPlayer  {
    private Player _Player;
    private TrainingPoints _TrainingPair = null;
    private TrainingPerWeek _TrainingWeek;
	private double experienceSub=0;

	//~ Constructors -------------------------------------------------------------------------------

	/**
     * Creates a new TrainingPerPlayer object initialized with a specific player
     */
    public TrainingPerPlayer(Player oPlayer) {
    	_Player = oPlayer; 
    }

    //~ Methods ------------------------------------------------------------------------------------

    
    /**
     * Setter for property player.
     *
     * @param player New value of property player.
     */
    public final void setPlayer(Player player) {
        this._Player = player;
    }

    /**
     * Getter for property player.
     *
     * @return Value of property player.
     */
    public final Player getPlayer() {
        return this._Player;
    }

    public void setTrainingPair(TrainingPoints trp) {
		_TrainingPair = trp;
	}

    public TrainingPoints getTrainingPair() {
    	return _TrainingPair;
    }

    /**
     * get the training point for this instance
     * @return	training point
     */
	public TrainingPerWeek getTrainingWeek() {
		return _TrainingWeek;
	}

	/**
	 * set the training point for this instance and
	 * calculate the sub skills for the player using 
	 * the training week from this training point
	 *  
	 * @param trainingWeek training week info
	 */
	public void setTrainingWeek(TrainingPerWeek trainingWeek) {
		this._TrainingWeek = trainingWeek;
	}

    public void addExperience(double inc) {
		this.experienceSub += inc;
    }

	public double getExperienceSub() {
		return experienceSub;
	}

    public double calcSubskillIncrement(PlayerSkill skill, double skillValueBeforeTraining, HODateTime date) {

		int skillValue = (int) skillValueBeforeTraining;
		double ret = 0;

		if (skill != PlayerSkill.STAMINA) {
			var wt = WeeklyTrainingType.instance(this._TrainingWeek.getTrainingType());
			boolean isTrainedSkill = wt != null && wt.isTraining(skill);
			if (isTrainedSkill) {
				ret += wt.calculateSkillIncreaseOfTrainingWeek(skillValue, this);
			}
			/* Time to perform skill drop */
			if (SkillDrops.instance().isActive()) {
				ret -= SkillDrops.instance().getSkillDropAtDate(skillValue, this._Player.getAge(), skill, date, isTrainedSkill);
			}
			if (ret > 1) ret = 1; // limit 1
		} else {
			var stamina = this.getTrainingWeek().getStaminaShare() * this.getTrainingWeek().getTrainingIntensity() / 10000.;
			var playerAge = this._Player.getAge();
			var skillLevel = max(0, skillValueBeforeTraining - 1);
			ret = calcStaminaIncrement(skillLevel, stamina, playerAge);

			// Conditioning training is minute-based, just like normal training,
			// players who have played 90 minutes will receive 100% of the regular conditioning training for that week.
			// Players who have played less than 90 minutes will receive 75% plus a minute-by-minute share of the remaining 25% (25/90=0.28% per minute).
			// Players who have not played but are healthy enough (+1 or less) will receive 50%.
			// Severely injured players (from +2) do not receive any fitness training.
			// Source: https://www83.hattrick.org/Forum/Read.aspx?t=16799794&v=0&a=1&n=104

			var minutes = this.getTrainingPair().getTrainingDuration().getPlayedMinutes();
//				if (minutes < 90) {
//					if (minutes > 0) {
//						ret *= 0.75 + 0.25 / 90. * minutes;
//					} else if (_Player.getInjuryWeeks() < 2) {
//						ret *= 0.5;
//					} else {
//						ret = 0;
//					}
//				}
//447528025
			// St0 = a*St^2 + b*St
			//a=-0.0016
			//b=0.5037
			//
			//St0 - effective stamina with 0 minutes played.

			if (minutes == 0) {
				var trainingDate = this.getTrainingWeek().getTrainingDate();
				var lastMatch = _Player.getLastMatchDate();
				if (lastMatch != null && HODateTime.fromHT(lastMatch).plus(2 * 7, ChronoUnit.DAYS).isBefore(trainingDate)) {
					ret = -1. / 40;    // constant decrease if no played minutes since more than 2 weeks
				} else {
					ret = -0.0016 * stamina * stamina + 0.5037 * stamina;
				}
			}

			HOLogger.instance().info(getClass(), "minutes=" + minutes + " skill=" + skillLevel + " age=" + _Player.getAge() + " ret=" + ret);
		}
		return ret;
	}

	/**
     * Calculate the stamina increment
     * The formula are from Schum's thread: <a href="https://www88.hattrick.org/Forum/Read.aspx?t=17404127&n=28&nm=48&v=4">...</a>
     * @param skillLevel Skill value - 1 (excellent range is from 7..8)
     * @param stamina Stamina training settings (staminaShare * training intensity)
     * @param playerAge Age in years
     * @return Stamina increment
     */
	private double calcStaminaIncrement(double skillLevel, double stamina, int playerAge) {
		int k;
		if (playerAge < 20) k = 3;
		else if (playerAge < 25) k = 0;
		else if (playerAge < 34) k = playerAge - 24;
		else k = 2 * playerAge - 57;
		var kAge = 7.0 * k / 30.;

		var L = kAge + skillLevel;
		var S = stamina;

		var ret = 0.;
		if (L <= 7.0) {
		/*
			At L=7.0 and below

			T = (-1.05S^2 + 2.1S) * (mL^3 + nL^2 + o*L + p) - 0.21

			m = 0.00013
			n = 0.0048
			o = -0.301
			p = 2.826
		 */
			ret = (-1.05 * S * S + 2.1 * S) * (0.00013 * L * L * L + 0.0048 * L * L - 0.301 * L + 2.826) - 0.21;
		} else if (L < 7.56) {
			/*
				At L between 7.0 and 7.56:
				T = -1.05S^2 + 2.1S + eL^3 + fL^2 + g*L + h
				The function is the same, but with different parameters.

				e = -0.00772
				f = 0.0636
				g = -0.0178
				h = -0.554
			 */
			ret = -1.05 * S * S + 2.1 * S - 0.00772 * L * L * L + 0.0636 * L * L - 0.0178 * L - 0.554;
		} else {
			/*
				At L=7.56 and above:
				T = -1.05S^2 + 2.1S + aL^3 + bL^2 + c*L + d
				T is the value of endurance training
				S is the proportion of endurance in training (10% is 0.1)
				L is the sum of the player's stamina level and age coefficient.

				a = -0.00016
				b = -0.00544
				c = 0.0013
				d = -0.0185
			 */

			ret = -1.05 * S * S + 2.1 * S - 0.0016 * L * L * L - 0.00544 * L * L + 0.0013 * L - 0.0185;
		}

		// Tmax = -2.1S^2 + 4.2S - 0.21
		var Tmax = -2.1*S*S+4.2*S-0.21;
		if ( ret > Tmax) {
			ret = Tmax;
		}
		return ret;
	}

	/**
	 * Calculate player's age in years at the given training date
	 * @return int age of the player in hattrick-years
	 */
	public int getPlayerAgeAtTrainingDate() {
		return (int) this.getPlayer().getDoubleAgeFromDate(this._TrainingWeek.getTrainingDate());
	}
}
