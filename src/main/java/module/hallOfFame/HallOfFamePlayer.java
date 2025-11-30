package module.hallOfFame;


import core.db.DBManager;
import core.model.player.Player;
import core.util.HODateTime;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

import static core.file.xml.XMLManager.*;

public class HallOfFamePlayer extends Player {

    public static class ExTrainer {
        HODateTime from;
        HODateTime to;
        HODateTime.HODuration getTrainerDuration (){
            if ( from != null && to != null) return HODateTime.HODuration.between(from, to);
            return null;
        }

        static class Rating {
            HODateTime time;
            int coachLevel;
            int leadership;
        }

        List<Rating> ratings = new ArrayList<>();
    }

    private ExTrainer exTrainer;

    private void loadExTrainer() {
        var history = DBManager.instance().loadPlayerHistory(this.getPlayerId());
        exTrainer = new ExTrainer();
        var trainerTime = history.stream().filter(Player::isCoach).toList();
        for ( var historicalPlayer : trainerTime){
            if (exTrainer.from == null) exTrainer.from = historicalPlayer.getHrfDate();
            exTrainer.to = historicalPlayer.getHrfDate();
            var rating = new ExTrainer.Rating();
            rating.time = historicalPlayer.getHrfDate();
            rating.coachLevel = historicalPlayer.getCoachSkill();
            rating.leadership = historicalPlayer.getLeadership();
            exTrainer.ratings.add(rating);
        }
    }

    public ExTrainer getExTrainer(){
        if ( exTrainer == null) loadExTrainer();
        return exTrainer;
    }

    public HODateTime getExTrainerFrom(){
        getExTrainer();
        if ( exTrainer.from != null) return exTrainer.from;
        return null;
    }
    public HODateTime getExTrainerTo(){
        getExTrainer();
        if ( exTrainer.to != null) return exTrainer.to;
        return null;
    }
    public HODateTime.HODuration getExTrainerDuration(){
        getExTrainer();
        return exTrainer.getTrainerDuration();
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
//        this.setCountryId(xmlIntValue(root, "CountryID"));
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
