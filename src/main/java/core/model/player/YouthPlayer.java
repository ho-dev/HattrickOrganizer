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
        return this.skillInfoMap.get(skillID.getValue());
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


        id = getInt(properties,"id", 0);
        firstName = properties.getProperty("firstname", "");
        nickName = properties.getProperty("nickname", "");
        lastName = properties.getProperty("lastname", "");
        ageYears = getInt(properties,"age", 0);
        ageDays = getInt(properties,"agedays", 0);
        arrivalDate = parseDate(properties.getProperty("arrivaldate", ""));
        canBePromotedIn = getInt(properties,"canbepromotedin", 0);
        playerNumber = properties.getProperty("playernumber", "");
        statement = properties.getProperty("statement", "");
        ownerNotes = properties.getProperty("ownernotes", "");
        playerCategoryID = getInt(properties,"playercategoryid", 0);
        cards = getInt(properties,"cards", 0);
        injuryLevel = getInt(properties,"injurylevel", 0);
        specialty = getInt(properties,"specialty", 0);
        careerGoals = getInt(properties,"careergoals", 0);
        careerHattricks = getInt(properties,"careerhattricks", 0);
        leagueGoals = getInt(properties,"leaguegoals", 0);
        friendlyGoals = getInt(properties,"friendlygoals", 0);
        scoutId = getInt(properties,"scoutid", 0);
        scoutingRegionID = getInt(properties,"scoutingregionid", 0);
        scoutName = properties.getProperty("scoutname", "");

        youthMatchID = getInt(properties,"youthmatchid", 0);
        positionCode = getInt(properties,"positioncode", 0);
        playedMinutes = getInt(properties,"playedminutes", 0);
        rating = getInt(properties,"rating", 0);
        youthMatchDate = parseDate(properties.getProperty("youthmatchdate", ""));

        parseSkillInfo(properties, Skills.HTSkillID.GOALKEEPER,  "keeperskill");
        parseSkillInfo(properties, Skills.HTSkillID.DEFENDING,  "defenderskill");
        parseSkillInfo(properties, Skills.HTSkillID.PLAYMAKING,  "playmakerskill");
        parseSkillInfo(properties, Skills.HTSkillID.WINGER,  "wingerskill");
        parseSkillInfo(properties, Skills.HTSkillID.PASSING,  "passingskill");
        parseSkillInfo(properties, Skills.HTSkillID.SET_PIECES,  "setpiecesskill");

        for ( int i=0; parseScoutComment(properties, i); i++);
    }

    private boolean parseScoutComment(Properties properties, int i) {
        var prefix = "scoutcomment"+i;
        var text = properties.getProperty(prefix+"Text");
        if ( text != null){
            var scoutComment = new ScoutComment();
            scoutComment.text = properties.getProperty(prefix+"text", "");
            scoutComment.type = getInt(properties,prefix+"type", -1);
            scoutComment.variation = getInt(properties,prefix+"variation", -1);
            scoutComment.skillType = getInt(properties,prefix+"skilltype", -1);
            scoutComment.skillLevel = getInt(properties,prefix+"skilllevel", -1);
            this.scoutComments.add(scoutComment);
            return true;
        }
        return false;
    }

    private void parseSkillInfo(Properties properties, Skills.HTSkillID skillID, String skill) {
        var skillInfo = new SkillInfo();
        skillInfo.level = getInt(properties, skill, -1);
        skillInfo.max = getInt(properties,skill+"max", -1);
        skillInfo.isAvailable = getBoolean(properties,skill + "isavailable", false);
        skillInfo.isMaxReached = getBoolean(properties,skill + "ismaxreached", false);
        skillInfo.mayUnlock = getBoolean(properties,skill + "mayunlock", false);
        skillInfo.isMaxAvailable = getBoolean(properties,skill + "maxisavailable", false);

        this.skillInfoMap.put(skillID.getValue(), skillInfo);
    }

    private boolean getBoolean(Properties p, String key, boolean defautValue) {
        var s  = p.getProperty(key);
        if ( s != null && s.length()>0) return Boolean.parseBoolean(s);
        return defautValue;
    }

    private int getInt(Properties p, String key, int defaultValue) {
        var s  = p.getProperty(key);
        if ( s != null && s.length()>0) return Integer.parseInt(s);
        return defaultValue;
    }

}
