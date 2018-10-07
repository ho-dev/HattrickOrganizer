package core.gui.theme;


import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;




public abstract class Schema  {

	/** cached all Strings, Boolean, Integer,  Color, Icons **/
	protected Hashtable<String, Object> cache = new Hashtable<String, Object>();

	
	public Schema(){
		
	}
	
	Schema(Properties data){
		setThemeData(data);
	}
	

	/**
	 * Sets values from .txt-file
	 * @param data
	 */
	void setThemeData(Properties data){
		cache.clear();
		Enumeration<Object> keys = data.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement().toString();
			String value = data.getProperty(key).trim();
			// i know that is not THE best solution, but currently i don´t know a better
			if(key.equalsIgnoreCase(HOBooleanName.IMAGEPANEL_BG_PAINTED))
				cache.put(key, Boolean.valueOf(value.equalsIgnoreCase("true")));
			else
				cache.put(key, createObjectFromXmlValue(value));
		}
	}
	
	private Object createObjectFromXmlValue(String value){
		if (Pattern.matches("\\d{1,3}(,\\d{1,3}){1,3}", value)){
			String[] rgb = value.split(",");
			int r = Integer.parseInt(rgb[0].trim());
			int g = Integer.parseInt(rgb[1].trim());
			int b = Integer.parseInt(rgb[2].trim());
			if(rgb.length==3)
				return new Color(r,g,b);
			else if(rgb.length==4){
				int a = Integer.parseInt(rgb[3].trim());
				return new Color(r,g,b,a);
			}
		}
		return value;
	}
	
	public Object get(String key){
		return cache.get(key);
	}
	
	protected void put(String key, Object c){
		cache.put(key,c);
	}

	
	/**
	 * extra method because color value can be a name or a Color
	 */
	public Object getThemeColor(String key){
		return cache.get(key);
	}

	public String getName() {
		return (String)cache.get("name");
	}

	public void setName(String name) {
		cache.put("name",name);
	}
	
	public abstract ImageIcon loadImageIcon(String path);
	
}
