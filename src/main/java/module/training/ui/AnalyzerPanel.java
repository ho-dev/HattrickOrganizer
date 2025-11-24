package module.training.ui;

import core.constants.player.PlayerSkill;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyPanel;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.player.Player;
import core.model.player.SkillChange;
import core.util.GUIUtils;
import module.training.PastTrainingManager;
import module.training.PlayerSkillChange;
import module.training.ui.model.ChangesTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

/**
 * Shows a table of skillups, which can be filtered using the checkboxes on the
 * side.
 * 
 * @author NetHyperon
 */
public class AnalyzerPanel extends LazyPanel implements ActionListener {

	private static final String CMD_SELECT_ALL = "selectAll";
	private static final String CMD_CLEAR_ALL = "clearAll";
	private ButtonModel oldPlayers;
	private JPanel filterPanel;
	private PlayersTable changesTable;
	private JCheckBox oldPlayersCheckBox;
	private final Map<PlayerSkill, ButtonModel> buttonModels = new HashMap<>();
	private Map<PlayerSkill, List<PlayerSkillChange>> skillups;
	private Map<PlayerSkill, List<PlayerSkillChange>> skillupsOld;
    private ChangesTableModel tableModel;

    /**
	 * Creates a new AnalyzerPanel object.
	 */
	public AnalyzerPanel() {
		super();
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
        this.tableModel = UserColumnController.instance().getTrainingAnalysisTableModel();
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
        this.changesTable.refresh();
	}
	
	/**
	 * Sets the model for skill changes table.
	 */
	private void updateTableModel() {
		List<PlayerSkillChange> values = new ArrayList<>();

        for (var skillType : this.buttonModels.keySet()) {
            ButtonModel bModel = this.buttonModels.get(skillType);

            if (this.skillups.containsKey(skillType) && bModel.isSelected()) {
                values.addAll(this.skillups.get(skillType));
            }

            if (this.oldPlayers.isSelected() && bModel.isSelected()
                    && this.skillupsOld.containsKey(skillType)) {
                values.addAll(this.skillupsOld.get(skillType));
            }
        }

        var trainingAnalysisTableModel = UserColumnController.instance().getTrainingAnalysisTableModel();
        trainingAnalysisTableModel.setChanges(values);
	}

	private void addListeners() {
        PlayersTable.Companion.addPropertyChangeListener(evt -> selectPlayerFromModel());
        this.oldPlayersCheckBox.addChangeListener(e -> updateFilterPanel());
		this.changesTable.getSelectionModel().addListSelectionListener(e->{
            var modelIndex = this.changesTable.getSelectedModelIndex();
            if (modelIndex > -1){
                var tableModel = (ChangesTableModel) this.changesTable.getModel();
                var playerSkillChange = tableModel.getValues().get(modelIndex);
                PlayersTable.Companion.setSelectedPlayer(playerSkillChange.getPlayer());
                this.selectPlayerFromModel();
            }
        });
    }

	private void setAllSelected(boolean selected) {
        for (ButtonModel bModel : this.buttonModels.values()) {
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
	private Map<PlayerSkill, List<PlayerSkillChange>> getSkillups(List<Player> players) {
		Map<PlayerSkill, List<PlayerSkillChange>> skillupsByType = new HashMap<>();

		for (Player player : players) {
			PastTrainingManager otm = new PastTrainingManager(player);
			List<SkillChange> allSkillChanges = otm.getAllSkillChanges();

			for (SkillChange skillChange : allSkillChanges) {
                if ( skillChange.getChange() > 0) {
                    var skillType = skillChange.getType();
                    List<PlayerSkillChange> playerSkillChanges = skillupsByType.computeIfAbsent(skillType, k -> new ArrayList<>());
                    playerSkillChanges.add(new PlayerSkillChange(player, skillChange));
                }
			}
		}

		return skillupsByType;
	}

	/**
	 * Creates a panel with a skill increases number and a checkbox.
	 * 
	 * @param skillType
	 *            skill type
	 * 
	 * @return a panel
	 */
	private JPanel createSkillSelector(PlayerSkill skillType) {

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
            cBox.setSelected(change > 0);
		}

		cBox.setText(skillType.getLanguageString());
		cBox.addActionListener(this);

		JPanel panel = new ImagePanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel(ImageUtilities.getWideImageIcon4Change(change)));
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
		skillPanel.setBorder(BorderFactory.createTitledBorder(TranslationFacility.tr("TAB_SKILL")));

		this.changesTable = new PlayersTable(this.tableModel,2);
        this.changesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		skillPanel.add(new JScrollPane(this.changesTable.getContainerComponent()), BorderLayout.CENTER);

		this.oldPlayersCheckBox = new JCheckBox();
		this.oldPlayersCheckBox.setOpaque(false);
		this.oldPlayersCheckBox.setText(TranslationFacility.tr("IncludeOld"));
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
		btnShowAll.setText(TranslationFacility.tr("ShowAll"));
		btnShowAll.setFocusable(false);
		btnShowAll.addActionListener(this);
		btnShowAll.setActionCommand(CMD_SELECT_ALL);
		gbc.gridy = 1;
		gbc.insets = new Insets(8, 8, 2, 8);
		sidePanel.add(btnShowAll, gbc);

		JButton btnClearAll = new JButton();
		btnClearAll.setText(TranslationFacility.tr("ClearAll"));
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
		filterPanel.add(createSkillSelector(PlayerSkill.SETPIECES), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.STAMINA), gbc);
		gbc.gridy++;
		filterPanel.add(createSkillSelector(PlayerSkill.EXPERIENCE), gbc);

		filterPanel.revalidate();
	}

    private boolean disableListSelectionListener = false;

    private void selectPlayerFromModel() {
        if (this.disableListSelectionListener) return;
        this.disableListSelectionListener = true;
		this.changesTable.clearSelection();
		Player player = PlayersTable.Companion.getSelectedPlayer();
		if (player != null) {
			ChangesTableModel tblModel = (ChangesTableModel) this.changesTable.getModel();
			for (int i = 0; i < tblModel.getRowCount(); i++) {
                var playerSkillChange = tblModel.getValues().get(i);
				if (player.getPlayerId() == playerSkillChange.getPlayer().getPlayerId()) {
					int viewIndex = this.changesTable.convertRowIndexToView(i);
                    this.changesTable.addRowSelectionInterval(viewIndex, viewIndex);
				}
			}
		}
        this.disableListSelectionListener = false;
	}

    public void storeUserSettings() {
        if ( this.tableModel != null ) {
            this.tableModel.storeUserSettings();
        }
    }
}
