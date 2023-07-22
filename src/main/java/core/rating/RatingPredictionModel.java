package core.rating;

import core.constants.player.PlayerSkill;
import core.model.match.IMatchDetails;
import core.model.match.MatchLineupPosition;
import core.model.match.MatchTacticType;
import core.model.match.Weather;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.model.player.Specialty;
import module.lineup.Lineup;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static core.constants.player.PlayerSkill.EXPERIENCE;
import static core.model.match.IMatchDetails.TAKTIK_PRESSING;
import static core.model.player.IMatchRoleID.*;
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

    enum RatingContributionParameter {
        SideDefence,
        CentralDefence,
        Midfield,
        SideAttack,
        CentralAttack
    }

    enum SideRestriction {
        none,           // all sides contribute to the ratings
        thisSide_only,
        middle_only,
        oppositeSide_only
    }

    enum Side {
        left,
        middle,
        right
    }

    private final Player trainer;
    private final int teamSpirit;
    private final int confidence;

    public RatingPredictionModel(Player trainer, int teamSpirit, int confidence) {
        if (ratingContributionParameterMap == null) {
            initRatingContributionParameterMap();
        }
        this.trainer = trainer;
        this.teamSpirit = teamSpirit;
        this.confidence = confidence;
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

    public double getPlayerRating(MatchLineupPosition p, Weather weather, int tacticType, RatingSector s, int minute, double overcrowdingPenalty) {
        var contribution = contributionCache.get(p, s);
        if (contribution > 0) {
            var player = p.getPlayer();
            contribution *= overcrowdingPenalty;
            contribution += experienceCache.get(player.getSkillValue(EXPERIENCE), s);
            contribution *= weatherCache.get(Specialty.getSpecialty(player.getPlayerSpecialty()), weather);
            contribution *= staminaCache.get((double) player.getStamina(), minute, p.getStartMinute(), tacticType);
        }
        return contribution;
    }

    public double getRating(Lineup lineup, RatingSector s, int minute) {
        var ret = 0.;
        var overcrowdingPenalty = getOvercrowdingPenalty(countPlayersInRatingSector(lineup, s), s);
        for (var p : lineup.getFieldPlayers(minute)) {
            if (p.getPlayerId() != 0) {
                var contribution = getPlayerRating(p, lineup.getWeather(), lineup.getTacticType(), s, minute, overcrowdingPenalty);
                ret += contribution;
            }
        }
        ret *= calcSector(lineup, s);
        return ret;
    }

    static abstract class Cache<T1, T2> extends HashMap<T1, HashMap<T2, Double>> {
        public double get(T1 t1, T2 t2) {
            var c = this.get(t1);
            if (c != null) {
                var ret = c.get(t2);
                if (ret != null) {
                    return ret;
                }
            }
            var r = calc(t1, t2);
            if (c == null) {
                c = new HashMap<>();
            }
            c.put(t2, r);
            this.put(t1, c);
            return r;
        }

        public abstract double calc(T1 t1, T2 t2);
    }

    static abstract class Cache4<T1, T2, T3, T4> extends HashMap<T1, HashMap<T2, HashMap<T3, HashMap<T4, Double>>>> {
        public double get(T1 t1, T2 t2, T3 t3, T4 t4) {
            Double d;
            HashMap<T4, Double> c = null;
            HashMap<T3, HashMap<T4, Double>> b = null;
            HashMap<T2, HashMap<T3, HashMap<T4, Double>>> a = this.get(t1);
            if (a != null) {
                b = a.get(t2);
                if (b != null) {
                    c = b.get(t3);
                    if (c != null) {
                        d = c.get(t4);
                        if (d != null) {
                            return d;
                        }
                    }
                }
            }
            var r = calc(t1, t2, t3, t4);
            if (c == null) {
                c = new HashMap<>();
            }
            c.put(t4, r);
            if (b == null) {
                b = new HashMap<>();
            }
            b.put(t3, c);
            if (a == null) {
                a = new HashMap<>();
            }
            a.put(t2, b);
            this.put(t1, a);
            return r;
        }

        public abstract double calc(T1 t1, T2 t2, T3 t3, T4 t4);
    }

    protected Cache4<Double, Integer, Integer, Integer> staminaCache = new Cache4<>() {
        @Override
        public double calc(Double stamina, Integer minute, Integer startMinute, Integer tacticType) {
            return calcStamina(stamina, minute, startMinute, tacticType);
        }
    };

    protected Cache<MatchLineupPosition, RatingSector> contributionCache = new Cache<>() {
        @Override
        public double calc(MatchLineupPosition matchLineupPosition, RatingSector ratingSector) {
            return calcContribution(ratingSector, matchLineupPosition);
        }
    };

    protected Cache<Double, RatingSector> experienceCache = new Cache<>() {
        @Override
        public double calc(Double skillValue, RatingSector ratingSector) {
            return calcExperience(ratingSector, skillValue);
        }
    };

    protected Cache<Specialty, Weather> weatherCache = new Cache<>() {
        @Override
        public double calc(Specialty specialty, Weather weather) {
            return calcWeather(specialty, weather);
        }
    };

    protected double calcTrainer(RatingSector s) {
        switch (s) {
            case Defence_Left, Defence_Right, Defence_Central -> {
                switch (trainer.getTrainerTyp()) {
                    case Balanced -> {
                        return 1.02;
                    }
                    case Defensive -> {
                        return 1.15;
                    }
                    case Offensive -> {
                        return 0.90;
                    }
                    default -> {
                    }
                }
            }
            case Attack_Central, Attack_Left, Attack_Right -> {
                switch (trainer.getTrainerTyp()) {
                    case Balanced -> {
                        return 1.02;
                    }
                    case Defensive -> {
                        return 0.9;
                    }
                    case Offensive -> {
                        return 1.1;
                    }
                    default -> {
                    }
                }
            }
        }
        return 1.;
    }

    protected double calcSector(Lineup lineup, RatingSector s) {
        var r = 1.;
        switch (s) {
            case Midfield -> {
                switch (lineup.getAttitude()) {
                    case IMatchDetails.EINSTELLUNG_PIC -> r *= 0.84;
                    case IMatchDetails.EINSTELLUNG_MOTS -> r *= 83. / 75.;
                    default -> {
                    }
                }
                switch (lineup.getLocation()) {
                    case IMatchDetails.LOCATION_AWAYDERBY -> r *= 1.1;
                    case IMatchDetails.LOCATION_HOME -> r *= 1.2;
                    default -> {
                    }
                }
                switch (MatchTacticType.fromInt(lineup.getTacticType())) {
                    case CounterAttacks -> r *= 0.93;
                    case LongShots -> r *= 0.96;
                }
                var spirit = calcTeamSpirit(teamSpirit);
                r *= spirit;
            }
            case Defence_Left, Defence_Right -> {
                r *= calcTrainer(s);
                switch (MatchTacticType.fromInt(lineup.getTacticType())) {
                    case AttackInTheMiddle -> r *= 0.85;
                    case PlayCreatively -> r *= 0.93;
                    default -> {
                    }
                }
            }
            case Defence_Central -> {
                r *= calcTrainer(s);
                switch (MatchTacticType.fromInt(lineup.getTacticType())) {
                    case AttackInWings -> r *= 0.85;
                    case PlayCreatively -> r *= 0.93;
                    default -> {
                    }
                }
            }
            case Attack_Central, Attack_Left, Attack_Right -> {
                r *= calcTrainer(s);
                if (Objects.requireNonNull(MatchTacticType.fromInt(lineup.getTacticType())) == MatchTacticType.LongShots) {
                    r *= 0.96;
                }
                r *= calcConfidence(confidence);
            }
        }
        return r;
    }

    protected double calcConfidence(int confidence) {
        return 0.8 + 0.05 * confidence;
    }

    protected double calcTeamSpirit(int teamSpirit) {
        return 0.1 + 0.425 * sqrt(teamSpirit + .5);
    }

    protected double calcWeather(Specialty specialty, Weather weather) {

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

    private boolean isRoleInRatingSector(int roleId, RatingSector s) {
        return switch (roleId) {
            case leftBack -> s == RatingSector.Defence_Left;
            case rightBack -> s == RatingSector.Defence_Right;
            case leftCentralDefender, rightCentralDefender, middleCentralDefender -> s == RatingSector.Defence_Central;
            case leftWinger -> s == RatingSector.Attack_Left;
            case rightWinger -> s == RatingSector.Attack_Right;
            case leftInnerMidfield, rightInnerMidfield, centralInnerMidfield -> s == RatingSector.Midfield;
            case leftForward, rightForward, centralForward -> s == RatingSector.Attack_Central;
            default -> false;
        };
    }

    protected double calcContribution(RatingSector s, MatchLineupPosition p) {
        AtomicReference<Double> ret = new AtomicReference<>(0.);
        var player = p.getPlayer();
        if (player != null) {
            var params = ratingSectorParameterMap.get(s);
            if (params != null) {
                var side = params.side;
                var contributions = params.contributionParameter;
                var factor = ratingContributionParameterMap.get(contributions);
                factor.forEach((playerSkill, sectors) ->
                        sectors.forEach((sector, sideRestrictions) -> {
                            if (p.getSector() == sector) {
                                sideRestrictions.forEach((sideRestriction, behaviours) -> {
                                    if (!isRoleSideRestricted(p.getRoleId(), side, sideRestriction)) {
                                        var specialties = behaviours.get(p.getBehaviour());
                                        var f = specialties.get(Specialty.getSpecialty(player.getPlayerSpecialty()));
                                        var r = f * calcStrength(player, playerSkill);
                                        ret.updateAndGet(v -> v + r);
                                    }
                                });
                            }
                        }));
            }
        }
        return ret.get();
    }

    protected double calcExperience(RatingSector ratingSector, double skillValue) {
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
                return k * 0.480;
            }
            default -> throw new IllegalStateException("Unexpected value: " + ratingSector);
        }
    }

    protected final Map<RatingSector, Map<Integer, Double>> overcrowdingFactors = Map.ofEntries(
            entry(RatingSector.Defence_Central, Map.ofEntries(
                            entry(2, 0.964),
                            entry(3, 0.9)
                    )
            ),
            entry(RatingSector.Midfield, Map.ofEntries(
                            entry(2, 0.935),
                            entry(3, 0.825)
                    )
            ),
            entry(RatingSector.Attack_Central, Map.ofEntries(
                            entry(2, 0.945),
                            entry(3, 0.865)
                    )
            )
    );

    private int countPlayersInRatingSector(Lineup lineup, RatingSector ratingSector) {
        var countPlayersInSector = 0;
        for (var p : lineup.getFieldPositions()) {
            if (isRoleInRatingSector(p.getRoleId(), ratingSector)) {
                countPlayersInSector++;
            }
        }
        return countPlayersInSector;
    }

    protected double getOvercrowdingPenalty(int countPlayersInSector, RatingSector ratingSector) {
        var overcrowding = overcrowdingFactors.get(ratingSector);
        if (overcrowding != null) {
            var ret = overcrowding.get(countPlayersInSector);
            if (ret != null) {
                return ret;
            }
        }
        return 1.;
    }

    protected double calcStrength(Player player, Integer playerSkill) {
        var skillRating = calcSkillRating(player.getSkill(playerSkill));
        skillRating += calcLoyalty(player);
        skillRating *= calcForm(player);
        return skillRating;
    }

    protected double calcForm(Player player) {
        var form = calcSkillRating(player.getSkill(PlayerSkill.FORM));
        return 0.378 * sqrt(form);
    }

    protected double calcLoyalty(Player player) {
        if (player.isHomeGrown()) return 1.5;
        var loyaltyRating = calcSkillRating(player.getSkill(PlayerSkill.LOYALTY));
        return loyaltyRating / 19.;
    }

    protected double calcSkillRating(double skill) {
        return max(0, skill - 1);
    }

    protected boolean isRoleSideRestricted(int roleID, Side side, SideRestriction sideRestriction) {
        switch (sideRestriction) {
            case none -> {
                return true;
            }
            case middle_only -> {
                return roleID == middleCentralDefender || roleID == centralInnerMidfield || roleID == centralForward;
            }
            case thisSide_only -> {
                switch (side) {
                    case left -> {
                        return roleID == leftBack || roleID == leftCentralDefender || roleID == leftWinger || roleID == leftInnerMidfield || roleID == leftForward;
                    }
                    case right -> {
                        return roleID == rightBack || roleID == rightCentralDefender || roleID == rightWinger || roleID == rightInnerMidfield || roleID == rightForward;
                    }
                    case middle -> {
                        return roleID == middleCentralDefender || roleID == centralInnerMidfield || roleID == centralForward;
                    }
                }
            }
            case oppositeSide_only -> {
                switch (side) {
                    case right -> {
                        return roleID == leftBack || roleID == leftCentralDefender || roleID == leftWinger || roleID == leftInnerMidfield || roleID == leftForward;
                    }
                    case left -> {
                        return roleID == rightBack || roleID == rightCentralDefender || roleID == rightWinger || roleID == rightInnerMidfield || roleID == rightForward;
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
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.KEEPER, MatchRoleID.Sector.Goal, SideRestriction.none, NORMAL, 0.599619047619048);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Goal, SideRestriction.none, NORMAL, 0.069125);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.thisSide_only, NORMAL, 0.147778142772);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.thisSide_only, OFFENSIVE, 0.11367549444);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.thisSide_only, TOWARDS_WING, 0.230192876241);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.middle_only, NORMAL, 0.73889071386);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.middle_only, OFFENSIVE, 0.05683774722);

        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, NORMAL, 0.261453637212);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, OFFENSIVE, 0.210299664714);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, DEFENSIVE, 0.2841887361);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, TOWARDS_MIDDLE, 0.213141552075);

        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, NORMAL, 0.053995859859);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, OFFENSIVE, 0.025576986249);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, DEFENSIVE, 0.076730958747);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, TOWARDS_WING, 0.068205296664);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, NORMAL, 0.025576986249);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, OFFENSIVE, 0.011367549444);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, DEFENSIVE, 0.039786423054);

        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, NORMAL, 0.099466057635);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, OFFENSIVE, 0.062521521942);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, DEFENSIVE, 0.173355129021);
        initAllSpecialties(RatingContributionParameter.SideDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, TOWARDS_MIDDLE, 0.082414733469);

        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.KEEPER, MatchRoleID.Sector.Goal, SideRestriction.none, NORMAL, .135452380952381);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Goal, SideRestriction.none, NORMAL, .066225);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, NORMAL, .186);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, OFFENSIVE, .13578);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, TOWARDS_WING, .12462);

        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.none, NORMAL, .07068);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.none, OFFENSIVE, .0651);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.none, DEFENSIVE, .07998);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Back, SideRestriction.none, TOWARDS_MIDDLE, .1302);

        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, NORMAL, .0744);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, OFFENSIVE, .02976);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, DEFENSIVE, .10788);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, TOWARDS_WING, .06138);

        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.none, NORMAL, .0372);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.none, OFFENSIVE, .02418);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.none, DEFENSIVE, .0465);
        initAllSpecialties(RatingContributionParameter.CentralDefence, PlayerSkill.DEFENDING, MatchRoleID.Sector.Wing, SideRestriction.none, TOWARDS_MIDDLE, .0465);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, NORMAL, .25);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, OFFENSIVE, .40);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.CentralDefence, SideRestriction.none, TOWARDS_WING, .15);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.none, NORMAL, .15);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.none, OFFENSIVE, .20);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.none, DEFENSIVE, .10);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Back, SideRestriction.none, TOWARDS_MIDDLE, .20);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, NORMAL, 1.);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, OFFENSIVE, .95);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, DEFENSIVE, .95);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, TOWARDS_WING, .90);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.none, NORMAL, .45);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.none, OFFENSIVE, .30);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.none, DEFENSIVE, .30);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Wing, SideRestriction.none, TOWARDS_MIDDLE, .55);

        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .25);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.none, OFFENSIVE, .30);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .35);
        initAllSpecialties(RatingContributionParameter.Midfield, PlayerSkill.PLAYMAKING, MatchRoleID.Sector.Forward, SideRestriction.none, TOWARDS_WING, .15);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, NORMAL, .0586384002765);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, OFFENSIVE, .0870691398045);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, DEFENSIVE, .031984581969);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, TOWARDS_WING, .0408691880715);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.none, NORMAL, .0195461334255);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.none, OFFENSIVE, .0230999758665);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.none, DEFENSIVE, .0088846061025);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.none, TOWARDS_MIDDLE, .028430739528);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .0586384002765);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, OFFENSIVE, .30);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .0941768246865);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, TOWARDS_WING, .0408691880715);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, NORMAL, .039092266851);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, OFFENSIVE, .0550845578355);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield, SideRestriction.none, DEFENSIVE, .0230999758665);
