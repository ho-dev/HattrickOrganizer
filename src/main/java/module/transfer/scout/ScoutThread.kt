package module.transfer.scout;

import java.util.Vector;

public class ScoutThread implements Runnable {
    //~ Instance fields ----------------------------------------------------------------------------

    private Vector<ScoutEintrag> m_vScoutEintraege = new Vector<ScoutEintrag>();
    private int difference = core.model.UserParameter.instance().TimeZoneDifference * 3600000;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of ScoutThread
     */
    public ScoutThread(Vector<ScoutEintrag> scouts) {
        m_vScoutEintraege = new Vector<ScoutEintrag>(scouts);
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final void run() {
        module.transfer.scout.ScoutEintrag se;
        final java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());
        final java.sql.Timestamp ts2 = new java.sql.Timestamp(System.currentTimeMillis());
        final int DELAY_BEFORE_DEADLINE = 300000;  // 5 minutes

        while (true) {
            ts.setTime(System.currentTimeMillis());

            for (int i = 0;
                 (m_vScoutEintraege != null) && !m_vScoutEintraege.isEmpty()
                 && (i < m_vScoutEintraege.size()); ++i) {
                se = m_vScoutEintraege.elementAt(i);
                ts2.setTime(se.getDeadline().getTime() - DELAY_BEFORE_DEADLINE + difference);

                if (ts2.before(ts) && !se.getDeadline().before(ts) && !se.isWecker()) {

                    new module.transfer.scout.Wecker(se.getName() + " ("
                                                                      + se.getPlayerID() + ")"
                                                                      + "\r\n"
                                                                      + se.getDeadline().toString());
                    se.setWecker(true);
                }
            }

            try {
                //30 sec heia machen
                Thread.sleep(30000);
            } catch (Exception e) {
            }
        }
    }

    /**
     * startet einen Scout
     */
    public static ScoutThread start(Vector<ScoutEintrag> scouts) {
        final ScoutThread temp = new ScoutThread(scouts);

        new Thread(temp).start();

        return temp;
    }
 
    public final void setVector(Vector<ScoutEintrag> vec) {
        m_vScoutEintraege = vec;
    }
}
