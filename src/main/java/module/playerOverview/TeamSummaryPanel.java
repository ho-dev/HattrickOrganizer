package module.playerOverview;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;
import core.util.Helper;

import javax.swing.*;
import java.util.Vector;

/**
 * This panel displays team summary below the list of players in the squad.
 */
public class TeamSummaryPanel extends ImagePanel {

    private final JLabel numPlayerLabel = new JLabel();
    private final JLabel averageAgeLabel = new JLabel();
    private final JLabel averageSalaryLabel = new JLabel();
    private final JLabel totalTsiLabel = new JLabel();
    private final JLabel averageTsiLabel = new JLabel();
    private final JLabel averageStaminaLabel = new JLabel();
    private final JLabel averageFormLabel = new JLabel();

    private Vector<Player> players;

    public TeamSummaryPanel(Vector<Player> players) {
        this.players = players;
        initComponents();
        reInit();
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

    private void createField(String labelName, JLabel fieldLabel) {
        JLabel label;
        label = new JLabel(labelName);
        this.add(label);
        this.add(Box.createHorizontalStrut(10));
        this.add(fieldLabel);
        this.add(Box.createHorizontalStrut(25));
    }

    public void setPlayers(Vector<Player> players) {
        this.players = players;
    }

    public void reInit() {
        long totalTsi = players.stream().mapToLong(Player::getTSI).sum();
        double averageTsi = players.stream().mapToDouble(Player::getTSI).average().orElse(0.0);
        double averageAge = players.stream().mapToDouble(Player::getAlterWithAgeDays).average().orElse(0.0);
        double averageSalary = players.stream().mapToDouble(Player::getGehalt).average().orElse(0.0) / UserParameter.instance().faktorGeld;

        double averageStamina = players.stream().mapToDouble(Player::getKondition).average().orElse(0.0);
        double averageForm = players.stream().mapToDouble(Player::getForm).average().orElse(0.0);

        numPlayerLabel.setText(String.valueOf(players.size()));
        averageAgeLabel.setText(Helper.getNumberFormat(false, 1)
                .format(Helper.round(averageAge, 1)));
        averageSalaryLabel.setText(Helper.getNumberFormat(true, 2)
                .format(Helper.round(averageSalary, 2)));
        totalTsiLabel.setText(Helper.getNumberFormat(false, 0)
                .format(Helper.round(totalTsi, 0)));
        averageTsiLabel.setText(Helper.getNumberFormat(false, UserParameter.instance().nbDecimals)
                .format(Helper.round(averageTsi, 2)));
        averageStaminaLabel.setText(Helper.getNumberFormat(false, UserParameter.instance().nbDecimals)
                .format(Helper.round(averageStamina, 2)));
        averageFormLabel.setText(Helper.getNumberFormat(false, UserParameter.instance().nbDecimals)
                .format(Helper.round(averageForm, 2)));
    }
}
