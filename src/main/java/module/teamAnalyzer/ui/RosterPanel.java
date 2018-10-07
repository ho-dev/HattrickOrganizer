package module.teamAnalyzer.ui;

import core.model.HOVerwaltung;
import module.teamAnalyzer.ui.model.UiRosterTableModel;
import module.teamAnalyzer.vo.PlayerInfo;
import module.teamAnalyzer.vo.RosterPlayerData;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

public class RosterPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------
	private static final long serialVersionUID = -3867854224503291836L;
	private JTable table;
    private List<PlayerInfo> oldPlayersInfo = new ArrayList<PlayerInfo>();
    private Map<String,RosterPlayerData> players = new HashMap<String,RosterPlayerData>();
    private UiRosterTableModel tableModel;
    private String[] columns = {
    		HOVerwaltung.instance().getLanguageString("ls.player.name"),
    		HOVerwaltung.instance().getLanguageString("Role"),
    		HOVerwaltung.instance().getLanguageString("Position"),
    		HOVerwaltung.instance().getLanguageString("Secondary"),
    		HOVerwaltung.instance().getLanguageString("ls.match.id"),
    		HOVerwaltung.instance().getLanguageString("ls.player.age"),
    		HOVerwaltung.instance().getLanguageString("ls.player.form"),
    		HOVerwaltung.instance().getLanguageString("ls.player.short_experience"),
    		HOVerwaltung.instance().getLanguageString("ls.player.tsi"),
    		HOVerwaltung.instance().getLanguageString("SpecialEvent"),
    		HOVerwaltung.instance().getLanguageString("Maximal"), HOVerwaltung.instance().getLanguageString("Durchschnitt"),
    		HOVerwaltung.instance().getLanguageString("Minimal"), "Status", "PlayerId"
                               };
    //private boolean reloading = false;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new RosterPanel object.
     */
    public RosterPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public PlayerInfo getPrevious(int playerId) {
        for (Iterator<PlayerInfo> iter = oldPlayersInfo.iterator(); iter.hasNext();) {
            PlayerInfo element = iter.next();

            if (element.getPlayerId() == playerId) {
                return element;
            }
        }

        return new PlayerInfo();
    }

    private void jbInit() {
        Vector<Object> data = new Vector<Object> ();

        tableModel = new UiRosterTableModel(data, new Vector<String>(Arrays.asList(columns)));
        table = new JTable(tableModel);

        // Set up tool tips for column headers.
        table.getTableHeader().setToolTipText(HOVerwaltung.instance().getLanguageString("RecapPanel.Tooltip")); //$NON-NLS-1$
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new RosterTableRenderer());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane);
    }
}
