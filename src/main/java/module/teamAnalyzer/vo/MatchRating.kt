package module.teamAnalyzer.vo;

import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.util.UTF8Control;
import java.util.ResourceBundle;

import static core.model.match.IMatchDetails.TAKTIK_NORMAL;

/**
 * Class which holds the Ratings of a team in the 7 areas of the field
 */
public class MatchRating {


    public static int ERROR = -9999;


    //~ Instance fields ----------------------------------------------------------------------------

    private double centralAttack;
    private double centralDefense;
    private double leftAttack;
    private double leftDefense;
    private double midfield;
    private double rightAttack;
    private double rightDefense;

    private int tacticType = TAKTIK_NORMAL;
    private double HatStats;
    private double LoddarStat;
    private int attitude;
    private double indirectSetPiecesAtt;
    private double indirectSetPiecesDef;

    public int getAttitude() {
        return attitude;
    }

    public void setAttitude(String attitude) {
        this.attitude = AttitudeStringToInt(attitude);
    }

    public static int AttitudeStringToInt(String attitude) {
        attitude = attitude.toLowerCase();

        ResourceBundle englishBundle = ResourceBundle.getBundle("sprache.English", new UTF8Control());
        HOVerwaltung hoi = HOVerwaltung.instance();

        String english_attitudeType = englishBundle.getString("ls.team.teamattitude.normal").toLowerCase();
        String local_attitudeType = hoi.getLanguageString("ls.team.teamattitude.normal").toLowerCase();
        if ((attitude.equals(english_attitudeType)) || (attitude.equals(local_attitudeType))) {
            return 0;
        }

        english_attitudeType = englishBundle.getString("ls.team.teamattitude.playitcool").toLowerCase();
        local_attitudeType = hoi.getLanguageString("ls.team.teamattitude.playitcool").toLowerCase();
        if ((attitude.equals(english_attitudeType)) || (attitude.equals(local_attitudeType))) {
            return -1;
        }

        english_attitudeType = englishBundle.getString("ls.team.teamattitude.matchoftheseason").toLowerCase();
        local_attitudeType = hoi.getLanguageString("ls.team.teamattitude.matchoftheseason").toLowerCase();
        if ((attitude.equals(english_attitudeType)) || (attitude.equals(local_attitudeType))) {
            return 1;
        }

        return ERROR;
    }


    public int getTacticType() {
        return tacticType;
    }

    public void setTacticType(String tacticType) {
        this.tacticType = TacticTypeStringToInt(tacticType);
    }

    public static int TacticTypeStringToInt(String tacticType) {
        tacticType = tacticType.toLowerCase();

        ResourceBundle englishBundle = ResourceBundle.getBundle("sprache.English", new UTF8Control());
        HOVerwaltung hoi = HOVerwaltung.instance();

        String english_tactictype = englishBundle.getString("ls.team.tactic.normal").toLowerCase();
        String local_tactictype = hoi.getLanguageString("ls.team.tactic.normal").toLowerCase();
        if ((tacticType.equals(english_tactictype)) || (tacticType.equals(local_tactictype))) {return 0;}

        english_tactictype = englishBundle.getString("ls.team.tactic.pressing").toLowerCase();
        local_tactictype = hoi.getLanguageString("ls.team.tactic.pressing").toLowerCase();
        if ((tacticType.equals(english_tactictype)) || (tacticType.equals(local_tactictype))) {return 1;}

        english_tactictype = englishBundle.getString("ls.team.tactic.counter-attacks").toLowerCase();
        local_tactictype = hoi.getLanguageString("ls.team.tactic.counter-attacks").toLowerCase();
        if ((tacticType.equals(english_tactictype)) || (tacticType.equals(local_tactictype))) {return 2;}

        english_tactictype = englishBundle.getString("ls.team.tactic.attackinthemiddle").toLowerCase();
        local_tactictype = hoi.getLanguageString("ls.team.tactic.attackinthemiddle").toLowerCase();
        if ((tacticType.equals(english_tactictype)) || (tacticType.equals(local_tactictype))) {return 3;}

        english_tactictype = englishBundle.getString("ls.team.tactic.attackonwings").toLowerCase();
        local_tactictype = hoi.getLanguageString("ls.team.tactic.attackonwings").toLowerCase();
        if ((tacticType.equals(english_tactictype)) || (tacticType.equals(local_tactictype))) {return 4;}

        english_tactictype = englishBundle.getString("ls.team.tactic.playcreatively").toLowerCase();
        local_tactictype = hoi.getLanguageString("ls.team.tactic.playcreatively").toLowerCase();
        if ((tacticType.equals(english_tactictype)) || (tacticType.equals(local_tactictype))) {return 7;}

        english_tactictype = englishBundle.getString("ls.team.tactic.longshots").toLowerCase();
        local_tactictype = hoi.getLanguageString("ls.team.tactic.longshots").toLowerCase();
        if ((tacticType.equals(english_tactictype)) || (tacticType.equals(local_tactictype))) {return 8;}

        else return ERROR;
    }


