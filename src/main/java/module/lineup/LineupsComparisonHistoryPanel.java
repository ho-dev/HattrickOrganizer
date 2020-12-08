package module.lineup;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.LineupCBItem;
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


// Panel to store Lineups or to compare them with others
public class LineupsComparisonHistoryPanel extends ImagePanel implements
		core.gui.Refreshable, ListSelectionListener, ActionListener, MouseListener {

	private static LineupCBItem m_clCurrentLineup;
	private static LineupCBItem m_clComparativeLineup;
	private static LineupCBItem m_clHRFNextLineup;
	private static LineupCBItem m_clHRFLastLineup;
	private static boolean m_bIsCompared;
	private final JButton m_jbShowLineup = new JButton(getTranslation("AufstellungAnzeigen"));
	private final JButton m_jbDeleteLineup = new JButton(getTranslation("AufstellungLoeschen"));
	private final JButton m_jbSaveLineup = new JButton(getTranslation("AufstellungSpeichern"));
	private final JList<LineupCBItem> m_jlLineups = new JList<>();


	 // constructor
	public LineupsComparisonHistoryPanel() {
		initComponents();

		RefreshManager.instance().registerRefreshable(this);

		// to avoid NPE
		try {
			m_clHRFNextLineup = new LineupCBItem(getTranslation("AktuelleAufstellung"), HOVerwaltung.instance().getModel()
					.getLineupWithoutRatingRecalc().duplicate());
			m_clHRFLastLineup = new LineupCBItem(HOVerwaltung.instance()
					.getLanguageString("LetzteAufstellung"), HOVerwaltung.instance().getModel()
					.getPreviousLineup().duplicate());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "Err: " + e);
		}
		createAufstellungsListe();
	}

	/**
	 * Ist die Übergebene Aufstellung angezeigt?
	 */
	public static boolean isAngezeigt(LineupCBItem aufstellung) {
		if (aufstellung != null) {
			return aufstellung.equals(m_clCurrentLineup);
		} else {
			return false;
		}
	}

	/**
	 * Setzt die angezeige Aufstellung
	 */
	public static void setAngezeigteAufstellung(LineupCBItem aufstellung) {
		m_clCurrentLineup = aufstellung.duplicate();
	}

	/**
	 * Setzt die HRFAufstellung nach dem Import einen HRFs
	 */
	public static void setHRFAufstellung(Lineup nextAufstellung, Lineup lastAufstellung) {
		if (nextAufstellung != null) {
			m_clHRFNextLineup = new LineupCBItem(HOVerwaltung.instance()
					.getLanguageString("AktuelleAufstellung"), nextAufstellung.duplicate());
		}

		if (lastAufstellung != null) {
			m_clHRFLastLineup = new LineupCBItem(HOVerwaltung.instance()
					.getLanguageString("LetzteAufstellung"), lastAufstellung.duplicate());
		}
	}

	/**
	 * Returns Last Lineup
	 */
	public static LineupCBItem getLastLineup() {
		return m_clHRFLastLineup;
	}

	/**
	 * Wird vom AufstellungsDetailPanel aufgerufen, um mit der
	 * Vergleichsaufstellung anzuzeigen. Wird danach wieder auf false gesetzt
	 */
	public static boolean isVergleichgefordert() {
		final boolean vergleichgefordert = m_bIsCompared;
		m_bIsCompared = false;
		return vergleichgefordert;
	}

	/**
	 * Gibt die VergleichsAufstellung zurück
	 */
	public static LineupCBItem getVergleichsAufstellung() {
		return m_clComparativeLineup;
	}

	/**
	 * Handle action events.
	 */
	@Override
	public final void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(m_jbShowLineup)) {
			// use selected lineup
			loadSelectedStoredLineup();
		} else if (actionEvent.getSource().equals(m_jbSaveLineup)) {
			saveLineup();
		} else if (actionEvent.getSource().equals(m_jbDeleteLineup)) {
			deleteLineup();
		}
		repaint();
	}

	private void saveLineup() {
		String aufstellungsname = "";
		if (m_jlLineups.getSelectedIndex() > 1) {
			aufstellungsname = ((LineupCBItem) m_jlLineups.getSelectedValue()).getText();
		}
		final int x = HOMainFrame.instance().getLocation().x
				+ HOMainFrame.instance().getSize().width;
		final int y = HOMainFrame.instance().getLocation().y
				+ HOMainFrame.instance().getSize().height;
		AufstellungsNameDialog dlg = new AufstellungsNameDialog(HOMainFrame.instance(),
				aufstellungsname, HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc(), x, y);
		dlg.setVisible(true);

		if (!dlg.isCanceled()) {
			reInit();
		}
	}

	private void deleteLineup() {
		String aufstellungsname = "";
		if (m_jlLineups.getSelectedIndex() > 0) {
			aufstellungsname = ((LineupCBItem) m_jlLineups.getSelectedValue()).getText();
		}
		DBManager.instance().deleteAufstellung(Lineup.NO_HRF_VERBINDUNG,
				((LineupCBItem) m_jlLineups.getSelectedValue()).getText());
		HOMainFrame.instance().setInformation(
						HOVerwaltung.instance().getLanguageString("Aufstellung")
								+ " "
								+ ((LineupCBItem) m_jlLineups
										.getSelectedValue()).getText() + " "
								+ HOVerwaltung.instance().getLanguageString("geloescht"));
		File f = new File("Lineups/" + HOVerwaltung.instance().getModel().getBasics().getManager()
				+ "/" + aufstellungsname + ".dat");
		f.delete();
		((DefaultListModel) m_jlLineups.getModel()).removeElement(m_jlLineups
				.getSelectedValue());
	}

	private void loadSelectedStoredLineup() {
		final Lineup old = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
		m_clCurrentLineup = ((LineupCBItem) m_jlLineups.getSelectedValue())
				.duplicate();
		final Lineup new1 = m_clCurrentLineup.getAufstellung().duplicate();
		if (old != null) { // else we lose the location (home / away / derby)
							// here
			new1.setLocation(old.getLocation());
		}
		HOVerwaltung.instance().getModel().setLineup(new1);
		HOMainFrame.instance().getLineupPanel().update();
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
			// Lineup marked
			if ((m_jlLineups.getSelectedValue() != null)
					&& m_jlLineups.getSelectedValue() instanceof LineupCBItem) {
				final LineupCBItem aufstellungCB = (LineupCBItem) m_jlLineups
						.getSelectedValue();
				// Do not delete "Current setup"!
				if (aufstellungCB.getText().equals(
						HOVerwaltung.instance().getLanguageString("AktuelleAufstellung"))
						|| aufstellungCB.getText().equals(
								HOVerwaltung.instance().getLanguageString("LetzteAufstellung"))) {
					m_jbShowLineup.setEnabled(true);
					m_jbDeleteLineup.setEnabled(false);
					m_jbSaveLineup.setEnabled(true);
				}
				// Load
				else {
					m_jbShowLineup.setEnabled(true);
					m_jbSaveLineup.setEnabled(true);
					m_jbDeleteLineup.setEnabled(true);
				}
				m_bIsCompared = true;
				m_clComparativeLineup = aufstellungCB.duplicate();
				final Lineup old = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
				if (old != null) { // keep the same location (home / away / derby)
					m_clComparativeLineup.getAufstellung().setLocation(old.getLocation());
				}
			} else {  //No comparison!
				m_jbShowLineup.setEnabled(false);
				m_jbSaveLineup.setEnabled(true);
				m_jbDeleteLineup.setEnabled(false);

				m_clComparativeLineup = null;
			}
			// gui.RefreshManager.instance ().doRefresh();
			HOMainFrame.instance().getLineupPanel().getLineupSettingsPanel().refresh();
		}
	}

	/**
	 * Create list with lineups.
	 */
	private void createAufstellungsListe() {
		List<LineupCBItem> aufstellungsListe = loadAufstellungsListe();

		m_jlLineups.removeListSelectionListener(this);

		final Object letzteMarkierung = m_jlLineups.getSelectedValue();

		if (letzteMarkierung == null) {
			m_clCurrentLineup = m_clHRFNextLineup;
		}

		DefaultListModel listmodel;

		if (m_jlLineups.getModel() instanceof DefaultListModel) {
			listmodel = (DefaultListModel) m_jlLineups.getModel();
			listmodel.removeAllElements();
		} else {
			listmodel = new DefaultListModel();
		}

		// HRF Aufstellung
		if ((m_clHRFNextLineup != null) && (m_clHRFNextLineup.getAufstellung() != null)) {
			listmodel.addElement(m_clHRFNextLineup);
		}

		if ((m_clHRFLastLineup != null) && (m_clHRFLastLineup.getAufstellung() != null)) {
			listmodel.addElement(m_clHRFLastLineup);
		}

		// Temporäre geladene Aufstellungen
		for (int i = 0; i < aufstellungsListe.size(); i++) {
			listmodel.addElement(aufstellungsListe.get(i));
		}

		m_jlLineups.setModel(listmodel);

		if (letzteMarkierung != null) {
			m_jlLineups.setSelectedValue(letzteMarkierung, true);
		}

		m_jlLineups.addListSelectionListener(this);
	}

	/**
	 * Initialize the GUI components.
	 */
	private void initComponents() {
		setLayout(new BorderLayout());

		// add( new JLabel( model.HOVerwaltung.instance().getLanguageString(
		// "VergleichsHRF" ) ), BorderLayout.NORTH );
		m_jlLineups.setOpaque(false);

		if ("Classic".equals(UserParameter.instance().skin)) {
			m_jlLineups.setCellRenderer(new AufstellungsListRenderer());
		} else {
			m_jlLineups.setCellRenderer(new LineupListRenderer(m_jlLineups));
		}
		m_jlLineups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_jlLineups.addListSelectionListener(this);
		m_jlLineups.addMouseListener(this);
		add(new JScrollPane(m_jlLineups), BorderLayout.CENTER);

		JPanel buttonPanel = new ImagePanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		m_jbShowLineup.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"AufstellungAnzeigen"));
		m_jbShowLineup.addActionListener(this);
		m_jbShowLineup.setEnabled(false);
		gbc.gridy = 0;
		buttonPanel.add(m_jbShowLineup, gbc);
		m_jbSaveLineup.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"AufstellungSpeichern"));
		m_jbSaveLineup.addActionListener(this);
		m_jbSaveLineup.setEnabled(true);
		gbc.gridy = 1;
		buttonPanel.add(m_jbSaveLineup, gbc);
		m_jbDeleteLineup.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"AufstellungLoeschen"));
		m_jbDeleteLineup.addActionListener(this);
		m_jbDeleteLineup.setEnabled(false);
		gbc.gridy = 2;
		gbc.weighty = 1.0;
		buttonPanel.add(m_jbDeleteLineup, gbc);

		GUIUtils.equalizeComponentSizes(this.m_jbShowLineup, this.m_jbDeleteLineup,
				this.m_jbSaveLineup);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Load the linup list.
	 */
	private List<LineupCBItem> loadAufstellungsListe() {
		List<String> aufstellungsnamen = DBManager.instance().getUserAufstellungsListe();
		List<LineupCBItem> aufstellungsCBItems = new ArrayList<LineupCBItem>();

		for (int i = 0; i < aufstellungsnamen.size(); i++) {
			aufstellungsCBItems.add(new LineupCBItem(aufstellungsnamen.get(i).toString(),
					DBManager.instance().getAufstellung(Lineup.NO_HRF_VERBINDUNG,
							aufstellungsnamen.get(i).toString())));
		}

		return aufstellungsCBItems;
	}

	private String getTranslation(String inputText){
		return HOVerwaltung.instance().getLanguageString(inputText);
	}
}
