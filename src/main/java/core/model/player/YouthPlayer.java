package core.model.player;

import module.opponentspy.CalcVariables;
import module.training.Skills;

import java.sql.Timestamp;
import java.util.*;

import static core.util.Helper.parseDate;

public class YouthPlayer {
    private int id;
    private String nickName;
    private String firstName;
    private String lastName;
    private int ageYears;
    private int ageDays;
    private Timestamp arrivalDate;
    private int canBePromotedIn;
    private String playerNumber;
    private String statement;
    private String ownerNotes;
    private int playerCategoryID;
    private int cards;
    private int injuryLevel;
    private int specialty;
    private int careerGoals;
    private int careerHattricks;
    private int leagueGoals;
    private int friendlyGoals;
    private int scoutId;
    private int scoutingRegionID;
    private String scoutName;
    private int youthMatchID;
    private int positionCode;
    private int playedMinutes;
    private int rating;
    private Timestamp youthMatchDate;
    private Timestamp hrfDate;

    private Map<Integer, SkillInfo> skillInfoMap = new HashMap<>();
    private List<ScoutComment> scoutComments = new ArrayList<>();

    public YouthPlayer() {

    }

    public List<ScoutComment> getScoutComments(){
        return this.scoutComments;
    }

    public void setScoutComments(List<ScoutComment> scoutComments) {
        this.scoutComments = scoutComments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(int ageYears) {
        this.ageYears = ageYears;
    }

    public int getAgeDays() {
        return ageDays;
    }

    public void setAgeDays(int ageDays) {
        this.ageDays = ageDays;
    }

    public Timestamp getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Timestamp arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public int getCanBePromotedIn() {
        return canBePromotedIn;
    }

    public void setCanBePromotedIn(int canBePromotedIn) {
        this.canBePromotedIn = canBePromotedIn;
    }

    public String getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(String playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getOwnerNotes() {
        return ownerNotes;
    }

    public void setOwnerNotes(String ownerNotes) {
        this.ownerNotes = ownerNotes;
    }

    public int getPlayerCategoryID() {
        return playerCategoryID;
    }

    public void setPlayerCategoryID(int playerCategoryID) {
        this.playerCategoryID = playerCategoryID;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }

    public int getInjuryLevel() {
        return injuryLevel;
    }

    public void setInjuryLevel(int injuryLevel) {
        this.injuryLevel = injuryLevel;
    }

    public int getSpecialty() {
        return specialty;
    }

    public void setSpecialty(int specialty) {
        this.specialty = specialty;
    }

    public int getCareerGoals() {
        return careerGoals;
    }

    public void setCareerGoals(int careerGoals) {
        this.careerGoals = careerGoals;
    }

    public int getCareerHattricks() {
        return careerHattricks;
    }

    public void setCareerHattricks(int careerHattricks) {
        this.careerHattricks = careerHattricks;
    }

    public int getLeagueGoals() {
        return leagueGoals;
    }

    public void setLeagueGoals(int leagueGoals) {
        this.leagueGoals = leagueGoals;
    }

    public int getFriendlyGoals() {
        return friendlyGoals;
    }

    public void setFriendlyGoals(int friendlyGoals) {
        this.friendlyGoals = friendlyGoals;
    }

    public int getScoutId() {
        return scoutId;
    }

    public void setScoutId(int scoutId) {
        this.scoutId = scoutId;
    }

    public int getScoutingRegionID() {
        return scoutingRegionID;
    }

    public void setScoutingRegionID(int scoutingRegionID) {
        this.scoutingRegionID = scoutingRegionID;
    }

    public String getScoutName() {
        return scoutName;
    }

    public void setScoutName(String scoutName) {
        this.scoutName = scoutName;
    }

    public int getYouthMatchID() {
        return youthMatchID;
    }

    public void setYouthMatchID(int youthMatchID) {
        this.youthMatchID = youthMatchID;
    }

    public int getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(int positionCode) {
        this.positionCode = positionCode;
    }

    public int getPlayedMinutes() {
        return playedMinutes;
    }

    public void setPlayedMinutes(int playedMinutes) {
        this.playedMinutes = playedMinutes;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Timestamp getYouthMatchDate() {
        return youthMatchDate;
    }

    public void setYouthMatchDate(Timestamp youthMatchDate) {
        this.youthMatchDate = youthMatchDate;
    }

    public Timestamp getHrfDate() {
        return hrfDate;
    }

    public void setHrfDate(Timestamp hrfDate) {
        this.hrfDate = hrfDate;
    }

    public SkillInfo getSkillInfo(Skills.HTSkillID skillID) {
        return this.skillInfoMap.get(skillID);
    }

    public class SkillInfo {
        public int level;
        public boolean isAvailable;
        public boolean isMaxReached;
        public boolean mayUnlock;
        public boolean isMaxAvailable;
        public int max;
    }

    public class ScoutComment {
        public String text;
        public int type;
        public int variation;
        public int skillType;
        public int skillLevel;
    }

    public YouthPlayer(Properties properties, Timestamp hrfdate) {

        this.hrfDate = hrfdate;


        id = Integer.parseInt(properties.getProperty("id", "0"));
        firstName = properties.getProperty("FirstName", "");
        nickName = properties.getProperty("NickName", "");
        lastName = properties.getProperty("LastName", "");
        ageYears = Integer.parseInt(properties.getProperty("Age", "0"));
        ageDays = Integer.parseInt(properties.getProperty("AgeDays", "0"));
        arrivalDate = parseDate(properties.getProperty("ArrivalDate", ""));
        canBePromotedIn = Integer.parseInt(properties.getProperty("CanBePromotedIn", "0"));
        playerNumber = properties.getProperty("PlayerNumber", "");
        statement = properties.getProperty("Statement", "");
        ownerNotes = properties.getProperty("OwnerNotes", "");
        playerCategoryID = Integer.parseInt(properties.getProperty("PlayerCategoryID", "0"));
        cards = Integer.parseInt(properties.getProperty("Cards", "0"));
        injuryLevel = Integer.parseInt(properties.getProperty("InjuryLevel", "0"));
        specialty = Integer.parseInt(properties.getProperty("Specialty", "0"));
        careerGoals = Integer.parseInt(properties.getProperty("CareerGoals", "0"));
        careerHattricks = Integer.parseInt(properties.getProperty("CareerHattricks", "0"));
        leagueGoals = Integer.parseInt(properties.getProperty("LeagueGoals", "0"));
        friendlyGoals = Integer.parseInt(properties.getProperty("FriendlyGoals", "0"));
        scoutId = Integer.parseInt(properties.getProperty("ScoutId", "0"));
        scoutingRegionID = Integer.parseInt(properties.getProperty("ScoutingRegionID", "0"));
        scoutName = properties.getProperty("ScoutName", "");

        youthMatchID = Integer.parseInt(properties.getProperty("YouthMatchID", "0"));
        positionCode = Integer.parseInt(properties.getProperty("PositionCode", "0"));
        playedMinutes = Integer.parseInt(properties.getProperty("PlayedMinutes", "0"));
        rating = Integer.parseInt(properties.getProperty("Rating", "0"));
        youthMatchDate = parseDate(properties.getProperty("YouthMatchDate", ""));

        parseSkillInfo(properties, Skills.HTSkillID.GOALKEEPER,  "KeeperSkill");
        parseSkillInfo(properties, Skills.HTSkillID.DEFENDING,  "DefenderSkill");
        parseSkillInfo(properties, Skills.HTSkillID.PLAYMAKING,  "PlaymakerSkill");
        parseSkillInfo(properties, Skills.HTSkillID.WINGER,  "WingerSkill");
        parseSkillInfo(properties, Skills.HTSkillID.PASSING,  "PassingSkill");
        parseSkillInfo(properties, Skills.HTSkillID.SET_PIECES,  "SetPiecesSkill");

        for ( int i=0; parseScoutComment(properties, i); i++);
    }

    private boolean parseScoutComment(Properties properties, int i) {
        var prefix = "ScoutComment"+i;
        var text = properties.getProperty(prefix+"Text");
        if ( text != null){
            var scoutComment = new ScoutComment();
            scoutComment.text = properties.getProperty(prefix+"Text", "");
            scoutComment.type = Integer.parseInt(properties.getProperty(prefix+"Type", "-1"));
            scoutComment.variation = Integer.parseInt(properties.getProperty(prefix+"Variation", "-1"));
            scoutComment.skillType = Integer.parseInt(properties.getProperty(prefix+"SkillType", "-1"));
            scoutComment.skillLevel = Integer.parseInt(properties.getProperty(prefix+"SkillLevel", "-1"));
            this.scoutComments.add(scoutComment);
            return true;
        }
        return false;
    }

    private void parseSkillInfo(Properties properties, Skills.HTSkillID skillID, String skill) {
        var skillInfo = new SkillInfo();
        skillInfo.level = Integer.parseInt(properties.getProperty(skill, "-1"));
        skillInfo.max = Integer.parseInt(properties.getProperty(skill+"Max", "-1"));
        skillInfo.isAvailable = Boolean.parseBoolean(properties.getProperty(skill + "IsAvailable", "false"));
        skillInfo.isMaxReached = Boolean.parseBoolean(properties.getProperty(skill + "IsMaxReached", "false"));
        skillInfo.mayUnlock = Boolean.parseBoolean(properties.getProperty(skill + "MayUnlock", "false"));
        skillInfo.isMaxAvailable = Boolean.parseBoolean(properties.getProperty(skill + "MaxIsAvailable", "false"));

        this.skillInfoMap.put(skillID.getValue(), skillInfo);
    }

}
