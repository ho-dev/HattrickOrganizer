package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.util.chart.LinesChartDataModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.Helper;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Panel Club in Module Statistics
 */
public class ClubStatisticsPanel extends LazyImagePanel {

    private ImageCheckbox jcbAssistantTrainerLevels;
    private ImageCheckbox jcbFinancialDirectorLevels;
    private ImageCheckbox jcbFormCoachLevels;
    private ImageCheckbox jcbDoctorLevels;
    private ImageCheckbox jcbSpokePersonLevels;
    private ImageCheckbox jcbSportPsychologistLevels;
    private ImageCheckbox jcbTacticalAssistantLevels;
    private ImageCheckbox jcbYouthSquadLevel;
    private ImageCheckbox jcbYouthSquadInvestment;
    private ImageCheckbox jcbFanClubSize;
    private ImageCheckbox jcbGlobalRanking;
    private ImageCheckbox jcbLeagueRanking;
    private ImageCheckbox jcbRegionRanking;
    private ImageCheckbox jcbPowerRating;
    private JButton jbApply;
    private JCheckBox jcbDataLabels;
    private JCheckBox jcbHelpLines;
    private JTextField jtfNbWeeks;
    private StatistikPanel oStatisticsPanel;

    @Override
    protected void initialize() {
        initComponents();
        addListeners();
        setNeedsRefresh(true);
        registerRefreshable(true);
    }

    @Override
    protected void update() {
        initStatistik();
    }


    private void addListeners() {
        ActionListener checkBoxActionListener = e -> {
            if (e.getSource() == jcbHelpLines) {
                oStatisticsPanel.setHelpLines(jcbHelpLines.isSelected());
                UserParameter.instance().statistikFinanzenHilfslinien = jcbHelpLines.isSelected();
            } else if (e.getSource() == jcbDataLabels) {
                oStatisticsPanel.setLabelling(jcbDataLabels.isSelected());
            } else if (e.getSource() == jcbAssistantTrainerLevels.getCheckbox()) {
                oStatisticsPanel.setShow("AssistantTrainerLevels", jcbAssistantTrainerLevels.isSelected());
                UserParameter.instance().statistikKontostand = jcbAssistantTrainerLevels.isSelected();
            } else if (e.getSource() == jcbFinancialDirectorLevels.getCheckbox()) {
                oStatisticsPanel.setShow("FinancialDirectorLevels", jcbFinancialDirectorLevels.isSelected());
                UserParameter.instance().statistikGewinnVerlust = jcbFinancialDirectorLevels.isSelected();
            } else if (e.getSource() == jcbFormCoachLevels.getCheckbox()) {
                oStatisticsPanel.setShow("FormCoachLevels", jcbFormCoachLevels.isSelected());
                UserParameter.instance().statistikGesamtEinnahmen = jcbFormCoachLevels.isSelected();
            }
        };

        jcbHelpLines.addActionListener(checkBoxActionListener);
        jcbDataLabels.addActionListener(checkBoxActionListener);
        jcbAssistantTrainerLevels.addActionListener(checkBoxActionListener);
        jcbFinancialDirectorLevels.addActionListener(checkBoxActionListener);
        jcbFormCoachLevels.addActionListener(checkBoxActionListener);

        jbApply.addActionListener(e -> initStatistik());

        jtfNbWeeks.addFocusListener(new FocusAdapter() {
            @Override
            public final void focusLost(java.awt.event.FocusEvent focusEvent) {
                Helper.parseInt(HOMainFrame.instance(), jtfNbWeeks, false);
            }
        });
    }

    private void initComponents() {
        JLabel label;

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 0, 2, 0);

        setLayout(layout);

        JPanel panel2 = new ImagePanel();
        GridBagLayout layout2 = new GridBagLayout();
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.weightx = 0.0;
        constraints2.weighty = 0.0;
        constraints2.insets = new Insets(2, 2, 2, 2);

        panel2.setLayout(layout2);

        label = new JLabel(getLangStr("Wochen"));
        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.anchor = GridBagConstraints.WEST;
        constraints2.gridx = 0;
        constraints2.gridy = 1;
        constraints2.gridwidth = 1;
        layout2.setConstraints(label, constraints2);
        panel2.add(label);
        jtfNbWeeks = new JTextField(String.valueOf(UserParameter.instance().statisticsClubNbWeeks));
        jtfNbWeeks.setHorizontalAlignment(SwingConstants.RIGHT);
        constraints2.gridx = 1;
        constraints2.gridy = 1;
        layout2.setConstraints(jtfNbWeeks, constraints2);
        panel2.add(jtfNbWeeks);

