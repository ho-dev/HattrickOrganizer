package core.rating;

import core.constants.player.PlayerSkill;
import core.model.player.MatchRoleID;

import java.util.Map;

import static java.util.Map.entry;

public class RatingPredictionModel {

    enum RatingSector {
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

    enum CentralPositionOrientation {
        thisSide,
        middle,
        otherSide
    }


    private static final Map<RatingContributionParameter, Map<Integer, Map<MatchRoleID.Sector, Map<CentralPositionOrientation, Map<Byte, Double>>>>> contributionFactor = Map.ofEntries(
            entry(RatingContributionParameter.SideDefence, Map.ofEntries(
                    entry(PlayerSkill.DEFENDING, Map.ofEntries(
                            entry(MatchRoleID.Sector.Goal, Map.ofEntries(
                                    entry(CentralPositionOrientation.middle, Map.ofEntries(
                                                    entry(MatchRoleID.NORMAL, 0.069125)
                                            )
                                    ))
                            ),
                            entry(MatchRoleID.Sector.CentralDefence, Map.ofEntries(
                                    entry(CentralPositionOrientation.thisSide, Map.ofEntries(
                                            entry(MatchRoleID.NORMAL, 0.147778142772),
                                            entry(MatchRoleID.OFFENSIVE, 0.11367549444),
                                            entry(MatchRoleID.TOWARDS_WING, 0.230192876241))
                                    ),
                                    entry(CentralPositionOrientation.middle, Map.ofEntries(
                                            entry(MatchRoleID.NORMAL, 0.73889071386),
                                            entry(MatchRoleID.OFFENSIVE, 0.05683774722))
                                    )
                            )),
                            entry(MatchRoleID.Sector.Back, Map.ofEntries(
                                    entry(CentralPositionOrientation.thisSide, Map.ofEntries(
                                            entry(MatchRoleID.NORMAL, 0.261453637212),
                                            entry(MatchRoleID.OFFENSIVE, 0.210299664714),
                                            entry(MatchRoleID.DEFENSIVE, 0.2841887361),
                                            entry(MatchRoleID.TOWARDS_MIDDLE, 0.213141552075))
                                    )
                            )),
                            entry(MatchRoleID.Sector.InnerMidfield, Map.ofEntries(
                                    entry(CentralPositionOrientation.thisSide, Map.ofEntries(
                                            entry(MatchRoleID.NORMAL, 0.053995859859),
                                            entry(MatchRoleID.OFFENSIVE, 0.025576986249),
                                            entry(MatchRoleID.DEFENSIVE, 0.076730958747),
                                            entry(MatchRoleID.TOWARDS_WING, 0.068205296664))
                                    ),
                                    entry(CentralPositionOrientation.middle, Map.ofEntries(
                                            entry(MatchRoleID.NORMAL, 0.025576986249),
                                            entry(MatchRoleID.OFFENSIVE, 0.011367549444),
                                            entry(MatchRoleID.DEFENSIVE, 0.039786423054))
                                    ))
                            ),
                            entry(MatchRoleID.Sector.Wing, Map.ofEntries(
                                    entry(CentralPositionOrientation.thisSide, Map.ofEntries(
                                            entry(MatchRoleID.NORMAL, 0.099466057635),
                                            entry(MatchRoleID.OFFENSIVE, 0.062521521942),
                                            entry(MatchRoleID.DEFENSIVE, 0.173355129021),
                                            entry(MatchRoleID.TOWARDS_MIDDLE, 0.082414733469))
                                    )
                            ))
                    )
            ))
    ));

    public double getRatingContributionFactor(RatingContributionParameter parameterType, int playerSkill, MatchRoleID.Sector sector, CentralPositionOrientation orientation, Byte behaviour) {
        try {
            return contributionFactor.get(parameterType).get(playerSkill).get(sector).get(orientation).get(behaviour);
        } catch (Exception ignored) {
        }
        return 0;
    }

}