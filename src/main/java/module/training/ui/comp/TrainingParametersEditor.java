package module.training.ui.comp;

import javax.swing.JComboBox;


/**
 * ComboBox for editing the training setting parameters in training table
 */
public class TrainingParametersEditor extends JComboBox {


    public TrainingParametersEditor(int min, int max, boolean emptyEntry) {
        super();
        if(emptyEntry){
            addItem(null);
        }
        if ((min < 0) || (min > 100)) {
        	min = 0;
        }
        
        for (int i = min; i <= max; i++) {
            addItem(i);
        }
        setRenderer(new TrainingComboBox.HighLightRowRenderer(getRenderer()));
    }

    public TrainingParametersEditor(int min, int max) {
        this(min, max, false);
    }


    public TrainingParametersEditor(int min, boolean emptyEntry) {
        this(min, 100, emptyEntry);
    }

    public TrainingParametersEditor(int min) {
        this(min, 100, false);
    }
}
