package module.teamAnalyzer.vo;

import core.db.AbstractTable;
import core.file.xml.SafeInsertMap;
import core.util.AmountOfMoney;
import core.util.HODateTime;
import core.util.HOLogger;
import lombok.Getter;
import lombok.Setter;
import module.teamAnalyzer.manager.PlayerDataManager;

import static module.lineup.substitution.LanguageStringLookup.getPosition;

@Getter
public class PlayerInfo extends AbstractTable.Storable {

    private int injuryLevel;
    private HODateTime lastMatchDate = null;
    private int lastMatchId;
    private int lastMatchPosition;
    private int lastMatchPlayedMinutes;
    private float lastMatchRatingEndOfGame;
    private float rating;
    //~ Instance fields ----------------------------------------------------------------------------
    @Setter
    String name = "";
    //~ Methods ------------------------------------------------------------------------------------
    @Setter
    int age;
    @Setter
    int experience;
    @Setter
    int form;
    @Setter
    int playerId;
    @Setter
    int specialEvent;
    int status;
    int injuryStatus = 0;
    int bookingStatus = 0;
    int transferListedStatus = 0;
    @Setter
    int tsi;
    @Setter
    int teamId;
    @Setter
    AmountOfMoney salary; // Money in SEK
    @Setter
    int stamina;
    @Setter
    boolean motherClubBonus;
    @Setter
    int loyalty;
    @Setter
    private int week;

    public PlayerInfo(SafeInsertMap i) {
        this.age = Integer.parseInt(i.get("Age"));
        this.experience = Integer.parseInt(i.get("Experience"));
        this.form = Integer.parseInt(i.get("PlayerForm"));
        this.loyalty = Integer.parseInt(i.get("Loyalty"));
        this.motherClubBonus = Boolean.parseBoolean(i.get("MotherClubBonus"));
        this.name = i.get("FirstName") + " " + i.get("LastName");
        this.playerId = Integer.parseInt(i.get("PlayerID"));
        this.salary = new AmountOfMoney(Integer.parseInt(i.get("Salary")));
        this.specialEvent = Integer.parseInt(i.get("Specialty"));
        this.stamina = Integer.parseInt(i.get("StaminaSkill"));
        this.status = 0;

        int cards = parseIntWithDefault(i.get("Cards"), 0);
        this.injuryLevel = parseIntWithDefault(i.get("InjuryLevel"), -1);

        switch (cards) {
            case 1 -> bookingStatus = PlayerDataManager.YELLOW;
            case 2 -> bookingStatus = PlayerDataManager.DOUBLE_YELLOW;
            case 3 -> bookingStatus = PlayerDataManager.SUSPENDED;
            default -> bookingStatus = 0;
        }

        switch (injuryLevel) {
            case -1 -> injuryStatus = 0;
            case 0 -> injuryStatus = PlayerDataManager.BRUISED;
            default -> injuryStatus = PlayerDataManager.INJURED;
        }

        if (parseBooleanWithDefault(i.get("TransferListed"), false)) {
            transferListedStatus = PlayerDataManager.TRANSFER_LISTED;
        } else {
            transferListedStatus = 0;
        }

        this.status = injuryStatus + 10 * bookingStatus + 100 * transferListedStatus;


        this.teamId = Integer.parseInt(i.get("TeamID"));
        this.tsi = Integer.parseInt(i.get("MarketValue"));
        this.lastMatchDate = HODateTime.fromHT(i.get("LastMatch_Date"));
        this.rating = Float.parseFloat(i.get("LastMatch_Rating"));
        this.lastMatchId = Integer.parseInt(i.get("LastMatch_id"));
        this.lastMatchPosition = Integer.parseInt(i.get("LastMatch_PositionCode"));
        this.lastMatchPlayedMinutes = Integer.parseInt(i.get("LastMatch_PlayedMinutes"));
        this.lastMatchRatingEndOfGame = Float.parseFloat(i.get("LastMatch_RatingEndOfGame"));
    }

    private int parseIntWithDefault(String s, int i) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
        }
        return i;
    }

    private boolean parseBooleanWithDefault(String s, boolean res) {
        try {
            return Boolean.parseBoolean(s);
        } catch (NumberFormatException e) {
            HOLogger.instance().error(this.getClass(), res + " could not be recognized as a valid boolean");
        }
        return res;
    }

    public PlayerInfo() {
    }

    public void setStatus(int i) {
        status = i;

        int digit = i % 10;
        this.injuryStatus = digit;
        i = i / 10;

        digit = i % 10;
        this.bookingStatus = digit;
        i = i / 10;

        digit = i % 10;
        this.transferListedStatus = digit;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        return getPosition(lastMatchPosition) +
                " " + name +
                ", age=" + age +
                ", experience=" + experience +
                ", form=" + form +
                ", rating=" + rating +
                ", status=" + status +
                ", motherClubBonus=" + motherClubBonus +
                ", loyalty=" + loyalty;
    }

    public boolean isTransferListed() {
        return transferListedStatus != 0;
    }
}
