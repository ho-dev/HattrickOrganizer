package module.lineup;

import core.gui.comp.table.BooleanColumn;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.PlayerColumn;
import core.gui.model.UserColumnFactory;
import core.model.player.Player;

import java.util.List;


public class LineupTableModel extends HOTableModel {

	private static final long serialVersionUID = 6706783648812506363L;

	private List<Player> m_vPlayers;

	public LineupTableModel(int id) {
		super(id, "Aufstellung");
		initialize();
	}

	private void initialize() {
		UserColumn[] basic = UserColumnFactory.createPlayerBasicArray();
		columns = new UserColumn[49];
		columns[0] = basic[0];
		columns[48] = basic[1];

		UserColumn[] skills = UserColumnFactory.createPlayerSkillArray();
		int skillIndex = 9; // - 20
		for (int i = 0; i < skills.length; i++) {
			columns[skillIndex+i] = skills[i];
		}

		UserColumn[] positions = UserColumnFactory.createPlayerPositionArray();
		int positionIndex = 23;//- 41
		for (int i = 0; i < positions.length; i++) {
			columns[positionIndex+i] = positions[i];
		}

		UserColumn[] goals = UserColumnFactory.createGoalsColumnsArray();
		int goalsIndex = 42;//-45
		for (int i = 0; i < goals.length; i++) {
			columns[goalsIndex+i] = goals[i];
		}

		UserColumn[] add = UserColumnFactory.createPlayerAdditionalArray();
		columns[1] = add[0];
		columns[2] = add[1];
		columns[3] = add[11];
		columns[4] = add[2];
		columns[5] = new BooleanColumn(UserColumnFactory.AUTO_LINEUP, " ", "AutoAufstellung", 28);
		columns[6] = add[4];
		columns[7] = add[5];
		columns[8] = add[6];
		columns[21] = add[3];
		columns[46] = add[7];
		columns[47] = add[8];
		columns[22] = add[9]; // lastmatch
	}

	@Override
	public final boolean isCellEditable(int row, int col) {
		return getValueAt(row, col) instanceof Boolean;
	}

	@Override
	public final int getColumnIndexOfDisplayedColumn(int searchId) {
		return super.getColumnIndexOfDisplayedColumn(searchId);
	}

	public final Player getSpieler(int id) {
		if (id > 0) {
			for (int i = 0; i < m_vPlayers.size(); i++) {
				if (((Player) m_vPlayers.get(i)).getSpielerID() == id) {
					return (Player) m_vPlayers.get(i);
				}
			}
		}

		return null;
	}

	/**
	 * Player neu setzen
	 */
	public final void setValues(List<Player> player) {
		m_vPlayers = player;
		initData();
	}

	/**
	 * Fügt der Tabelle einen Player hinzu
	 */
	public final void addSpieler(Player player, int index) {
		m_vPlayers.add(index, player);
		initData();
	}

	/**
	 * create a data[][] from player-Vector
	 */
	@Override
	protected void initData() {
		UserColumn[] tmpDisplayedColumns = getDisplayedColumns();
		m_clData = new Object[m_vPlayers.size()][tmpDisplayedColumns.length];

		for (int i = 0; i < m_vPlayers.size(); i++) {
			final Player aktuellerPlayer = (Player) m_vPlayers.get(i);

			for (int j = 0; j < tmpDisplayedColumns.length; j++) {
				if (tmpDisplayedColumns[j] instanceof PlayerColumn)
					m_clData[i][j] = ((PlayerColumn) tmpDisplayedColumns[j]).getTableEntry(
							aktuellerPlayer, null);
				if (tmpDisplayedColumns[j] instanceof BooleanColumn)
					m_clData[i][j] = ((BooleanColumn) tmpDisplayedColumns[j])
							.getValue(aktuellerPlayer);
			}
		}
	}

	/**
	 * Passt nur die Aufstellung an
	 */
	public final void reInitData() {
		UserColumn[] tmpDisplayedColumns = getDisplayedColumns();
		for (int i = 0; i < m_vPlayers.size(); i++) {
			final Player aktuellerPlayer = m_vPlayers.get(i);

			for (int j = 0; j < tmpDisplayedColumns.length; j++) {
				if (tmpDisplayedColumns[j].getId() == UserColumnFactory.NAME
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.LINUP
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.BEST_POSITION
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.GROUP) {
					m_clData[i][j] = ((PlayerColumn) tmpDisplayedColumns[j]).getTableEntry(aktuellerPlayer, null);
				} else if (tmpDisplayedColumns[j].getId() == UserColumnFactory.AUTO_LINEUP) {
					m_clData[i][j] = aktuellerPlayer.getCanBeSelectedByAssistant();
				}
			}
		}
	}
}
