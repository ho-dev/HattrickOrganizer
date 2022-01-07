package module.matches.statistics;

import core.db.DBManager;
import core.gui.comp.renderer.TableHeaderRenderer1;
import core.gui.comp.table.ToolTipHeader;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.model.UserParameter;
import core.model.match.MatchesOverviewRow;
import core.util.Helper;
import module.matches.MatchLocation;
import module.matches.MatchesPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;


public class MatchesOverviewTable extends JTable {
	
	private MatchesOverviewColumnModel tableModel;
	 
	public MatchesOverviewTable(int iMatchType){
		super();
	    initModel(iMatchType, UserParameter.instance().matchLocation);
        setDefaultRenderer(Object.class,new MatchesOverviewRenderer());
        setDefaultRenderer(Integer.class,new MatchesOverviewRenderer());
        getTableHeader().setDefaultRenderer(new TableHeaderRenderer1(this));
        getTableHeader().setFont(getTableHeader().getFont().deriveFont(Font.BOLD));
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	
    private void initModel(int iMatchType, MatchLocation matchLocation) {
        setOpaque(false);

        if (tableModel == null) {
        	tableModel = UserColumnController.instance().getMatchesOverview1ColumnModel();
        	if(iMatchType == MatchesPanel.ALL_GAMES || iMatchType == MatchesPanel.OTHER_TEAM_GAMES){
            	MatchesOverviewRow[] tmp = new MatchesOverviewRow[0];
            	tableModel.setValues(tmp);
            } else {
            	tableModel.setValues(DBManager.instance().getMatchesOverviewValues(iMatchType, matchLocation));
            }

            final ToolTipHeader header = new ToolTipHeader(getColumnModel());
            header.setToolTipStrings(tableModel.getTooltips());
            header.setToolTipText("");
            setTableHeader(header);
            setModel(tableModel);

            final TableColumnModel tableColumnModel = getColumnModel();

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                tableColumnModel.getColumn(i).setIdentifier(i);
            }

            int[][] targetColumn = tableModel.getColumnOrder();

            //Reihenfolge -> nach [][1] sortieren
            targetColumn = Helper.sortintArray(targetColumn, 1);

            if (targetColumn != null) {
                for (int i = 0; i < targetColumn.length; i++) {
                    this.moveColumn(getColumnModel().getColumnIndex(Integer.valueOf(targetColumn[i][0])),
                                    targetColumn[i][1]);
                }
            }

            //m_clTableSorter.addMouseListenerToHeaderInTable(this);
            tableModel.setColumnsSize(getColumnModel());
        } else {
        	tableModel.setValues(DBManager.instance().getMatchesOverviewValues(iMatchType, matchLocation));
        }

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setRowSelectionAllowed(true);

        //m_clTableSorter.initsort();
    }

    public final void saveColumnOrder(){
    	final UserColumn[] columns = tableModel.getDisplayedColumns();
    	final TableColumnModel tableColumnModel = getColumnModel();
    	for (int i = 0; i < columns.length; i++) {
    		columns[i].setIndex(convertColumnIndexToView(i));
    		columns[i].setPreferredWidth(tableColumnModel.getColumn(convertColumnIndexToView(i)).getWidth());
    	}
    	tableModel.setCurrentValueToColumns(columns);
    	DBManager.instance().saveHOColumnModel(tableModel);
    }
    
    public void refresh(int iMatchType, MatchLocation matchLocation) {
        if(iMatchType == MatchesPanel.ALL_GAMES || iMatchType == MatchesPanel.OTHER_TEAM_GAMES){
        	MatchesOverviewRow[] tmp = new MatchesOverviewRow[0];
        	tableModel.setValues(tmp);
        } else {
        	initModel(iMatchType, matchLocation);
        }
    }
 
}
