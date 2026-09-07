package core.model.player;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.HOLogger;
import java.time.temporal.ChronoUnit;

/**
 * Health calculation based on Schum formula (<a href="https://www82.hattrick.org/Forum/Read.aspx?t=17404127&n=6&v=0&mr=0">...</a>)
 */
class Injury {

    private static final int NOT_INJURED = -1;
    private static final int MAX_DOCTOR_LEVEL = 5;

    Injury(Player player) {
        if (!player.isExternallyRecruitedCoach()) {
            if (player.getInjuryWeeks() != NOT_INJURED) {
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
        return (x1Factor * doctorLevel + x0Factor) / (x0Factor + MAX_DOCTOR_LEVEL * x1Factor);
    }

    /**
     * Calculate player's health increase per daily update
     * @param player Player
     * @param doctorLevel Club's doctor level [0..5]
     * @param dateTime Date of the daily update
     * @return double The health increment
     */
    private static double calculateHealthIncrease(Player player, int doctorLevel, HODateTime dateTime) {
        var regainerFactor = player.getSpecialty() == Specialty.Regainer.getValue() ? 10. / 9. : 1.0;
        return regainerFactor * calcAgeFactor(player.getAgeAtDate(dateTime).toDouble()) * calcMedicianFactor(doctorLevel);
    }

    /**
     * Calculate the player's recovery from injuries
     * First the current health of the player is estimated. Health value is 1 if player is not injured.
     * Injury level are reported by hattrick.
     * There are 5 possible injury levels, which are indicated on the player's page and expressed as a percentage of remaining health
     * <p>
     * Health Level, Health range
     * Plaster          0.9 <= health < 1
     * 1                0.8 <= health < 0.9
     * 2                0.7 <= health < 0.8
     * 3                0.6 <= health < 0.7
     * 4                0.5 <= health < 0.6
     * It is impossible to get an injury of more than +4 in a match, but for players over 40, injuries do not decrease;
     * they increase and eventually reach +9.
     * The calculated Health is taken as start of the recovery. The increments for the upcoming daily updates are calculated
     * as long as the player becomes healthy or the daily update increment becomes negative, which means that the
     * player can no longer recover. The player becomes an invalid in this case.
     * @param player Player
     */
    private void calculateRecovery(Player player) {
        var hoModel = HOVerwaltung.instance().getModel();
        var persistenceManager = hoModel.getPersistenceManager();
        var clubData = persistenceManager.getVerein(player.getHrfId());
        var doctorLevel = clubData.getAerzte();
        var nextDailyUpdates = hoModel.getXtraDaten().getDailyUpdates().stream().sorted().toList();
        var calculatedHealth = calculateHealth(player);
        while (this.whenHealthy == null) {

            for (var futureUpdate : nextDailyUpdates) {
                var increase = calculateHealthIncrease(player, doctorLevel, futureUpdate);
                if (increase < 0) {
                    isSportsInvalid = true;
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

    /**
     * Calculate the current health of the player.
     * Formula of the health is health = currentTSI / tSInotInjured
     * Tsi of the not injured player is calculated with the current skill values of the player.
     * In two cases, it can happen that the health calculated this way does not match the level reported by Hattrick:
     * (1) Directly after the match, when the player gets injured the current TSI does not reflect the injury but remains on
     * the healthy value (until the first daily update?).
     * (2) The form skill can vary and lead to an error in the calculated TSI value. Same with all other skill values
     * if they are not adjusted correctly in the skills editor panel.
     * In these cases the type of estimate is not set to realistic but either optimistic or pessimistic.
     * @param player Player
     * @return Double Health value [0..1]
     */
    private double calculateHealth(Player player) {
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
            typeOfRecoveryEstimation = TypeOfRecoveryEstimation.PESSIMISTIC_ESTIMATE;
        } else if (calculatedHealth >= healthMin + 0.1) {
            calculatedHealth = healthMin + 0.09;
            typeOfRecoveryEstimation = TypeOfRecoveryEstimation.OPTIMISTIC_ESTIMATE;
        } else {
            typeOfRecoveryEstimation = TypeOfRecoveryEstimation.REALISTIC_ESTIMATE;
        }
        return calculatedHealth;
    }

    /**
     * Load the latest known form skill value before player got injured
     * If no value is found in the database the current form skill value of the player is returned.
     * @param player Player
     * @return double Form skill value
     */
    private double loadFormBeforeInjured(Player player) {
        var date = player.getHrfDate();
        while (true) {
            var persistenceManager = HOVerwaltung.instance().getModel().getPersistenceManager();
            var playerBefore = persistenceManager.getLatestPlayerDownloadBefore(player.getPlayerId(), date.toDbTimestamp());
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
     * Return the date when player becomes healthy.
     * Null, if the player is healthy
     * @return HODateTime
     */
    public HODateTime getWhenHealthy() {
        return whenHealthy;
    }

    /**
     * Return the date when the player becomes slightly injured
     * Null, if the player is healthy or slightly injured
     * @return HODateTime
     */
    public HODateTime getWhenSlightlyInjured() {
        return whenSlightlyInjured;
    }

    /**
     * Disabled player cannot recover from injury anymore
     * @return boolean
     */
    public boolean isSportsInvalid() {
        return isSportsInvalid;
    }

    /**
     * Get type of recovery estimate.
     * @return TypeOfRecoveryEstimation
     */
    public TypeOfRecoveryEstimation getTypeOfEstimate() {
        return typeOfRecoveryEstimation;
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
     * True if the player cannot recover from injury anymore
     */
    private boolean isSportsInvalid = false;

    private TypeOfRecoveryEstimation typeOfRecoveryEstimation = TypeOfRecoveryEstimation.REALISTIC_ESTIMATE;
}
