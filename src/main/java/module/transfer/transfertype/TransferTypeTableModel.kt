// %1126721330541:hoplugins.transfers.ui.model%
package module.transfer.transfertype;

import core.db.DBManager;
import core.model.HOVerwaltung;
import module.transfer.TransferType;

import java.io.Serial;
import java.util.List;

import javax.swing.table.AbstractTableModel;



/**
 * TableModel representing the effect of training.
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class TransferTypeTableModel extends AbstractTableModel {
    //~ Instance fields ----------------------------------------------------------------------------

    /**
	 *
	 */
	@Serial
    private static final long serialVersionUID = 2943508984461781906L;
	private final List<TransferredPlayer> values;
    private final String[] colNames = new String[4];

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a TransferTypeTableModel.
     *
     * @param values List of values to show in table.
     */
    TransferTypeTableModel(List<TransferredPlayer> values) {
        super();

        this.colNames[0] = HOVerwaltung.instance().getLanguageString("ls.player.id");
        this.colNames[1] = HOVerwaltung.instance().getLanguageString("ls.player.name");
        this.colNames[2] = HOVerwaltung.instance().getLanguageString("Type");
        this.colNames[3] = HOVerwaltung.instance().getLanguageString("Income");

        //this.colNames[2] = HOVerwaltung.instance().getLanguageString("Week"); //$NON-NLS-1$
        this.values = values;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Method that returns if a cell is editable or not
     *
     * @param row Row number
     * @param column Column number
     *
     * @return <code>true</code> is cell is editable, <code>false</code> if not.
     */
    @Override
	public final boolean isCellEditable(int row, int column) {
        return column == 2;
    }

    /** {@inheritDoc} */
    public final int getColumnCount() {
        return colNames.length;
    }

    /** {@inheritDoc} */
    @Override
	public final String getColumnName(int column) {
        return colNames[column];
    }

    /** {@inheritDoc} */
    public final int getRowCount() {
        return values.size();
    }

    /**
     * When a value is updated, update the value in the right TrainingWeek in p_V_trainingsVector
     * store the change thru HO API. Refresh the main table, and deselect any player
     *
     * @param value Value to set
     * @param row Row number
     * @param col Column number
     */
    @Override
	public final void setValueAt(Object value, int row, int col) {
        if (col == 2) {
            final String type = value.toString();
            final Object id = getValueAt(row, 0);

            try {
                var transferType = new TransferType();
                transferType.setPlayerId(Integer.parseInt("" + id));
                transferType.setTransferType(TransferType.getTransferCode(type));
                DBManager.instance().setTransferType(transferType);
            } catch (Exception e) {
                // DO Nothing
            }
        }

        fireTableCellUpdated(row, col);
    }

    /** {@inheritDoc} */
    public final Object getValueAt(int rowIndex, int columnIndex) {
        final TransferredPlayer transfer = values.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> transfer.getPlayerId();
            case 1 -> transfer.getPlayerName();
            case 2 -> TransferType.getTransferDesc(transfer.getTransferType());
            case 3 -> transfer.getIncome();
            default -> ""; //$NON-NLS-1$
        };
    }
}
