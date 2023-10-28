// %1127326956603:plugins%
package core.gui;

/**
 * An Object of this interface can be registered in the IGUI-Interface.  The method are called, if
 * data change ( new HRF downloaded, etc. )
 */
public interface IRefreshable {
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Called, if the data changed
     */
    public void refresh();
}
