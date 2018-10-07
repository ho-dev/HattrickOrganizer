// %2712983001:de.hattrickorganizer.tools.updater%
/*
 * Created on 21.07.2004
 *
 */
package tool.updater;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * Value-Object of information about the plugins on hoplugins.de
 *
 * @author Thorsten Dietz
 *
 * @since 1.35
 */
final class HPPluginInfo {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static String TAG_VERSION = "version";
    private static String TAG_FILE = "datei";
    private static String TAG_NAME = "name";
    private static String TAG_PLUGIN_ID = "plugin_id";
    private static String TAG_HOVERSION = "hoversion";
    private static String TAG_VISIBLE = "v";
    private static String TAG_UPDATETEXT = "updatetext";

    //~ Instance fields ----------------------------------------------------------------------------

    private String hoversion = "";
    private String name;
    private String updateText = "";
    private String zipFileName;
    private boolean visible = true;
    private double version;
    private int pluginId;

    @Override
	public String toString() {
        if (name != null) {
            return name;
        }

        return "?";
    }

    protected String getHoversion() {
        return hoversion;
    }

    protected double getHoversionAsDouble() {
        if (hoversion.equals("-")) {
            return 0.0d;
        }

        return Double.parseDouble(hoversion);
    }

    protected static HPPluginInfo getInstance(NodeList elements) {
        HPPluginInfo hpPluginInfo = new HPPluginInfo();

        try {
            for (int i = 0; i < elements.getLength(); i++) {
                if (elements.item(i) instanceof Element) {
                    Element element = (Element) elements.item(i);
                    Text txt = (Text) element.getFirstChild();

                    if (txt != null) {
                        if (element.getTagName().equals(TAG_PLUGIN_ID)) {
                            hpPluginInfo.setPluginId(txt.getData().trim());
                        }

                        if (element.getTagName().equals(TAG_NAME)) {
                            hpPluginInfo.setName(txt.getData().trim());
                        }

                        if (element.getTagName().equals(TAG_FILE)) {
                            hpPluginInfo.setZipFileName(txt.getData().trim());
                        }

                        if (element.getTagName().equals(TAG_VERSION)) {
                            hpPluginInfo.setVersion(txt.getData().trim());
                        }

                        if (element.getTagName().equals(TAG_HOVERSION)) {
                            hpPluginInfo.setHoversion(txt.getData().trim());
                        }

                        if (element.getTagName().equals(TAG_UPDATETEXT)) {
                            hpPluginInfo.setUpdateText(txt.getData().trim());
                        }

                        if (element.getTagName().equals(TAG_VISIBLE)) {
                            hpPluginInfo.setVisible(txt.getData().trim());
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }

        return hpPluginInfo;
    }

    protected String getName() {
        return name;
    }

    protected int getPluginId() {
        return pluginId;
    }

    protected String getUpdateText() {
        return updateText;
    }

    protected void setVersion(String version) throws Exception {
        this.version = Double.parseDouble(version);
    }

    protected double getVersion() {
        return version;
    }

    protected void setVisible(String value) {
        visible = value.equals("0") ? true : false;
    }

    protected boolean isVisible() {
        return visible;
    }

    protected String getZipFileName() {
        return zipFileName;
    }

    private void setHoversion(String hoversion) {
        this.hoversion = hoversion;
    }

    private void setName(String string) {
        name = string;
    }

    private void setPluginId(String pluginId) {
        this.pluginId = Integer.parseInt(pluginId);
    }

    private void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    private void setZipFileName(String string) {
        zipFileName = string;
    }
}
