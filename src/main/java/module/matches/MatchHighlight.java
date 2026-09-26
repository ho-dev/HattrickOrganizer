package module.matches;

import core.model.match.MatchEvent;

import javax.swing.*;
import java.util.List;

public interface MatchHighlight {

    MatchScore score();
    boolean homeScored();
    boolean guestScored();
    MatchEvent.MatchEventID getMatchEventID();
    boolean isHomeAction();
    boolean isGoalEvent();
    boolean isSubstitution();
    boolean isOwnGoalGoalEvent();
    boolean isPenaltyContestEvent();
    boolean isPenaltyContestStartEvent();
    boolean isPenaltyContestDecisionByCoinToss();
    boolean isPenaltyContestGoalEvent();
    boolean isPenaltyContestNoGoalEvent();
    List<Icon> getIcons();
    String getPlayerName();
    String getAssistingPlayerName();
    int getMinute();
}
