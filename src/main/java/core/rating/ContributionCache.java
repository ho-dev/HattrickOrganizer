package core.rating;

import core.model.player.Player;

import java.util.HashMap;
import java.util.Map;

class ContributionCache extends HashMap<RatingPredictionModel.RatingSector, Map<Integer, Map<Player, Map<Byte, Double>>>> {

    private final RatingPredictionModel ratingPredictionModel;

    public ContributionCache(RatingPredictionModel ratingPredictionModel) {
        this.ratingPredictionModel = ratingPredictionModel;
    }

    /**
     * Get cached contribution result. If not available, it is calculated.
     * To save memory space and computing power, only calculate and cache left hand side and middle positions
     * If right hand side contribution is requested, try to get the opposite (left) side and toggle rating sector's side.
     *
     * @param s         rating sector
     * @param roleId    position
     * @param player    player
     * @param behaviour behaviour
     * @return double rating contribution
     */
    public double get(RatingPredictionModel.RatingSector s, int roleId, Player player, byte behaviour) {
        if (ratingPredictionModel.isRightHandSidePosition(roleId)) {
            s = ratingPredictionModel.toggleRatingSectorSide(s);
            roleId = ratingPredictionModel.togglePositionSide(roleId);
        }
        Double b;
        Map<Byte, Double> p = null;
        Map<Player, Map<Byte, Double>> pos = null;
        var sector = get(s);
        if (sector != null) {
            pos = sector.get(roleId);
            if (pos != null) {
                p = pos.get(player);
                if (p != null) {
                    b = p.get(behaviour);
                    if (b != null) {
                        return b;
                    }
                }
            }
        }
        var ret = ratingPredictionModel.calcContribution(player, roleId, behaviour, s);
        if (p == null) {
            p = new HashMap<>();
        }
        p.put(behaviour, ret);
        if (pos == null) {
            pos = new HashMap<>();
        }
        pos.put(player, p);
        if (sector == null) {
            sector = new HashMap<>();
        }
        sector.put(roleId, pos);
        this.put(s, sector);
        return ret;
    }
}
