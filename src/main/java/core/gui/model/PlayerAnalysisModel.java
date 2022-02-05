package core.gui.model;

import core.db.DBManager;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.model.enums.MatchType;
import core.model.match.Matchdetails;
import core.model.player.Player;
import core.net.OnlineWorker;

import java.util.Vector;


/**
 * 
 * @author Thorsten Dietz
 * @since 1.36
 *
 */
public class PlayerAnalysisModel extends HOTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -2953738895366809237L;
	private Vector<PlayerMatchCBItem> m_vSpielerMatchCBItem;


	/**
	 * constructor
	 *
	 */
	public PlayerAnalysisModel(int id, int instance){
		super(id,"SpielerAnalyse");
		super.instance = instance;
		initialize();
	}

	private void initialize() {
		columns = new UserColumn[55];

		final UserColumn[] add =  UserColumnFactory.createPlayerAdditionalArray();
		final MatchKurzInfoColumn [] matches = UserColumnFactory.createMatchesArray();
		final MatchDetailsColumn [] matchdetails = UserColumnFactory.createMatchDetailsColumnsArray();
		final PlayerColumn2[] cbItems = UserColumnFactory.createPlayerCBItemArray();
		final PlayerSkillColumn[] skills =  UserColumnFactory.createPlayerSkillArray();
		final UserColumn[] goals =  UserColumnFactory.createGoalsColumnsArray();
		final PlayerPositionColumn[] positions =  UserColumnFactory.createPlayerPositionArray();

		columns[0] = matches[0];
		columns[1] = cbItems[3];
		columns[2] = cbItems[2];//new PlayerColumn(700," ",10); // Position muss dort hin
		columns[3] = add[3];

		columns[4] = matches[1];
		columns[5] = matches[2];
		columns[6] = matches[3];
		columns[7] = matches[4];

		columns[8] = add[6];
		columns[9] = matchdetails[0];
		columns[10] = matchdetails[1];
		columns[11] = matchdetails[2];
		columns[12] = matchdetails[3];
		columns[13] = cbItems[0];
		columns[14] = cbItems[1];
		columns[15] = add[11];
		columns[16] = skills[0];
		columns[17] = skills[1];
		columns[18] = skills[2];
		columns[19] = skills[3];
		columns[20] = skills[4];

		columns[21] = skills[5];
		columns[22] = skills[6];
		columns[23] = skills[7];
		columns[24] = skills[8];
		columns[25] = skills[9];
		columns[26] = skills[10];
		columns[27] = skills[11];

		columns[28] = positions[0];
		columns[29] = positions[1];
		columns[30] = positions[2];
		columns[31] = positions[3];
		columns[32] = positions[4];

		columns[33] = positions[5];
		columns[34] = positions[6];
		columns[35] = positions[7];
		columns[36] = positions[8];
		columns[37] = positions[9];

		columns[38] = positions[10];
		columns[39] = positions[11];
		columns[40] = positions[12];
		columns[41] = positions[13];
		columns[42] = positions[14];

		columns[43] = positions[15];
		columns[44] = positions[16];
		columns[45] = positions[17];
		columns[46] = positions[18];

		columns[47] = goals[0];
		columns[48] = goals[1];
		columns[49] = goals[2];
		columns[50] = goals[3];

		columns[51] = add[7];
		columns[52] = add[8];
		columns[53] = cbItems[4];

		columns[54] = matches[7];
	}



//  -----initialisierung-----------------------------------------

    /**
     * create a data[][] from player-Vector
     */
	@Override
	protected void initData() {
    	UserColumn [] tmpDisplayedColumns = getDisplayedColumns();
    	m_clData = new Object[m_vSpielerMatchCBItem.size()][tmpDisplayedColumns.length];

    	for (int i = 0; i < m_vSpielerMatchCBItem.size(); i++) {
			final PlayerMatchCBItem spielerCBItem = m_vSpielerMatchCBItem.get(i);
			final Player aktuellerPlayer = spielerCBItem.getSpieler();
			Matchdetails matchdetails = spielerCBItem.getMatchdetails();

			matchdetails = checkMatchDetailsPresent(spielerCBItem, matchdetails);

			// If we didn't manage to get details for this match, skip as we can do no more.
			if (matchdetails == null) {
				continue;
			}

			for (int j = 0; j < tmpDisplayedColumns.length; j++) {
    			if(tmpDisplayedColumns[j] instanceof PlayerColumn)
    				m_clData[i][j] = ((PlayerColumn)tmpDisplayedColumns[j]).getTableEntry(aktuellerPlayer,null);
    			if(tmpDisplayedColumns[j] instanceof MatchDetailsColumn)
    				m_clData[i][j] = ((MatchDetailsColumn)tmpDisplayedColumns[j]).getTableEntry(matchdetails);
    			if(tmpDisplayedColumns[j] instanceof MatchKurzInfoColumn)
    				m_clData[i][j] = ((MatchKurzInfoColumn)tmpDisplayedColumns[j]).getTableEntry(spielerCBItem);
    			if(tmpDisplayedColumns[j] instanceof PlayerColumn2)
    				m_clData[i][j] = ((PlayerColumn2)tmpDisplayedColumns[j]).getTableEntry(spielerCBItem);

    		}
    	}
    }

	private Matchdetails checkMatchDetailsPresent(PlayerMatchCBItem spielerCBItem, Matchdetails matchdetails) {
		if (matchdetails.getMatchID() == -1) {
			boolean success = OnlineWorker.downloadMatchData(spielerCBItem.getMatchID(), MatchType.getById(spielerCBItem.getMatchType().getMatchTypeId()), true);
			if (success) {
				matchdetails = DBManager.instance().loadMatchDetails(spielerCBItem.getMatchType().getMatchTypeId(), spielerCBItem.getMatchID());
			}
		}
		return matchdetails;
	}

	/**
     * Player neu setzen
     */
    public final void setValues(Vector<PlayerMatchCBItem> spielermatchCBItem) {
        m_vSpielerMatchCBItem = spielermatchCBItem;
        initData();
    }

}
