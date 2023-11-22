package module.lineup;

import core.gui.comp.table.BooleanColumn;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.PlayerColumn;
import core.gui.model.UserColumnController;
import core.gui.model.UserColumnFactory;
import core.model.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class LineupTableModel extends HOTableModel {

	private List<Player> m_vPlayers;

	public LineupTableModel(UserColumnController.ColumnModelId id) {
		super(id, "Aufstellung");
		initialize();
	}

	@Override
	public boolean userCanDisableColumns(){
		return true;
	}

	private void initialize() {
		UserColumn[] basic = UserColumnFactory.createPlayerBasicArray();
		columns = new UserColumn[50];
		columns[0] = basic[0];
		columns[48] = basic[1];

		UserColumn[] skills = UserColumnFactory.createPlayerSkillArray();
		int skillIndex = 9;
		System.arraycopy(skills, 0, columns, skillIndex, skills.length);

		UserColumn[] positions = UserColumnFactory.createPlayerPositionArray();
		int positionIndex = 23;
		System.arraycopy(positions, 0, columns, positionIndex, positions.length);

		UserColumn[] goals = UserColumnFactory.createGoalsColumnsArray();
		int goalsIndex = 42;
		System.arraycopy(goals, 0, columns, goalsIndex, goals.length);

		UserColumn[] add = UserColumnFactory.createPlayerAdditionalArray();
		columns[1] = add[0];
		columns[2] = add[1];
		columns[3] = add[12];
		columns[4] = add[2];
		columns[5] = new BooleanColumn(UserColumnFactory.AUTO_LINEUP, " ", "AutoAufstellung", 28);
		columns[6] = add[4];
		columns[7] = add[5];
		columns[8] = add[6];
		columns[21] = add[3];
		columns[46] = add[7];
		columns[47] = add[8];
		columns[22] = add[9];
		columns[49] = add[10];
	}

	@Override
	public final boolean isCellEditable(int row, int col) {
		return getValueAt(row, col) instanceof Boolean;
	}

	@Override
	public final int getColumnIndexOfDisplayedColumn(int searchId) {
		return super.getColumnIndexOfDisplayedColumn(searchId);
	}

	public final @Nullable Player getPlayer(int id) {
		if (id > 0) {
			for(Player m_vPlayer : m_vPlayers) {
				if (m_vPlayer.getPlayerId() == id) return m_vPlayer;
			}
		}

		return null;
	}

	/**
	 * Init the player list
	 * Disabled players are not applied
	 * @param players current players in team (may include disabled coaches)
	 */
	public final void setValues(List<Player> players) {
		this.m_vPlayers = new ArrayList<>();
		for ( var player : players){
			if (!player.isLineupDisabled()){
				this.m_vPlayers.add(player);
			}
		}
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
			final Player aktuellerPlayer = m_vPlayers.get(i);

			for (int j = 0; j < tmpDisplayedColumns.length; j++) {
				if (tmpDisplayedColumns[j] instanceof PlayerColumn)
					m_clData[i][j] = ((PlayerColumn) tmpDisplayedColumns[j]).getTableEntry(aktuellerPlayer, null);
				if (tmpDisplayedColumns[j] instanceof BooleanColumn)
					m_clData[i][j] = ((BooleanColumn) tmpDisplayedColumns[j]).getValue(aktuellerPlayer);
			}
		}
	}

	/**
	 * Triggered by changes in lineup
	 */
	public final void reInitData() {
		UserColumn[] tmpDisplayedColumns = getDisplayedColumns();
		for (int i = 0; i < m_vPlayers.size(); i++) {
			final Player currentPlayer = m_vPlayers.get(i);

			for (int j = 0; j < tmpDisplayedColumns.length; j++) {
				if (tmpDisplayedColumns[j].getId() == UserColumnFactory.NAME
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.LINEUP
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.BEST_POSITION
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.GROUP) {
					m_clData[i][j] = ((PlayerColumn) tmpDisplayedColumns[j]).getTableEntry(currentPlayer, null);
				} else if (tmpDisplayedColumns[j].getId() == UserColumnFactory.AUTO_LINEUP) {
					m_clData[i][j] = ((BooleanColumn) tmpDisplayedColumns[j]).getValue(currentPlayer);
				}
			}
		}
	}
}
