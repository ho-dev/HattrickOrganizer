package module.youth;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.enums.MatchType;
import core.model.match.*;
import core.model.player.MatchRoleID;
import module.lineup.substitution.model.MatchOrderType;
import module.training.Skills;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YouthTraining {

    public enum Priority {
        Primary,
        Secondary
    }

    private static double[] trainingPrioFactor = {
            UserParameter.instance().youthtrainingFactorPrimary,
            UserParameter.instance().youthtrainingFactorSecondary
    };

    private MatchLineup matchLineup;
    private Matchdetails matchdetails;
    private int youthMatchId;
    private MatchType youthMatchType;
    private YouthTrainingType[] training = new YouthTrainingType[2];
    private List<YouthTrainerComment> commentList = new ArrayList<>();

    public YouthTraining(int youthMatchId, MatchType matchType) {
        this.youthMatchId = youthMatchId;
        this.youthMatchType = matchType;
    }

    private void setMatchLineup(MatchLineup youthMatch) {
        this.youthMatchId = youthMatch.getMatchID();
        this.youthMatchType = youthMatch.getMatchType();
        this.matchLineup = youthMatch;
    }

    public YouthTraining(MatchLineup lineup) {
        setMatchLineup(lineup);
    }

    public YouthTrainingType getTraining(Priority p) {
        return training[p.ordinal()];
    }

    public void setTraining(Priority p, YouthTrainingType trainingType) {
        this.training[p.ordinal()] = trainingType;
    }

    public void recalcSkills() {
        var team = this.getMatchLineup().getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());

        for (var matchRoleId : team.getLineup().getFieldPositions()) {
            var player = (MatchRoleID)matchRoleId;
            recalcSkills(player.getPlayerId());
        }
        for (var subs : team.getSubstitutions()) {
            if (subs.getOrderType().equals(MatchOrderType.SUBSTITUTION)) {
                recalcSkills(subs.getObjectPlayerID());
            }
        }
    }

    private void recalcSkills(int playerId) {
        var since = this.matchLineup.getMatchDate();
        var p = HOVerwaltung.instance().getModel().getCurrentYouthPlayer(playerId);
        if (p != null) {
            p.recalcSkills(since);
        }
    }

    public void store() {
        DBManager.instance().storeYouthTraining(this);
    }

    public List<YouthTrainerComment> getCommentList() {
        return commentList;
    }

    public void addComment(YouthTrainerComment comment) {
        commentList.add(comment);
        var youthplayerID = comment.getYouthPlayerId();
        var team = matchLineup.getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());

        var player = HOVerwaltung.instance().getModel().getCurrentYouthPlayer(youthplayerID);
        player.addComment(comment);
    }

    public int getMatchId() {
        return this.youthMatchId;
    }

    public MatchLineupTeam getTeam(Integer youthTeamId) {
        return this.getMatchLineup().getTeam(youthTeamId);
    }

    MatchLineup getMatchLineup() {
        if (this.matchLineup == null) {
            this.matchLineup = DBManager.instance().loadMatchLineup(this.getMatchType().getId(), this.youthMatchId);
        }
        return this.matchLineup;
    }

    public Matchdetails getMatchDetails() {
        if (this.matchdetails == null) {
            this.matchdetails = DBManager.instance().loadMatchDetails(this.getMatchType().getId(), this.youthMatchId);
        }
        return this.matchdetails;
    }

    public Timestamp getMatchDate() {
        return this.getMatchDetails().getMatchDate();
    }

    public String getHomeTeamName() {
        return this.getMatchDetails().getHomeTeamName();
    }

    public String getGuestTeamName() {
        return this.getMatchDetails().getGuestTeamName();
    }

    public MatchType getMatchType() {
        return this.youthMatchType;
    }

    public YouthSkillInfo calcSkill(YouthSkillInfo value, YouthPlayer player, MatchLineupTeam team) {

        var ret = new YouthSkillInfo(value.getSkillID());
        ret.setStartValueRange(value.getStartValueRange());
        ret.setMax(value.getMax());
        ret.setIsTop3(value.isTop3());
        // Current level could be changed, correct value is set in setConstraint
        //ret.setCurrentLevel(value.getCurrentLevel());
        ret.setStartLevel(value.getStartLevel());
        ret.setMaxReached(value.isMaxReached());
        if (value.isMaxReached()) {
            ret.setCurrentValue(value.getCurrentValue());
        } else {
            var newVal = value.getCurrentValue() + calcSkillIncrement(value, player, team);
            ret.setCurrentValue(newVal);
            var adjustment = ret.getCurrentValue() - newVal;
            if (adjustment != 0) {
                value.setStartValue(player.adjustSkill(value.getSkillID(), adjustment, this.getMatchDate()));
            }
        }
        // Current value needs to be set before start value could be changed
        // (adjustment would reset start value otherwise)
        ret.setStartValue(value.getStartValue());
        return ret;
    }

    private double calcSkillIncrement(YouthSkillInfo value, YouthPlayer player, MatchLineupTeam lineupTeam) {
        double ret = 0;
        var matchTypeFactor = getMatchTypeFactor();
        YouthTrainingType primaryTraining = null;
        // for each youth training
        for (var priority : Priority.values()) {
            var train = training[priority.ordinal()];
            if (train != null) {
                // training type is specified
                var trainingFactor = UserParameter.instance().youthtrainingFactorPrimary;
                if (priority == Priority.Secondary) {
                    // specialty of second training type
                    trainingFactor = UserParameter.instance().youthtrainingFactorSecondary;
                    if (train == primaryTraining) {
                        // reduce efficiency if it is equal to primary training
                        trainingFactor /= 2;
                    }
                }
                if (train != YouthTrainingType.IndividualTraining) {
                    // NOT individual training
                    int totalTrainedMinutes = 0;
                    int posPrio = 0;    // 0=Bonus, 1=Full, 2=Partly, 3=Osmosis sectors
                    for (var prioPositions : train.getTrainedSectors()) {
                        int minutesInPrioPositions = lineupTeam.getTrainingMinutesPlayedInSectors(player.getId(),
                                prioPositions,
                                this.getMatchDetails().isWalkoverMatchWin(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId()));
                        if (minutesInPrioPositions + totalTrainedMinutes > 90) {
                            minutesInPrioPositions = 90 - totalTrainedMinutes;
                        }
                        if (minutesInPrioPositions > 0) {
                            ret += matchTypeFactor *
                                    trainingFactor *
                                    minutesInPrioPositions *
                                    train.calcSkillIncrementPerMinute(value.getSkillID(), (int) value.getCurrentValue(), posPrio, player.getAgeYears());
                        }
                        totalTrainedMinutes += minutesInPrioPositions;
                        if (totalTrainedMinutes == 90) break;
                        posPrio++; // next position priority
                    }
                    if (primaryTraining == null) primaryTraining = train;
                } else {
                    // Calc Individual training
                    var minutesInSectors = lineupTeam.getTrainMinutesPlayedInSectors(player.getId());
                    for ( var s : minutesInSectors.entrySet()){
                        ret += trainingFactor * s.getValue() * train.calcSkillIncrementPerMinute(value.getSkillID(), (int) value.getCurrentValue(), s.getKey(), player.getAgeYears());
                    }
                }
            }
        }
        if (ret > 1) ret = 1; // skill increment is limited
        return ret;
    }

    static final double TRAINING_FRIENDLYFACTOR = .5;

    public double getMatchTypeFactor() {
        if (this.getMatchLineup().getMatchType() == MatchType.YOUTHLEAGUE) {
            return 1.;
        }
        return TRAINING_FRIENDLYFACTOR;
    }

    public String getPlayerTrainedSectors(int playerId) {
        var ret = new StringBuilder();
        var hov = HOVerwaltung.instance();
        var lineupTeam = this.getTeam(hov.getModel().getBasics().getYouthTeamId());
        var sectors = lineupTeam.getTrainMinutesPlayedInSectors(playerId);
        for ( var s : sectors.entrySet()){
            ret.append(hov.getLanguageString("ls.youth.training.sector." + s.getKey()))
                    .append(":")
                    .append(s.getValue())
                    .append(" ");
        }
        return ret.toString();
    }

    static final double fullTrainingsPerWeek = (6 * 21 + 44 * TRAINING_FRIENDLYFACTOR) / (21 * 44) * 7;
    static final double equalTrainings = 1.33;

    /**
     * Calc most effective training for given skill id
     * training rate is 6 league matches/training in 44 days
     * plus             1 friendly match in 21 days
     *
     * @param skillId skill id
     * @param skillVal skill level value
     * @param age player age in years
     * @return maximum skill increment with optimal training
     */
    public static double getMaxTrainingPerWeek(Skills.HTSkillID skillId, int skillVal, int age) {

        // TODO check if shooting as secondary training is more effective
        // TODO check if defending position as secondary training is more effective
        // TODO check if wing attack as secondary training is more effective
        // TODO check if through passes as secondary training is more effective
        // TODO check if shooting as secondary training is more effective
        return switch (skillId) {
            case Keeper -> YouthTrainingType.Goalkeeping.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * equalTrainings * fullTrainingsPerWeek * 90.;
            case Playmaker -> YouthTrainingType.Playmaking.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * equalTrainings * fullTrainingsPerWeek * 90.;
            case SetPieces -> YouthTrainingType.SetPieces.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * equalTrainings * fullTrainingsPerWeek * 90.;
            case Defender -> YouthTrainingType.Defending.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * equalTrainings * fullTrainingsPerWeek * 90.;
            case Winger -> YouthTrainingType.Winger.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * equalTrainings * fullTrainingsPerWeek * 90.;
            case Passing -> YouthTrainingType.Passing.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * equalTrainings * fullTrainingsPerWeek * 90.;
            case Scorer -> YouthTrainingType.Scoring.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * equalTrainings * fullTrainingsPerWeek * 90.;
            default -> 0;
        };
    }

    public static Map<Skills.HTSkillID, Double> potentialNormingFactor = Map.of(
            Skills.HTSkillID.Defender, getMaxTrainingPerWeek(Skills.HTSkillID.Defender, 7, 17),
            Skills.HTSkillID.Keeper, getMaxTrainingPerWeek(Skills.HTSkillID.Keeper, 7, 17),
            Skills.HTSkillID.Passing, getMaxTrainingPerWeek(Skills.HTSkillID.Passing, 7, 17),
            Skills.HTSkillID.SetPieces, getMaxTrainingPerWeek(Skills.HTSkillID.SetPieces, 7, 17),
            Skills.HTSkillID.Scorer, getMaxTrainingPerWeek(Skills.HTSkillID.Scorer, 7, 17),
            Skills.HTSkillID.Playmaker, getMaxTrainingPerWeek(Skills.HTSkillID.Playmaker, 7, 17),
            Skills.HTSkillID.Winger, getMaxTrainingPerWeek(Skills.HTSkillID.Winger, 7, 17)
    );

    public double getRating(int playerId) {
        var player = this.getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId()).getPlayerByID(playerId);
        if ( player != null ) return player.getRating();
        return 0;
    }

}