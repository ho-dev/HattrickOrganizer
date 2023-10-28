// %400793514:de.hattrickorganizer.tools%
package core.gui.theme;

import java.awt.Color;
import java.awt.image.RGBImageFilter;


public class LightGrayFilter extends RGBImageFilter {
    float graywert = 0.5f;

    public LightGrayFilter(float graywert) {
        this.graywert = graywert;
    }

    @Override
	public final int filterRGB(int x, int y, int rgb) {
        final Color color = new Color(rgb);
        final Color color2 = new Color((int) (color.getRed() * graywert),
                                       (int) (color.getGreen() * graywert),
                                       (int) (color.getBlue() * graywert));
        return color2.getRGB();
    }
}
