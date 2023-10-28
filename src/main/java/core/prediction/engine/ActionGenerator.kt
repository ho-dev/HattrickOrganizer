// %2981324713:de.hattrickorganizer.logik.matchengine.engine.core%
package core.prediction.engine;




import core.model.match.IMatchDetails;

import java.util.ArrayList;
import java.util.List;

 class ActionGenerator extends BaseActionGenerator {

	private CounterAttackGenerator caGenerator;
	private TeamGameData awayTeamGameData;
	private TeamGameData homeTeamGameData;

	public ActionGenerator() {
	}

	public ActionGenerator(TeamData homeTeamData, TeamData awayTeamData) {
		setTeams(homeTeamData, awayTeamData);
		caGenerator = new CounterAttackGenerator(homeTeamData, awayTeamData);
	}

	final void setTeams(TeamData homeTeamData, TeamData awayTeamData) {
		caGenerator = new CounterAttackGenerator(homeTeamData, awayTeamData);
		homeTeamGameData = compare(homeTeamData, awayTeamData);
		homeTeamGameData.setHome(true);
		awayTeamGameData = compare(awayTeamData, homeTeamData);
		awayTeamGameData.setHome(false);
	}

	final Action[] predict(int minute) {
		final List<Action> actions = new ArrayList<Action>();
		actions.addAll(calculateActions(minute, homeTeamGameData, awayTeamGameData));
		actions.addAll(calculateActions(minute, awayTeamGameData, homeTeamGameData));
		return actions.toArray(new Action[0]);
	}

	Action[] simulate() {
		final List<Action> actions = new ArrayList<Action>();
		int midfieldPossession = (int) getEffectiveness(homeTeamGameData.getRatings().getMidfield());
		int pressing = getPressing(homeTeamGameData, awayTeamGameData);
		int succesfulPressing = 0;
						
		for(int i = 0; i < 10; i++) {
			// Pressing modifier
			if ( (succesfulPressing<7) && ((succesfulPressing*2)<=pressing) ) {			
				if (getRandom(14)<pressing) {
					succesfulPressing++;
					continue;			
				}								
			}			
									
			boolean homeAction = false;
			if (getRandom(100)<midfieldPossession) {
				homeAction = true;			
			} 		
									
			if (homeAction) {
				actions.addAll(calculateAction(homeTeamGameData, awayTeamGameData));
			} else {
				actions.addAll(calculateAction(awayTeamGameData, homeTeamGameData));		
			}
		}						
		return actions.toArray(new Action[0]);
	}

	private int getPressing(TeamData td1, TeamData td2) {
		int lvl = 0;

		if ( (td1.getTacticType() == IMatchDetails.TAKTIK_PRESSING) && (td1.getTacticLevel()>4) )  {
			lvl = lvl + td1.getTacticLevel() - 4;
		}

		if ( (td2.getTacticType() == IMatchDetails.TAKTIK_PRESSING) && (td2.getTacticLevel()>4) )  {
			lvl = lvl + td2.getTacticLevel() - 4;
		}
		return lvl;
	}

	private List<Action> calculateActions(int minute, TeamGameData team, TeamGameData opponent) {
		final List<Action> actions = new ArrayList<Action>();
		boolean hasChance = hasChance(team, minute);

		if (hasChance) {
			final int rnd = getRandom(20);

			if (rnd < getPressing(team, opponent)) {
				hasChance = false;
				team.addActionPlayed();
			}
		}

		if (hasChance) {
			final Action action = new Action();
			action.setArea(getArea(team.getTacticType(), team.getTacticLevel()));
			action.setMinute(minute);
			action.setType(0);
			action.setHomeTeam(team.isHome());
			actions.add(action);

			if (isScore(team, action.getArea())) {
				action.setScore(true);
			} else if (opponent.getTacticType() == IMatchDetails.TAKTIK_KONTER) {
				final Action ca = caGenerator.calculateCounterAttack(minute, opponent);

				if (ca != null) {
					actions.add(ca);
				}
			}

			team.addActionPlayed();
		}

		return actions;
	}

	private List<Action> calculateAction(TeamGameData team, TeamGameData opponent) {
		final List<Action> actions = new ArrayList<Action>();
		final Action action = new Action();
		action.setArea(getArea(team.getTacticType(), team.getTacticLevel()));
		action.setType(0);
		action.setHomeTeam(team.isHome());
		actions.add(action);
		
		if (getRandom(10)<1) {
			// SP event
			int type = getRandom(2);
		
			int successRate = 75;
			if (type==0) {
				successRate = 25;
			}
			boolean isScore = false;
			if (getRandom(100)<successRate) {
				isScore = true;
			}
			
			action.setScore(isScore);			
			return actions;		
		}
		
		if (isScore(team, action.getArea())) {
			action.setScore(true);
		} else if (opponent.getTacticType() == IMatchDetails.TAKTIK_KONTER) {
			final Action ca = caGenerator.calculateCounterAttack(0, opponent);

			if (ca != null) {
				actions.add(ca);
			}
		}
		return actions;
	}

}
