package module.youth;

import core.datatype.CBItem;
import core.gui.comp.entry.IHOTableEntry;
import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.event.ActionEvent;

public class YouthTrainingTableEntry extends JLabel implements IHOTableEntry {

    public static class ComboBoxModel implements javax.swing.ComboBoxModel<YouthTrainingType> {

        private YouthTrainingType value;

        @Override
        public void setSelectedItem(Object anItem) {
            value = (YouthTrainingType) anItem;
        }

        public static String getLabel(YouthTrainingType value){
            var hov = HOVerwaltung.instance();
            if ( value == null) return hov.getLanguageString("undefined");
            return hov.getLanguageString(value.toString());
        }

        @Override
        public Object getSelectedItem() {
            return getLabel(value);
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

    private YouthTrainingType youthTraining;

    public YouthTrainingTableEntry(YouthTrainingType training) {
        super(ComboBoxModel.getLabel(training));
        this.youthTraining = training;
    }

    @Override
    public JComponent getComponent(boolean isSelected) {

        return this;

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

    }

    @Override
    public void updateComponent() {

    }
}
