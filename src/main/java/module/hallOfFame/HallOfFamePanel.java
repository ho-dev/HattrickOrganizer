package module.hallOfFame;

import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import javax.swing.*;
import java.awt.*;

public class HallOfFamePanel extends JPanel {
    private PlayersTable hallOfFameTable;

    public HallOfFamePanel(){
        setLayout(new BorderLayout());
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        this.hallOfFameTable = new PlayersTable(tableModel);
        add(this.hallOfFameTable.getContainerComponent(), BorderLayout.CENTER);
        tableModel.initData();
    }

    public void storeUserSettings() {
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        tableModel.storeUserSettings();
    }
}
