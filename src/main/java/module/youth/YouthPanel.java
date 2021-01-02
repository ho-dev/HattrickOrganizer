package module.youth;

import core.model.HOVerwaltung;

import javax.swing.*;
import java.awt.*;

public class YouthPanel extends JPanel {

    private YouthPlayerView youthPlayerView;
    private YouthTrainingView youthTrainingView;
    private JTabbedPane tabbedPane;

    public YouthPanel(){
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        youthPlayerView = new YouthPlayerView();
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Player"), new JScrollPane(this.youthPlayerView));
        youthTrainingView = new YouthTrainingView();
        this.tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Training"), new JScrollPane((this.youthTrainingView)));
        add(this.tabbedPane, BorderLayout.CENTER);
    }
}
