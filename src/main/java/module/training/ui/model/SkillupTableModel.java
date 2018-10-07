package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.ISkillup;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SkillupTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1636458081835657412L;
	private List<ISkillup> data;

	public void setData(List<ISkillup> data) {
		this.data = data;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return (this.data != null) ? data.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ISkillup skillup = this.data.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return PlayerSkill.toString(skillup.getType()) + ": "
					+ PlayerAbility.getNameForSkill(skillup.getValue(), true);
		case 1:
			return skillup.getHtWeek();
		case 2:
			return skillup.getHtSeason();
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return HOVerwaltung.instance().getLanguageString("ls.team.trainingtype");
		case 1:
			return HOVerwaltung.instance().getLanguageString("Week");
		case 2:
			return HOVerwaltung.instance().getLanguageString("Season");
		default:
			return "";
		}
	}

	public ISkillup getSkillup(int row) {
		return this.data.get(row);
	}
}