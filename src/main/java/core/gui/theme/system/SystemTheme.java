package core.gui.theme.system;

import core.gui.theme.Theme;
import core.util.HOLogger;

import javax.swing.*;

public class SystemTheme implements Theme {

    public final static String THEME_NAME = "System";

    @Override
    public String getName() {
        return THEME_NAME;
    }

    @Override
    public boolean loadTheme() {
        boolean success = true;

        try {
            UIManager.LookAndFeelInfo win = null;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    win = info;
                    break;
                }
            }
            if (win != null) {
                HOLogger.instance().log(getClass(), "Use " + win.getName() + " l&f");
                UIManager.setLookAndFeel(win.getClassName());
            } else {
                HOLogger.instance().log(getClass(), "Use System l&f...");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            success = false;
        }

        return success;
    }
}
