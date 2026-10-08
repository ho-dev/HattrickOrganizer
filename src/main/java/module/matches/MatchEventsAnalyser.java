package module.matches;

import core.model.match.MatchEvent;
import core.model.match.MatchEventID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class MatchEventsAnalyser {

    private MatchEventsAnalyser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static MatchHighlights analyse(int homeTeamId, List<MatchEvent> matchHighlights) {
        List<MatchEvent> regularAndExtraTime = new ArrayList<>();
        List<MatchEvent> penaltyContestEvents = new ArrayList<>();

        matchHighlights.stream()
            .filter(MatchEventsAnalyser::isHighlight)
            .forEach(matchEvent -> {
                    if (isHighlightInRegularOrExtraTime(matchEvent)) {
                        regularAndExtraTime.add(matchEvent);
                    } else if (isHighlightInPenaltyContest(matchEvent)) {
                        penaltyContestEvents.add(matchEvent);
                    } else {
                        throw new IllegalStateException("MatchEvent is not a highlight in regular or extra time and not a highlight for penalty context: %s"
                            .formatted(matchEvent));
                    }
                }
            );

        final var regularAndExtraTimeHighlights = createRegularAndExtraTimeHighlights(homeTeamId, regularAndExtraTime);
        final MatchHighlightPenaltyContest matchHighlightPenaltyContest = createMatchHighlightPenaltyContest(
            homeTeamId,
            regularAndExtraTimeHighlights.score(),
            penaltyContestEvents);
        return new MatchHighlights(regularAndExtraTimeHighlights, matchHighlightPenaltyContest);
    }

    private static boolean isHighlight(MatchEvent matchEvent) {
        return isHighlightInRegularOrExtraTime(matchEvent) || isHighlightInPenaltyContest(matchEvent);
    }

    private static boolean isHighlightInRegularOrExtraTime(MatchEvent matchEvent) {
        return matchEvent.isGoalEvent() ||
            matchEvent.isNonGoalEvent() ||
            matchEvent.isBruisedOrInjured() ||
            matchEvent.isBooked() ||
            matchEvent.isSubstitution() ||
            matchEvent.isChangeOfTactic();
    }

    private static boolean isHighlightInPenaltyContest(MatchEvent matchEvent) {
        return matchEvent.isPenaltyContestEvent() ||
            matchEvent.getMatchEventID() == MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION;
    }

    private static MatchHighlightsRegularAndExtraTime createRegularAndExtraTimeHighlights(int homeTeamId, List<MatchEvent> regularAndExtraTimeMatchEvents) {
        AtomicInteger currentHomeScore = new AtomicInteger(0);
        AtomicInteger currentGuestScore = new AtomicInteger(0);
        List<MatchHighlight> highlights = regularAndExtraTimeMatchEvents.stream()
            .map(matchEvent -> new MatchHighlightFromEvent(homeTeamId,
                MatchScore.of(currentHomeScore.get(), currentGuestScore.get()),
                false, false,
                matchEvent))
            .map(matchHighlightFromEvent -> {
                if (matchHighlightFromEvent.isGoalEvent()) {
                    if (matchHighlightFromEvent.isHomeAction() ^ matchHighlightFromEvent.isOwnGoalGoalEvent()) {
                        return matchHighlightFromEvent.withHomeGoal(currentHomeScore.incrementAndGet());
                    } else {
                        return matchHighlightFromEvent.withGuestGoal(currentGuestScore.incrementAndGet());
                    }
                }
                return matchHighlightFromEvent;
            })
            .map(MatchHighlight.class::cast)
            .toList();
        return new MatchHighlightsRegularAndExtraTime(MatchScore.of(currentHomeScore.get(), currentGuestScore.get()), highlights);
    }

    private static MatchHighlightPenaltyContest createMatchHighlightPenaltyContest(int homeTeamId,
                                                                                   MatchScore scoreAfterRegularAndExtraTime,
                                                                                   List<MatchEvent> penaltyContestMatchEvents) {
        AtomicInteger currentHomeScore = new AtomicInteger(scoreAfterRegularAndExtraTime.homeScore());
        AtomicInteger currentGuestScore = new AtomicInteger(scoreAfterRegularAndExtraTime.guestScore());
        var highlights = penaltyContestMatchEvents.stream()
            .map(matchEvent -> new MatchHighlightFromEvent(homeTeamId,
                MatchScore.of(currentHomeScore.get(), currentGuestScore.get()),
                false, false,
                matchEvent))
            .map(matchHighlightFromEvent -> {
                if (matchHighlightFromEvent.isPenaltyContestGoalEvent()) {
                    if (matchHighlightFromEvent.isHomeAction()) {
                        return matchHighlightFromEvent.withHomeGoal(currentHomeScore.incrementAndGet());
                    } else {
                        return matchHighlightFromEvent.withGuestGoal(currentGuestScore.incrementAndGet());
                    }
                } else {
                    return matchHighlightFromEvent;
                }
            })
            .toList();

        if (!penaltyContestMatchEvents.isEmpty()) {
            return new MatchHighlightPenaltyContest(
                MatchScore.of(currentHomeScore.get() - scoreAfterRegularAndExtraTime.homeScore(),
                currentGuestScore.get() - scoreAfterRegularAndExtraTime.guestScore()),
                highlights.stream().map(MatchHighlight.class::cast).toList()
            );
        } else {
            return null;
        }
    }
}
