package core.prediction.engine;


import core.model.match.IMatchDetails;
import core.util.HOLogger;

import java.util.List;



public class MatchData {

    private final ActionGenerator generator = new ActionGenerator();
    private final TeamData awayTeam;
    private final TeamData homeTeam;
    private int minute;

    MatchData(TeamData home, TeamData away) {
        homeTeam = home;
        awayTeam = away;
        generator.setTeams(homeTeam, awayTeam);
    }

    public final Action[] advance() {
        final Action[] actions = generator.predict(minute);

        for (final Action action : actions) {
            if (action.isHomeTeam()) {
                homeTeam.addAction(action);
            } else {
                awayTeam.addAction(action);
            }
        }

        minute++;
        return actions;
    }

	public final Action[] simulate() {
		final Action[] actions = generator.simulate();
        for (final Action action : actions) {
            if (action.isHomeTeam()) {
                homeTeam.addAction(action);
            } else {
                awayTeam.addAction(action);
            }
        }
		return actions;
	}

    public final void recap() {
        printRecap(homeTeam);
        printRecap(awayTeam);
    }

    @Override
	public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Match[");
        buffer.append("generator = ").append(generator);
        buffer.append(", minute = ").append(minute);
        buffer.append(", team1 = ").append(homeTeam);
        buffer.append(", team2 = ").append(awayTeam);
        buffer.append("]");
        return buffer.toString();
    }

    private void printRecap(TeamData team) {
        final List<Action> actions = team.getActions();
        HOLogger.instance().log(getClass(),team.getTeamName());

        int c = 0;
        int g = 0;
        int ca = 0;

        for (Action action : actions) {

            if (((Action) action).isScore()) {
                g++;
            }

            if (((Action) action).getType() == IMatchDetails.TAKTIK_KONTER) {
                ca++;
            } else {
                c++;
            }

            HOLogger.instance().log(getClass(), (Action) action);
        }

        HOLogger.instance().log(getClass(),"Chances " + c);
        HOLogger.instance().log(getClass(),"Goals   " + g);
        HOLogger.instance().log(getClass(),"counter " + ca);
    }
}
