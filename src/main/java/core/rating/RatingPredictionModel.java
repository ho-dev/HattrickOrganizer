package core.rating;

import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.model.Team;
import core.model.match.IMatchDetails;
import core.model.match.MatchLineupPosition;
import core.model.match.MatchTacticType;
import core.model.match.Weather;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.model.player.Specialty;
import core.util.HOLogger;
import module.lineup.Lineup;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static core.constants.player.PlayerSkill.*;
import static core.model.match.IMatchDetails.*;
import static core.model.player.IMatchRoleID.*;
import static core.model.player.IMatchRoleID.KEEPER;
import static core.model.player.IMatchRoleID.WINGER;
import static java.lang.Math.*;
import static java.util.Map.entry;

public class RatingPredictionModel {



    public enum RatingSector {
        Defence_Left,
        Defence_Central,
        Defence_Right,
        Midfield,
        Attack_Left,
        Attack_Central,
        Attack_Right
    }

    protected enum RatingContributionParameter {
        SideDefence,
        CentralDefence,
        Midfield,
        SideAttack,
        CentralAttack
    }

    public enum SideRestriction {
        none,           // all sides contribute to the ratings
        thisSide_only,
        middle_only,
        oppositeSide_only
    }

    public enum Side {
        left,
        middle,
        right
    }

    private final Team team;
    private long teamRatingRevision;

    public RatingPredictionModel(Team team) {
        if (ratingContributionParameterMap == null) {
            initRatingContributionParameterMap();
        }
        this.team = team;
        this.teamRatingRevision = team.getRatingRevision();
    }

    /**
     * Rating revision -> sector -> minute -> rating
     */
    private final LineupRatingCache ratingCache = new LineupRatingCache() {
        @Override
        public double calc(Lineup lineup, RatingSector s, Integer minute) {
            return calcSectorRating(lineup, s, minute);
        }
    };

    public double getRating(Lineup lineup, RatingSector s, int minute) {
        if ( team.getRatingRevision() != teamRatingRevision){
            ratingCache.clear();
            teamRatingRevision = team.getRatingRevision();
        }
        return ratingCache.get(lineup, s, minute);
    }

    private final LineupRatingCache averageRatingCache = new LineupRatingCache() {
        @Override
        public double calc(Lineup lineup, RatingSector s, Integer minute) {
            return calcAverageRating(lineup, s, minute);
        }
    };

    public double getAverageRating(Lineup lineup, RatingSector s, int minutes) {
        return averageRatingCache.get(lineup, s, minutes);
    }

    static public @NotNull TreeSet<Integer> getRatingChangeMinutes(Lineup lineup, int minutes){
        var staminaChanges = new TreeSet<Integer>();
        for (int i = 0; i < minutes; i += 5) {
            staminaChanges.add(i);
        }
        staminaChanges.addAll(lineup.getLineupChangeMinutes());
        return staminaChanges;
    }

    /**
     * Calculate average rating of lineup sector for 90 or 120 minutes match time
     *
     * @param lineup  match order
     * @param s       rating sector
     * @param minutes match duration to calculate average for
     * @return average match rating of sector
     */
    private double calcAverageRating(Lineup lineup, RatingSector s, int minutes) {
        var iStart = 0;
        var lastRating = 0.;
        var sumRating = 0.;
        for (var m : getRatingChangeMinutes(lineup, minutes)) {
            var rating = getRating(lineup, s, m);
            sumRating += lastRating * (m - iStart);
            lastRating = rating;
            iStart = m;
        }
        sumRating += lastRating * (minutes - iStart);
        return sumRating / minutes;
    }

    private static class RatingCalculationParameter {
        public RatingContributionParameter contributionParameter;
        public Side side;

        public RatingCalculationParameter(RatingContributionParameter p, Side s) {
            contributionParameter = p;
            side = s;
        }
    }

    private static final Map<RatingSector, RatingCalculationParameter> ratingSectorParameterMap = Map.ofEntries(
            entry(RatingSector.Defence_Left, new RatingCalculationParameter(RatingContributionParameter.SideDefence, Side.left)),
            entry(RatingSector.Defence_Central, new RatingCalculationParameter(RatingContributionParameter.CentralDefence, Side.middle)),
            entry(RatingSector.Defence_Right, new RatingCalculationParameter(RatingContributionParameter.SideDefence, Side.right)),
            entry(RatingSector.Midfield, new RatingCalculationParameter(RatingContributionParameter.Midfield, Side.middle)),
            entry(RatingSector.Attack_Left, new RatingCalculationParameter(RatingContributionParameter.SideAttack, Side.left)),
            entry(RatingSector.Attack_Central, new RatingCalculationParameter(RatingContributionParameter.CentralAttack, Side.middle)),
            entry(RatingSector.Attack_Right, new RatingCalculationParameter(RatingContributionParameter.SideAttack, Side.right))
    );

