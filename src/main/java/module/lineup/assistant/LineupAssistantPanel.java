package module.lineup.assistant;

import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.*;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.Helper;
import module.lineup.*;
import module.lineup.lineup.PlayerPositionPanel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import static module.lineup.LineupPanel.TITLE_FG;
import static core.util.Helper.getTranslation;


//TODO check if it needs to implement Refreshable
public class LineupAssistantPanel extends ImagePanel implements Refreshable, ActionListener, ItemListener {

	UserParameter userParameter = core.model.UserParameter.instance();

	private final JCheckBox m_jcbxNotLast = new JCheckBox("",	userParameter.aufstellungsAssistentPanel_notLast);

	private final CBItem[] INCLUDE_EXCLUDE = {
			new CBItem(getTranslation("ls.module.lineup.exclude"), 0),
			new CBItem(getTranslation("ls.module.lineup.include"), 1),
 };

	private final JComboBox<CBItem> m_jcbIncludeExclude = new JComboBox<>(INCLUDE_EXCLUDE);

	private final JComboBox<String> m_jcbGroups = new JComboBox<>(GroupTeamFactory.TEAMSMILIES);
	private final JCheckBox m_jcbxFilterPlayerPositionCB = new JCheckBox("", userParameter.aufstellungsAssistentPanel_cbfilter);
	private final JCheckBox m_jcbxConsiderForm = new JCheckBox("", userParameter.aufstellungsAssistentPanel_form);
	private final JCheckBox m_jcbxConsiderInjuredPlayers = new JCheckBox("", userParameter.aufstellungsAssistentPanel_verletzt);
	private final JCheckBox m_jcbxConsiderSuspendedPlayers = new JCheckBox("", userParameter.aufstellungsAssistentPanel_gesperrt);
	private final JCheckBox m_jcbxIdealPositionFirst = new JCheckBox("", userParameter.aufstellungsAssistentPanel_idealPosition);

