package core.gui;

// panel will be notified when data changes and will redraw themselves
public interface Refreshable extends IRefreshable {
    void reInit();
}