    public double getPositionContribution(@NotNull MatchLineupPosition p, Weather weather, int tacticType, RatingSector s, int minute, double overcrowdingPenalty) {
        return getPositionContribution(p.getPlayer(), p.getRoleId(), p.getBehaviour(), weather, tacticType, s, minute, p.getStartMinute(), overcrowdingPenalty);
    }
    public double getPositionContribution(Player player, int roleId, byte behaviour, Weather weather, int tacticType, RatingSector s, int minute, int startMinute, double overcrowdingPenalty) {
        if ( player == null) return 0.;
        var contribution = contributionCache.get(s, roleId, player, behaviour);
        if (contribution > 0) {
            contribution *= overcrowdingPenalty;
            contribution += experienceCache.get(player.getSkillValue(EXPERIENCE), s );
            contribution *= weatherCache.get(Specialty.getSpecialty(player.getPlayerSpecialty()), weather);
            contribution *= staminaCache.get((double) player.getStamina(), minute, startMinute, tacticType);
        }
        HOLogger.instance().debug(getClass(), "getPositionContribution " + player.getFullName()
                + " " + MatchRoleID.getNameForPosition(MatchRoleID.getPosition(roleId, behaviour))
                + " " + weather.toString()
                + " " + tacticType
                + " " + s.toString()
                + " " + minute
                + " " + overcrowdingPenalty
                + "= " + contribution
        );
        return contribution;
    }

    public double getPlayerSetPiecesStrength(Player p){
        return getPlayerTacticStrength(new MatchLineupPosition(setPieces, NORMAL, p ), SET_PIECES, null, TAKTIK_NORMAL, 0);
    }

    public double getPlayerTacticStrength(@NotNull MatchLineupPosition p, int playerSkill, Weather weather, int tacticType, int minute) {
        var player = p.getPlayer();
        if (player != null) {
            var ret = playerTaticStrengthCache.get(player, playerSkill);
            ret *= weatherCache.get(Specialty.getSpecialty(player.getPlayerSpecialty()), weather);
            ret *= staminaCache.get((double) player.getStamina(), minute, p.getStartMinute(), tacticType);
            return ret;
        }
        return 0;
    }

    public double calcSectorRating(Lineup lineup, RatingSector s, int minute) {
        var ret = 0.;
        for (var p : lineup.getFieldPlayers(minute)) {
            if (p.getPlayerId() != 0) {
                var overcrowdingPenalty = getOvercrowdingPenalty(countPlayersInSector(lineup, p.getSector()), p.getSector());
                var contribution = getPositionContribution(p, lineup.getWeather(), lineup.getTacticType(), s, minute, overcrowdingPenalty);
                ret += contribution;
            }
        }
        ret *= calcSector(lineup, s);

        return pow(ret, 1.165) + 0.75;
    }

    private final StatsCache hatStatsCache = new StatsCache() {
        @Override
        public double calc(Lineup lineup, int minute) {
            return calcHatStats(lineup, minute);
        }
    };

    protected double calcHatStats(Lineup lineup, int minute) {
        double hatStats = 3 * getRating(lineup, RatingSector.Midfield, minute);
        hatStats += getRating(lineup, RatingSector.Defence_Left, minute);
        hatStats += getRating(lineup, RatingSector.Defence_Central, minute);
        hatStats += getRating(lineup, RatingSector.Defence_Right, minute);
        hatStats += getRating(lineup, RatingSector.Attack_Left, minute);
        hatStats += getRating(lineup, RatingSector.Attack_Central, minute);
        hatStats += getRating(lineup, RatingSector.Attack_Right, minute);
        return 4 * hatStats;
    }

    public double getHatStats(Lineup lineup, int minute){
        return hatStatsCache.get(lineup, minute);
    }
    public double getAverageHatStats(Lineup lineup, int minutes) {
        if (minutes == 90) return getAverage90HatStats(lineup);
        return getAverage120HatStats(lineup);
    }

    public double getAverage90HatStats(Lineup lineup) {
        return hatStatsCache.getAverage90(lineup);
    }
    public double getAverage120HatStats(Lineup lineup) {
        return hatStatsCache.getAverage120(lineup);
    }

    private final StatsCache loddarStatsCache = new StatsCache() {
        @Override
        public double calc(Lineup lineup, int minute) {
            return calcLoddarStats(lineup, minute);
        }
    };

