/**
 * 
 */
package core.gui.theme;


import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.RasenPanel;
import core.gui.theme.ho.HOClassicSchema;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
import javax.swing.UIManager;




public final class ThemeManager {
	private final static ThemeManager MANAGER = new ThemeManager();
	private File themesDir = new File("themes");
	
	HOClassicSchema 	classicSchema = new HOClassicSchema();
	private ExtSchema 	extSchema;

	private ThemeManager(){
		initialize();
	}

	public static ThemeManager instance(){
		return MANAGER;
	}
	
	private void initialize(){
		if(!themesDir.exists()){
			themesDir.mkdir();
		}
	}

	public static Color getColor(String key){
		Object obj = null;
		if(instance().extSchema != null){
			obj = instance().extSchema.getThemeColor(key);
			if(obj!= null && obj instanceof Color)
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
		return tmp.booleanValue();
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
			tmp =  UIManager.get(key);
		
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
		if(name != null && !name.equals(classicSchema.getName()))
			extSchema = loadSchema(name);
		RasenPanel.background = ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.GRASSPANEL_BACKGROUND).getImage());
		ImagePanel.background =  ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.IMAGEPANEL_BACKGROUND).getImage());
	}
	
	
	public String[] getAvailableThemeNames(){
		final String[] fileList = themesDir.list();
		final String[] schemaNames = new String[fileList.length+1];
		schemaNames[0] = classicSchema.getName();
		for (int i = 0; i < fileList.length; i++) {
			schemaNames[i+1] = fileList[i].split("[.]")[0];
		}
		return schemaNames;
	}
	
}