        constraints2.gridx = 0;
        constraints2.gridy = 4;
        constraints2.gridwidth = 2;
        jbApply = new JButton(getLangStr("ls.button.apply"));
        jbApply.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
        layout2.setConstraints(jbApply, constraints2);
        panel2.add(jbApply);

        constraints2.gridwidth = 1;
        constraints2.gridx = 0;
        constraints2.gridy = 5;
        jcbHelpLines = new JCheckBox(getLangStr("Hilflinien"), UserParameter.instance().statisticsClubHelpLines);
        jcbHelpLines.setOpaque(false);
        layout2.setConstraints(jcbHelpLines, constraints2);
        panel2.add(jcbHelpLines);

        constraints2.gridwidth = 1;
        constraints2.gridx = 0;
        constraints2.gridy = 6;
        jcbDataLabels = new JCheckBox(getLangStr("Beschriftung"), UserParameter.instance().statisticsClubDataLabels);
        jcbDataLabels.setOpaque(false);
        layout2.setConstraints(jcbDataLabels, constraints2);
        panel2.add(jcbDataLabels);

        constraints2.gridwidth = 1;
        constraints2.gridx = 0;
        constraints2.gridy = 7;
        jcbAssistantTrainerLevels = new ImageCheckbox(getLangStr("AssistantTrainerLevels"),
                ThemeManager.getColor(HOColorName.PALETTE15[0]),
                UserParameter.instance().statistikKontostand);
        jcbAssistantTrainerLevels.setOpaque(false);
        layout2.setConstraints(jcbAssistantTrainerLevels, constraints2);
        panel2.add(jcbAssistantTrainerLevels);

        constraints2.gridwidth = 1;
        constraints2.gridx = 0;
        constraints2.gridy = 8;
        jcbFinancialDirectorLevels = new ImageCheckbox(getLangStr("FinancialDirectorLevels"),
                ThemeManager.getColor(HOColorName.PALETTE15[1]),
                UserParameter.instance().statistikGewinnVerlust);
        jcbFinancialDirectorLevels.setOpaque(false);
        layout2.setConstraints(jcbFinancialDirectorLevels, constraints2);
        panel2.add(jcbFinancialDirectorLevels);

