package core.gui.theme.jgoodies;

import core.gui.theme.FontUtil;
import core.model.UserParameter;
import core.util.HOLogger;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.FontSets;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.BrownSugar;
import com.jgoodies.looks.plastic.theme.DesertBlue;
import com.jgoodies.looks.plastic.theme.DesertGreen;
import com.jgoodies.looks.plastic.theme.ExperienceGreen;
import com.jgoodies.looks.plastic.theme.ExperienceRoyale;
import com.jgoodies.looks.plastic.theme.Silver;
import com.jgoodies.looks.plastic.theme.SkyBlue;
import com.jgoodies.looks.plastic.theme.SkyGreen;
import com.jgoodies.looks.plastic.theme.SkyKrupp;


public class JGoodiesTheme {

	private JGoodiesTheme() {
	}

	public static boolean enableJGoodiesTheme(final String cfgName, final int fontSize) {
		try {
			
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				HOLogger.instance().log(JGoodiesTheme.class, "L&F: " + info.getName());
				// if ("Nimbus".equals(info.getName())) {
				// nimbus = info;
				// break;
				// }
			}

			final String fontName = FontUtil.getFontName(UserParameter.instance().sprachDatei);
			final Font userFont = new Font((fontName != null ? fontName : "SansSerif"), Font.PLAIN, fontSize);
//			final Font smallFont = new Font((fontName != null ? fontName : "SansSerif"), Font.PLAIN, (fontSize - 1));
			final Font boldFont = new Font((fontName != null ? fontName : "SansSerif"), Font.BOLD, fontSize);

			if (cfgName != null && cfgName.contains("Green")) {
				PlasticLookAndFeel.setPlasticTheme(new SkyGreen());
			} else if (cfgName != null && cfgName.contains("Green 2")) { // unused
				PlasticLookAndFeel.setPlasticTheme(new ExperienceGreen());
			} else if (cfgName != null && cfgName.contains("Green 3")) { // unused
				PlasticLookAndFeel.setPlasticTheme(new DesertGreen());
			} else if (cfgName != null && cfgName.contains("Silver")) {
				PlasticLookAndFeel.setPlasticTheme(new Silver());
			} else if (cfgName != null && cfgName.contains("Sky")) {
				PlasticLookAndFeel.setPlasticTheme(new SkyKrupp());
			} else if (cfgName != null && cfgName.contains("Brown")) { // unused
				PlasticLookAndFeel.setPlasticTheme(new BrownSugar());
			} else if (cfgName != null && cfgName.contains("Royale")) {
				PlasticLookAndFeel.setPlasticTheme(new ExperienceRoyale());
			} else if (cfgName != null && cfgName.contains("Blue 2")) { // unused
				PlasticLookAndFeel.setPlasticTheme(new DesertBlue());
			} else {
				PlasticLookAndFeel.setPlasticTheme(new SkyBlue());
			}
			FontSet fontSet = FontSets.createDefaultFontSet( //
					userFont, // control font
					userFont, // menu font
					boldFont  // title font
					);
			FontPolicy fixedPolicy = FontPolicies.createFixedPolicy(fontSet);
			PlasticLookAndFeel.setFontPolicy(fixedPolicy);
			try {
				if (System.getProperty("os.name").toLowerCase(java.util.Locale.ENGLISH).startsWith("mac")) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					Object mbUI = UIManager.get("MenuBarUI");
					Object mUI = UIManager.get("MenuUI");
					Object cbmiUI = UIManager.get("CheckBoxMenuItemUI");
					Object rbmiUI = UIManager.get("RadioButtonMenuItemUI");
					Object pmUI = UIManager.get("PopupMenuUI");

					UIManager.setLookAndFeel(new Plastic3DLookAndFeel());

					UIManager.put("MenuBarUI", mbUI);
					UIManager.put("MenuUI", mUI);
					UIManager.put("CheckBoxMenuItemUI", cbmiUI);
					UIManager.put("RadioButtonMenuItemUI", rbmiUI);
					UIManager.put("PopupMenuUI", pmUI);
				} else {
					UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		} catch (Exception e) {
			HOLogger.instance().log(JGoodiesTheme.class, e);
		}
		return false;
	}

}
