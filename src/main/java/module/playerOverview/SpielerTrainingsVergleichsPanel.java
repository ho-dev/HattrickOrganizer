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
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;



/**
 * Hier kann eingestellt werden, mit welchem HRF die aktuelle Mannschaft verglichen werden soll
 */
public class SpielerTrainingsVergleichsPanel extends ImagePanel
    implements Refreshable, ListSelectionListener, ActionListener {

	private static final long serialVersionUID = 7090555271664890027L;

	//~ Static fields/initializers -----------------------------------------------------------------

    private static List<Player> vergleichsPlayer = new ArrayList<>();
    private static boolean vergleichsMarkierung;

    //~ Instance fields ----------------------------------------------------------------------------

    private JButton m_jbLoeschen = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.delete"));
    private JList m_jlHRFs = new JList();
    private List<ChangeListener> changeListeners = new ArrayList<>();

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
    public static boolean isVergleichsMarkierung() {
        return vergleichsMarkierung;
    }

    /**
     * Gibt die Vergleichsspieler zurück
     */
    public static List<Player> getVergleichsPlayer() {
        return vergleichsPlayer;
    }

    public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        final Object[] hrfs = m_jlHRFs.getSelectedValues();
        String text = HOVerwaltung.instance().getLanguageString("ls.button.delete");

        if (hrfs.length > 1) {
            text += (" (" + hrfs.length + " Files) : ");
        } else {
            text += ": ";
        }

        for (int i = 0; (i < hrfs.length) && (i < 11); i++) {
            text += ("\n" + hrfs[i].toString());

            if (i == 10) {
                text += "\n ... ";
            }
        }

        final int value = JOptionPane.showConfirmDialog(this, text,
				HOVerwaltung.instance().getLanguageString("confirmation.title"), JOptionPane.YES_NO_OPTION);

        if (value == JOptionPane.OK_OPTION) {
            for (int i = 0; i < hrfs.length; i++) {
                DBManager.instance().deleteHRF(((CBItem) hrfs[i]).getId());
            }

            loadHRFListe(false);
            vergleichsPlayer.clear(); // .removeAllElements();

            // HRF Deleted, recalculate Skillups
			DBManager.instance().reimportSkillup();

            //Nur manuelles Update der Tabelle, kein reInit, damit die Sortierung bleibt.
            HOMainFrame.instance().getSpielerUebersichtPanel().refreshHRFVergleich();
        }
    }

    public final void reInit() {
        loadHRFListe(false);
    }

    public void refresh() {
        //nix
    }

    /**
     * Handle valueChanged() events.
     */
	public final void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
		// Markierung vorhanden
		if (m_jlHRFs.getSelectedValue() != null) {
			vergleichsPlayer = DBManager.instance().getSpieler(((CBItem) m_jlHRFs.getSelectedValue()).getId());
			vergleichsMarkierung = true;

			if (m_jlHRFs.getSelectedIndex() > 0) {
				m_jbLoeschen.setEnabled(true);
			} else {
				m_jbLoeschen.setEnabled(false);
			}
		}
		// Keine Markierung -> Alles löschen
		else {
			vergleichsPlayer.clear();
			vergleichsMarkierung = false;
			m_jbLoeschen.setEnabled(false);
		}

        ChangeEvent changeEvent = new ChangeEvent(this);
        fireChangeEvent(changeEvent);

		// Manual update of the table, so no reInit to keep the current sorting.
		HOMainFrame.instance().getSpielerUebersichtPanel().refreshHRFVergleich();
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

    private void loadHRFListe(boolean init) {
        final Vector<CBItem> hrfListe = DBManager.instance().getCBItemHRFListe(new Timestamp(0));

        m_jlHRFs.removeListSelectionListener(this);

        final Object letzteMarkierung = m_jlHRFs.getSelectedValue();

        DefaultListModel listmodel;

        if (m_jlHRFs.getModel() instanceof DefaultListModel) {
            listmodel = (DefaultListModel) m_jlHRFs.getModel();
            listmodel.removeAllElements();
        } else {
            listmodel = new DefaultListModel();
        }

        for (int i = 0; i < hrfListe.size(); i++) {
            listmodel.addElement(hrfListe.get(i));
        }

        m_jlHRFs.setModel(listmodel);

        //Bei der Initialisierung noch keinen Vergleich anzeigen!
        if (!init) {
            m_jlHRFs.addListSelectionListener(this);
        }

        if (letzteMarkierung != null) {
            m_jlHRFs.setSelectedValue(letzteMarkierung, true);
        } else if ((listmodel.size() > 1) && !init) {
            //Sonst das 2. HRF markieren, wenn es nicht direkt nach dem Start ist!
            m_jlHRFs.setSelectedIndex(1);
        }

        //Beim Initialisieren hier den Listener hinzufügen
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
