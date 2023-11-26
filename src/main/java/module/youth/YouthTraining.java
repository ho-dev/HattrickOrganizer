package module.youth;

import core.constants.player.PlayerSkill;
import core.db.AbstractTable;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.enums.MatchType;
import core.model.match.*;
import core.util.HODateTime;
import java.util.Map;
import java.util.Vector;

public class YouthTraining extends AbstractTable.Storable {

    public void setYouthMatchId(int v) {
        this.youthMatchId = v;
    }
    public void setYouthMatchType(MatchType v) {
        this.youthMatchType = v;
    }

    public enum Priority {
        Primary,
        Secondary
    }
    private MatchLineup matchLineup;
    private Matchdetails matchdetails;
    private int youthMatchId;
    private MatchType youthMatchType;
    private final YouthTrainingType[] training = new YouthTrainingType[2];

    private void setMatchLineup(MatchLineup youthMatch) {
        this.youthMatchId = youthMatch.getMatchID();
        this.youthMatchType = youthMatch.getMatchType();
        this.matchLineup = youthMatch;
    }

    public YouthTraining(MatchLineup lineup) {
        setMatchLineup(lineup);
    }

    /**
     * constructor is used by AbstractTable
     */
    public YouthTraining(){}

    public YouthTrainingType getTraining(Priority p) {
        return training[p.ordinal()];
    }

    public void setTraining(Priority p, YouthTrainingType trainingType) {
        this.training[p.ordinal()] = trainingType;
    }

    public void recalcSkills() {
        var team = this.getMatchLineup().getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());
        var lineup = team.getLineup();
        var allActivePlayers = new Vector<MatchLineupPosition>();
        allActivePlayers.addAll(lineup.getFieldPositions());
        allActivePlayers.addAll(lineup.getReplacedPositions());
        for (var matchRoleId : allActivePlayers) {
            recalcSkills(matchRoleId.getPlayerId());
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

    public int getYouthMatchId() {
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

    public HODateTime getMatchDate() {
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

    /**
     * Training friendly factor before season 86
     */
    static final double TRAINING_FRIENDLY_FACTOR_BEFORE_8601 = .5;

    /**
     * With <a href="https://www88.hattrick.org/World/News/?messageId=8641&isEditorial=1">...</a>
     * a new training speed of youth friendly matches was introduced.
     */
    static final double TRAINING_FRIENDLY_FACTOR = .9;

    public double getMatchTypeFactor() {
        var lineup = this.getMatchLineup();
        if (lineup.getMatchType() == MatchType.YOUTHLEAGUE) {
            return 1.;
        }
        if (lineup.getMatchDate().isAfter(HODateTime.fromHTWeek(new HODateTime.HTWeek(86,1)))){
            return TRAINING_FRIENDLY_FACTOR;
        }
        return TRAINING_FRIENDLY_FACTOR_BEFORE_8601;
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

    /**
     * leage match training per week : 1
     * friendly match training per week : 1/3
     */
    static final double fullTrainingsPerWeek = 1 + TRAINING_FRIENDLY_FACTOR / 3.;
    static final double efficiencyOfEqualPrimaryAndSecondaryTraining = 1.33;

    /**
     * Calc most effective training for given skill id
     * training rate is 6 league matches/training in 42 days
     * plus             1 friendly match in 21 days
     *
     * @param skillId skill id
     * @param skillVal skill level value
     * @param age player age in years
     * @return maximum skill increment with optimal training
     */
    public static double getMaxTrainingPerWeek(PlayerSkill skillId, int skillVal, int age) {
        var f = efficiencyOfEqualPrimaryAndSecondaryTraining * fullTrainingsPerWeek * 90.;

        // TODO check if shooting as secondary training is more effective
        // TODO check if defending position as secondary training is more effective
        // TODO check if wing attack as secondary training is more effective
        // TODO check if through passes as secondary training is more effective
        // TODO check if shooting as secondary training is more effective
        return switch (skillId) {
            case KEEPER -> YouthTrainingType.Goalkeeping.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * f;
            case PLAYMAKING -> YouthTrainingType.Playmaking.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * f;
            case SETPIECES -> YouthTrainingType.SetPieces.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * f;
            case DEFENDING -> YouthTrainingType.Defending.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * f;
            case WINGER -> YouthTrainingType.Winger.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * f;
            case PASSING -> YouthTrainingType.Passing.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * f;
            case SCORING -> YouthTrainingType.Scoring.calcSkillIncrementPerMinute(skillId, skillVal, 1, age) * f;
            default -> 0;
        };
    }

    public static Map<PlayerSkill, Double> potentialNormingFactor = Map.of(
            PlayerSkill.DEFENDING, getMaxTrainingPerWeek(PlayerSkill.DEFENDING, 7, 17),
            PlayerSkill.KEEPER, getMaxTrainingPerWeek(PlayerSkill.KEEPER, 7, 17),
            PlayerSkill.PASSING, getMaxTrainingPerWeek(PlayerSkill.PASSING, 7, 17),
            PlayerSkill.SETPIECES, getMaxTrainingPerWeek(PlayerSkill.SETPIECES, 7, 17),
            PlayerSkill.SCORING, getMaxTrainingPerWeek(PlayerSkill.SCORING, 7, 17),
            PlayerSkill.PLAYMAKING, getMaxTrainingPerWeek(PlayerSkill.PLAYMAKING, 7, 17),
            PlayerSkill.WINGER, getMaxTrainingPerWeek(PlayerSkill.WINGER, 7, 17)
    );

    public double getRating(int playerId) {
        var player = this.getTeam(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId()).getPlayerByID(playerId);
        if ( player != null ) return player.getRating();
        return 0;
    }

}