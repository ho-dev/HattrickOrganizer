package module.youth;

import core.db.YouthScoutCommentTable;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import module.matches.AufstellungsSternePanel;
import module.matches.MatchesModel;
import module.playerOverview.PlayerOverviewTable;

import javax.swing.*;
import java.awt.*;

public class YouthPanel extends JPanel {

    private YouthPlayerOverviewTable youthPlayerOverviewTable;
    private JTabbedPane tabbedPane;

    public YouthPanel(){
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        youthPlayerOverviewTable = new YouthPlayerOverviewTable();
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Player"), new JScrollPane(this.youthPlayerOverviewTable));
        add(this.tabbedPane, BorderLayout.CENTER);
    }
}
