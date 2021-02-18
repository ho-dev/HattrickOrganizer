package module.training;

import core.gui.comp.panel.LazyPanel;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.StaffMember;
import core.model.StaffType;
import core.model.UserParameter;
import core.model.player.Player;
import module.training.ui.AnalyzerPanel;
import module.training.ui.EffectPanel;
import module.training.ui.OutputPanel;
import module.training.ui.PlayerDetailPanel;
import module.training.ui.SkillupPanel;
import module.training.ui.StaffPanel;
import module.training.ui.TrainingPanel;
import module.training.ui.TrainingRecapPanel;
import module.training.ui.comp.DividerListener;
import module.training.ui.model.TrainingModel;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class TrainingModulePanel extends LazyPanel {

	private TrainingModel model;

	@Override
	protected void initialize() {
		this.model = new TrainingModel();
		setStaffInTrainingModel(this.model);
		initComponents();
		registerRefreshable(true);
	}

	@Override
	protected void update() {
		Player oldPlayer = this.model.getActivePlayer();
		// reset the selected player
		this.model.setActivePlayer(null);
		this.model.resetFutureTrainings();
		// reload the staff, could have changed
		setStaffInTrainingModel(this.model);

		if (oldPlayer != null) {
			Player player = HOVerwaltung.instance().getModel()
					.getCurrentPlayer(oldPlayer.getPlayerID());
			this.model.setActivePlayer(player);
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				new SkillupPanel(this.model), new StaffPanel(this.model));
		leftPane.setResizeWeight(1);
		leftPane.setDividerLocation(UserParameter.instance().training_lowerLeftSplitPane);
		leftPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.training_lowerLeftSplitPane));

		JSplitPane bottomPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane,
				new JScrollPane(new PlayerDetailPanel(this.model)));
		bottomPanel.setDividerLocation(UserParameter.instance().training_bottomSplitPane);
		bottomPanel.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.training_bottomSplitPane));

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(getLangStr("Training"), new OutputPanel(this.model));
		tabbedPane.addTab(getLangStr("MainPanel.Prediction"), new TrainingRecapPanel(this.model));
		tabbedPane.addTab(getLangStr("MainPanel.Analyzer"), new AnalyzerPanel(this.model));
		tabbedPane.addTab(getLangStr("MainPanel.Effect"), new EffectPanel());

		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, bottomPanel);
		splitPanel.setDividerLocation(UserParameter.instance().training_mainSplitPane);
		splitPanel.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.training_mainSplitPane));

		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPanel,
				new TrainingPanel(this.model));
		mainPanel.setDividerLocation(UserParameter.instance().training_rightSplitPane);
		mainPanel.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.training_rightSplitPane));

		mainPanel.setOpaque(false);
		add(mainPanel, BorderLayout.CENTER);
	}

	private void setStaffInTrainingModel(TrainingModel trainingModel) {
		HOModel hoModel = HOVerwaltung.instance().getModel();
		// Assistant coaches
		if (hoModel.getStaff() != null) {
			// Loop over the staff members and add up the assistant coach levels
			int level = 0;
			List<StaffMember> staff = hoModel.getStaff();
			for (StaffMember staffMember : staff) {
				if (staffMember.getStaffType() == StaffType.ASSISTANTTRAINER) {
					level += staffMember.getLevel();
				}
			}
			trainingModel.setNumberOfCoTrainers(level);
		}
		// Main coach
		if (hoModel.getTrainer() != null) {
			trainingModel.setTrainerLevel(hoModel.getTrainer().getTrainerSkill());
		} else {
			trainingModel.setTrainerLevel(4);
		}
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
