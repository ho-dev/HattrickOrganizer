package core.gui.model;

import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.model.player.Player;
import module.playerOverview.SpielerTrainingsVergleichsPanel;

import java.util.List;


/**
 * Model used to display players in the Squad table.
 *
 * @author Thorsten Dietz
 * @since 1.36
 */
public final class PlayerOverviewModel extends HOTableModel {
	
	private static final long serialVersionUID = 5149408240369536138L;
	
	/** all players **/
	private List<Player> m_vPlayers;
	
	/**
	 * constructor
	 *
	 */
	protected PlayerOverviewModel(int id ){
		super(id,"Spieleruebersicht");
		initialize();
	}
	
	/**
	 * initialize all columns.
	 */
	private void initialize() {
		UserColumn[] basic = UserColumnFactory.createPlayerBasicArray();
		columns = new UserColumn[50];
		columns[0] = basic[0];
		columns[48] = basic[1];
		
		UserColumn[] skills =  UserColumnFactory.createPlayerSkillArray();
		int skillIndex = 9; // - 20
		for (int i = 0; i < skills.length; i++) {
			columns[skillIndex+i] = skills[i];
		}
		
		UserColumn[] positions =  UserColumnFactory.createPlayerPositionArray();
		int positionIndex = 23;//- 41
		for (int i = 0; i < positions.length; i++) {
			columns[positionIndex+i] = positions[i];
		}
		
		UserColumn[] goals =  UserColumnFactory.createGoalsColumnsArray();
		int goalsIndex = 42;//-45
		for (int i = 0; i < goals.length; i++) {
			columns[goalsIndex+i] = goals[i];
		}
		UserColumn[] add = UserColumnFactory.createPlayerAdditionalArray();
		columns[1] = add[0];
		columns[2] = add[1];
		columns[4] = add[2];
		columns[21] = add[3]; // best position
		columns[5] = add[4];
		columns[6] = add[5];
		columns[7] = add[6];
		columns[46] = add[7];
		columns[8] = add[8];// tsi
		columns[22] = add[9]; // lastmatch
		columns[47] = add[11];
		columns[3] = add[12];// Motherclub
		columns[49] = add[10];
	}
	
    public final Player getPlayer(int id) {
        // Can be negative for temp player
        if (id != 0) {
            for (int i = 0; i < m_vPlayers.size(); i++) {
                if (m_vPlayers.get(i).getPlayerID() == id) {
                    return m_vPlayers.get(i);
                }
            }
        }

        return null;
    }
    
    /**
     * Sets the new list of players.
     */
    public final void setValues(List<Player> player) {
    	m_vPlayers = player;
        initData();
    }
    
    /**
     * Resets the data for an HRF comparison.
     */
    public final void reInitDataHRFVergleich() {
        initData();
    }

    
    /**
     * Returns the {@link Player} with the same ID as the instance passed, or <code>null</code>.
     */
    private Player getVergleichsSpieler(Player vorlage) {
        final int id = vorlage.getPlayerID();

        for (int i = 0;
             (SpielerTrainingsVergleichsPanel.getVergleichsPlayer() != null)
             && (i < SpielerTrainingsVergleichsPanel.getVergleichsPlayer().size()); i++) {
            final Player vergleichsPlayer = SpielerTrainingsVergleichsPanel.getVergleichsPlayer().get(i);

            if (vergleichsPlayer.getPlayerID() == id) {
                return vergleichsPlayer;
            }
        }

        if (SpielerTrainingsVergleichsPanel.isVergleichsMarkierung()) {
            return getVergleichsSpielerFirstHRF(vorlage);
        }

        return null;
    }
    
    /**
     * Returns the {@link Player} from the first HRF in which he appears.
     */
    private Player getVergleichsSpielerFirstHRF(Player vorlage) {
        return core.db.DBManager.instance().getSpielerFirstHRF(vorlage.getPlayerID());
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
    		final Player vergleichsPlayer = getVergleichsSpieler(aktuellerPlayer);
    		
    		for (int j = 0; j < tmpDisplayedColumns.length; j++) {
    			m_clData[i][j] = ((PlayerColumn)tmpDisplayedColumns[j]).getTableEntry(aktuellerPlayer, vergleichsPlayer);
			}
    	}
    }
    
    /**
     * Passt nur die Aufstellung an
     */
    public final void reInitData() {
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
