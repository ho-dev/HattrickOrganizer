package core.gui.event;

import javax.swing.event.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Support for handling {@link ChangeListener}s.
 */
public class ChangeEventHandler {

    /** CopyOnWriteArrayList ensures thread-safety */
    private final List<ChangeListener> changeListeners = new CopyOnWriteArrayList<>();

    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        if (changeListeners.contains(changeListener)) {
            changeListeners.remove(changeListener);
        }
    }

    public void fireChangeEvent(ChangeEvent event) {
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }
    }
}
