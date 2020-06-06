// %1572960370:de.hattrickorganizer.gui%
package core.gui;

import core.util.HOLogger;

import java.util.Vector;



/**
 * Managed das Refreshen
 */
public class RefreshManager {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static RefreshManager m_clRefreshManager;

    //~ Instance fields ----------------------------------------------------------------------------

    private Vector<IRefreshable> m_clRefreshable = new Vector<IRefreshable>();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new RefreshManager object.
     */
    private RefreshManager() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    public static RefreshManager instance() {
        if (m_clRefreshManager == null) {
            m_clRefreshManager = new RefreshManager();
        }

        return m_clRefreshManager;
    }

    /**
     * Informiert alle registrierten Objekte
     */
    public void doReInit() {
        for (int i = 0; i < m_clRefreshable.size(); i++) {
            try {
                //no plugin
                if (m_clRefreshable.get(i) instanceof Refreshable) {
                    ((Refreshable) m_clRefreshable.get(i)).reInit();
                }
                //plugin
                else {
                    (m_clRefreshable.get(i)).refresh();
                }
            } catch (Exception e) {
                HOLogger.instance().log(getClass(),"Gefangener Fehler beim doReInit:");
                HOLogger.instance().log(getClass(),e);
            }
        }
        System.gc();
        Thread.yield();
    }

    /**
     * Informiert alle registrierten Objekte
     */
    public void doRefresh() {
        for (int i = 0; i < m_clRefreshable.size(); i++) {
            try {
                ((IRefreshable) m_clRefreshable.get(i)).refresh();
            } catch (Exception e) {
                HOLogger.instance().log(getClass(),"Gefangener Fehler beim doRefresh:");
                HOLogger.instance().log(getClass(),e);
            }
        }

        System.gc();
        Thread.yield();
    }

    public void registerRefreshable(IRefreshable refreshable) {
        m_clRefreshable.add(refreshable);
    }

    public void unregisterRefreshable(IRefreshable refreshable) {
        m_clRefreshable.remove(refreshable);
    }
}
