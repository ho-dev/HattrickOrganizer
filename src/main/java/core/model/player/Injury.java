package core.model.player;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;

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

        var dailyUpdates = new ArrayList<>(HOVerwaltung.instance().getModel().getXtraDaten().getDailyUpdates());

        // TODO: Calculate the age related decrease of the TSI between match and first update date.
        // TSIact is almost always equal to the player's TSI during the match in which they got injured,
        // but there is one important exception here:
        // If an older player got injured in a league match on Sun,
        // then for them at the moment after the first update on Mon
        // TSIact = TSI_Sun – X_Mon, where
        // TSI_Sun – the player's TSI at the time of the match on Sun
        // X_Mon – the change in this player's TSI on Mondays in healthy condition.
        // Age should correspond to the current one.
        // For example, on the last Monday before the injury, the player's TSI dropped by 80.
        // At the time of the match in which he got injured, his TSI was TSIvs = 10000,
        // and after the injury TSItr = 6150.
        // For him, TSIzd = 10000 – 80 = 9920. H = 6150 / 9920 = 0.62 = 62%. Str = (1-0.62)*10 = 3.8
        // That is, the player has 62% of his health remaining, which corresponds to an injury sublevel of 3.8 conventional weeks.
        //
        // (The only age dependency of the TSI I know is the dropping at player's birthday from the age of 28 on.
        // Maybe the TSI dropping mentioned by Schum comes from form changes and or skill drops)
        //
        // From the age of 28, the TSI decreases on each birthday,
        // for field players by 1/8 of the actual TSI on each birthday,
        // until from the 34th birthday onward it remains constant at 1/8 of the true TSI.

        // Determination of the starting status of healing
        var playerHealthy = recovery.get(recovery.size()-1);
        if (playerHealthy.getInjuryWeeks() == -1){
            if (recovery.size() > 1){
                var playerInjured = recovery.get(recovery.size()-2);
                var clubData = DBManager.instance().getVerein(playerInjured.getHrfId());
                var doctorLevel = clubData.getAerzte();
                // Find match date when injury happened
                var injuries = DBManager.instance().getInjuries(player.getPlayerId(), playerHealthy.getHrfDate(), playerInjured.getHrfDate());
                if (!injuries.isEmpty()){
                    var health = (double) playerInjured.getTsi() / playerHealthy.getTsi();
                    var injuryDate = injuries.get(0).getMatchDate();
                    // Updates between first update after injury date and injured download increased the health
                    var updates = getDailyUpdatesBetween(injuryDate, playerInjured.getHrfDate());
                    var healthIncreaseBeforeDownload = 0.;
                    for (var update : updates){
                        healthIncreaseBeforeDownload += calculateHealthIncrease(playerInjured, doctorLevel, update);
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
    HODateTime whenHealthy;
    HODateTime whenSlightlyInjured;
}
