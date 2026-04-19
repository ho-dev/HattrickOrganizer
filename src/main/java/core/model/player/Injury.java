package core.model.player;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.HOLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Health calculation based on Schum formula (<a href="https://www82.hattrick.org/Forum/Read.aspx?t=17404127&n=6&v=0&mr=0">...</a>)
 */
public class Injury {

    Injury(Player player) {
        if (!player.isExternallyRecruitedCoach()) {

            // Append to file
            try (FileWriter writer = new FileWriter("healing.csv", true)) { // true = append mode
                var playerHistory = DBManager.instance().loadPlayerHistory(player.getPlayerId());
                Player previousPlayer = null;
                String text = "";
                for ( var entry : playerHistory){
                    if ( entry.getInjuryWeeks() > -1){
                        if (previousPlayer != null && previousPlayer.getInjuryWeeks() == -1){
                            writer.write(getCSVString(previousPlayer));
                            writer.write(System.lineSeparator()); // Add a newline
                        }
                        writer.write(getCSVString(entry));
                        writer.write(System.lineSeparator()); // Add a newline
                    }
                    else if (previousPlayer != null && previousPlayer.getInjuryWeeks() > -1){
                        writer.write(getCSVString(entry));
                        writer.write(System.lineSeparator()); // Add a newline
                    }
                    previousPlayer = entry;
                }
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }




            this.injuryLevel = player.getInjuryWeeks();
            if (injuryLevel == -1) {
                whenHealthy = player.getHrfDate();
            } else {
                calculateRecovery(player);
            }
        }
    }

    private String getCSVString(Player player) {
        String separator = ";";
        var clubData = DBManager.instance().getVerein(player.getHrfId());
        var doctorLevel = clubData.getAerzte();

        String ret = player.getPlayerId() + separator;
        ret += player.getFullName() + separator;
        ret += player.getInjuryWeeks() + separator;
        ret += player.getHrfDate() + separator;
        ret += player.getTsi() + separator;
        ret += player.getAge() + separator;
        ret += player.getAgeDays() + separator;
        ret += player.getSkill(PlayerSkill.FORM) + separator;
        ret += player.getSkill(PlayerSkill.STAMINA) + separator;
        ret += player.getSkill(PlayerSkill.KEEPER) + separator;
        ret += player.getSkill(PlayerSkill.DEFENDING) + separator;
        ret += player.getSkill(PlayerSkill.PASSING) + separator;
        ret += player.getSkill(PlayerSkill.PLAYMAKING) + separator;
        ret += player.getSkill(PlayerSkill.WINGER) + separator;
        ret += player.getSkill(PlayerSkill.SCORING) + separator;
        ret += doctorLevel;

        return ret.replace('.', ',');
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
        double x0Factor =  0.0384;
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
        return (x1Factor * (double) doctorLevel + x0Factor) / (x0Factor + 5 *  x1Factor);
    }

    private double calculateHealthIncrease(Player player, int doctorLevel, HODateTime dateTime){
        var regainerFactor = player.getSpecialty() == Specialty.Regainer.getValue()? 10./9.: 1.0;
        return regainerFactor * calcAgeFactor(player.getAgeAtDate(dateTime).toDouble()) * calcMedicianFactor(doctorLevel);
    }

