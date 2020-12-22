package module.youth;

import core.datatype.CBItem;
import core.gui.comp.entry.IHOTableEntry;
import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class YouthTrainingComboBoxItem extends CBItem implements IHOTableEntry {

    private static JComboBox<CBItem> comboBox;
    private static CBItem[] items;

    public static JComboBox<CBItem> getComboBox() {
        if ( comboBox == null) {
            var hov = HOVerwaltung.instance();
            items = new CBItem[YouthTrainingType.values().length + 1];
            items[0] = new CBItem(hov.getLanguageString("undefined"), 0);
            for (var tt : YouthTrainingType.values()) {
                items[tt.getValue()] = new CBItem(hov.getLanguageString(tt.toString()), tt.getValue());
            }
            comboBox = new JComboBox<>(items);
            comboBox.setEditable(false);
        }
        return comboBox;
    }

    private YouthTrainingType youthTraining;

    public YouthTrainingComboBoxItem(YouthTrainingType training) {
        super(String.valueOf(training), training == null ? 0 : training.getValue());
        this.youthTraining = training;
    }

    private void changedSelection(ActionEvent action) {
        //this.youthTraining.setTraining(this.prio, YouthTrainingType.valueOf(getSelectedIndex()));
    }

    @Override
    public JComponent getComponent(boolean isSelected) {
        int ind = 0;
        if ( youthTraining != null){
            ind = youthTraining.getValue();
        }
        var ret = getComboBox();
        ret.setSelectedIndex(ind);
        return ret;
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
