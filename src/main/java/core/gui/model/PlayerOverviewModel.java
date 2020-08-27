package core.gui.model;

import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.model.player.Player;
import module.playerOverview.SpielerTrainingsVergleichsPanel;

import java.util.List;


/**
 * 
 * @author Thorsten Dietz
 * @since 1.36
 */
public final  class PlayerOverviewModel extends HOTableModel {
	
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
	 * initialize all columns
	 *
	 */
	private void initialize() {
		UserColumn[] basic =  UserColumnFactory.createPlayerBasicArray();
		columns = new UserColumn[49];
		columns[0] = basic[0];
		columns[48] = basic[1];
		
		UserColumn[] skills =  UserColumnFactory.createPlayerSkillArray();
		for (int i = 9; i < skills.length+9; i++) {
			columns[i] = skills[i-9];
		}
		
		UserColumn[] positions =  UserColumnFactory.createPlayerPositionArray();
		for (int i = 21; i < positions.length+21; i++) {
			columns[i] = positions[i-21];
		}
		
		UserColumn[] goals =  UserColumnFactory.createGoalsColumnsArray();
		for (int i = 41; i < goals.length+41; i++) {
			columns[i] = goals[i-41];
		}
		UserColumn[] add =  UserColumnFactory.createPlayerAdditionalArray();
		columns[1] = add[0];
		columns[2] = add[1];
		columns[3] = add[2];
		columns[4] = add[3];
		columns[5] = add[4];
		columns[6] = add[5];
		columns[7] = add[6];
		columns[8] = add[11]; // Motherclub
		columns[45] = add[7];
		columns[46] = add[8];
		columns[40] = add[9];
		columns[47] = add[10];
	}
	
    public final Player getSpieler(int id) {
        //Kann < 0 sein für TempSpieler if ( id > 0 )
        if (id != 0) {
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
     * Passt alle Spalten an, die Verändungen bei einem HRF-Vergleich anzeigen
     */
    public final void reInitDataHRFVergleich() {
        initData();
    }
    
    /**
     * Entfernt den Player aus der Tabelle
     */
    public final void removeSpieler(Player player) {
    	m_vPlayers.remove(player);
        initData();
    }
    
    /**
     * Gibt den Player mit der gleichen ID, wie die übergebene, zurück, oder null
     */
    private Player getVergleichsSpieler(Player vorlage) {
        final int id = vorlage.getSpielerID();

        for (int i = 0;
             (SpielerTrainingsVergleichsPanel.getVergleichsPlayer() != null)
             && (i < SpielerTrainingsVergleichsPanel.getVergleichsPlayer().size()); i++) {
            final Player vergleichsPlayer = SpielerTrainingsVergleichsPanel.getVergleichsPlayer().get(i);

            if (vergleichsPlayer.getSpielerID() == id) {
                //Treffer
                return vergleichsPlayer;
            }
        }

        if (SpielerTrainingsVergleichsPanel.isVergleichsMarkierung()) {
            return getVergleichsSpielerFirstHRF(vorlage);
        }

        return null;
    }
    
    /**
     * Gibt den Player aus dem ersten HRF, wo der Player aufgetauch ist, zurück
     */
    private Player getVergleichsSpielerFirstHRF(Player vorlage) {
        return core.db.DBManager.instance().getSpielerFirstHRF(vorlage.getSpielerID());
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
