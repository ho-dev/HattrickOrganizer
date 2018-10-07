// %3239344521:de.hattrickorganizer.tools.updater%
/*
 * Created on 06.04.2005
 *
 */
package tool.updater;

import core.file.ExampleFileFilter;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;



/**
 * The LanguageDialog compare all installed language.properties Files and the Files on
 * www.hoplugins.de
 *
 * @author Thorsten Dietz
 *
 * @since 1.35
 */
public final class LanguagesDialog extends UpdaterDialog {
    //~ Static fields/initializers -----------------------------------------------------------------

    /**
	 *
	 */
	private static final long serialVersionUID = -8429766970834045846L;

    //~ Instance fields ----------------------------------------------------------------------------

    protected final String WEB_LANGUAGE_DIR = UpdateController.UPDATES_URL + "/sprache/";

    protected Map<String, HPLanguageInfo> hash;

    private final String SPRACHE_DIRECTORY = System.getProperty("user.dir") + File.separator
                                             + "sprache";

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new LanguagesDialog object.
     */
    protected LanguagesDialog(Map<String, HPLanguageInfo> data) {
        super(data, HOVerwaltung.instance().getLanguageString("ls.menu.file.update") + " - " + HOVerwaltung.instance().getLanguageString("ls.menu.file.update.languagefile"));
        hash = data;
        inizialize();

        Container contenPane = getContentPane();
        contenPane.add(createTable(), BorderLayout.CENTER);
        contenPane.add(createButtons(), BorderLayout.SOUTH);
    }

    //~ Methods ------------------------------------------------------------------------------------

    @Override
	protected TableModel getModel(boolean selected, String[] columnNames2) {
        Object[][] updates = new Object[object.length][4];
        TableModel model = null;
        Properties props = null;
        String local_version = " - ";
        String hp_version = "-";
        boolean enabled = false;

        try {
            for (int i = 0; i < object.length; i++) {
                local_version = " - ";
                hp_version = "-";
                enabled = false;
                props = new java.util.Properties();

                props.load(new java.io.FileInputStream(((File) object[i])));
                local_version = props.getProperty("Version");

                HPLanguageInfo info = (HPLanguageInfo) hash.get(((File) object[i]).getName());

                if (info != null) {
                    hp_version = info.toString();
                    hash.remove(((File) object[i]).getName());
                }

                if ((local_version != null)
                    && !hp_version.equalsIgnoreCase("-")
                    && !local_version.equals(hp_version)) {
                    enabled = true;
                }

                updates[i][1] = getLabel(enabled, ((File) object[i]).getName());
                updates[i][2] = getLabel(enabled, local_version);
                updates[i][3] = getLabel(enabled, hp_version);
                updates[i][0] = getCheckbox(selected, enabled);
            }

            Object[][] newLanguages = getNewLanguages(hash, selected);
            Object[][] value = new Object[updates.length + newLanguages.length][4];

            System.arraycopy(updates, 0, value, 0, updates.length);
            System.arraycopy(newLanguages, 0, value, updates.length, newLanguages.length);

            model = new TableModel(value, columnNames2);
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }

        return model;
    }

    @Override
	protected void action() {
        if (table != null) {
            int i = 0;

            try {
                for (i = 0; i < table.getRowCount(); i++) {
                    boolean selected = ((JCheckBox) table.getValueAt(i, 0)).isSelected();

                    if (selected) {
                        File languageFile = new File(SPRACHE_DIRECTORY + File.separator
                                                     + ((JLabel) table.getValueAt(i, 1)).getText());
                        languageFile.createNewFile();

                        String url = WEB_LANGUAGE_DIR + languageFile.getName();
                        UpdateHelper.download(url, languageFile);
                    }
                     // selected
                }
                 // for

                HOMainFrame.instance().getInfoPanel().clearLangInfo();
                JOptionPane.showMessageDialog(null, HOVerwaltung.instance().getLanguageString("NeustartErforderlich"), HOVerwaltung.instance().getLanguageString("ls.menu.file.update") + " - " + HOVerwaltung.instance().getLanguageString("ls.menu.file.update.languagefile"),
                                              JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, HOVerwaltung.instance().getLanguageString("DateiNichtGefunden") + ": ",
                                              HOVerwaltung.instance().getLanguageString("ls.menu.file.update") + " - " + HOVerwaltung.instance().getLanguageString("ls.menu.file.update.languagefile"), JOptionPane.ERROR_MESSAGE);
            }
        }

        this.dispose();
    }

    private Object[][] getNewLanguages(Map<String, HPLanguageInfo> hashi, boolean selected) {
        Object[][] tmp = new Object[hashi.size()][4];
        boolean enabled = true;

        int i=0;
        for (Iterator<?> it=hashi.values().iterator(); it.hasNext(); i++) {
            HPLanguageInfo element = (HPLanguageInfo)it.next();
            tmp[i][1] = getLabel(enabled, element.getFilename());
            tmp[i][2] = getLabel(enabled, "-");
            tmp[i][3] = getLabel(enabled, element.getVersion());
            tmp[i][0] = getCheckbox(selected, enabled);
        }

        return tmp;
    }

    private void inizialize() {
        final File dir = new File(SPRACHE_DIRECTORY);
        object = dir.listFiles(new ExampleFileFilter("properties"));
        columnNames = new String[4];
        columnNames[0] = HOVerwaltung.instance().getLanguageString("ls.button.update");
        columnNames[1] = HOVerwaltung.instance().getLanguageString("Name");
        columnNames[2] = HOVerwaltung.instance().getLanguageString("update.languages.installed");
        columnNames[3] = HOVerwaltung.instance().getLanguageString("update.languages.available");
        okButtonLabel = HOVerwaltung.instance().getLanguageString("ls.button.ok");
    }
}