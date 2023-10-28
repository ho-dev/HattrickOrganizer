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
     * Informs all registered objects
     */
    public void doReInit() {
        for (IRefreshable iRefreshable : m_clRefreshable) {
            try {
                //no plugin
                if (iRefreshable instanceof Refreshable) {
                    ((Refreshable) iRefreshable).reInit();
                }
                //plugin
                else {
                    iRefreshable.refresh();
                }
            } catch (Exception e) {
                HOLogger.instance().log(getClass(), "Refresh Manager: doReInit() throws error");
                HOLogger.instance().log(getClass(), e);
            }
        }
        System.gc();
        Thread.yield();
    }

    /**
     * Informs all registered objects
     */
    public void doRefresh() {
        for (IRefreshable iRefreshable : m_clRefreshable) {
            try {
                iRefreshable.refresh();
            } catch (Exception e) {
                HOLogger.instance().log(getClass(), "Refresh Manager: doRefresh() throws error");
                HOLogger.instance().log(getClass(), e);
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