        constraints2.gridwidth = 1;
        constraints2.gridx = 0;
        constraints2.gridy = 9;
        jcbFormCoachLevels = new ImageCheckbox(getLangStr("FormCoachLevels"),
                ThemeManager.getColor(HOColorName.PALETTE15[2]),
                UserParameter.instance().statistikGesamtEinnahmen);
        jcbFormCoachLevels.setOpaque(false);
        layout2.setConstraints(jcbFormCoachLevels, constraints2);
        panel2.add(jcbFormCoachLevels);

//        constraints2.gridwidth = 1;
//        constraints2.gridx = 1;
//        constraints2.gridy = 7;
//        m_jchGesamtausgaben = new ImageCheckbox(getLangStr("Gesamtausgaben"),
//                ThemeManager.getColor(HOColorName.STAT_COSTSUM),
//                UserParameter.instance().statistikGesamtAusgaben);
//        m_jchGesamtausgaben.setOpaque(false);
//        layout2.setConstraints(m_jchGesamtausgaben, constraints2);
//        panel2.add(m_jchGesamtausgaben);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 0;
//        constraints2.gridy = 8;
//        m_jchZuschauer = new ImageCheckbox(getLangStr("Zuschauer"),
//                ThemeManager.getColor(HOColorName.STAT_INCOMESPECTATORS),
//                UserParameter.instance().statistikZuschauer);
//        m_jchZuschauer.setOpaque(false);
//        layout2.setConstraints(m_jchZuschauer, constraints2);
//        panel2.add(m_jchZuschauer);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 0;
//        constraints2.gridy = 9;
//        m_jchSponsoren = new ImageCheckbox(getLangStr("Sponsoren"),
//                ThemeManager.getColor(HOColorName.STAT_INCOMESPONSORS),
//                UserParameter.instance().statistikSponsoren);
//        m_jchStadion = new ImageCheckbox(getLangStr("Stadion"),
//                ThemeManager.getColor(HOColorName.STAT_COSTARENA),
//                UserParameter.instance().statistikStadion);
//        m_jchSponsoren.setOpaque(false);
//        layout2.setConstraints(m_jchSponsoren, constraints2);
//        panel2.add(m_jchSponsoren);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 0;
//        constraints2.gridy = 10;
//        m_jchSonstigeEinnahmen = new ImageCheckbox(getLangStr("Sonstiges"),
//                ThemeManager.getColor(HOColorName.STAT_INCOMETEMPORARY),
//                UserParameter.instance().statistikSonstigeEinnahmen);
//        m_jchSonstigeEinnahmen.setOpaque(false);
//        layout2.setConstraints(m_jchSonstigeEinnahmen, constraints2);
//        panel2.add(m_jchSonstigeEinnahmen);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 1;
//        constraints2.gridy = 8;
//        m_jchStadion.setOpaque(false);
//        layout2.setConstraints(m_jchStadion, constraints2);
//        panel2.add(m_jchStadion);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 1;
//        constraints2.gridy = 9;
//        m_jchSpielergehaelter = new ImageCheckbox(getLangStr("Spielergehaelter"),
//                ThemeManager.getColor(HOColorName.STAT_COSTSPLAYERS),
//                UserParameter.instance().statistikSpielergehaelter);
//        m_jchSpielergehaelter.setOpaque(false);
//        layout2.setConstraints(m_jchSpielergehaelter, constraints2);
//        panel2.add(m_jchSpielergehaelter);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 1;
//        constraints2.gridy = 10;
//        m_jchSonstigeAusgaben = new ImageCheckbox(getLangStr("Sonstiges"),
//                ThemeManager.getColor(HOColorName.STAT_COSTTEMPORARY),
//                UserParameter.instance().statistikSonstigeAusgaben);
//        m_jchSonstigeAusgaben.setOpaque(false);
//        layout2.setConstraints(m_jchSonstigeAusgaben, constraints2);
//        panel2.add(m_jchSonstigeAusgaben);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 1;
//        constraints2.gridy = 12;
//        m_jchTrainerstab = new ImageCheckbox(getLangStr("Trainerstab"),
//                ThemeManager.getColor(HOColorName.STAT_COSTSTAFF),
//                UserParameter.instance().statistikTrainerstab);
//        m_jchTrainerstab.setOpaque(false);
//        layout2.setConstraints(m_jchTrainerstab, constraints2);
//        panel2.add(m_jchTrainerstab);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 1;
//        constraints2.gridy = 13;
//        m_jchJugend = new ImageCheckbox(getLangStr("Jugend"),
//                ThemeManager.getColor(HOColorName.STAT_COSTSYOUTH),
//                UserParameter.instance().statistikJugend);
//        m_jchJugend.setOpaque(false);
//        layout2.setConstraints(m_jchJugend, constraints2);
//        panel2.add(m_jchJugend);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 1;
//        constraints2.gridy = 14;
//        m_jchZinsaufwendungen = new ImageCheckbox(getLangStr("Zinsaufwendungen"),
//                ThemeManager.getColor(HOColorName.STAT_COSTFINANCIAL),
//                UserParameter.instance().statistikZinsaufwendungen);
//        m_jchZinsaufwendungen.setOpaque(false);
//        layout2.setConstraints(m_jchZinsaufwendungen, constraints2);
//        panel2.add(m_jchZinsaufwendungen);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 0;
//        constraints2.gridy = 12;
//        m_jchMarktwert = new ImageCheckbox(getLangStr("TotalTSI"),
//                ThemeManager.getColor(HOColorName.STAT_MARKETVALUE),
//                UserParameter.instance().statistikMarktwert);
//        m_jchMarktwert.setOpaque(false);
//        layout2.setConstraints(m_jchMarktwert, constraints2);
//        panel2.add(m_jchMarktwert);
//
//        constraints2.gridwidth = 1;
//        constraints2.gridx = 0;
//        constraints2.gridy = 13;
//        m_jchFans = new ImageCheckbox(getLangStr("Fans"),
//                ThemeManager.getColor(HOColorName.STAT_FANS),
//                UserParameter.instance().statistikFananzahl);
//        m_jchFans.setOpaque(false);
//        layout2.setConstraints(m_jchFans, constraints2);
//        panel2.add(m_jchFans);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.01;
        constraints.weighty = 0.001;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel2, constraints);
        add(panel2);

        // Graph
        JPanel panel = new ImagePanel();
        panel.setLayout(new BorderLayout());

        oStatisticsPanel = new StatistikPanel(true);
        panel.add(oStatisticsPanel);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weighty = 1.0;
        constraints.weightx = 1.0;
        constraints.anchor = GridBagConstraints.NORTH;
        panel.setBorder(BorderFactory.createLineBorder(ThemeManager
                .getColor(HOColorName.PANEL_BORDER)));
        layout.setConstraints(panel, constraints);
        add(panel);
    }

    private void initStatistik() {
        try {
            int anzahlHRF = Integer.parseInt(jtfNbWeeks.getText());
            if (anzahlHRF <= 0) {
                anzahlHRF = 1;
            }

            UserParameter.instance().statistikFinanzenAnzahlHRF = anzahlHRF;

            NumberFormat format = NumberFormat.getInstance();

            double[][] statistikWerte = DBManager.instance().getDataForFinancesStatisticsPanel(anzahlHRF); //TODO: create getClub4Statistik()
            LinesChartDataModel[] models;
            models = new LinesChartDataModel[3];

            if (statistikWerte.length > 0) {
                models[0] = new LinesChartDataModel(statistikWerte[0], "AssistantTrainerLevels",
                        jcbAssistantTrainerLevels.isSelected(), ThemeManager.getColor(HOColorName.PALETTE15[0]),
                        format);
                models[1] = new LinesChartDataModel(statistikWerte[1], "FinancialDirectorLevels",
                        jcbFinancialDirectorLevels.isSelected(),
                        ThemeManager.getColor(HOColorName.PALETTE15[1]), format);
                models[2] = new LinesChartDataModel(statistikWerte[2], "FormCoachLevels",
                        jcbFormCoachLevels.isSelected(),
                        ThemeManager.getColor(HOColorName.PALETTE15[2]), format);
//                models[3] = new StatistikModel(statistikWerte[3], "Gesamtausgaben",
//                        m_jchGesamtausgaben.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_COSTSUM), format);
//                models[4] = new StatistikModel(statistikWerte[4], "Zuschauer",
//                        m_jchZuschauer.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_INCOMESPECTATORS), format);
//                models[5] = new StatistikModel(statistikWerte[5], "Sponsoren",
//                        m_jchSponsoren.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_INCOMESPONSORS), format);
//                models[6] = new StatistikModel(statistikWerte[7], "SonstigeEinnahmen",
//                        m_jchSonstigeEinnahmen.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_INCOMETEMPORARY), format);
//                models[7] = new StatistikModel(statistikWerte[8], "Stadion",
//                        m_jchStadion.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_COSTARENA), format);
//                models[8] = new StatistikModel(statistikWerte[9], "Spielergehaelter",
//                        m_jchSpielergehaelter.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_COSTSPLAYERS), format);
//                models[9] = new StatistikModel(statistikWerte[11], "SonstigeAusgaben",
//                        m_jchSonstigeAusgaben.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_COSTTEMPORARY), format);
//                models[10] = new StatistikModel(statistikWerte[12], "Trainerstab",
//                        m_jchTrainerstab.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_COSTSTAFF), format);
//                models[11] = new StatistikModel(statistikWerte[13], "Jugend",
//                        m_jchJugend.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_COSTSYOUTH), format);
//                models[12] = new StatistikModel(statistikWerte[14], "Fans", m_jchFans.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_FANS), format2, 100);
//                models[13] = new StatistikModel(statistikWerte[15], "Marktwert",
//                        m_jchMarktwert.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_MARKETVALUE), format2, 10);
//                models[14] = new StatistikModel(statistikWerte[10], "Zinsaufwendungen",
//                        m_jchZinsaufwendungen.isSelected(),
//                        ThemeManager.getColor(HOColorName.STAT_COSTFINANCIAL), format);
            }

            String[] xAxisLabels = core.util.Helper.convertTimeMillisToFormatString(statistikWerte[16]);

            oStatisticsPanel.setAllValues(models, xAxisLabels, format, HOVerwaltung.instance()
                            .getLanguageString("Wochen"), "", jcbDataLabels.isSelected(),
                    jcbHelpLines.isSelected());
        } catch (Exception e) {
            HOLogger.instance().log(getClass(), e);
        }
    }

    private String getLangStr(String key) {
        return HOVerwaltung.instance().getLanguageString(key);
    }
}
