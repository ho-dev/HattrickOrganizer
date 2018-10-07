// %3800124443:de.hattrickorganizer.gui.transferscout%
package module.transfer.scout;

/**
 * Player used for PlayerConverter (TransferScout)
 *
 * @author Marco Senn
 */
public class Player {
    //~ Instance fields ----------------------------------------------------------------------------

    private String expiryDate;
    private String expiryTime;
    private String playerName;
    private int age;
    private int ageDays;
    private int attack;
    private int defense;
    private int experience;
    private int form;
    private int goalKeeping;
    private int injury;
    private int leadership;
    private int passing;
    private int playMaking;
    private int playerId;
    private int price;
    private int setPieces;
    private int speciality;
    private int stamina;
    private int tsi;
    private int wing;
    private int loyalty;
    private boolean homegrown;
    private String booked = null;
    private int agreeability;
    private int baseWage;
    private int nationality;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Player object.
     */
    public Player() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for age
     *
     * @param i Set age to i
     */
    public final void setAge(int i) {
        age = i;
    }

    /**
     * Getter for age
     *
     * @return Returns age
     */
    public final int getAge() {
        return age;
    }

    /**
     * Setter for ageDays
     *
     * @param i Set ageDays to i
     */
    public final void setAgeDays(int i) {
        ageDays = i;
    }

    /**
     * Getter for ageDays
     *
     * @return Returns ageDays
     */
    public final int getAgeDays() {
        return ageDays;
    }

    /**
     * Setter for attack
     *
     * @param i Set attack to i
     */
    public final void setAttack(int i) {
        attack = i;
    }

    /**
     * Getter for attack
     *
     * @return Returns attack
     */
    public final int getAttack() {
        return attack;
    }

    /**
     * Setter for defense
     *
     * @param i Set defense to i
     */
    public final void setDefense(int i) {
        defense = i;
    }

    /**
     * Getter for defense
     *
     * @return Returns defense
     */
    public final int getDefense() {
        return defense;
    }

    /**
     * Setter for experience
     *
     * @param i Set experience to i
     */
    public final void setExperience(int i) {
        experience = i;
    }

    /**
     * Getter for experience
     *
     * @return Returns experience
     */
    public final int getExperience() {
        return experience;
    }

    /**
     * Setter for expiryDate
     *
     * @param string Set expiryDate to string
     */
    public final void setExpiryDate(String string) {
        expiryDate = string;
    }

    /**
     * Getter for expiryDate
     *
     * @return Returns expiryDate
     */
    public final String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Setter for expiryTime
     *
     * @param string Set expiryTime to string
     */
    public final void setExpiryTime(String string) {
        expiryTime = string;
    }

    /**
     * Getter for expiryTime
     *
     * @return Returns expiryTime
     */
    public final String getExpiryTime() {
        return expiryTime;
    }

    /**
     * Setter for form
     *
     * @param i Set form to i
     */
    public final void setForm(int i) {
        form = i;
    }

    /**
     * Getter for form
     *
     * @return Returns form
     */
    public final int getForm() {
        return form;
    }

    /**
     * Setter for goalKeeping
     *
     * @param i Set goalKeeping to i
     */
    public final void setGoalKeeping(int i) {
        goalKeeping = i;
    }

    /**
     * Getter for goalKeeping
     *
     * @return Returns goalKeeping
     */
    public final int getGoalKeeping() {
        return goalKeeping;
    }

    /**
     * Get some informations about player
     *
     * @return Returns text with player informations
     */
    public final String getInfo() {
        String info = "";

        if (getInjury() > 0) {
            final String tmp = core.model.HOVerwaltung.instance().getLanguageString("scout_injury");
            info = info + tmp.replaceAll("%weeks%", String.valueOf(getInjury())) + "\r\n";
        }

        // add warnings info text (yellow / red card)
        if (getBooked() != null && getBooked().length()>0) {
            info += getBooked() + "\r\n";
        }

        return info;
    }

    /**
     * Setter for injury
     *
     * @param i Set injury to i
     */
    public final void setInjury(int i) {
        injury = i;
    }

    /**
     * Getter for injury
     *
     * @return Returns injury
     */
    public final int getInjury() {
        return injury;
    }

    /**
     * Setter for leadership
     *
     * @param i Set leadership to i
     */
    public final void setLeadership(int i) {
        leadership = i;
    }

    /**
     * Getter for leadership
     *
     * @return Returns leadership
     */
    public final int getLeadership() {
        return leadership;
    }

    /**
     * Setter for passing
     *
     * @param i Set passing to i
     */
    public final void setPassing(int i) {
        passing = i;
    }

    /**
     * Getter for passing
     *
     * @return Returns passing
     */
    public final int getPassing() {
        return passing;
    }

    /**
     * Setter for playmaking
     *
     * @param i Set playmaking to i
     */
    public final void setPlayMaking(int i) {
        playMaking = i;
    }

    /**
     * Getter for playmaking
     *
     * @return Returns playmaking
     */
    public final int getPlayMaking() {
        return playMaking;
    }

    /**
     * Setter for playerId
     *
     * @param i Set playerId to i
     */
    public final void setPlayerID(int i) {
        playerId = i;
    }

