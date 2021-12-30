// %2601556114:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.constants.player.PlayerSkill;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyPanel;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.player.ISkillChange;
import core.model.player.Player;
import core.util.GUIUtils;
import module.training.PastTrainingManager;
import module.training.PlayerSkillChange;
import module.training.ui.model.ChangesTableModel;
import module.training.ui.model.ModelChange;
import module.training.ui.model.ModelChangeListener;
import module.training.ui.model.TrainingModel;
import module.training.ui.renderer.ChangeTableRenderer;
import module.training.ui.renderer.SkillupTypeTableCellRenderer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

/**
 * Shows a table of skillups, which can be filtered using the checkboxes on the
 * side.
 * 
 * @author NetHyperon
 */
public class AnalyzerPanel extends LazyPanel implements ActionListener {

	private static final long serialVersionUID = -2152169077412317532L;
	private static final String CMD_SELECT_ALL = "selectAll";
	private static final String CMD_CLEAR_ALL = "clearAll";
	private ButtonModel oldPlayers;
	private JPanel filterPanel;
	private JTable changesTable;
	private JCheckBox oldPlayersCheckBox;
	private Map<Integer, ButtonModel> buttonModels = new HashMap<Integer, ButtonModel>();
	private Map<Integer, List<PlayerSkillChange>> skillups;
	private Map<Integer, List<PlayerSkillChange>> skillupsOld;
	private final TrainingModel model;

	/**
	 * Creates a new AnalyzerPanel object.
	 */
	public AnalyzerPanel(TrainingModel model) {
		super();
		this.model = model;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (CMD_SELECT_ALL.equals(e.getActionCommand())) {
			setAllSelected(true);
		} else if (CMD_CLEAR_ALL.equals(e.getActionCommand())) {
			setAllSelected(false);
		}
		updateTableModel();
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		update();
		registerRefreshable(true);
		setNeedsRefresh(false);
	}

	@Override
	protected void update() {
		this.skillups = getSkillups(HOVerwaltung.instance().getModel().getCurrentPlayers());
		this.skillupsOld = getSkillups(HOVerwaltung.instance().getModel().getFormerPlayers());
		updateFilterPanel();
		updateTableModel();		
	}
	
	/**
	 * Sets the model for skill changes table.
	 */
	private void updateTableModel() {
		List<PlayerSkillChange> values = new ArrayList<PlayerSkillChange>();

		for (Iterator<Integer> iter = this.buttonModels.keySet().iterator(); iter.hasNext();) {
			Integer skillType = iter.next();
			ButtonModel bModel = this.buttonModels.get(skillType);

			if (this.skillups.containsKey(skillType) && bModel.isSelected()) {
				values.addAll(this.skillups.get(skillType));
			}

			if (this.oldPlayers.isSelected() && bModel.isSelected()
					&& this.skillupsOld.containsKey(skillType)) {
				values.addAll(this.skillupsOld.get(skillType));
			}
		}

		Collections.sort(values, new Comparator<PlayerSkillChange>() {
			@Override
			public int compare(PlayerSkillChange sc1, PlayerSkillChange sc2) {
				if (sc1.getSkillup().getHtSeason() > sc2.getSkillup().getHtSeason()) {
					return -1;
				} else if (sc1.getSkillup().getHtSeason() < sc2.getSkillup().getHtSeason()) {
					return 1;
				} else {
					if (sc1.getSkillup().getHtWeek() > sc2.getSkillup().getHtWeek()) {
						return -1;
					} else if (sc1.getSkillup().getHtWeek() < sc2.getSkillup().getHtWeek()) {
						return 1;
					} else {
						if ((sc1.getPlayer().equals(sc2.getPlayer()))
								&& (sc1.getSkillup().getType() == sc2.getSkillup().getType())) {
							if (sc1.getSkillup().getValue() > sc2.getSkillup().getValue()) {
								return -1;
							} else {
								return 1;
							}
						} else {
							return sc1.getPlayer().getFullName().compareTo(sc2.getPlayer().getFullName());
						}
					}
				}
			}
		});

		changesTable.setModel(new ChangesTableModel(values));
		changesTable.setDefaultRenderer(Object.class, new ChangeTableRenderer());
		changesTable.getTableHeader().setReorderingAllowed(false);
		changesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		changesTable.getColumnModel().getColumn(1).setPreferredWidth(50);
		changesTable.getColumnModel().getColumn(2).setPreferredWidth(150);
		changesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		changesTable.getColumnModel().getColumn(4).setPreferredWidth(100);

		// Hide column 5
		TableColumn tblColumn = changesTable.getTableHeader().getColumnModel().getColumn(5);
		tblColumn.setPreferredWidth(0);
		tblColumn.setMinWidth(0);
		tblColumn.setMaxWidth(0);

		// Hide column 6
		tblColumn = changesTable.getTableHeader().getColumnModel().getColumn(6);
		tblColumn.setPreferredWidth(0);
		tblColumn.setMinWidth(0);
		tblColumn.setMaxWidth(0);

		// Set own renderer instance for skillup column.
		changesTable.getTableHeader().getColumnModel().getColumn(3)
				.setCellRenderer(new SkillupTypeTableCellRenderer());
	}

