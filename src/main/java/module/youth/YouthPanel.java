package module.youth;

import core.model.TranslationFacility;
import core.playername.Location;
import core.playername.NameFormat;
import core.playername.PlayerNameSettingsManager;

import javax.swing.*;
import java.awt.*;

public class YouthPanel extends JPanel {

    private final YouthPlayerView youthPlayerView;
    private final YouthTrainingView youthTrainingView;
    private final NameFormat nameFormat = PlayerNameSettingsManager.instance().getFormat(Location.YOUTH_PLAYER);

    public YouthPanel() {
        setLayout(new BorderLayout());
        youthPlayerView = new YouthPlayerView(nameFormat);
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
