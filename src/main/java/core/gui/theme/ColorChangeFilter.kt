// %2517597608:de.hattrickorganizer.tools%
package core.gui.theme;

import java.awt.Color;
import java.awt.image.RGBImageFilter;


class ColorChangeFilter extends RGBImageFilter {
    int changeRGB;
    int sourceRGB;

  ColorChangeFilter(Color source, Color change) {
        sourceRGB = source.getRGB();
        changeRGB = change.getRGB();
    }

    @Override
	public final int filterRGB(int x, int y, int rgb) {
        if (rgb == sourceRGB) {
            return changeRGB;
        } else {
            // nothing to do
            return rgb;
        }
    }
}
