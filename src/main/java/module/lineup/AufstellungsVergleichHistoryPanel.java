// %651501138:de.hattrickorganizer.gui.lineup%
package module.lineup;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.AufstellungCBItem;
import core.gui.model.AufstellungsListRenderer;
import core.gui.model.LineupListRenderer;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.GUIUtils;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Aufstellungen können hier gespeichert werden oder mit anderen verglichen
 * werden
 */
public class AufstellungsVergleichHistoryPanel extends ImagePanel implements
		core.gui.Refreshable, ListSelectionListener, ActionListener, MouseListener {

	private static final long serialVersionUID = 7313614630687892362L;
	private static AufstellungCBItem m_clAngezeigteAufstellung;
	private static AufstellungCBItem m_clVergleichsAufstellung;
	private static AufstellungCBItem m_clHRFNextAufstellung;
	private static AufstellungCBItem m_clHRFLastAufstellung;
	private static boolean m_bVergleichAngestossen;
	private JButton m_jbAufstellungAnzeigen = new JButton(HOVerwaltung.instance()
			.getLanguageString("AufstellungAnzeigen"));
	private JButton m_jbAufstellungLoeschen = new JButton(HOVerwaltung.instance()
			.getLanguageString("AufstellungLoeschen"));
	private JButton m_jbAufstellungSpeichern = new JButton(HOVerwaltung.instance()
			.getLanguageString("AufstellungSpeichern"));
	private JList m_jlAufstellungen = new JList();

	/**
	 * Creates a new AufstellungsVergleichHistoryPanel object.
	 */
	public AufstellungsVergleichHistoryPanel() {
		initComponents();

		RefreshManager.instance().registerRefreshable(this);

		// There was an NPE once...
		try {
			m_clHRFNextAufstellung = new AufstellungCBItem(HOVerwaltung.instance()
					.getLanguageString("AktuelleAufstellung"), HOVerwaltung.instance().getModel()
					.getLineup().duplicate());
			m_clHRFLastAufstellung = new AufstellungCBItem(HOVerwaltung.instance()
					.getLanguageString("LetzteAufstellung"), HOVerwaltung.instance().getModel()
					.getLastAufstellung().duplicate());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "Err: " + e);
		}
		createAufstellungsListe();
	}

	/**
	 * Ist die Übergebene Aufstellung angezeigt?
	 */
	public static boolean isAngezeigt(AufstellungCBItem aufstellung) {
		if (aufstellung != null) {
			return aufstellung.equals(m_clAngezeigteAufstellung);
		} else {
			return false;
		}
	}

	/**
	 * Setzt die angezeige Aufstellung
	 */
	public static void setAngezeigteAufstellung(AufstellungCBItem aufstellung) {
		m_clAngezeigteAufstellung = aufstellung.duplicate();
	}

	/**
	 * Setzt die HRFAufstellung nach dem Import einen HRFs
	 */
	public static void setHRFAufstellung(Lineup nextAufstellung, Lineup lastAufstellung) {
		if (nextAufstellung != null) {
			m_clHRFNextAufstellung = new AufstellungCBItem(HOVerwaltung.instance()
					.getLanguageString("AktuelleAufstellung"), nextAufstellung.duplicate());
		}

		if (lastAufstellung != null) {
			m_clHRFLastAufstellung = new AufstellungCBItem(HOVerwaltung.instance()
					.getLanguageString("LetzteAufstellung"), lastAufstellung.duplicate());
		}
	}

	/**
	 * Returns Last Lineup
	 */
	public static AufstellungCBItem getLastLineup() {
		return m_clHRFLastAufstellung;
	}

	/**
	 * Wird vom AufstellungsDetailPanel aufgerufen, um mit der
	 * Vergleichsaufstellung anzuzeigen. Wird danach wieder auf false gesetzt
	 */
	public static boolean isVergleichgefordert() {
		final boolean vergleichgefordert = m_bVergleichAngestossen;
		m_bVergleichAngestossen = false;
		return vergleichgefordert;
	}

	/**
	 * Gibt die VergleichsAufstellung zurück
	 */
	public static AufstellungCBItem getVergleichsAufstellung() {
		return m_clVergleichsAufstellung;
	}

	/**
	 * Handle action events.
	 */
	@Override
	public final void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(m_jbAufstellungAnzeigen)) {
			// use selected lineup
			loadSelectedStoredLineup();
		} else if (actionEvent.getSource().equals(m_jbAufstellungSpeichern)) {
			saveLineup();
		} else if (actionEvent.getSource().equals(m_jbAufstellungLoeschen)) {
			deleteLineup();
		}
		repaint();
	}

	private void saveLineup() {
		String aufstellungsname = "";
		if (m_jlAufstellungen.getSelectedIndex() > 1) {
			aufstellungsname = ((AufstellungCBItem) m_jlAufstellungen.getSelectedValue()).getText();
		}
		final int x = HOMainFrame.instance().getLocation().x
				+ HOMainFrame.instance().getSize().width;
		final int y = HOMainFrame.instance().getLocation().y
				+ HOMainFrame.instance().getSize().height;
		AufstellungsNameDialog dlg = new AufstellungsNameDialog(HOMainFrame.instance(),
				aufstellungsname, HOVerwaltung.instance().getModel().getLineup(), x, y);
		dlg.setVisible(true);

		if (!dlg.isCanceled()) {
			reInit();
		}
	}

	private void deleteLineup() {
		String aufstellungsname = "";
		if (m_jlAufstellungen.getSelectedIndex() > 0) {
			aufstellungsname = ((AufstellungCBItem) m_jlAufstellungen.getSelectedValue()).getText();
		}
		DBManager.instance().deleteAufstellung(Lineup.NO_HRF_VERBINDUNG,
				((AufstellungCBItem) m_jlAufstellungen.getSelectedValue()).getText());
		HOMainFrame
				.instance()
				.getInfoPanel()
				.setLangInfoText(
						HOVerwaltung.instance().getLanguageString("Aufstellung")
								+ " "
								+ ((core.gui.model.AufstellungCBItem) m_jlAufstellungen
										.getSelectedValue()).getText() + " "
								+ HOVerwaltung.instance().getLanguageString("geloescht"));
		File f = new File("Lineups/" + HOVerwaltung.instance().getModel().getBasics().getManager()
				+ "/" + aufstellungsname + ".dat");
		f.delete();
		((DefaultListModel) m_jlAufstellungen.getModel()).removeElement(m_jlAufstellungen
				.getSelectedValue());
	}

	private void loadSelectedStoredLineup() {
		final Lineup old = HOVerwaltung.instance().getModel().getLineup();
		m_clAngezeigteAufstellung = ((AufstellungCBItem) m_jlAufstellungen.getSelectedValue())
				.duplicate();
		final Lineup new1 = m_clAngezeigteAufstellung.getAufstellung().duplicate();
		if (old != null) { // else we lose the location (home / away / derby)
							// here
			new1.setLocation(old.getLocation());
		}
		HOVerwaltung.instance().getModel().setAufstellung(new1);
		HOMainFrame.instance().getAufstellungsPanel().update();
	}

	/**
	 * Handle mouse clicked events.
	 */
	@Override
	public final void mouseClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() >= 2) {
			loadSelectedStoredLineup();
			repaint();
		}
	}

	/**
	 * Handle mouse entered events.
	 */
	@Override
	public void mouseEntered(MouseEvent mouseEvent) {
	}

	/**
	 * Handle mouse exited events.
	 */
	@Override
	public void mouseExited(MouseEvent mouseEvent) {
	}

	/**
	 * Handle mouse pressed events.
	 */
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
	}

	/**
	 * Handle mouse released events.
	 */
	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
	}

	/**
	 * Re-Init lineup.
	 */
	@Override
	public final void reInit() {
		createAufstellungsListe();
	}

	@Override
	public void refresh() {
	}

	/**
	 * Handle list selection events.
	 */
	@Override
	public final void valueChanged(ListSelectionEvent listSelectionEvent) {
		if (!listSelectionEvent.getValueIsAdjusting()) {
			// Aufstellung markiert
			if ((m_jlAufstellungen.getSelectedValue() != null)
					&& m_jlAufstellungen.getSelectedValue() instanceof AufstellungCBItem) {
				final AufstellungCBItem aufstellungCB = (AufstellungCBItem) m_jlAufstellungen
						.getSelectedValue();
				// "Aktuelle Aufstellung" nicht zu löschen!
				if (aufstellungCB.getText().equals(
						HOVerwaltung.instance().getLanguageString("AktuelleAufstellung"))
						|| aufstellungCB.getText().equals(
								HOVerwaltung.instance().getLanguageString("LetzteAufstellung"))) {
					m_jbAufstellungAnzeigen.setEnabled(true);
					m_jbAufstellungLoeschen.setEnabled(false);
					m_jbAufstellungSpeichern.setEnabled(true);
				}
				// Geladen
				else {
					m_jbAufstellungAnzeigen.setEnabled(true);
					m_jbAufstellungSpeichern.setEnabled(true);
					m_jbAufstellungLoeschen.setEnabled(true);
				}
				m_bVergleichAngestossen = true;
				m_clVergleichsAufstellung = aufstellungCB.duplicate();
				final Lineup old = HOVerwaltung.instance().getModel().getLineup();
				if (old != null) { // keep the same location (home / away /
									// derby)
					m_clVergleichsAufstellung.getAufstellung().setLocation(old.getLocation());
				}
			} else { // Keine Vergleich!
				m_jbAufstellungAnzeigen.setEnabled(false);
				m_jbAufstellungSpeichern.setEnabled(true);
				m_jbAufstellungLoeschen.setEnabled(false);

				m_clVergleichsAufstellung = null;
			}
			// gui.RefreshManager.instance ().doRefresh();
			HOMainFrame.instance().getAufstellungsPanel().getAufstellungsDetailPanel().refresh();
		}
	}

	/**
	 * Create list with lineups.
	 */
	private void createAufstellungsListe() {
		List<AufstellungCBItem> aufstellungsListe = loadAufstellungsListe();

		m_jlAufstellungen.removeListSelectionListener(this);

		final Object letzteMarkierung = m_jlAufstellungen.getSelectedValue();

		if (letzteMarkierung == null) {
			m_clAngezeigteAufstellung = m_clHRFNextAufstellung;
		}

		DefaultListModel listmodel;

		if (m_jlAufstellungen.getModel() instanceof DefaultListModel) {
			listmodel = (DefaultListModel) m_jlAufstellungen.getModel();
			listmodel.removeAllElements();
		} else {
			listmodel = new DefaultListModel();
		}

		// HRF Aufstellung
		if ((m_clHRFNextAufstellung != null) && (m_clHRFNextAufstellung.getAufstellung() != null)) {
			listmodel.addElement(m_clHRFNextAufstellung);
		}

		if ((m_clHRFLastAufstellung != null) && (m_clHRFLastAufstellung.getAufstellung() != null)) {
			listmodel.addElement(m_clHRFLastAufstellung);
		}

		// Temporäre geladene Aufstellungen
		for (int i = 0; i < aufstellungsListe.size(); i++) {
			listmodel.addElement(aufstellungsListe.get(i));
		}

		m_jlAufstellungen.setModel(listmodel);

		if (letzteMarkierung != null) {
			m_jlAufstellungen.setSelectedValue(letzteMarkierung, true);
		}

		m_jlAufstellungen.addListSelectionListener(this);
	}

	/**
	 * Initialize the GUI components.
	 */
	private void initComponents() {
		setLayout(new BorderLayout());

		// add( new JLabel( model.HOVerwaltung.instance().getLanguageString(
		// "VergleichsHRF" ) ), BorderLayout.NORTH );
		m_jlAufstellungen.setOpaque(false);

		if ("Classic".equals(UserParameter.instance().skin)) {
			m_jlAufstellungen.setCellRenderer(new AufstellungsListRenderer());
		} else {
			m_jlAufstellungen.setCellRenderer(new LineupListRenderer(m_jlAufstellungen));
		}
		m_jlAufstellungen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_jlAufstellungen.addListSelectionListener(this);
		m_jlAufstellungen.addMouseListener(this);
		add(new JScrollPane(m_jlAufstellungen), BorderLayout.CENTER);

		JPanel buttonPanel = new ImagePanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		m_jbAufstellungAnzeigen.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"AufstellungAnzeigen"));
		m_jbAufstellungAnzeigen.addActionListener(this);
		m_jbAufstellungAnzeigen.setEnabled(false);
		gbc.gridy = 0;
		buttonPanel.add(m_jbAufstellungAnzeigen, gbc);
		m_jbAufstellungSpeichern.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"AufstellungSpeichern"));
		m_jbAufstellungSpeichern.addActionListener(this);
		m_jbAufstellungSpeichern.setEnabled(true);
		gbc.gridy = 1;
		buttonPanel.add(m_jbAufstellungSpeichern, gbc);
		m_jbAufstellungLoeschen.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"AufstellungLoeschen"));
		m_jbAufstellungLoeschen.addActionListener(this);
		m_jbAufstellungLoeschen.setEnabled(false);
		gbc.gridy = 2;
		gbc.weighty = 1.0;
		buttonPanel.add(m_jbAufstellungLoeschen, gbc);

		GUIUtils.equalizeComponentSizes(this.m_jbAufstellungAnzeigen, this.m_jbAufstellungLoeschen,
				this.m_jbAufstellungSpeichern);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Load the linup list.
	 */
	private List<AufstellungCBItem> loadAufstellungsListe() {
		List<String> aufstellungsnamen = DBManager.instance().getUserAufstellungsListe();
		List<AufstellungCBItem> aufstellungsCBItems = new ArrayList<AufstellungCBItem>();

		for (int i = 0; i < aufstellungsnamen.size(); i++) {
			aufstellungsCBItems.add(new AufstellungCBItem(aufstellungsnamen.get(i).toString(),
					DBManager.instance().getAufstellung(Lineup.NO_HRF_VERBINDUNG,
							aufstellungsnamen.get(i).toString())));
		}

		return aufstellungsCBItems;
	}
}