//        initAllSpecialties(RatingContributionParameter.CentralAttack,PlayerSkill.SCORING, MatchRoleID.Sector.InnerMidfield , SideRestriction.none, TOWARDS_WING, 0.);

        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .17769212205);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .099507588348);
        initAllSpecialties(RatingContributionParameter.CentralAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, TOWARDS_WING, .117276800553);


        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, NORMAL, 0.02886);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, OFFENSIVE, 0.03996);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.middle_only, DEFENSIVE, 0.01554);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, 0.02886);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, 0.06882);
        initSpecialty(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, Specialty.Technical, 0.09102);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, NORMAL, 0.05772);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, OFFENSIVE, 0.07992);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, DEFENSIVE, 0.03108);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, TOWARDS_WING, 0.06882);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, NORMAL, 0.05772);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, OFFENSIVE, 0.06438);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, DEFENSIVE, 0.04662);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, TOWARDS_MIDDLE, 0.0333);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.thisSide_only, TOWARDS_WING, 0.04662);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.PASSING, MatchRoleID.Sector.Forward, SideRestriction.oppositeSide_only, TOWARDS_WING, 0.01332);


        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.CentralDefence, SideRestriction.thisSide_only, TOWARDS_WING, 0.05772);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, NORMAL, .13098);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, OFFENSIVE, .15318);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, DEFENSIVE, .0999);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Back, SideRestriction.thisSide_only, TOWARDS_MIDDLE, .0777);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.InnerMidfield, SideRestriction.thisSide_only, TOWARDS_WING, .13098);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, NORMAL, .19092);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, OFFENSIVE, .222);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, DEFENSIVE, .15318);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Wing, SideRestriction.thisSide_only, TOWARDS_MIDDLE, .16428);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.thisSide_only, NORMAL, .05328);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.thisSide_only, DEFENSIVE, .02886);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.thisSide_only, TOWARDS_WING, .14208);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.oppositeSide_only, NORMAL, .04662);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.oppositeSide_only, DEFENSIVE, .02886);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.WINGER, MatchRoleID.Sector.Forward, SideRestriction.oppositeSide_only, TOWARDS_WING, .04662);

        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, NORMAL, .05994);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.none, DEFENSIVE, .02886);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.oppositeSide_only, TOWARDS_WING, .04218);
        initAllSpecialties(RatingContributionParameter.SideAttack, PlayerSkill.SCORING, MatchRoleID.Sector.Forward, SideRestriction.thisSide_only, TOWARDS_WING, .11322);
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