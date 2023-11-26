// %3903747163:hoplugins.trainingExperience.ui.renderer%
package module.training.ui.renderer;

import core.constants.player.PlayerSkill;
import module.training.Skills;
import module.training.ui.TrainingLegendPanel;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JTable;



/**
 * TableCellRenderer for showing arrows representing the amount of change.
 *
 * @author NetHyperon
 */
public class SkillupTypeTableCellRenderer extends ChangeTableRenderer {
	@Serial
    private static final long serialVersionUID = -8584898772728443298L;

 
    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        try {
            var skill = PlayerSkill.fromInteger((Integer) value);
            setText(skill.getLanguageString());
            setIcon(TrainingLegendPanel.getSkillupTypeIcon(skill, 1));
            setForeground(Skills.getSkillColor(skill));
        } catch (NumberFormatException ignored) {
        }

        return this;
    }
}
