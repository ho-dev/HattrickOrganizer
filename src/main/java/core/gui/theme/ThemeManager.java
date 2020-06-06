package core.gui.theme;


import core.gui.HOMainFrame;
import core.gui.theme.dark.DarculaDarkTheme;
import core.gui.theme.dark.HighContrastTheme;
import core.gui.theme.dark.SolarizedDarkTheme;
import core.gui.theme.ho.HOClassicSchema;
import core.gui.theme.ho.HOTheme;
import core.gui.theme.light.SolarizedLightTheme;
import core.gui.theme.nimbus.NimbusTheme;
import core.gui.theme.system.SystemTheme;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.OSUtils;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.*;
import javax.swing.text.*;


public final class ThemeManager {
	private final static ThemeManager MANAGER = new ThemeManager();
	private final File themesDir = new File("themes");

	HOClassicSchema classicSchema = new HOClassicSchema();
	private ExtSchema extSchema;

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
		themes.put(HOTheme.THEME_NAME, new HOTheme());
		themes.put(NimbusTheme.THEME_NAME, new NimbusTheme());
		themes.put(DarculaDarkTheme.THEME_NAME, new DarculaDarkTheme());
// Comment out those themes for now as they are not ready yet.
//		themes.put(HighContrastTheme.THEME_NAME, new HighContrastTheme());
		themes.put(SolarizedDarkTheme.THEME_NAME, new SolarizedDarkTheme());
		themes.put(SolarizedLightTheme.THEME_NAME, new SolarizedLightTheme());
		themes.put(SystemTheme.THEME_NAME, new SystemTheme());

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
		if(instance().extSchema != null){
			obj = instance().extSchema.getThemeColor(key);
			if(obj != null && obj instanceof Color)
				return (Color)obj;
			if(obj != null && obj instanceof String)
				return getColor(obj.toString());
		}

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
		if(extSchema != null)
			tmp = (Boolean)extSchema.get(key);
		if(tmp == null)
			tmp = (Boolean)classicSchema.get(key);
		if(tmp == null)
			tmp = Boolean.FALSE;
		return tmp;
	}

	public void put(String key,Object value){
		classicSchema.put(key, value);
	}

	public Object get(String key){
		Object tmp = null;
		if(extSchema != null)
			tmp = extSchema.get(key);

		if(tmp == null)
			tmp = classicSchema.get(key);

		if(tmp == null)
			tmp = UIManager.get(key);

		return tmp;
	}

	private ImageIcon getImageIcon(String key){
		Object tmp = null;
		if(extSchema != null){
			tmp = extSchema.get(key);
			if(tmp != null){
				return extSchema.loadImageIcon(tmp.toString());
			}
		}
		tmp = classicSchema.get(key);
		if(tmp == null)
			return null;
		if(tmp instanceof ImageIcon)
			return (ImageIcon)tmp;
		return classicSchema.loadImageIcon(tmp.toString());
	}

	private ImageIcon getScaledImageIcon(String key, int x, int y){
		ImageIcon tmp = null;
		if(extSchema != null){
			tmp = (ImageIcon)extSchema.get(key+"("+x+","+y+")");
			if(tmp == null){
				tmp = getImageIcon(key);

				if(tmp != null){
					tmp = new ImageIcon(tmp.getImage().getScaledInstance(x, y,Image.SCALE_SMOOTH));
					extSchema.put(key+"("+x+","+y+")",tmp);
				}
			}

		} else {
			tmp = (ImageIcon)classicSchema.get(key+"("+x+","+y+")");
			if(tmp == null){
				tmp = getImageIcon(key);

				if(tmp != null){
					tmp = new ImageIcon(tmp.getImage().getScaledInstance(x, y,Image.SCALE_SMOOTH));
					classicSchema.put(key+"("+x+","+y+")",tmp);
				}
			}
		}

		return tmp;
	}

	public static ImageIcon getIcon(String key){
		return instance().getImageIcon(key);
	}

	public static ImageIcon getScaledIcon(String key,int x,int y){
		return instance().getScaledImageIcon(key,x,y);
	}

	public static ImageIcon getTransparentIcon(String key,Color color){
		return instance().getTransparentImageIcon(key, color);
	}

	private ImageIcon getTransparentImageIcon(String key,Color color){
		ImageIcon tmp = null;
		if(extSchema != null){
			tmp = (ImageIcon)extSchema.get(key+"(T)");
			if(tmp == null){
				tmp = getImageIcon(key);

				if(tmp != null){
					tmp = new ImageIcon(ImageUtilities.makeColorTransparent(tmp.getImage(),color));
					extSchema.put(key+"(T)",tmp);
				}
			}
		} else {
			tmp = (ImageIcon)classicSchema.get(key+"(T)");
			if(tmp == null){
				tmp = getImageIcon(key);

				if(tmp != null){
					tmp = new ImageIcon(ImageUtilities.makeColorTransparent(tmp.getImage(),color));
					classicSchema.put(key+"(T)",tmp);
				}
			}
		}
		return tmp;
	}

	public static Image loadImage(String datei) {
		return instance().classicSchema.loadImageIcon(datei).getImage();
	}

	public ExtSchema loadSchema(String name) throws Exception {
		ExtSchema theme = null;
		File themeFile = new File(themesDir,name+".zip");
		if(themeFile.exists()){
			ZipFile zipFile = new ZipFile(themeFile);
			Properties p = new Properties();
			ZipEntry dataEntry = zipFile.getEntry(ExtSchema.fileName);
			if(dataEntry == null)
				throw new Exception("data.txt is missing");
			p.load(zipFile.getInputStream(dataEntry));
			theme = new ExtSchema(themeFile,p);

			// check
			Collection<Object> c = p.values();
			for (Iterator<Object> iterator = c.iterator(); iterator.hasNext();) {
				String txt = iterator.next().toString().trim();
				if(	Pattern.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)", txt)){
					ZipEntry tmp = zipFile.getEntry(txt);
						if(tmp == null)
							throw new Exception(txt+" is missing");
					}
			} // for
		} else {
			throw new Exception("File "+name+".zip is missing");
		}
		return theme;
	}

	public void setCurrentTheme(String name) throws Exception {
		if (name != null && !name.equals(classicSchema.getName())) {
			extSchema = loadSchema(name);
		}

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
