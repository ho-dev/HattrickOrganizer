package core.gui.theme.nimbus;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.RasenPanel;
import core.gui.theme.*;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.OSUtils;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;


public class NimbusTheme extends BaseTheme {

	public final static String THEME_NAME = "Nimbus";

	public String getName() {
		return THEME_NAME;
	}

	@Override
	public boolean loadTheme() {
		return enableTheme(UserParameter.instance().schriftGroesse);
	}

	public boolean enableTheme(int fontSize) {
		try {
			LookAndFeelInfo nimbus = null;
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            nimbus = info;
		            break;
		        }
		    }
			
			if (nimbus != null) {
				if (OSUtils.isMac()) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					Object mbUI = UIManager.get("MenuBarUI");
					Object mUI = UIManager.get("MenuUI");
					Object cbmiUI = UIManager.get("CheckBoxMenuItemUI");
					Object rbmiUI = UIManager.get("RadioButtonMenuItemUI");
					Object pmUI = UIManager.get("PopupMenuUI");

					UIManager.setLookAndFeel(nimbus.getClassName());

					UIManager.put("MenuBarUI", mbUI);
					UIManager.put("MenuUI", mUI);
					UIManager.put("CheckBoxMenuItemUI", cbmiUI);
					UIManager.put("RadioButtonMenuItemUI", rbmiUI);
					UIManager.put("PopupMenuUI", pmUI);
				} else {
					UIManager.setLookAndFeel(nimbus.getClassName());
				}

				setFont(fontSize);
				UIDefaults uid = UIManager.getLookAndFeelDefaults();
				
				uid.put("Table.intercellSpacing", new DimensionUIResource(1, 1));
				uid.put("Table.showGrid", Boolean.TRUE);
				uid.put("Table.gridColor", new ColorUIResource(214, 217, 223));

				BorderUIResource tableBorder = new BorderUIResource(BorderFactory.createEmptyBorder(2, 3, 2, 3));
				uid.put("Table.cellNoFocusBorder", tableBorder);
				uid.put("Table.focusCellHighlightBorder", tableBorder);

				RasenPanel.background = ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.GRASSPANEL_BACKGROUND).getImage());
				ImagePanel.background = ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.IMAGEPANEL_BACKGROUND).getImage());

				return true;
			}
		} catch (Exception e) {
			HOLogger.instance().log(NimbusTheme.class, e);
		}
		return false;
	}

}
