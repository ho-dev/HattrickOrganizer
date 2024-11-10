package module.youth;

import core.model.TranslationFacility;

import javax.swing.*;
import java.awt.*;

public class YouthPanel extends JPanel {

    private YouthPlayerView youthPlayerView;
    private YouthTrainingView youthTrainingView;

    public YouthPanel() {
        setLayout(new BorderLayout());
        youthPlayerView = new YouthPlayerView();
        var tabbedPane = new JTabbedPane();
        tabbedPane.addTab(TranslationFacility.tr("ls.youth.player"), this.youthPlayerView);
        youthTrainingView = new YouthTrainingView();
        tabbedPane.addTab(TranslationFacility.tr("ls.youth.training"), this.youthTrainingView.getContainerComponent());
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void storeUserSettings() {
        youthPlayerView.storeUserSettings();
        youthTrainingView.storeUserSettings();
    }

    public void refreshYouthPlayerView() {
        youthPlayerView.refresh();
    }
}
