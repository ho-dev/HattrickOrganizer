package core.model.player;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.HOLogger;

import java.time.temporal.ChronoUnit;

/**
 * Health calculation based on Schum formula (<a href="https://www82.hattrick.org/Forum/Read.aspx?t=17404127&n=6&v=0&mr=0">...</a>)
 */
public class Injury {

    private static final int NOT_INJURED = -1;

    Injury(Player player) {
        if (!player.isExternallyRecruitedCoach()) {
            if (player.getInjuryWeeks() > NOT_INJURED) {
                calculateRecovery(player);
            }
        }
    }

    /**
     * Calculate the age related factor of the healing increase.
     * Fitting of the parameters released by Schum.
     *
     * @param age double: Age of the player [17..]
     * @return double
     */
    private static double calcAgeFactor(double age) {
        double x2Factor = 0.000016;
        double x1Factor = -0.002;
        double x0Factor = 0.0384;
        double x = age - 17;
        return x2Factor * x * x + x1Factor * x + x0Factor;
    }

    /**
     * Calculate the doctor related factor of the healing increase.
     *
     * @param doctorLevel int [0..5]
     * @return double
     */
    private static double calcMedicianFactor(int doctorLevel) {
        double x1Factor = 0.2124;
        double x0Factor = 1;
        return (x1Factor * (double) doctorLevel + x0Factor) / (x0Factor + 5 * x1Factor);
    }

    private static double calculateHealthIncrease(Player player, int doctorLevel, HODateTime dateTime) {
        var regainerFactor = player.getSpecialty() == Specialty.Regainer.getValue() ? 10. / 9. : 1.0;
        return regainerFactor * calcAgeFactor(player.getAgeAtDate(dateTime).toDouble()) * calcMedicianFactor(doctorLevel);
    }

    private void calculateRecovery(Player player) {
        var formBeforeInjured = loadFormBeforeInjured(player);
        if (formBeforeInjured > player.getForm() + 1) {
            player.setSkillValue(PlayerSkill.FORM, player.getForm() + 0.99);
        } else if (formBeforeInjured >= player.getForm()) {
            player.setSkillValue(PlayerSkill.FORM, formBeforeInjured);
        }

        var calculatedTSI = player.calculateTSI();
        var calculatedHealth = (double) player.getTsi() / calculatedTSI;
        var healthMin = 0.9 - player.getInjuryWeeks() * 0.1;
        if (calculatedHealth < healthMin) {
            calculatedHealth = healthMin;
            typeOfEstimate = TypeOfEstimate.PESSIMISTIC_ESTIMATE;
        } else if (calculatedHealth >= healthMin + 0.1) {
            calculatedHealth = healthMin + 0.09;
            typeOfEstimate = TypeOfEstimate.OPTIMISTIC_ESTIMATE;
        } else {
            typeOfEstimate = TypeOfEstimate.REALISTIC_ESTIMATE;
        }

        var clubData = DBManager.instance().getVerein(player.getHrfId());
        var doctorLevel = clubData.getAerzte();

        var nextDailyUpdates = HOVerwaltung.instance().getModel().getXtraDaten().getDailyUpdates();
        while (this.whenHealthy == null) {

            for (var futureUpdate : nextDailyUpdates) {
                var increase = calculateHealthIncrease(player, doctorLevel, futureUpdate);
                if (increase < 0) {
                    isInvalid = true;
                    return; // No recovery possible
                }
                calculatedHealth += increase;
                HOLogger.instance().info(this.getClass(), "UpDate " + futureUpdate.toLocaleDateTime() + " Health: " + calculatedHealth);
                if (this.whenSlightlyInjured == null && calculatedHealth >= 0.9 && player.getInjuryWeeks() > 0) {
                    this.whenSlightlyInjured = futureUpdate;
                }
                if (this.whenHealthy == null && calculatedHealth >= 1) {
                    this.whenHealthy = futureUpdate;
                    break;
                }
            }
            if (this.whenHealthy == null) {
                nextDailyUpdates = nextDailyUpdates.stream().map(v -> v.plus(7, ChronoUnit.DAYS)).toList();
            }
        }
    }

    private double loadFormBeforeInjured(Player player) {
        var date = player.getHrfDate();
        while (true) {
            var playerBefore = DBManager.instance().getLatestPlayerDownloadBefore(player.getPlayerId(), date.toDbTimestamp());
            if (playerBefore != null) {
                if (playerBefore.getInjuryWeeks() == NOT_INJURED) {
                    return playerBefore.getSkill(PlayerSkill.FORM);
                }
                date = playerBefore.getHrfDate();
            } else {
                break; // No data of healthy player available
            }
        }
        return player.getSkill(PlayerSkill.FORM);
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
    public HODateTime getWhenHealthy() {
        return whenHealthy;
    }

    public HODateTime getWhenSlightlyInjured() {
        return whenSlightlyInjured;
    }

    public boolean getIsInvalid() {
        return isInvalid;
    }

    public TypeOfEstimate getTypeOfEstimate() {
        return typeOfEstimate;
    }

    /**
     * Date when player gets healthy
     * Date is null if player is healthy
     */
    private HODateTime whenHealthy;
    /**
     * Date when player gets slightly injured
     * Date is null if player is healthy or slightly injured
     */
    private HODateTime whenSlightlyInjured;

    /**
     * True if the player is an invalid (no recovery possible)
     */
    private boolean isInvalid = false;

    public enum TypeOfEstimate {
        REALISTIC_ESTIMATE,
        OPTIMISTIC_ESTIMATE,
        PESSIMISTIC_ESTIMATE,
    }

    private TypeOfEstimate typeOfEstimate = TypeOfEstimate.REALISTIC_ESTIMATE;
}
