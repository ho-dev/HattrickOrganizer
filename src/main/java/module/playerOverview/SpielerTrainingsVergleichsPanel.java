package module.playerOverview;

import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.AufstellungsListRenderer;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * Panel that displays the list of stored HRFs, which can be selected to perform
 * comparisons with current team.
 */
public class SpielerTrainingsVergleichsPanel extends ImagePanel
    implements Refreshable, ListSelectionListener, ActionListener {

	@Serial
    private static final long serialVersionUID = 7090555271664890027L;

	//~ Static fields/initializers -----------------------------------------------------------------

    private static List<Player> vergleichsPlayer = new ArrayList<>();
    private static Integer hrfId;
    private static boolean vergleichsMarkierung;

    //~ Instance fields ----------------------------------------------------------------------------

    private final JButton m_jbLoeschen = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.delete"));
    private final JList<CBItem> m_jlHRFs = new JList<>();
    private final List<ChangeListener> changeListeners = new ArrayList<>();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerTrainingsVergleichsPanel object.
     */
    public SpielerTrainingsVergleichsPanel() {
        initComponents();
        RefreshManager.instance().registerRefreshable(this);
        loadHRFListe(true);
    }

    //~ Methods ------------------------------------------------------------------------------------
    public static boolean isDevelopmentStageSelected() {
        return vergleichsMarkierung;
    }

    /**
     * Gibt die Vergleichsspieler zurück
     */
    public static List<Player> getSelectedPlayerDevelopmentStage() {
        return vergleichsPlayer;
    }

    public final void actionPerformed(ActionEvent actionEvent) {
        final var hrfs = m_jlHRFs.getSelectedValuesList();
        StringBuilder deleteInfoText = new StringBuilder(HOVerwaltung.instance().getLanguageString("ls.button.delete"));

        if (hrfs.size() > 1) {
            deleteInfoText.append(" (").append(hrfs.size()).append(" Files) : ");
        } else {
            deleteInfoText.append(": ");
        }

        var MAX_NUMBER_OF_FILENAMES_IN_DELETEINFOTEXT = 11;
        int i=0;
        for (CBItem hrf : hrfs){
            deleteInfoText.append("\n");
            if ( i++ < MAX_NUMBER_OF_FILENAMES_IN_DELETEINFOTEXT){
                deleteInfoText.append(hrf.toString());
            }
            else {
                deleteInfoText.append(" ... ");
                break;
            }
        }

        final int value = JOptionPane.showConfirmDialog(this, deleteInfoText.toString(),
				HOVerwaltung.instance().getLanguageString("confirmation.title"), JOptionPane.YES_NO_OPTION);

        if (value == JOptionPane.OK_OPTION) {
            for (CBItem hrf : hrfs) {
                DBManager.instance().deleteHRF(hrf.getId());
            }

            loadHRFListe(false);
            vergleichsPlayer.clear();

            // Manual update of the table, so no reInit to keep the current sorting.
            HOMainFrame.instance().getSpielerUebersichtPanel().refreshHRFComparison();
        }
    }

    public final void reInit() {
        loadHRFListe(false);
    }

    public void refresh() {
        //nix
    }

    public static Integer getSelectedHrfId(){
        return hrfId;
    }

    /**
     * Handle valueChanged() events.
     */
	public final void valueChanged(ListSelectionEvent listSelectionEvent) {
		// Markierung vorhanden
		if (m_jlHRFs.getSelectedValue() != null) {
            hrfId = m_jlHRFs.getSelectedValue().getId();
			vergleichsPlayer = DBManager.instance().getSpieler(hrfId);
			vergleichsMarkierung = true;

            m_jbLoeschen.setEnabled(m_jlHRFs.getSelectedIndex() > 0);
		}
		// Keine Markierung -> Alles löschen
		else {
            hrfId = null;
			vergleichsPlayer.clear();
			vergleichsMarkierung = false;
			m_jbLoeschen.setEnabled(false);
		}

        ChangeEvent changeEvent = new ChangeEvent(this);
        fireChangeEvent(changeEvent);

		// Manual update of the table, so no reInit to keep the current sorting.
		HOMainFrame.instance().getSpielerUebersichtPanel().refreshHRFComparison();
	}

    /**
     * Init GUI components.
     */
	private void initComponents() {
		setLayout(new BorderLayout());

        final JLabel hrfComparisonLabel = new JLabel(HOVerwaltung.instance().getLanguageString("VergleichsHRF"));
        hrfComparisonLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 0));

		add(hrfComparisonLabel, BorderLayout.NORTH);
		m_jlHRFs.setOpaque(false);
		// use the default renderer for all non-classic skins
		if ("Classic".equals(UserParameter.instance().skin)) {
			m_jlHRFs.setCellRenderer(new AufstellungsListRenderer());
		}

		m_jlHRFs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		m_jlHRFs.addListSelectionListener(this);
		add(new JScrollPane(m_jlHRFs), BorderLayout.CENTER);

		m_jbLoeschen.setEnabled(false);
		m_jbLoeschen.addActionListener(this);
		add(m_jbLoeschen, BorderLayout.SOUTH);
	}

    /**
     * load all hrf file entries and creates a list of combo box items
     */
    List<CBItem> loadCBItemHRFList() {
        var hrfs = DBManager.instance().loadAllHRFs(false);
        var cbitems = new ArrayList<CBItem>();

        for (var hrf : hrfs) {
            var date = hrf.getDatum();
            var trainingWeek = date.toTrainingWeek();
            cbitems.add(
                    new core.datatype.CBItem(
                            date.toLocaleDateTime()
                                    + " ( "
                                    + core.model.HOVerwaltung.instance().getLanguageString("Season")
                                    + " "
                                    + trainingWeek.season
                                    + "  "
                                    + core.model.HOVerwaltung.instance().getLanguageString("ls.training.week")
                                    + " "
                                    + trainingWeek.week
                                    + " )",
                            hrf.getHrfId()));
        }
        return cbitems;
    }

    private void loadHRFListe(boolean init) {
        var hrfListe = loadCBItemHRFList();

        m_jlHRFs.removeListSelectionListener(this);

        Object lastSelectedEntry = m_jlHRFs.getSelectedValue();
        if (lastSelectedEntry == null && m_jlHRFs.getModel() instanceof DefaultListModel<CBItem> && m_jlHRFs.getModel().getSize() > 0) {
            // Memorize first entry
            lastSelectedEntry = m_jlHRFs.getModel().getElementAt(0);
        }

        DefaultListModel listmodel;

        if (m_jlHRFs.getModel() instanceof DefaultListModel) {
            listmodel = (DefaultListModel) m_jlHRFs.getModel();
            listmodel.removeAllElements();
        } else {
            listmodel = new DefaultListModel();
        }

        listmodel.addAll(hrfListe);
        m_jlHRFs.setModel(listmodel);

        // Don't show comparison during init phase.
        if (!init) {
            m_jlHRFs.addListSelectionListener(this);
        }

        if (lastSelectedEntry != null) {
            m_jlHRFs.setSelectedValue(lastSelectedEntry, true);
        } else if ((listmodel.size() > 1) && !init) {
            // Select the second HRF when not right after start
            // (presumably to select and display comparison after a new HRF download)
            m_jlHRFs.setSelectedIndex(1);
        }

        if (init) {
            m_jlHRFs.addListSelectionListener(this);
        }
    }

    public void addChangeListener(ChangeListener changeListener) {
	    changeListeners.add(changeListener);
    }

    private void fireChangeEvent(ChangeEvent event) {
	    for (ChangeListener listener: changeListeners) {
	        listener.stateChanged(event);
        }
    }
}
