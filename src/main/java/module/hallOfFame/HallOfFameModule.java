package module.hallOfFame;

import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class HallOfFameModule extends DefaultModule {

    private HallOfFamePanel hallOfFamePanel;

    public HallOfFameModule() {
        super(true);
    }

    @Override
    public int getModuleId() {
        return HALL_OF_FAME;
    }

    @Override
    public String getDescription() {
        return TranslationFacility.tr("ls.HallOfFame");
    }

    @Override
    public JPanel createTabPanel() {
        this.hallOfFamePanel = new HallOfFamePanel();
        return this.hallOfFamePanel;
    }

    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_MASK);
    }

    @Override
    public void storeUserSettings() {
        if (this.hallOfFamePanel != null) {
            this.hallOfFamePanel.storeUserSettings();
        }
    }

}
