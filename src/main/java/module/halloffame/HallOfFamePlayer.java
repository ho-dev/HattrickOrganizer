package module.halloffame;

import core.db.DBManager;
import core.model.player.Player;
import core.util.HODateTime;
import org.w3c.dom.Element;
import java.util.List;

import static core.file.xml.XMLManager.*;

public class HallOfFamePlayer extends Player {

    HODateTime trainerFrom;
    HODateTime trainerTo;
    List<Player> history = null;

    /**
     * Load historical data of the hall of fame player
     */
    private void loadHistory() {
        this.history = DBManager.instance().loadPlayerHistory(this.getPlayerId());
        var trainerTime = history.stream().filter(Player::isCoach).toList();
        for (var historicalPlayer : trainerTime) {
            if (this.trainerFrom == null) this.trainerFrom = historicalPlayer.getHrfDate();
            this.trainerTo = historicalPlayer.getHrfDate();
            this.setCurrentTeamGoals(historicalPlayer.getCurrentTeamGoals());
            this.setCurrentTeamMatches(historicalPlayer.getCurrentTeamMatches());
            this.setCareerAssists(historicalPlayer.getCareerAssists());
            this.setAssistsCurrentTeam(historicalPlayer.getAssistsCurrentTeam());
        }
    }

    /**
     * Get historical data of the hall of fame player
     * @return List of Player objects
     */
    public List<Player> getHistory() {
        if (history == null) loadHistory();
        return history;
    }

    /**
     * Get beginning date of trainer career
     * @return HODatetime, null if player never was trainer
     */
    public HODateTime getTrainerFrom() {
        getHistory();
        if (trainerFrom != null) return trainerFrom;
        return null;
    }

    /**
     * Get ending date of trainer career
     * @return HODatetime, null if player never was trainer
     */
    public HODateTime getTrainerTo() {
        getHistory();
        if (trainerTo != null) return trainerTo;
        return null;
    }

    /**
     * Get durcation of trainer career
     * @return HODuration, null if player was trainer
     */
    public HODateTime.HODuration getTrainerDuration() {
        getHistory();
        if (trainerTo != null && trainerFrom != null) return HODateTime.HODuration.between(trainerFrom, trainerTo);
        return null;
    }

    /**
     * NextBirthday : DateTime
     * The approximate Date/time of next birthday.
     */
    private HODateTime nextBirthday;

    public HODateTime getNextBirthday() {
        return nextBirthday;
    }

    /**
     * ExpertType : unsigned Integer
     * An identifier to show which type of job the player have now.
     * # See table HoFExpertType
     */
    private int expertTypeId;

    /**
     * HofDate : DateTime
     * The date the player was made hall of fame.
     */
    private HODateTime hofDate;

    /**
     * HofAge : unsigned Integer
     * The age of the player in years when he was made hall of fame.
     */
    private int hofAge;

    public HallOfFamePlayer() {
    }

    /**
     * Create hall of fame player from xml element
     * @param root xml element
     */
    public HallOfFamePlayer(Element root) {
        this.setPlayerId(xmlIntValue(root, "PlayerId"));
        this.setFirstName(xmlValue(root, "FirstName"));
        this.setNickName(xmlValue(root, "NickName"));
        this.setLastName(xmlValue(root, "LastName"));
        this.setAge(xmlIntValue(root, "Age"));
        this.setNextBirthday(xmlHODateTimeValue(root, "NextBirthday"));
        this.setCountryId(xmlIntValue(root, "CountryID"));
        this.setArrivalDate(xmlHODateTimeValue(root, "ArrivalDate"));
        this.setExpertTypeId(xmlIntValue(root, "ExpertType"));
        this.setHofDate(xmlHODateTimeValue(root, "HofDate"));
        this.setHofAge(xmlIntValue(root, "HofAge"));
    }

    /**
     * Set next birthday
     * @param nextBirthday HODatetime
     */
    public void setNextBirthday(HODateTime nextBirthday) {
        this.nextBirthday = nextBirthday;
    }

    /**
     * Get date when player came to hall of fame
     * @return HODatetime
     */
    public HODateTime getHofDate() {
        return hofDate;
    }

    /**
     * Set date when player came to hall of fame
     * @param hofDate HODatetime
     */
    public void setHofDate(HODateTime hofDate) {
        this.hofDate = hofDate;
    }

    /**
     * Get expert type
     * @return int
     */
    public int getExpertTypeId() {
        return expertTypeId;
    }

    /**
     * Set expert type
     * @param expertTypeId int
     */
    public void setExpertTypeId(int expertTypeId) {
        this.expertTypeId = expertTypeId;
    }

    /**
     * Get player age
     * The hall of fame file only counts years (no days)
     * @return int
     */
    public int getHofAge() {
        return hofAge;
    }

    /**
     * Set player age
     * @param hofAge int
     */
    public void setHofAge(int hofAge) {
        this.hofAge = hofAge;
    }

    /**
     * Get expert type as translated string
     * @return String
     */
    public String getExpertType() {
        var type = ExpertType.fromInteger(this.expertTypeId);
        if (type != null) return type.getLanguageString();
        return null;
    }
}
