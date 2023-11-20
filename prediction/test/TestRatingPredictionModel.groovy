import module.lineup.*
import core.rating.RatingPredictionModel
import core.rating.RatingPredictionModel.*
import core.model.Team

@groovy.transform.InheritConstructors
class TestRatingPredictionModel extends RatingPredictionModel {

    TestRatingPredictionModel(Team team) {
        super(team)
    }

    @Override
    double calcSectorRating(Lineup lineup, RatingSector s, int minute) {
        addCopyright("© test")
        return 1
    }

    /**
     * Get the rating contribution of a single player in lineup.
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
//    @Override
//    double getPositionContribution(Player player, int roleId, byte behaviour, RatingSector sector, int minute, int startMinute, double overcrowdingPenalty) {
//        return 1;
//    }

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
//    static void initRatingContributionParameter(RatingContributionParameterSet ratingContributionParameter, int skill, MatchRoleID.Sector sector, SideRestriction sideRestriction, byte behaviour, Specialty specialty, double v) {
//    }

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
//    static Double getRatingContributionParameter(RatingContributionParameterSet ratingContributionParameter, int skill, MatchRoleID.Sector sector, SideRestriction sideRestriction, byte behaviour, Specialty specialty) {
//    }


    /**
     * Calculate the effect of the lineup settings to the sector ratings
     *
     * @param lineup Lineup
     * @param s Rating sector
     * @return Sector factor
     */
//    @Override
//    double calcSector(Lineup lineup, RatingSector s) {
//        return 1;
//    }

    /**
     * Transform skill scale to rating sector scale
     *
     * @param s Rating sector
     * @param ret Skill scale rating sum
     * @return Sector rating
     */
//    @Override
//    double calcRatingSectorScale(RatingSector s, double ret) {
//        return 1;
//    }

    /**
     * Get rating sector scaling factor
     *
     * @param s Rating sector
     * @return scaling factor
     */
//    @Override
//    double getRatingSectorScaleFactor(RatingSector s) {
//        return 1;
//    }

    /**
     * Calculate the confidence factor
     *
     * @param confidence Confidence value without any sublevel (.5 is used)
     * @return Confidence factor
     */
//    @Override
//    double calcConfidence(double confidence) {
//        return 1;
//    }

    /**
     * Calculate the team spirit factor
     *
     * @param teamSpirit Team spirit including any sublevel
     * @return Team spirit factor
     */
//    @Override
//    double calcTeamSpirit(double teamSpirit) {
//        return 1;
//    }

    /**
     * Get the overcrowding penalty factor
     * If no penalty factor is found in the map, the factor 1 is returned.
     *
     * @param countPlayersInSector Player count of the lineup sector
     * @param sector Lineup sector
     * @return double
     */
//    @Override
//    double getOvercrowdingPenalty(int countPlayersInSector, MatchRoleID.Sector sector) {
//        return 1;
//    }

    /**
     * Get player count of a lineup sector
     *
     * @param positions Lineup positions
     * @param sector Lineup sector
     * @return Number of players in given lineup sector
     */
//    int countPlayersInSector(List<MatchLineupPosition> positions, MatchRoleID.Sector sector) {
//    }

    /**
     * Calculate the experience rating contribution to a rating sector (Eff(Exp))
     *
     * @param ratingSector Rating sector
     * @param skillValue Experience skill value
     * @return Experience rating contribution to the rating sector
     */
//    @Override
//    protected double calcExperience(RatingSector ratingSector, double skillValue) {
//        return 1;
//    }

    /**
     * Calculate the stamina factor
     *
     * @param stamina Stamina skill value
     * @param minute Match minute
     * @param startMinute Player's match start minute
     * @param tacticType Match tactic
     * @return double
     */
//    @Override
//     double calcStamina(double stamina, int minute, int startMinute, int tacticType) {
//        return 1;
//    }

    /**
     * Calculate the factor of the weather impact
     * @param specialty Player's specialty
     * @param weather Weather
     * @return Double
     */
//    @Override
//    protected double calcWeather(Specialty specialty, Weather weather) {
//        return 1;
//    }