    private double calcLoddarStats(@NotNull Lineup lineup, int minute) {
        final double MIDFIELD_SHIFT = 0.0;
        final double COUNTERATTACK_WEIGHT = 0.25;
        final double DEFENSE_WEIGHT = 0.47;
        final double ATTACK_WEIGHT = 1 - DEFENSE_WEIGHT;
        final double CENTRAL_WEIGHT = 0.37;
        final double WINGER_WEIGHT = (1 - CENTRAL_WEIGHT) / 2d;
        double correctedCentralWeight = CENTRAL_WEIGHT;
        double counterCorrection = 0;

        var tacticType = lineup.getTacticType();
        var tacticLevel = getTacticRating(lineup, minute);
        switch (tacticType) {
            case IMatchDetails.TAKTIK_MIDDLE ->
                    correctedCentralWeight = CENTRAL_WEIGHT + (((0.2 * (tacticLevel - 1)) / 19d) + 0.2);
            case IMatchDetails.TAKTIK_WINGS ->
                    correctedCentralWeight = CENTRAL_WEIGHT - (((0.2 * (tacticLevel - 1)) / 19d) + 0.2);
            default -> {
            }
        }

        final double correctedWingerWeight = (1 - correctedCentralWeight) / 2d;

        if (tacticType == IMatchDetails.TAKTIK_KONTER) {
            counterCorrection = (COUNTERATTACK_WEIGHT * 2 * tacticLevel) / (tacticLevel + 20);
        }

        var dMD = getRating(lineup, RatingSector.Midfield, minute);
        var dRD = getRating(lineup, RatingSector.Defence_Right, minute);
        var dCD = getRating(lineup, RatingSector.Defence_Central, minute);
        var dLD = getRating(lineup, RatingSector.Defence_Left, minute);
        var dLA = getRating(lineup, RatingSector.Attack_Left, minute);
        var dCA = getRating(lineup, RatingSector.Attack_Central, minute);
        var dRA = getRating(lineup, RatingSector.Attack_Right, minute);


        // Calculate attack rating
        final double attackStrength = (ATTACK_WEIGHT + counterCorrection) * ((correctedCentralWeight * hq(dCA))
                + (correctedWingerWeight * (hq(dLA) + hq(dRA))));

        // Calculate defense rating
        final double defenseStrength = DEFENSE_WEIGHT * ((CENTRAL_WEIGHT * hq(dCD))
                + (WINGER_WEIGHT * (hq(dLD) + hq(dRD))));

        // Calculate midfield rating
        final double midfieldFactor = MIDFIELD_SHIFT + hq(dMD);

        // Calculate and return the LoddarStats rating
        return  80 * midfieldFactor * (defenseStrength + attackStrength);
    }

    private double hq(double value) {
        // Convert reduced float rating (1.00....20.99) to original integer HT rating (1...80) one +0.5 is because of correct rounding to integer
        int x = (int)(((value - 1.0f) * 4.0f) + 1.0f);
        return (2.0 * x) / (x + 80.0);
    }

    public double getLoddarStats(Lineup lineup, int minute){
        return loddarStatsCache.get(lineup, minute);
    }
    public double getAverageLoddarStats(Lineup lineup, int minutes) {
        if (minutes == 90) return getAverage90LoddarStats(lineup);
        return getAverage120LoddarStats(lineup);
    }
    public double getAverage90LoddarStats(Lineup lineup){
        return loddarStatsCache.getAverage90(lineup);
    }
    public double getAverage120LoddarStats(Lineup lineup){
        return loddarStatsCache.getAverage120(lineup);
    }

    private final TacticRatingCache tacticRatingCache = new TacticRatingCache() {
        @Override
        public double calc(Lineup lineup, Integer minute) {
            return calcTacticsRating(lineup, minute);
        }
    };
    public double getTacticRating(Lineup lineup, int minute){
        return tacticRatingCache.get(lineup, minute);
    }

    public double calcTacticsRating(@NotNull Lineup lineup, int minute) {
        var tacticType = lineup.getTacticType();
        switch (tacticType) {
            case TAKTIK_KONTER -> {
                return calcCounterAttack(lineup, minute);
            }
            case TAKTIK_PRESSING -> {
                return calcPressing(lineup, minute);
            }
            case TAKTIK_LONGSHOTS -> {
                return calcLongshots(lineup, minute);
            }
            case TAKTIK_MIDDLE, TAKTIK_WINGS -> {
                return calcPassing(lineup, minute);
            }
            case TAKTIK_CREATIVE -> {
                return calcCreative(lineup, minute);
            }
        }
        return 1;
    }

    private double calcCreative(@NotNull Lineup lineup, int minute) {
        var sum = 0.;
        for (var p : lineup.getFieldPlayers(minute)) {
            if (p.getRoleId() == KEEPER) continue;
            var player = p.getPlayer();
            if (player != null) {
                var passing = calcStrength(player, PASSING);
                var experience = calcSkillRating(player.getSkill(EXPERIENCE));
                var contrib = 4 * passing + experience;
                if (player.getPlayerSpecialty() == PlayerSpeciality.UNPREDICTABLE) {
                    contrib *= 2;
                }
                sum += contrib;
            }
        }
        return sum / 50.; // TODO: guessed, investigate the formula rating(sum)
    }

    private double calcPressing(@NotNull Lineup lineup, int minute) {
        double ret = 0;
        for (var p : lineup.getFieldPlayers(minute)) {
            var defending = getPlayerTacticStrength(p, DEFENDING, lineup.getWeather(), lineup.getTacticType(), minute);
            if (defending > 0) {
                var player = p.getPlayer();
                if (player.getPlayerSpecialty() == PlayerSpeciality.POWERFUL) {
                    defending *= 2;
                }
            }
            ret += defending;
        }
        return 0.085 * ret + 0.075;
    }

    private double calcPassing(@NotNull Lineup lineup, int minute) {
        var sumPassing = 0.;
        for (var p : lineup.getFieldPositions()) {
            var player = p.getPlayer();
            if (player != null) {
                sumPassing += calcSkillRating(player.getSkill(PASSING));
            }
        }
        return sumPassing / 5. - 2.;
    }

