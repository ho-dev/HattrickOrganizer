// %1126721451026:hoplugins.trainingExperience.ui.bar%
package module.training.ui.comp;

import java.awt.Color;


/**
 * Color Class for StateBar
 *
 * @author Volker
 */
public class ColorModus {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Dark Color */
    public Color dunkel;

    /** Light Color */
    public Color hell;

    /** Normal Color */
    public Color mittel;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ColorModus object.
     *
     * @param color The Base color for the bar
     */
    public ColorModus(Color color) {
        hell = color.brighter();
        mittel = color;
        dunkel = color.darker();
    }
}
