package core.gui.model;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.RatingTableEntry;
import core.gui.theme.ImageUtilities;
import core.model.TranslationFacility;
import core.model.player.MatchRoleID;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class SpielerPositionTableModel extends AbstractTableModel {

    public String[] m_sToolTipStrings = {
            TranslationFacility.tr("Position"),
            //Maximal
            TranslationFacility.tr("Maximal"),
            //Minimal
            TranslationFacility.tr("Minimal"),
            //Durchschnitt
            TranslationFacility.tr("Durchschnitt"),
    };

    protected Object[][] m_clData;

    protected String[] m_sColumnNames = {
            TranslationFacility.tr("Position"),


            //Maximal
            TranslationFacility.tr("Maximal"),


            //Minimal
            TranslationFacility.tr("Minimal"),


            //Durchschnitt
            TranslationFacility.tr("Durchschnitt"),
    };
    private Vector<float[]> m_playersEvaluation;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerPositionTableModel object.
     */
    public SpielerPositionTableModel(Vector<float[]> playersEvaluation) {
        m_playersEvaluation = playersEvaluation;
        initData();
    }

    //~ Methods ------------------------------------------------------------------------------------

    @Override
    public final boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public final Class<?> getColumnClass(int columnIndex) {
        final Object obj = getValueAt(0, columnIndex);

        if (obj != null) {
            return obj.getClass();
        }

        return "".getClass();
    }

    //-----Zugriffsmethoden----------------------------------------        
    public final int getColumnCount() {
        return m_sColumnNames.length;
    }

    @Override
    public final String getColumnName(int columnIndex) {
        if ((m_sColumnNames != null) && (m_sColumnNames.length > columnIndex)) {
            return m_sColumnNames[columnIndex];
        }

        return null;
    }

    public final int getRowCount() {
        return (m_clData != null) ? m_clData.length : 0;
    }

    public final Object getValue(int row, String columnName) {
        if ((m_sColumnNames != null) && (m_clData != null)) {
            int i = 0;

            while ((i < m_sColumnNames.length) && !m_sColumnNames[i].equals(columnName)) {
                i++;
            }

            return m_clData[row][i];
        }

        return null;
    }

    @Override
    public final void setValueAt(Object value, int row, int column) {
        m_clData[row][column] = value;
    }

    public final Object getValueAt(int row, int column) {
        if (m_clData != null) {
            return m_clData[row][column];
        }

        return null;
    }

    /**
     * Player neu setzen
     */
    public final void setValues(Vector<float[]> spielerbewertung) {
        m_playersEvaluation = spielerbewertung;
        initData();
    }

    //-----initialisierung-----------------------------------------

    /**
     * Creates a Data[][] from the playerVvector
     */
    private void initData() {
        m_clData = new Object[m_playersEvaluation.size()][m_sColumnNames.length];

        for (int i = 0; i < m_playersEvaluation.size(); i++) {
            //First the position, then max, min, average stars
            final float[] rating = m_playersEvaluation.get(i);

            //Position
            m_clData[i][0] = new ColorLabelEntry(
                    ImageUtilities.getJerseyIcon(MatchRoleID.getHTPosidForHOPosition4Image((byte) rating[3]), (byte) 0, 0),
                    -MatchRoleID.getSortId((byte) rating[3], false),
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            ((ColorLabelEntry) m_clData[i][0]).setText(MatchRoleID.getNameForPosition((byte) rating[3]));

            //Maximal
            m_clData[i][1] = new RatingTableEntry((int) (rating[0] * 2));

            //Minial
            m_clData[i][2] = new RatingTableEntry((int) (rating[1] * 2));

            //Durchschnitt
            m_clData[i][3] = new RatingTableEntry(Math.round(rating[2] * 2));
        }
    }
}