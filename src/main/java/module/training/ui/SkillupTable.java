// %827897234:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.model.player.ISkillup;
import module.training.ui.model.SkillupTableModel;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * Table for players past and future skillups
 * 
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class SkillupTable extends JTable {

	private static final long serialVersionUID = 8692544698980743170L;

	/**
	 * Creates a new SkillupTable object.
	 * 
	 * @param tableModel
	 *            The table model to be used
	 */
	public SkillupTable(TableModel tableModel) {
		super(tableModel);
	}

	/**
	 * Return string toolTip for the skillup
	 * 
	 * @param e
	 *            MouseEvent of being over the cell
	 * 
	 * @return String toolTip for active skillup
	 */
	@Override
	public String getToolTipText(MouseEvent e) {
		SkillupTableModel model = (SkillupTableModel) getModel();
		ISkillup skillup = model.getSkillup(convertRowIndexToModel(rowAtPoint(e.getPoint())));

		if (skillup != null && skillup.getTrainType() == ISkillup.SKILLUP_REAL) {
			return java.text.DateFormat.getDateTimeInstance().format(skillup.getDate());
		}

		return "";
	}
}
