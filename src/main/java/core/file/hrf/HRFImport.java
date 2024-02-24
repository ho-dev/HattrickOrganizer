package core.file.hrf;

import core.db.DBManager;
import core.file.ExampleFileFilter;
import core.gui.HOMainFrame;
import core.gui.InfoPanel;
import core.gui.RefreshManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.HODateTime;
import core.util.HOLogger;
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
 * Imports selected HRF files.
 */
public class HRFImport {

	public HRFImport(HOMainFrame frame) {

		File[] files = getHRFFiles(frame);
		if (files != null) {
			HOModel homodel;

			UserChoice choice = null;
			for (int i = 0; i < files.length; i++) {
				if (!files[i].getPath().endsWith(".hrf")) {
					files[i] = new File(files[i].getAbsolutePath() + ".hrf");
				}

				if (!files[i].exists()) {
					frame.setInformation(getLangStr("DateiNichtGefunden"), InfoPanel.FEHLERFARBE);
					Helper.showMessage(frame, getLangStr("DateiNichtGefunden"), getLangStr("Fehler"), JOptionPane.ERROR_MESSAGE);
					return;
				}

				// remember path
				UserParameter.instance().hrfImport_HRFPath = files[i].getParentFile().getAbsolutePath();
				frame.setInformation(getLangStr("StartParse"));
				homodel = HRFFileParser.parse(files[i]);
				if ( homodel != null && homodel.getBasics().getTeamId() != HOVerwaltung.instance().getModel().getBasics().getTeamId()){
					HOLogger.instance().error(getClass(), "hrf file from other team can not be imported: " + homodel.getBasics().getTeamName());
					homodel = null;
				}

				if (homodel == null) {
					frame.setInformation(getLangStr("Importfehler") + " : " + files[i].getName(), InfoPanel.FEHLERFARBE);
					Helper.showMessage(frame, getLangStr("Importfehler"), getLangStr("Fehler"), JOptionPane.ERROR_MESSAGE);
				} else {
					frame.setInformation(getLangStr("HRFSave"));

					// file already imported?
					var HRFts = homodel.getBasics().getDatum().toDbTimestamp();
					var storedHrf = DBManager.instance().loadHRFDownloadedAt(HRFts);

					if (choice == null || !choice.applyToAll) {
						choice = confirmHRFSaving(frame, HRFts, storedHrf, files.length > 1);
						if (choice.cancel) {
							break;
						}
					}

					if (choice.importHRF) {
						if (storedHrf != null) {
							DBManager.instance().deleteHRF(storedHrf.getHrfId());
						}
						homodel.saveHRF();
						var training = homodel.getTraining();
						if ( training != null) {
							DBManager.instance().saveTraining(training, HODateTime.now());
						}
						frame.setInformation(getLangStr("HRFErfolg"));
					} else {
						// Cancel
						frame.setInformation(getLangStr("HRFAbbruch"), InfoPanel.FEHLERFARBE);
					}
				}
			}

			HOVerwaltung.instance().loadLatestHoModel();
			HOModel hom = HOVerwaltung.instance().getModel();

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

	/**
	 * User confirmation for saving the hrf file
	 * @param parent Parent component
	 * @param HRF_date Timestamp of the hrf file
	 * @param oldHRF Previously stored hrf of the same date, null if it not in database yet.
	 * @param multipleFilesSelected User selected more than one file to import
	 * @return User choice
	 * 		Import Yes/No
	 * 		Apply to all Yes/No
	 * 		Cancel
	 */
	private UserChoice confirmHRFSaving(Component parent, Timestamp HRF_date, HRF oldHRF, boolean multipleFilesSelected) {
		UserChoice choice = new UserChoice();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		String text = getLangStr("HRFfrom") + " " + dateFormat.format(HRF_date);

		if (oldHRF != null) {
			text += "\n(" + getLangStr("HRFinDB") + " " + oldHRF.getName() + ")";
			text += "\n" + getLangStr("ErneutImportieren");
		}

		JCheckBox applyToAllCheckBox = null;
		if ( multipleFilesSelected) {
			 applyToAllCheckBox = new JCheckBox(getLangStr("hrfImport.applyToAll"));
		}
		Object[] o = {text, applyToAllCheckBox};
		int value = JOptionPane.showConfirmDialog(parent, o, getLangStr("confirmation.title"), JOptionPane.YES_NO_CANCEL_OPTION);

		if (value == JOptionPane.CANCEL_OPTION) {
			choice.cancel = true;
		} else {
			choice.applyToAll = !multipleFilesSelected || applyToAllCheckBox.isSelected();
			if (value == JOptionPane.YES_OPTION) {
				choice.importHRF = true;
			}
		}
		return choice;
	}

	private static class UserChoice {
		boolean importHRF;
		boolean applyToAll;
		boolean cancel;
	}
}