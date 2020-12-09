package module.lineup.assistant;

import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.LineupCBItem;
import core.gui.theme.*;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;
import module.lineup.*;
import module.lineup.lineup.PlayerPositionPanel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;
import javax.swing.*;

import static core.util.Helper.getTranslation;


//TODO check if it needs to implement Refreshable
public class LineupAssistantPanel extends ImagePanel implements Refreshable, ActionListener, ItemListener {

	UserParameter userParameter = core.model.UserParameter.instance();

	private final static Color TITLE_FG = ThemeManager.getColor(HOColorName.BLUE);

	private final JCheckBox m_jcbxNot = new JCheckBox(getTranslation("Not"), userParameter.aufstellungsAssistentPanel_not);
	private final JComboBox<String> m_jcbGroups = new JComboBox<>(GroupTeamFactory.TEAMSMILIES);

	private final JButton m_jbLoeschen = new JButton(ThemeManager.getIcon(HOIconName.CLEARASSIST));
	private final JButton m_jbOK = new JButton(ThemeManager.getIcon(HOIconName.STARTASSIST));
	private final JButton m_jbReserveLoeschen = new JButton(
			ThemeManager.getIcon(HOIconName.CLEARRESERVE));
	private final JButton m_jbClearPostionOrders = new JButton(
			ThemeManager.getIcon(HOIconName.CLEARPOSORDERS));
	private final JCheckBox m_jchForm = new JCheckBox(HOVerwaltung.instance().getLanguageString(
			"Form_beruecksichtigen"),
			core.model.UserParameter.instance().aufstellungsAssistentPanel_form);
	private final JCheckBox m_jchGesperrte = new JCheckBox(HOVerwaltung.instance()
			.getLanguageString("Gesperrte_aufstellen"),
			core.model.UserParameter.instance().aufstellungsAssistentPanel_gesperrt);
	private final JCheckBox m_jchIdealPosition = new JCheckBox(HOVerwaltung.instance()
			.getLanguageString("Idealposition_zuerst"),
			core.model.UserParameter.instance().aufstellungsAssistentPanel_idealPosition);
	private final JCheckBox m_jchLast = new JCheckBox(HOVerwaltung.instance().getLanguageString(
			"NotLast_aufstellen"),
			core.model.UserParameter.instance().aufstellungsAssistentPanel_notLast);
	private final JCheckBox m_jchListBoxGruppenFilter = new JCheckBox(HOVerwaltung.instance()
			.getLanguageString("ListBoxGruppenFilter"),
			core.model.UserParameter.instance().aufstellungsAssistentPanel_cbfilter);
	private final JCheckBox m_jchVerletzte = new JCheckBox(HOVerwaltung.instance()
			.getLanguageString("Verletze_aufstellen"),
			core.model.UserParameter.instance().aufstellungsAssistentPanel_verletzt);


	private final CBItem[] REIHENFOLGE = {
			new CBItem(HOVerwaltung.instance().getLanguageString("AW-MF-ST"),
					LineupAssistant.AW_MF_ST),

			new CBItem(HOVerwaltung.instance().getLanguageString("AW-ST-MF"),
					LineupAssistant.AW_ST_MF),

			new CBItem(HOVerwaltung.instance().getLanguageString("MF-AW-ST"),
					LineupAssistant.MF_AW_ST),

			new CBItem(HOVerwaltung.instance().getLanguageString("MF-ST-AW"),
					LineupAssistant.MF_ST_AW),

			new CBItem(HOVerwaltung.instance().getLanguageString("ST-AW-MF"),
					LineupAssistant.ST_AW_MF),

			new CBItem(HOVerwaltung.instance().getLanguageString("ST-MF-AW"),
					LineupAssistant.ST_MF_AW) };
	private JComboBox m_jcbReihenfolge = new JComboBox(REIHENFOLGE);
	private HashMap<PlayerPositionPanel, LineupAssistantSelectorOverlay> positions = new HashMap<PlayerPositionPanel, LineupAssistantSelectorOverlay>();

	// UI items for additions to the LineupPositionsPanel

	JLabel infoLabel = null;
	JButton overlayOk = null;
	JButton overlayCancel = null;


