package module.hallOfFame;

import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import javax.swing.*;
import java.awt.*;

public class HallOfFamePanel extends JPanel {

    public HallOfFamePanel(){
        setLayout(new BorderLayout());
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        PlayersTable hallOfFameTable = new PlayersTable(tableModel);
        add(hallOfFameTable.getContainerComponent(), BorderLayout.CENTER);
        tableModel.initData();
    }

    public void storeUserSettings() {
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        tableModel.storeUserSettings();
    }
}
