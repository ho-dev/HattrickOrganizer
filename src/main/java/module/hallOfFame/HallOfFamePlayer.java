package module.hallOfFame;


import core.db.DBManager;
import core.model.player.Player;
import core.util.HODateTime;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

import static core.file.xml.XMLManager.*;

public class HallOfFamePlayer extends Player {

    public static class History {
        HODateTime trainerFrom;
        HODateTime trainerTo;
        HODateTime.HODuration getTrainerDuration (){
            if ( trainerFrom != null && trainerTo != null) return HODateTime.HODuration.between(trainerFrom, trainerTo);
            return null;
        }

        static class Rating {
            HODateTime time;
            int coachLevel;
            int leadership;
        }

        List<Rating> ratings = new ArrayList<>();
    }

    private History history;

    private void loadHistory() {
        var history = DBManager.instance().loadPlayerHistory(this.getPlayerId());
        this.history = new History();
        var trainerTime = history.stream().filter(Player::isCoach).toList();
        for ( var historicalPlayer : trainerTime){
            if (this.history.trainerFrom == null) this.history.trainerFrom = historicalPlayer.getHrfDate();
            this.history.trainerTo = historicalPlayer.getHrfDate();
            var rating = new History.Rating();
            rating.time = historicalPlayer.getHrfDate();
            rating.coachLevel = historicalPlayer.getCoachSkill();
            rating.leadership = historicalPlayer.getLeadership();
            this.history.ratings.add(rating);
        }
    }

    public History getHistory(){
        if ( history == null) loadHistory();
        return history;
    }

    public HODateTime getTrainerFrom(){
        getHistory();
        if ( history.trainerFrom != null) return history.trainerFrom;
        return null;
    }
    public HODateTime getTrainerTo(){
        getHistory();
        if ( history.trainerTo != null) return history.trainerTo;
        return null;
    }
    public HODateTime.HODuration getTrainerDuration(){
        getHistory();
        return history.getTrainerDuration();
    }

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

    public HallOfFamePlayer(){}

    public HallOfFamePlayer(Element root){
        this.setPlayerId(xmlIntValue(root, "PlayerId"));
        this.setFirstName(xmlValue( root, "FirstName"));
        this.setNickName(xmlValue( root, "NickName"));
        this.setLastName(xmlValue( root, "LastName"));
        this.setAge(xmlIntValue( root, "Age"));
        this.setNextBirthday(xmlHODateTimeValue(root, "NextBirthday"));
        this.setCountryId(xmlIntValue(root, "CountryID"));
        this.setArrivalDate(xmlHODateTimeValue( root, "ArrivalDate"));
        this.setExpertTypeId(xmlIntValue(root, "ExpertType"));
        this.setHofDate(xmlHODateTimeValue(root, "HofDate"));
        this.setHofAge(xmlIntValue(root, "HofAge"));
    }

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
