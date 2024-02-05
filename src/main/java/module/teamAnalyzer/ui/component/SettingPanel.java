// %1193228360:hoplugins.teamAnalyzer.ui.component%
package module.teamAnalyzer.ui.component;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import module.teamAnalyzer.SystemManager;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;



/**
 * A panel that allows the user to configure the plugin
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class SettingPanel extends JPanel {
    private final JCheckBox checkName = new JCheckBox();
    private final JCheckBox descRating = new JCheckBox();
    private final JCheckBox mixedLineup = new JCheckBox();
    private final JCheckBox myLineup = new JCheckBox();
    private final JCheckBox numberRating = new JCheckBox();
    private final JCheckBox playerInfo = new JCheckBox();

    private final JCheckBox tacticDetail = new JCheckBox();
    private final JCheckBox unavailable = new JCheckBox();

    private final JCheckBox specialEventsVisible = new JCheckBox();

    public SettingPanel() {
        super();
        numberRating.setSelected(SystemManager.isNumericRating.isSet());
        numberRating.setOpaque(false);
        descRating.setSelected(SystemManager.isDescriptionRating.isSet());
        descRating.setOpaque(false);
        myLineup.setSelected(SystemManager.isLineup.isSet());
        myLineup.setOpaque(false);
        tacticDetail.setSelected(SystemManager.isTacticDetail.isSet());
        tacticDetail.setOpaque(false);
        unavailable.setSelected(SystemManager.isShowUnavailable.isSet());
        unavailable.setOpaque(false);
        playerInfo.setSelected(SystemManager.isShowPlayerInfo.isSet());
        playerInfo.setOpaque(false);
        mixedLineup.setSelected(SystemManager.isMixedLineup.isSet());
        mixedLineup.setOpaque(false);
        checkName.setSelected(SystemManager.isCheckTeamName.isSet());
        checkName.setOpaque(false);
        specialEventsVisible.setSelected(SystemManager.isSpecialEventVisible.isSet());
        specialEventsVisible.setOpaque(false);
        jbInit();
    }

    /**
     * Create a new Panel
     *
     * @param string Label text
     * @param checkBox CheckBox
     *
     * @return a panel
     */
    private JPanel createPanel(String string, JComponent checkBox) {
        JPanel panel = new ImagePanel();

        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        JPanel innerPanel = new ImagePanel();

        innerPanel.add(checkBox);
        innerPanel.add(new JLabel(string, SwingConstants.LEFT));
        innerPanel.setOpaque(false);
        panel.add(innerPanel, BorderLayout.WEST);

        return panel;
    }

    /**
     * Initialize listeners
     */
    private void initListeners() {
        numberRating.addActionListener(e -> {
            if (numberRating.isSelected() || descRating.isSelected()) {
                SystemManager.isNumericRating.set(numberRating.isSelected());
                SystemManager.updateUI();
            } else {
                numberRating.setSelected(true);
            }
        });

        descRating.addActionListener(e -> {
            if (numberRating.isSelected() || descRating.isSelected()) {
                SystemManager.isDescriptionRating.set(descRating.isSelected());
                SystemManager.updateUI();
            } else {
                descRating.setSelected(true);
            }
        });

        checkName.addActionListener(e -> {
            SystemManager.isCheckTeamName.set(checkName.isSelected());
            SystemManager.updateUI();
        });

        myLineup.addActionListener(e -> {
            SystemManager.isLineup.set(myLineup.isSelected());
            SystemManager.updateUI();
        });

        tacticDetail.addActionListener(e -> {
            SystemManager.isTacticDetail.set(tacticDetail.isSelected());
            SystemManager.updateUI();
        });

        unavailable.addActionListener(e -> {
            SystemManager.isShowUnavailable.set(unavailable.isSelected());
            SystemManager.updateUI();
        });
        playerInfo.addActionListener(e -> {
            SystemManager.isShowPlayerInfo.set(playerInfo.isSelected());
            SystemManager.updateUI();
        });
        mixedLineup.addActionListener(e -> {
            SystemManager.isMixedLineup.set(mixedLineup.isSelected());
            SystemManager.updateUI();
        });
        specialEventsVisible.addActionListener(e -> {
            SystemManager.isSpecialEventVisible.set(specialEventsVisible.isSelected());
            SystemManager.updateUI();
        });
    }

    /**
     * Initializes the state of this instance.
     */
    private void jbInit() {
        initListeners();

        JPanel mainPanel = new ImagePanel();

        mainPanel.setLayout(new GridLayout(12, 1));
        mainPanel.setOpaque(false);
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.MyLineup"), myLineup));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.TacticDetail"), tacticDetail));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.MixedLineup"), mixedLineup));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.NumericRatings"), numberRating));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.DescriptionRatings"), descRating));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.ShowUnavailable"), unavailable));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.Playerinformations"), playerInfo));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.CheckName"), checkName));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.SpecialEventVisible"), specialEventsVisible));

        setLayout(new BorderLayout());
        setOpaque(false);
        add(mainPanel, BorderLayout.CENTER);
    }
}
