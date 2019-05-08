package core.model;

import core.model.match.IMatchDetails;

import java.util.Hashtable;
import java.util.Map;

public class Ratings {
    public Hashtable<Integer, Double> getLeftDefense() {
        return leftDefense;
    }

    public void setLeftDefense(Hashtable<Integer, Double> leftDefense) {
        this.leftDefense = leftDefense;
    }

    public Hashtable<Integer, Double> getCentralDefense() {
        return centralDefense;
    }

    public void setCentralDefense(Hashtable<Integer, Double> centralDefense) {
        this.centralDefense = centralDefense;
    }

    public Hashtable<Integer, Double> getRightDefense() {
        return rightDefense;
    }

    public void setRightDefense(Hashtable<Integer, Double> rightDefense) {
        this.rightDefense = rightDefense;
    }

    public Hashtable<Integer, Double> getMidfield() {
        return midfield;
    }

    public void setMidfield(Hashtable<Integer, Double> midfield) {
        this.midfield = midfield;
    }

    public Hashtable<Integer, Double> getLeftAttack() {
        return leftAttack;
    }

    public void setLeftAttack(Hashtable<Integer, Double> leftAttack) {
        this.leftAttack = leftAttack;
    }

    public Hashtable<Integer, Double> getCentralAttack() {
        return centralAttack;
    }

    public void setCentralAttack(Hashtable<Integer, Double> centralAttack) {
        this.centralAttack = centralAttack;
    }

    public Hashtable<Integer, Double> getRightAttack() {
        return rightAttack;
    }

    public void setRightAttack(Hashtable<Integer, Double> rightAttack) {
        this.rightAttack = rightAttack;
    }

    public Hashtable<Integer, Integer> getHatStats() {
        return HatStats;
    }

    public Hashtable<Integer, Double> getLoddarStat() {
        return LoddarStat;
    }

    private Hashtable<Integer, Double> leftDefense = new Hashtable<>();
    private Hashtable<Integer, Double> centralDefense = new Hashtable<>();
    private Hashtable<Integer, Double> rightDefense = new Hashtable<>();
    private Hashtable<Integer, Double> midfield = new Hashtable<>();
    private Hashtable<Integer, Double> leftAttack = new Hashtable<>();
    private Hashtable<Integer, Double> centralAttack = new Hashtable<>();
    private Hashtable<Integer, Double> rightAttack = new Hashtable<>();
    private Hashtable<Integer, Integer> HatStats = new Hashtable<>();
    private Hashtable<Integer, Double> LoddarStat = new Hashtable<>();

    private int tacticType;
    private int tacticLevel;

    public void setHatStats() {

        this.HatStats = HatStats;
    }

    public void computeHatStats() {
        Hashtable<Integer, Integer> _HatStats = new Hashtable<>();
        int t, iHatStats;
        Double dMD, dRD, dLD, dCD, dLA, dRA, dCA;

        for (Map.Entry<Integer,Double> tMid : midfield.entrySet()) {
            t = tMid.getKey();
            dMD = tMid.getValue();
            dRD = rightDefense.get(t);
            dLD = leftDefense.get(t);
            dCD = centralDefense.get(t);
            dLA = leftAttack.get(t);
            dRA = rightAttack.get(t);
            dCA = centralAttack.get(t);

            iHatStats = HTfloat2int(dMD) * 3;
            iHatStats += HTfloat2int(dLD);
            iHatStats += HTfloat2int(dCD);
            iHatStats += HTfloat2int(dRD);
            iHatStats += HTfloat2int(dLA);
            iHatStats += HTfloat2int(dCA);
            iHatStats += HTfloat2int(dRA);

            _HatStats.put(t, iHatStats);
        }

        this.HatStats = _HatStats;

    }

    public void computeLoddarStats() {
        Hashtable<Integer, Double> _LoddarStat = new Hashtable<>();
        final double MIDFIELD_SHIFT = 0.0;
        final double COUNTERATTACK_WEIGHT = 0.25;
        final double DEFENSE_WEIGHT = 0.47;
        final double ATTACK_WEIGHT = 1 - DEFENSE_WEIGHT;
        final double CENTRAL_WEIGHT = 0.37;
        final double WINGER_WEIGTH = (1 - CENTRAL_WEIGHT) / 2d;
        double correctedCentralWeigth = CENTRAL_WEIGHT;
        double counterCorrection = 0;

        switch (this.tacticType) {
            case IMatchDetails.TAKTIK_MIDDLE:
                correctedCentralWeigth = CENTRAL_WEIGHT + (((0.2 * (this.tacticLevel - 1)) / 19d) + 0.2);
                break;

            case IMatchDetails.TAKTIK_WINGS:
                correctedCentralWeigth = CENTRAL_WEIGHT - (((0.2 * (this.tacticLevel - 1)) / 19d) + 0.2);
                break;
            default:
                break;
        }

        final double correctedWingerWeight = (1 - correctedCentralWeigth) / 2d;


        if (this.tacticType == IMatchDetails.TAKTIK_KONTER) {
            counterCorrection = (COUNTERATTACK_WEIGHT * 2 * this.tacticLevel) / (this.tacticLevel + 20);
        }

        int t;
        Double dMD, dRD, dLD, dCD, dLA, dRA, dCA, dLoddar;

        for (Map.Entry<Integer,Double> tMid : midfield.entrySet()) {
            t = tMid.getKey();
            dMD = tMid.getValue();
            dRD = rightDefense.get(t);
            dLD = leftDefense.get(t);
            dCD = centralDefense.get(t);
            dLA = leftAttack.get(t);
            dRA = rightAttack.get(t);
            dCA = centralAttack.get(t);

            // Calculate attack rating
            final double attackStrength = (ATTACK_WEIGHT + counterCorrection) * ((correctedCentralWeigth * hq(dCA))
                    + (correctedWingerWeight * (hq(dLA) + hq(dRA))));

            // Calculate defense rating
            final double defenseStrength = DEFENSE_WEIGHT * ((CENTRAL_WEIGHT * hq(dCD))
                    + (WINGER_WEIGTH * (hq(dLD) + hq(dRD))));

            // Calculate midfield rating
            final double midfieldFactor = MIDFIELD_SHIFT + ((1 - MIDFIELD_SHIFT) * hq(dMD));

            // Calculate and return the LoddarStats rating
            dLoddar =  80 * midfieldFactor * (defenseStrength + attackStrength);

            _LoddarStat.put(t, dLoddar);
        }

        this.LoddarStat = _LoddarStat;

    }

    private double hq(double value) {
        // Convert reduced float rating (1.00....20.99) to original integer HT rating (1...80) one +0.5 is because of correct rounding to integer
        int x = (int)(((value - 1.0f) * 4.0f) + 1.0f);
        return (2.0 * x) / (x + 80.0);
    }

    /**
     * convert reduced float rating (1.00....20.99) to original integer HT
     * rating (1...80) one +0.5 is because of correct rounding to integer
     */
    public static final int HTfloat2int(double x) {
        return (int) (((x - 1.0f) * 4.0f) + 1.0f);
    }


    public Ratings() {
    }

}
