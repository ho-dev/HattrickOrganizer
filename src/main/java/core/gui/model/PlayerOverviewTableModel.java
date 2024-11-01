package core.gui.model;

import core.db.DBManager;
import core.gui.comp.table.BooleanColumn;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.model.player.Player;
import core.util.HODateTime;
import module.playerOverview.SpielerTrainingsVergleichsPanel;

import java.io.Serial;
import java.util.List;


/**
 * Model used to display players in the Squad table.
 *
 * @author Thorsten Dietz
 * @since 1.36
 */
public class PlayerOverviewTableModel extends HOTableModel {

	@Serial
	private static final long serialVersionUID = 5149408240369536138L;

	/** all players **/
	private List<Player> m_vPlayers;

	/**
	 * constructor
	 *
	 */
	PlayerOverviewTableModel(UserColumnController.ColumnModelId id){
		this(id,"Spieleruebersicht");
	}

	protected PlayerOverviewTableModel(UserColumnController.ColumnModelId id, String name){
		super(id, name);
		initialize();
	}

	/**
	 * initialize all columns.
	 */
	private void initialize() {
		UserColumn[] basic = UserColumnFactory.createPlayerBasicArray();
		columns = new UserColumn[64];
		columns[0] = basic[0];
		columns[48] = basic[1];

		UserColumn[] skills =  UserColumnFactory.createPlayerSkillArray();
		int skillIndex = 9; // - 20
		System.arraycopy(skills, 0, columns, skillIndex, skills.length);

		UserColumn[] positions =  UserColumnFactory.createPlayerPositionArray();
		int positionIndex = 23;//- 41
		System.arraycopy(positions, 0, columns, positionIndex, positions.length);

		UserColumn[] goals =  UserColumnFactory.createGoalsColumnsArray();
		int goalsIndex = 42;//-46
		System.arraycopy(goals, 0, columns, goalsIndex, goals.length);
		UserColumn[] additionalArray = UserColumnFactory.createPlayerAdditionalArray();
		columns[1] = additionalArray[0];
		columns[2] = additionalArray[1];
		columns[4] = additionalArray[2];
		columns[21] = additionalArray[3]; // best position
		columns[5] = additionalArray[4];
		columns[6] = additionalArray[5];
		columns[7] = additionalArray[6];
		columns[58] = additionalArray[7];
		columns[8] = additionalArray[8];// tsi
		columns[22] = additionalArray[9]; // lastmatch
		columns[47] = additionalArray[11];
		columns[3] = additionalArray[12];// Motherclub
		columns[49] = additionalArray[10];
		columns[50] = additionalArray[16];
		columns[51] = additionalArray[17];
		columns[52] = additionalArray[18];
		columns[53] = additionalArray[13];
		columns[54] = additionalArray[14];
		columns[55] = additionalArray[15];
		columns[56] = additionalArray[19];
		columns[57] = additionalArray[20];
		columns[59] = additionalArray[21];
		columns[60] = additionalArray[22];
		columns[61] = additionalArray[23]; // schum-rank
		columns[62] = additionalArray[24]; // schum-rank benchmark
		columns[63] = new BooleanColumn(UserColumnFactory.AUTO_LINEUP, " ", "AutoAufstellung", 28);
	}

	@Override
	public final boolean isCellEditable(int row, int col) {
		return getValueAt(row, col) instanceof Boolean;
	}

	public int getRowIndexOfPlayer(int playerId){
		var modelIndex = getPlayerIndex(playerId);
		if (modelIndex > -1){
			return this.table.convertRowIndexToView(modelIndex);
		}
		return -1;
	}

	public Player getPlayerAtRow(int tableRow) {
		if (tableRow > -1 ) return m_vPlayers.get(this.table.convertRowIndexToModel(tableRow));
		return null;
	}

    public Player getPlayer(int playerId) {
        // Can be negative for temp player
        if (playerId != 0) {
			for (Player m_vPlayer : m_vPlayers) {
				if (m_vPlayer.getPlayerId() == playerId) {
					return m_vPlayer;
				}
			}
        }

        return null;
    }

