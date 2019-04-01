package module.teamAnalyzer.vo;
import core.model.match.IMatchDetails;
import core.model.match.Matchdetails;

import java.util.Map;


/**
 * Class with holds the Ratings of a team in the 7 areas of the field
 */
public class MatchRating {
    //~ Instance fields ----------------------------------------------------------------------------

    private double centralAttack;
    private double centralDefense;
    private double leftAttack;
    private double leftDefense;
    private double midfield;
    private double rightAttack;
    private double rightDefense;
    private int tacticSkill;
    private int tacticType;
    private double HatStats;
    private double LoddarStat;


    public MatchRating() {}
    public MatchRating(Map<String, String> matchRating) {
        this.centralAttack = intHT2loatHT(Integer.parseInt(matchRating.get("RatingMidAtt")));
        this.centralDefense = intHT2loatHT(Integer.parseInt(matchRating.get("RatingMidDef")));
        this.leftAttack = intHT2loatHT(Integer.parseInt(matchRating.get("RatingLeftAtt")));
        this.leftDefense = intHT2loatHT(Integer.parseInt(matchRating.get("RatingLeftDef")));
        this.midfield = intHT2loatHT(Integer.parseInt(matchRating.get("RatingMidfield")));
        this.rightAttack = intHT2loatHT(Integer.parseInt(matchRating.get("RatingRightAtt")));
        this.rightDefense = intHT2loatHT(Integer.parseInt(matchRating.get("RatingRightDef")));
        this.tacticSkill = Integer.parseInt(matchRating.get("TacticSkill"));
        this.tacticType = Integer.parseInt(matchRating.get("TacticType"));
        HatStats = (midfield * 3) + leftAttack + rightAttack + centralAttack + centralDefense + leftDefense + rightDefense;
        LoddarStat = computeLoddarStats(tacticType, tacticSkill);
    }
    public MatchRating(double LD, double CD, double RD, double MF, double LA, double CA, double RA, int tacticType, int tacticSkill) {
        this.centralAttack = CA;
        this.centralDefense = CD;
        this.leftAttack = LA;
        this.leftDefense = LD;
        this.midfield = MF;
        this.rightAttack = RA;
        this.rightDefense = RD;
        this.tacticSkill = tacticSkill;
        this.tacticType = tacticType;
        HatStats = (midfield * 3) + leftAttack + rightAttack + centralAttack + centralDefense + leftDefense + rightDefense;
        LoddarStat = computeLoddarStats(tacticType, tacticSkill);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the central attack rating
     *
     * @param rating The rating
     */
    public final void setCentralAttack(double rating) {
        centralAttack = rating;
    }

    /**
     * Gets the central attack rating
     *
     * @return The rating
     */
    public final double getCentralAttack() {
        return centralAttack;
    }

    /**
     * Sets the central defense rating
     *
     * @param rating The rating
     */
    public final void setCentralDefense(double rating) {
        centralDefense = rating;
    }

    /**
     * Gets the central defense rating
     *
     * @return The rating
     */
    public final double getCentralDefense() {
        return centralDefense;
    }

    /**
     * Returns the calculated <B>HatStats</B><code>(3  midfield) + (sum of defence) + (sum of
     * attack )</code>
     *
     * @return the rating
     */
    public final double getHatStats() {
        return HatStats;
    }

    public final void setHatStats(double _HatStats) {
        HatStats=_HatStats;
    }

    /**
     * Sets the left attack rating
     *
     * @param rating The rating
     */
    public final void setLeftAttack(double rating) {
        leftAttack = rating;
    }

    /**
     * Gets the left attack rating
     *
     * @return The rating
     */
    public final double getLeftAttack() {
        return leftAttack;
    }

    /**
     * Sets the left defense rating
     *
     * @param rating The rating
     */
    public final void setLeftDefense(double rating) {
        leftDefense = rating;
    }

    /**
     * Gets the left defense rating
     *
     * @return The rating
     */
    public final double getLeftDefense() {
        return leftDefense;
    }


    public final void setLoddarStats(double _LoddarStats) {
        LoddarStat = _LoddarStats;
    }

    public final double getLoddarStats() {
        return LoddarStat;
    }

    /**
     * Returns the calculated LoddarStats
     *
     * @param tactic The tactic code
     * @param level The tactic rating
     *
     * @return the rating
     */
    public final double computeLoddarStats(int tactic, int level) {
        final double MIDFIELD_SHIFT = 0.0;
        final double COUNTERATTACK_WEIGHT = 0.25;
        final double DEFENSE_WEIGHT = 0.47;
        final double ATTACK_WEIGHT = 1 - DEFENSE_WEIGHT;
        final double CENTRAL_WEIGHT = 0.37;
        final double WINGER_WEIGTH = (1 - CENTRAL_WEIGHT) / 2d;

        double correctedCentralWeigth = CENTRAL_WEIGHT;

        switch (tactic) {
            case IMatchDetails.TAKTIK_MIDDLE:
                correctedCentralWeigth = CENTRAL_WEIGHT + (((0.2 * (level - 1)) / 19d) + 0.2);
                break;

            case IMatchDetails.TAKTIK_WINGS:
                correctedCentralWeigth = CENTRAL_WEIGHT - (((0.2 * (level - 1)) / 19d) + 0.2);
                break;

            default:
                break;
        }

        final double correctedWingerWeight = (1 - correctedCentralWeigth) / 2d;

        double counterCorrection = 0;

        if (tactic == IMatchDetails.TAKTIK_KONTER) {
            counterCorrection = (COUNTERATTACK_WEIGHT * 2 * level) / (level + 20);
        }

        // Calculate attack rating
        final double attackStrength = (ATTACK_WEIGHT + counterCorrection) * ((correctedCentralWeigth * hq(centralAttack))
                                      + (correctedWingerWeight * (hq(leftAttack) + hq(rightAttack))));

        // Calculate defense rating
        final double defenseStrength = DEFENSE_WEIGHT * ((CENTRAL_WEIGHT * hq(centralDefense))
                                       + (WINGER_WEIGTH * (hq(leftDefense) + hq(rightDefense))));

        // Calculate midfield rating
        final double midfieldFactor = MIDFIELD_SHIFT + ((1 - MIDFIELD_SHIFT) * hq(midfield));

        // Calculate and return the LoddarStats rating
        return 80 * midfieldFactor * (defenseStrength + attackStrength);
    }

    /**
     * Sets the midfield rating
     *
     * @param rating The rating
     */
    public final void setMidfield(double rating) {
        midfield = rating;
    }

    /**
     * Gets the midfield rating
     *
     * @return The rating
     */
    public final double getMidfield() {
        return midfield;
    }

    /**
     * Returns the calculated <B>PStats</B> (aka PeasoStats)
     *
     * @return the rating
     */
    public final double getPStats() {
        final double MIDFIELD = 0.46;
        final double ATTACK = 0.32;
        final double DEFENSE = 0.22;
        final double SIDE = 0.3;
        final double CENTER = 0.4;

        return (rightDefense * DEFENSE * SIDE) + (centralDefense * DEFENSE * CENTER)
               + (leftDefense * DEFENSE * SIDE) + (rightAttack * ATTACK * SIDE)
               + (centralAttack * ATTACK * CENTER) + (leftAttack * ATTACK * SIDE)
               + (midfield * MIDFIELD);
    }

    /**
     * Sets the right attack rating
     *
     * @param rating The rating
     */
    public final void setRightAttack(double rating) {
        rightAttack = rating;
    }

    /**
     * Gets the right attack rating
     *
     * @return The rating
     */
    public final double getRightAttack() {
        return rightAttack;
    }

    /**
     * Sets the right defense rating
     *
     * @param rating The rating
     */
    public final void setRightDefense(double rating) {
        rightDefense = rating;
    }

    /**
     * Gets the right defense rating
     *
     * @return The rating
     */
    public final double getRightDefense() {
        return rightDefense;
    }

    /**
     * Returns the calculated <B>Smart Squad Rating</B> (<code>Squad Rating / stars</code>)
     *
     * @param stars Team star rating
     *
     * @return the rating
     */
    public final double getSmartSquad(double stars) {
        return getSquad() / stars;
    }

    /**
     * Returns the calculated <B>Squad Rating</B><code>(2  midfield) + (sum of defence) + (sum of
     * attack )</code>
     *
     * @return the rating
     */
    public final double getSquad() {
        return (midfield * 2) + leftAttack + rightAttack + centralAttack + centralDefense
               + leftDefense + rightDefense;
    }

    /**
     * Convert reduced float rating (1.00....20.99) to original integer HT rating (1...80) one +0.5
     * is because of correct rounding to integer
     *
     * @param x HO float rating
     *
     * @return Integer HT rating
     */
    public final int float2HTint(float x) {
        return (int) (((x - 1.0f) * 4.0f) + 1.0f);
    }

    public final double intHT2loatHT(int x) {
        return (double) (((x - 1.0f) / 4.0f) + 1.0f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public final String toString() {
        final StringBuffer buffer = new StringBuffer();

        buffer.append("MatchRating[");
        buffer.append("midfield = " + midfield);
        buffer.append(", leftDefense = " + leftDefense);
        buffer.append(", centralDefense = " + centralDefense);
        buffer.append(", rightDefense = " + rightDefense);
        buffer.append(", leftAttack = " + leftAttack);
        buffer.append(", centralAttack = " + centralAttack);
        buffer.append(", rightAttack = " + rightAttack);
        buffer.append("]");

        return buffer.toString();
    }

    /**
     * Hattrick Quality function
     *
     * @param value Official Hattrick value
     *
     * @return Hattrick Quality value
     */
    private double hq(double value) {
        return (2 * value) / (value + 80);
    }
}
