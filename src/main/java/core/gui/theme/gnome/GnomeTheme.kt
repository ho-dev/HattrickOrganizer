package core.gui.theme.gnome;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.RasenPanel;
import core.gui.theme.BaseTheme;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.gui.theme.nimbus.NimbusTheme;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.OSUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * GNOME Theme for Linux machines.
 */
public class GnomeTheme extends BaseTheme {

    public final static String THEME_NAME = "Gnome";

    @Override
    public String getName() {
        return THEME_NAME;
    }

    @Override
    public boolean loadTheme() {
        return enableTheme(UserParameter.instance().fontSize);
    }

    public boolean enableTheme(int fontSize) {
        // This is a Linux-only theme.
        if (!OSUtils.isLinux()) {
            return false;
        }

        try {

            final Optional<UIManager.LookAndFeelInfo> lafInfoOpt = Arrays.stream(UIManager.getInstalledLookAndFeels())
                    .filter(lookAndFeelInfo -> "GTK+".equals(lookAndFeelInfo.getName()))
                    .findFirst();

            if (lafInfoOpt.isPresent()) {
                final UIManager.LookAndFeelInfo lafInfo = lafInfoOpt.get();

                UIManager.setLookAndFeel(lafInfo.getClassName());
                setFont(fontSize);

                UIManager.put("Table.gridColor", Color.LIGHT_GRAY);

                RasenPanel.background = ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.GRASSPANEL_BACKGROUND));
                ImagePanel.background = ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.IMAGEPANEL_BACKGROUND));

                return true;
            }
        } catch (Exception e) {
            HOLogger.instance().log(NimbusTheme.class, e);
        }
        return false;
    }
}