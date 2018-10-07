// %1272568856:de.hattrickorganizer.tools%
package core.gui.theme;

import java.awt.Color;
import java.awt.image.RGBImageFilter;


class FuzzyTransparentFilter extends RGBImageFilter {
    private int m_iMaxBlue;
    private int m_iMaxGreen;
    private int m_iMaxRed;
    private int m_iMinBlue;
    private int m_iMinGreen;
    private int m_iMinRed;

    public FuzzyTransparentFilter(int minred, int mingreen, int minblue, int maxred, int maxgreen,
                                  int maxblue) {
        //markerRGB = transparentColor.getRGB() | 0xFF000000;
        m_iMinRed = minred;
        m_iMinGreen = mingreen;
        m_iMinBlue = minblue;
        m_iMaxRed = maxred;
        m_iMaxGreen = maxgreen;
        m_iMaxBlue = maxblue;
    }

    //~ Methods ------------------------------------------------------------------------------------

    @Override
	public final int filterRGB(int x, int y, int rgb) {
        final Color rgbColor = new Color(rgb);

        if (((rgbColor.getRed() >= m_iMinRed) && (rgbColor.getRed() <= m_iMaxRed))
            && ((rgbColor.getGreen() >= m_iMinGreen) && (rgbColor.getGreen() <= m_iMaxGreen))
            && ((rgbColor.getBlue() >= m_iMinBlue) && (rgbColor.getBlue() <= m_iMaxBlue))) {
            // Mark the alpha bits as zero - transparent
            return 0x00FFFFFF & rgb;
        } else {
            // nothing to do
            return rgb;
        }
    }
}
