package core.gui

import core.util.HOLogger

import java.util.Vector



/**
 * Manages the refreshing of the different components of HO when an event occurs.
 */
object RefreshManager {

    private val m_clRefreshable = Vector<IRefreshable>()
    /**
     * Informs all registered objects
     */
    fun doReInit() {
        for (iRefreshable in m_clRefreshable) {
            try {
                //no plugin
                if (iRefreshable is Refreshable) {
                    iRefreshable.reInit()
                } else {
                    iRefreshable.refresh()
                }
            } catch (e: Exception) {
                HOLogger.instance().log(javaClass, "Refresh Manager: doReInit() throws error")
                HOLogger.instance().log(javaClass, e)
            }
        }
        System.gc()
        Thread.yield()
    }

    /**
     * Informs all registered objects
     */
    fun doRefresh() {
        for (iRefreshable in m_clRefreshable) {
            try {
                iRefreshable.refresh()
            } catch (e: Exception) {
                HOLogger.instance().log(javaClass, "Refresh Manager: doRefresh() throws error")
                HOLogger.instance().log(javaClass, e)
            }
        }

        System.gc()
        Thread.yield()
    }

    fun registerRefreshable(refreshable: IRefreshable) = m_clRefreshable.add(refreshable)

    fun unregisterRefreshable(refreshable: IRefreshable) = m_clRefreshable.remove(refreshable)

}
