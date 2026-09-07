package core.model.match;


import core.util.HOLogger;

public class StyleOfPlay {

    private static final int NEUTRAL_STYLE_OF_PLAY = 0;
    private static final int MIN_DEFENSIVE_STYLE_OF_PLAY = -10;
    private static final int MAX_OFFENSIVE_STYLE_OF_PLAY = 10;
    private static final int OLD_MATCHES_STYLE_OF_PLAY = -1000;

    public static final StyleOfPlay NEUTRAL = new StyleOfPlay(NEUTRAL_STYLE_OF_PLAY);
    public static final StyleOfPlay DEFENSIVE = new StyleOfPlay(MIN_DEFENSIVE_STYLE_OF_PLAY);
    public static final StyleOfPlay OFFENSIVE = new StyleOfPlay(MAX_OFFENSIVE_STYLE_OF_PLAY);

    /**
     * Possible values for style of play
     * null unknown (not downloaded yet)
     * -10	100% defensive
     * -9	90% defensive
     * -8	80% defensive
     * -7	70% defensive
     * -6	60% defensive
     * -5	50% defensive
     * -4	40% defensive
     * -3	30% defensive
     * -2	20% defensive
     * -1	10% defensive
     * 0	Neutral
     * 1	10% offensive
     * 2	20% offensive
     * 3	30% offensive
     * 4	40% offensive
     * 5	50% offensive
     * 6	60% offensive
     * 7	70% offensive
     * 8	80% offensive
     * 9	90% offensive
     * 10	100% offensive
     */
    private final Integer value;

    private StyleOfPlay(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static StyleOfPlay fromInt(Integer value) {
        if (value == null || value >= MIN_DEFENSIVE_STYLE_OF_PLAY && value <= MAX_OFFENSIVE_STYLE_OF_PLAY) {
            return new StyleOfPlay(value);
        }
        if (value == OLD_MATCHES_STYLE_OF_PLAY) { // old matches has -1000
            return NEUTRAL;
        }
        HOLogger.instance().warning(StyleOfPlay.class, "Unknown style of play: " + value);
        return new StyleOfPlay(null);
    }

    public static Integer toInt(StyleOfPlay in) {
        if (in != null) {
            return in.value;
        }
        return null;
    }

    public boolean isNotAvailable() {
        return value == null;
    }

    public boolean isNeutral() {
        return value == 0;
    }

    public boolean isOffensive() {
        return value > 0;
    }
}
