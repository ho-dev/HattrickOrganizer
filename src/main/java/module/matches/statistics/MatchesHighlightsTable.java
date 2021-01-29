package module.matches.statistics;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchesHighlightsStat;
import module.matches.MatchesPanel;
import tool.updater.TableModel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;



public class MatchesHighlightsTable extends JTable {

	private static final long serialVersionUID = -4245517410989289229L;
	private String[] columns = {HOVerwaltung.instance().getLanguageString("Highlights"),HOVerwaltung.instance().getLanguageString("Gesamt"),HOVerwaltung.instance().getLanguageString("Tore"),"%"};
	
	public MatchesHighlightsTable(int matchtyp){
		super();
	    initModel(matchtyp);
        setDefaultRenderer(Object.class,new MatchesOverviewRenderer());
        setDefaultRenderer(Integer.class,new MatchesOverviewRenderer());
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getTableHeader().setReorderingAllowed(false);
	}
	
    private void initModel(int matchtyp) {
        setOpaque(false);
        setModel(new TableModel(getValues(matchtyp),columns));
    }
    
    private Object[][] getValues(int matchtyp){
    	if(matchtyp == MatchesPanel.ALL_MATCHS || matchtyp == MatchesPanel.OTHER_TEAM_MATCHS){
         	return new Object[0][0];
         }
    	MatchesHighlightsStat[] rows = DBManager.instance().getChancesStat(true,matchtyp);
    	Object[][] data = new Object[rows.length][columns.length];
    	for (int i = 0; i < rows.length; i++) {
			data[i][0] = rows[i];
			data[i][1] = rows[i].getTotalString();
			data[i][2] = rows[i].getGoalsString();
			data[i][3] = rows[i].getPerformanceString();
		}
    	return data;
    }
    
    public void refresh(int matchtyp){
    	 
    	setModel(new TableModel(getValues(matchtyp),columns));
    }
}