	public LineupAssistantPanel() {
		initComponents();
	}

	public final boolean isExcludeLastMatch() {
		return m_jchLast.isSelected();
	}

	public final boolean isConsiderForm() {
		return m_jchForm.isSelected();
	}

	public final boolean isIgnoreSuspended() {
		return m_jchGesperrte.isSelected();
	}

	public final String getGroup() {
		return Objects.requireNonNull(m_jcbGroups.getSelectedItem()).toString();
	}

	public final boolean isGroupFilter() {
		return m_jchListBoxGruppenFilter.isSelected();
	}

	public final boolean isIdealPositionZuerst() {
		return m_jchIdealPosition.isSelected();
	}

	public final boolean isNotGroup() {
		return m_jcbxNot.isSelected();
	}

	public final int getOrder() {
		return ((CBItem) m_jcbReihenfolge.getSelectedItem()).getId();
	}

	public final boolean isIgnoreInjured() {
		return m_jchVerletzte.isSelected();
	}

	public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		final HOModel hoModel = HOVerwaltung.instance().getModel();
		final HOMainFrame mainFrame = core.gui.HOMainFrame.instance();

		if (actionEvent.getSource().equals(m_jbLoeschen)) {
			// Alle Positionen leeren
			hoModel.getLineupWithoutRatingRecalc().resetAufgestellteSpieler();
			hoModel.getLineupWithoutRatingRecalc().setKicker(0);
			hoModel.getLineupWithoutRatingRecalc().setKapitaen(0);
			HOMainFrame.instance().setInformation(
							HOVerwaltung.instance().getLanguageString("Aufstellung_geloescht"));
			mainFrame.getLineupPanel().update();

			// gui.RefreshManager.instance ().doRefresh ();
		} else if (actionEvent.getSource().equals(m_jbClearPostionOrders)) {
			// event listener for clear positonal orders button
			hoModel.getLineupWithoutRatingRecalc().resetPositionOrders();
			HOMainFrame.instance().setInformation(
							HOVerwaltung.instance().getLanguageString("Positional_orders_cleared"));
			mainFrame.getLineupPanel().update();

		} else if (actionEvent.getSource().equals(m_jbReserveLoeschen)) {
			hoModel.getLineupWithoutRatingRecalc().resetReserveBank();
			mainFrame.getLineupPanel().update();

			// gui.RefreshManager.instance ().doRefresh ();
		} else if (actionEvent.getSource().equals(m_jbOK)) {
			displayGUI();
		} else if (actionEvent.getSource().equals(m_jchListBoxGruppenFilter)
				|| actionEvent.getSource().equals(m_jchLast)) {
			mainFrame.getLineupPanel().getLineupPositionsPanel().refresh();
		} else if (actionEvent.getSource().equals(m_jcbGroups)
				|| actionEvent.getSource().equals(m_jcbxNot)) {
			// Nur wenn Filter aktiv
			if (m_jchListBoxGruppenFilter.isSelected()) {
				mainFrame.getLineupPanel().getLineupPositionsPanel().refresh();
			}
		} else if (actionEvent.getSource().equals(overlayOk)) {

			// Check that max 11 positions are sent
			Iterator<Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay>> it = positions
					.entrySet().iterator();
			int reds = 0;
			while (it.hasNext()) {
				if (!it.next().getValue().isSelected()) {
					reds++;
				}
			}
			if (reds < 3) {
				// We have more positions left than is allowed in the lineup.
				// Return.
				javax.swing.JOptionPane.showMessageDialog(HOMainFrame.instance()
						.getLineupPanel(),
						HOVerwaltung.instance().getLanguageString("lineupassist.Error"),
						HOVerwaltung.instance().getLanguageString("lineupassist.ErrorHeader"),
						javax.swing.JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			removeGUI();
			updateDefaultSelection();

			startAssistant(hoModel, mainFrame);

		} else if (actionEvent.getSource().equals(overlayCancel)) {
			removeGUI();
		}

	}

	@Override
	public final void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			// Wetter -> Refresh
			core.gui.HOMainFrame.instance().getLineupPanel().update();

			// gui.RefreshManager.instance ().doRefresh ();
		}
	}


	public void addToAssistant(PlayerPositionPanel positionPanel) {
		positions.put(positionPanel, null);
	}

	private void startAssistant(HOModel hoModel, HOMainFrame mainFrame) {

		// First, clear all positions that are not selected. We need to clear
		// the way.

		for (Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay> entry : positions
				.entrySet()) {
			if (!entry.getValue().isSelected()) {
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc()
						.setSpielerAtPosition(entry.getKey().getPositionsID(), 0);
			}
		}

		final List<Player> vPlayer = new Vector<Player>();
		final List<Player> allePlayer = hoModel.getCurrentPlayers();

		for ( Player player: allePlayer){

			//If the player is eligible to play and either all groups are selected or the one to which the player belongs
			if (player.getCanBeSelectedByAssistant()
					&& (((this.getGroup().trim().equals("") || player.getTeamInfoSmilie().equals(
							this.getGroup())) && !m_jcbxNot.isSelected()) || (!player
							.getTeamInfoSmilie().equals(this.getGroup()) && m_jcbxNot.isSelected()))) {
				boolean include = true;
				final LineupCBItem lastLineup = LineupsComparisonHistoryPanel
						.getLastLineup();

				if (m_jchLast.isSelected()
						&& (lastLineup != null)
						&& lastLineup.getAufstellung()
								.isPlayerInStartingEleven(player.getSpielerID())) {
					include = false;
					HOLogger.instance().log(getClass(), "Exclude: " + player.getFullName());
				}

				if (include) {
					vPlayer.add(player);
				}
			}
		}

		hoModel.getLineup().optimizeLineup(vPlayer,
				(byte) ((CBItem) Objects.requireNonNull(m_jcbReihenfolge.getSelectedItem())).getId(),
				m_jchForm.isSelected(), m_jchIdealPosition.isSelected(),
				m_jchVerletzte.isSelected(), m_jchGesperrte.isSelected(),
				core.model.UserParameter.instance().WetterEffektBonus);
		mainFrame.setInformation(
				HOVerwaltung.instance().getLanguageString("Autoaufstellung_fertig"));
		mainFrame.getLineupPanel().update();

		// gui.RefreshManager.instance ().doRefresh ();
	}

	private void displayGUI() {

		// Add overlays to player panels

		for (Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay> entry : positions
				.entrySet()) {
			if (entry.getValue() == null) {
				boolean selected = true;
				LineupAssistantSelectorOverlay laso = new LineupAssistantSelectorOverlay();
				Map<String, String> upValues = UserParameter.instance().getValues();
				if (UserParameter.instance().assistantSaved) {
					selected = UserParameter.instance().getBooleanValue(upValues,
							"assistant" + entry.getKey().getPositionsID());
				} else {
					int posId = entry.getKey().getPositionsID();
					if ((posId == IMatchRoleID.centralForward)
							|| (posId == IMatchRoleID.centralInnerMidfield)
							|| (posId == IMatchRoleID.middleCentralDefender)) {
						selected = false;
					}
				}

				laso.setSelected(selected);
				entry.setValue(laso);
			}
			entry.getKey().addAssistantOverlay(entry.getValue());
		}

		// Add two buttons and a label

		JLayeredPane posPanel = HOMainFrame.instance().getLineupPanel()
				.getLineupPositionsPanel().getCenterPanel();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 2, 2, 2);

		constraints.gridx = 5;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		if (infoLabel == null) {
			infoLabel = new JLabel();
			infoLabel.setText(HOVerwaltung.instance().getLanguageString("lineupassist.Info"));
			infoLabel.setOpaque(true);
			infoLabel.setHorizontalAlignment(JLabel.CENTER);
			infoLabel.setFont(getFont().deriveFont(Font.BOLD));
		}
		posPanel.add(infoLabel, constraints, 2);

		constraints.gridx = 6;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;

		if (overlayOk == null) {
			overlayOk = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.ok"));
			overlayOk.setFont(new Font("serif", Font.BOLD, 16));
			overlayOk.setBackground(ThemeManager.getColor(HOColorName.BUTTON_ASSIST_OK_BG));
			overlayOk.setForeground(ImageUtilities.getColorForContrast(HOColorName.BUTTON_ASSIST_OK_BG));
			overlayOk.addActionListener(this);
		}
		posPanel.add(overlayOk, constraints, 2);

		constraints.gridy = 1;
		if (overlayCancel == null) {
			overlayCancel = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
			overlayCancel.addActionListener(this);
			overlayCancel.setFont(new Font("serif", Font.BOLD, 16));
			overlayCancel.setBackground(ThemeManager.getColor(HOColorName.BUTTON_ASSIST_CANCEL_BG));
			overlayCancel.setForeground(ImageUtilities.getColorForContrast(HOColorName.BUTTON_ASSIST_CANCEL_BG));
		}
		posPanel.add(overlayCancel, constraints, 2);

		posPanel.revalidate();

	}

	private void removeGUI() {
		// Remove overlays
		Iterator<Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay>> it = positions
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay> entry = it.next();
			entry.getKey().removeAssistantOverlay(entry.getValue());
		}

		// Remove buttons and labels
		JLayeredPane pane = HOMainFrame.instance().getLineupPanel()
				.getLineupPositionsPanel().getCenterPanel();

		pane.remove(infoLabel);
		pane.remove(overlayCancel);
		pane.remove(overlayOk);

		HOMainFrame.instance().getLineupPanel().repaint();

	}

	public Map<Integer, Boolean> getPositionStatuses() {
		HashMap<Integer, Boolean> returnMap = new HashMap<Integer, Boolean>();
		Iterator<Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay>> it = positions
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay> entry = it.next();
			returnMap.put(entry.getKey().getPositionsID(), entry.getValue().isSelected());
		}

		return returnMap;
	}

	private void updateDefaultSelection() {
		// There should be a more sensible way to do this. Merging maps or
		// something...
		// But brute force and ignorance should never be underestimated.

		UserParameter.instance().assistant101 = getStatusForPosition(101);
		UserParameter.instance().assistant102 = getStatusForPosition(102);
		UserParameter.instance().assistant103 = getStatusForPosition(103);
		UserParameter.instance().assistant104 = getStatusForPosition(104);
		UserParameter.instance().assistant105 = getStatusForPosition(105);
		UserParameter.instance().assistant106 = getStatusForPosition(106);
		UserParameter.instance().assistant107 = getStatusForPosition(107);
		UserParameter.instance().assistant108 = getStatusForPosition(108);
		UserParameter.instance().assistant109 = getStatusForPosition(109);
		UserParameter.instance().assistant110 = getStatusForPosition(110);
		UserParameter.instance().assistant111 = getStatusForPosition(111);
		UserParameter.instance().assistant112 = getStatusForPosition(112);
		UserParameter.instance().assistant113 = getStatusForPosition(113);
		UserParameter.instance().assistantSaved = true;
	}

	private boolean getStatusForPosition(int position) {
		Iterator<Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay>> it = positions
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay> entry = it.next();
			if (entry.getKey().getPositionsID() == position) {
				return entry.getValue().isSelected();
			}
		}
		return false;
	}

	private void initComponents() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
