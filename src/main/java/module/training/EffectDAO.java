package module.training;

import core.db.DBManager;
import core.db.ConnectionManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.model.player.SkillChange;
import core.util.HODateTime;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This class is used to collect all required data and fill the lists of values with instances of
 * value objects.
 *
 * @author NetHyperon
 */
public class EffectDAO {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final List<TrainWeekEffect> trainWeeks = new Vector<>();

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Return list of TrainingEffects for each week
     *
     * @return List<TrainWeekEffect>
     */
    public static List<TrainWeekEffect> getTrainEffect() {
        return trainWeeks;
    }

    private static final String trainingDatesSql =
            "SELECT HRFMIN.hrf_id, HRFMAX.hrf_id, trainingdate" +
            " FROM HRF as HRFMIN, HRF as HRFMAX, (SELECT max(HRF.datum) as maxdate, min(HRF.datum) as mindate, trainingdate" +
            " FROM HRF, XTRADATA" +
            " WHERE HRF.hrf_id=XTRADATA.hrf_id" +
            " GROUP BY trainingdate) AS X" +
            " WHERE maxdate = HRFMAX.datum AND mindate = HRFMIN.datum ORDER BY trainingdate DESC" ;
    private static final String weeksSql =
            "SELECT SUM(marktwert) as totaltsi, AVG(marktwert) as avgtsi , SUM(form) as form, COUNT(form) as number FROM SPIELER WHERE trainer = 0 AND hrf_id = ?";

    private static final String playersSql =
            "SELECT * FROM SPIELER WHERE trainer = 0 AND hrf_id = ?";

    private static final String playersBasics =
            "SELECT * FROM SPIELER, BASICS WHERE trainer = 0 AND SPIELER.hrf_id = BASICS.hrf_id AND SPIELER.hrf_id = ?";

            /**
             * Calculates the training weeks and returns a list of TrainWeek instances. These value object
             * contain the last hrf id before the training update and the first hrf id after the update.
             */
    public static void reload() {
        try {
            Map<String,List<SkillChange>> weeklySkillups = new HashMap<>();

            // Loop through all player (also old players) to get all trained skillups.
            // Group these skillups by season and week.
            List<Player> players = new Vector<>(HOVerwaltung.instance().getModel().getCurrentPlayers());

            players.addAll(HOVerwaltung.instance().getModel().getFormerPlayers());

            for (Player player : players) {
                PastTrainingManager otm = new PastTrainingManager(player);
                List<SkillChange> skillups = otm.getTrainedSkillups();

                for (SkillChange skillup : skillups) {
                    String key = skillup.getHtSeason() + "-" + skillup.getHtWeek(); //$NON-NLS-1$
                    List<SkillChange> collectedSkillups = weeklySkillups.computeIfAbsent(key, k -> new Vector<>());

                    collectedSkillups.add(skillup);
                }
            }

            ConnectionManager db = DBManager.instance().getConnectionManager();
            trainWeeks.clear();
            ResultSet tDateset = db.executePreparedQuery(trainingDatesSql);
            List<TrainWeekEffect> trainingDates = new Vector<>();

            try {
                int first_in_week = 0;
                assert tDateset != null;
                if (tDateset.next()) {
                    first_in_week = tDateset.getInt(1);
                }
                while (tDateset.next()) {
                    var trainDate = HODateTime.fromDbTimestamp(tDateset.getTimestamp(3));
                    var htWeek =trainDate.toLocaleHTWeek();
                    trainingDates.add(new TrainWeekEffect(htWeek.week, htWeek.season, tDateset.getInt(1), first_in_week));
                    first_in_week = tDateset.getInt(1);
                }

                tDateset.close();
            } catch (Exception ignored) {
            }

            for (TrainWeekEffect week : trainingDates) {
                ResultSet set = db.executePreparedQuery(weeksSql, week.getHRFafterUpdate());
                if (set != null) {
                    set.next();
                    week.setTotalTSI(set.getInt("totaltsi")); //$NON-NLS-1$
                    week.setAverageTSI(set.getInt("avgtsi")); //$NON-NLS-1$

                    double avgForm = 0.0d;

                    if (set.getInt("number") != 0) { //$NON-NLS-1$
                        avgForm = set.getDouble("form") / set.getInt("number"); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    week.setAverageForm(avgForm);
                    set.close();
                }

                Map<Integer, PlayerValues> valuesBeforeUpdate = new HashMap<>();

                set = db.executePreparedQuery(playersSql, week.getHRFbeforeUpdate());

                if (set != null) {
                    while (set.next()) {
                        PlayerValues result = new PlayerValues(set.getInt("marktwert"), //$NON-NLS-1$
                                set.getInt("form")); //$NON-NLS-1$

                        valuesBeforeUpdate.put(set.getInt("spielerid"), result); //$NON-NLS-1$
                    }

                    set.close();
                }

                set = db.executePreparedQuery(playersBasics, week.getHRFafterUpdate());

                if (set != null) {
                    while (set.next()) {
                        Integer playerID = set.getInt("spielerid"); //$NON-NLS-1$

                        if (valuesBeforeUpdate.containsKey(playerID)) {
                            PlayerValues before = valuesBeforeUpdate.get(playerID);

                            week.addTSI(set.getInt("marktwert") - before.getTsi()); //$NON-NLS-1$
                            week.addForm(set.getInt("form") - before.getForm()); //$NON-NLS-1$
                        }
                    }

                    set.close();
                }

                // Set amount of skillups for this training week
                String key = week.getHattrickSeason() + "-" + week.getHattrickWeek(); //$NON-NLS-1$

                if (weeklySkillups.containsKey(key)) {

                    List<SkillChange> wsList = weeklySkillups.get(key);
                    week.setAmountSkillups(wsList.size());

                    if (!wsList.isEmpty()) {
                        SkillChange su = wsList.get(0);
                        week.setTrainingType(su.getType());
                    }
                }

                trainWeeks.add(week);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
