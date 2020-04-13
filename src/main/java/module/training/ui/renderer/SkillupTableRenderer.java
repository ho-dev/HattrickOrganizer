// %169758679:hoplugins.trainingExperience.ui.renderer%
/*
 * Created on 14-mar-2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package module.training.ui.renderer;

import core.model.player.ISkillChange;
import module.training.Skills;
import module.training.ui.model.SkillupTableModel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SkillupTableRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 4941016016981672099L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax
	 * .swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);

		SkillupTableModel model = (SkillupTableModel)table.getModel();
		ISkillChange skillup = model.getSkillup(table.convertRowIndexToModel(row));
		if (skillup.getTrainType() == ISkillChange.SKILLUP_FUTURE) {
			cell.setForeground(Skills.getSkillColor(skillup.getType()));
		} else {
			cell.setForeground(Color.BLACK);
		}

		return cell;
	}
}
