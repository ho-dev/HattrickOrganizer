package core.rating;

import core.constants.player.PlayerSkill;
import core.model.match.MatchLineupPosition;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import module.lineup.Lineup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static core.model.player.IMatchRoleID.*;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;
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

    public RatingPredictionModel(Lineup lineup, int minute){
        calc(lineup, minute);
    }

    private static class RatingCalculationParameter {
        public RatingContributionParameter contributionParameter;
        public Side side;
        public RatingCalculationParameter(RatingContributionParameter p, Side s) {
            contributionParameter = p;
            side = s;
        }
    }
    private static final Map<RatingSector, RatingCalculationParameter>  ratingSectorParameterMap = Map.ofEntries(
            entry(RatingSector.Defence_Left, new RatingCalculationParameter(RatingContributionParameter.SideDefence, Side.left)),
            entry(RatingSector.Defence_Central, new RatingCalculationParameter(RatingContributionParameter.CentralDefence, Side.middle)),
            entry(RatingSector.Defence_Right, new RatingCalculationParameter(RatingContributionParameter.SideDefence, Side.right)),
            entry(RatingSector.Midfield, new RatingCalculationParameter(RatingContributionParameter.Midfield, Side.middle)),
            entry(RatingSector.Attack_Left, new RatingCalculationParameter(RatingContributionParameter.SideAttack, Side.left)),
            entry(RatingSector.Attack_Central, new RatingCalculationParameter(RatingContributionParameter.CentralAttack, Side.middle)),
            entry(RatingSector.Attack_Right, new RatingCalculationParameter(RatingContributionParameter.SideAttack, Side.right))
    );

    protected Map<RatingSector, Double> ratings = new HashMap<>();

    public double getRating(RatingSector s){
        return ratings.getOrDefault(s, 0.);
    }

    protected void setRating(RatingSector s, double value){
        ratings.put(s, value);
    }
    private void calc(Lineup lineup, int minute) {
        for (var s : RatingSector.values()) {
            var overcrowdingPenalty = calcOvercrowdingPenalty(lineup, s);
            for (var p : lineup.getFieldPositions()) {
                if (p.getPlayerId() != 0) {
                    // (S+Eff(L)*K(F) * C
                    var contribution = calcContribution(s, p);
                    if (contribution > 0) {
                        contribution *= overcrowdingPenalty;
                        var experience = calcExperience(p.getPlayer(), s);
                        contribution += experience;
                        setRating(s, getRating(s) + contribution);
                    }
                }
            }
        }
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

    private double calcContribution(RatingSector s, MatchLineupPosition p) {
        AtomicReference<Double> ret = new AtomicReference<>(0.);
        var player = p.getPlayer();
        if (player != null) {
            var params = ratingSectorParameterMap.get(s);
            if (params != null) {
                var side = params.side;
                var contributions = params.contributionParameter;
                var factor = contributionFactor.get(contributions);
                factor.forEach((playerSkill, sectors) -> sectors.forEach((sector, sideRestrictions) -> {
                    if (p.getSector() == sector) {
                        sideRestrictions.forEach((sideRestriction, behaviours) -> {
                            if (!isRoleSideRestricted(p.getRoleId(), side, sideRestriction)) {
                                var r = calcStrength(player, playerSkill);
                                ret.updateAndGet(v -> new Double(v + r));
                            }
                        });
                    }
                }));
            }
        }
        return ret.get();
    }
    private double calcExperience(Player player, RatingSector ratingSector) {
        return 0;
    }

    private final Map<RatingSector, Map<Integer, Double>> overcrowdingFactors = Map.ofEntries(
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

    private double calcOvercrowdingPenalty(Lineup lineup, RatingSector ratingSector) {
        var overcrowding = overcrowdingFactors.get(ratingSector);
        if ( overcrowding != null){
            var countPlayersInSector = 0;
            for ( var p : lineup.getFieldPositions()){
                if ( isRoleInRatingSector(p.getRoleId(), ratingSector)){
                    countPlayersInSector++;
                }
                var ret = overcrowding.get(countPlayersInSector);
                if ( ret != null){
                    return ret;
                }
            }
        }
        return 1.;
    }

    private double calcStrength(Player player, Integer playerSkill) {
        var skillRating = calcSkillRating(player.getSkill(playerSkill));
        skillRating += calcLoyalty(player);
        skillRating *= calcForm(player);
        return skillRating;
    }

    private double calcForm(Player player) {
        var form = calcSkillRating(player.getSkill(PlayerSkill.FORM));
        return 0.378 * sqrt(form);
    }

    private double calcLoyalty(Player player) {
        if (player.isHomeGrown()) return 1.5;
        var loyaltyRating = calcSkillRating(player.getSkill(PlayerSkill.LOYALTY));
        return loyaltyRating / 19.;
    }

    private double calcSkillRating(double skill) {
        return max(0,skill-1);
    }

    private boolean isRoleSideRestricted(int roleID, Side side, SideRestriction sideRestriction) {
        switch (sideRestriction){
            case none -> {return true;}
            case middle_only -> {
                return roleID == MatchRoleID.middleCentralDefender || roleID == MatchRoleID.centralInnerMidfield || roleID == MatchRoleID.centralForward;
            }
            case thisSide_only -> {
                switch ( side){
                    case left -> {
                        return roleID == leftBack || roleID == MatchRoleID.leftCentralDefender || roleID == MatchRoleID.leftWinger || roleID == MatchRoleID.leftInnerMidfield || roleID == MatchRoleID.leftForward;
                    }
                    case right -> {
                        return roleID == rightBack || roleID == MatchRoleID.rightCentralDefender || roleID == MatchRoleID.rightWinger || roleID == MatchRoleID.rightInnerMidfield || roleID == MatchRoleID.rightForward;
                    }
                    case middle -> {
                        return roleID == MatchRoleID.middleCentralDefender || roleID == MatchRoleID.centralInnerMidfield || roleID == MatchRoleID.centralForward;
                    }
                }
            }
            case oppositeSide_only -> {
                switch ( side){
                    case right -> {
                        return roleID == leftBack || roleID == MatchRoleID.leftCentralDefender || roleID == MatchRoleID.leftWinger || roleID == MatchRoleID.leftInnerMidfield || roleID == MatchRoleID.leftForward;
                    }
                    case left -> {
                        return roleID == rightBack || roleID == MatchRoleID.rightCentralDefender || roleID == MatchRoleID.rightWinger || roleID == MatchRoleID.rightInnerMidfield || roleID == MatchRoleID.rightForward;
                    }
                }
            }

        }
        return false;
    }

    private static final Map<RatingContributionParameter,
            Map<Integer,
                    Map<MatchRoleID.Sector,
                            Map<SideRestriction,
                                    Map<Byte, Double>
                                    >
                            >
                    >
            > contributionFactor = Map.ofEntries(
            entry(RatingContributionParameter.SideDefence, Map.ofEntries(
                    entry(PlayerSkill.KEEPER, Map.ofEntries(
                                    entry(MatchRoleID.Sector.Goal, Map.ofEntries(
                                            entry(SideRestriction.none, Map.ofEntries(
                                                            entry(MatchRoleID.NORMAL, 0.599619047619048)
                                                    )
                                            ))
                                    )
                            )),
                    entry(PlayerSkill.DEFENDING, Map.ofEntries(
                                    entry(MatchRoleID.Sector.Goal, Map.ofEntries(
                                            entry(SideRestriction.none, Map.ofEntries(
                                                            entry(MatchRoleID.NORMAL, 0.069125)
                                                    )
                                            ))
                                    ),
                                    entry(MatchRoleID.Sector.CentralDefence, Map.ofEntries(
                                            entry(SideRestriction.thisSide_only, Map.ofEntries(
                                                    entry(MatchRoleID.NORMAL, 0.147778142772),
                                                    entry(MatchRoleID.OFFENSIVE, 0.11367549444),
                                                    entry(MatchRoleID.TOWARDS_WING, 0.230192876241))
                                            ),
                                            entry(SideRestriction.middle_only, Map.ofEntries(
                                                    entry(MatchRoleID.NORMAL, 0.73889071386),
                                                    entry(MatchRoleID.OFFENSIVE, 0.05683774722))
                                            )
                                    )),
                                    entry(MatchRoleID.Sector.Back, Map.ofEntries(
                                            entry(SideRestriction.thisSide_only, Map.ofEntries(
                                                    entry(MatchRoleID.NORMAL, 0.261453637212),
                                                    entry(MatchRoleID.OFFENSIVE, 0.210299664714),
                                                    entry(MatchRoleID.DEFENSIVE, 0.2841887361),
                                                    entry(MatchRoleID.TOWARDS_MIDDLE, 0.213141552075))
                                            )
                                    )),
                                    entry(MatchRoleID.Sector.InnerMidfield, Map.ofEntries(
                                            entry(SideRestriction.thisSide_only, Map.ofEntries(
                                                    entry(MatchRoleID.NORMAL, 0.053995859859),
                                                    entry(MatchRoleID.OFFENSIVE, 0.025576986249),
                                                    entry(MatchRoleID.DEFENSIVE, 0.076730958747),
                                                    entry(MatchRoleID.TOWARDS_WING, 0.068205296664))
                                            ),
                                            entry(SideRestriction.middle_only, Map.ofEntries(
                                                    entry(MatchRoleID.NORMAL, 0.025576986249),
                                                    entry(MatchRoleID.OFFENSIVE, 0.011367549444),
                                                    entry(MatchRoleID.DEFENSIVE, 0.039786423054))
                                            ))
                                    ),
                                    entry(MatchRoleID.Sector.Wing, Map.ofEntries(
                                            entry(SideRestriction.thisSide_only, Map.ofEntries(
                                                    entry(MatchRoleID.NORMAL, 0.099466057635),
                                                    entry(MatchRoleID.OFFENSIVE, 0.062521521942),
                                                    entry(MatchRoleID.DEFENSIVE, 0.173355129021),
                                                    entry(MatchRoleID.TOWARDS_MIDDLE, 0.082414733469))
                                            )
                                    ))
                            )
                    ))
            ));

    public double getRatingContributionFactor(RatingContributionParameter parameterType, int playerSkill, MatchRoleID.Sector sector, SideRestriction orientation, Byte behaviour) {
        try {
            return contributionFactor.get(parameterType).get(playerSkill).get(sector).get(orientation).get(behaviour);
        } catch (Exception ignored) {
        }
        return 0;
    }

}