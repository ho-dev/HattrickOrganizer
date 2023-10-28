package module.training;

import core.gui.comp.panel.LazyPanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;
import core.util.Helper;
import module.training.ui.AnalyzerPanel;
import module.training.ui.EffectPanel;
import module.training.ui.OutputPanel;
import module.training.ui.PlayerDetailPanel;
import module.training.ui.TrainingDevelopmentPanel;
import module.training.ui.TrainingPanel;
import module.training.ui.TrainingPredictionPanel;
import module.training.ui.model.TrainingModel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class TrainingModulePanel extends LazyPanel {

	private TrainingModel model;

	@Override
	protected void initialize() {
		this.model = new TrainingModel();
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
		//setStaffInTrainingModel(this.model);

		if (oldPlayer != null) {
			Player player = HOVerwaltung.instance().getModel()
					.getCurrentPlayer(oldPlayer.getPlayerID());
			this.model.setActivePlayer(player);
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		var trainingDevelopmentPanel = new TrainingDevelopmentPanel(this.model);

		JSplitPane bottomPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, trainingDevelopmentPanel,
				new JScrollPane(new PlayerDetailPanel(this.model)));
		UserParameter.instance().training_bottomSplitPane.init(bottomPanel);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(Helper.getTranslation("Training"), new OutputPanel(this.model));
		tabbedPane.addTab(Helper.getTranslation("MainPanel.Prediction"), new TrainingPredictionPanel(this.model));
		tabbedPane.addTab(Helper.getTranslation("MainPanel.Analyzer"), new AnalyzerPanel(this.model));
		tabbedPane.addTab(Helper.getTranslation("MainPanel.Effect"), new EffectPanel());

		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, bottomPanel);
		UserParameter.instance().training_mainSplitPane.init(splitPanel);

		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPanel, new TrainingPanel(this.model));
		UserParameter.instance().training_rightSplitPane.init(mainPanel);
		mainPanel.setResizeWeight(0.8);

		mainPanel.setOpaque(false);
		add(mainPanel, BorderLayout.CENTER);
	}

}
