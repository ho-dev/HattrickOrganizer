// %1762772776:de.hattrickorganizer.logik.matchengine.engine.core%
package core.prediction.engine;

import core.model.match.IMatchDetails;


class BaseActionGenerator {

	protected final int getArea(int tactic, int level) {
		int attackMiddle = 40;

		if (tactic == IMatchDetails.TAKTIK_MIDDLE) {
			attackMiddle = attackMiddle + (level * 3);
		}

		if (tactic == IMatchDetails.TAKTIK_WINGS) {
			attackMiddle = (int) (attackMiddle - (level * 1.5));
		}

		final int area = getRandom(100);

		if (area < attackMiddle) {
			return 0;
		}

		// Choose between left and Right
		if (area < (((100 - attackMiddle) / 2) + attackMiddle)) {
			return -1;
		}

		return 1;
	}

	protected static final int getRandom(int value) {
	        return (int) (Math.random() * value);
	}

	protected boolean isScore(TeamGameData tgd, int area) {
		double chance = 0;

		switch (area) {
			case -1 :
				chance = tgd.getRatings().getLeftAttack();
				break;

			case 0 :
				chance = tgd.getRatings().getMiddleAttack();
				break;

			case 1 :
				chance = tgd.getRatings().getRightAttack();
				break;
		}

		int effectiveness = (int) getEffectiveness(chance);

		if (getRandom(100) < effectiveness) {
			return true;
		}

		return false;
	}

	protected final TeamGameData compare(TeamData tv, TeamData ctv) {
		final double possession = getLinearChance(tv.getRatings().getMidfield(), ctv.getRatings().getMidfield());
		final double rchance = getLinearChance(tv.getRatings().getRightAttack(), ctv.getRatings().getLeftDef());
		final double lchance = getLinearChance(tv.getRatings().getLeftAttack(), ctv.getRatings().getRightDef());
		final double mchance = getLinearChance(tv.getRatings().getMiddleAttack(), ctv.getRatings().getMiddleDef());
		final double rrisk = getLinearChance(tv.getRatings().getRightDef(), ctv.getRatings().getLeftAttack());
		final double lrisk = getLinearChance(tv.getRatings().getLeftDef(), ctv.getRatings().getRightAttack());
		final double mrisk = getLinearChance(tv.getRatings().getMiddleDef(), ctv.getRatings().getMiddleAttack());

		final int actionNumber = (int) (getEffectiveness(possession) / 10.0) + 1;

		final TeamGameData tgd = new TeamGameData(actionNumber, possession, rchance, lchance, mchance, rrisk, lrisk, mrisk, tv.getTacticType(), tv.getTacticLevel());
		return tgd;
	}

	protected final boolean hasChance(TeamGameData tgd, int minute) {
		if (tgd.getActionAlreadyPlayed() >= tgd.getActionNumber()) {
			return false;
		}

		// Factor is used to increase chance of actions if the team has had less than expected as the game progress
		double factor = ((tgd.getActionNumber() - tgd.getActionAlreadyPlayed() + 1d) / (tgd.getActionNumber() + 1d) * (91 - minute)) / 90d * 6;

		if (factor > 1) {
			factor = 1;
		}

		return (getRandom((int) (90.0d * factor)) < tgd.getActionNumber());
	}

	static double getEffectiveness(double value) {
		double x = value * 100d;
		boolean low = false;

		if (x < 50) {
			low = true;
			x = 100 - x;
		}

		double v = (-500000.0 / Math.pow(x, 2.0)) + (10000.0 / x) + 50.0;

		if (low) {
			return 100 - v;
		}

		return v;
	}


	private double getLinearChance(double rate1, double rate2) {
		double ret = rate1 / (rate1 + rate2);
		return ret;
	}
	
	protected static int getRandomInt(double number) {
		int intPart = (int) (number / 1);		
		double decPart = number % 1.0;
		if (getRandom(10)<decPart*10) {
			intPart++;
		} 						
		return intPart;
	}	
}
