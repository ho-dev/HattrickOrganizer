package core.prediction.engine;


import core.util.Helper;

import java.util.Vector;


public class MatchPredictionManager {
    //~ Static fields/initializers -----------------------------------------------------------------
    private static MatchPredictionManager m_clInstance;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new instance of MatchPredictionManager
     */
    private MatchPredictionManager() {
    }

    //~ Methods ------------------------------------------------------------------------------------
    public static MatchPredictionManager instance() {
        if (m_clInstance == null) {
            m_clInstance = new MatchPredictionManager();
        }
        return m_clInstance;
    }

    public MatchData getMatchData(TeamData home, TeamData away) {
        return new MatchData((TeamData) home, (TeamData) away);
    }

    /**
     * calculates a match ( 90 minutes ) and returns list of events for both teams.
     *
     * @return Vector holding IMPActions for that match
     */
    public Vector<Action> calculateMatch(TeamData home, TeamData away) {
        final Vector<Action> actions = new Vector<Action>();
        final MatchData matchengine = new MatchData((TeamData) home, (TeamData) away);

        for (int i = 0; i < 91; i++) {
        	Helper.copyArray2Vector(matchengine.advance(), actions);
        }
        return actions;
    }
    
	public MatchResult calculateMatchResult(TeamData home, TeamData away) {
		final MatchData matchengine = new MatchData((TeamData) home, (TeamData) away);
		MatchResult result = new MatchResult();
		result.addActions(matchengine.simulate());
		return result;
	}    

    /**
     * calculates a number of matches match ( 90 minutes ) and returns list of events for both
     * teams.
     *
     * @return vector contaning Vectors holding IMPActions for each match
     */
    public MatchResult calculateNMatches(int numberOfMatches, TeamData home,
                                              TeamData away) {
		final MatchData matchengine = new MatchData((TeamData) home, (TeamData) away);
		MatchResult result = new MatchResult();		
        for (int i = 0; i < numberOfMatches; i++) {			        	
			result.addActions(matchengine.simulate());
        }
		return result;

    }

    public TeamData generateTeamData(String name, TeamRatings _ratings, int _tactic, int _level) {
        return new TeamData(name, (TeamRatings) _ratings, _tactic, _level);
    }

    public TeamRatings generateTeamRatings(double midfield, double leftDef, double middleDef,
                                              double rightDef, double leftAttack,
                                              double middleAttack, double rightAttack) {
        return new TeamRatings(midfield, leftDef, middleDef, rightDef, leftAttack, middleAttack,
                               rightAttack);
    }
}
