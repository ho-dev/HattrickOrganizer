package module.teamAnalyzer.vo;

import core.db.AbstractTable;
import core.util.HODateTime;

public class SquadInfo extends AbstractTable.Storable {
    private int teamId;

    // Last match loaded before squad info were loaded
    private HODateTime lastMatchDate;

    private HODateTime fetchDate;
    private int bruisedCount = 0;
    private int injuredCount = 0;
    private int injuredWeeksSum = 0;
    private int singleYellowCards = 0;
    private int twoYellowCards = 0;
    private int redCards = 0;
    private int transferListedCount = 0;
    private int tSISum=0;
    private int salarySum=0; // Money in SEK
    private int playerCount=0;
    private int homegrownCount =0;

    public SquadInfo(int teamId, HODateTime lastMatchDate) {
        this.teamId=teamId;
        this.lastMatchDate = lastMatchDate;
        this.fetchDate = HODateTime.now();
    }

    /**
     * create squad info
     * ATTENTION: this is used by SquadInfoTable (do not delete it)
     */
    public SquadInfo(){}

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getBruisedCount() {
        return bruisedCount;
    }

    public void incrementBruisedCount(){
        bruisedCount++;
    }

    public void setBruisedCount(int bruisedCount) {
        this.bruisedCount = bruisedCount;
    }
    public int getInjuredCount() {
        return injuredCount;
    }

    public void incrementInjuredCount(){
        injuredCount++;
    }

    public void setInjuredCount(int count) {
        this.injuredCount = count;
    }

    public int getInjuredWeeksSum() {
        return injuredWeeksSum;
    }

    public void addInjuredWeeksSum(int v){
        injuredWeeksSum+=v;
    }

    public void setInjuredWeeksSum(int injuredWeeksCount) {
        this.injuredWeeksSum = injuredWeeksCount;
    }

    public int getSingleYellowCards() {
        return singleYellowCards;
    }

    public void incrementSingleYellowCards(){
        singleYellowCards++;
    }

    public void setSingleYellowCards(int singleYellowCards) {
        this.singleYellowCards = singleYellowCards;
    }

    public int getTwoYellowCards() {
        return twoYellowCards;
    }

    public void incrementTwoYellowCards(){
        twoYellowCards++;
    }

    public void setTwoYellowCards(int twoYellowCards) {
        this.twoYellowCards = twoYellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    public void incrementSuspended(){
        redCards++;
    }

    public int getTransferListedCount() {
        return transferListedCount;
    }

    public void incrementTransferListedCount(){
        transferListedCount++;
    }

    public void setTransferListedCount(int transferListedCount) {
        this.transferListedCount = transferListedCount;
    }

    public int gettSISum() {
        return tSISum;
    }

    public void addTsi(int v){
        tSISum+=v;
    }

    public void settSISum(int tSISum) {
        this.tSISum = tSISum;
    }

    public int getSalarySum() {
        return salarySum;
    }

    public void addSalary(int v){
        salarySum+= v;
    }

    public void setSalarySum(int salarySum) {
        this.salarySum = salarySum;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void incrementPlayerCount(){
        playerCount++;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getHomegrownCount() {
        return homegrownCount;
    }

    public void incrementHomegrownCount(){
        homegrownCount++;
    }

    public void setHomegrownCount(int homegrownCount) {
        this.homegrownCount = homegrownCount;
    }

    public HODateTime getFetchDate() {
        return fetchDate;
    }

    public void setFetchDate(HODateTime fetchDate) {
        this.fetchDate = fetchDate;
    }

    public HODateTime getLastMatchDate() {
        return lastMatchDate;
    }

    public void setLastMatchDate(HODateTime lastMatchDate) {
        this.lastMatchDate = lastMatchDate;
    }
}
