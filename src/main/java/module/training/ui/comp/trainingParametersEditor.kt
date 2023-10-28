package module.training.ui.comp;

import javax.swing.JComboBox;


/**
 * ComboBox for editing the training paramters in Training table
 */
public class trainingParametersEditor extends JComboBox {


    public trainingParametersEditor(int min, int max, boolean emptyEntry) {
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

    public trainingParametersEditor(int min, int max) {
        this(min, max, false);
    }


    public trainingParametersEditor(int min, boolean emptyEntry) {
        this(min, 100, emptyEntry);
    }

    public trainingParametersEditor(int min) {
        this(min, 100, false);
    }
}
