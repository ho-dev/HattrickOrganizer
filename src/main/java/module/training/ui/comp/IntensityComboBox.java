// %1126721451229:hoplugins.trainingExperience.ui.component%
package module.training.ui.comp;

import javax.swing.JComboBox;


/**
 * ComboBox for editing the Training intensity
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class IntensityComboBox extends JComboBox {
    //~ Constructors -------------------------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = -4612244092459687360L;

	/**
	 * Creates a new IntensityComboBox object.
	 * 
	 * @param min The minimum value for the list. Between 0 and 100.
	 */
    public IntensityComboBox(int min) {
        super();
        if ((min < 0) || (min > 100)) {
        	min = 0;
        }
        
        for (int i = min; i <= 100; i++) {
            addItem(new Integer(i));
        }
    }
}
