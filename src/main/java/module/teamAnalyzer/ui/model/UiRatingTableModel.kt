// %415978404:hoplugins.teamAnalyzer.ui.model%
/*
 * UiFilterTableModel.java
 *
 * Created on 20 settembre 2004, 16.17
 */
package module.teamAnalyzer.ui.model;

import core.gui.model.BaseTableModel;

import java.util.Vector;

import javax.swing.ImageIcon;



/**
 * Custom RatingTable model
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class UiRatingTableModel extends BaseTableModel {
	private static final long serialVersionUID = -825185956672889047L;

	//~ Constructors -------------------------------------------------------------------------------


	/**
     * Creates a new UiRatingTableModel object.
     *
     * @param vector Vector of table data
     * @param vector2 Vector of column names
     */
    public UiRatingTableModel(Vector<Vector<Object>> vector, Vector<String> vector2) {
        super(vector, vector2);
    }

    /**
     * Creates a new instance of UiFilterTableModel
     */
    public UiRatingTableModel() {
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
        if (column == 2) {
            return ImageIcon.class;
        }

        return super.getColumnClass(column);
    }
}