    private double calcLongshots(@NotNull Lineup lineup, int minute) {
        var sumScoring = 0.;
        var sumSetPieces = 0.;
        var n = 0;
        for (var p : lineup.getFieldPositions()) {
            var player = p.getPlayer();
            if (player != null) {
                n++;
                sumScoring += calcSkillRating(player.getSkill(SCORING));
                sumSetPieces += calcSkillRating(player.getSkill(SET_PIECES));
            }
        }

        //Tactic Level = 1.66*SC + 0.55*SP - 7.6
        return 1.66 * sumScoring / n + 0.55 * sumSetPieces / n - 7.6;
    }

    /**
     * <a href="https://www88.hattrick.org/Forum/Read.aspx?t=16741488&v=4&a=1&n=24">...</a>
     *
     * @param lineup Lineup to calculate counter-attack rating for
     * @param minute int match minute
     * @return double The rating value
     */
    protected double calcCounterAttack(@NotNull Lineup lineup, int minute) {
        var defence = lineup.getFieldPositions().stream().filter(i -> i.getRoleId() >= rightBack && i.getRoleId() <= leftBack && i.getPlayerId() > 0).toList();
        var a = 0.;
        var f = 0.;
        var n = 0;
        for (var p : defence) {
            var player = p.getPlayer();
            var form = player.getForm();
            var passing = calcSkillRating(player.getSkill(PASSING));
            var defending = calcSkillRating(player.getSkill(DEFENDING));
            n++;
            f += form;
            a += 2 * passing + defending;
        }
        a *= f / n;
        // 0,017272a + 1,042313
        return 0.01727 * a + 1.042313;
    }

    Cache4<Double, Integer, Integer, Integer> staminaCache = new Cache4<>() {
        @Override
        public double calc(Double stamina, Integer minute, Integer startMinute, Integer tacticType) {
            return calcStamina(stamina, minute, startMinute, tacticType);
        }
    };

    static List<Integer> rhsPositions = List.of(rightBack, rightCentralDefender, rightInnerMidfield, rightWinger, rightForward);
    boolean isRightHandSidePosition(int roleId){
        return rhsPositions.contains(roleId);
    }

    int togglePositionSide(int roleId){
        switch (roleId){
            case rightBack -> {
                return leftBack;
            }
            case rightCentralDefender -> {
                return leftCentralDefender;
            }
            case rightInnerMidfield -> {
                return leftInnerMidfield;
            }
            case rightWinger -> {
                return leftWinger;
            }
            case rightForward -> {
                return leftForward;
            }
        }
        return roleId;
    }

    RatingSector toggleRatingSectorSide(@NotNull RatingSector s){
        switch (s){
            case Defence_Left -> {
                return RatingSector.Defence_Right;
            }
            case Defence_Right -> {
                return RatingSector.Defence_Left;
            }
            case Attack_Left -> {
                return RatingSector.Attack_Right;
            }
            case Attack_Right -> {
                return RatingSector.Attack_Left;
            }
        }

        return s;
    }

    ContributionCache contributionCache = new ContributionCache(this);

    Cache<Player, Integer> playerTaticStrengthCache = new Cache<>() {
        @Override
        public double calc(Player player, Integer skill) {
            return calcPlayerTacticStrength(player, skill);
        }

    };

    Cache<Double, RatingSector> experienceCache = new Cache<>() {
        @Override
        public double calc(Double skillValue, RatingSector ratingSector) {
            return calcExperience(ratingSector, skillValue);
        }
    };

    Cache<Specialty, Weather> weatherCache = new Cache<>() {
        @Override
        public double calc(Specialty specialty, Weather weather) {
            return calcWeather(specialty, weather);
        }
    };

    protected double calcPlayerTacticStrength(Player player, Integer skill) {
        var ret = calcStrength(player, skill);
        var xp = calcSkillRating(player.getSkillValue(EXPERIENCE));
        var f = Math.log10(xp) * 4. / 3.;
        ret += f;
        return ret;
    }

    public static List<Integer> playerRatingPositions = List.of(keeper, leftBack, leftCentralDefender, leftWinger, leftInnerMidfield, leftForward);

