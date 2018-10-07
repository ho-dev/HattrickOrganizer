package core.gui;

/**
 * Die Implementation kann sich beim MainFrame anmelden und wird dann bei Datenänderungen
 * aufgefordert sich neu zu zeichnen
 */
public interface Refreshable extends IRefreshable {
    //~ Methods ------------------------------------------------------------------------------------
    public void reInit();
}
