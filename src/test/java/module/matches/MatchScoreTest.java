package module.matches;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchScoreTest {

    private static final int HOME_SCORE = 2;
    private static final int GUEST_SCORE = 1;

    private static final MatchScore MATCH_SCORE = new MatchScore(HOME_SCORE, GUEST_SCORE);

    @Test
    void of() {
        final var matchScore = MatchScore.of(HOME_SCORE, GUEST_SCORE);
        assertThat(matchScore.homeScore()).isEqualTo(HOME_SCORE);
        assertThat(matchScore.guestScore()).isEqualTo(GUEST_SCORE);
    }

    @Test
    void withHomeScore() {
        final int newHomeScore = 5;
        final var matchScore = MATCH_SCORE.withHomeScore(newHomeScore);
        assertThat(matchScore.homeScore()).isEqualTo(newHomeScore);
        assertThat(matchScore.guestScore()).isEqualTo(MATCH_SCORE.guestScore());
    }

    @Test
    void withGuestScore() {
        final int newGuestScore = 6;
        final var matchScore = MATCH_SCORE.withGuestScore(newGuestScore);
        assertThat(matchScore.guestScore()).isEqualTo(newGuestScore);
        assertThat(matchScore.homeScore()).isEqualTo(MATCH_SCORE.homeScore());
    }
}
