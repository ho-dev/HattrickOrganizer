package module.training.ui.comp;

import core.model.player.Player;
import core.training.TrainingPreviewPlayers;

import javax.swing.*;

public class PlayerNameCell extends JLabel implements Comparable<PlayerNameCell> {

    int speed;

    public PlayerNameCell(Player player, int speed) {
        super();
        this.speed = speed;
        this.setOpaque(true);
        this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        this.setText(player.getFullName());

        String tooltip = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getText();
        if (tooltip == null){
            tooltip = "";
        }
        this.setToolTipText(tooltip);
        this.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getIcon());
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public int compareTo(PlayerNameCell other) {

        if (this.getSpeed() > other.getSpeed()) {
            return -1;
        }

        if (this.getSpeed() < other.getSpeed()) {
            return 1;
        }

        return 0;
    }
}
