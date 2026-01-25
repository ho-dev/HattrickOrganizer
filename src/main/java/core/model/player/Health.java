package core.model.player;

import core.db.DBManager;
import core.util.HODateTime;

import java.util.ArrayList;

/**
 * Health calculation based on Schum formula (<a href="https://www82.hattrick.org/Forum/Read.aspx?t=17404127&n=6&v=0&mr=0">...</a>)
 */
public class Health {

    Health(Player player) {
        this.injuryLevel = player.getInjuryWeeks();
        var date = player.getHrfDate();
        if (injuryLevel == -1){
            whenHealthy = date;
            whenSlightlyInjured = date;
        }
        else {
            calculateRecovery(player);
        }
    }

    /**
     * Calculate the age factor of the healing increment
     * Fitting of the parameters released by Schum.
     * @param age double: Age of the player [17..]
     * @return double
     */
    private double calcAgeFactor(double age) {
        double x2Factor = 0.00001666;
        double x1Factor = -0.002;
        double x0Factor = -0.384;
        double x = age - 17;
        return x2Factor * x * x + x1Factor * x + x0Factor;
    }

    private double calcMedicianFactor(int medicianLevel) {
        double x1Factor = 0.2124;
        double x0Factor = 1;
        var coefficient = x1Factor * medicianLevel + x0Factor;
        return 1 / coefficient;
    }

    private void calculateRecovery(Player player) {
        var date = player.getHrfDate();
        ArrayList<Player> recovery = new ArrayList<>();
        recovery.add(player);
        var wasInjured = this.injuryLevel > -1;
        while (wasInjured){
            var playerBefore = DBManager.instance().getLatestPlayerDownloadBefore(player.getPlayerId(), date.toDbTimestamp());
            if ( playerBefore != null){
                recovery.add(playerBefore);
                wasInjured = playerBefore.getInjuryWeeks() > -1;
                date = playerBefore.getHrfDate();
            }
            else {
                break; // No data of healthy player available
            }
        }

        var playerHealthy = recovery.get(recovery.size()-1);
        if (playerHealthy.getInjuryWeeks() == -1){
            if (recovery.size() > 1){
                var playerInjured = recovery.get(recovery.size()-2);
                var healthCoefficient = (double) playerInjured.getTsi() / playerHealthy.getTsi();
            }
            else {
                // TODO: Not enough recovery data available
            }
        }
        else {
            // TODO: No data of healthy player available
        }
    }

    /**
     * Injury level reported by hattrick.
     * There are 5 possible trauma levels, which are indicated on the player's page and expressed as a percentage of remaining health.*
     * Health Level, Health range
     * Plaster          90-99.99%
     * 1                80-90%
     * 2                70-80%
     * 3                60-70%
     * 4                50-60%
     * It is impossible to get an injury of more than +4 in a match, but for players over 40, injuries do not decrease;
     * they increase and eventually reach +9.
     */
    int injuryLevel;
    HODateTime whenHealthy;
    HODateTime whenSlightlyInjured;
}
