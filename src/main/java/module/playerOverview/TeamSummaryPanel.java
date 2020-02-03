package module.playerOverview;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.panel.DoubleLabelPanel;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;
import core.util.Helper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Vector;

/**
 * This panel displays team summary below the list of players in the squad.
 */
public class TeamSummaryPanel extends ImagePanel implements ChangeListener, Refreshable {

    private final DoubleLabelPanel numPlayerLabel = new DoubleLabelPanel();
    private final DoubleLabelPanel averageAgeLabel = new DoubleLabelPanel();
    private final DoubleLabelPanel averageSalaryLabel = new DoubleLabelPanel();
    private final DoubleLabelPanel totalTsiLabel = new DoubleLabelPanel();
    private final DoubleLabelPanel averageTsiLabel = new DoubleLabelPanel();
    private final DoubleLabelPanel averageStaminaLabel = new DoubleLabelPanel();
    private final DoubleLabelPanel averageFormLabel = new DoubleLabelPanel();

    // Darker shared of green for better visibility of greyish bg.
    private final static Color DARK_GREEN = new Color(0, 102, 0);

    private TeamSummaryModel model;

    public TeamSummaryPanel(TeamSummaryModel model) {
        initComponents();
        RefreshManager.instance().registerRefreshable(this);
        setModel(model);
    }

    private void initComponents() {
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        final BoxLayout layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(layout);

        createField(HOVerwaltung.instance().getLanguageString("ls.team.numplayers"), numPlayerLabel);
        createField(HOVerwaltung.instance().getLanguageString("ls.team.averageage"), averageAgeLabel);
        createField(HOVerwaltung.instance().getLanguageString("ls.team.averagesalary"), averageSalaryLabel);
        createField(HOVerwaltung.instance().getLanguageString("ls.team.totaltsi"), totalTsiLabel);
        createField(HOVerwaltung.instance().getLanguageString("ls.team.averagetsi"), averageTsiLabel);
        createField(HOVerwaltung.instance().getLanguageString("ls.team.averagestamina"), averageStaminaLabel);
        createField(HOVerwaltung.instance().getLanguageString("ls.team.averageform"), averageFormLabel);
    }

    private void createField(String labelName, JComponent fieldLabel) {
        JLabel label;
        label = new JLabel(labelName);
        this.add(label);
        this.add(Box.createHorizontalStrut(10));
        this.add(fieldLabel);
        this.add(Box.createHorizontalStrut(15));
    }

    private void setComparisonField(Number val, DoubleLabelPanel label, int numDecimals) {
        if (val != null && val.floatValue() != 0.0) {
            JLabel rightLabel = new JLabel();

            String value;
            if (numDecimals == 0) {
                value = String.format("%d", val.intValue());
            } else {
                value = String.format("%.2f", val.floatValue());
            }
            if (val.floatValue() > 0.0) {
                value = "+" + value;
                rightLabel.setForeground(DARK_GREEN);
            } else {
                rightLabel.setForeground(Color.RED);
            }
            rightLabel.setText(value);

            label.setRightLabel(rightLabel);
        } else {
            label.setRightLabel(new JLabel());
        }
    }

    public void setModel(TeamSummaryModel model) {
        this.model = model;
        reInit();
    }

    @Override
    public void reInit() {
        model.setComparisonPlayers(null);
        display();
    }

    private void display() {
        TeamSummaryModel.TeamStatistics stats = model.getTeamStatistics();
        TeamSummaryModel.TeamStatistics comparisonStats = model.getComparisonTeamStatistics();

        JLabel numPlayerLeftLabel = new JLabel();
        numPlayerLeftLabel.setText(String.valueOf(stats.numPlayers));
        numPlayerLabel.setLeftLabel(numPlayerLeftLabel);

        setComparisonField(comparisonStats.numPlayers, numPlayerLabel, 0);

        JLabel averageAgeLeftLabel = new JLabel();
        averageAgeLeftLabel.setText(Helper.getNumberFormat(false, 1)
                .format(Helper.round(stats.averageAge, 1)));
        averageAgeLabel.setLeftLabel(averageAgeLeftLabel);

        setComparisonField(comparisonStats.averageAge, averageAgeLabel, 2);

        JLabel averageSalaryLeftLabel = new JLabel();
        averageSalaryLeftLabel.setText(Helper.getNumberFormat(true, 2)
                .format(Helper.round(stats.averageSalary, 2)));
        averageSalaryLabel.setLeftLabel(averageSalaryLeftLabel);

        setComparisonField(comparisonStats.averageSalary, averageSalaryLabel, 0);

        JLabel totalTsiLeftLabel = new JLabel();
        totalTsiLeftLabel.setText(Helper.getNumberFormat(false, 0)
                .format(Helper.round(stats.totalTsi, 0)));
        totalTsiLabel.setLeftLabel(totalTsiLeftLabel);

        setComparisonField(comparisonStats.totalTsi, totalTsiLabel, 0);

        JLabel averageTsiLeftLabel = new JLabel();
        averageTsiLeftLabel.setText(Helper.getNumberFormat(false, UserParameter.instance().nbDecimals)
                .format(Helper.round(stats.averageTsi, 2)));
        averageTsiLabel.setLeftLabel(averageTsiLeftLabel);

        setComparisonField(comparisonStats.averageTsi, averageTsiLabel, 0);

        JLabel averageStaminaLeftLabel = new JLabel();
        averageStaminaLeftLabel.setText(Helper.getNumberFormat(false, UserParameter.instance().nbDecimals)
                .format(Helper.round(stats.averageStamina, 2)));
        averageStaminaLabel.setLeftLabel(averageStaminaLeftLabel);

        setComparisonField(comparisonStats.averageStamina, averageStaminaLabel, 2);

        JLabel averageFormLeftLabel = new JLabel();
        averageFormLeftLabel.setText(Helper.getNumberFormat(false, UserParameter.instance().nbDecimals)
                .format(Helper.round(stats.averageForm, 2)));
        averageFormLabel.setLeftLabel(averageFormLeftLabel);

        setComparisonField(comparisonStats.averageForm, averageFormLabel, 2);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Vector<Player> comparisonPlayers = SpielerTrainingsVergleichsPanel.getVergleichsPlayer();
        if (comparisonPlayers != null) {
            model.setComparisonPlayers(comparisonPlayers);
            display();
        }
    }

    @Override
    public void refresh() {
    }
}
