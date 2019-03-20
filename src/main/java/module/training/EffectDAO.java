package module.training;

import core.db.DBManager;
import core.db.JDBCAdapter;
import core.model.HOVerwaltung;
import core.model.player.ISkillup;
import core.model.player.Player;
import core.util.HTCalendarFactory;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
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

    private static List<TrainWeekEffect> trainWeeks = new Vector<TrainWeekEffect>();

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Return list of TrainingEffects for each week
     *
     * @return
     */
    public static List<TrainWeekEffect> getTrainEffect() {
        return trainWeeks;
    }

    /**
     * Calculates the training weeks and returns a list of TrainWeek instances. These value object
     * contain the last hrf id before the training update and the first hrf id after the update.
     */
    public static void reload() {
        try {
            Map<String,List<ISkillup>> weeklySkillups = new HashMap<String,List<ISkillup>>();

            // Loop through all player (also old players) to get all trained skillups.
            // Group these skillups by season and week.
            List<Player> players = new Vector<Player>(HOVerwaltung.instance().getModel().getAllSpieler());

            players.addAll(HOVerwaltung.instance().getModel().getAllOldSpieler());

            for (Iterator<Player> iterPlayers = players.iterator(); iterPlayers.hasNext();) {
                Player player = (Player) iterPlayers.next();
                OldTrainingManager otm = new OldTrainingManager(player);
                List<ISkillup> skillups = otm.getTrainedSkillups();

                for (Iterator<ISkillup> iterSkillups = skillups.iterator(); iterSkillups.hasNext();) {
                    ISkillup skillup = (ISkillup) iterSkillups.next();
                    String key = skillup.getHtSeason() + "-" + skillup.getHtWeek(); //$NON-NLS-1$
                    List<ISkillup> collectedSkillups = weeklySkillups.get(key);

                    if (collectedSkillups == null) {
                        collectedSkillups = new Vector<ISkillup>();
                        weeklySkillups.put(key, collectedSkillups);
                    }

                    collectedSkillups.add(skillup);
                }
            }

            JDBCAdapter db = DBManager.instance().getAdapter();

            trainWeeks.clear();

            ResultSet tDateset = db.executeQuery("SELECT HRFMIN.hrf_id, HRFMAX.hrf_id, trainingdate" +
                    " FROM HRF as HRFMIN, HRF as HRFMAX, (SELECT max(HRF.datum) as maxdate, min(HRF.datum) as mindate, trainingdate" +
                        " FROM HRF, XTRADATA" +
                        " WHERE HRF.hrf_id=XTRADATA.hrf_id" +
                        " GROUP BY trainingdate) AS X" +
                        " WHERE maxdate = HRFMAX.datum AND mindate = HRFMIN.datum ORDER BY trainingdate DESC");

            List<TrainWeekEffect> trainingDates = new Vector<TrainWeekEffect>();

            try {
                int first_in_week = 0;
                if (tDateset.next()) {
                    first_in_week = tDateset.getInt(1);
                }
                while (tDateset.next()) {
                    Timestamp trainDate = tDateset.getTimestamp(3);
                    int HTWeek = HTCalendarFactory.getHTWeek(trainDate);
                    int HTSeason = HTCalendarFactory.getHTSeason(trainDate);
                    trainingDates.add(new TrainWeekEffect(HTWeek, HTSeason, tDateset.getInt(1), first_in_week));
                    first_in_week = tDateset.getInt(1);
                }

                tDateset.close();
            } catch (Exception e) {
            }

            for (Iterator<TrainWeekEffect> iter = trainingDates.iterator(); iter.hasNext();) {
                TrainWeekEffect week = iter.next();

                ResultSet set = db.executeQuery("SELECT SUM(marktwert) as totaltsi, AVG(marktwert) as avgtsi , SUM(form) as form, COUNT(form) as number FROM SPIELER WHERE trainer = 0 AND hrf_id = " //$NON-NLS-1$
                    + Integer.toString(week.getHRFafterUpdate()));

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

                Map<Integer,PlayerValues> valuesBeforeUpdate = new HashMap<Integer,PlayerValues>();

                set = db.executeQuery("SELECT * FROM SPIELER WHERE trainer = 0 AND hrf_id = " //$NON-NLS-1$
                        + Integer.toString(week.getHRFbeforeUpdate()));

                if (set != null) {
                    while (set.next()) {
                        PlayerValues result = new PlayerValues(set.getInt("marktwert"), //$NON-NLS-1$
                                set.getInt("form")); //$NON-NLS-1$

                        valuesBeforeUpdate.put(new Integer(set.getInt("spielerid")), result); //$NON-NLS-1$
                    }

                    set.close();
                }

                set = db.executeQuery("SELECT * FROM SPIELER, BASICS WHERE trainer = 0 AND SPIELER.hrf_id = BASICS.hrf_id AND SPIELER.hrf_id = " //$NON-NLS-1$
                        + Integer.toString(week.getHRFafterUpdate()));

                if (set != null) {
                    while (set.next()) {
                        Integer playerID = new Integer(set.getInt("spielerid")); //$NON-NLS-1$

                        if (valuesBeforeUpdate.containsKey(playerID)) {
                            PlayerValues before = (PlayerValues) valuesBeforeUpdate.get(playerID);

                            week.addTSI(set.getInt("marktwert") - before.getTsi()); //$NON-NLS-1$
                            week.addForm(set.getInt("form") - before.getForm()); //$NON-NLS-1$
                        }
                    }

                    set.close();
                }

                // Set amount of skillups for this training week
                String key = week.getHattrickSeason() + "-" + week.getHattrickWeek(); //$NON-NLS-1$

                if (weeklySkillups.containsKey(key)) {

                    List<ISkillup> wsList = weeklySkillups.get(key);
                    week.setAmountSkillups(wsList.size());

                    if (wsList.size() > 0) {
                        ISkillup su = (ISkillup) wsList.get(0);
                        week.setTrainingType(su.getType());
                    }
                }

                if (week != null) {
                    trainWeeks.add(week);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