    public static Integer getPlayerRatingPosition(byte positionWithBehaviour){
        return switch (positionWithBehaviour){
            case KEEPER -> keeper;
            case CENTRAL_DEFENDER, CENTRAL_DEFENDER_OFF, CENTRAL_DEFENDER_TOWING -> leftCentralDefender;
            case BACK, BACK_OFF, BACK_TOMID, BACK_DEF -> leftBack;
            case MIDFIELDER, MIDFIELDER_OFF, MIDFIELDER_DEF, MIDFIELDER_TOWING -> leftInnerMidfield;
            case WINGER, WINGER_OFF, WINGER_DEF,  WINGER_TOMID -> leftWinger;
            case FORWARD, FORWARD_DEF, FORWARD_TOWING ->leftForward;
            default -> throw new IllegalStateException("Unexpected value: " + positionWithBehaviour);
        };
    }
    public static Byte getBehaviour(byte positionWithBehaviour){
        return switch (positionWithBehaviour){
            case FORWARD, WINGER, KEEPER,  MIDFIELDER, BACK, CENTRAL_DEFENDER -> NORMAL;
            case WINGER_OFF, CENTRAL_DEFENDER_OFF, MIDFIELDER_OFF, BACK_OFF-> OFFENSIVE;
            case FORWARD_TOWING, MIDFIELDER_TOWING , CENTRAL_DEFENDER_TOWING -> TOWARDS_WING;
            case WINGER_TOMID, BACK_TOMID -> TOWARDS_MIDDLE;
            case FORWARD_DEF, WINGER_DEF, MIDFIELDER_DEF, BACK_DEF -> DEFENSIVE;
            default -> throw new IllegalStateException("Unexpected value: " + positionWithBehaviour);
        };
    }
    Cache4<Player, Integer, Byte, Integer> playerRatingCache = new Cache4<>() {
        @Override
        public double calc(Player player, Integer roleId, Byte behaviour, Integer minute) {
            return calcPlayerRating(player, roleId, behaviour, minute);
        }
    };

    /**
     * Add the player's rating contributions to all rating sectors
     * @param p
     * @return
     */
    protected double calcPlayerRating(Player p, int roleId, byte behaviour, int minute){
        var ret = 0.;
        for ( var s : RatingSector.values()){
            var c = getPositionContribution(p, roleId, behaviour,  Weather.UNKNOWN, TAKTIK_NORMAL, s, minute, 0, 1.  );
            ret += c;
        }
        return ret;
    }

    public double getPlayerRating(Player p, byte positionWithBehaviour){
        return playerRatingCache.get(p, getPlayerRatingPosition(positionWithBehaviour), getBehaviour(positionWithBehaviour), 0);
    }
    public double getPlayerRating(Player p, int roleId, byte behaviour){
        return playerRatingCache.get(p, togglePositionSide(roleId), behaviour, 0);
    }
    public double getPlayerRatingEndOfMatch(Player p, int roleId, byte behaviour){
        return playerRatingCache.get(p, togglePositionSide(roleId), behaviour, 90);
    }
    public double getPlayerRatingEndOfExtraTime(Player p, int roleId, byte behaviour){
        return playerRatingCache.get(p, togglePositionSide(roleId), behaviour, 120);
    }

    public double calcRelativePlayerRating(Player p, int roleId, byte behaviour, int minute){
        Player reference;
        if (  roleId == KEEPER){
            reference = Player.getReferenceKeeper();
        }
        else {
            reference = Player.getReferencePlayer();
        }
        return  getPlayerRating(p, roleId, behaviour, minute) /  getPlayerRating(reference, roleId, behaviour, minute);
    }

    public double getPlayerRating(Player p, int roleId, byte behaviour, int minute) {
        return playerRatingCache.get(p, togglePositionSide(roleId), behaviour, minute); // calc left sides only
    }

    public double getPlayerRating(@NotNull Player p, int roleId, byte behaviour, int minute, Weather weather) {
        return weatherCache.get(Specialty.getSpecialty(p.getPlayerSpecialty()), weather)
                * getPlayerRating(p, roleId, behaviour, minute);
    }

    private final Map<Player, Double> playerPenaltyMap = new HashMap<>();
    public double getPlayerPenaltyStrength(Player player) {
        var ret = playerPenaltyMap.get(player);
        if (ret != null) return ret;

        ret = calcPlayerPenaltyStrength(player);
        playerPenaltyMap.put(player, ret);
        return ret;
    }

    private Double calcPlayerPenaltyStrength(@NotNull Player player) {
        var ret = calcSkillRating(player.getSkill(EXPERIENCE)) * 1.5;
        ret += calcStrength(player, SET_PIECES) * 0.7;
        ret += calcStrength(player, SCORING) * 0.3;

        if (player.getPlayerSpecialty() == PlayerSpeciality.TECHNICAL) {
            ret *= 1.1;
        }
        return ret;
    }

    /**
     * @param s
     * @param coachModifier Integer value representing the style of play the team will use in the match. The value ranges from -10 (100% defensive) to 10 (100% offensive).
     * @return
     */
    protected double calcTrainer(@NotNull RatingSector s, int coachModifier) {
        switch (s) {
            case Defence_Left, Defence_Right, Defence_Central -> {
                if (coachModifier <= 0) {
                    // Balanced or Defensive
                    return 1.02 - coachModifier * (1.15 - 1.02) / 10.;
                } else {
                    // Offensive
                    return 1.02 - coachModifier * (1.02 - 0.9) / 10.;
                }
            }
            case Attack_Central, Attack_Left, Attack_Right -> {
                if (coachModifier <= 0) {
                    // Balanced or Defensive
                    return 1.02 - coachModifier * (0.9 - 1.02) / 10.;
                } else {
                    // Offensive
                    return 1.02 - coachModifier * (1.02 - 1.1) / 10.;
                }
            }
        }
        return 1.;
    }

