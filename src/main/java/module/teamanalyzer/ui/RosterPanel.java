package module.teamanalyzer.ui;

import core.model.TranslationFacility;
import module.teamanalyzer.ui.model.UiRosterTableModel;
import module.teamanalyzer.vo.PlayerInfo;
import module.teamanalyzer.vo.RosterPlayerData;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class RosterPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------
	private JTable table;
    private List<PlayerInfo> oldPlayersInfo = new ArrayList<>();
    private Map<String,RosterPlayerData> players = new HashMap<>();
    private UiRosterTableModel tableModel;
    private String[] columns = {
            TranslationFacility.tr("ls.player.name"),
            TranslationFacility.tr("Role"),
            TranslationFacility.tr("Position"),
            TranslationFacility.tr("Secondary"),
            TranslationFacility.tr("ls.match.id"),
            TranslationFacility.tr("ls.player.age"),
            TranslationFacility.tr("ls.player.form"),
            TranslationFacility.tr("ls.player.short_experience"),
            TranslationFacility.tr("ls.player.tsi"),
            TranslationFacility.tr("SpecialEvent"),
            TranslationFacility.tr("Maximal"),
            TranslationFacility.tr("Durchschnitt"),
            TranslationFacility.tr("Minimal"),
            "Status",
            "PlayerId"
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
        for (PlayerInfo element : oldPlayersInfo) {
            if (element.getPlayerId() == playerId) {
                return element;
            }
        }

        return new PlayerInfo();
    }

    private void jbInit() {
        Vector<Vector<Object>> data = new Vector<>();

        tableModel = new UiRosterTableModel(data, new Vector<>(Arrays.asList(columns)));
        table = new JTable(tableModel);

        // Set up tool tips for column headers.
        table.getTableHeader().setToolTipText(TranslationFacility.tr("RecapPanel.Tooltip")); //$NON-NLS-1$
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
