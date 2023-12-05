package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.model.player.SkillChange;

import java.io.Serial;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class SkillupTableModel extends AbstractTableModel {

	@Serial
	private static final long serialVersionUID = 1636458081835657412L;
	private List<SkillChange> data;

	private Player player;

	public void setData(Player player, List<SkillChange> data) {
		this.player = player;
		this.data = data;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return (this.data != null) ? data.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SkillChange skillup = this.data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> skillup.getType().getLanguageString() + ": "
                    + PlayerAbility.getNameForSkill(skillup.getValue(), true);
            case 1 -> skillup.getHtWeek();
            case 2 -> skillup.getHtSeason();
            case 3 -> player.getAgeAtDate(skillup.getDate()).toString();
            default -> null;
        };
	}

	@Override
	public String getColumnName(int column) {
        return switch (column) {
            case 0 -> HOVerwaltung.instance().getLanguageString("ls.player.skill");
            case 1 -> HOVerwaltung.instance().getLanguageString("Week");
            case 2 -> HOVerwaltung.instance().getLanguageString("Season");
            case 3 -> HOVerwaltung.instance().getLanguageString("ls.player.age");
            default -> "";
        };
	}

	public SkillChange getSkillup(int row) {
		return this.data.get(row);
	}
}