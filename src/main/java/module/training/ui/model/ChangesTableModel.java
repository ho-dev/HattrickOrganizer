package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.model.TranslationFacility;
import module.training.PlayerSkillChange;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.List;

/**
 * TableModel representing skill changes for individual players.
 * 
 * @author NetHyperon
 */
public class ChangesTableModel extends AbstractTableModel {

	public static final int COL_PLAYER_ID = 6;
	@Serial
	private static final long serialVersionUID = -9082549798814304017L;
	private final List<PlayerSkillChange> values;
	private final String[] colNames = new String[7];

		/**
	 * Creates a new ChangesTableModel object.
	 * 
	 * @param values
	 *            List of values to show in table.
	 */
	public ChangesTableModel(List<PlayerSkillChange> values) {
		super();
		this.colNames[0] = TranslationFacility.tr("Week");
		this.colNames[1] = TranslationFacility.tr("Season");
		this.colNames[2] = TranslationFacility.tr("Spieler");
		this.colNames[3] = TranslationFacility.tr("ls.player.skill");
		this.colNames[4] = TranslationFacility.tr("TO");
		this.colNames[5] = "isOld";
		this.colNames[COL_PLAYER_ID] = "playerId";

		this.values = values;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
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
	@Override
	public int getRowCount() {
		return values.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PlayerSkillChange change = values.get(rowIndex);
		return switch (columnIndex) {
            case 0 -> Integer.toString(change.getSkillChange().getHtWeek());
            case 1 -> Integer.toString(change.getSkillChange().getHtSeason());
            case 2 -> change.getPlayer().getFullName();
            case 3 -> change.getSkillChange().getType().toInt();
            case 4 -> PlayerAbility.getNameForSkill(change.getSkillChange().getValue(), true);
            case 5 -> change.getPlayer().isGoner();
            case COL_PLAYER_ID -> Integer.toString(change.getPlayer().getPlayerId());
            default -> "";
        };
	}
}