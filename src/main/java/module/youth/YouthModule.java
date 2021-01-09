package module.youth;

import core.model.HOVerwaltung;
import core.module.DefaultModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class YouthModule extends DefaultModule {

    public YouthModule() {
        super(true);
    }

    @Override
    public int getModuleId() {
        return YOUTH;
    }

    @Override
    public String getDescription() {
        return HOVerwaltung.instance().getLanguageString("ls.youth");
    }

    @Override
    public JPanel createTabPanel() {
//        return new YouthPanel();
        return new JPanel();
    }

    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0);
    }

}
