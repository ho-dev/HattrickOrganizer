package module.teamAnalyzer.ui;

import core.constants.player.PlayerAbility;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchType;
import core.model.match.Matchdetails;
import core.module.config.ModuleConfig;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.MatchPopulator;
import module.teamAnalyzer.ui.controller.RecapListSelectionListener;
import module.teamAnalyzer.ui.model.UiRecapTableModel;
import module.teamAnalyzer.vo.Match;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.MatchRating;
import module.teamAnalyzer.vo.TeamLineup;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;



public class RecapPanel extends JPanel {
    //~ Static fields/initializers -----------------------------------------------------------------
	private static final long serialVersionUID = 486150690031160261L;
    public static final String VALUE_NA = "---"; //$NON-NLS-1$
    private static final String GOALS_SPACE = " - "; //$NON-NLS-1$

    //~ Instance fields ----------------------------------------------------------------------------
    private JTable table;
    private RecapTableSorter sorter;
    private UiRecapTableModel tableModel;
    private RecapListSelectionListener recapListener = null;
    private String[] columns = {
    		HOVerwaltung.instance().getLanguageString("RecapPanel.Game"), //$NON-NLS-1$
    		HOVerwaltung.instance().getLanguageString("Type"), //$NON-NLS-1$
    		HOVerwaltung.instance().getLanguageString("ls.match.result"),
    		HOVerwaltung.instance().getLanguageString("Week"), //$NON-NLS-1$
    		HOVerwaltung.instance().getLanguageString("Season"), //$NON-NLS-1$
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"),
    		HOVerwaltung.instance().getLanguageString("RecapPanel.Stars"), //$NON-NLS-1$
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.hatstats"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.squad"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.smartsquad"),
    		HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.loddarstats"),
    HOVerwaltung.instance().getLanguageString("ls.team.tactic"),
    HOVerwaltung.instance().getLanguageString("ls.team.tacticalskill"),
    HOVerwaltung.instance().getLanguageString("ls.team.formation"),
    "", //$NON-NLS-1$
    "" //$NON-NLS-1$
    };

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new RecapPanel object.
     */
    public RecapPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void reload(TeamLineup lineup) {
        // Empty model
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        MatchRating averageRating = null;
        double stars = 0;

        if (lineup != null) {
            averageRating = lineup.getRating();
            stars = lineup.getStars();
        }

        List<MatchDetail> list = MatchPopulator.getAnalyzedMatch();
        Vector<Object> rowData;

        if (list.size() > 1) {
            rowData = new Vector<Object>();
            rowData.add(HOVerwaltung.instance().getLanguageString("Durchschnitt"));
            rowData.add(VALUE_NA);
            rowData.add(VALUE_NA);
            rowData.add(VALUE_NA);
            rowData.add(VALUE_NA);
            setRating(rowData, averageRating);

            DecimalFormat df = new DecimalFormat("###.#");

            rowData.add(df.format(stars));
            if(averageRating!=null) {
	            rowData.add(df.format(averageRating.getHatStats()));
	            rowData.add(df.format(averageRating.getSquad()));
	            if (stars > 0) {
	            	rowData.add(df.format(averageRating.getSquad() / stars));
	            } else {
	            	rowData.add(df.format(0));
	            }
                rowData.add(df.format(averageRating.getLoddarStats()));
            } else {
            	rowData.add(df.format(0));
	            rowData.add(df.format(0));
	            rowData.add(df.format(0));
                rowData.add(df.format(0));
            }
            rowData.add(VALUE_NA);
            rowData.add(VALUE_NA);
            rowData.add(VALUE_NA);
            rowData.add(VALUE_NA);
            rowData.add("");
            rowData.add("");
            tableModel.addRow(rowData);
            table.getSelectionModel().setSelectionInterval(0, 0);
        }

        for (Iterator<MatchDetail> iter = list.iterator(); iter.hasNext();) {
            MatchDetail matchDetail = (MatchDetail) iter.next();

            rowData = new Vector<Object>();

            Match match = matchDetail.getMatchDetail();

            MatchType matchType = match.getMatchType();
            boolean isHomeMatch = match.isHome();

            // Columns 0-2
            if (isHomeMatch) {
                rowData.add(match.getAwayTeam());
                rowData.add(ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()]));
                rowData.add(match.getHomeGoals() + GOALS_SPACE + match.getAwayGoals());
            } else {
                rowData.add("* " + match.getHomeTeam()); //$NON-NLS-1$
                rowData.add(ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()]));
                rowData.add(match.getAwayGoals() + GOALS_SPACE + match.getHomeGoals());
            }

            // Columns 3 & 4
            rowData.add(match.getWeek() + ""); //$NON-NLS-1$
            rowData.add(match.getSeason() + ""); //$NON-NLS-1$

            // Columns 5-11
            setRating(rowData, matchDetail.getRating());

            DecimalFormat df = new DecimalFormat("###.#"); //$NON-NLS-1$

            // Columns 12-15
            rowData.add(df.format(matchDetail.getStars()));
            rowData.add(df.format(matchDetail.getRating().getHatStats()));
            rowData.add(df.format(matchDetail.getRating().getSquad()));
            rowData.add(df.format(matchDetail.getRating().getSquad() / matchDetail.getStars()));

            DecimalFormat df2 = new DecimalFormat("###.##"); //$NON-NLS-1$

            // Columns 16-17
            rowData.add(df2.format(matchDetail.getRating().getLoddarStats()));
            rowData.add(Matchdetails.getNameForTaktik(matchDetail.getTacticCode()));

            // Column 18
            if (matchDetail.getTacticCode() == 0) {
                rowData.add(VALUE_NA);
            } else {
                rowData.add(PlayerAbility.getNameForSkill(matchDetail.getTacticLevel(), false));
            }

            // Columns 19-21
            rowData.add(matchDetail.getFormation());
            rowData.add(new Integer(matchType.getId()));
            rowData.add(new Boolean(isHomeMatch));

            tableModel.addRow(rowData);
        }

        if (list.size() == 0) {
            rowData = new Vector<Object>();
            rowData.add(HOVerwaltung.instance().getLanguageString("RecapPanel.NoMatch")); //$NON-NLS-1$
            tableModel.addRow(rowData);
        }

        setColumnWidth(0, 100);
        setColumnWidth(1, 20);
        setColumnWidth(2, 40);
        setColumnWidth(3, 50);
        setColumnWidth(4, 50);

        if (ModuleConfig.instance().getBoolean(SystemManager.ISSTARS)) {
            setColumnWidth(12, 50);
        } else {
            setColumnInvisible(12);
        }

        if (ModuleConfig.instance().getBoolean(SystemManager.ISTOTALSTRENGTH)) {
            setColumnWidth(13, 50);
        } else {
            setColumnInvisible(13);
        }

        if (ModuleConfig.instance().getBoolean(SystemManager.ISSQUAD)) {
            setColumnWidth(14, 50);
        } else {
            setColumnInvisible(14);
        }

        if (ModuleConfig.instance().getBoolean(SystemManager.ISSMARTSQUAD)) {
            setColumnWidth(15, 50);
        } else {
            setColumnInvisible(15);
        }

        if (ModuleConfig.instance().getBoolean(SystemManager.ISLODDARSTATS)) {
            setColumnWidth(16, 50);
        } else {
            setColumnInvisible(16);
        }

        // Hide 'match type' and 'is home match?' columns.
        setColumnInvisible(20);
        setColumnInvisible(21);
    }

    private void setColumnInvisible(int col) {
        table.getTableHeader().getColumnModel().getColumn(col).setWidth(0);
        table.getTableHeader().getColumnModel().getColumn(col).setPreferredWidth(0);
        table.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
    }

    private void setColumnWidth(int col, int width) {
        table.getTableHeader().getColumnModel().getColumn(col).setWidth(width);
        table.getTableHeader().getColumnModel().getColumn(col).setPreferredWidth(width);
        table.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(200);
        table.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
    }

    private void setRating(Vector<Object> row, MatchRating rating) {
        if (rating == null) {
            for (int i = 0; i < 7; i++) {
                row.add(""); //$NON-NLS-1$
            }

            return;
        }

        row.add(getRating((int) rating.getMidfield()));
        row.add(getRating((int) rating.getRightDefense()));
        row.add(getRating((int) rating.getCentralDefense()));
        row.add(getRating((int) rating.getLeftDefense()));
        row.add(getRating((int) rating.getRightAttack()));
        row.add(getRating((int) rating.getCentralAttack()));
        row.add(getRating((int) rating.getLeftAttack()));
    }

    private String getRating(int rating) {
        return RatingUtil.getRating(rating,
        		ModuleConfig.instance().getBoolean(SystemManager.ISNUMERICRATING),
        		ModuleConfig.instance().getBoolean(SystemManager.ISDESCRIPTIONRATING));
    }

    private void jbInit() {
        Vector<Object> data = new Vector<Object>();

        tableModel = new UiRecapTableModel(data, new Vector<String>(Arrays.asList(columns)));

        sorter = new RecapTableSorter(tableModel);
        table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());

        // Set up tool tips for column headers.
        table.getTableHeader().setToolTipText(HOVerwaltung.instance().getLanguageString("RecapPanel.Tooltip")); //$NON-NLS-1$

        table.setDefaultRenderer(Object.class, new RecapTableRenderer());
        table.setDefaultRenderer(ImageIcon.class, new RecapTableRenderer());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel rowSM = table.getSelectionModel();
        recapListener = new RecapListSelectionListener(sorter, tableModel);
        rowSM.addListSelectionListener(recapListener);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane);
    }

    public String getSelectedTacticType() {
    	return recapListener.getSelectedTacticType();
    }

    public String getSelectedTacticSkill() {
    	return recapListener.getSelectedTacticSkill();
    }
}
