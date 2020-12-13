package core.model.player;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.training.YouthTrainerComment;
import core.util.HOLogger;
import module.training.Skills;
import module.training.Skills.ScoutCommentSkillTypeID;

import java.sql.Timestamp;
import java.util.*;

import static core.util.Helper.parseDate;
import static java.lang.Math.max;
import static module.training.Skills.HTSkillID.*;

public class YouthPlayer {
    private int hrfid;
    private int id;
    private String nickName;
    private String firstName;
    private String lastName;
    private int ageYears;
    private int ageDays;
    private Timestamp arrivalDate;
    private Timestamp promotionDate;
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
    private Integer youthMatchID;
    private Integer positionCode;
    private int playedMinutes;
    private Double rating;
    private Timestamp youthMatchDate;

    private Map<Integer, SkillInfo> skillInfoMap = new HashMap<>();
    private List<ScoutComment> scoutComments;
    private List<YouthTrainerComment> trainerComments;

    public YouthPlayer() {

    }

    public List<ScoutComment> getScoutComments(){
        if ( scoutComments==null){
            scoutComments = DBManager.instance().loadYouthScoutComments(this.getId());
        }
        return this.scoutComments;
    }

    public void setScoutComments(List<ScoutComment> scoutComments) {
        this.scoutComments = scoutComments;
    }

