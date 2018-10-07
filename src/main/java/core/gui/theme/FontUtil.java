package core.gui.theme;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class FontUtil {
	
	private FontUtil() {
	}
	
    private static String checkInstalledFont(String targetFont, String sample, Font[] allfonts) {
    	if (targetFont != null) {
    		for (int j = 0; j < allfonts.length; j++) {
    			if (targetFont.equalsIgnoreCase(allfonts[j].getFontName()) && (sample == null || allfonts[j].canDisplayUpTo(sample) == -1)) {
    				return allfonts[j].getFontName();
    			}
    		}
    	}
    	return null;
    }

	public static String getFontName(final String languageFile) {
		if ("Georgian".equalsIgnoreCase(languageFile)) {
			String georgiansample = "\u10d0\u10e0\u10f0\u2013"; // different Georgian chars used by HO
			Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
			String geFont = checkInstalledFont("BPG Nino Elite Round Cond", georgiansample, allfonts); // prefer BPG_Nino_Elite_Round_Cond
			if (geFont == null) {
				geFont = checkInstalledFont("Arial Unicode MS", georgiansample, allfonts); // Arial Unicode is the 2nd option
			}
			if (geFont == null) {
				geFont = checkInstalledFont("Sylfaen", georgiansample, allfonts); // try Sylfan as 3rd
			}
			if (geFont == null) {
				for (int j = 0; j < allfonts.length; j++) {
					if (allfonts[j].canDisplayUpTo(georgiansample) == -1) {
						geFont = allfonts[j].getFontName();
						break;
					}
				}
			}
			return geFont;
		} else if ("Chinese".equalsIgnoreCase(languageFile)) {
			String chinesesample = "\u4e00\u524d\u672a\u7ecf\u80fd\u9996"; // different Chinese chars used by HO
			Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
			String chFont = checkInstalledFont("SimSun", chinesesample, allfonts); // 1. best option is SimSun
			if (chFont == null) {
				chFont = checkInstalledFont("Arial Unicode MS", chinesesample, allfonts); // 2. try to use Arial Unicode MS
			}
			if (chFont == null) { // 3. still no font found yet, check other fonts
				for (int j = 0; j < allfonts.length; j++) {
					if (allfonts[j].canDisplayUpTo(chinesesample) == -1) {
						chFont = allfonts[j].getFontName();
						break;
					}
				}
			}
			return chFont;
		} else {
			return "SansSerif";
		}
	}
}
