package core.model.player;

import module.opponentspy.CalcVariables;
import module.training.Skills;

import java.sql.Timestamp;
import java.util.*;

import static core.util.Helper.parseDate;

public class YouthPlayer {
    private final int id;
    private final String firstName;
    private final String nickName;
    private final String lastName;
    private final int ageYears;
    private final int ageDays;
    private final Timestamp arrivalDate;
    private final int canBePromotedIn;
    private final String playerNumber;
    private final String statement;
    private final String ownerNotes;
    private final int playerCategoryID;
    private final int cards;
    private final int injuryLevel;
    private final int specialty;
    private final int careerGoals;
    private final int careerHattricks;
    private final int leagueGoals;
    private final int friendlyGoals;
    private final int scoutId;
    private final int scoutingRegionID;
    private final String scoutName;
    private final int youthMatchID;
    private final int positionCode;
    private final int playedMinutes;
    private final int rating;
    private final Timestamp youthMatchDate;

    private Map<Integer, SkillInfo> skillInfoMap = new HashMap<>();
    private List<ScoutComment> scoutComments = new ArrayList<>();

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
