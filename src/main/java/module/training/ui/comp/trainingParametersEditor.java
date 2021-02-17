package module.training.ui.comp;

import javax.swing.JComboBox;


/**
 * ComboBox for editing the training paramters in Training table
 */
public class trainingParametersEditor extends JComboBox {


    public trainingParametersEditor(int min, int max) {
        super();
        if ((min < 0) || (min > 100)) {
        	min = 0;
        }
        
        for (int i = min; i <= max; i++) {
            addItem(i);
        }
    }

    public trainingParametersEditor(int min) {
        this(min, 100);
    }
}