    /**
     * Calculate the factor of the coach modifier
     *
     * @param s Rating sector
     * @param coachModifier Integer value representing the style of play the team will use in the match. The value ranges from -10 (100% defensive) to 10 (100% offensive).
     * @return Double
     */
//    @Override
//    double calcTrainer(RatingSector s, int coachModifier) {
//        return 1;
//    }

    /**
     * Calculate player's skill strength, regarding loyalty and form
     * @param player , Player
     * @param playerSkill , Skill
     * @return Double
     */
//    @Override
//    double calcStrength(@NotNull Player player, Integer playerSkill) {
//        return 1;
//    }

    /**
     * Calculate player's loyalty impact on rating
     * @param player , Player
     * @return Double
     */
//    @Override
//    double calcLoyalty(@NotNull Player player) {
//        return 1;
//    }

    /**
     * Calculate skill rating
     * Excellent without sub skill gives 7.0
     * @param skill , Double displayed skill value
     * @return Double [0..]
     */
//    @Override
//    double calcSkillRating(double skill) {
//        return 1;
//    }


    /**
     * Calculate player's form impact on rating
     * @param player , Player
     * @return , Double
     */
//    @Override
//    double calcForm(@NotNull Player player) {
//        return 1;
//    }

    /**
     * Calculate the sum of the player's rating contributions to all rating sectors
     *
     * @param p Player
     * @return double
     */
//    @Override
//    double calcPlayerRating(Player p, int roleId, byte behaviour, int minute) {
//        return 1;
//    }

    /**
     * Calculate the match average stamina factor
     * Formula fitting the values published by Schum.
     *
     * @param stamina Stamina skill value
     * @return
     */
//    @Override
//    double calcMatchAverageStaminaFactor(double stamina) {
//        return 1;
//    }

    /**
     * Calculate player's tactic strength of given skill
     * @param player Player
     * @param skill Skill
     * @return Double
     */
//    @Override
//    double calcPlayerTacticStrength(Player player, Integer skill) {
//        return 1;
//    }

    /**
     * Calculate player's penalty strength
     * @param player , Player
     * @return Double
     */
//    @Override
//    Double calcPlayerPenaltyStrength(Player player) {
//        return 1;
//    }

    /**
     * Calculate tactic rating
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
//    @Override
//    double calcTacticsRating(@NotNull Lineup lineup, int minute) {
//        return 1;
//    }

    /**
     * Calculate creative tactic rating
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
//    @Override
//    double calcCreative(@NotNull Lineup lineup, int minute) {
//        return 1;
//    }

    /**
     * Calculate pressing tactic rating
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
//    @Override
//    double calcPressing(@NotNull Lineup lineup, int minute) {
//        return 1;
//    }

    /**
     * Calculate passing tactic rating (towards wind, towards middle)
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
//    @Override
//    double calcPassing(@NotNull Lineup lineup, int minute) {
//        return 1;
//    }

    /**
     * Calculate long shots tactic rating
     * @param lineup Lineup
     * @param minute Match minute
     * @return Double
     */
//    @Override
//    double calcLongshots(@NotNull Lineup lineup, int minute) {
//        return 1;
//    }

    /**
     * Calculate counter attack tactic rating
     * @param lineup Lineup to calculate counter-attack rating for
     * @param minute int match minute
     * @return double The rating value
     */
//    @Override
//    double calcCounterAttack(@NotNull Lineup lineup, int minute) {
//        return 1;
//    }

    /**
     * Calculate lineup hatstats
     * @param lineup Lineup
     * @param minute match minute
     * @return Double
     */
//    @Override
//    double calcHatStats(Lineup lineup, int minute) {
//        return 1;
//    }

    /**
     * Calculate loddar stats value
     * @param lineup Lineup
     * @param minute match minute
     * @return Double
     */
//    @Override
//    double calcLoddarStats(@NotNull Lineup lineup, int minute) {
//        return 1;
//    }
}