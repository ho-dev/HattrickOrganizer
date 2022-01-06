package module.matches.statistics;

import core.db.DBManager;
import core.gui.comp.renderer.TableHeaderRenderer1;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchesHighlightsStat;
import module.matches.MatchLocation;
import module.matches.MatchesPanel;
import tool.updater.TableModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.*;


public class MatchesHighlightsTable extends JTable {

	private String[] columns = {HOVerwaltung.instance().getLanguageString("Highlights"),HOVerwaltung.instance().getLanguageString("Gesamt"),HOVerwaltung.instance().getLanguageString("Tore"),"%"};
	
	public MatchesHighlightsTable(int iMatchType){
		super();
	    initModel(iMatchType, UserParameter.instance().matchLocation);

		setDefaultRenderer(Object.class, new MatchesOverviewRenderer());
        setDefaultRenderer(Integer.class, new MatchesOverviewRenderer());

		getTableHeader().setDefaultRenderer(new TableHeaderRenderer1(this));

        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getTableHeader().setReorderingAllowed(false);
		getTableHeader().setFont(getTableHeader().getFont().deriveFont(Font.BOLD));
	}
	
    private void initModel(int iMatchType, MatchLocation matchLocation) {
        setOpaque(false);
        setModel(new TableModel(getValues(iMatchType, matchLocation),columns));
    }
    
    private Object[][] getValues(int iMatchType, MatchLocation matchLocation){
    	if(iMatchType == MatchesPanel.ALL_MATCHS || iMatchType == MatchesPanel.OTHER_TEAM_MATCHS){
         	return new Object[0][0];
         }
    	MatchesHighlightsStat[] rows = DBManager.instance().getGoalsByActionType(true, iMatchType, matchLocation);
    	Object[][] data = new Object[rows.length][columns.length];
    	for (int i = 0; i < rows.length; i++) {
			data[i][0] = rows[i];
			data[i][1] = rows[i].getTotalString();
			data[i][2] = rows[i].getGoalsString();
			data[i][3] = rows[i].getPerformanceString();
		}
    	return data;
    }
    
    public void refresh(int iMatchType, MatchLocation matchLocation){
    	 
    	setModel(new TableModel(getValues(iMatchType, matchLocation), columns));
    }
}
