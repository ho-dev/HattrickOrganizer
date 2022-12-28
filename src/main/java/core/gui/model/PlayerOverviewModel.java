package core.gui.model;

import core.db.DBManager;
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
public final class PlayerOverviewModel extends HOTableModel {
	
	@Serial
	private static final long serialVersionUID = 5149408240369536138L;
	
	/** all players **/
	private List<Player> m_vPlayers;
	
	/**
	 * constructor
	 *
	 */
	PlayerOverviewModel(UserColumnController.ColumnModelId id){
		super(id,"Spieleruebersicht");
		initialize();
	}

	@Override
	public boolean userCanDisableColumns() {
		return !DBManager.instance().isFirstStart();
	}
	
	/**
	 * initialize all columns.
	 */
	private void initialize() {
		UserColumn[] basic = UserColumnFactory.createPlayerBasicArray();
		columns = new UserColumn[59];
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
	}
	
    public Player getPlayer(int id) {
        // Can be negative for temp player
        if (id != 0) {
			for (Player m_vPlayer : m_vPlayers) {
				if (m_vPlayer.getPlayerID() == id) {
					return m_vPlayer;
				}
			}
        }

        return null;
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
    public void reInitDataHRFVergleich() {
        initData();
    }

    
    /**
     * Returns the {@link Player} with the same ID as the instance passed, or <code>null</code>.
     */
    private Player getPreviousPlayerDevelopmentStage(Player currentDevelopmentStage) {
        final int id = currentDevelopmentStage.getPlayerID();

        for (int i = 0;
			 (SpielerTrainingsVergleichsPanel.getSelectedPlayerDevelopmentStage() != null)
             && (i < SpielerTrainingsVergleichsPanel.getSelectedPlayerDevelopmentStage().size()); i++) {
            final Player selectedDevelopmentStage = SpielerTrainingsVergleichsPanel.getSelectedPlayerDevelopmentStage().get(i);

            if (selectedDevelopmentStage.getPlayerID() == id) {
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
		if ( hrfId!=null) {
			var hrf = DBManager.instance().loadHRF(hrfId);
			after = hrf.getDatum();
		}
        return core.db.DBManager.instance().loadPlayerFirstHRF(vorlage.getPlayerID(), after);
    }
    
//  -----initialisierung-----------------------------------------

    /**
     * create a data[][] from player-Vector
     */
    @Override
	protected void initData() {
    	UserColumn [] tmpDisplayedColumns = getDisplayedColumns();
    	m_clData = new Object[m_vPlayers.size()][tmpDisplayedColumns.length];
    	
    	for (int i = 0; i < m_vPlayers.size(); i++) {
    		final Player aktuellerPlayer = m_vPlayers.get(i);
    		final Player vergleichsPlayer = getPreviousPlayerDevelopmentStage(aktuellerPlayer);
    		
    		for (int j = 0; j < tmpDisplayedColumns.length; j++) {
    			m_clData[i][j] = ((PlayerColumn)tmpDisplayedColumns[j]).getTableEntry(aktuellerPlayer, vergleichsPlayer);
			}
    	}
    }
    
    /**
     * Passt nur die Aufstellung an
     */
    public void reInitData() {
    	UserColumn [] tmpDisplayedColumns = getDisplayedColumns();
        for (int i = 0; i < m_vPlayers.size(); i++) {
            final Player aktuellerPlayer = m_vPlayers.get(i);
            
            for (int j = 0; j < tmpDisplayedColumns.length; j++) {
				if(tmpDisplayedColumns[j].getId() == UserColumnFactory.NAME
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.LINUP
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.BEST_POSITION
						|| tmpDisplayedColumns[j].getId() == UserColumnFactory.GROUP)
					m_clData[i][j] = ((PlayerColumn)tmpDisplayedColumns[j]).getTableEntry(aktuellerPlayer,null);
			}
        }   
    }
}
