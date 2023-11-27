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
public class ChangeTableRenderer extends DefaultTableCellRenderer {
    //~ Static fields/initializers -----------------------------------------------------------------

	@Serial
    private static final long serialVersionUID = -8664774318961127994L;

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 3) {
            try {
                var skillID = (Integer)table.getValueAt(row, 3);
                var skill = PlayerSkill.fromInteger(skillID);
                setText(skill.getLanguageString());
                setIcon(TrainingLegendPanel.getSkillupTypeIcon(skill, 1));
            } catch (NumberFormatException ignored) {
            }
        }
        return this;
    }
}
