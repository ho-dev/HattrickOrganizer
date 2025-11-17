package module.hallOfFame;

import core.model.player.Player;
import core.util.HODateTime;

public class HallOfFamePlayer extends Player {

    /**
     NextBirthday : DateTime
     The approximate Date/time of next birthday.
     */
    private HODateTime nextBirthday;
    public HODateTime getNextBirthday() {
        return nextBirthday;
    }

    /**
     *  ExpertType : unsigned Integer
     * An identifier to show which type of job the player have now.
     * # See table HoFExpertType
     */
    private int expertTypeId;

    /**
     * HofDate : DateTime
     *  The date the player was made hall of fame.
     */
    private HODateTime hofDate;

    /**
     * HofAge : unsigned Integer
     * The age of the player in years when he was made hall of fame.
     */
    private int hofAge;

    public void setNextBirthday(HODateTime nextBirthday) {
        this.nextBirthday = nextBirthday;
    }

    public HODateTime getHofDate() {
        return hofDate;
    }

    public void setHofDate(HODateTime hofDate) {
        this.hofDate = hofDate;
    }

    public int getExpertTypeId() {
        return expertTypeId;
    }

    public void setExpertTypeId(int expertTypeId) {
        this.expertTypeId = expertTypeId;
    }

    public int getHofAge() {
        return hofAge;
    }

    public void setHofAge(int hofAge) {
        this.hofAge = hofAge;
    }

    public String getExpertType() {
        var type =  ExpertType.fromInteger(this.expertTypeId);
        if ( type != null) return type.getLanguageString();
        return null;
    }
}
