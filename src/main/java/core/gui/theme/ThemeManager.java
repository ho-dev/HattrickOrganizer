package core.gui.theme;


import core.gui.HOMainFrame;
import core.gui.theme.dark.DarculaDarkTheme;
import core.gui.theme.dark.SolarizedDarkTheme;
import core.gui.theme.ho.HOClassicSchema;
import core.gui.theme.light.SolarizedLightTheme;
import core.gui.theme.nimbus.NimbusTheme;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.OSUtils;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;


public final class ThemeManager {
	private final static ThemeManager MANAGER = new ThemeManager();
	private final File themesDir = new File("themes");

	HOClassicSchema classicSchema = new HOClassicSchema();

	private final Map<String, Theme> themes = new LinkedHashMap<>();

	/** Name of the default theme. */
	public final static String DEFAULT_THEME_NAME = NimbusTheme.THEME_NAME;

	private ThemeManager(){
		initialize();
	}

	public static ThemeManager instance(){
		return MANAGER;
	}

	private void initialize() {
		themes.put(NimbusTheme.THEME_NAME, new NimbusTheme());
		themes.put(DarculaDarkTheme.THEME_NAME, new DarculaDarkTheme());
// Comment out those themes for now as they are not ready yet.
//		themes.put(HighContrastTheme.THEME_NAME, new HighContrastTheme());
		themes.put(SolarizedDarkTheme.THEME_NAME, new SolarizedDarkTheme());
		themes.put(SolarizedLightTheme.THEME_NAME, new SolarizedLightTheme());

		if (!themesDir.exists()) {
			themesDir.mkdir();
		}
	}

	/**
	 * Returns the list of registered themes.
	 *
	 * @return List<Theme> – List of registered themes
	 */
	public List<Theme> getRegisteredThemes() {
		return new ArrayList<>(themes.values());
	}

	public static Color getColor(String key) {
		Object obj = null;

		obj = instance().classicSchema.getThemeColor(key);
		if(obj!= null && obj instanceof Color)
			return (Color)obj;
		if(obj != null && obj instanceof String)
			return getColor(obj.toString());

		if(obj == null)
			obj = UIManager.getColor(key);

		if(obj == null)
			return instance().classicSchema.getDefaultColor(key);

		return (Color)obj;
	}

	public boolean isSet(String key){
		Boolean tmp = null;
		if(tmp == null)
			tmp = (Boolean)classicSchema.get(key);
		if(tmp == null)
			tmp = Boolean.FALSE;
		return tmp;
	}

	public void put(String key, Object value){
		classicSchema.put(key, value);
	}

	private  <T> T get(String key, Class<T> type) {
		Object obj = classicSchema.get(key);
		if (type.isInstance(obj)) {
			return type.cast(obj);
		}
		return null;
	}

	public Object get(String key){
		Object tmp = null;
		if(tmp == null)
			tmp = classicSchema.get(key);

		if(tmp == null)
			tmp = UIManager.get(key);

		return tmp;
	}

	private Icon getIconImpl(String key){
		Object tmp = classicSchema.get(key);
		if(tmp == null)
			return null;
		if(tmp instanceof Icon)
			return (Icon)tmp;
		return classicSchema.loadImageIcon(tmp.toString());
	}

	private Icon getScaledIconImpl(String key, int x, int y){
		String scaledKey = key + "(" + x + "," + y + ")";
		Icon icon = get(scaledKey, Icon.class);
		if (icon == null) {
			icon = ImageUtilities.getScaledIcon(getIconImpl(key), x, y);
			if (icon != null) put(scaledKey, icon);
		}
		return icon;
	}

	public static Icon getIcon(String key) {
		return instance().getIconImpl(key);
	}

	public static Object getIconPath(String key){
		return instance().get(key);
	}

	public static Icon getScaledIcon(String key,int x,int y){
		return instance().getScaledIconImpl(key, x, y);
	}

	public static Icon getTransparentIcon(String key,Color color){
		return instance().getTransparentImageIcon(key, color);
	}

	private Icon getTransparentImageIcon(String key,Color color){
		Icon tmp = null;
		tmp = (ImageIcon) classicSchema.get(key + "(T)");
		if (tmp == null) {
			tmp = getIconImpl(key);

			if (tmp instanceof ImageIcon) {
				tmp = new ImageIcon(ImageUtilities.makeColorTransparent(((ImageIcon)tmp).getImage(), color));
				classicSchema.put(key + "(T)", tmp);
			}
		}
		return tmp;
	}

	public static Image loadImage(String datei) {
		return instance().classicSchema.loadImageIcon(datei).getImage();
	}

	public void setCurrentTheme() {

		try {
			boolean success = false;

			Theme theme = themes.get(UserParameter.instance().skin);
			if (theme != null) {
				success = theme.loadTheme();
			}

			if (!success) {
				Theme classicTheme = themes.get(DEFAULT_THEME_NAME);
				success = classicTheme.loadTheme();
			}

			initializeMacKeyBindings(success);

		} catch (Exception e) {
			HOLogger.instance().log(HOMainFrame.class, e);
		}
	}

	private void initializeMacKeyBindings(boolean success) {
		// #177 Standard shortcuts for copy/cut/paste don't work in MacOSX if LookAndFeel changes
		if (success && OSUtils.isMac()) {
			InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
		}
	}


	public String[] getAvailableThemeNames(){
		final String[] fileList = themesDir.list();
		final String[] schemaNames = new String[fileList.length+1];
		schemaNames[0] = classicSchema.getName();
		for (int i = 0; i < fileList.length; i++) {
			schemaNames[i+1] = fileList[i].split("\\.")[0];
		}
		return schemaNames;
	}

}
