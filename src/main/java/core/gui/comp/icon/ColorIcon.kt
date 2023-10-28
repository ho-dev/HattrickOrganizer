package core.gui.comp.icon;

import javax.swing.*;
import java.awt.*;

/**
 * Icon to display overview of a colour defined by a {@link Color} instance.
 */
public class ColorIcon implements Icon {

    private final static int ICON_SIZE = 8;
    private final Color colour;

    public ColorIcon(Color colour) {
        this.colour = colour;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, ICON_SIZE, ICON_SIZE);

        g.setColor(colour);
        g.fillRect( x+1, y+1, ICON_SIZE-1, ICON_SIZE-1);
    }

    @Override
    public int getIconWidth() {
        return ICON_SIZE;
    }

    @Override
    public int getIconHeight() {
        return ICON_SIZE;
    }
}
