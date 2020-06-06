package core.gui.theme;

import core.util.HOLogger;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.ImageIcon;



public class ExtSchema extends Schema {

	private Hashtable<String, Object> contents = new Hashtable<String, Object>();
	private File themeFile;
	static String fileName = "data.txt";

	ExtSchema() {

	}

	public ExtSchema(File fileName, Properties data) {
		super(data);
		this.themeFile = fileName;
		init();
	}

	public byte[] getResource(String name) {
		return (byte[]) contents.get(name);
	}

	private void init() {
		try {
			FileInputStream fis = new FileInputStream(themeFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipInputStream zis = new ZipInputStream(bis);
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory() || ze.getName().equals(fileName)) {
					continue;
				}
				int size = (int) ze.getSize();
				byte[] b = new byte[(int) size];
				int rb = 0;
				int chunk = 0;
				while (((int) size - rb) > 0) {
					chunk = zis.read(b, rb, (int) size - rb);
					if (chunk == -1) {
						break;
					}
					rb += chunk;
				}

				contents.put(ze.getName(), b);
			}
		} catch (Exception e) {
			HOLogger.instance().log(ExtSchema.class, e);
		}
	}

	@Override
	public ImageIcon loadImageIcon(String path) {
		ImageIcon image = null;

		image = (ImageIcon) cache.get(path);
		if (image == null) {
			try {

				Image logo = Toolkit.getDefaultToolkit().createImage(getResource(path));

				if (logo == null) {
					HOLogger.instance().log(Schema.class,
							path + " Not Found!!!");
					return ThemeManager.instance().classicSchema
							.loadImageIcon("gui/bilder/Unknownflag.png");
				}
				image = new ImageIcon(logo);
				cache.put(path, image);

				return image;
			} catch (Throwable e) {
				HOLogger.instance().log(Schema.class, e);
			}
		}
		return image;
	}
	
}
