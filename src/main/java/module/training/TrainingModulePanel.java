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
	private OutputPanel trainingProgressPanel;
    private TrainingPredictionPanel trainingPredictionPanel;
    private AnalyzerPanel trainingAnalyzerPanel;
    private EffectPanel trainingEffectPanel;

    @Override
	protected void initialize() {
		this.model = new TrainingModel();
		this.trainingProgressPanel = new OutputPanel(this.model);
        this.trainingPredictionPanel =  new TrainingPredictionPanel(this.model);
        this.trainingAnalyzerPanel =  new AnalyzerPanel();
        this.trainingEffectPanel =  new EffectPanel();
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
					.getCurrentPlayer(oldPlayer.getPlayerId());
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
		tabbedPane.addTab(Helper.getTranslation("Training"), this.trainingProgressPanel);
		tabbedPane.addTab(Helper.getTranslation("MainPanel.Prediction"), this.trainingPredictionPanel);
		tabbedPane.addTab(Helper.getTranslation("MainPanel.Analyzer"), this.trainingAnalyzerPanel);
		tabbedPane.addTab(Helper.getTranslation("MainPanel.Effect"), this.trainingEffectPanel);

		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, bottomPanel);
		UserParameter.instance().training_mainSplitPane.init(splitPanel);

		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPanel, new TrainingPanel(this.model));
		UserParameter.instance().training_rightSplitPane.init(mainPanel);
		mainPanel.setResizeWeight(0.8);

		mainPanel.setOpaque(false);
		add(mainPanel, BorderLayout.CENTER);
	}

	public void storeUserSettings() {
        if (this.trainingProgressPanel != null)
            this.trainingProgressPanel.storeUserSettings();
        if (this.trainingAnalyzerPanel != null)
            this.trainingAnalyzerPanel.storeUserSettings();
        if (this.trainingPredictionPanel != null)
            this.trainingPredictionPanel.storeUserSettings();
        if (this.trainingEffectPanel != null)
            this.trainingEffectPanel.storeUserSettings();
	}
}
