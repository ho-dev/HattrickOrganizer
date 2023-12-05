// %2597556203:hoplugins.trainingExperience.ui.model%
package module.training.ui.model;

import core.constants.player.PlayerAbility;
import core.model.HOVerwaltung;
import module.training.PlayerSkillChange;

import java.io.Serial;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * TableModel representing skill changes for individual players.
 * 
 * @author NetHyperon
 */
public class ChangesTableModel extends AbstractTableModel {

	public final static int COL_PLAYER_ID = 6;
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
		HOVerwaltung hoV = HOVerwaltung.instance();
		this.colNames[0] = hoV.getLanguageString("Week");
		this.colNames[1] = hoV.getLanguageString("Season");
		this.colNames[2] = hoV.getLanguageString("Spieler");
		this.colNames[3] = hoV.getLanguageString("ls.player.skill");
		this.colNames[4] = hoV.getLanguageString("TO");
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