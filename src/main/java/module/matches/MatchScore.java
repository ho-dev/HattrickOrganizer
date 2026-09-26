package module.matches;

public record MatchScore(int homeScore, int guestScore) {

    public static MatchScore of(int homeScore, int guestScore) {
        return new MatchScore(homeScore, guestScore);
    }

    public MatchScore withHomeScore(int newHomeScore) {
        return new MatchScore(newHomeScore, guestScore);
    }

    public MatchScore withGuestScore(int newGuestScore) {
        return new MatchScore(homeScore, newGuestScore);
    }
}
