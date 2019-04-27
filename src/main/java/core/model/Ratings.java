package core.model;

import java.util.Hashtable;

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

    private Hashtable<Integer, Double> leftDefense = new Hashtable<>();
    private Hashtable<Integer, Double> centralDefense = new Hashtable<>();
    private Hashtable<Integer, Double> rightDefense = new Hashtable<>();
    private Hashtable<Integer, Double> midfield = new Hashtable<>();
    private Hashtable<Integer, Double> leftAttack = new Hashtable<>();
    private Hashtable<Integer, Double> centralAttack = new Hashtable<>();
    private Hashtable<Integer, Double> rightAttack = new Hashtable<>();
    private Hashtable<Integer, Double> HatStats = new Hashtable<>();
    private Hashtable<Integer, Double> LoddarStat = new Hashtable<>();

    public Ratings() {
    }

}
