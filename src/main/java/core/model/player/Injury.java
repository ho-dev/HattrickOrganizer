package core.model.player;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Health calculation based on Schum formula (<a href="https://www82.hattrick.org/Forum/Read.aspx?t=17404127&n=6&v=0&mr=0">...</a>)
 */
public class Injury {

    Injury(Player player) {
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
     * Calculate the age related factor of the healing increase.
     * Fitting of the parameters released by Schum.
     * @param age double: Age of the player [17..]
     * @return double
     */
    private double calcAgeFactor(double age) {
        double x2Factor = 0.000016;
        double x1Factor = -0.002;
        double x0Factor = -0.384;
        double x = age - 17;
        return x2Factor * x * x + x1Factor * x + x0Factor;
    }

    /**
     * Calculate the doctor related factor of the healing increase.
     * @param doctorLevel int [0..5]
     * @return double
     */
    private double calcMedicianFactor(int doctorLevel) {
        double x1Factor = 0.2124;
        double x0Factor = 1;
        double x = doctorLevel + 1;
        var coefficient = x1Factor * x + x0Factor;
        return 1 / coefficient;
    }

    private double calculateHealthIncrease(Player player, int doctorLevel, HODateTime dateTime){
        return calcAgeFactor(player.getAgeAtDate(dateTime).toDouble()) * calcMedicianFactor(doctorLevel);
    }

    private void calculateRecovery(Player player) {

        // Load healing history
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

        // TSIact is  equal to the player's TSI during the match in which they got injured,
        // Age should correspond to the current one.
        // From the age of 28, the TSI decreases on each birthday,
        // for field players by 1/8 of the actual TSI on each birthday,
        // until from the 34th birthday onward it remains constant at 1/8 of the true TSI.

        // Determination of the starting status of healing
        var playerHealthy = recovery.get(recovery.size()-1);
        if (playerHealthy.getInjuryWeeks() == -1){
            if (recovery.size() > 1) {
                // Determine player's most recent tsi when healthy.
                // This might be the value calculated above, if no update with possible randomly skill drops or
                // training effects happened during time from healthy download and injury download.
                // Alternatively the tsi range maybe calculated from skill and form values. whereby form sub is not
                // well known.
                var recoveryIndex = recovery.size() - 2;
                var playerInjured = recovery.get(recoveryIndex);

                // If there were training updates between the healthy player download and the injured download, the
                // healthy tsi might require some correction because of skill or form changes
                // If player is older than 27 years old, the tsi drops also on birthday of the player
                playerHealthy.setSubskill4PlayerSkill(PlayerSkill.FORM, 0);
                var tsiHealthyCalculated = playerHealthy.calculateTSI();
                var tsiInjuredCalculated = playerInjured.calculateTSI();
                double healthyTSI = playerHealthy.getTsi() + tsiInjuredCalculated - tsiHealthyCalculated;
                var health = playerInjured.getTsi() / healthyTSI;
                var pastUpdates = getDailyUpdatesBetween(playerInjured.getHrfDate(), player.getHrfDate());
                var doctorLevel = 0;
                for (var update : pastUpdates) {
                    if (recoveryIndex >= 0) {
                        var p = recovery.get(recoveryIndex);
                        while (update.isAfter(p.getHrfDate())) {
                            var clubData = DBManager.instance().getVerein(playerInjured.getHrfId());
                            doctorLevel = clubData.getAerzte();
                            recoveryIndex--;
                            p = recovery.get(recoveryIndex);
                        }
                    }
                    var healthIncrease = calculateHealthIncrease(player, doctorLevel, update);
                    health += healthIncrease;
                    if (this.whenSlightlyInjured == null && health > 0.9) {
                        this.whenSlightlyInjured = update;
                    }
                }

                var nextDailyUpdates = HOVerwaltung.instance().getModel().getXtraDaten().getDailyUpdates();
                while (this.whenHealthy == null) {

                    for (var update : nextDailyUpdates) {
                        var increase = calculateHealthIncrease(player, doctorLevel, update);
                        if (increase < 0) {
                            return; // No recovery possible
                        }
                        health += increase;
                        if (this.whenSlightlyInjured == null && health >= 0.9) {
                            this.whenSlightlyInjured = update;
                        }
                        if (this.whenHealthy == null && health >= 1) {
                            this.whenHealthy = update;
                            break;
                        }
                    }
                    if (this.whenHealthy == null) {
                        nextDailyUpdates = nextDailyUpdates.stream().map(v -> v.plus(7, ChronoUnit.DAYS)).toList();
                    }
                }
            }
            else {
                // TODO: Not enough recovery data available
            }
        }
        else {
            // TODO: No data of healthy player available
        }
    }

    private List<HODateTime> getDailyUpdatesBetween(HODateTime injuryDate, HODateTime hrfDate) {
        return HOVerwaltung.instance().getModel().getXtraDaten().getDailyUpdatesBetween(injuryDate, hrfDate);
    }

    /**
     * Injury level reported by hattrick.
     * There are 5 possible trauma levels, which are indicated on the player's page and expressed as a percentage of remaining health
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

    /**
     * Date when player gets healthy
     * Date is in the past if player is healthy
     * If null, no recovery is possible any more
     */
    HODateTime whenHealthy;
    HODateTime whenSlightlyInjured;
}
