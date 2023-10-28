package module.transfer.ui.sorter;

import javax.swing.Icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

class Arrow implements Icon {
    private boolean descending;
    private int priority;
    private int size;

    /**
     * Creates a new Arrow object.
     *
     * @param descending
     * @param size
     * @param priority
     */
    public Arrow(boolean descending, int size, int priority) {
        this.descending = descending;
        this.size = size;
        this.priority = priority;
    }

    public int getIconHeight() {
        return size;
    }

    public int getIconWidth() {
        return size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color color = (c == null) ? Color.GRAY : c.getBackground();

        // In a compound sort, make each succesive triangle 20% 
        // smaller than the previous one. 
        int dx = (int) (size / 2 * Math.pow(0.8, priority));
        int dy = descending ? dx : (-dx);

        // Align icon (roughly) with font baseline. 
        y = y + ((5 * size) / 6) + (descending ? (-dy) : 0);

        int shift = descending ? 1 : (-1);

        g.translate(x, y);

        // Right diagonal. 
        g.setColor(color.darker());
        g.drawLine(dx / 2, dy, 0, 0);
        g.drawLine(dx / 2, dy + shift, 0, shift);

        // Left diagonal. 
        g.setColor(color.brighter());
        g.drawLine(dx / 2, dy, dx, 0);
        g.drawLine(dx / 2, dy + shift, dx, shift);

        // Horizontal line. 
        if (descending) {
            g.setColor(color.darker().darker());
        }
        else {
            g.setColor(color.brighter().brighter());
        }

        g.drawLine(dx, 0, 0, 0);

        g.setColor(color);
        g.translate(-x, -y);
    }
}