	private final CBItem[] PRIORITIES = {
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
	private final JComboBox<CBItem> m_jcbPriority = new JComboBox<>(PRIORITIES);


	private final JButton m_jbClearLineup = new JButton();
	private final JButton m_jbStartAssistant = new JButton();


	private HashMap<PlayerPositionPanel, LineupAssistantSelectorOverlay> positions = new HashMap<>();

	// UI items for additions to the LineupPositionsPanel

	JLabel infoLabel = null;
	JButton overlayOk = null;
	JButton overlayCancel = null;


	public LineupAssistantPanel() {
		initComponents();
	}

	public final boolean isExcludeLastMatch() {
		return m_jcbxNotLast.isSelected();
	}

	public final boolean isConsiderForm() {
		return m_jcbxConsiderForm.isSelected();
	}

	public final boolean isIgnoreSuspended() {
		return m_jcbxConsiderSuspendedPlayers.isSelected();
	}

	public final String getGroup() {
		return Objects.requireNonNull(m_jcbGroups.getSelectedItem()).toString();
	}

	public final boolean isGroupFilter() {
		return m_jcbxFilterPlayerPositionCB.isSelected();
	}

	public final void setGroupFilter(boolean isSelected) {m_jcbxFilterPlayerPositionCB.setSelected(isSelected);}

	public final boolean isIdealPositionZuerst() {
		return m_jcbxIdealPositionFirst.isSelected();
	}

	public final boolean isSelectedGroupExcluded() {
		return m_jcbIncludeExclude.getSelectedIndex() == 0;
	}

	public final int getOrder() {
		return ((CBItem) Objects.requireNonNull(m_jcbPriority.getSelectedItem())).getId();
	}

	public final boolean isIgnoreInjured() {
		return m_jcbxConsiderInjuredPlayers.isSelected();
	}

	public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		final HOModel hoModel = HOVerwaltung.instance().getModel();
		final HOMainFrame mainFrame = core.gui.HOMainFrame.instance();

		if (actionEvent.getSource().equals(m_jbClearLineup)) {
			// Empty all positions
			hoModel.getLineupWithoutRatingRecalc().resetStartingLineup();
			hoModel.getLineupWithoutRatingRecalc().resetPositionOrders();
			hoModel.getLineupWithoutRatingRecalc().resetSubstituteBench();
			hoModel.getLineupWithoutRatingRecalc().setKicker(0);
			hoModel.getLineupWithoutRatingRecalc().setCaptain(0);
			HOMainFrame.instance().setInformation(HOVerwaltung.instance().getLanguageString("Aufstellung_geloescht"));
			mainFrame.getLineupPanel().update();
		}
		else if (actionEvent.getSource().equals(m_jbStartAssistant)) {
			displayGUI();
		}
		else if (actionEvent.getSource().equals(m_jcbxFilterPlayerPositionCB) || actionEvent.getSource().equals(m_jcbxNotLast)) {
			mainFrame.getLineupPanel().getLineupPositionsPanel().refresh();
		}
		else if (actionEvent.getSource().equals(m_jcbGroups) || actionEvent.getSource().equals(m_jcbIncludeExclude)) {
			// Only if filter active
			if (m_jcbxFilterPlayerPositionCB.isSelected()) {
				mainFrame.getLineupPanel().getLineupPositionsPanel().refresh();
			}
		}
		else if (actionEvent.getSource().equals(overlayOk)) {

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

		final List<Player> vPlayer = new Vector<>();
		final List<Player> allePlayer = hoModel.getCurrentPlayers();

		for ( Player player: allePlayer){

			//If the player is eligible to play and either all groups are selected or the one to which the player belongs
			if (player.getCanBeSelectedByAssistant()
					&& (((this.getGroup().trim().equals("") || player.getTeamInfoSmilie().equals(
							this.getGroup())) && !isSelectedGroupExcluded()) || (!player
							.getTeamInfoSmilie().equals(this.getGroup()) && isSelectedGroupExcluded()))) {
				boolean include = true;
				if ( m_jcbxNotLast.isSelected()) {
					var previousLineup = hoModel.getPreviousLineup();
					if (previousLineup != null && previousLineup.isPlayerInStartingEleven(player.getPlayerID())) {
						include = false;
					}
				}
				if (include) {
					vPlayer.add(player);
				}
			}
		}

		hoModel.getLineup().optimizeLineup(vPlayer,
				(byte) ((CBItem) Objects.requireNonNull(m_jcbPriority.getSelectedItem())).getId(),
				m_jcbxConsiderForm.isSelected(), m_jcbxIdealPositionFirst.isSelected(),
				m_jcbxConsiderInjuredPlayers.isSelected(), m_jcbxConsiderSuspendedPlayers.isSelected());
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
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 2, 2, 2);

		constraints.gridx = 5;
		constraints.gridy = 4;
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
		constraints.gridy = 3;
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

		constraints.gridy = 2;
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
		for (Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay> entry : positions
				.entrySet()) {
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
		HashMap<Integer, Boolean> returnMap = new HashMap<>();
		for (Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay> entry : positions
				.entrySet()) {
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
		for (Map.Entry<PlayerPositionPanel, LineupAssistantSelectorOverlay> entry : positions
				.entrySet()) {
			if (entry.getKey().getPositionsID() == position) {
				return entry.getValue().isSelected();
			}
		}
		return false;
	}

	private void initComponents() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;

		setLayout(layout);

		// Line 1 ===================================================
		constraints.gridx = 0;
		constraints.gridy = 0;
		JLabel label = new JLabel(getTranslation("NotLast_aufstellen"));
		label.setToolTipText(getTranslation("tt_AufstellungsAssistent_NotLast"));
		addLabel(constraints, layout, label);

		constraints.gridx = 1;
		m_jcbxNotLast.setToolTipText(getTranslation("tt_AufstellungsAssistent_NotLast"));
		m_jcbxNotLast.addActionListener(this);
		layout.setConstraints(m_jcbxNotLast, constraints);
		add(m_jcbxNotLast);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		add(new JLabel(""));

//		Line 2 =======================================================
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0.0;
		addLabel(constraints, layout, new JLabel(getTranslation("ls.module.lineup.assistant.group")));

		constraints.gridx = 1;
		constraints.gridwidth = 3;
		constraints.anchor = GridBagConstraints.WEST;
		JPanel jpGroupSelection = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Helper.setComboBoxFromID(m_jcbIncludeExclude, userParameter.lineupAssistentPanel_include_group ? 1 : 0);
		m_jcbIncludeExclude.addActionListener(this);
		m_jcbIncludeExclude.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		jpGroupSelection.add(m_jcbIncludeExclude);
		m_jcbGroups.setSelectedItem(userParameter.aufstellungsAssistentPanel_gruppe);
		m_jcbGroups.setRenderer(new core.gui.comp.renderer.SmilieListCellRenderer());
		m_jcbGroups.addActionListener(this);
		jpGroupSelection.add(m_jcbGroups);
		layout.setConstraints(jpGroupSelection, constraints);
		add(jpGroupSelection);

		// Line 3 ===============================================
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		label = new JLabel(getTranslation("ls.module.lineup.sync_lineup_panel"));
		label.setToolTipText(getTranslation("ls.module.lineup.sync_lineup_panel.tooltip"));
		addLabel(constraints, layout, label);

		constraints.gridx = 1;
		m_jcbxFilterPlayerPositionCB.setToolTipText(getTranslation("ls.module.lineup.sync_lineup_panel.tooltip"));
		m_jcbxFilterPlayerPositionCB.addActionListener(this);
		layout.setConstraints(m_jcbxFilterPlayerPositionCB, constraints);
		add(m_jcbxFilterPlayerPositionCB);

		// Line 4 (break line)
		constraints.gridx = 0;
		constraints.gridy = 3;
		addLabel(constraints, layout, new JLabel(" "));

		// Line 5 ===============================================
		constraints.gridx = 0;
		constraints.gridy = 4;
		label = new JLabel(getTranslation("Form_beruecksichtigen"));
		label.setToolTipText(getTranslation("tt_AufstellungsAssistent_Form"));
		addLabel(constraints, layout, label);

		constraints.gridx = 1;
		m_jcbxConsiderForm.setToolTipText(getTranslation("tt_AufstellungsAssistent_Form"));
		layout.setConstraints(m_jcbxConsiderForm, constraints);
		add(m_jcbxConsiderForm);

		constraints.gridx = 2;
		label = new JLabel(getTranslation("Verletze_aufstellen"));
		label.setToolTipText(getTranslation("tt_AufstellungsAssistent_Verletzte"));
		addLabel(constraints, layout, label);

		constraints.gridx = 3;
		m_jcbxConsiderInjuredPlayers.setToolTipText(getTranslation("tt_AufstellungsAssistent_Verletzte"));
		layout.setConstraints(m_jcbxConsiderInjuredPlayers, constraints);
		add(m_jcbxConsiderInjuredPlayers);

		// Line 6 ===============================================
		constraints.gridx = 0;
		constraints.gridy = 5;
		label = new JLabel(getTranslation("Gesperrte_aufstellen"));
		label.setToolTipText(getTranslation("tt_AufstellungsAssistent_Gesperrte"));
		addLabel(constraints, layout, label);

		constraints.gridx = 1;
		m_jcbxConsiderSuspendedPlayers.setToolTipText(getTranslation("tt_AufstellungsAssistent_Gesperrte"));
		layout.setConstraints(m_jcbxConsiderSuspendedPlayers, constraints);
		add(m_jcbxConsiderSuspendedPlayers);

		constraints.gridx = 2;
		label = new JLabel(getTranslation("Idealposition_zuerst"));
		label.setToolTipText(getTranslation("tt_AufstellungsAssistent_Idealposition"));
		addLabel(constraints, layout, label);

		constraints.gridx = 3;
		m_jcbxIdealPositionFirst.setToolTipText(getTranslation("tt_AufstellungsAssistent_Idealposition"));
		layout.setConstraints(m_jcbxIdealPositionFirst, constraints);
		add(m_jcbxIdealPositionFirst);

		// Line 7 (break line)  =============================================================================
		constraints.gridx = 0;
		constraints.gridy = 6;
		addLabel(constraints, layout, new JLabel(" "));

		// Line 8 =============================================================================
		constraints.gridx = 0;
		constraints.gridy = 7;
		addLabel(constraints, layout, new JLabel(getTranslation("ls.module.lineup.assistant.priority")));

		constraints.gridx = 1;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		m_jcbPriority.setToolTipText(getTranslation("tt_AufstellungsAssistent_Reihenfolge"));
		core.util.Helper.setComboBoxFromID(m_jcbPriority, userParameter.aufstellungsAssistentPanel_reihenfolge);
		layout.setConstraints(m_jcbPriority, constraints);
		m_jcbPriority.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
		add(m_jcbPriority);

		// Line 9 =============================================================================
		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.gridwidth = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		JPanel jpButtons = new JPanel(new FlowLayout());

		m_jbStartAssistant.setIcon(ImageUtilities.getStartAssistantIcon(30, HOColorName.LINEUP_COLOR, HOColorName.START_ASSISTANT));
		m_jbClearLineup.setIcon(ImageUtilities.getClearLineupIcon(30, HOColorName.LINEUP_COLOR, HOColorName.CLEAR_LINEUP));

		JButton[] buttons = new JButton[]{m_jbClearLineup, m_jbStartAssistant};
		String[] tooltips = new String[]{"Aufstellung_leeren", "Assistent_starten"};

		for(int i = 0; i < buttons.length; i++){
			buttons[i].setToolTipText(getTranslation(tooltips[i]));
			buttons[i].addActionListener(this);
			buttons[i].setMargin(new Insets(6, 6, 6, 6));
			buttons[i].setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
			jpButtons.add(buttons[i]);
		}

		layout.setConstraints(jpButtons, constraints);
		add(jpButtons);

		core.gui.RefreshManager.instance().registerRefreshable(this);
	}


	private void addLabel(GridBagConstraints constraints, GridBagLayout layout, JLabel label) {
		label.setForeground(TITLE_FG);
		label.setFont(getFont().deriveFont(Font.BOLD));
		label.setHorizontalAlignment(SwingConstants.LEFT);
		layout.setConstraints(label, constraints);
		add(label);
	}

	@Override
	public void reInit() {}

	@Override
	public void refresh() {
		reInit();
	}}
