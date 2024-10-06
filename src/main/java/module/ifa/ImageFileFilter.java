package module.ifa;

import java.io.File;

import javax.swing.filechooser.FileFilter;

class ImageFileFilter extends FileFilter {
	String[] ext;

	public ImageFileFilter(String[] ext) {
		this.ext = ext;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if ((i > 0) && (i < s.length() - 1)) {
			extension = s.substring(i + 1).toLowerCase();
		}
        for (String string : this.ext) {
            if (extension.equals(string)) {
                return true;
            }
        }
        return false;
	}

	@Override
	public String getDescription() {
		return "Images";
	}
}