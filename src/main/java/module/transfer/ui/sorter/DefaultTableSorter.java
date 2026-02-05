package module.transfer.ui.sorter;

import java.util.Comparator;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;


/**
 * Default Table sorter
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class DefaultTableSorter extends AbstractTableSorter {
    //~ Constructors -------------------------------------------------------------------------------

	/**
     * Creates a new DefaultTableSorter object.
     */
    public DefaultTableSorter() {
        super();
    }

    /**
     * Creates a new DefaultTableSorter object.
     *
     * @param tableModel
     */
    public DefaultTableSorter(TableModel tableModel) {
        super(tableModel);
    }

    /**
     * Creates a new DefaultTableSorter object.
     *
     * @param tableModel
     * @param tableHeader
     */
    public DefaultTableSorter(TableModel tableModel, JTableHeader tableHeader) {
        super(tableModel, tableHeader);
    }

    //~ Methods ------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
	@Override
	public Comparator getCustomComparator(int column) {
        return null;
    }

    @Override
	public boolean hasHeaderLine() {
        return false;
    }

    @Override
	public int minSortableColumn() {
        return 0;
    }
}