    private void calculateRecovery(Player player) {

        // Load healing history
        var date = player.getHrfDate();
        ArrayList<Player> recovery = new ArrayList<>();
        recovery.add(player);
        var wasInjured = this.injuryLevel > -1;
        while (wasInjured) {
            var playerBefore = DBManager.instance().getLatestPlayerDownloadBefore(player.getPlayerId(), date.toDbTimestamp());
            if (playerBefore != null) {
                recovery.add(playerBefore);
                wasInjured = playerBefore.getInjuryWeeks() > -1;
                date = playerBefore.getHrfDate();
            } else {
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
                while (recoveryIndex > 0 && playerInjured.getTsi() == playerHealthy.getTsi()){
                    // TSI update not happened yet. It will happen on first update after match date
                    playerInjured = recovery.get(--recoveryIndex);
                }

                // If there were training updates between the healthy player download and the injured download, the
                // healthy tsi might require some correction because of skill or form changes
                // If player is older than 27 years old, the tsi drops also on birthday of the player
//                playerHealthy.setSubskill4PlayerSkill(PlayerSkill.FORM, 0.5);
//                playerInjured.setSubskill4PlayerSkill(PlayerSkill.FORM, 0.5);
//                playerHealthy.setSubskill4PlayerSkill(PlayerSkill.STAMINA, 0.5);
//                playerInjured.setSubskill4PlayerSkill(PlayerSkill.STAMINA, 0.5);


//                var formHealthy = playerHealthy.getSkillValue(PlayerSkill.FORM);
//                playerInjured.setSkillValue(PlayerSkill.FORM, formHealthy);
//                playerInjured.setSubskill4PlayerSkill(PlayerSkill.FORM, 0.);
                var tsiHealthyCalculated = playerHealthy.calculateTSI();
                var tsiInjuredCalculated = playerInjured.calculateTSI();
                double healthyTSI = playerHealthy.getTsi() + tsiInjuredCalculated - tsiHealthyCalculated;
                HOLogger.instance().info(this.getClass(), "---- " + playerInjured.getFullName());
                LogRecovery(playerHealthy.getTsi() / healthyTSI, playerHealthy, healthyTSI);

                var health = playerInjured.getTsi() / healthyTSI;
                LogRecovery(health, playerInjured, healthyTSI);
                var pastUpdates = new ArrayList<>(getDailyUpdatesBetween(playerInjured.getHrfDate(), player.getHrfDate()));
                var update = pastUpdates.stream().findFirst().orElse(null);
                var doctorLevel = 0;
                while (recoveryIndex >= 0) {
                    var p = recovery.get(recoveryIndex--);
//                    p.setSkillValue(PlayerSkill.FORM, formHealthy);

//                    p.setSubskill4PlayerSkill(PlayerSkill.FORM, 0.5);
                    while (update != null && update.isBefore(p.getHrfDate())) {
                        var clubData = DBManager.instance().getVerein(p.getHrfId());
                        doctorLevel = clubData.getAerzte();
                        var healthIncrease = calculateHealthIncrease(player, doctorLevel, update);
                        health += healthIncrease;
                        HOLogger.instance().info(this.getClass(), "UpDate " + update.toLocaleDateTime() + " Health: " + health);
                        pastUpdates.remove(update);
                        update = pastUpdates.stream().findFirst().orElse(null);
                    }
//                    p.setSubskill4PlayerSkill(PlayerSkill.FORM, 0.5);
//                    p.setSubskill4PlayerSkill(PlayerSkill.STAMINA, 0.5);
                    healthyTSI = playerHealthy.getTsi() + p.calculateTSI() - tsiHealthyCalculated;
                    LogRecovery(health, p, healthyTSI);
                    if (this.whenSlightlyInjured == null && health > 0.9) {
                        this.whenSlightlyInjured = update;
                    }
                }

                var nextDailyUpdates = HOVerwaltung.instance().getModel().getXtraDaten().getDailyUpdates();
                while (this.whenHealthy == null) {

                    for (var futureUpdate : nextDailyUpdates) {
                        var increase = calculateHealthIncrease(player, doctorLevel, futureUpdate);
                        if (increase < 0) {
                            return; // No recovery possible
                        }
                        health += increase;
                        HOLogger.instance().info(this.getClass(), "UpDate " + futureUpdate.toLocaleDateTime() + " Health: " + health);
                        if (this.whenSlightlyInjured == null && health >= 0.9) {
                            this.whenSlightlyInjured = futureUpdate;
                        }
                        if (this.whenHealthy == null && health >= 1) {
                            this.whenHealthy = futureUpdate;
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

    private void LogRecovery(double health, Player p, double healthyTSI) {
        HOLogger.instance().info(this.getClass(), "Date " + p.getHrfDate().toLocaleDateTime()
            + " Stamina: " + p.getSkill(PlayerSkill.STAMINA)
            + " Level: " + p.getInjuryWeeks()
            + " Form: " + p.getSkill(PlayerSkill.FORM)
            + " Stamina: " + p.getSkill(PlayerSkill.STAMINA)
            + " Health: " + health
            + " Calculated: " + p.getTsi() / healthyTSI
            + "=" + p.getTsi() + "/" + healthyTSI);
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

    public HODateTime getWhenHealthy() {
        return whenHealthy;
    }

    public HODateTime getWhenSlightlyInjured() {
        return whenSlightlyInjured;
    }

    /**
     * Date when player gets healthy
     * Date is in the past if player is healthy
     * If null, no recovery is possible any more
     */
    HODateTime whenHealthy;
    HODateTime whenSlightlyInjured;
}