    private void evaluateScoutComments(){
        if ( scoutComments != null){
            for(var c : scoutComments){
                if ( c.type == CommentType.CURRENT_SKILL_LEVEL){
                    this.getSkillInfo(c.skillType.toHTSkillId()).setStartLevel(c.skillLevel);
                }
                else if (c.type == CommentType.POTENTIAL_SKILL_LEVEL){
                    this.getSkillInfo(c.skillType.toHTSkillId()).setMax(c.skillLevel);
                }
                else if ( c.type == CommentType.PLAYER_HAS_SPECIALTY){
                    this.specialty = c.skillLevel; // guessed (not documented which attribute stores the specialty number)
                }
            }
        }
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

    public int getCanBePromotedInAtDate(long date){
        long hrftime = HOVerwaltung.instance().getModel().getBasics().getDatum().getTime();
        long diff = (date - hrftime) / (1000 * 60 * 60 * 24);
        return max(0,canBePromotedIn-(int)diff);
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

    public Integer getYouthMatchID() {
        return youthMatchID;
    }

    public void setYouthMatchID(Integer youthMatchID) {
        this.youthMatchID = youthMatchID;
    }

    public Integer getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(Integer positionCode) {
        this.positionCode = positionCode;
    }

    public int getPlayedMinutes() {
        return playedMinutes;
    }

    public void setPlayedMinutes(int playedMinutes) {
        this.playedMinutes = playedMinutes;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Timestamp getYouthMatchDate() {
        return youthMatchDate;
    }

    public void setYouthMatchDate(Timestamp youthMatchDate) {
        this.youthMatchDate = youthMatchDate;
    }

    public SkillInfo getSkillInfo(Skills.HTSkillID skillID) {
        return this.skillInfoMap.get(skillID.getValue());
    }

    public int getHrfid() {
        return hrfid;
    }

    public void setHrfid(int hrfid) {
        this.hrfid = hrfid;
    }

    public Timestamp getPromotionDate() {
        return promotionDate;
    }

    public void setPromotionDate(Timestamp promotionDate) {
        this.promotionDate = promotionDate;
    }

    public void addComment(YouthTrainerComment comment) {
        var count = this.getTrainerComments().stream()
                .filter(c -> c.getYouthPlayerId() == this.id &&
                        c.getYouthMatchId() == comment.getYouthMatchId() &&
                        c.getIndex() == comment.getIndex())
                .count();
        if ( count == 0) this.trainerComments.add(comment);
    }

    private List<YouthTrainerComment> getTrainerComments() {
        if ( this.trainerComments == null){
            this.trainerComments = DBManager.instance().loadYouthTrainerComments(this.id);
        }
        return this.trainerComments;

    }

    public String getFullName() {
        var ret = this.getFirstName();
        if ( ret.length() > 0 && this.getNickName().length() > 0){
            ret += " '" + this.getNickName() + "'";
        }
        if ( ret.length() > 0 && this.getLastName().length() > 0){
            ret += " " + this.getLastName();
        }
        return ret;
    }

    public static Skills.HTSkillID[] skillIds = {Keeper, Defender, Playmaker, Winger, Passing, Scorer, SetPieces};

    public void setSkillInfo(SkillInfo skillinfo) {
        this.skillInfoMap.put(skillinfo.skillID.getValue(), skillinfo);
    }

    public static class SkillInfo {

        /**
         * Skill Id
         */
        private Skills.HTSkillID skillID;
        /**
         * Value at scouting date, edited by the user (user's estimation)
         */
        private double startValue;
        /**
         * Calculated value based on trainings and edited start value
         */
        private double currentValue;
        /**
         * Skill level at the current download
         */
        private Integer currentLevel;
        /**
         * Skill level at the scouting date
         */
        private Integer startLevel;
        /**
         * Maximum reachable skill level (potential)
         */
        private Integer max;
        /**
         *  Indicates if the skill cant be trained anymore (false if current or max is not available).
         */
        private boolean isMaxReached;

        public SkillInfo(Skills.HTSkillID id){
            this.skillID = id;
        }

        public boolean isCurrentLevelAvailable() {
            return currentLevel != null;
        }
        public boolean isStartLevelAvailable() {
            return startLevel != null;
        }

        public boolean isMaxAvailable(){
            return max != null;
        }

        public double getStartValue(){return this.startValue;}
        public void setStartValue(double value){
            this.startValue=value;
            adjustValues();
        }

        private void adjustValues()
        {
            if ( max != null) {
                if (currentValue > max + 1) {
                    currentValue = max + 0.99;
                }
            }
            if ( currentLevel != null) {
                if (currentValue < currentLevel) {
                    this.currentValue = currentLevel;
                } else if (currentValue > currentLevel + 1) {
                    this.currentValue = currentLevel + 0.99;
                }
            }

            if ( startLevel != null) {
                if (startValue < startLevel) {
                    this.startValue = startLevel;
                } else if (startValue > startLevel + 1) {
                    this.startValue = startLevel + 0.99;
                }

                if (currentValue < startValue) {
                    currentValue = startValue;
                }
            }
            else if ( currentValue < startValue){
                startValue = currentValue;
            }
        }

        public double getCurrentValue(){return currentValue;}
        public void setCurrentValue(double value){
            this.currentValue=value;
            adjustValues();
        }

        public Integer getCurrentLevel() {
            return currentLevel;
        }

        public void setCurrentLevel(Integer currentLevel) {
            this.currentLevel = currentLevel;
            adjustValues();
        }

        public Integer getMax() {
            return max;
        }

        public void setMax(Integer max) {
            this.max = max;
            adjustValues();
        }

        public boolean isMaxReached() {
            return isMaxReached;
        }

        public void setMaxReached(boolean maxReached) {
            isMaxReached = maxReached;
        }

        public Integer getStartLevel() {
            return startLevel;
        }

        public void setStartLevel(Integer startLevel) {
            this.startLevel = startLevel;
            adjustValues();
        }

        public Skills.HTSkillID getSkillID(){
            return skillID;
        }
    }

    public static class ScoutComment {
        private int youthPlayerId;
        private int index;
        private String text;
        private CommentType type;
        private Integer variation;
        private ScoutCommentSkillTypeID skillType;
        private Integer skillLevel;

        public ScoutComment(){}

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public CommentType getType() {
            return type;
        }

        public void setType(CommentType type) { this.type = type; }

        public Integer getVariation() {
            return variation;
        }

        public void setVariation(Integer variation) {
            this.variation = variation;
        }

        public ScoutCommentSkillTypeID getSkillType() {
            return skillType;
        }

        public void setSkillType(ScoutCommentSkillTypeID skillType) {
            this.skillType = skillType;
        }

        public Integer getSkillLevel() {
            return skillLevel;
        }

        public void setSkillLevel(Integer skillLevel) {
            this.skillLevel = skillLevel;
        }

        public int getYouthPlayerId() {
            return youthPlayerId;
        }

        public void setYouthPlayerId(int youthPlayerId) {
            this.youthPlayerId = youthPlayerId;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public YouthPlayer(Properties properties) {

        id = getInt(properties, "id", 0);
        firstName = properties.getProperty("firstname", "");
        nickName = properties.getProperty("nickname", "");
        lastName = properties.getProperty("lastname", "");
        ageYears = getInt(properties, "age", 0);
        ageDays = getInt(properties, "agedays", 0);
        arrivalDate = parseDate(properties.getProperty("arrivaldate", ""));
        canBePromotedIn = getInt(properties, "canbepromotedin", 0);
        playerNumber = properties.getProperty("playernumber", "");
        statement = properties.getProperty("statement", "");
        ownerNotes = properties.getProperty("ownernotes", "");
        playerCategoryID = getInt(properties, "playercategoryid", 0);
        cards = getInt(properties, "cards", 0);
        injuryLevel = getInt(properties, "injurylevel", 0);
        specialty = getInt(properties, "specialty", 0);
        careerGoals = getInt(properties, "careergoals", 0);
        careerHattricks = getInt(properties, "careerhattricks", 0);
        leagueGoals = getInt(properties, "leaguegoals", 0);
        friendlyGoals = getInt(properties, "friendlygoals", 0);
        scoutId = getInt(properties, "scoutid", 0);
        scoutingRegionID = getInt(properties, "scoutingregionid", 0);
        scoutName = properties.getProperty("scoutname", "");

        youthMatchID = getInteger(properties, "youthmatchid");
        positionCode = getInteger(properties, "positioncode");
        playedMinutes = getInt(properties, "playedminutes", 0);
        rating = getDouble(properties, "rating");
        youthMatchDate = parseNullableDate(properties.getProperty("youthmatchdate"));

        for (var skillId : YouthPlayer.skillIds) {
            parseSkillInfo(properties, skillId);
        }

        this.scoutComments = new ArrayList<>();
        for (int i = 0; true; i++) {
            if (!parseScoutComment(properties, i)) break;
        }
        evaluateScoutComments();
    }

    // don't want to see warnings in HO log file on empty date strings
    private Timestamp parseNullableDate(String date) {
        if ( date != null && date.length()>0) return parseDate(date);
        return null;
    }

    private boolean parseScoutComment(Properties properties, int i) {
        var prefix = "scoutcomment"+i;
        var text = properties.getProperty(prefix+"text");
        if ( text != null){
            var scoutComment = new ScoutComment();
            scoutComment.text = properties.getProperty(prefix+"text", "");
            scoutComment.type = CommentType.valueOf(getInteger(properties,prefix+"type"));
            scoutComment.variation = getInteger(properties,prefix+"variation");
            if ( scoutComment.type == CommentType.AVERAGE_SKILL_LEVEL){
                // Average comment stores skill level in skillType
                scoutComment.skillType = ScoutCommentSkillTypeID.AVERAGE;
                scoutComment.skillLevel = getInteger(properties,prefix+"skilltype");
            }
            else {
                scoutComment.skillType = ScoutCommentSkillTypeID.valueOf(getInteger(properties,prefix+"skilltype"));
                scoutComment.skillLevel = getInteger(properties,prefix+"skilllevel");
            }
            this.scoutComments.add(scoutComment);
            return true;
        }
        return false;
    }

    private void parseSkillInfo(Properties properties, Skills.HTSkillID skillID) {
        var skill = skillID.toString().toLowerCase(java.util.Locale.ENGLISH)+"skill";
        var skillInfo = new SkillInfo(skillID);
        skillInfo.currentLevel = getInteger(properties, skill);
        skillInfo.max = getInteger(properties,skill+"max");
        skillInfo.isMaxReached = getBoolean(properties,skill + "ismaxreached", false);

        this.skillInfoMap.put(skillID.getValue(), skillInfo);
    }

    private Boolean getBoolean(Properties p, String key) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Boolean.parseBoolean(s);
        }
        catch(Exception ignored){
        }
        return null;
    }

    private Integer getInteger(Properties p, String key) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Integer.parseInt(s);
        }
        catch(Exception ignored){
        }
        return null;
    }

    private boolean getBoolean(Properties p, String key, boolean defautValue) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Boolean.parseBoolean(s);
        }
        catch(Exception e){
            HOLogger.instance().warning(getClass(), "getBoolean: " + e.toString());
        }
        return defautValue;
    }

    private int getInt(Properties p, String key, int defaultValue) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Integer.parseInt(s);
        }
        catch(Exception e){
            HOLogger.instance().warning(getClass(), "getInt: " + e.toString());
        }
        return defaultValue;
    }

    private Double getDouble(Properties p, String key) {
        try {
            var s = p.getProperty(key);
            if (s != null && s.length() > 0) return Double.parseDouble(s);
        }
        catch(Exception ignored){
        }
        return null;
    }
}
