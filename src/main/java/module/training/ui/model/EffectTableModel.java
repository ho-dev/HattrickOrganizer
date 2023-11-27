package module.training.ui.model;

import core.model.HOVerwaltung;
import module.training.TrainWeekEffect;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.table.AbstractTableModel;



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
        HOVerwaltung hoV = HOVerwaltung.instance();
        this.colNames[0] = hoV.getLanguageString("Week"); //$NON-NLS-1$
        this.colNames[1] = hoV.getLanguageString("Season"); //$NON-NLS-1$
        this.colNames[2] = hoV.getLanguageString("TotalTSI"); //$NON-NLS-1$
        this.colNames[3] = hoV.getLanguageString("AverageTSI"); //$NON-NLS-1$
        this.colNames[4] = hoV.getLanguageString("ls.player.tsi") + " +/-"; //$NON-NLS-1$ //$NON-NLS-2$
        this.colNames[5] = hoV.getLanguageString("DurchschnittForm"); //$NON-NLS-1$
        this.colNames[6] = hoV.getLanguageString("ls.player.form") + " +/-"; //$NON-NLS-1$ //$NON-NLS-2$
        this.colNames[7] = hoV.getLanguageString("Skillups"); //$NON-NLS-1$
        this.colNames[8] = hoV.getLanguageString("ls.player.skill"); //$NON-NLS-1$

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
