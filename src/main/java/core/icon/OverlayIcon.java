package core.icon;

import com.github.weisj.darklaf.icons.DerivableIcon;
import core.gui.theme.ImageUtilities;

import javax.swing.*;
import java.awt.*;

public class OverlayIcon implements Icon, DerivableIcon<OverlayIcon> {

    private final Icon icon;
    private final Icon overlay;
    private final int width;
    private final int height;

    public OverlayIcon(Icon icon, Icon overlay) {
        this(icon, overlay, -1, -1);
    }

    public OverlayIcon(Icon icon, Icon overlay, int width, int height) {
        this.icon = icon;
        this.overlay = overlay;
        this.width = width > 0 ? width : Math.max(icon.getIconWidth(), overlay.getIconWidth());
        this.height = height > 0 ? width : Math.max(icon.getIconHeight(), overlay.getIconHeight());
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int iconX = x + (getIconWidth() - icon.getIconWidth()) / 2;
        int iconY = y + (getIconHeight() - icon.getIconHeight()) / 2;
        int overlayX = x + (getIconWidth() - overlay.getIconWidth()) / 2;
        int overlayY = y + (getIconHeight() - overlay.getIconHeight()) / 2;
        icon.paintIcon(c, g, iconX, iconY);
        overlay.paintIcon(c, g, overlayX, overlayY);
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public OverlayIcon derive(int width, int height) {
        Icon derivedIcon = ImageUtilities.getScaledIcon(icon, width * icon.getIconWidth() / this.width,
                                                        height * icon.getIconHeight() / this.height);
        Icon derivedOverlay = ImageUtilities.getScaledIcon(overlay, width * overlay.getIconWidth() / this.width,
                                                           height * overlay.getIconHeight() / this.height);
        return new OverlayIcon(derivedIcon, derivedOverlay, width, height);
    }
}