    protected double calcSector(Lineup lineup, @NotNull RatingSector s) {
        var r = 1.;
        switch (s) {
            case Midfield -> {
                switch (lineup.getAttitude()) {
                    case IMatchDetails.EINSTELLUNG_PIC -> r *= 0.83945;
                    case IMatchDetails.EINSTELLUNG_MOTS -> r *= 1.1149;
                    default -> {
                    }
                }
                switch (lineup.getLocation()) {
                    case IMatchDetails.LOCATION_AWAYDERBY -> r *= 1.11493;
                    case IMatchDetails.LOCATION_HOME -> r *= 1.19892;
                    default -> {
                    }
                }
                switch (MatchTacticType.fromInt(lineup.getTacticType())) {
                    case CounterAttacks -> r *= 0.93;
                    case LongShots -> r *= 0.96;
                }
                var spirit = calcTeamSpirit(team.getTeamSpirit());
                r *= spirit;
            }
            case Defence_Left, Defence_Right -> {
                r *= calcTrainer(s, lineup.getCoachModifier());
                switch (MatchTacticType.fromInt(lineup.getTacticType())) {
                    case AttackInTheMiddle -> r *= 0.85;
                    case PlayCreatively -> r *= 0.93;
                    default -> {
                    }
                }
            }
            case Defence_Central -> {
                r *= calcTrainer(s, lineup.getCoachModifier());
                switch (MatchTacticType.fromInt(lineup.getTacticType())) {
                    case AttackInWings -> r *= 0.85;
                    case PlayCreatively -> r *= 0.93;
                    default -> {
                    }
                }
            }
            case Attack_Central, Attack_Left, Attack_Right -> {
                r *= calcTrainer(s, lineup.getCoachModifier());
                if (Objects.requireNonNull(MatchTacticType.fromInt(lineup.getTacticType())) == MatchTacticType.LongShots) {
                    r *= 0.96;
                }
                r *= calcConfidence(team.getConfidence());
            }
        }
        return r;
    }

    protected double calcConfidence(double confidence) {
        return 0.8 + 0.05 * confidence;
    }

    protected double calcTeamSpirit(double teamSpirit) {
        return 0.1 + 0.425 * sqrt(teamSpirit + .5);
    }

    protected double calcWeather(Specialty specialty, Weather weather) {
        if ( specialty != null) {
            switch (specialty) {
                case Technical -> {
                    if (weather == Weather.RAINY) return 0.95;
                    if (weather == Weather.SUNNY) return 1.05;
                }
                case Powerful -> {
                    if (weather == Weather.RAINY) return 1.05;
                    if (weather == Weather.SUNNY) return 0.95;
                }
                case Quick -> {
                    if (weather == Weather.RAINY || weather == Weather.SUNNY) return 0.95;
                }
            }
        }
        return 1;
    }

    protected double calcStamina(double stamina, int minute, int startMinute, int tacticType) {
        var p = tacticType == TAKTIK_PRESSING ? 1.1 : 1.;
        var s = calcSkillRating(stamina);
        double r0, delta;
        if (s < 7) {
            r0 = 102. + 23. / 7. * s;
            delta = p * (27. / 70. * s - 5.95);
        } else {
            r0 = 102 + 23 + (s - 7) * 100. / 7.;
            delta = -3.25 * p;
        }

        var r = r0;
        for (var m = startMinute; m < minute; m += 5) {
            if (startMinute < 45 && m >= 45 && m < 50) {
                r = min(r0, r + 120.75 - 102);
            } else if (startMinute < 90 && m >= 90 && m < 95) {
                r = min(r0, r + 127 - 120.75);
            } else {
                r += delta;
            }
        }

        return min(1, r / 100.);
    }

