package core.util;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class HODividerListener implements PropertyChangeListener {
    public Integer location;
    public HODividerListener(Integer location) {
        this.location = location;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Number value = (Number) evt.getNewValue();
        location = value.intValue();
    }

    public void init(JSplitPane pane) {
        if (location != null && location >= 0) pane.setDividerLocation(location);
        pane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
    }
}
