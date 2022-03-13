package module.matches.statistics;

import core.gui.comp.table.HOTableModel;
import core.gui.model.UserColumnController;
import core.model.match.MatchesOverviewRow;
import core.util.StringUtils;


public final class MatchesOverviewColumnModel extends HOTableModel {

	private MatchesOverviewRow[] rows;
	
	public MatchesOverviewColumnModel(UserColumnController.ColumnModelId id){
		super(id,"MatchesStatistics");
		columns = createMatchesStatisticsArray();
	}
	
	private MatchesOverviewColumn[] createMatchesStatisticsArray(){
		MatchesOverviewColumn[] columns = new MatchesOverviewColumn[6];
		columns[0] = new MatchesOverviewColumn(701, " "," ",50);
		columns[1] = new MatchesOverviewColumn(702, "Spiele","Spiele",100);
		columns[2] = new MatchesOverviewColumn(703, "SerieAuswaertsSieg","SerieAuswaertsSieg",50);
		columns[3] = new MatchesOverviewColumn(704, "SerieAuswaertsUnendschieden","SerieAuswaertsUnendschieden",50);
		columns[4] = new MatchesOverviewColumn(706, "SerieAuswaertsNiederlage","SerieAuswaertsNiederlage",50);
		columns[5] = new MatchesOverviewColumn(707, "Tore","Tore",50);
		return columns;
	}
	
	@Override
	protected void initData() {
		m_clData = new Object[rows.length][columns.length];
		for (int i = 0; i < rows.length; i++) {
			boolean title = rows[i].getType() == -1;
			m_clData[i][0] = rows[i];
			m_clData[i][1] = title?"":Integer.valueOf(rows[i].getCount());
			m_clData[i][2] = title?"":Integer.valueOf(rows[i].getWin());
			m_clData[i][3] = title?"":Integer.valueOf(rows[i].getDraw());
			m_clData[i][4] = title?"":Integer.valueOf(rows[i].getLoss());
			m_clData[i][5] = title?"": StringUtils.getResultString(rows[i].getHomeGoals(), rows[i].getAwayGoals(), "");
		}
		fireTableDataChanged();						
	}

    public void setValues(MatchesOverviewRow[] rows) {
    	this.rows = rows;
        initData();
    }
}
