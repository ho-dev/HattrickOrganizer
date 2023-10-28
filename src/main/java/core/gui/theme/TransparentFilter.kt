// %1953378511:de.hattrickorganizer.tools%
package core.gui.theme;

import java.awt.Color;
import java.awt.image.RGBImageFilter;


class TransparentFilter extends RGBImageFilter {
    public int markerRGB;

    TransparentFilter(Color transparentColor) {
        markerRGB = transparentColor.getRGB() | 0xFF000000;
    }

    @Override
	public final int filterRGB(int x, int y, int rgb) {
        if ((rgb | 0xFF000000) == markerRGB) {
            // Mark the alpha bits as zero - transparent
            return 0x00FFFFFF & rgb;
        } else {
            // nothing to do
            return rgb;
        }
    }
}
