package module.teamAnalyzer.vo;

import com.install4j.runtime.util.MinimumSizeTextArea;
import core.file.xml.MyHashtable;
import core.file.xml.XMLManager;
import core.model.player.MatchRoleID;
import module.teamAnalyzer.manager.PlayerDataManager;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static core.model.player.MatchRoleID.getKurzNameForPosition;
import static module.lineup.substitution.LanguageStringLookup.getPosition;

public class PlayerInfo {
    private Date lastMatchDate = null;
    private int lastMatchId;
    private int lastMatchPosition;
    private int lastMatchPlayedMinutes;
    private float lastMatchRatingEndOfGame;
    private float rating;
    //~ Instance fields ----------------------------------------------------------------------------
    String name = "";
    int age;
    int experience;
    int form;
    int playerId;
    int specialEvent;
    int status = PlayerDataManager.UNKNOWN;
    int tSI;
    int teamId;
    int salary; // Money in SEK
    int stamina;
    boolean motherClubBonus;
    int loyalty;

    public PlayerInfo(MyHashtable i) {
        this.age = Integer.parseInt(i.get("Age"));
        this.experience = Integer.parseInt(i.get("Experience"));
        this.form = Integer.parseInt(i.get("PlayerForm"));
        this.loyalty = Integer.parseInt(i.get("Loyalty"));
        this.motherClubBonus = Boolean.parseBoolean(i.get("MotherClubBonus"));
        this.name = i.get("FirstName") + " " + i.get("LastName");
        this.playerId = Integer.parseInt(i.get("PlayerID"));
        this.salary = Integer.parseInt(i.get("Salary"));
        this.specialEvent = Integer.parseInt(i.get("Specialty"));
        this.stamina = Integer.parseInt(i.get("StaminaSkill"));

        int cards = parseIntWithDefault(i.get("Cards"), 0);
        int injury = parseIntWithDefault(i.get("InjuryLevel"), 0);
        this.status = PlayerDataManager.AVAILABLE;

        if (cards == 3) {
            status = PlayerDataManager.SUSPENDED;
        }

        if (injury > 0) {
            status = PlayerDataManager.INJURED;
        }

        this.teamId = Integer.parseInt(i.get("TeamID"));
        this.tSI = Integer.parseInt(i.get("MarketValue"));

        try {
            this.lastMatchDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(i.get("LastMatch_Date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.rating = Float.parseFloat(i.get("LastMatch_Rating"));
        this.lastMatchId = Integer.parseInt(i.get("LastMatch_id"));
        this.lastMatchPosition = Integer.parseInt(i.get("LastMatch_PositionCode"));
        this.lastMatchPlayedMinutes = Integer.parseInt(i.get("LastMatch_PlayedMinutes"));
        this.lastMatchRatingEndOfGame = Float.parseFloat(i.get("LastMatch_RatingEndOfGame"));
    }

    private int parseIntWithDefault(String s, int i) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e){}
        return i;
    }

    public PlayerInfo() {

    }

    //~ Methods ------------------------------------------------------------------------------------
    public void setAge(int i) {
        age = i;
    }

    public int getAge() {
        return age;
    }

    public void setExperience(int i) {
        experience = i;
    }

    public int getExperience() {
        return experience;
    }

    public void setForm(int i) {
        form = i;
    }

    public int getForm() {
        return form;
    }

    public void setName(String string) {
        name = string;
    }

    public String getName() {
        return name;
    }

    public void setPlayerId(int i) {
        playerId = i;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setSpecialEvent(int i) {
        specialEvent = i;
    }

    public int getSpecialEvent() {
        return specialEvent;
    }

    public void setStatus(int i) {
        status = i;
    }

    public int getStatus() {
        return status;
    }

    public void setTSI(int i) {
        tSI = i;
    }

    public int getTSI() {
        return tSI;
    }

    public void setTeamId(int i) {
        teamId = i;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getSalary() {
        return salary;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getStamina() {
        return this.stamina;
    }

    public void setMotherClubBonus(boolean bonus) {
        this.motherClubBonus = bonus;
    }

    public boolean getMotherClubBonus() {
        return this.motherClubBonus;
    }

    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }

    public int getLoyalty() {
        return this.loyalty;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        String buffer = getPosition(lastMatchPosition) +
                " " + name +
                ", age=" + age +
                ", experience=" + experience +
                ", form=" + form +
                ", rating=" + rating +
                ", status=" + status +
                ", motherClubBonus=" + motherClubBonus +
                ", loyalty=" + loyalty;
        return buffer;
    }

    public Date getLastMatchDate() {
        return lastMatchDate;
    }

    public int getLastMatchId() {
        return lastMatchId;
    }

    public int getLastMatchPosition() {
        return lastMatchPosition;
    }

    public int getLastMatchPlayedMinutes() {
        return lastMatchPlayedMinutes;
    }

    public float getLastMatchRatingEndOfGame() {
        return lastMatchRatingEndOfGame;
    }

    public float getRating() {
        return rating;
    }
}
