package core.prediction.engine;

import java.util.ArrayList;
import java.util.List;


public class TeamData  {
    //~ Instance fields ----------------------------------------------------------------------------
    private List<Action> actions;
    private String teamName;
    private TeamRatings ratings;
    private int tacticLevel;
    private int tacticType;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Create the Data for an Team
     *
     * @param name Name of the team
     * @param _ratings The teamratings of the team
     * @param _tactic The tactic, Uses the TAKTIK - Constants from IMatchDetails
     * @param _level Tacticlevel from 1 to 20
     */
    public TeamData(String name, TeamRatings _ratings, int _tactic, int _level) {
        actions = new ArrayList<Action>();
        ratings = _ratings;
        tacticLevel = _level;
        tacticType = _tactic;
        teamName = name;
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final List<Action> getActions() {
        return actions;
    }

    public final void setRatings(TeamRatings ratings) {
        this.ratings = ratings;
    }

    public final TeamRatings getRatings() {
        return ratings;
    }

    public final void setTacticLevel(int i) {
        tacticLevel = i;
    }

    public final int getTacticLevel() {
        return tacticLevel;
    }

    public final void setTacticType(int i) {
        tacticType = i;
    }

    public final int getTacticType() {
        return tacticType;
    }

    public final String getTeamName() {
        return teamName;
    }

    public final void addAction(Action action) {
        actions.add(action);
    }

    @Override
	public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("TeamData[");
        buffer.append("actions = " + actions);
        buffer.append(", ratings = " + ratings);
        buffer.append(", tacticType = " + tacticType);
        buffer.append(", tacticLevel = " + tacticLevel);
        buffer.append(", teamName = " + teamName);
        buffer.append("]");
        return buffer.toString();
    }
}
