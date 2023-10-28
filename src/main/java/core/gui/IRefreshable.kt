package core.gui

/**
 * An Object of this interface can be registered in the IGUI-Interface.
 * The <code>refresh</code> method is called, if data changes (new HRF downloaded, etc.).
 */
interface IRefreshable {
    /**
     * Called, if the data changed
     */
    fun refresh()
}
