package core.prediction.engine;


import core.model.match.IMatchDetails;

class CounterAttackGenerator extends BaseActionGenerator {

	private int home = 0;
	private int away = 0;

	CounterAttackGenerator(TeamData homeTeamData, TeamData awayTeamData) {
		home = getCounterAction(homeTeamData, awayTeamData);
		away = getCounterAction(awayTeamData, homeTeamData);
	}

	/**
	 * Generates the CA action, if any
	 *
	 * @param minute The minute to simulate
	 * @param team The TeamGameData for the team having the opportunity for a CA
	 *
	 * @return the CA Action generated or null if team had no CA
	 */
	protected final Action calculateCounterAttack(int minute, TeamGameData team) {
		// IF team win at midfield no CA
		if (team.getRatings().getMidfield() > 0.5) {
			return null;
		}
		int maxChance = 0;

		if (team.isHome()) {
			maxChance = home;
		} else {
			maxChance = away;
		}

		if (team.getCounterAction() >= maxChance) {
			return null;
		}

		team.addCounterActionPlayed();

		final Action ca = new Action();
		ca.setType(IMatchDetails.TAKTIK_KONTER);
		ca.setMinute(minute);
		ca.setArea(getArea(team.getTacticType(), team.getTacticLevel()));
		ca.setHomeTeam(team.isHome());
		ca.setScore(isScore(team, ca.getArea()));
//		HOLogger.instance().log(getClass(),"CA");
		return ca;
	}

	private int getCounterAction(TeamData homeTeamData, TeamData awayTeamData) {
		if (homeTeamData.getTacticType() != IMatchDetails.TAKTIK_KONTER) {
			return 0;
		}
		final int ability = homeTeamData.getTacticLevel();
		final double def = awayTeamData.getRatings().getLeftDef() + awayTeamData.getRatings().getMiddleDef() + awayTeamData.getRatings().getRightDef();

		double counterIndex = (ability / (def / 6.0 + ability)) * 100;
		double ca = 4.00008896306671/(1+58995.2231780103*Math.exp(-0.21970325236894*counterIndex));		
		int number = getRandomInt(ca);
		if (number > 3) {
			number = 3;
		}
		return number;
	}
}
