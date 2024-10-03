package module.youth;

import core.model.TranslationFacility;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class YouthModule extends DefaultModule {

    private YouthPanel youthPanel;
    public YouthModule() {
        super(true);
    }

    @Override
    public int getModuleId() {
        return YOUTH;
    }

    @Override
    public String getDescription() {
        return TranslationFacility.tr("ls.youth");
    }

    @Override
    public JPanel createTabPanel() {
        this.youthPanel = new YouthPanel();
        return this.youthPanel;
    }

    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0);
    }

    @Override
    public void storeUserSettings()
    {
        if ( youthPanel != null) youthPanel.storeUserSettings();
    }

}
