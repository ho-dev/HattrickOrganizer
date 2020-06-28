package core.icon;

import com.github.weisj.darklaf.icons.DerivableIcon;

import javax.swing.*;
import java.awt.*;

public class TextIcon implements Icon, DerivableIcon<TextIcon> {

    private final String text;
    private final Color color;
    private final Font font;
    private final int width;
    private final int height;
    private final int baseline;

    public TextIcon(String text, Color color, Font font, int width, int height, int baseline) {
        this.text = text;
        this.color = color;
        this.font = font;
        this.width = width;
        this.height = height;
        this.baseline = baseline;
    }

    @Override
    public void paintIcon(Component c, Graphics gg, int x, int y) {
        Graphics2D g = (Graphics2D) gg.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int w = fm.stringWidth(text);
        int textX = x + (getIconWidth() - w) / 2;
        int textY = y + baseline;
        g.setColor(color);
        g.drawString(text, textX, textY);
        g.dispose();
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
    public TextIcon derive(int width, int height) {
        int base = Math.round(height * ((float) this.baseline / this.height));
        Font f = font.deriveFont(height * (font.getSize2D() / this.height));
        return new TextIcon(text, color, f, width, height, base);
    }
}
