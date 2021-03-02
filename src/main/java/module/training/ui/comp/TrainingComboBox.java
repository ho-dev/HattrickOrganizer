package module.training.ui.comp;

import core.constants.TrainingType;
import core.datatype.CBItem;
import javax.swing.*;
import java.awt.*;

public class TrainingComboBox extends JComboBox {

	/**
     * Creates a new TrainingComboBox object.
     */
    public TrainingComboBox() {
        super(TrainingType.ITEMS);
    }


    /**
     * Creates a new TrainingComboBox object with an empty entry
     */
    public TrainingComboBox(boolean emptyEntry) {
        super();
        if(emptyEntry){
            addItem(null);
        }
        for(CBItem _trainingType:TrainingType.ITEMS){
            addItem(_trainingType);
        }

        setRenderer(new HighLightRowRenderer(getRenderer()));
    }


    public static class HighLightRowRenderer implements ListCellRenderer {

        private final ListCellRenderer delegate;

        public HighLightRowRenderer(ListCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Dimension size = component.getPreferredSize();
            if (value == null) {
                return new JLabel(" ");
            }
            return component;
        }
    }

}
