package module.training.ui.comp;

import core.model.player.Player;
import core.training.TrainingPreviewPlayers;

import javax.swing.*;

public class PlayerNameCell extends JLabel {

    public PlayerNameCell(Player player) {
        super();

        this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        this.setText(player.getName());

        String tooltip = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getText();
        if (tooltip == null){
            tooltip = "";
        }
        this.setToolTipText(tooltip);
        this.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getIcon());
    }
}