//		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.NONE;;


		setLayout(layout);

		// Line 1
		constraints.gridx = 0;
		int yPos = 0;
		addLabel(constraints, layout, new JLabel(getTranslation("ls.lineup.asssitant.goup")), yPos);

		constraints.gridx = 1;
		m_jcbxNot.setToolTipText(getTranslation("tt_AufstellungsAssistent_Not"));
//		m_jchNot.setOpaque(false);
		m_jcbxNot.addActionListener(this);
		layout.setConstraints(m_jcbxNot, constraints);
		add(m_jcbxNot);

		constraints.gridx = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		m_jcbGroups.setToolTipText(getTranslation("tt_AufstellungsAssistent_Gruppe"));
		m_jcbGroups.setSelectedItem(userParameter.aufstellungsAssistentPanel_gruppe);
		m_jcbGroups.setRenderer(new core.gui.comp.renderer.SmilieListCellRenderer());
		m_jcbGroups.addActionListener(this);
		layout.setConstraints(m_jcbGroups, constraints);
		add(m_jcbGroups);
//
//		m_jchListBoxGruppenFilter.setToolTipText(hoVerwaltung
//				.getLanguageString("tt_AufstellungsAssistent_GruppeFilter"));
//		m_jchListBoxGruppenFilter.setOpaque(false);
//		m_jchListBoxGruppenFilter.addActionListener(this);
//		panel.add(m_jchListBoxGruppenFilter);
//
//		m_jcbReihenfolge.setToolTipText(hoVerwaltung
//				.getLanguageString("tt_AufstellungsAssistent_Reihenfolge"));
//		core.util.Helper.setComboBoxFromID(m_jcbReihenfolge,
//				core.model.UserParameter.instance().aufstellungsAssistentPanel_reihenfolge);
//		panel.add(m_jcbReihenfolge);
//		m_jchIdealPosition.setToolTipText(hoVerwaltung
//				.getLanguageString("tt_AufstellungsAssistent_Idealposition"));
//		m_jchIdealPosition.setOpaque(false);
//		panel.add(m_jchIdealPosition);
//		m_jchForm.setToolTipText(hoVerwaltung.getLanguageString("tt_AufstellungsAssistent_Form"));
//		m_jchForm.setOpaque(false);
//		panel.add(m_jchForm);
//		m_jchVerletzte.setToolTipText(hoVerwaltung
//				.getLanguageString("tt_AufstellungsAssistent_Verletzte"));
//		m_jchVerletzte.setOpaque(false);
//		panel.add(m_jchVerletzte);
//		m_jchGesperrte.setToolTipText(hoVerwaltung
//				.getLanguageString("tt_AufstellungsAssistent_Gesperrte"));
//		m_jchGesperrte.setOpaque(false);
//		panel.add(m_jchGesperrte);
//		m_jchLast
//				.setToolTipText(hoVerwaltung.getLanguageString("tt_AufstellungsAssistent_NotLast"));
//		m_jchLast.setOpaque(false);
//		m_jchLast.addActionListener(this);
//		panel.add(m_jchLast);
//
//		add(panel, BorderLayout.CENTER);
//
//		panel = new JPanel();
//		panel.setOpaque(false);
//		m_jbLoeschen.setPreferredSize(new Dimension(28, 28));
//		m_jbLoeschen.setToolTipText(hoVerwaltung.getLanguageString("Aufstellung_leeren"));
//		m_jbLoeschen.addActionListener(this);
//		panel.add(m_jbLoeschen);
//		m_jbClearPostionOrders.setPreferredSize(new Dimension(28, 28));
//		m_jbClearPostionOrders.setToolTipText(hoVerwaltung
//				.getLanguageString("Clear_positional_orders"));
//		m_jbClearPostionOrders.addActionListener(this);
//		panel.add(m_jbClearPostionOrders);
//		m_jbReserveLoeschen.setPreferredSize(new Dimension(28, 28));
//		m_jbReserveLoeschen.setToolTipText(hoVerwaltung.getLanguageString("Reservebank_leeren"));
//		m_jbReserveLoeschen.addActionListener(this);
//		panel.add(m_jbReserveLoeschen);
//		m_jbOK.setPreferredSize(new Dimension(28, 28));
//		m_jbOK.setToolTipText(hoVerwaltung.getLanguageString("Assistent_starten"));
//		m_jbOK.addActionListener(this);
//		panel.add(m_jbOK);
//		add(panel, BorderLayout.SOUTH);
//
//		core.gui.RefreshManager.instance().registerRefreshable(this);
	}

//	public static List<String> asList(String str) {
//		String[] pieces = str.split(";");
//		return Arrays.asList(pieces);
//	}


	private void addLabel(GridBagConstraints constraints, GridBagLayout layout, JLabel label, int y) {
		label.setForeground(TITLE_FG);
		label.setFont(getFont().deriveFont(Font.BOLD));
		label.setHorizontalAlignment(SwingConstants.LEFT);
		constraints.gridx = 0;
		constraints.gridy = y;
		layout.setConstraints(label, constraints);
		add(label);
	}

	@Override
	public void reInit() {
	}

	@Override
	public void refresh() {
		reInit();
	}}
