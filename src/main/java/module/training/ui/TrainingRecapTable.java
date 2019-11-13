// %1126721452104:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.training.TrainingPreviewPlayers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;


/**
 * A Table with frozen columns
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TrainingRecapTable extends JScrollPane {
    //~ Instance fields ----------------------------------------------------------------------------

    /**
     *
     */
    private static final long serialVersionUID = 7666144967819145973L;
    private JTable fixed;
    private JTable scroll;

    /**
     * Fixed table rendere to add special background colors depending on training speed
     */
    private class FixedTrainingRecapRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 7666144967819145974L;

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            this.setIcon(null);
            this.setToolTipText("");

            if (column == 0) {
                String text = null;
                String tooltip = null;
                Icon icon = null;
                int playerId = Integer.parseInt((String) table.getValueAt(row, table.getColumnCount() - 1));
                Player player = HOVerwaltung.instance().getModel().getSpieler(playerId);

                this.setOpaque(true);
                this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
                this.setText(player.getName());

                tooltip = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getText();
                if (tooltip == null) {
                    tooltip = "";
                }
                this.setToolTipText(tooltip);
                this.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getIcon());
            }
            
            if (isSelected) {
                return this;
            }

            int speed = Integer.parseInt((String) table.getValueAt(row, 2));
            Color bg_color;
            // Speed range is 16 to 125
            if (speed > (125 + 50) / 2) {
                bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
            } else if (speed > (50 + 16) / 2) {
                bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
            } else {
                bg_color = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
            }

            setBackground(bg_color);
            return this;
        }
    }
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TrainingRecapTable object.
     *
     * @param main         Table to show
     * @param fixedColumns number of fixed columns
     */
    public TrainingRecapTable(JTable main, int fixedColumns) {
        super(main);
        scroll = main;

        fixed = new JTable(scroll.getModel());
        fixed.setFocusable(false);
        fixed.setSelectionModel(scroll.getSelectionModel());
        fixed.getTableHeader().setReorderingAllowed(false);
        fixed.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //$NON-NLS-1$
        scroll.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //$NON-NLS-1$

        //  Remove the fixed columns from the main table
        for (int i = 0; i < fixedColumns; i++) {
            TableColumnModel columnModel = scroll.getColumnModel();

            columnModel.removeColumn(columnModel.getColumn(0));
        }

        scroll.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fixed.setSelectionModel(scroll.getSelectionModel());

        //  Remove the non-fixed columns from the fixed table
        while (fixed.getColumnCount() > fixedColumns) {
            TableColumnModel columnModel = fixed.getColumnModel();

            columnModel.removeColumn(columnModel.getColumn(fixedColumns));
        }

        fixed.getColumnModel().getColumn(0).setMaxWidth(150);
        fixed.getColumnModel().getColumn(0).setMinWidth(150);
        fixed.getColumnModel().getColumn(0).setWidth(150);
        fixed.getColumnModel().getColumn(1).setMaxWidth(60);
        fixed.getColumnModel().getColumn(1).setMinWidth(60);
        fixed.getColumnModel().getColumn(1).setWidth(60);
        fixed.getColumnModel().getColumn(2).setMaxWidth(0);
        fixed.getColumnModel().getColumn(2).setMinWidth(0);
        fixed.getColumnModel().getColumn(2).setPreferredWidth(0);
        fixed.getColumnModel().getColumn(3).setMaxWidth(0);
        fixed.getColumnModel().getColumn(3).setMinWidth(0);
        fixed.getColumnModel().getColumn(3).setPreferredWidth(0);

        //  Add the fixed table to the scroll pane
        fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
        setRowHeaderView(fixed);

        fixed.setDefaultRenderer(Object.class, new FixedTrainingRecapRenderer());

        setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, fixed.getTableHeader());
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the Locked LeftTable
     *
     * @return Jtable
     */
    public JTable getLockedTable() {
        return fixed;
    }

    /**
     * Returns the Scrollable RightTable
     *
     * @return Jtable
     */
    public JTable getScrollTable() {
        return scroll;
    }
}
