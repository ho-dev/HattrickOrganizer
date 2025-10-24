package module.training.ui;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.NumericDocument;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import core.model.enums.MatchType;
import core.net.OnlineWorker;
import core.training.TrainingManager;
import core.util.Helper;
import core.util.HelperWrapper;
import core.util.StringUtils;
import module.training.ui.model.TrainingModel;
import module.training.ui.renderer.OutputTableRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The Panel where the main training table is shown ("Training").
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class OutputPanel extends LazyImagePanel {

    private final TrainingProgressTableModel trainingProgressTableModel;
    private PlayersTable trainingProgressTable;
    private JButton importButton;
    private JButton calculateButton;
    private final TrainingModel trainingModel;
    private FutureTrainingPrioPopup trainingPrioPopUp;

    /**
     * Creates a new OutputPanel object.
     */
    public OutputPanel(TrainingModel model) {
        super();
        this.trainingProgressTableModel = UserColumnController.instance().getTrainingProgressTableModel();
        this.trainingModel = model;
    }

    public void storeUserSettings(){
        this.trainingProgressTableModel.storeUserSettings();
    }

    @Override
    protected void initialize() {
        initComponents();
        addListeners();
        registerRefreshable(true);
        update();
        setNeedsRefresh(false);
    }

    @Override
    protected void update() {
        var model = (TrainingProgressTableModel)this.trainingProgressTable.getModel();
        model.initData();
        this.trainingProgressTable.initSelection();
    }

    /**
     * Import a match from Hattrick
     */
    @SuppressWarnings("deprecation")
    private void importMatches() {

        JTextField tf = new JTextField();
        tf.setDocument(new NumericDocument(10));
        Object[] objs = {TranslationFacility.tr("ls.match.id"), tf};

        int value = JOptionPane.showConfirmDialog(HOMainFrame.instance(), objs,
                TranslationFacility.tr("ImportMatch"), JOptionPane.OK_CANCEL_OPTION);

        String input = tf.getText();
        if (value == JOptionPane.YES_OPTION && !StringUtils.isEmpty(input)) {

            int matchID = Integer.parseInt(input);

            if (HelperWrapper.instance().isUserMatch(input, MatchType.LEAGUE)) {
                if (OnlineWorker.downloadMatchData(matchID, MatchType.LEAGUE, false)) {
                    Helper.showMessage(null,
                            TranslationFacility.tr("MatchImported"),
                            TranslationFacility.tr("ImportOK"), 1);
                    RefreshManager.instance().doRefresh();
                }
            } else {
                Helper.showMessage(null, TranslationFacility.tr("NotUserMatch"),
                        TranslationFacility.tr("ImportError"), 1);
            }
        }
    }

    private void addListeners() {
        this.importButton.addActionListener(arg0 -> importMatches());
        this.calculateButton.addActionListener(arg0 -> {
            // recalcSubskills() causes UI update via RefreshManager, so no
            // need to update UI ourself
            TrainingManager.instance().recalcSubskills(true);
        });

        this.trainingProgressTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (trainingProgressTable.getSelectedRow() < 0)
                    return;
                if (e.getComponent() instanceof JTable) {
                    trainingPrioPopUp.updateActivePlayer();
                    trainingPrioPopUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Initialize the object layout
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        this.trainingProgressTableModel.setModel(this.trainingModel);
        this.trainingProgressTable = new PlayersTable(this.trainingProgressTableModel);
        this.trainingProgressTable.setDefaultRenderer(Object.class, new OutputTableRenderer(false));
        add(this.trainingProgressTable.getContainerComponent(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());

        this.importButton = new JButton(TranslationFacility.tr("ImportMatch"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(6, 8, 6, 4);
        buttonPanel.add(this.importButton, gbc);

        this.calculateButton = new JButton(TranslationFacility.tr("ls.menu.file.subskillrecalculation"));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 4, 6, 8);
        buttonPanel.add(this.calculateButton, gbc);

        add(buttonPanel, BorderLayout.NORTH);
        trainingPrioPopUp = new FutureTrainingPrioPopup(this, this.trainingModel);
    }

}