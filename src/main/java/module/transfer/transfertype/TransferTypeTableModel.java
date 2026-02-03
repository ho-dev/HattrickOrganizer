package module.transfer.transfertype;

import core.db.DBManager;
import core.model.TranslationFacility;
import module.transfer.TransferType;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.List;



/**
 * TableModel representing the effect of training.
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class TransferTypeTableModel extends AbstractTableModel {
    //~ Instance fields ----------------------------------------------------------------------------

    private static final int COLUMN_INDEX_PLAYER_ID = 0;
    private static final int COLUMN_INDEX_PLAYER_NAME = 1;
    private static final int COLUMN_INDEX_TRANSFER_TYPE = 2;
    private static final int COLUMN_INDEX_PLAYER_INCOME = 3;

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

        this.colNames[COLUMN_INDEX_PLAYER_ID] = TranslationFacility.tr("ls.player.id");
        this.colNames[COLUMN_INDEX_PLAYER_NAME] = TranslationFacility.tr("ls.player.name");
        this.colNames[COLUMN_INDEX_TRANSFER_TYPE] = TranslationFacility.tr("Type");
        this.colNames[COLUMN_INDEX_PLAYER_INCOME] = TranslationFacility.tr("Income");

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
        return column == COLUMN_INDEX_TRANSFER_TYPE;
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
        if (col == COLUMN_INDEX_TRANSFER_TYPE) {
            final String type = value.toString();
            final Object id = getValueAt(row, COLUMN_INDEX_PLAYER_ID);

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
            case COLUMN_INDEX_PLAYER_ID -> transfer.getPlayerId();
            case COLUMN_INDEX_PLAYER_NAME -> transfer.getPlayerName();
            case COLUMN_INDEX_TRANSFER_TYPE -> TransferType.getTransferDesc(transfer.getTransferType());
            case COLUMN_INDEX_PLAYER_INCOME -> transfer.getIncome().toLocaleString();
            default -> ""; //$NON-NLS-1$
        };
    }
}