    protected double calcContribution(Player player, int roleId, Byte behaviour, RatingSector s) {
        var ret = 0.;
        if (player != null) {
            var params = ratingSectorParameterMap.get(s);
            if (params != null) {
                var side = params.side;
                var contributions = params.contributionParameter;
                var factor = ratingContributionParameterMap.get(contributions);
                for ( var f : factor.entrySet()){
                    var skillType = f.getKey();
                    var sectors = f.getValue();
                    for ( var se : sectors.entrySet()){
                        var sector = se.getKey();
                        if (MatchRoleID.getSector(roleId) == sector) {
                            var sideRestrictions = se.getValue();
                            for ( var r : sideRestrictions.entrySet()){
                                var sideRestriction = r.getKey();
                                if (!isRoleSideRestricted(roleId, side, sideRestriction)){
                                    var behaviours = r.getValue();
                                    var specialties = behaviours.get(behaviour);
                                    if ( specialties != null){
                                        var res = specialties.get(Specialty.getSpecialty(player.getPlayerSpecialty()));
                                        res *= calcStrength(player, skillType);
                                        ret += res;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    protected double calcExperience(@NotNull RatingSector ratingSector, double skillValue) {
        var exp = calcSkillRating(skillValue);
        var k = -0.00000725 * pow(exp, 4) + 0.0005 * pow(exp, 3) - .01336 * pow(exp, 2) + 0.176 * exp;

        switch (ratingSector) {
            case Defence_Left, Defence_Right -> {
                return k * 0.345;
            }
            case Defence_Central -> {
                return k * 0.48;
            }
            case Midfield -> {
                return k * 0.73;
            }
            case Attack_Left, Attack_Right -> {
                return k * 0.375;
            }
            case Attack_Central -> {
                return k * 0.450;
            }
            default -> throw new IllegalStateException("Unexpected value: " + ratingSector);
        }
    }

    protected final Map<MatchRoleID.Sector, Map<Integer, Double>> overcrowdingFactors = Map.ofEntries(
            entry(MatchRoleID.Sector.CentralDefence, Map.ofEntries(
                            entry(2, 0.964),
                            entry(3, 0.9)
                    )
            ),
            entry(MatchRoleID.Sector.InnerMidfield, Map.ofEntries(
                            entry(2, 0.935),
                            entry(3, 0.825)
                    )
            ),
            entry(MatchRoleID.Sector.Forward, Map.ofEntries(
                            entry(2, 0.945),
                            entry(3, 0.865)
                    )
            )
    );

    private int countPlayersInSector(@NotNull Lineup lineup, MatchRoleID.Sector sector) {
        var countPlayersInSector = 0;
        for (var p : lineup.getFieldPositions()) {
            if (p.getSector() == sector) countPlayersInSector++;
        }
        return countPlayersInSector;
    }

    protected double getOvercrowdingPenalty(int countPlayersInSector, MatchRoleID.Sector sector) {
        var overcrowding = overcrowdingFactors.get(sector);
        if (overcrowding != null) {
            var ret = overcrowding.get(countPlayersInSector);
            if (ret != null) {
                return ret;
            }
        }
        return 1.;
    }

    protected double calcStrength(@NotNull Player player, Integer playerSkill) {
        var skillRating = calcSkillRating(player.getSkill(playerSkill));
        skillRating += calcLoyalty(player);
        skillRating *= calcForm(player);
        return skillRating;
    }

    protected double calcForm(@NotNull Player player) {
        var form = calcSkillRating(player.getSkill(PlayerSkill.FORM));
        return 0.378 * sqrt(form);
    }

    protected double calcLoyalty(@NotNull Player player) {
        if (player.isHomeGrown()) return 1.5;
        var loyaltyRating = calcSkillRating(player.getSkill(PlayerSkill.LOYALTY));
        return loyaltyRating / 19.;
    }

    protected double calcSkillRating(double skill) {
        return max(0, skill - 1);
    }

    protected boolean isRoleSideRestricted(int roleID, Side side, @NotNull SideRestriction sideRestriction) {
        switch (sideRestriction) {
            case middle_only -> {
                return roleID != middleCentralDefender && roleID != centralInnerMidfield && roleID != centralForward;
            }
            case thisSide_only -> {
                switch (side) {
                    case left -> {
                        return roleID != leftBack && roleID != leftCentralDefender && roleID != leftWinger && roleID != leftInnerMidfield && roleID != leftForward;
                    }
                    case right -> {
                        return roleID != rightBack && roleID != rightCentralDefender && roleID != rightWinger && roleID != rightInnerMidfield && roleID != rightForward;
                    }
                    case middle -> {
                        return roleID != middleCentralDefender && roleID != centralInnerMidfield && roleID != centralForward;
                    }
                }
            }
            case oppositeSide_only -> {
                switch (side) {
                    case right -> {
                        return roleID != leftBack && roleID != leftCentralDefender && roleID != leftWinger && roleID != leftInnerMidfield && roleID != leftForward;
                    }
                    case left -> {
                        return roleID != rightBack && roleID != rightCentralDefender && roleID != rightWinger && roleID != rightInnerMidfield && roleID != rightForward;
                    }
                }
            }
        }
        return false;
    }

    protected static Map<RatingContributionParameter,
            Map<Integer,
                    Map<MatchRoleID.Sector,
                            Map<SideRestriction,
                                    Map<Byte,
                                            Map<Specialty, Double>
                                            >
                                    >
                            >
                    >
            > ratingContributionParameterMap = null;

    protected static void initRatingContributionParameterMap() {
        ratingContributionParameterMap = new HashMap<>();
        double factorSideDefence = .25;
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.KEEPER, MatchRoleID.Sector.Goal, SideRestriction.none, NORMAL, .61* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Goal, SideRestriction.none, NORMAL, .25* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.thisSide_only, NORMAL, .52* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.thisSide_only, OFFENSIVE, .4 * factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.thisSide_only, TOWARDS_WING, .81 * factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.middle_only, NORMAL, .26* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.middle_only, OFFENSIVE, .2* factorSideDefence);

        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, NORMAL, .92* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, OFFENSIVE, .74* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, DEFENSIVE, factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, TOWARDS_MIDDLE, .75* factorSideDefence);

        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, NORMAL, .19* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, OFFENSIVE, .09* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, DEFENSIVE, .27* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, TOWARDS_WING, .24* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, NORMAL, .095* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, OFFENSIVE, .045* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, DEFENSIVE, .135* factorSideDefence);

        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, NORMAL, .35* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, OFFENSIVE, .22* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, DEFENSIVE, .61* factorSideDefence);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, TOWARDS_MIDDLE, .29* factorSideDefence);

        double factorCentralDefence = .14;
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.KEEPER, MatchRoleID.Sector.Goal, SideRestriction.none, NORMAL, .87*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Goal, SideRestriction.none, NORMAL, .35*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, NORMAL, factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, OFFENSIVE, .73*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, TOWARDS_WING, .67*factorCentralDefence);

        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.none, NORMAL, .38*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.none, OFFENSIVE, .35*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.none, DEFENSIVE, .43*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.none, TOWARDS_MIDDLE, .7*factorCentralDefence);

        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, NORMAL, .4*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, OFFENSIVE, .16*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, DEFENSIVE, .58*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, TOWARDS_WING, .33*factorCentralDefence);

        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.none, NORMAL, .2*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.none, OFFENSIVE, .13*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.none, DEFENSIVE, .25*factorCentralDefence);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.none, TOWARDS_MIDDLE, .25*factorCentralDefence);

