package core.rating;

import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.model.HOVerwaltung;
import core.model.Team;
import core.model.match.IMatchDetails;
import core.model.match.MatchLineupPosition;
import core.model.match.MatchTacticType;
import core.model.match.Weather;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.model.player.Specialty;
import module.lineup.Lineup;
import module.lineup.substitution.model.Substitution;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static core.constants.player.PlayerSkill.*;
import static core.model.match.IMatchDetails.*;
import static core.model.player.IMatchRoleID.*;
import static core.model.player.IMatchRoleID.KEEPER;
import static core.model.player.IMatchRoleID.WINGER;
import static java.lang.Math.*;
import static java.util.Map.entry;

/**
 * The rating calculations used in this class is based on Schum's formula Σ [ (S+Eff(L)) * K(F) * C * K(P) + Eff(Exp) ]
 * released in <a href="https://www86.hattrick.org/Forum/Read.aspx?t=17404127&n=59&v=0&mr=0">...</a>
 */
public class RatingPredictionModel {

    protected Substitution manMarkingOrder;
    protected Player.ManMarkingPosition manMarkingPosition;
    protected Weather weather;
    protected int tacticType;

    public enum RatingSector {
        DEFENCE_LEFT,
        DEFENCE_CENTRAL,
        DEFENCE_RIGHT,
        MIDFIELD,
        ATTACK_LEFT,
        ATTACK_CENTRAL,
        ATTACK_RIGHT
    }

    protected enum RatingContributionParameterSet {
        SIDE_DEFENCE,
        CENTRAL_DEFENCE,
        MIDFIELD,
        SIDE_ATTACK,
        CENTRAL_ATTACK
    }

    public enum SideRestriction {
        NONE,           // all sides contribute to the ratings
        THIS_SIDE_ONLY,
        MIDDLE_ONLY,
        OPPOSITE_SIDE_ONLY
    }

    public enum Side {
        LEFT,
        MIDDLE,
        RIGHT
    }

    private final Team team;
    private long teamRatingRevision = -1;

    /**
     * Create a new rating prediction model
     * The team object not only provides the parameters necessary to calculate lineup rations as team spirit and confidence
     * but also a rating revision number which changes if one of the relevant parameters was changed.
     *
     * @param team Team
     */
    public RatingPredictionModel(Team team) {
        if (ratingContributionParameterMap == null) {
            initRatingContributionParameterMap();
        }
        this.team = team;
    }

    /**
     * The lineup rating cache stores sector ratings of all relevant match minutes.
     * is cleared each time the lineup rating revision number is changed.
     * If the requested sector and minute of the lineup rating is not available, the corresponding
     * calculation is called and its result is stored in the cache.
     * Rating revision -> sector -> minute -> rating
     */
    private final LineupRatingCache ratingCache = new LineupRatingCache() {
        @Override
        public double calc(Lineup lineup, RatingSector s, Integer minute) {
            return calcSectorRating(lineup, s, minute);
        }
    };

    /**
     * Get rating
     * The team rating revision is checked. If team parameters were changed the rating cache is cleared.
     *
     * @param lineup match order
     * @param s      rating sector
     * @param minute match minute [0...120]
     * @return double
     */
    public double getRating(Lineup lineup, RatingSector s, int minute) {
        if (team.getRatingRevision() != teamRatingRevision) {
            ratingCache.clear();
            teamRatingRevision = team.getRatingRevision();
        }
        return ratingCache.get(lineup, s, minute);
    }

    /**
     * The lineup average rating cache stores sector's average rating for 90 or 120 match minutes.
     */
    private final LineupRatingCache averageRatingCache = new LineupRatingCache() {
        @Override
        public double calc(Lineup lineup, RatingSector s, Integer minutes) {
            return calcAverageRating(lineup, s, minutes);
        }
    };

    /**
     * Get match average rating
     * The team rating revision is checked. If team parameters were changed the rating cache is cleared.
     *
     * @param lineup  match order
     * @param s       rating sector
     * @param minutes match duration to calculate average for [90|120]
     * @return double
     */
    public double getAverageRating(Lineup lineup, RatingSector s, int minutes) {
        if (team.getRatingRevision() != teamRatingRevision) {
            ratingCache.clear();
            teamRatingRevision = team.getRatingRevision();
        }
        return averageRatingCache.get(lineup, s, minutes);
    }

