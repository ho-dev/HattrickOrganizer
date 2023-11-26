// %292731377:hoplugins.trainingExperience.ui.renderer%
package module.training.ui.renderer;

import core.constants.player.PlayerSkill;
import module.training.ui.TrainingLegendPanel;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * TableCellRenderer for showing arrows representing the amount of change.
 *
 * @author NetHyperon
 */
public class SkillupsTableCellRenderer extends DefaultTableCellRenderer {
    //~ Methods ------------------------------------------------------------------------------------

    /**
	 * 
	 */
	@Serial
    private static final long serialVersionUID = 2836512590615874682L;

	/**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        try {
            int count = Integer.parseInt((String) value);
            var skill = (PlayerSkill) table.getValueAt(row, 8);
            setText(null);
            setIcon(TrainingLegendPanel.getSkillupTypeIcon(skill, count));
        } catch (NumberFormatException ignored) {
        }

        return this;
    }
}
