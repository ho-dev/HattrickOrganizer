// %2328098351:hoplugins.teamAnalyzer.ui.model%
package module.teamAnalyzer.ui.model;

import core.gui.model.BaseTableModel;

import java.util.Vector;

import javax.swing.ImageIcon;



/**
 * Custom RatingTable model
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class UiRecapTableModel extends BaseTableModel {
    //~ Constructors -------------------------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = -5858488972549437663L;

	/**
     * Creates a new UiRecapTableModel object.
     *
     * @param vector Vector of table data
     * @param vector2 Vector of column names
     */
    public UiRecapTableModel(Vector<Vector<Object>> vector, Vector<String> vector2) {
        super(vector, vector2);
    }

    /**
     * Creates a new instance of UiFilterTableModel
     */
    public UiRecapTableModel() {
        super();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the column class type
     *
     * @param column
     *
     * @return
     */
    @Override
	public Class<?> getColumnClass(int column) {
        if (column == 1) {
            return ImageIcon.class;
        }

        return super.getColumnClass(column);
    }
}