    /**
     * Get a list of minutes when the rating of the lineup possibly changes.
     * This happens every 5 minutes and each time the lineup changes during the match (e. g. substitutions happens)
     *
     * @param lineup  match order
     * @param minutes match duration to calculate average for [90|120]
     * @return TreeSet of integer
     */
    static public @NotNull TreeSet<Integer> getRatingChangeMinutes(Lineup lineup, int minutes) {
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

    /**
     * Calculate the rating of a lineup sector
     * The contributions of all players of the lineup in the requested match minute are added, taking into account
     * the overcrowding penalty at the specified match minute.
     * The sum is multiplied by the sector related factors (team factors, lineup settings)
     *
     * @param lineup match order
     * @param s      rating sector
     * @param minute match minute
     * @return double
     */
    protected double calcSectorRating(Lineup lineup, RatingSector s, int minute) {
        if (copyrightSchumTranslated == null) {
            // the author of the formulas";
            copyrightSchumTranslated = "© Schum - " + HOVerwaltung.instance().getLanguageString("ls.copyright.authoroftheformulas");
        }
        addCopyright(copyrightSchumTranslated);

        this.manMarkingOrder = lineup.getManMarkingOrder();
        this.manMarkingPosition = lineup.getManMarkingPosition();
        this.weather = lineup.getWeather();
        this.tacticType = lineup.getTacticType();

        var ret = 0.;
        var positions = lineup.getFieldPlayers(minute);
        for (var p : positions) {
            if (p.getPlayerId() != 0) {
                var overcrowdingPenalty = getOvercrowdingPenalty(countPlayersInSector(positions, p.getSector()), p.getSector());
                var contribution = getPositionContribution(p, s, minute, overcrowdingPenalty);
                ret += contribution;
            }
        }
        ret *= calcSector(lineup, s);
        return calcRatingSectorScale(s, ret);
    }

    /**
     * Transform skill scale to rating sector scale
     *
     * @param s   Rating sector
     * @param ret Skill scale rating sum
     * @return Sector rating
     */
    protected double calcRatingSectorScale(RatingSector s, double ret) {
        if (ret > 0) {
            ret *= getRatingSectorScaleFactor(s);
            return pow(ret, 1.2) / 4. + 1.;
        }
        return 0.75;
    }

    /**
     * Get rating sector scaling factor
     *
     * @param s Rating sector
     * @return scaling factor
     */
    protected double getRatingSectorScaleFactor(RatingSector s) {
        return switch (s) {
            case MIDFIELD -> .312;
            case DEFENCE_LEFT, DEFENCE_RIGHT -> .834;
            case DEFENCE_CENTRAL -> .501;
            case ATTACK_CENTRAL -> .513;
            case ATTACK_LEFT, ATTACK_RIGHT -> .615;
        };
    }

    /**
     * Calculate the effect of the lineup settings to the sector ratings
     *
     * @param lineup Lineup
     * @param s      Rating sector
     * @return Sector factor
     */
    protected double calcSector(Lineup lineup, @NotNull RatingSector s) {
        var r = 1.;
        switch (s) {
            case MIDFIELD -> {
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
            case DEFENCE_LEFT, DEFENCE_RIGHT -> {
                r *= calcTrainer(s, lineup.getCoachModifier());
                switch (MatchTacticType.fromInt(lineup.getTacticType())) {
                    case AttackInTheMiddle -> r *= 0.85;
                    case PlayCreatively -> r *= 0.93;
                    default -> {
                    }
                }
            }
            case DEFENCE_CENTRAL -> {
                r *= calcTrainer(s, lineup.getCoachModifier());
                switch (MatchTacticType.fromInt(lineup.getTacticType())) {
                    case AttackInWings -> r *= 0.85;
                    case PlayCreatively -> r *= 0.93;
                    default -> {
                    }
                }
            }
            case ATTACK_CENTRAL, ATTACK_LEFT, ATTACK_RIGHT -> {
                r *= calcTrainer(s, lineup.getCoachModifier());
                if (Objects.requireNonNull(MatchTacticType.fromInt(lineup.getTacticType())) == MatchTacticType.LongShots) {
                    r *= 0.96;
                }
                r *= calcConfidence(team.getConfidence());
            }
        }
        return r;
    }

    /**
     * Calculate the confidence factor
     *
     * @param confidence Confidence value without any sublevel (.5 is used)
     * @return Confidence factor
     */
    protected double calcConfidence(double confidence) {
        return 0.8 + 0.05 * (confidence + .5);
    }

    /**
     * Calculate the team spirit factor
     *
     * @param teamSpirit Team spirit including any sublevel
     * @return Team spirit factor
     */
    protected double calcTeamSpirit(double teamSpirit) {
        return 0.1 + 0.425 * sqrt(teamSpirit);
    }

    public List<String> getCopyrights() {
        return copyrights;
    }

    private final List<String> copyrights = new ArrayList<>();
    private String copyrightSchumTranslated = null;
    protected  void addCopyright(String cr){
        if (!this.copyrights.contains(cr)) {
            this.copyrights.add(cr);
        }
    }

    /**
     * Get Player of lineup position
     * If the player is man marking a copy with reduced skill values is returned, if match minute is more than 5 minutes after match entry
     * @param p Match lineup position
     * @param minute match minute
     * @return Player, null, if position is an empty one
     */
    protected Player getPlayer(MatchLineupPosition p, int minute){
        var ret = p.getPlayer();
        if ( ret != null){
            if (manMarkingOrder != null &&
                    ret.getPlayerId() == manMarkingOrder.getSubjectPlayerID() &&
                    p.getStartMinute() + 5 <= minute    // man marking starts 5 minutes after player enters the match
            ) {
                // create player clone with reduced skill values
                return ret.getPlayerAsManMarker(manMarkingPosition);
            }
        }
        return ret;
    }

    /**
     * Get the rating contribution of a single player in lineup.
     * © Schum - the author of the formulas,
     * If the player's lineup position contributes to the given sector, this contribution is
     * multiplied with the given overcrowding penalty, then
     * add the calculated experience contribution of the sector, then
     * multiply the weather effect and
     * multiply the stamina effect
     *
     * @param player              Player
     * @param roleId              the lineup position of the player
     * @param behaviour           the behaviour, orientation of the player (offensive, defensive, towards middle, towards wing)
     * @param sector              rating sector
     * @param minute              match minute
     * @param startMinute         player's match start minute (0 or substitution time)
     * @param overcrowdingPenalty overcrowding factor of middle sectors
     * @return double
     */
    protected double getPositionContribution(Player player, int roleId, byte behaviour, RatingSector sector, int minute, int startMinute, double overcrowdingPenalty) {
        if (player == null) return 0.;
//        var isRightHandSidePosition = isRightHandSidePosition(roleId);
//        var p = isRightHandSidePosition?togglePositionSide(roleId):roleId;
//        var s = isRightHandSidePosition?toggleRatingSectorSide(sector):sector;

        var contribution = contributionCache.get(sector, roleId, player, behaviour);
        if (contribution > 0) {
            contribution *= overcrowdingPenalty;
            var exp = experienceCache.get(player.getSkillValue(EXPERIENCE), sector);
            contribution += exp;
            contribution *= weatherCache.get(Specialty.getSpecialty(player.getSpecialty()), weather);
            contribution *= staminaCache.get((double) player.getStamina(), minute, startMinute, tacticType);

//            if ( minute == 0) {
//                HOLogger.instance().debug(getClass(), "getPositionContribution " + player.getFullName()
//                        + " " + MatchRoleID.getNameForPosition(MatchRoleID.getPosition(roleId, behaviour))
//                        + " " + weather.toString()
//                        + " " + tacticType
//                        + " " + sector.toString()
//                        + " minute " + minute
//                        + " k(P)=" + overcrowdingPenalty
//                        + " (S+L)*K(F)*C=" + c
//                        + " (S+L)*K(F)*C*K(P)=" + p
//                        + " +Exp " + exp
//                        + "= " + contribution
//                );
//            }
        }
        return contribution;
    }

    private double getPositionContribution(@NotNull MatchLineupPosition p, RatingSector s, int minute, double overcrowdingPenalty) {
        return getPositionContribution(getPlayer(p, minute), p.getRoleId(), p.getBehaviour(), s, minute, p.getStartMinute(), overcrowdingPenalty);
    }

    private double getPositionContribution(Player p, int roleId, byte behaviour, RatingSector s, int minute) {
        return getPositionContribution(p, roleId, behaviour, s, minute, 0, 1.);
    }

    /**
     * A map of contribution factors of each player to the different rating sectors
     * If the value is not available in the cache it is calculated and stored in the cache.
     */
    RatingCalculationCache4<RatingSector, Integer, Player, Byte> contributionCache = new RatingCalculationCache4<>() {
        @Override
        public double calc(RatingSector sector, Integer roleId, Player player, Byte behaviour) {
            return calcContribution(player, roleId, behaviour, sector);
        }
    };

    /**
     * Get value from contribution cache
     * (Attention: Method is used by AkasolaceRatingPredictionModel.groovy)
     * @param p Player
     * @param roleId Lineup position
     * @param behaviour Behaviour
     * @param s Rating sector
     * @return double
     */
    protected double getContribution(Player p, Integer roleId, Byte behaviour, RatingSector s){
        return contributionCache.get(s, roleId, p, behaviour);
    }

    /**
     * Calculate the player's contribution factor to a given rating sector.
     * The relevant rating contribution parameters of the rating sector are examined.
     * For each lineup sector in the parameter list it is checked if the player's position is in the sector.
     * In that case the side restrictions of the parameters are checked if they were fulfilled by the player's position.
     * In that case if the behaviour and specialty corresponding factor is available it is multiplied with the
     * calculated player strength of the skill type from the parameter map.
     *
     * @param player    Player
     * @param roleId    Player's match position
     * @param behaviour Player's behaviour
     * @param s         Rating sector
     * @return double
     */
    protected double calcContribution(Player player, int roleId, Byte behaviour, RatingSector s) {
        var ret = 0.;
        if (player != null) {
            var params = ratingSectorParameterMap.get(s);
            if (params != null) {
                var side = params.side;
                var contributions = params.contributionParameter;
                var factor = ratingContributionParameterMap.get(contributions);
                for (var f : factor.entrySet()) {
                    var skillType = f.getKey();
                    var lineupSectors = f.getValue();
                    for (var lineupSector : lineupSectors.entrySet()) {
                        var sector = lineupSector.getKey();
                        if (MatchRoleID.getSector(roleId) == sector) {
                            var sideRestrictions = lineupSector.getValue();
                            for (var r : sideRestrictions.entrySet()) {
                                var sideRestriction = r.getKey();
                                if (!isRoleSideRestricted(roleId, side, sideRestriction)) {
                                    var behaviours = r.getValue();
                                    var specialties = behaviours.get(behaviour);
                                    if (specialties != null) {
                                        var c = specialties.get(Specialty.getSpecialty(player.getSpecialty()));
                                        var strength = calcStrength(player, skillType);
                                        var res = strength * c;
                                        ret += res;
//                                        HOLogger.instance().debug(getClass(), "calcContribution " + player.getFullName()
//                                                + " " + s.toString()
//                                                + " " + PlayerSkill.toString(skillType)
//                                                + " (S+L)*K(F)=" + strength
//                                                + " C=" + c
//                                                + " (S+L)*K(F)*C=" + res
//                                                + " Sum=" + ret
//                                        );
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

    /**
     * The rating calculation parameter stores the combination of
     * rating contribution parameter key (side defence, central defence, midfield, side attack, central attack)
     * and side key (left, middle, right)
     * for each rating sector.
     */
    private static class RatingCalculationParameter {
        public RatingContributionParameterSet contributionParameter;
        public Side side;

        public RatingCalculationParameter(RatingContributionParameterSet p, Side s) {
            contributionParameter = p;
            side = s;
        }
    }

    /**
     * The parameter map containing all ratings sectors' parameter keys.
     */
    private static final Map<RatingSector, RatingCalculationParameter> ratingSectorParameterMap = Map.ofEntries(
            entry(RatingSector.DEFENCE_LEFT, new RatingCalculationParameter(RatingContributionParameterSet.SIDE_DEFENCE, Side.LEFT)),
            entry(RatingSector.DEFENCE_CENTRAL, new RatingCalculationParameter(RatingContributionParameterSet.CENTRAL_DEFENCE, Side.MIDDLE)),
            entry(RatingSector.DEFENCE_RIGHT, new RatingCalculationParameter(RatingContributionParameterSet.SIDE_DEFENCE, Side.RIGHT)),
            entry(RatingSector.MIDFIELD, new RatingCalculationParameter(RatingContributionParameterSet.MIDFIELD, Side.MIDDLE)),
            entry(RatingSector.ATTACK_LEFT, new RatingCalculationParameter(RatingContributionParameterSet.SIDE_ATTACK, Side.LEFT)),
            entry(RatingSector.ATTACK_CENTRAL, new RatingCalculationParameter(RatingContributionParameterSet.CENTRAL_ATTACK, Side.MIDDLE)),
            entry(RatingSector.ATTACK_RIGHT, new RatingCalculationParameter(RatingContributionParameterSet.SIDE_ATTACK, Side.RIGHT))
    );

    /**
     * Map of the rating contribution factors
     * contains entries for
     * the rating contribution parameters (Side defense, central defense, midfield, side attack, central attack)
     * the skill types
     * the side restriction (none, this side only, middle only, opposite side only)
     * the player's behaviour (normal, offensive, defensive, towards middle, towards wing)
     * the player's specialty (none, powerful, ...)
     * the contribution factor
     */
    protected static Map<RatingContributionParameterSet,
            Map<PlayerSkill,
                    Map<MatchRoleID.Sector,
                            Map<SideRestriction,
                                    Map<Byte,
                                            Map<Specialty, Double>
                                            >
                                    >
                            >
                    >
            > ratingContributionParameterMap = null;

    /**
     * Initialize the rating contribution factor map
     */
    protected static void initRatingContributionParameterMap() {
        ratingContributionParameterMap = new HashMap<>();
        double factorSideDefence = 1.; //  .25;
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.KEEPER, MatchRoleID.Sector.Goal, SideRestriction.NONE, NORMAL, .61 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Goal, SideRestriction.NONE, NORMAL, .25 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.THIS_SIDE_ONLY, NORMAL, .52 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.THIS_SIDE_ONLY, OFFENSIVE, .4 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.THIS_SIDE_ONLY, TOWARDS_WING, .81 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.MIDDLE_ONLY, NORMAL, .26 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.MIDDLE_ONLY, OFFENSIVE, .2 * factorSideDefence);

        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.THIS_SIDE_ONLY, NORMAL, .92 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.THIS_SIDE_ONLY, OFFENSIVE, .74 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.THIS_SIDE_ONLY, DEFENSIVE, factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.THIS_SIDE_ONLY, TOWARDS_MIDDLE, .75 * factorSideDefence);

        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, NORMAL, .19 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, OFFENSIVE, .09 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, DEFENSIVE, .27 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, TOWARDS_WING, .24 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.MIDDLE_ONLY, NORMAL, .095 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.MIDDLE_ONLY, OFFENSIVE, .045 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.MIDDLE_ONLY, DEFENSIVE, .135 * factorSideDefence);

        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, NORMAL, .35 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, OFFENSIVE, .22 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, DEFENSIVE, .61 * factorSideDefence);
        initAllSpecialties(RatingContributionParameterSet.SIDE_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, TOWARDS_MIDDLE, .29 * factorSideDefence);

        double factorCentralDefence = 1.; // .14;
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.KEEPER, MatchRoleID.Sector.Goal, SideRestriction.NONE, NORMAL, .87 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Goal, SideRestriction.NONE, NORMAL, .35 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.NONE, NORMAL, factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.NONE, OFFENSIVE, .73 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.NONE, TOWARDS_WING, .67 * factorCentralDefence);

        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.NONE, NORMAL, .38 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.NONE, OFFENSIVE, .35 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.NONE, DEFENSIVE, .43 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.NONE, TOWARDS_MIDDLE, .7 * factorCentralDefence);

        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, NORMAL, .4 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, OFFENSIVE, .16 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, DEFENSIVE, .58 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, TOWARDS_WING, .33 * factorCentralDefence);

        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.NONE, NORMAL, .2 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.NONE, OFFENSIVE, .13 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.NONE, DEFENSIVE, .25 * factorCentralDefence);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_DEFENCE, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.NONE, TOWARDS_MIDDLE, .25 * factorCentralDefence);

        double factorMidfield = 1.; // .09;
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.NONE, NORMAL, .25 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.NONE, OFFENSIVE, .4 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.NONE, TOWARDS_WING, .15 * factorMidfield);

        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.NONE, NORMAL, .15 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.NONE, OFFENSIVE, .2 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.NONE, DEFENSIVE, .1 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.NONE, TOWARDS_MIDDLE, .2 * factorMidfield);

        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, NORMAL, 1 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, OFFENSIVE, .95 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, DEFENSIVE, .95 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, TOWARDS_WING, .9 * factorMidfield);

        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.NONE, NORMAL, .45 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.NONE, OFFENSIVE, .3 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.NONE, DEFENSIVE, .3 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.NONE, TOWARDS_MIDDLE, .55 * factorMidfield);

        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.NONE, NORMAL, .25 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.NONE, DEFENSIVE, .35 * factorMidfield);
        initAllSpecialties(RatingContributionParameterSet.MIDFIELD, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.NONE, TOWARDS_WING, .15 * factorMidfield);

        double factorCentralAttack = 1.; // .13;
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, NORMAL, .33 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, OFFENSIVE, .49 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, DEFENSIVE, .18 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, TOWARDS_WING, .23 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.NONE, NORMAL, .11 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.NONE, OFFENSIVE, .13 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.NONE, DEFENSIVE, .05 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.NONE, TOWARDS_MIDDLE, .16 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.NONE, NORMAL, .33 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.NONE, DEFENSIVE, .53 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.NONE, TOWARDS_WING, .23 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, NORMAL, .22 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, OFFENSIVE, .31 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.NONE, DEFENSIVE, .13 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.NONE, NORMAL, factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.NONE, DEFENSIVE, .56 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.CENTRAL_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.NONE, TOWARDS_WING, .66 * factorCentralAttack);

        double factorSideAttack = 1.; // .45;
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.MIDDLE_ONLY, NORMAL, .13 * factorSideAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.MIDDLE_ONLY, OFFENSIVE, 0.18 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.MIDDLE_ONLY, DEFENSIVE, 0.07 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.NONE, NORMAL, .14 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.NONE, DEFENSIVE, 0.31 * factorCentralAttack);
        initRatingContributionParameter(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.NONE, DEFENSIVE, Specialty.Technical, 0.41 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, NORMAL, .26 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, OFFENSIVE, 0.36 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, DEFENSIVE, 0.14 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, TOWARDS_WING, 0.31 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, NORMAL, .26 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, OFFENSIVE, .29 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, DEFENSIVE, 0.21 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, TOWARDS_MIDDLE, 0.15 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.THIS_SIDE_ONLY, TOWARDS_WING, 0.21 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.OPPOSITE_SIDE_ONLY, TOWARDS_WING, 0.06 * factorCentralAttack);


        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.CentralDefence, SideRestriction.THIS_SIDE_ONLY, TOWARDS_WING, 0.26 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.THIS_SIDE_ONLY, NORMAL, .59 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.THIS_SIDE_ONLY, OFFENSIVE, .69 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.THIS_SIDE_ONLY, DEFENSIVE, .45 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.THIS_SIDE_ONLY, TOWARDS_MIDDLE, .35 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.InnerMidfield, SideRestriction.THIS_SIDE_ONLY, TOWARDS_WING, .59 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, NORMAL, .86 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, OFFENSIVE, factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, DEFENSIVE, .69 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.THIS_SIDE_ONLY, TOWARDS_MIDDLE, .74 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.NONE, NORMAL, .24 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.NONE, DEFENSIVE, .13 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.THIS_SIDE_ONLY, TOWARDS_WING, .64 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.OPPOSITE_SIDE_ONLY, TOWARDS_WING, .21 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.NONE, NORMAL, .27 * factorCentralAttack);
        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.NONE, DEFENSIVE, .13 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.OPPOSITE_SIDE_ONLY, TOWARDS_WING, .19 * factorCentralAttack);

        initAllSpecialties(RatingContributionParameterSet.SIDE_ATTACK, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.THIS_SIDE_ONLY, TOWARDS_WING, .51 * factorCentralAttack);
    }

    /**
     * Initialize a rating contribution parameter
     *
     * @param ratingContributionParameter SIDE_DEFENCE, CENTRAL_DEFENCE, MIDFIELD, SIDE_ATTACK, CENTRAL_ATTACK
     * @param skill KEEPER, DEFENDING, PLAYMAKING, PASSING, WINGER, SCORING
     * @param sector Goal, CentralDefence, Back, InnerMidfield, Wing, Forward
     * @param sideRestriction NONE, THIS_SIDE_ONLY, MIDDLE_ONLY, OPPOSITE_SIDE_ONLY
     * @param behaviour NORMAL, OFFENSIVE, DEFENSIVE, TOWARDS_MIDDLE, TOWARDS_WING
     * @param specialty NoSpecialty, Technical, Quick, Powerful, Unpredictable, Head, Regainer, Support
     * @param v Double
     */
    protected static void initRatingContributionParameter(RatingContributionParameterSet ratingContributionParameter, PlayerSkill skill, MatchRoleID.Sector sector, SideRestriction sideRestriction, byte behaviour, Specialty specialty, double v) {
        var sr = initSideRestriction(ratingContributionParameter, skill, sector, sideRestriction);
        var specialties = sr.computeIfAbsent(behaviour, k -> new HashMap<>());
        specialties.put(specialty, v);
    }

    /**
     * Get a rating contribution parameter
     *
     * @param ratingContributionParameter SIDE_DEFENCE, CENTRAL_DEFENCE, MIDFIELD, SIDE_ATTACK, CENTRAL_ATTACK
     * @param skill KEEPER, DEFENDING, PLAYMAKING, PASSING, WINGER, SCORING
     * @param sector Goal, CentralDefence, Back, InnerMidfield, Wing, Forward
     * @param sideRestriction NONE, THIS_SIDE_ONLY, MIDDLE_ONLY, OPPOSITE_SIDE_ONLY
     * @param behaviour NORMAL, OFFENSIVE, DEFENSIVE, TOWARDS_MIDDLE, TOWARDS_WING
     * @param specialty NoSpecialty, Technical, Quick, Powerful, Unpredictable, Head, Regainer, Support
     * @return double
     */
    protected static Double getRatingContributionParameter(RatingContributionParameterSet ratingContributionParameter, int skill, MatchRoleID.Sector sector, SideRestriction sideRestriction, byte behaviour, Specialty specialty) {
        return ratingContributionParameterMap.get(ratingContributionParameter).get(skill).get(sector).get(sideRestriction).get(behaviour).get(specialty);
    }

    private static void initAllSpecialties(RatingContributionParameterSet ratingContributionParameter, PlayerSkill skill, MatchRoleID.Sector sector, SideRestriction sideRestriction, byte behaviour, double v) {
        Map<Specialty, Double> specialtyMap = new HashMap<>();
        for (var specialty : Specialty.values()) {
            specialtyMap.put(specialty, v);
        }
        var sr = initSideRestriction(ratingContributionParameter, skill, sector, sideRestriction);
        sr.put(behaviour, specialtyMap);
    }

    private static Map<Byte, Map<Specialty, Double>> initSideRestriction(RatingContributionParameterSet ratingContributionParameter, PlayerSkill skill, MatchRoleID.Sector sector, SideRestriction sideRestriction) {
        var p = ratingContributionParameterMap.computeIfAbsent(ratingContributionParameter, k -> new HashMap<>());
        var s = p.computeIfAbsent(skill, k -> new HashMap<>());
        var se = s.computeIfAbsent(sector, k -> new HashMap<>());
        return se.computeIfAbsent(sideRestriction, k -> new HashMap<>());
    }

    /**
     * A map of overcrowding penalties of the central lineup sectors
     */
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

    /**
     * Get the overcrowding penalty factor
     * If no penalty factor is found in the map, the factor 1 is returned.
     *
     * @param countPlayersInSector Player count of the lineup sector
     * @param sector               Lineup sector
     * @return double
     */
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

    /**
     * Get player count of a lineup sector
     *
     * @param positions Lineup positions
     * @param sector    Lineup sector
     * @return Number of players in given lineup sector
     */
    protected int countPlayersInSector(List<MatchLineupPosition> positions, MatchRoleID.Sector sector) {
        return (int)positions.stream().filter(p->p.getPlayerId() > 0 && p.getSector() == sector).count();
    }

    /**
     * The stamina cache stores rating correction factors depending on
     * stamina skill value
     * match minute
     * player's match start minute
     * match tactic
     * If the value is not found in cache, it is calculated and stored in the cache
     */
    RatingCalculationCache4<Double, Integer, Integer, Integer> staminaCache = new RatingCalculationCache4<>() {
        @Override
        public double calc(Double stamina, Integer minute, Integer startMinute, Integer tacticType) {
            return calcStamina(stamina, minute, startMinute, tacticType);
        }
    };
    protected double getStamina(double stamina, int minute, int startMinute, int tacticType){
        return staminaCache.get(stamina, minute, startMinute, tacticType);
    }

    /**
     * Cache of experience rating contribution to a rating sector
     * If the requested value is missing, it will be calculated.
     */
    RatingCalculationCache2<Double, RatingSector> experienceCache = new RatingCalculationCache2<>() {
        @Override
        public double calc(Double skillValue, RatingSector ratingSector) {
            return calcExperience(ratingSector, skillValue);
        }
    };

    /**
     * Calculate the experience rating contribution to a rating sector (Eff(Exp))
     *
     * @param ratingSector Rating sector
     * @param skillValue   Experience skill value
     * @return Experience rating contribution to the rating sector
     */
    protected double calcExperience(@NotNull RatingSector ratingSector, double skillValue) {
        var exp = calcSkillRating(skillValue);
        var k = -0.00000725 * pow(exp, 4) + 0.0005 * pow(exp, 3) - .01336 * pow(exp, 2) + 0.176 * exp;

        switch (ratingSector) {
            case DEFENCE_LEFT, DEFENCE_RIGHT -> {
                return k * 0.345;
            }
            case DEFENCE_CENTRAL -> {
                return k * 0.48;
            }
            case MIDFIELD -> {
                return k * 0.73;
            }
            case ATTACK_LEFT, ATTACK_RIGHT -> {
                return k * 0.375;
            }
            case ATTACK_CENTRAL -> {
                return k * 0.450;
            }
            default -> throw new IllegalStateException("Unexpected value: " + ratingSector);
        }
    }

    /**
     * Calculate the stamina factor
     *
     * @param stamina     Stamina skill value
     * @param minute      Match minute
     * @param startMinute Player's match start minute
     * @param tacticType  Match tactic
     * @return double
     */
    protected double calcStamina(double stamina, int minute, int startMinute, int tacticType) {
        var p = tacticType == TAKTIK_PRESSING ? 1.1 : 1.;
        var s = calcSkillRating(stamina);
        double r0, delta;
        if (s < 7) {
            r0 = 102. + 23. / 7. * s;
            delta = p * (27. / 70. * s - 5.95);
        } else {
            r0 = 102. + 23. + (s - 7.) * 100. / 7.;
            delta = -3.25 * p;
        }

        var r = r0;
        var to = min(45, minute);
        if (startMinute < to) {
            r += (to - startMinute) * delta / 5.;
        }
        var from = max(45, startMinute);
        if (minute >= 45) {
            if (startMinute < 45) {
                r = min(r0, r + 120.75 - 102);
            }
            to = min(90, minute);
            if (from < to) {
                r += (to - from) * delta / 5.;
            }
        }
        if (minute >= 90) {
            from = max(90, startMinute);
            if (startMinute < 90) {
                r = min(r0, r + 127 - 120.75);
            }
            if (from < minute) {
                r += (minute - from) * delta / 5.;
            }
        }
        return min(1, r / 100.);
    }

    static List<Integer> rhsPositions = List.of(rightBack, rightCentralDefender, rightInnerMidfield, rightWinger, rightForward);

    boolean isRightHandSidePosition(int roleId) {
        return rhsPositions.contains(roleId);
    }

    int togglePositionSide(int roleId) {
        switch (roleId) {
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

    RatingPredictionModel.RatingSector toggleRatingSectorSide(@NotNull RatingPredictionModel.RatingSector s) {
        switch (s) {
            case DEFENCE_LEFT -> {
                return RatingPredictionModel.RatingSector.DEFENCE_RIGHT;
            }
            case DEFENCE_RIGHT -> {
                return RatingPredictionModel.RatingSector.DEFENCE_LEFT;
            }
            case ATTACK_LEFT -> {
                return RatingPredictionModel.RatingSector.ATTACK_RIGHT;
            }
            case ATTACK_RIGHT -> {
                return RatingPredictionModel.RatingSector.ATTACK_LEFT;
            }
        }
        return s;
    }

    /**
     * Get the player rating
     * Multiplying the weather factor to the weather independent rating value.
     *
     * @param p         Player
     * @param roleId    lineup position
     * @param behaviour behaviour
     * @param minute    match minute
     * @param weather   weather
     * @return Player rating value
     */
    public double getPlayerRating(@NotNull Player p, int roleId, byte behaviour, int minute, Weather weather) {
        return weatherCache.get(Specialty.getSpecialty(p.getSpecialty()), weather)
                * getPlayerRating(p, roleId, behaviour, minute);
    }

    /**
     * Get the weather independent player rating.
     *
     * @param p         Player
     * @param roleId    lineup position
     * @param behaviour behaviour
     * @param minute    match minute
     * @return Weather independent player rating
     */
    public double getPlayerRating(Player p, int roleId, byte behaviour, int minute) {
        return playerRatingCache.get(p, togglePositionSide(roleId), behaviour, minute); // calc left sides only
    }

    /**
     * Get the weather independent player rating at match beginning
     *
     * @param p         Player
     * @param roleId    lineup position
     * @param behaviour behaviour
     * @return Weather independent player rating at match beginning
     */
    public double getPlayerRatingMatchBeginning(Player p, int roleId, byte behaviour) {
        return playerRatingCache.get(p, togglePositionSide(roleId), behaviour, 0);
    }

    public double getPlayerRatingMatchBeginning(Player p, byte positionWithBehaviour) {
        return playerRatingCache.get(p, getPlayerRatingPosition(positionWithBehaviour), getBehaviour(positionWithBehaviour), 0);
    }

    /**
     * The cache of player rating results.
     * If the requested value is missing, it will be calculated.
     */
    RatingCalculationCache4<Player, Integer, Byte, Integer> playerRatingCache = new RatingCalculationCache4<>() {
        @Override
        public double calc(Player player, Integer roleId, Byte behaviour, Integer minute) {
            return calcPlayerRating(player, roleId, behaviour, minute);
        }
    };

    /**
     * Calculate the sum of the player's rating contributions to all rating sectors
     * Midfield contribution is scaled with a factor of 3 (like hatstats formula)
     * Result is scaled to player rating scale.
     *
     * @param p Player
     * @return double
     */
    protected double calcPlayerRating(Player p, int roleId, byte behaviour, int minute) {
        this.manMarkingOrder = null;
        var ret = 0.;
        for (var s : RatingSector.values()) {
            var c = getPositionContribution(p, roleId, behaviour, s, minute);
            c *= getRatingSectorScaleFactor(s);
            if (s == RatingSector.MIDFIELD) c *= 3; // fit to hatstats
            ret += c;
        }
        return pow(ret, 1.2) / 4.;
    }

    /**
     * Get the player's weather independent match average rating
     *
     * @param p                     Player
     * @param positionWithBehaviour Lineup position and behaviour
     * @return double
     */
    public double getPlayerMatchAverageRating(Player p, byte positionWithBehaviour) {
        return getPlayerRatingMatchBeginning(p, getPlayerRatingPosition(positionWithBehaviour), getBehaviour(positionWithBehaviour)) * getMatchAverageStaminaFactor(p.getSkill(STAMINA));
    }

    /**
     * Get the player's weather independent match average rating
     *
     * @param p Player
     * @param roleId Lineup position
     * @param behaviour Behaviour
     * @return Double
     */
    public double getPlayerMatchAverageRating(Player p, int roleId, byte behaviour) {
        return getPlayerRatingMatchBeginning(p, roleId, behaviour) * getMatchAverageStaminaFactor(p.getSkill(STAMINA));
    }

    /**
     * Get the player's weather dependent match average rating
     *
     * @param p Player
     * @param roleId Lineup position
     * @param behaviour Behaviour
     * @param weather Weather
     * @return Double
     */
    public double getPlayerMatchAverageRating(Player p, int roleId, byte behaviour, Weather weather) {
        return weatherCache.get(Specialty.getSpecialty(p.getSpecialty()), weather)
                * getPlayerMatchAverageRating(p, roleId, behaviour);
    }

    /**
     * Map of match average stamina factors
     * If the requested value is not contained, it is calculated and added to the map.
     */
    private final RatingCalculationCache<Double> matchAverageStaminaFactorCache = new RatingCalculationCache<>() {
        @Override
        public double calc(Double stamina) {
            return calcMatchAverageStaminaFactor(stamina);
        }
    };

    /**
     * Get the match average stamina factor
     *
     * @param skill Stamina skill value
     * @return double
     */
    private double getMatchAverageStaminaFactor(double skill) {
        return matchAverageStaminaFactorCache.get(skill);
    }

    /**
     * Calculate the match average stamina factor
     * Formula fitting the values published by Schum.
     *
     * @param stamina Stamina skill value
     * @return double
     */
    protected double calcMatchAverageStaminaFactor(double stamina) {
        var ret = -.0033 * stamina * stamina + .085 * stamina + .51;
        return min(1., ret);
    }

    public double getPlayerRatingEndOfMatch(Player p, int roleId, byte behaviour) {
        return playerRatingCache.get(p, togglePositionSide(roleId), behaviour, 90);
    }

    public double getPlayerRatingEndOfExtraTime(Player p, int roleId, byte behaviour) {
        return playerRatingCache.get(p, togglePositionSide(roleId), behaviour, 120);
    }

    /**
     * Calculate the player rating related to an optimal reference player
     *
     * @param p         PLayer
     * @param roleId    Lineup position
     * @param behaviour Behaviour
     * @param minute    match minute
     * @return Double
     */
    public double calcRelativePlayerRating(Player p, int roleId, byte behaviour, int minute) {
        Player reference;
        if (roleId == KEEPER) {
            reference = Player.getReferenceKeeper();
        } else {
            reference = Player.getReferencePlayer();
        }
        return getPlayerRating(p, roleId, behaviour, minute) / getPlayerRating(reference, roleId, behaviour, minute);
    }

    /**
     * Cache of player's tactic strength
     * Player->Skill->strength (Double)
     */
    RatingCalculationCache2<Player, PlayerSkill> playerTacticStrengthCache = new RatingCalculationCache2<>() {
        @Override
        public double calc(Player player, PlayerSkill skill) {
            return calcPlayerTacticStrength(player, skill);
        }

    };

    /**
     * Cache of specialty specific weather impact
     * Specialty->Weather->factor (Double)
     */
    RatingCalculationCache2<Specialty, Weather> weatherCache = new RatingCalculationCache2<>() {
        @Override
        public double calc(Specialty specialty, Weather weather) {
            return calcWeather(specialty, weather);
        }
    };

    /**
     * Calculate player's tactic strength of given skill
     * @param player Player
     * @param skill Skill
     * @return Double
     */
    protected double calcPlayerTacticStrength(Player player, PlayerSkill skill) {
        var ret = calcStrength(player, skill);
        var xp = calcSkillRating(player.getSkillValue(EXPERIENCE));
        var f = Math.log10(xp) * 4. / 3.;
        ret += f;
        return ret;
    }

    public static List<Integer> playerRatingPositions = List.of(keeper, leftBack, leftCentralDefender, leftWinger, leftInnerMidfield, leftForward);

    /**
     * Get player's position from combined value
     * @param positionWithBehaviour Byte, combined position and behaviour
     * @return Integer, left hand side position of argument
     */
    public static Integer getPlayerRatingPosition(byte positionWithBehaviour) {
        return switch (positionWithBehaviour) {
            case KEEPER -> keeper;
            case CENTRAL_DEFENDER, CENTRAL_DEFENDER_OFF, CENTRAL_DEFENDER_TOWING -> leftCentralDefender;
            case BACK, BACK_OFF, BACK_TOMID, BACK_DEF -> leftBack;
            case MIDFIELDER, MIDFIELDER_OFF, MIDFIELDER_DEF, MIDFIELDER_TOWING -> leftInnerMidfield;
            case WINGER, WINGER_OFF, WINGER_DEF, WINGER_TOMID -> leftWinger;
            case FORWARD, FORWARD_DEF, FORWARD_TOWING -> leftForward;
            default -> throw new IllegalStateException("Unexpected value: " + positionWithBehaviour);
        };
    }

    /**
     * Get player's behaviour from combined value
     * @param positionWithBehaviour Byte, combined position and behaviour
     * @return Byte, behaviour
     */
    public static Byte getBehaviour(byte positionWithBehaviour) {
        return switch (positionWithBehaviour) {
            case FORWARD, WINGER, KEEPER, MIDFIELDER, BACK, CENTRAL_DEFENDER -> NORMAL;
            case WINGER_OFF, CENTRAL_DEFENDER_OFF, MIDFIELDER_OFF, BACK_OFF -> OFFENSIVE;
            case FORWARD_TOWING, MIDFIELDER_TOWING, CENTRAL_DEFENDER_TOWING -> TOWARDS_WING;
            case WINGER_TOMID, BACK_TOMID -> TOWARDS_MIDDLE;
            case FORWARD_DEF, WINGER_DEF, MIDFIELDER_DEF, BACK_DEF -> DEFENSIVE;
            default -> throw new IllegalStateException("Unexpected value: " + positionWithBehaviour);
        };
    }

    /**
     * Map of players' penalty strength
     */
    private final Map<Player, Double> playerPenaltyMap = new HashMap<>();

    /**
     * Get player's penalty strength
     * @param player, Player
     * @return Double
     */
    public double getPlayerPenaltyStrength(Player player) {
        var ret = playerPenaltyMap.get(player);
        if (ret != null) return ret;

        ret = calcPlayerPenaltyStrength(player);
        playerPenaltyMap.put(player, ret);
        return ret;
    }

    /**
     * Calculate player's penalty strength
     * @param player, Player
     * @return Double
     */
    protected Double calcPlayerPenaltyStrength(@NotNull Player player) {
        var ret = calcSkillRating(player.getSkill(EXPERIENCE)) * 1.5;
        ret += calcStrength(player, SETPIECES) * 0.7;
        ret += calcStrength(player, SCORING) * 0.3;

        if (player.getSpecialty() == PlayerSpeciality.TECHNICAL) {
            ret *= 1.1;
        }
        return ret;
    }

    /**
     * Calculate the factor of the coach modifier
     *
     * @param s             Rating sector
     * @param coachModifier Integer value representing the style of play the team will use in the match. The value ranges from -10 (100% defensive) to 10 (100% offensive).
     * @return Double
     */
    protected double calcTrainer(@NotNull RatingSector s, int coachModifier) {
        switch (s) {
            case DEFENCE_LEFT, DEFENCE_RIGHT, DEFENCE_CENTRAL -> {
                if (coachModifier <= 0) {
                    // Balanced or Defensive
                    return 1.02 - coachModifier * (1.15 - 1.02) / 10.;
                } else {
                    // Offensive
                    return 1.02 - coachModifier * (1.02 - 0.9) / 10.;
                }
            }
            case ATTACK_CENTRAL, ATTACK_LEFT, ATTACK_RIGHT -> {
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

    /**
     * Calculate the factor of the weather impact
     * @param specialty Player's specialty
     * @param weather Weather
     * @return Double
     */
    protected double calcWeather(Specialty specialty, Weather weather) {
        if (specialty != null) {
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

    /**
     * Calculate player's skill strength, regarding loyalty and form
     * @param player, Player
     * @param playerSkill, Skill
     * @return Double
     */
    protected double calcStrength(@NotNull Player player, PlayerSkill playerSkill) {
        var skillRating = calcSkillRating(player.getSkill(playerSkill));
        var loyalty = calcLoyalty(player);
        var form = calcForm(player);
        return (skillRating + loyalty) * form;
    }

    /**
     * Calculate player's form impact on rating
     * @param player, Player
     * @return Double
     */
    protected double calcForm(@NotNull Player player) {
        var form = min(7., calcSkillRating(.5 + player.getSkill(PlayerSkill.FORM)));
        return 0.378 * sqrt(form);  // form 0.5 .. 7.0
    }

    /**
     * Calculate player's loyalty impact on rating
     * @param player, Player
     * @return Double
     */
    protected double calcLoyalty(@NotNull Player player) {
        if (player.isHomeGrown()) return 1.5;
        var loyaltyRating = calcSkillRating(player.getSkill(PlayerSkill.LOYALTY));
        return loyaltyRating / 19.;
    }

    /**
     * Calculate skill rating
     * Excellent without sub skill gives 7.0
     * @param skill, Double displayed skill value
     * @return Double [0..]
     */
    protected double calcSkillRating(double skill) {
        return max(0, skill - 1);
    }

    /**
     * Checks if lineup position doesn't contribute to given side
     * @param roleID lineup position
     * @param side Side
     * @param sideRestriction Side restriction
     * @return boolean, true, if position doesn't contribute to given side
     */
    protected boolean isRoleSideRestricted(int roleID, Side side, @NotNull SideRestriction sideRestriction) {
        switch (sideRestriction) {
            case MIDDLE_ONLY -> {
                return roleID != middleCentralDefender && roleID != centralInnerMidfield && roleID != centralForward;
            }
            case THIS_SIDE_ONLY -> {
                switch (side) {
                    case LEFT -> {
                        return roleID != leftBack && roleID != leftCentralDefender && roleID != leftWinger && roleID != leftInnerMidfield && roleID != leftForward;
                    }
                    case RIGHT -> {
                        return roleID != rightBack && roleID != rightCentralDefender && roleID != rightWinger && roleID != rightInnerMidfield && roleID != rightForward;
                    }
                    case MIDDLE -> {
                        return roleID != middleCentralDefender && roleID != centralInnerMidfield && roleID != centralForward;
                    }
                }
            }
            case OPPOSITE_SIDE_ONLY -> {
                switch (side) {
                    case RIGHT -> {
                        return roleID != leftBack && roleID != leftCentralDefender && roleID != leftWinger && roleID != leftInnerMidfield && roleID != leftForward;
                    }
                    case LEFT -> {
                        return roleID != rightBack && roleID != rightCentralDefender && roleID != rightWinger && roleID != rightInnerMidfield && roleID != rightForward;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get the player's set pieces strength
     * @param p, Player
     * @return Double
     */
    public double getPlayerSetPiecesStrength(Player p) {
        return getPlayerTacticStrength(new MatchLineupPosition(setPieces, NORMAL, p), SETPIECES, null, TAKTIK_NORMAL, 0);
    }

    /**
     * Get player's tactic strength
     * @param p, Player
     * @param playerSkill, skill type
     * @param weather, Weather
     * @param tacticType, tactic type
     * @param minute, match minute
     * @return Double
     */
    public double getPlayerTacticStrength(@NotNull MatchLineupPosition p, PlayerSkill playerSkill, Weather weather, int tacticType, int minute) {
        var player = p.getPlayer();
        if (player != null) {
            var ret = playerTacticStrengthCache.get(player, playerSkill);
            ret *= weatherCache.get(Specialty.getSpecialty(player.getSpecialty()), weather);
            ret *= staminaCache.get((double) player.getStamina(), minute, p.getStartMinute(), tacticType);
            return ret;
        }
        return 0;
    }

    /**
     * Cache of lineup hatstat values
     * lineup->minute->hatstats
     */
    private final StatsCache hatStatsCache = new StatsCache() {
        @Override
        public double calc(Lineup lineup, int minute) {
            return calcHatStats(lineup, minute);
        }
    };

    /**
     * Calculate lineup hatstats
     * @param lineup Lineup
     * @param minute match minute
     * @return Double
     */
    protected double calcHatStats(Lineup lineup, int minute) {
        double hatStats = 3 * getRating(lineup, RatingSector.MIDFIELD, minute);
        hatStats += getRating(lineup, RatingSector.DEFENCE_LEFT, minute);
        hatStats += getRating(lineup, RatingSector.DEFENCE_CENTRAL, minute);
        hatStats += getRating(lineup, RatingSector.DEFENCE_RIGHT, minute);
        hatStats += getRating(lineup, RatingSector.ATTACK_LEFT, minute);
        hatStats += getRating(lineup, RatingSector.ATTACK_CENTRAL, minute);
        hatStats += getRating(lineup, RatingSector.ATTACK_RIGHT, minute);
        return 4 * hatStats;
    }

    /**
     * Get lineup hatstats
     * @param lineup, Lineup
     * @param minute, match minute
     * @return Double
     */
    public double getHatStats(Lineup lineup, int minute) {
        return hatStatsCache.get(lineup, minute);
    }

    /**
     * Get match average hatstats
     * @param lineup, Lineup
     * @param minutes, 90 or 120 minutes average
     * @return Double
     */
    public double getAverageHatStats(Lineup lineup, int minutes) {
        if (minutes == 90) return getAverage90HatStats(lineup);
        return getAverage120HatStats(lineup);
    }

    /**
     * Get 90 minute hatstats average
     * @param lineup, Lineup
     * @return Double
     */
    public double getAverage90HatStats(Lineup lineup) {
        return hatStatsCache.getAverage90(lineup);
    }

    /**
     * Get 120 minutes hatstats average
     * @param lineup Lineup
     * @return Double
     */
    public double getAverage120HatStats(Lineup lineup) {
        return hatStatsCache.getAverage120(lineup);
    }

    /**
     * Cache of loddar stats values
     * Lineup->minute->loddar stats
     */
    private final StatsCache loddarStatsCache = new StatsCache() {
        @Override
        public double calc(Lineup lineup, int minute) {
            return calcLoddarStats(lineup, minute);
        }
    };

    /**
     * Calculate loddar stats value
     * @param lineup Lineup
     * @param minute match minute
     * @return Double
     */
    protected double calcLoddarStats(@NotNull Lineup lineup, int minute) {
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

        var dMD = getRating(lineup, RatingSector.MIDFIELD, minute);
        var dRD = getRating(lineup, RatingSector.DEFENCE_RIGHT, minute);
        var dCD = getRating(lineup, RatingSector.DEFENCE_CENTRAL, minute);
        var dLD = getRating(lineup, RatingSector.DEFENCE_LEFT, minute);
        var dLA = getRating(lineup, RatingSector.ATTACK_LEFT, minute);
        var dCA = getRating(lineup, RatingSector.ATTACK_CENTRAL, minute);
        var dRA = getRating(lineup, RatingSector.ATTACK_RIGHT, minute);

        // Calculate attack rating
        final double attackStrength = (ATTACK_WEIGHT + counterCorrection) * ((correctedCentralWeight * hq(dCA))
                + (correctedWingerWeight * (hq(dLA) + hq(dRA))));

        // Calculate defense rating
        final double defenseStrength = DEFENSE_WEIGHT * ((CENTRAL_WEIGHT * hq(dCD))
                + (WINGER_WEIGHT * (hq(dLD) + hq(dRD))));

        // Calculate midfield rating
        final double midfieldFactor = MIDFIELD_SHIFT + hq(dMD);

        // Calculate and return the LoddarStats rating
        return 80 * midfieldFactor * (defenseStrength + attackStrength);
    }

    private double hq(double value) {
        // Convert reduced float rating (1.00....20.99) to original integer HT rating (1...80) one +0.5 is because of correct rounding to integer
        int x = (int) (((value - 1.0f) * 4.0f) + 1.0f);
        return (2.0 * x) / (x + 80.0);
    }

    /**
     * Get loddar stats
     * @param lineup Lineup
     * @param minute match minute
     * @return Double
     */
    public double getLoddarStats(Lineup lineup, int minute) {
        return loddarStatsCache.get(lineup, minute);
    }

    /**
     * Get average loddar stats
     * @param lineup Lineup
     * @param minutes 90 or 120 minutes
     * @return Double
     */
    public double getAverageLoddarStats(Lineup lineup, int minutes) {
        if (minutes == 90) return getAverage90LoddarStats(lineup);
        return getAverage120LoddarStats(lineup);
    }

    /**
     * Get 90 minutes average loddar stats
     * @param lineup Lineup
     * @return Double
     */
    public double getAverage90LoddarStats(Lineup lineup) {
        return loddarStatsCache.getAverage90(lineup);
    }

    /**
     * Get 120 minutes average loddar stats
     * @param lineup Lineup
     * @return Double
     */
    public double getAverage120LoddarStats(Lineup lineup) {
        return loddarStatsCache.getAverage120(lineup);
    }

    /**
     * Cache of tactic strength values
     * Lineup->minute->tactic strength
     */
    private final TacticRatingCache tacticRatingCache = new TacticRatingCache() {
        @Override
        public double calc(Lineup lineup, Integer minute) {
            return calcTacticsRating(lineup, minute);
        }
    };

    /**
     * Get tactic rating
     * @param lineup Lineup
     * @param minute match minute
     * @return Double
     */
    public double getTacticRating(Lineup lineup, int minute) {
        return tacticRatingCache.get(lineup, minute);
    }

    /**
     * Calculate tactic rating
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
    protected double calcTacticsRating(@NotNull Lineup lineup, int minute) {
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

    /**
     * Calculate creative tactic rating
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
    protected double calcCreative(@NotNull Lineup lineup, int minute) {
        var sum = 0.;
        for (var p : lineup.getFieldPlayers(minute)) {
            if (p.getRoleId() == KEEPER) continue;
            var player = p.getPlayer();
            if (player != null) {
                var passing = calcStrength(player, PASSING);
                var experience = calcSkillRating(player.getSkill(EXPERIENCE));
                var contrib = 4 * passing + experience;
                if (player.getSpecialty() == PlayerSpeciality.UNPREDICTABLE) {
                    contrib *= 2;
                }
                sum += contrib;
            }
        }
        return sum / 50.; // TODO: guessed, investigate the formula rating(sum)
    }

    /**
     * Calculate pressing tactic rating
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
    protected double calcPressing(@NotNull Lineup lineup, int minute) {
        double ret = 0;
        for (var p : lineup.getFieldPlayers(minute)) {
            var defending = getPlayerTacticStrength(p, DEFENDING, lineup.getWeather(), lineup.getTacticType(), minute);
            if (defending > 0) {
                var player = p.getPlayer();
                if (player.getSpecialty() == PlayerSpeciality.POWERFUL) {
                    defending *= 2;
                }
            }
            ret += defending;
        }
        return 0.085 * ret + 0.075;
    }

    /**
     * Calculate passing tactic rating (towards wind, towards middle)
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
    protected double calcPassing(@NotNull Lineup lineup, int minute) {
        var sumPassing = 0.;
        for (var p : lineup.getFieldPositions()) {
            var player = p.getPlayer();
            if (player != null) {
                sumPassing += calcSkillRating(player.getSkill(PASSING));
            }
        }
        return sumPassing / 5. - 2.;
    }

    /**
     * Calculate long shots tactic rating
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
    protected double calcLongshots(@NotNull Lineup lineup, int minute) {
        var sumScoring = 0.;
        var sumSetPieces = 0.;
        var n = 0;
        for (var p : lineup.getFieldPositions()) {
            var player = p.getPlayer();
            if (player != null) {
                n++;
                sumScoring += calcSkillRating(player.getSkill(SCORING));
                sumSetPieces += calcSkillRating(player.getSkill(SETPIECES));
            }
        }

        //Tactic Level = 1.66*SC + 0.55*SP - 7.6
        return 1.66 * sumScoring / n + 0.55 * sumSetPieces / n - 7.6;
    }

    /**
     * Calculate counter attack tactic rating
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
}