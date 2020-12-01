// %1193228360:hoplugins.teamAnalyzer.ui.component%
package module.teamAnalyzer.ui.component;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;
import module.teamAnalyzer.SystemManager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

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
    //~ Instance fields ----------------------------------------------------------------------------
	public static ModuleConfig config = ModuleConfig.instance();
    /**
	 *
	 */
	private static final long serialVersionUID = -5721035453587068724L;
	private JCheckBox checkName = new JCheckBox();
    private JCheckBox descRating = new JCheckBox();
    private JCheckBox loddarStats = new JCheckBox();
    private JCheckBox mixedLineup = new JCheckBox();
    private JCheckBox myLineup = new JCheckBox();
    private JCheckBox numberRating = new JCheckBox();
    private JCheckBox playerInfo = new JCheckBox();
    private JCheckBox smartSquad = new JCheckBox();
    private JCheckBox squad = new JCheckBox();
    private JCheckBox stars = new JCheckBox();
    private JCheckBox tacticDetail = new JCheckBox();
    private JCheckBox totalStrength = new JCheckBox();
    private JCheckBox unavailable = new JCheckBox();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a new instance.
     */
    public SettingPanel() {
        super();
        numberRating.setSelected(config.getBoolean(SystemManager.ISNUMERICRATING));
        numberRating.setOpaque(false);
        descRating.setSelected(config.getBoolean(SystemManager.ISDESCRIPTIONRATING));
        descRating.setOpaque(false);
        myLineup.setSelected(config.getBoolean(SystemManager.ISLINEUP));
        myLineup.setOpaque(false);
        tacticDetail.setSelected(config.getBoolean(SystemManager.ISTACTICDETAIL));
        tacticDetail.setOpaque(false);
        unavailable.setSelected(config.getBoolean(SystemManager.ISSHOWUNAVAILABLE, true));
        unavailable.setOpaque(false);
        playerInfo.setSelected(config.getBoolean(SystemManager.ISSHOWPLAYERINFO));
        playerInfo.setOpaque(false);
        mixedLineup.setSelected(config.getBoolean(SystemManager.ISMIXEDLINEUP));
        mixedLineup.setOpaque(false);
        stars.setSelected(config.getBoolean(SystemManager.ISSTARS));
        stars.setOpaque(false);
        smartSquad.setSelected(config.getBoolean(SystemManager.ISSMARTSQUAD));
        smartSquad.setOpaque(false);
        loddarStats.setSelected(config.getBoolean(SystemManager.ISLODDARSTATS));
        loddarStats.setOpaque(false);
        squad.setSelected(config.getBoolean(SystemManager.ISSQUAD));
        squad.setOpaque(false);
        totalStrength.setSelected(config.getBoolean(SystemManager.ISTOTALSTRENGTH));
        totalStrength.setOpaque(false);
        checkName.setSelected(config.getBoolean(SystemManager.ISCHECKTEAMNAME));
        checkName.setOpaque(false);
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------

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

        //innerPanel.setLayout(new BorderLayout());
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
        numberRating.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    if (numberRating.isSelected() || descRating.isSelected()) {
                    	config.setBoolean(SystemManager.ISNUMERICRATING,numberRating.isSelected());
                        SystemManager.updateUI();
                    } else {
                        numberRating.setSelected(true);
                    }
                }
            });

        descRating.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    if (numberRating.isSelected() || descRating.isSelected()) {
                    	config.setBoolean(SystemManager.ISDESCRIPTIONRATING,descRating.isSelected());
                        SystemManager.updateUI();
                    } else {
                        descRating.setSelected(true);
                    }
                }
            });

        stars.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISSTARS,stars.isSelected());
                    SystemManager.updateUI();

                }
            });
        totalStrength.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISTOTALSTRENGTH,totalStrength.isSelected());
                    SystemManager.updateUI();

                }
            });
        checkName.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISCHECKTEAMNAME,checkName.isSelected());
                    SystemManager.updateUI();

                }
            });

        squad.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISSQUAD,squad.isSelected());
                    SystemManager.updateUI();

                }
            });
        smartSquad.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISSMARTSQUAD,smartSquad.isSelected());
                    SystemManager.updateUI();

                }
            });

        loddarStats.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISLODDARSTATS,loddarStats.isSelected());
                    SystemManager.updateUI();

                }
            });

        myLineup.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISLINEUP,myLineup.isSelected());
                    SystemManager.updateUI();
                }
            });

        tacticDetail.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISTACTICDETAIL,tacticDetail.isSelected());
                    SystemManager.updateUI();
                }
            });

        unavailable.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISSHOWUNAVAILABLE,unavailable.isSelected());
                    SystemManager.updateUI();
                }
            });
        playerInfo.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISSHOWPLAYERINFO,playerInfo.isSelected());
                    SystemManager.updateUI();
                }
            });
        mixedLineup.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                	config.setBoolean(SystemManager.ISMIXEDLINEUP,mixedLineup.isSelected());
                    SystemManager.updateUI();
                }
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

        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("RecapPanel.Stars"), stars));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.hatstats"), totalStrength));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.squad"), squad));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.smartsquad"), smartSquad));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.loddarstats"), loddarStats));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.Playerinformations"), playerInfo));
        mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("SettingPanel.CheckName"), checkName));

        setLayout(new BorderLayout());
        setOpaque(false);
        add(mainPanel, BorderLayout.CENTER);
    }
}