        double factorMidfield = .09;
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, NORMAL, .25*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, OFFENSIVE, .4*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, TOWARDS_WING, .15*factorMidfield);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.none, NORMAL, .15*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.none, OFFENSIVE, .2*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.none, DEFENSIVE, .1*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.none, TOWARDS_MIDDLE, .2*factorMidfield);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, NORMAL, 1*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, OFFENSIVE, .95*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, DEFENSIVE, .95*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, TOWARDS_WING, .9*factorMidfield);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.none, NORMAL, .45*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.none, OFFENSIVE, .3*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.none, DEFENSIVE, .3*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.none, TOWARDS_MIDDLE, .55*factorMidfield);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .25*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .35*factorMidfield);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.none, TOWARDS_WING, .15*factorMidfield);

        double factorCentralAttack = .13;
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, NORMAL, .33*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, OFFENSIVE, .49*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, DEFENSIVE, .18*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, TOWARDS_WING, .23*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.none, NORMAL, .11*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.none, OFFENSIVE, .13*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.none, DEFENSIVE, .05*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.none, TOWARDS_MIDDLE, .16*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .33*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .53*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, TOWARDS_WING, .23*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, NORMAL, .22*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, OFFENSIVE, .31*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, DEFENSIVE, .13*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .56*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, TOWARDS_WING, .66*factorCentralAttack);

        double factorSideAttack = .45;
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, NORMAL, .13*factorSideAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, OFFENSIVE, 0.18*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, DEFENSIVE, 0.07*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .14*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, 0.31*factorCentralAttack);
        initSpecialty(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, Specialty.Technical, 0.41*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, NORMAL, .26*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, OFFENSIVE, 0.36*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, DEFENSIVE, 0.14*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, TOWARDS_WING, 0.31*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, NORMAL, .26*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, OFFENSIVE, .29*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, DEFENSIVE, 0.21*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, TOWARDS_MIDDLE, 0.15*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.thisSide_only, TOWARDS_WING, 0.21*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.oppositeSide_only, TOWARDS_WING, 0.06*factorCentralAttack);


        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.CentralDefence, SideRestriction.thisSide_only, TOWARDS_WING, 0.26*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, NORMAL, .59*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, OFFENSIVE, .69*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, DEFENSIVE, .45*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, TOWARDS_MIDDLE, .35*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, TOWARDS_WING, .59*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, NORMAL, .86*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, OFFENSIVE, factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, DEFENSIVE, .69*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, TOWARDS_MIDDLE, .74*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .24*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .13*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.thisSide_only, TOWARDS_WING, .64*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.oppositeSide_only, TOWARDS_WING, .21*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .27*factorCentralAttack);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .13*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.oppositeSide_only, TOWARDS_WING, .19*factorCentralAttack);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.thisSide_only, TOWARDS_WING, .51*factorCentralAttack);
    }

    private static void initSpecialty(RatingContributionParameter ratingContributionParameter, int skill, MatchRoleID.Sector sector, SideRestriction sideRestriction, byte behaviour, Specialty specialty, double v) {
        var sr = initSideRestriction(ratingContributionParameter, skill, sector, sideRestriction);
        var specialties = sr.computeIfAbsent(behaviour, k -> new HashMap<>());
        specialties.put(specialty, v);
    }

    private static void initAllSpecialties(RatingContributionParameter ratingContributionParameter, int skill, MatchRoleID.Sector sector, SideRestriction sideRestriction, byte behaviour, double v) {
        Map<Specialty, Double> specialtyMap = new HashMap<>();
        for (var specialty : Specialty.values()) {
            specialtyMap.put(specialty, v);
        }
        var sr = initSideRestriction(ratingContributionParameter, skill, sector, sideRestriction);
        sr.put(behaviour, specialtyMap);
    }

    private static Map<Byte, Map<Specialty, Double>> initSideRestriction(RatingContributionParameter ratingContributionParameter, int skill, MatchRoleID.Sector sector, SideRestriction sideRestriction) {
        var p = ratingContributionParameterMap.computeIfAbsent(ratingContributionParameter, k -> new HashMap<>());
        var s = p.computeIfAbsent(skill, k -> new HashMap<>());
        var se = s.computeIfAbsent(sector, k -> new HashMap<>());
        return se.computeIfAbsent(sideRestriction, k -> new HashMap<>());
    }
}