	public int getPlayerIndex(int playerId){
		int i = 0;
		for (Player m_vPlayer : m_vPlayers) {
			if (m_vPlayer.getPlayerId() == playerId) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public List<Player> getPlayers() {
		return m_vPlayers;
	}

    /**
     * Sets the new list of players.
     */
    public void setValues(List<Player> player) {
    	m_vPlayers = player;
        initData();
    }

    /**
     * Resets the data for an HRF comparison.
     */
    public void reInitDataHRFComparison() {
        initData();
    }


    /**
     * Returns the {@link Player} with the same ID as the instance passed, or <code>null</code>.
     */
    private Player getPreviousPlayerDevelopmentStage(Player currentDevelopmentStage) {
        final int id = currentDevelopmentStage.getPlayerId();

		List<Player> selectedPlayerDevelopmentStage = SpielerTrainingsVergleichsPanel.getSelectedPlayerDevelopmentStage();
		for (int i = 0; (selectedPlayerDevelopmentStage != null)  && (i < selectedPlayerDevelopmentStage.size()); i++) {
            final Player selectedDevelopmentStage = selectedPlayerDevelopmentStage.get(i);

            if (selectedDevelopmentStage.getPlayerId() == id) {
                return selectedDevelopmentStage;
            }
        }

		if (SpielerTrainingsVergleichsPanel.isDevelopmentStageSelected()) {
			var hrf = SpielerTrainingsVergleichsPanel.getSelectedHrfId();
            return getFirstPlayerDevelopmentStageAfterSelected(currentDevelopmentStage, hrf);
        }
        return null;
    }

    /**
     * Returns the {@link Player} from the first HRF in which he appears.
     */
    private Player getFirstPlayerDevelopmentStageAfterSelected(Player vorlage, Integer hrfId) {
		HODateTime after = null;
		if (hrfId != null) {
			var hrf = DBManager.instance().loadHRF(hrfId);
			if (hrf != null) {
				after = hrf.getDatum();
			}
		}
		return core.db.DBManager.instance().loadPlayerFirstHRF(vorlage.getPlayerId(), after);
	}

    /**
     * create a data[][] from player-Vector
     */
    @Override
	protected void initData() {
		UserColumn[] tmpDisplayedColumns = getDisplayedColumns();
		m_clData = new Object[m_vPlayers.size()][tmpDisplayedColumns.length];

		for (int i = 0; i < m_vPlayers.size(); i++) {
			final Player currentPlayer = m_vPlayers.get(i);
			final Player comparisonPlayer = getPreviousPlayerDevelopmentStage(currentPlayer);
			for (int j = 0; j < tmpDisplayedColumns.length; j++) {
				if (tmpDisplayedColumns[j] instanceof PlayerColumn) {
					m_clData[i][j] = ((PlayerColumn) tmpDisplayedColumns[j]).getTableEntry(currentPlayer, comparisonPlayer);
				} else if (tmpDisplayedColumns[j] instanceof BooleanColumn) {
					m_clData[i][j] = ((BooleanColumn) tmpDisplayedColumns[j]).getValue(currentPlayer);
				}
			}
		}
	}

    /**
     * Initializes the lineup only
     */
    public void reInitData() {
    	UserColumn [] tmpDisplayedColumns = getDisplayedColumns();
        for (int i = 0; i < m_vPlayers.size(); i++) {
            final Player currentPlayer = m_vPlayers.get(i);

            for (int j = 0; j < tmpDisplayedColumns.length; j++) {
				if(tmpDisplayedColumns[j].getId() == UserColumnFactory.NAME
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.LINEUP
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.BEST_POSITION
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.SCHUM_RANK_BENCHMARK
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.GROUP){
					m_clData[i][j] = ((PlayerColumn)tmpDisplayedColumns[j]).getTableEntry(currentPlayer,null);
				} else if (tmpDisplayedColumns[j].getId() == UserColumnFactory.AUTO_LINEUP) {
					m_clData[i][j] = ((BooleanColumn) tmpDisplayedColumns[j]).getValue(currentPlayer);
				}
			}
        }
    }
}
