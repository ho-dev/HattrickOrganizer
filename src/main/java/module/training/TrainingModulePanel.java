package module.training;

import core.gui.comp.panel.LazyPanel;
import core.model.TranslationFacility;
import core.model.UserParameter;
import module.training.ui.*;
import module.training.ui.model.TrainingModel;

import javax.swing.*;
import java.awt.*;

public class TrainingModulePanel extends LazyPanel {

	private TrainingModel model;
	private OutputPanel trainingProgressPanel;
    private TrainingPredictionPanel trainingPredictionPanel;
    private AnalyzerPanel trainingAnalyzerPanel;
    private EffectPanel trainingEffectPanel;
    private TrainingPanel trainingPanel;
    private TrainingDevelopmentPanel trainingDevelopmentPanel;

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
		this.model.resetFutureTrainings();
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		this.trainingDevelopmentPanel = new TrainingDevelopmentPanel(this.model);
		JSplitPane bottomPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, trainingDevelopmentPanel,
				new JScrollPane(new PlayerDetailPanel(this.model)));
		UserParameter.instance().training_bottomSplitPane.init(bottomPanel);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(TranslationFacility.tr("Training"), this.trainingProgressPanel);
		tabbedPane.addTab(TranslationFacility.tr("MainPanel.Prediction"), this.trainingPredictionPanel);
		tabbedPane.addTab(TranslationFacility.tr("MainPanel.Analyzer"), this.trainingAnalyzerPanel);
		tabbedPane.addTab(TranslationFacility.tr("MainPanel.Effect"), this.trainingEffectPanel);

		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, bottomPanel);
		UserParameter.instance().training_mainSplitPane.init(splitPanel);

        this.trainingPanel = new TrainingPanel(this.model);
		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPanel, trainingPanel);
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
        if (this.trainingPanel != null){
            this.trainingPanel.storeUserSettings();
        }
        if (this.trainingDevelopmentPanel != null){
            this.trainingDevelopmentPanel.storeUserSettings();
        }
	}
}
