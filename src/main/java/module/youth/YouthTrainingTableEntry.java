package module.youth;

import core.gui.comp.entry.IHOTableEntry;
import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListDataListener;

public class YouthTrainingTableEntry implements IHOTableEntry {

    public static class ComboBoxModel implements javax.swing.ComboBoxModel<YouthTrainingType> {

        private YouthTrainingType value;

        @Override
        public void setSelectedItem(Object anItem) {
            value = (YouthTrainingType) anItem;
        }

        public static String getLabelText(YouthTrainingType value){
            var hov = HOVerwaltung.instance();
            if ( value == null) return hov.getLanguageString("undefined");
            return hov.getLanguageString(value.toString());
        }

        @Override
        public Object getSelectedItem() {
            return new YouthTrainingTableEntry(value);
        }

        @Override
        public int getSize() {
            return YouthTrainingType.values().length+1;
        }

        @Override
        public YouthTrainingType getElementAt(int index) {
            return YouthTrainingType.valueOf(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {

        }

        @Override
        public void removeListDataListener(ListDataListener l) {

        }
    }

    private JLabel label;
    private YouthTrainingType trainingType;

    public YouthTrainingTableEntry(YouthTrainingType trainingType) {
        this.trainingType = trainingType;
        createComponent();
    }

    public YouthTrainingType getTrainingType() {
        return trainingType;
    }

    @Override
    public JComponent getComponent(boolean isSelected) {
        return label;
    }

    @Override
    public void clear() {
    }

    @Override
    public int compareTo(@NotNull IHOTableEntry obj) {
        return 0;
    }

    @Override
    public int compareToThird(IHOTableEntry obj) {
        return 0;
    }

    @Override
    public void createComponent() {
        label = new JLabel(ComboBoxModel.getLabelText(trainingType));
    }

    @Override
    public void updateComponent() {
        label.setText(ComboBoxModel.getLabelText(trainingType));
    }
}
