package tool.updater;

import core.util.HOLogger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Value-Object about Language-files on hoplugins.de
 *
 * @author Thorsten Dietz
 *
 * @since 1.35
 */
final class HPLanguageInfo {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final String TAG_FILE = "datei";
    private static final String TAG_ID = "lang_id";
    private static final String TAG_VERSION = "version";

    //~ Instance fields ----------------------------------------------------------------------------

    private String filename;
    private String version;
    private int id;

    //~ Methods ------------------------------------------------------------------------------------

    @Override
	public String toString() {
        if (version == null) {
            return "-";
        }

        return version;
    }

    protected String getFilename() {
        return filename;
    }

    protected int getId() {
        return id;
    }

    protected String getVersion() {
        return version;
    }

    protected static HPLanguageInfo instance(NodeList elements) {
        HPLanguageInfo hpPluginInfo = new HPLanguageInfo();

        try {
            for (int i = 0; i < elements.getLength(); i++) {
                if (elements.item(i) instanceof Element) {
                    Element element = (Element) elements.item(i);
                    Text txt = (Text) element.getFirstChild();

                    if (txt != null) {
                        if (element.getTagName().equals(TAG_FILE)) {
                            hpPluginInfo.setFilename(txt.getData().trim());
                        }

                        if (element.getTagName().equals(TAG_ID)) {
                            hpPluginInfo.setId(txt.getData().trim());
                        }

                        if (element.getTagName().equals(TAG_VERSION)) {
                            hpPluginInfo.setVersion(txt.getData().trim());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            HOLogger.instance().log(HPLanguageInfo.class,ex);
        }

        return hpPluginInfo;
    }

    private void setFilename(String filename) {
        this.filename = filename;
    }

    private void setId(String newId) throws Exception {
        id = Integer.parseInt(newId);
    }

    private void setVersion(String version) {
        this.version = version;
    }
}
