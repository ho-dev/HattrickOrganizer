package module.youth;

import core.db.YouthScoutCommentTable;
import core.gui.comp.panel.ImagePanel;
import module.playerOverview.PlayerOverviewTable;

import javax.swing.*;
import java.awt.*;

public class YouthPlayerOverviewPanel extends ImagePanel {
    private YouthPlayerOverviewTable youthPlayerOverviewTable;

    public YouthPlayerOverviewPanel(){
        setLayout(new BorderLayout());

        JPanel overviewPanel = new JPanel();
        overviewPanel.setLayout(new BorderLayout());

        youthPlayerOverviewTable = new YouthPlayerOverviewTable();
        JScrollPane scrollpane = new JScrollPane(youthPlayerOverviewTable);
        overviewPanel.add(scrollpane);
        this.add(overviewPanel);
    }
}