    public MatchRating() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    public MatchRating minus(MatchRating other) {
        if (null == other) return this;

        MatchRating diff = new MatchRating();
        diff.setCentralDefense(this.getCentralDefense() - other.getCentralDefense());
        diff.setRightDefense(this.getRightDefense() - other.getRightDefense());
        diff.setLeftDefense(this.getLeftDefense() - other.getLeftDefense());
        diff.setMidfield(this.getMidfield() - other.getMidfield());
        diff.setCentralAttack(this.getCentralAttack() - other.getCentralAttack());
        diff.setRightAttack(this.getRightAttack() - other.getRightAttack());
        diff.setLeftAttack(this.getLeftAttack() - other.getLeftAttack());
        diff.setHatStats(this.getHatStats() - other.getHatStats());
        diff.setLoddarStats(this.getLoddarStats() - other.getLoddarStats());

        return diff;
    }

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
        HatStats = _HatStats;
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


    public final int computeHatStats() {
        int mid = (int) this.midfield;
        int def = (int) (this.leftDefense + this.centralDefense + this.rightDefense);
        int att = (int) (this.leftAttack + this.centralAttack + this.rightAttack);
        return 3 * mid + def + att;
    }


    public final double computeLoddarStats() {
        final double MIDFIELD_SHIFT = 0.0;
        final double COUNTERATTACK_WEIGHT = 0.25;
        final double DEFENSE_WEIGHT = 0.47;
        final double ATTACK_WEIGHT = 1 - DEFENSE_WEIGHT;
        final double CENTRAL_WEIGHT = 0.37;
        final double WINGER_WEIGTH = (1 - CENTRAL_WEIGHT) / 2d;

        double correctedCentralWeigth = CENTRAL_WEIGHT;

        int tacticSkill = 0;
        switch (this.tacticType) {
            case IMatchDetails.TAKTIK_MIDDLE ->
                    correctedCentralWeigth = CENTRAL_WEIGHT + (((0.2 * (tacticSkill - 1)) / 19d) + 0.2);
            case IMatchDetails.TAKTIK_WINGS ->
                    correctedCentralWeigth = CENTRAL_WEIGHT - (((0.2 * (tacticSkill - 1)) / 19d) + 0.2);
            default -> {
            }
        }

        final double correctedWingerWeight = (1 - correctedCentralWeigth) / 2d;

        double counterCorrection = 0;

        if (this.tacticType == IMatchDetails.TAKTIK_KONTER) {
            counterCorrection = (COUNTERATTACK_WEIGHT * 2 * tacticSkill) / (tacticSkill + 20);
        }

        // Calculate attack rating
        final double attackStrength = (ATTACK_WEIGHT + counterCorrection) * ((correctedCentralWeigth * hq(centralAttack))
                + (correctedWingerWeight * (hq(leftAttack) + hq(rightAttack))));

        // Calculate defense rating
        final double defenseStrength = DEFENSE_WEIGHT * ((CENTRAL_WEIGHT * hq(centralDefense))
                + (WINGER_WEIGTH * (hq(leftDefense) + hq(rightDefense))));

        // Calculate midfield rating
        final double midfieldFactor = MIDFIELD_SHIFT + (hq(midfield));

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
     * Returns the calculated <B>Squad Rating</B><code>(2  midfield) + (sum of defence) + (sum of
     * attack )</code>
     *
     * @return the rating
     */
    public final double getSquad() {
        return (midfield * 2) + leftAttack + rightAttack + centralAttack + centralDefense
                + leftDefense + rightDefense;
    }

    public final double intHT2floatHT(int x) {
        return ((x - 1.0f) / 4.0f) + 1.0f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {

        return "MatchRating[" +
                "midfield = " + midfield +
                ", leftDefense = " + leftDefense +
                ", centralDefense = " + centralDefense +
                ", rightDefense = " + rightDefense +
                ", leftAttack = " + leftAttack +
                ", centralAttack = " + centralAttack +
                ", rightAttack = " + rightAttack +
                "]";
    }

    /**
     * Hattrick Quality function
     */
    private double hq(double _value) {
        return (2.0f * _value) / (_value + 80.0f);
    }

    public double getIndirectSetPiecesAtt() {
        return indirectSetPiecesAtt;
    }

    public void setIndirectSetPiecesAtt(int indirectSetPiecesAtt) {
        this.indirectSetPiecesAtt = intHT2floatHT(indirectSetPiecesAtt);
    }

    public double getIndirectSetPiecesDef() {
        return indirectSetPiecesDef;
    }

    public void setIndirectSetPiecesDef(int indirectSetPiecesDef) {
        this.indirectSetPiecesDef = intHT2floatHT(indirectSetPiecesDef);
    }
}