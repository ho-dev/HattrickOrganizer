package module.matches;

import core.model.match.MatchEvent;
import core.model.match.MatchEventID;

import javax.swing.*;
import java.util.List;

record MatchHighlightFromEvent(int homeTeamId,
                               MatchScore score,
                               boolean homeScored,
                               boolean guestScored,
                               MatchEvent matchEvent) implements MatchHighlight {

    public MatchEventID getMatchEventID() {
        return matchEvent.getMatchEventID();
    }

    public boolean isHomeAction() {
        return matchEvent.getTeamID() == homeTeamId;
    }

    public boolean isGoalEvent() {
        return matchEvent.isGoalEvent();
    }

    public boolean isSubstitution() {
        return matchEvent.isSubstitution();
    }

    public boolean isOwnGoalGoalEvent() {
        return matchEvent.isOwnGoalGoalEvent();
    }

    public boolean isPenaltyContestEvent() {
        return matchEvent.isPenaltyContestEvent();
    }

    public boolean isPenaltyContestStartEvent() {
        return matchEvent.getMatchEventID() == MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION;
    }

    public boolean isPenaltyContestDecisionByCoinToss() {
        return matchEvent.getMatchEventID() == MatchEventID.AFTER_22_PENALTIES_TOSSING_COIN;
    }

    public boolean isPenaltyContestGoalEvent() {
        return matchEvent.isPenaltyContestGoalEvent();
    }

    public boolean isPenaltyContestNoGoalEvent() {
        return matchEvent.isPenaltyContestNoGoalEvent();
    }

    public List<Icon> getIcons() {
        return matchEvent.getIcons();
    }

    public String getPlayerName() {
        return matchEvent.getPlayerName();
    }

    public String getAssistingPlayerName() {
        return matchEvent.getAssistingPlayerName();
    }

    public int getMinute() {
        return matchEvent.getMinute();
    }

    public MatchHighlightFromEvent withHomeGoal(int newHomeScore) {
        return new MatchHighlightFromEvent(
            this.homeTeamId,
            score.withHomeScore(newHomeScore),
            true,
            false,
            this.matchEvent);
    }

    public MatchHighlightFromEvent withGuestGoal(int newGuestScore) {
        return new MatchHighlightFromEvent(
            this.homeTeamId,
            score.withGuestScore(newGuestScore),
            false,
            true,
            this.matchEvent);
    }
}