    /**
     * Getter for playerId
     *
     * @return Returns playerId
     */
    public final int getPlayerID() {
        return playerId;
    }

    /**
     * Setter for playerName
     *
     * @param string Set playerName to string
     */
    public final void setPlayerName(String string) {
        playerName = string;
    }

    /**
     * Getter for playerName
     *
     * @return Returns playerName
     */
    public final String getPlayerName() {
        return playerName;
    }

    /**
     * Setter for price
     *
     * @param i Set price to i
     */
    public final void setPrice(int i) {
        price = i;
    }

    /**
     * Getter for price
     *
     * @return Returns price
     */
    public final int getPrice() {
        return price;
    }

    /**
     * Setter for setPieces
     *
     * @param i Set setPieces to i
     */
    public final void setSetPieces(int i) {
        setPieces = i;
    }

    /**
     * Getter for setPieces
     *
     * @return Returns setPieces
     */
    public final int getSetPieces() {
        return setPieces;
    }

    /**
     * Setter for speciality
     *
     * @param i Set speciality to i
     */
    public final void setSpeciality(int i) {
        speciality = i;
    }

    /**
     * Getter for speciality
     *
     * @return Returns speciality
     */
    public final int getSpeciality() {
        return speciality;
    }

    /**
     * Setter for stamina
     *
     * @param i Set stamina to i
     */
    public final void setStamina(int i) {
        stamina = i;
    }

    /**
     * Getter for stamina
     *
     * @return Returns stamina
     */
    public final int getStamina() {
        return stamina;
    }

    /**
     * Setter for tsi
     *
     * @param i Set tsi to i
     */
    public final void setTSI(int i) {
        tsi = i;
    }

    /**
     * Getter for tsi
     *
     * @return Returns tsi
     */
    public final int getTSI() {
        return tsi;
    }

    /**
     * Setter for wing
     *
     * @param i Set wing to i
     */
    public final void setWing(int i) {
        wing = i;
    }

    /**
     * Getter for wing
     *
     * @return Returns wing
     */
    public final int getWing() {
        return wing;
    }
    
    /**
     * Setter for agreeability
     *
     * @param i Set agreeability to i
     */
    public final void setAgreeability(int i) {
        agreeability = i;
    }

    /**
     * Getter for agreeability
     *
     * @return Returns agreeability
     */
    public final int getAgreeability() {
        return agreeability;
    }
    
    /**
     * Setter for baseWage
     *
     * @param i Set baseWage to i
     */
    public final void setBaseWage(int i) {
    	baseWage = i;
    }

    /**
     * Getter for baseWage
     *
     * @return Returns baseWage
     */
    public final int getBaseWage() {
        return baseWage;
    }
    
    /**
     * Setter for nationality
     *
     * @param i Set nationality to i
     */
    public final void setNationality(int i) {
    	nationality = i;
    }

    /**
     * Getter for nationality
     *
     * @return Returns nationality
     */
    public final int getNationality() {
        return nationality;
    }

    /**
     * Setter for the booked infos.
     */
    public final void setBooked(String booked) {
    	this.booked = booked;
    }

    /**
     * Getter for the booked infos.
     */
    public final String getBooked() {
    	return this.booked;
    }

    /**
     * Setter for loyalty
     *
     * @param i Set loyalty to i
     */
    public final void setLoyalty(int i) {
        loyalty = i;
    }

    /**
     * Getter for loyalty
     *
     * @return Returns loyalty
     */
    public final int getLoyalty() {
        return loyalty;
    }
    
    /**
     * Setter for homegrown
     *
     * @param b Set homegrown to b
     */
    public final void setHomeGrown(boolean b) {
    	homegrown = b;
    }

    /**
     * Getter for homegrown
     *
     * @return Returns homegrown
     */
    public final boolean isHomwGrown() {
        return homegrown;
    }
    
    /**
     * Creates a string representation of the object
     *
     * @return Returns a string representation of the object
     */
    @Override
	public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("playerName = " + playerName);
        buffer.append(", price = " + price);
        buffer.append(", playerId = " + playerId);
        buffer.append(", speciality = " + speciality);
        buffer.append(", TSI = " + tsi);
        buffer.append(", Age = " + age +"."+ageDays);
        buffer.append(", expiryDate = " + expiryDate + " " + expiryTime);
        buffer.append(", experience = " + experience);
        buffer.append(", form = " + form);
        buffer.append(", stamina = " + stamina);
        buffer.append(", goalKeeping = " + goalKeeping);
        buffer.append(", playMaking = " + playMaking);
        buffer.append(", passing = " + passing);
        buffer.append(", wing = " + wing);
        buffer.append(", defense = " + defense);
        buffer.append(", attack = " + attack);
        buffer.append(", setPieces = " + setPieces);
        buffer.append(", loyalty = " + loyalty);
        buffer.append(", homegrown = " + homegrown);
        buffer.append(", agreeability = " + agreeability);
        buffer.append(", baseWage = " + baseWage);
        buffer.append(", nationality = " + nationality);
        buffer.append(", info = " + getInfo());
        return buffer.toString();
    }
}
