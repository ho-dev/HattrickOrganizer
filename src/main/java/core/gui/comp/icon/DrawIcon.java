package core.gui.comp.icon;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * Icon that represents either a green upward triangle, or a red downward triangle
 * based on the direction chosen.
 *
 * <p>The triangle is currently used in the table league to show position evolution
 * in the league.  It cannot be resized as of yet.</p>
 */
public class DrawIcon implements Icon {
    public final static int UPWARD_DIRECTION = 1;
    public final static int DOWNWARD_DIRECTION = 0;

    private static final Color WIN_COLOR = ThemeManager.getColor(HOColorName.FORM_STREAK_WIN);
    private static final Color DEFEAT_COLOR = ThemeManager.getColor(HOColorName.FORM_STREAK_DEFEAT);

    private final int direction;

    public DrawIcon(int direction) {
        this.direction = direction;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (direction == UPWARD_DIRECTION) {
            g2.setColor(WIN_COLOR);
            g2.fillPolygon(new int[]{12, 20, 16}, new int[]{12, 12, 18}, 3);
        } else {
            g2.setColor(DEFEAT_COLOR);
            g2.fillPolygon(new int[]{12, 20, 16}, new int[]{18, 18, 12}, 3);
        }
    }

    @Override
    public int getIconWidth() {
        return 10;
    }

    @Override
    public int getIconHeight() {
        return 10;
    }
}
