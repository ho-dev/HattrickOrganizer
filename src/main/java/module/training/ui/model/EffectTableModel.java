package module.training.ui.model;

import core.model.TranslationFacility;
import module.training.TrainWeekEffect;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.List;



/**
 * TableModel representing the effect of training.
 *
 * @author NetHyperon
 */
public class EffectTableModel extends AbstractTableModel {
    //~ Static fields/initializers -----------------------------------------------------------------

    /**
	 *
	 */
	@Serial
    private static final long serialVersionUID = 6647124384624067021L;

    private static final NumberFormat FORMATTER = NumberFormat.getInstance();

    //~ Instance fields ----------------------------------------------------------------------------

    private final List<TrainWeekEffect> values;
    private final String[] colNames = new String[9];

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new EffectTableModel object.
     *
     * @param values List of values to show in table.
     */
    public EffectTableModel(List<TrainWeekEffect> values) {
        super();
        FORMATTER.setMaximumFractionDigits(2);
        FORMATTER.setMinimumFractionDigits(2);
        this.colNames[0] = TranslationFacility.tr("Week"); //$NON-NLS-1$
        this.colNames[1] = TranslationFacility.tr("Season"); //$NON-NLS-1$
        this.colNames[2] = TranslationFacility.tr("TotalTSI"); //$NON-NLS-1$
        this.colNames[3] = TranslationFacility.tr("AverageTSI"); //$NON-NLS-1$
        this.colNames[4] = TranslationFacility.tr("ls.player.tsi") + " +/-"; //$NON-NLS-1$ //$NON-NLS-2$
        this.colNames[5] = TranslationFacility.tr("DurchschnittForm"); //$NON-NLS-1$
        this.colNames[6] = TranslationFacility.tr("ls.player.form") + " +/-"; //$NON-NLS-1$ //$NON-NLS-2$
        this.colNames[7] = TranslationFacility.tr("Skillups"); //$NON-NLS-1$
        this.colNames[8] = TranslationFacility.tr("ls.player.skill"); //$NON-NLS-1$

        this.values = values;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return colNames.length;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
	public String getColumnName(int column) {
        return colNames[column];
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return values.size();
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        TrainWeekEffect effect = values.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> Integer.toString(effect.getHattrickWeek());
            case 1 -> Integer.toString(effect.getHattrickSeason());
            case 2 -> Integer.toString(effect.getTotalTSI());
            case 3 -> Integer.toString(effect.getAverageTSI());
            case 4 -> "+" + effect.getTSIIncrease() + " / " //$NON-NLS-1$ //$NON-NLS-2$
                    + effect.getTSIDecrease();
            case 5 -> FORMATTER.format(effect.getAverageForm());
            case 6 -> "+" + effect.getFormIncrease() + " / " //$NON-NLS-1$ //$NON-NLS-2$
                    + effect.getFormDecrease();
            case 7 -> Integer.toString(effect.getAmountSkillups());
            case 8 -> effect.getTrainingType();
            default -> ""; //$NON-NLS-1$
        };
    }
}
