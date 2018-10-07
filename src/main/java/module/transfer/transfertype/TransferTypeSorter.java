// %1126721330854:hoplugins.transfers.ui%
package module.transfer.transfertype;


import module.transfer.ui.sorter.DefaultTableSorter;

import java.util.Comparator;

import javax.swing.table.TableModel;


/**
 * Sorter for the transfer type table.
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class TransferTypeSorter extends DefaultTableSorter {
    //~ Constructors -------------------------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = -4831786347484081536L;

	/**
     * Create a TransferTypeSorter
     *
     * @param model Table model to sort.
     */
    TransferTypeSorter(TableModel model) {
        super(model);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Method that define Custom Comparator
     *
     * @param column column to sort
     *
     * @return A custom comparator if any, null if not specified
     */
    @Override
	public final Comparator<Integer> getCustomComparator(int column) {
        if ((column == 0) || (column == 3)) {
            return new Comparator<Integer>() {
                    @Override
					public boolean equals(Object arg0) {
                        return false;
                    }

                    public int compare(Integer arg0, Integer arg1) {
                        final Integer d1 = arg0;
                        final Integer d2 = arg1;
                        return d1.compareTo(d2);
                    }
                };
        }

        return null;
    }
}
