package module.training;

import core.gui.comp.panel.LazyPanel;
import core.gui.comp.tabbedPane.HOTabbedPane;
import core.model.TranslationFacility;
import core.model.UserParameter;
import module.training.ui.AnalyzerPanel;
import module.training.ui.EffectPanel;
import module.training.ui.OutputPanel;
import module.training.ui.PlayerDetailPanel;
import module.training.ui.TrainingDevelopmentPanel;
import module.training.ui.TrainingPanel;
import module.training.ui.TrainingPredictionPanel;
import module.training.ui.model.TrainingModel;

import java.awt.Component;
import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class TrainingModulePanel extends LazyPanel {

    private TrainingModel model;
    private OutputPanel trainingProgressPanel;
    private TrainingPredictionPanel trainingPredictionPanel;
    private AnalyzerPanel trainingAnalyzerPanel;
    private EffectPanel trainingEffectPanel;
    private TrainingPanel trainingPanel;
    private TrainingDevelopmentPanel trainingDevelopmentPanel;

    private final Map<String, Component> tabComponents = new LinkedHashMap<>();
    private HOTabbedPane tabbedPane;

    @Override
    protected void initialize() {
        model = new TrainingModel();
        trainingProgressPanel = new OutputPanel(this.model);
        trainingPredictionPanel = new TrainingPredictionPanel(this.model);
        trainingAnalyzerPanel = new AnalyzerPanel();
        trainingEffectPanel = new EffectPanel();
        tabComponents.put("Training", trainingProgressPanel);
        tabComponents.put("MainPanel.Prediction", trainingPredictionPanel);
        tabComponents.put("MainPanel.Analyzer", trainingAnalyzerPanel);
        tabComponents.put("MainPanel.Effect", trainingEffectPanel);
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

        tabbedPane = new HOTabbedPane("Training") {
            @Override
            protected Optional<HOTabComponent> findComponent(String tabId) {
                if (tabComponents.containsKey(tabId)) {
                    return Optional.of(new HOTabComponent(TranslationFacility.tr(tabId), tabComponents.get(tabId)));
                }
                return Optional.empty();
            }
        };

        tabComponents.forEach((key, value) -> tabbedPane.addTab(key, value));

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
        if (this.trainingProgressPanel != null) {
            this.trainingProgressPanel.storeUserSettings();
        }
        if (this.trainingAnalyzerPanel != null) {
            this.trainingAnalyzerPanel.storeUserSettings();
        }
        if (this.trainingPredictionPanel != null) {
            this.trainingPredictionPanel.storeUserSettings();
        }
        if (this.trainingEffectPanel != null) {
            this.trainingEffectPanel.storeUserSettings();
        }
        if (this.trainingPanel != null) {
            this.trainingPanel.storeUserSettings();
        }
        if (this.trainingDevelopmentPanel != null) {
            this.trainingDevelopmentPanel.storeUserSettings();
        }

        if (this.tabbedPane != null) {
            tabbedPane.storeHOTabInfo();
        }
    }
}
