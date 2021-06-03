package core.file.hrf;

import core.db.DBManager;
import core.file.ExampleFileFilter;
import core.gui.HOMainFrame;
import core.gui.InfoPanel;
import core.gui.RefreshManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.Helper;
import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Imports a specified HRF file.
 */
public class HRFImport {

	public HRFImport(HOMainFrame frame) {

		File[] files = getHRFFiles(frame);
		if (files != null) {
			Timestamp olderHrf = new Timestamp(System.currentTimeMillis());
			HOModel homodel;

			UserChoice choice = null;
			for (int i = 0; i < files.length; i++) {
				files[i].getPath();
				// Endung nicht hrf?
				if (!files[i].getPath().endsWith(".hrf")) {
					files[i] = new File(files[i].getAbsolutePath() + ".hrf");
				}

				// Datei schon vorhanden?
				if (!files[i].exists()) {
					// Info
					frame.setInformation(getLangStr("DateiNichtGefunden"),
							InfoPanel.FEHLERFARBE);

					// Fehler
					Helper.showMessage(frame, getLangStr("DateiNichtGefunden"),
							getLangStr("Fehler"), JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Pfad speichern
				UserParameter.instance().hrfImport_HRFPath = files[i].getParentFile()
						.getAbsolutePath();

				// Info
				frame.setInformation(getLangStr("StartParse"));

				// HRFParser
				homodel = HRFFileParser.parse(files[i]);

				if (homodel == null) {
					// Info
					frame.setInformation(
							getLangStr("Importfehler") + " : " + files[i].getName(),
							InfoPanel.FEHLERFARBE);

					// Fehler
					Helper.showMessage(frame, getLangStr("Importfehler"), getLangStr("Fehler"),
							JOptionPane.ERROR_MESSAGE);
				} else {
					// Info
					frame.setInformation(getLangStr("HRFSave"));

					// file already imported?
					java.sql.Timestamp HRFts = homodel.getBasics().getDatum();
					String oldHRFName = DBManager.instance().getHRFName4Date(HRFts);

					if (choice == null || !choice.applyToAll ) {
						choice = bStoreHRF(frame, HRFts, oldHRFName);
						if (choice.cancel) {
							// chaneled -> bail out here
							break;
						}
					}

					// Speichern
					if (choice.importHRF) {
						// Saven
						homodel.saveHRF();

						if (homodel.getBasics().getDatum().before(olderHrf)) {
							olderHrf = new Timestamp(homodel.getBasics().getDatum().getTime());
						}

						// Info
						frame.setInformation(getLangStr("HRFErfolg"));
					}
					// Abbruch
					else {
						// Info
						frame.setInformation(getLangStr("HRFAbbruch"),
								InfoPanel.FEHLERFARBE);
					}
				}
			}

			DBManager.instance().reimportSkillup();
			HOVerwaltung.instance().loadLatestHoModel();

			HOModel hom = HOVerwaltung.instance().getModel();

			// Refreshen aller Fenster
			RefreshManager.instance().doReInit();
		}
	}

	private File[] getHRFFiles(Frame parent) {
		// Filechooser
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle(getLangStr("ls.menu.file.importfromhrf"));

		File pfad = new File(UserParameter.instance().hrfImport_HRFPath);

		if (pfad.exists() && pfad.isDirectory()) {
			fileChooser.setCurrentDirectory(new File(UserParameter.instance().hrfImport_HRFPath));
		}

		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("hrf");
		filter.setDescription(HOVerwaltung.instance().getLanguageString("filetypedescription.hrf"));
		fileChooser.setFileFilter(filter);

		if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFiles();
		}
		return null;
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
	
	private UserChoice bStoreHRF(Component parent, Timestamp HRF_date, String oldHRFName) {
		UserChoice choice = new UserChoice();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		String text = getLangStr("HRFfrom") + " " + dateFormat.format(HRF_date);

		if (oldHRFName!=null)
		{text += "\n(" + getLangStr("HRFinDB") + " " + oldHRFName + ")";}

		text += "\n" + getLangStr("ErneutImportieren");

		JCheckBox applyToAllCheckBox = new JCheckBox(getLangStr("hrfImport.applyToAll"));
		Object[] o = {text, applyToAllCheckBox};
		int value = JOptionPane.showConfirmDialog(parent, o, getLangStr("confirmation.title"), JOptionPane.YES_NO_CANCEL_OPTION);
		
		if (value == JOptionPane.CANCEL_OPTION) {
			choice.cancel = true;
		} else {
			choice.applyToAll = applyToAllCheckBox.isSelected();
			if (value == JOptionPane.YES_OPTION) {
				choice.importHRF = true;
			}
		}
		return choice;
	}
	
	private class UserChoice {
		boolean importHRF;
		boolean applyToAll;
		boolean cancel;
	}
}
