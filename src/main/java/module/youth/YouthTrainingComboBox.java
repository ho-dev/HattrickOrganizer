package module.youth;

import core.constants.TrainingType;
import core.datatype.CBItem;
import core.gui.comp.entry.IHOTableEntry;
import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class YouthTrainingComboBox extends JComboBox implements IHOTableEntry {

    private static CBItem[] Items;

    private static CBItem[] getItems() {
        if ( Items == null) {
            var hov = HOVerwaltung.instance();
            Items = new CBItem[YouthTrainingType.values().length + 1];
            Items[0] = new CBItem(hov.getLanguageString("undefined"), 0);
            for (var tt : YouthTrainingType.values()) {
                Items[tt.getValue()] = new CBItem(hov.getLanguageString(tt.toString()), tt.getValue());
            }
        }
        return Items;
    }

    private YouthTraining youthTraining;
    private YouthTraining.TrainingPrio prio;
    public YouthTrainingComboBox(YouthTraining youthTraining, YouthTraining.TrainingPrio prio) {
        super(getItems());
        this.youthTraining = youthTraining;
        this.prio = prio;
        int ind = 0;
        var training = youthTraining.getTraining(prio);
        if ( training != null){
            ind = training.getValue();
        }
        setSelectedIndex(ind);
    }

    private void changedSelection(){
        var selectionIndex = getSelectedIndex();
        if ( selectionIndex == 0){
            this.youthTraining.setTraining(this.prio, null);
        }
        else {
            this.youthTraining.setTraining(this.prio, YouthTrainingType.valueOf(selectionIndex));
        }
    }

    @Override
    public JComponent getComponent(boolean isSelected) {
        return this;
    }

    @Override
    public void clear() {
        setSelectedIndex(0);
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
