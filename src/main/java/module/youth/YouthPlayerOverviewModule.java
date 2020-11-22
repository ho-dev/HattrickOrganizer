package module.youth;

import core.model.HOVerwaltung;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class YouthPlayerOverviewModule extends DefaultModule {

    public YouthPlayerOverviewModule() {
        super(true);
    }

    @Override
    public int getModuleId() {
        return YOUTHPLAYEROVERVIEW;
    }

    @Override
    public String getDescription() {
        return HOVerwaltung.instance().getLanguageString("YouthPlayerOverview");
    }

    @Override
    public JPanel createTabPanel() {
        return new YouthPlayerOverviewPanel();
    }

    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0);
    }

}
