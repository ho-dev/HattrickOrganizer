package module.youth;

import core.constants.player.PlayerSkill;
import core.db.AbstractTable;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.CommentType;
import core.model.player.Specialty;
import core.util.HODateTime;
import core.util.HOLogger;
import module.training.Skills.ScoutCommentSkillTypeID;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class YouthPlayer extends AbstractTable.Storable {
    private int hrfid;
    private int id;
    private String nickName;
    private String firstName;
    private String lastName;
    private int ageYears;
    private int ageDays;
    private HODateTime arrivalDate;
    private HODateTime promotionDate;
    private int canBePromotedIn;
    private String playerNumber;
    private String statement;
    private String ownerNotes;
    private int playerCategoryID;
    private int cards;
    private int injuryLevel;
    private Specialty specialty;
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
    private HODateTime youthMatchDate;

    private double progressLastMatch=0;

    /**
     * Current skills of the player
     * mapping skill id to skill info
     */
    private YouthSkillsInfo currentSkills = new YouthSkillsInfo();

    /**
     * Comments of the scout at player's arrival
     */
    private List<ScoutComment> scoutComments;

    /**
     * Comments of the trainer after training matches (not available yet)
     */
    private List<YouthTrainerComment> trainerComments;

    /**
     * Player's training development.
     * One entry for each training match the player has participated
     * mapping training match date to development entry
     */
    private TreeMap<HODateTime, YouthTrainingDevelopmentEntry> trainingDevelopment;

    /**
     * Player's potential development calculated by calcTrainingDevelopment.
     */
    private SortedMap<HODateTime, AbstractMap.SimpleEntry<PlayerSkill, Double>> futureTrainings = new TreeMap<>();

    /**
     * Create youth player
     * (used in YouthPlayerTable when player is loaded from database)
     */
    public YouthPlayer() {}

    /**
     * Get the scout comments
     * The list is loaded from database.
     *
     * @return list of Scout comments
     */
    public List<ScoutComment> getScoutComments() {
        if (scoutComments == null) {
            scoutComments = DBManager.instance().loadYouthScoutComments(this.getId());
        }
        return this.scoutComments;
    }

    /**
     * Look for start skill levels, potential and specialties given by the scout.
     * The mentioned skills are tagged as top3 skills.
     */
    private void evaluateScoutComments() {
        if (scoutComments != null) {
            for (var c : scoutComments) {
                if (c.type == CommentType.CURRENT_SKILL_LEVEL) {
                    var startSkill = this.getSkillInfo(c.skillType.toHTSkillId());
                    startSkill.setStartLevel(c.skillLevel);
                    startSkill.setIsTop3(true);
                } else if (c.type == CommentType.POTENTIAL_SKILL_LEVEL) {
                    var maxSkill = this.getSkillInfo(c.skillType.toHTSkillId());
                    maxSkill.setMax(c.skillLevel);
                    maxSkill.setIsTop3(true);
                } else if (c.type == CommentType.PLAYER_HAS_SPECIALTY) {
                    this.specialty = Specialty.getSpecialty(c.skillType.getValue());
                }
            }
        }

        // Find Top3 skills
        this.currentSkills.findTop3Skills();
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

    public HODateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(HODateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public int getCanBePromotedIn() {
        return canBePromotedIn;
    }

    /**
     * Number of days until the player can be promoted at a given date.
     * If only  part of one day left, the duration is formatted as d hh:mm:ss
     *
     * @param date for which the duration should be calculated, typically the current date.
     * @return string not negative number of days. 0 when the player can be promoted.
     */
    public String getCanBePromotedInAtDate(HODateTime date) {
        var diff = Duration.between( HOVerwaltung.instance().getModel().getBasics().getDatum().instant, date.instant);
        var days = canBePromotedIn - (int) diff.toDays();
        if ( days > 0) return "" + days;
        else if ( days < 0 ) return "0";
        else {
            var earliestPromotion = this.arrivalDate.plus(112, ChronoUnit.DAYS);
            if ( earliestPromotion.isAfter(date)){
                diff = Duration.between(date.instant, earliestPromotion.instant);
                var strbuilder = new StringBuilder("0 ");
                var t = diff.toHoursPart();
                if ( t < 10) strbuilder.append("0");
                strbuilder.append(t).append(":");
                t = diff.toMinutesPart();
                if ( t < 10) strbuilder.append("0");
                strbuilder.append(t).append(":");
                t = diff.toSecondsPart();
                if ( t < 10) strbuilder.append("0");
                strbuilder.append(t);
                return strbuilder.toString();
            }
            return "0";
        }
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

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
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

    public HODateTime getYouthMatchDate() {
        return youthMatchDate;
    }

    public void setYouthMatchDate(HODateTime youthMatchDate) {
        this.youthMatchDate = youthMatchDate;
    }

    public YouthSkillInfo getSkillInfo(PlayerSkill skillID) {
        return this.currentSkills.get(skillID);
    }

    public int getHrfid() {
        return hrfid;
    }

    public void setHrfid(int hrfid) {
        this.hrfid = hrfid;
    }

    public HODateTime getPromotionDate() {
        return promotionDate;
    }

    public void setPromotionDate(HODateTime promotionDate) {
        this.promotionDate = promotionDate;
    }

    public void addComment(YouthTrainerComment comment) {
        var count = this.getTrainerComments().stream()
                .filter(c -> c.getYouthPlayerId() == this.id &&
                        c.getYouthMatchId() == comment.getYouthMatchId() &&
                        c.getIndex() == comment.getIndex())
                .count();
        if (count == 0) this.trainerComments.add(comment);
    }

    private List<YouthTrainerComment> getTrainerComments() {
        if (this.trainerComments == null) {
            this.trainerComments = DBManager.instance().loadYouthTrainerComments(this.id);
        }
        return this.trainerComments;
    }

    /**
     * Get player's full name
     * @return String
     */
    public String getFullName() {
        var ret = this.getFirstName();
        if (!ret.isEmpty() && !this.getNickName().isEmpty()) {
            ret += " '" + this.getNickName() + "'";
        }
        if (!ret.isEmpty() && !this.getLastName().isEmpty()) {
            ret += " " + this.getLastName();
        }
        return ret;
    }

    /**
     * The skill ids relevant for youth player training.
     */
    public static PlayerSkill[] skillIds = {PlayerSkill.KEEPER, PlayerSkill.DEFENDING, PlayerSkill.PLAYMAKING, PlayerSkill.WINGER, PlayerSkill.PASSING, PlayerSkill.SCORING, PlayerSkill.SETPIECES};

    /**
     * Set the youth skill info
     * @param skillinfo YouthSkillInfo
     */
    public void setSkillInfo(YouthSkillInfo skillinfo) {
        this.currentSkills.put(skillinfo.getSkillID(), skillinfo);
    }

    /**
     * Get the training development resulting from past trainings.
     * @return Treemap of training date mapping to training development entry
     */
    public TreeMap<HODateTime, YouthTrainingDevelopmentEntry> getTrainingDevelopment() {
        if (trainingDevelopment == null) {
            calcTrainingDevelopment();
        }
        return trainingDevelopment;
    }

    /**
     * Calculate the player's training development and store current skills in the database.
     * Player's potential based on prior calculation is reset.
     * Player's training of last match is set.
     */
    public void calcTrainingDevelopment() {
        this.potential=null;    // trigger potential recalc
        // init from models match list
        trainingDevelopment = new TreeMap<>();
        var model = HOVerwaltung.instance().getModel();
        var teamId = model.getBasics().getYouthTeamId();

        // set start skill values (they may be edited by the user)
        var skills = getStartSkills();
        var trainings = model.getYouthTrainingsAfter(this.getArrivalDate());
        for (var training : trainings) {
            var keeper = skills.areKeeperSkills();
            if (keeper != null) {
                skills.setPlayerMaxSkills(keeper);
                this.currentSkills.setPlayerMaxSkills(keeper);
            }
            var team = training.getTeam(teamId);
            if (team != null && team.hasPlayerPlayed(this.id)) {
                var trainingEntry = new YouthTrainingDevelopmentEntry(this, training);
                var oldSkills = skills;
                skills = trainingEntry.calcSkills(skills, getSkillsAt(training.getMatchDate()), team);
                progressLastMatch = skills.getSkillSum() - oldSkills.getSkillSum();
                trainingEntry.setInjuryLevel(getInjuryLevelAt(training.getMatchDate()));
                trainingEntry.setIsSuspended(isSuspendedAt(training.getMatchDate()));
                trainingDevelopment.put(training.getMatchDate(), trainingEntry);
            } else {
                progressLastMatch = 0;
            }
        }
        this.currentSkills = skills;
        DBManager.instance().storeYouthPlayer(this.hrfid, this);
    }

    /**
     * Skills info at player's arrival date are reconstructed.
     * @return youth skills info
     */
    private YouthSkillsInfo getStartSkills() {
        YouthSkillsInfo startSkills = new YouthSkillsInfo();
        for (var skill : this.currentSkills.values()) {
            var startSkill = new YouthSkillInfo(skill.getSkillID());
            var startValue = skill.getStartValue();
            startSkill.setCurrentValue(startValue);
            startSkill.setStartLevel(skill.getStartLevel());
            startSkill.setCurrentLevel(skill.getStartLevel());
            // check if current value was adjusted
            var adjustment = startSkill.getCurrentValue() - startValue; // Levels may raise current value
            startSkill.setStartValue(startValue + adjustment);
            skill.setStartValue(startSkill.getStartValue());
            startSkill.setMax(skill.getMax());
            startSkill.setIsTop3(skill.isTop3());
            startSkills.put(startSkill.getSkillID(), startSkill);
        }
        return startSkills;
    }

    /**
     * Cache of old skills info
     */
    private HODateTime playerDevelopmentDate;
    private YouthPlayer oldPlayerInfo;

    /**
     * Get player's state at given date
     * Skills are loaded from database, if not given in cache.
     *
     * @param date date of the requested player info
     * @return youth player state at date
     */
    private YouthPlayer getOldPlayerInfo(HODateTime date){
        if ( playerDevelopmentDate == null || !date.equals(playerDevelopmentDate)){
            playerDevelopmentDate = date;
            oldPlayerInfo = DBManager.instance().loadYouthPlayerOfMatchDate(this.id, date.toDbTimestamp());
        }
        return oldPlayerInfo;
    }

    /**
     * Get player's skills after match at given date
     * the skills needs to be cloned, since they would be manipulated during calculation of training
     * @param date timestamp
     * @return youth player's skills
     */
    private YouthSkillsInfo getSkillsAt(HODateTime date) {
        if (!date.equals(this.youthMatchDate)) {
            var oldPlayerInfo = getOldPlayerInfo(date);
            if (oldPlayerInfo != null) {
                getKnownMaxValues(oldPlayerInfo.currentSkills);
                return (YouthSkillsInfo) oldPlayerInfo.currentSkills.clone();
            } else {
                var ret = getStartSkills();
                for (var entry : this.trainingDevelopment.entrySet()) {
                    if (entry.getKey() == date) {
                        return (YouthSkillsInfo) ret.clone();
                    }
                    ret = entry.getValue().getSkills();
                }
                getKnownMaxValues(ret);
                return (YouthSkillsInfo) ret.clone();
            }
        }
        return (YouthSkillsInfo) this.currentSkills.clone();
    }

    /**
     * Get the the known maximum skill values from the current skills.
     * @param skills The skills, where the maximum values are set.
     */
    private void getKnownMaxValues(YouthSkillsInfo skills) {
        for ( var currentSkill : this.currentSkills.values()){
            if ( currentSkill.getMax() != null ){
                skills.get(currentSkill.getSkillID()).setMax(currentSkill.getMax());
            }
        }
    }

    /**
     * Get player's skills before match at given date
     * @param date timestamp
     * @return youth player's skills
     */
    private YouthSkillsInfo getSkillsBefore(HODateTime date) {
        var ret = getStartSkills();
        for (var entry : this.trainingDevelopment.entrySet()) {
            if (entry.getKey().isBefore(date)) {
                ret = entry.getValue().getSkills();
            } else {
                break;
            }
        }
        return ret;
    }

    /**
     * Get player's injury level at given date
     * @param date timestamp
     * @return injury level
     */
    private int getInjuryLevelAt(HODateTime date){
        var oldPlayerInfo = getOldPlayerInfo(date);
        if ( oldPlayerInfo != null) return oldPlayerInfo.getInjuryLevel();
        return this.injuryLevel;
    }

    /**
     * Get player's suspension at given date
     * @param date timestamp
     * @return boolean
     */
    private boolean isSuspendedAt(HODateTime date){
        var oldPlayerInfo = getOldPlayerInfo(date);
        if ( oldPlayerInfo != null) return oldPlayerInfo.isSuspended();
        return this.isSuspended();

    }

    public boolean isSuspended() {
        return this.cards == 3;
    }

    /**
     * Recalculate skills since given date
     * @param since timestamp
     */
    public void recalcSkills(HODateTime since) {
        if (trainingDevelopment != null) {
            var startSkills = getSkillsBefore(since);
            for (var entry : this.trainingDevelopment.tailMap(since, true).values()) {
                var team = entry.getTraining().getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());
                var newSkills = entry.calcSkills(startSkills, getSkillsAt(entry.getMatchDate()), team);
                progressLastMatch = newSkills.getSkillSum() - startSkills.getSkillSum();
                startSkills = newSkills;
            }
        }
    }

    private boolean isAdjusting;
    /**
     * Skill start value is incremented by value adjustment. Change is propagated through all existing
     * training development entries until given before date
     *  @param skillID    skill id
     * @param adjustment change of the start value
     * @param before adjust training development before given date
     */
    public double adjustSkill(PlayerSkill skillID, double adjustment, HODateTime before) {
        // start skills are examined from current skill infos
        var currentSkill = getSkillInfo(skillID);
        if (!isAdjusting) {
            isAdjusting=true;
            currentSkill.addStartValue(adjustment);
            if (currentSkill.getCurrentValue() < currentSkill.getStartValue()) {
                currentSkill.setCurrentValue(currentSkill.getStartValue());
            }
            if (trainingDevelopment != null && !trainingDevelopment.isEmpty()) {
                var youthteamId = HOVerwaltung.instance().getModel().getBasics().getYouthTeamId();
                var skills = getStartSkills();
                for (var trainingEntry : trainingDevelopment.values()) {
                    if (!trainingEntry.getMatchDate().isBefore(before)) break; // stop if before date is reached
                    var constraints = trainingEntry.getSkills(); // constraints (levels) are not changed
                    // this calculates all skill Ids, not only the requested one (this is a kind of overhead, i accept)
                    skills = trainingEntry.calcSkills(skills, constraints, trainingEntry.getTraining().getTeam(youthteamId));
                }
            }
            isAdjusting=false;
        }
        return currentSkill.getStartValue();
    }

    /**
     * Get known player's specialty as string.
     * @return String, empty, of specialty is not known
     */
    public String getSpecialtyString() {
        this.getTrainingDevelopment(); // may add specialties from highlights
        if (this.specialty != Specialty.NoSpecialty) {
            return HOVerwaltung.instance().getLanguageString("ls.player.speciality." + this.specialty.toString().toLowerCase());
        }
        return "";
    }

    public double getTrainedSkillSum() {
        return this.currentSkills.getTrainedSkillSum();
    }

    public int getMatchCount() {
        return this.getTrainingDevelopment().size();
    }

    public double getProgressLastMatch() {
        return progressLastMatch;
    }

    /**
     * Get player's age at given date.
     * @param t HODateTime, thw date
     * @return int, player's age in years.
     */
    public int getAgeYearsAtDate(HODateTime t) {
        var hrfTime = HOVerwaltung.instance().getModel().getBasics().getDatum();
        var diff = HODateTime.HODuration.between(hrfTime, t);
        return new HODateTime.HODuration(this.getAgeYears(), this.getAgeDays()).plus(diff).seasons;
    }

    public YouthSkillsInfo getCurrentSkills() {
        return this.currentSkills;
    }

    /**
     * Get the player's skill development as double array used by the line chart display.
     * It consists of the development of past trainings and the estimated development potential of future trainings.
     * @param skillId Player skill id
     * @return double array of skill values
     */
    public double[] getSkillDevelopment(PlayerSkill skillId) {
        var trainingDevelopment = getTrainingDevelopment();
        var ret = new double[max(1, trainingDevelopment.size()) + futureTrainings.size()];
        int i=0;
        var value = 0.;
        if (!trainingDevelopment.isEmpty()) {
            for (var t : trainingDevelopment.values()) {
                var skills = t.getSkills();
                var skill = skills.get(skillId);
                value = skill.getCurrentValue();
                ret[i++] = value;
            }
        }
        else {
            value = getSkillInfo(skillId).getCurrentValue();
            ret[i++] = value;
        }
        for ( var futureTrainingValue : this.futureTrainings.values()){
            if (futureTrainingValue.getKey() == skillId){
                value = futureTrainingValue.getValue();
            }
            ret[i++] = value;
        }
        return ret;
    }

    /**
     * Get the corresponding dates of the player's skill development.
     * It consists of the development of past trainings and the estimated development potential of future trainings.
     * @return double array of the dates (milliseconds since epoch)
     */
    public double[] getSkillDevelopmentDates() {
        var trainingDevelopment = getTrainingDevelopment();
        var ret = new double[max(1, trainingDevelopment.size()) + futureTrainings.size()];
        int i = 0;
        if (!trainingDevelopment.isEmpty()) {
            for (var t : trainingDevelopment.keySet()) {
                ret[i++] = Date.from(t.instant).getTime();
            }
        }
        else {
            ret[i++] = Date.from(Instant.now()).getTime();
        }
        for ( var futureTrainingKey : futureTrainings.keySet()){
            ret[i++] = Date.from(futureTrainingKey.instant).getTime();
        }
        return ret;
    }

    /**
     * Set the current level of the given skill
     * @param skillId Player skill id
     * @param v Integer skill level value of the skill
     */
    public void setCurrentLevel(PlayerSkill skillId, Integer v) {
        initSkillInfo();
        this.currentSkills.get(skillId).setCurrentLevel(v);
    }

    /**
     * Set the reachable maximum skill level of the given skill
     * @param skillId Player skill id
     * @param v Integer maximum skill level value of the skill
     */
    public void setMax(PlayerSkill skillId, Integer v) {
        initSkillInfo();
        this.currentSkills.get(skillId).setMax(v);
    }

    /**
     * Set the start skill level of the given skill
     * @param skillId Player skill id
     * @param v Integer start skill level value of the skill
     */
    public void setStartLevel(PlayerSkill skillId, Integer v) {
        initSkillInfo();
        this.currentSkills.get(skillId).setStartLevel(v);
    }

    /**
     * Set the maximum skill value is reached of the given skill
     * @param skillId Player skill id
     * @param v boolean
     */
    public void setIsMaxReached(PlayerSkill skillId, boolean v) {
        initSkillInfo();
        this.currentSkills.get(skillId).setMaxReached(v);
    }

    /**
     * Set the current skill value of the given skill
     * @param skillId Player skill id
     * @param v double current skill value of the skill
     */
    public void setCurrentValue(PlayerSkill skillId, double v) {
        initSkillInfo();
        this.currentSkills.get(skillId).setCurrentValue(v);
    }

    /**
     * Set the start skill value of the given skill
     * @param skillId Player skill id
     * @param v double start skill value of the skill
     */
    public void setStartValue(PlayerSkill skillId, double v) {
        initSkillInfo();
        this.currentSkills.get(skillId).setStartValue(v);
    }

    /**
     * Tag the given skill as top 3 skill
     * @param skillId Player skill id
     * @param v Boolean
     */
     public void setIsTop3(PlayerSkill skillId, Boolean v) {
        initSkillInfo();
        this.currentSkills.get(skillId).setIsTop3(v);
    }

    public static class ScoutComment extends AbstractTable.Storable {
        private int youthPlayerId;
        private int index;
        private String text;
        private CommentType type;
        private Integer variation;
        private ScoutCommentSkillTypeID skillType;
        private Integer skillLevel;

        public ScoutComment() {
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public CommentType getType() {
            return type;
        }

        public void setType(CommentType type) {
            this.type = type;
        }

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

    /**
     * Create a youth player from downloaded information.
     *
     * @param properties extracted from chpp-xml file
     */
    public YouthPlayer(Properties properties) {

        id = getInt(properties, "id", 0);
        firstName = properties.getProperty("firstname", "");
        nickName = properties.getProperty("nickname", "");
        lastName = properties.getProperty("lastname", "");
        ageYears = getInt(properties, "age", 0);
        ageDays = getInt(properties, "agedays", 0);
        arrivalDate = HODateTime.fromHT(properties.getProperty("arrivaldate", ""));
        canBePromotedIn = getInt(properties, "canbepromotedin", 0);
        playerNumber = properties.getProperty("playernumber", "");
        statement = properties.getProperty("statement", "");
        ownerNotes = properties.getProperty("ownernotes", "");
        playerCategoryID = getInt(properties, "playercategoryid", 0);
        cards = getInt(properties, "cards", 0);
        injuryLevel = getInt(properties, "injurylevel", 0);
        specialty = Specialty.getSpecialty(getInt(properties, "specialty", 0));
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
        youthMatchDate = HODateTime.fromHT(properties.getProperty("youthmatchdate"));

        var playerStatusPreviousDownload = HOVerwaltung.instance().getModel().getCurrentYouthPlayer(id);
        for (var skillId : YouthPlayer.skillIds) {
            var skillinfo = parseSkillInfo(properties, skillId);
            if (playerStatusPreviousDownload != null) {
                var prevSkills = playerStatusPreviousDownload.getSkillInfo(skillId);
                skillinfo.setCurrentValue(prevSkills.getCurrentValue());
                skillinfo.setStartValue(prevSkills.getStartValue());
            }
            this.setSkillInfo(skillinfo);
        }
        parseScoutComments(properties);
        evaluateScoutComments();
    }

    /**
     * Initialize the player's current skills.
     * (The skill values itself will remain uninitialized)
     */
    private void initSkillInfo() {
        if (this.currentSkills.isEmpty()) {
            for (var skillId : YouthPlayer.skillIds) {
                var skillInfo = new YouthSkillInfo(skillId);
                this.setSkillInfo(skillInfo);
            }
        }
    }

    /**
     * Parse the scout comments from properties.
     * The comments of the scout at player's arrival are initialized.
     * @param properties Properties
     */
    private void parseScoutComments(Properties properties) {
        this.scoutComments = new ArrayList<>();
        for (int i=0; true; i++ ) {
            var prefix = "scoutcomment" + i;
            var text = properties.getProperty(prefix + "text");
            if (text != null) {
                var scoutComment = new ScoutComment();
                scoutComment.text = properties.getProperty(prefix + "text", "");
                scoutComment.type = CommentType.valueOf(getInteger(properties, prefix + "type"));
                scoutComment.variation = getInteger(properties, prefix + "variation");
                if (scoutComment.type == CommentType.AVERAGE_SKILL_LEVEL) {
                    // Average comment stores skill level in skillType
                    scoutComment.skillType = ScoutCommentSkillTypeID.AVERAGE;
                    scoutComment.skillLevel = getInteger(properties, prefix + "skilltype");
                } else {
                    scoutComment.skillType = ScoutCommentSkillTypeID.valueOf(getInteger(properties, prefix + "skilltype"));
                    scoutComment.skillLevel = getInteger(properties, prefix + "skilllevel");
                }
                this.scoutComments.add(scoutComment);
            }
            else {
                // No more scout comment found
                return;
            }
        }
    }

    /**
     * Parse the skill information of player skill from properties
     * @param properties Properties
     * @param skillID Player skill id
     * @return YouthSkillInfo
     */
    private YouthSkillInfo parseSkillInfo(Properties properties, PlayerSkill skillID) {
        var skill = skillID.getXMLElementName().toLowerCase() + "skill";
        var skillInfo = new YouthSkillInfo(skillID);
        skillInfo.setCurrentLevel(getInteger(properties, skill));
        skillInfo.setMax(getInteger(properties, skill + "max"));
        skillInfo.setMaxReached(getBoolean(properties, skill + "ismaxreached", false));
        this.currentSkills.put(skillID, skillInfo);
        return skillInfo;
    }

    /**
     * Get a integer value from properties.
     * @param p Properties
     * @param key Key
     * @return Integer the value of the key, if it is available in the properties, and it could be parsed as integer.
     *          Null if not.
     */
    private Integer getInteger(Properties p, String key) {
        try {
            var s = p.getProperty(key);
            if (s != null && !s.isEmpty()) return Integer.parseInt(s);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Get a boolean value from properties.
     * @param p Properties
     * @param key Key
     * @param defaultValue Default
     * @return boolean the value of the key, if it is available in the properties and it could be parsed as boolean.
     *          defaultValue if not.
     */
    private boolean getBoolean(Properties p, String key, boolean defaultValue) {
        try {
            var s = p.getProperty(key);
            if (s != null && !s.isEmpty()) return Boolean.parseBoolean(s);
        } catch (Exception e) {
            HOLogger.instance().warning(getClass(), "getBoolean: " + e);
        }
        return defaultValue;
    }

    /**
     * Get a integer value from properties.
     * @param p Properties
     * @param key Key
     * @param defaultValue Default
     * @return int the value of the key, if it is available in the properties, and it could be parsed as integer.
     *          defaultValue if not.
     */
    private int getInt(Properties p, String key, int defaultValue) {
        try {
            var s = p.getProperty(key);
            if (s != null && !s.isEmpty()) return Integer.parseInt(s);
        } catch (Exception e) {
            HOLogger.instance().warning(getClass(), "getInt: " + e);
        }
        return defaultValue;
    }

    /**
     * Get a double value from properties.
     * @param p Properties
     * @param key Key
     * @return Double the value of the key, if it is available in the properties, and it could be parsed as double.
     *          Null if not.
     */
    private Double getDouble(Properties p, String key) {
        try {
            var s = p.getProperty(key);
            if (s != null && !s.isEmpty()) return Double.parseDouble(s);
        } catch (Exception ignored) {
        }
        return null;
    }

    private Boolean _isOverallSkillsLevelAvailable;

    /**
     * Is overall skills level available.
     * Overall skills level can be given in the scout's comment.
     * @return True, if overall skills is known.
     */
    public boolean isOverallSkillsLevelAvailable(){
        if (_isOverallSkillsLevelAvailable == null) {
            var sc = this.getScoutComments().stream()
                    .filter(i -> i.type == CommentType.AVERAGE_SKILL_LEVEL)
                    .findFirst()
                    .orElse(null);
            _isOverallSkillsLevelAvailable = sc != null;
            if (_isOverallSkillsLevelAvailable) {
                overallSkillsLevel = sc.getSkillLevel();
            }
        }
        return _isOverallSkillsLevelAvailable;
    }

    private int overallSkillsLevel;

    /**
     * Get the allrounder skill level
     * @return Integer The allrounder skill level, null if unknown
     */
    public Integer getOverallSkillsLevel() {
        if (isOverallSkillsLevelAvailable()) return overallSkillsLevel;
        return null;
    }

    /**
     * Get the player's overall skills level given by the scout.
     * @return String
     *          overall skills level
     *          empty, if no overall skills was given by the scout
     */
    public String getOverallSkillsLevelAsString() {
        var stringBuilder = new StringBuilder();
        if (isOverallSkillsLevelAvailable()) {
            stringBuilder.append(overallSkillsLevel).append(" ");
        }
        stringBuilder.append("(").append(String.format("%.2f", this.currentSkills.calculateMinimumOverallSkillsLevel()))
                .append(")");
        return stringBuilder.toString();
    }

    private Integer potential;

    /**
     * Get a player's potential number (sum of normed maximum skill values at player's age of 17 years).
     * The maximum reachable skill values are divided by a norming factor representing the training speed of the skills.
     *
     * @return int potential number
     */
    public int getPotential()
    {
        if ( potential == null){
            calcMaxSkills17();
            double p = 0d;
            for ( var skillId: skillIds){
                p += max(0, getSkillInfo(skillId).getPotential17Value() - YouthSkillInfo.UsefulTrainingThreshold) / YouthTraining.potentialNormingFactor.get(skillId);
            }
            potential = (int)Math.round(p);
        }
        return potential;
    }

    /**
     * Calculate the maximum reachable skills
     */
    private void calcMaxSkills17() {
        if (this.getAgeYears() > 16) {
            // the player is older than 17 years old
            // Find skill values at age of 17,0
            YouthSkillsInfo skill17 = null;
            // scan training development
            if (!this.getTrainingDevelopment().isEmpty()) {
                for (var entry : this.getTrainingDevelopment().values()) {
                    // find skill when player's age skipped to 17
                    skill17 = entry.getSkills();
                    if (entry.getPlayerAgeYears() > 16) {
                        break;
                    }
                }
            }
            // if no training development was found
            if (skill17 == null) {
                // use current skill value. Player was never trained
                skill17 = this.currentSkills;
            }
            // Set skill potential values of each skill
            for (var skill : skill17.values()) {
                this.getSkillInfo(skill.getSkillID()).setPotential17Value(skill.getCurrentValue());
            }
        } else {
            var trainingContext = new YouthTrainingContext(this);
            Comparator<YouthSkillInfo> trainingUsefulnessComparator = (i1, i2) -> getTrainingUsefulness(i2).compareTo(getTrainingUsefulness(i1));
            var trainingPlan = this.currentSkills.values().stream().sorted(trainingUsefulnessComparator).toList();
            for (var t : trainingPlan) {
                calculatePotential17Value(t, trainingContext);
            }
            this.futureTrainings = trainingContext.futureTrainings;
        }
    }

    /**
     * Calculate the effect of training skill s until either age of 17 years or the maximum skill level is reached.
     * @param s Youth skill info
     * @param trainingContext Current training context of the calculation
     */
    private void calculatePotential17Value(YouthSkillInfo s, YouthTrainingContext trainingContext) {
        var skillLimit = 8.3;
        if (s.isMaxAvailable()){
            skillLimit = min(8.3, s.getMax() + .99);
        }
        else {
            if (s.isTop3() != null) {
                if (!s.isTop3()) {
                    if (skillLimit > trainingContext.minimumTop3SkillPotential + .99) {
                        skillLimit = min(8.3, trainingContext.minimumTop3SkillPotential + .99);
                    }
                }
            } else {
                if (trainingContext.numberOfKnownTop3Skills == 3) {
                    if (skillLimit > trainingContext.minimumTop3SkillPotential + .99) {
                        skillLimit = min(8.3, trainingContext.minimumTop3SkillPotential + .99);
                    }
                } else {
                    trainingContext.numberOfKnownTop3Skills++;
                }
            }
        }
        // init max
        var max = s.getCurrentValue();
        while (trainingContext.age < 17 && max < skillLimit) {
            var increment = YouthTraining.getMaxTrainingPerWeek(s.getSkillID(), (int) max, trainingContext.age);
            if (isOverallSkillsLevelAvailable() && (!s.isMaxAvailable() || max > s.getMax())) {
                // check if overall skills level is reached.
                s.setPotential17Value(max+increment);
                var minOverallSkillsLevel = this.currentSkills.calculateMinimumOverallSkillsLevel();
                if (minOverallSkillsLevel >= getOverallSkillsLevel()) {
                    break;
                }
            }

            //  weekly increment of the skill
            max += increment;
            // player's age of next week
            trainingContext.days += 7;
            if (trainingContext.days > 111) {
                trainingContext.days -= 112;
                trainingContext.age++;
            }
            if (max > skillLimit) {
                max = skillLimit;
            }
            trainingContext.addFutureTraining(s.getSkillID(), max);
        }
        s.setPotential17Value(max);
    }

    /**
     * Get a value describing the usefulness of training the given skill
     * It is a weighted sum of current and maximum skill values.
     * If player's age is 15.0 the maximum skill is weighted with 1 (100%). At age of 17.0 maximum skill gets weight 0.
     * The current skill value gets weight 0 at age of 15 and 2 (200%) at age of 17.
     * Above 17 zero is returned.
     *
     * @param skillInfo Skill info
     * @return Integer, the returned value is a weighted sum of current and maximum skill value
     */
    private Integer getTrainingUsefulness(YouthSkillInfo skillInfo) {
        if (this.ageYears < 17 && skillInfo.isTrainingUsefull()) {
            var trainingDaysUntil17 = (17-this.ageYears)*112 - this.ageDays;
            var factorMax = trainingDaysUntil17/2.24;       // [15.000:100%..17.000:0%]
            var factorCurrent = (200-factorMax);     // [15:100..17:200]
            var max = 8.3;
            if (skillInfo.isMaxAvailable() && skillInfo.getMax() < 8) max = skillInfo.getMax() + .99;
            max *= factorMax;
            return (int) (max + factorCurrent*skillInfo.getCurrentValue());
        }
        return 0;
    }
}