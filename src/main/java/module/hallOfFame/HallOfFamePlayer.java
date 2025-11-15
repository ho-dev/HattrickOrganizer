package module.hallOfFame;

import core.model.player.Player;
import core.util.HODateTime;

public class HallOfFamePlayer extends Player {
    public HODateTime getNextBirthday() {
        return nextBirthday;
    }

    public void setNextBirthday(HODateTime nextBirthday) {
        this.nextBirthday = nextBirthday;
    }

    //    PlayerID : unsigned Integer
//    The globally unique PlayerID.
//    FirstName : String
//    Player FirstName name.
//            NickName : String
//    Player NickName name.
//            LastName : String
//    Player LastName name.
//            Age : unsigned Integer
//    The age of the player in years.
//            NextBirthday : DateTime
//    The approximate Date/time of next birthday.
    private HODateTime nextBirthday;

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

    //    CountryID : unsigned Integer
//    CountryID of the country where the player was born.
//            ArrivalDate : DateTime
//    The date of arrival to the team.
//            ExpertType : unsigned Integer
//    An identifier to show which type of job the player have now.
//            # See table HoFExpertType
    private int expertTypeId;
//    HofDate : DateTime
//    The date the player was made hall of fame.
    private HODateTime hofDate;

    public int getHofAge() {
        return hofAge;
    }

    public void setHofAge(int hofAge) {
        this.hofAge = hofAge;
    }

    //            HofAge : unsigned Integer
//    The age of the player in years when he was made hall of fame.
    private int hofAge;


    public String getExpertType() {
    }
}
