// %2212666334:de.hattrickorganizer.gui.utils%
package core.file;


/*
 * @(#)ExampleFileFilter.java    1.9 99/04/23
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;


/**
 * A convenience implementation of FileFilter that filters out all files except for those type
 * extensions that it knows about. Extensions are of the type ".foo", which is typically found on
 * Windows and Unix boxes, but not on Macinthosh. Case is ignored. Example - create a new filter
 * that filerts out all files but gif and jpg image files: JFileChooser chooser = new
 * JFileChooser(); ExampleFileFilter filter = new ExampleFileFilter( new String{"gif", "jpg"},
 * "JPEG & GIF Images") chooser.addChoosableFileFilter(filter); chooser.showOpenDialog(this);
 *
 * @author Jeff Dinkins
 * @version 1.9 04/23/99
 */
public class ExampleFileFilter extends javax.swing.filechooser.FileFilter
    implements java.io.FileFilter
{
    //~ Instance fields ----------------------------------------------------------------------------

    private Hashtable<String,ExampleFileFilter> filters;
    private String description;
    private String fullDescription;
    private boolean useExtensionsInDescription = true;
    private boolean ignoreDirectories = false;
    
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a file filter. If no filters are added, then all files are accepted.
     *
     * @see #addExtension
     */
    public ExampleFileFilter() {
        this.filters = new Hashtable<>();
    }

    /**
     * Creates a file filter that accepts files with the given extension. Example: new
     * ExampleFileFilter("jpg");
     *
     * @see #addExtension
     */
    public ExampleFileFilter(String extension) {
        this(extension, null);
    }

    /**
     * Creates a file filter that accepts the given file type. Example: new
     * ExampleFileFilter("jpg", "JPEG Image Images"); Note that the "." before the extension is
     * not needed. If provided, it will be ignored.
     *
     * @see #addExtension
     */
    public ExampleFileFilter(String extension, String description) {
        this();

        if (extension != null) {
            addExtension(extension);
        }

        if (description != null) {
            setDescription(description);
        }
    }

    /**
     * Creates a file filter from the given string array. Example: new ExampleFileFilter(String
     * {"gif", "jpg"}); Note that the "." before the extension is not needed adn will be ignored.
     *
     * @see #addExtension
     */
    public ExampleFileFilter(String[] filters) {
        this(filters, null);
    }

    /**
     * Creates a file filter from the given string array and description. Example: new
     * ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG Images"); Note that the "." before
     * the extension is not needed and will be ignored.
     *
     * @see #addExtension
     */
    public ExampleFileFilter(String[] filters, String description) {
        this();

        for (String filter : filters) {
            // add filters one by one
            addExtension(filter);
        }

        if (description != null) {
            setDescription(description);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the human readable description of this filter. For example: filter.setDescription("Gif
     * and JPG Images");
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     */
    public final void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }

    /**
     * Returns the human readable description of this filter. For example: "JPEG and GIF Image
     * Files (.jpg, .gif)"
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     * @see FileFilter#getDescription
     */
    @Override
	public final String getDescription() {
        if (fullDescription == null) {
            if ((description == null) || isExtensionListInDescription()) {
                fullDescription = (description == null) ? "(" : (description + " (");

                // build the description from the extension list
                final Enumeration<String> extensions = filters.keys();

                if (extensions != null) {
                    fullDescription += ("." + (String) extensions.nextElement());

                    while (extensions.hasMoreElements()) {
                        fullDescription += (", " + (String) extensions.nextElement());
                    }
                }

                fullDescription += ")";
            } else {
                fullDescription = description;
            }
        }

        return fullDescription;
    }

    /**
     * Return the extension portion of the file's name .
     */
    public final String getExtension(File f) {
        if (f != null) {
            final String filename = f.getName();
            final int i = filename.lastIndexOf('.');

            if ((i > 0) && (i < (filename.length() - 1))) {
                return filename.substring(i + 1).toLowerCase(Locale.ENGLISH);
            }

        }

        return null;
    }

    /**
     * Determines whether the extension list (.jpg, .gif, etc) should show up in the human readable
     * description. Only relevent if a description was provided in the constructor or using
     * setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see isExtensionListInDescription
     */
    public final void setExtensionListInDescription(boolean b) {
        useExtensionsInDescription = b;
        fullDescription = null;
    }

    /**
     * Returns whether the extension list (.jpg, .gif, etc) should show up in the human readable
     * description. Only relevent if a description was provided in the constructor or using
     * setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see setExtensionListInDescription
     */
    public final boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }

    /**
     * Return true if this file should be shown in the directory pane, false if it shouldn't. Files
     * that begin with "." are ignored.
     */
    @Override
	public final boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory() && !ignoreDirectories) {
                return true;
            }

            final String extension = getExtension(f);

            if ((extension != null) && (filters.get(getExtension(f)) != null)) {
                return true;
            }

        }

        return false;
    }

    /**
     * Adds a filetype "dot" extension to filter against. For example: the following code will
     * create a filter that filters out all files except those that end in ".jpg" and ".tif":
     * ExampleFileFilter filter = new ExampleFileFilter(); filter.addExtension("jpg");
     * filter.addExtension("tif"); Note that the "." before the extension is not needed and will
     * be ignored.
     */
    public final void addExtension(String extension) {
        if (filters == null) {
            filters = new Hashtable<>(5);
        }

        filters.put(extension.toLowerCase(Locale.ENGLISH), this);
        fullDescription = null;
    }

	public boolean isIgnoreDirectories() {
		return ignoreDirectories;
	}

	public void setIgnoreDirectories(boolean ignoreDirectories) {
		this.ignoreDirectories = ignoreDirectories;
	}
}