	private void addListeners() {	
		this.model.addModelChangeListener(new ModelChangeListener() {

			@Override
			public void modelChanged(ModelChange change) {
				if (change == ModelChange.ACTIVE_PLAYER) {
					selectPlayerFromModel();
				}
			}
		});

		this.oldPlayersCheckBox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateFilterPanel();
			}
		});

		this.changesTable.getSelectionModel().addListSelectionListener(
				new PlayerSelectionListener(this.model, this.changesTable,
						ChangesTableModel.COL_PLAYER_ID));
	}

	private void setAllSelected(boolean selected) {
		for (Iterator<ButtonModel> iter = this.buttonModels.values().iterator(); iter.hasNext();) {
			ButtonModel bModel = iter.next();
			bModel.setSelected(selected);
		}
		this.oldPlayers.setSelected(selected);
	}

	/**
	 * Get map of skillups for a list of players. The map will contain a list of
	 * skillups for each skill, represented as an <code>Integer</code> as key
	 * 
	 * @param players
	 *            List of players to analyze
	 * 
	 * @return Map of skillups
	 */
	private Map<Integer, List<PlayerSkillChange>> getSkillups(List<Player> players) {
		Map<Integer, List<PlayerSkillChange>> skillupsByType = new HashMap<Integer, List<PlayerSkillChange>>();

		for (Player player : players) {
			PastTrainingManager otm = new PastTrainingManager(player);
			List<ISkillChange> skillups = otm.getAllSkillups();

			for (ISkillChange skillup : skillups) {
				Integer skillType = skillup.getType();
				List<PlayerSkillChange> playerSkillChanges = skillupsByType.get(skillType);

				if (playerSkillChanges == null) {
					playerSkillChanges = new ArrayList<>();
					skillupsByType.put(skillType, playerSkillChanges);
				}

				playerSkillChanges.add(new PlayerSkillChange(player, skillup));
			}
		}

		return skillupsByType;
	}

	/**
	 * Creates a panel with a skill increases number and a checkbox.
	 * 
	 * @param skill
	 *            skill type
	 * 
	 * @return a panel
	 */
	private JPanel createSkillSelector(int skill) {
		Integer skillType = skill;

		int change = 0;

		if (this.skillups.containsKey(skillType)) {
			change += (this.skillups.get(skillType)).size();
		}

		if (this.oldPlayers.isSelected() && this.skillupsOld.containsKey(skillType)) {
			change += (this.skillupsOld.get(skillType)).size();
		}

		JCheckBox cBox = new JCheckBox();
		cBox.setOpaque(false);
		cBox.setFocusable(false);

		if (this.buttonModels.containsKey(skillType)) {
			cBox.setModel(this.buttonModels.get(skillType));
		} else {
			this.buttonModels.put(skillType, cBox.getModel());
			if (change > 0) {
				cBox.setSelected(true);
			} else {
				cBox.setSelected(false);
			}
		}

		cBox.setText(PlayerSkill.toString(skill));
		cBox.addActionListener(this);

		JPanel panel = new ImagePanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel(ImageUtilities.getWideImageIcon4Veraenderung(change, true)));
		panel.add(cBox);

		return panel;
	}

	/**
	 * Initialize panel.
	 */
	private void initComponents() {
		setOpaque(false);
		setLayout(new BorderLayout());

		JPanel mainpanel = new ImagePanel(new BorderLayout());
		JPanel skillPanel = new ImagePanel();

		skillPanel.setLayout(new BorderLayout());
		skillPanel.setBorder(BorderFactory.createTitledBorder(HOVerwaltung.instance()
				.getLanguageString("TAB_SKILL")));

		// Add selection listener.
		this.changesTable = new JTable();
		skillPanel.add(new JScrollPane(this.changesTable), BorderLayout.CENTER);

		this.oldPlayersCheckBox = new JCheckBox();
		this.oldPlayersCheckBox.setOpaque(false);
		this.oldPlayersCheckBox.setText(HOVerwaltung.instance().getLanguageString("IncludeOld"));
		this.oldPlayersCheckBox.setFocusable(false);
		this.oldPlayersCheckBox.setSelected(false);
		this.oldPlayersCheckBox.addActionListener(this);
		this.oldPlayers = this.oldPlayersCheckBox.getModel();
		skillPanel.add(this.oldPlayersCheckBox, BorderLayout.SOUTH);

		JPanel sidePanel = new ImagePanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 8, 4, 8);
		filterPanel = new ImagePanel();
		sidePanel.add(filterPanel, gbc);

		filterPanel.setLayout(new GridBagLayout());

		JButton btnShowAll = new JButton();
		btnShowAll.setText(HOVerwaltung.instance().getLanguageString("ShowAll"));
		btnShowAll.setFocusable(false);
		btnShowAll.addActionListener(this);
		btnShowAll.setActionCommand(CMD_SELECT_ALL);
		gbc.gridy = 1;
		gbc.insets = new Insets(8, 8, 2, 8);
		sidePanel.add(btnShowAll, gbc);

		JButton btnClearAll = new JButton();
		btnClearAll.setText(HOVerwaltung.instance().getLanguageString("ClearAll"));
		btnClearAll.setFocusable(false);
		btnClearAll.addActionListener(this);
		btnClearAll.setActionCommand(CMD_CLEAR_ALL);
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 8, 8, 8);
		gbc.weighty = 1.0;
		sidePanel.add(btnClearAll, gbc);

		GUIUtils.equalizeComponentSizes(btnShowAll, btnClearAll);

		JScrollPane sidePane = new JScrollPane(sidePanel);
		sidePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sidePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sidePane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		mainpanel.add(sidePane, BorderLayout.WEST);
		mainpanel.add(skillPanel, BorderLayout.CENTER);
		add(mainpanel, BorderLayout.CENTER);
	}

	/**
	 * Redraws the panel with a checkbox for each skill and a number of
	 * increases per skill.
	 */
	private void updateFilterPanel() {
		filterPanel.removeAll();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridy = 0;
		filterPanel.add(createSkillSelector(PlayerSkill.KEEPER), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.PLAYMAKING), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.PASSING), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.WINGER), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.DEFENDING), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.SCORING), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.SET_PIECES), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.STAMINA), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.EXPERIENCE), gbc);

		filterPanel.revalidate();
	}

	private void selectPlayerFromModel() {
		this.changesTable.clearSelection();
		Player player = this.model.getActivePlayer();
		if (player != null) {
			ChangesTableModel tblModel = (ChangesTableModel) this.changesTable.getModel();
			for (int i = 0; i < tblModel.getRowCount(); i++) {
				String val = (String) tblModel.getValueAt(i, ChangesTableModel.COL_PLAYER_ID);
				int id = Integer.parseInt(val);
				if (player.getPlayerID() == id) {
					int viewIndex = this.changesTable.convertRowIndexToView(i);
					this.changesTable.getSelectionModel()
							.addSelectionInterval(viewIndex, viewIndex);
				}
			}
		}
	}
}
