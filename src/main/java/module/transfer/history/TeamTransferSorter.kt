// %1126721330698:hoplugins.transfers.ui%
package module.transfer.history;


import module.transfer.ui.sorter.DefaultTableSorter;

import java.util.Comparator;

import javax.swing.table.TableModel;


/**
 * Sorter for the team transfer table.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
class TeamTransferSorter extends DefaultTableSorter {
    //~ Constructors -------------------------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = -8930143133882696457L;

	/**
     * Create a TransferTypeSorter
     *
     * @param model Table model to sort.
     */
    TeamTransferSorter(TableModel model) {
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
        if ((column == 1) || (column == 2) || (column >= 6)) {
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
