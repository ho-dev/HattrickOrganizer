package tool.export;

import core.constants.player.PlayerSkill;
import core.file.ExampleFileFilter;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.net.login.LoginWaitDialog;
import core.util.HOLogger;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JWindow;

import core.constants.player.PlayerAggressiveness;
import core.constants.player.PlayerAgreeability;
import core.constants.player.PlayerHonesty;
import core.constants.player.PlayerSpeciality;

/**
 * CsvPlayerExport
 *
 * Export all players as CSV file
 *
 * @author flattermann <HO@flattermann.net>
 */
public class CsvPlayerExport {
	private static String NAME = "CSV PlayerExport";
	private static final String defaultFilename = "playerexport.csv";

	public CsvPlayerExport() {
	}

	public void showSaveDialog() {
		JWindow waitDialog = null;
		// File
		File file = new File(defaultFilename);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle(HOVerwaltung.instance().getLanguageString("CSVExporter"));

		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("csv");
		filter.setDescription(HOVerwaltung.instance().getLanguageString("filetypedescription.csv"));
		fileChooser.setFileFilter(filter);
		fileChooser.setSelectedFile(file);

		int returnVal = fileChooser.showSaveDialog(HOMainFrame.instance());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			if (file.exists() && JOptionPane.showConfirmDialog(
					HOMainFrame.instance(),
					HOVerwaltung.instance().getLanguageString("overwrite"), HOVerwaltung.instance().getLanguageString("CSVExporter"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				// Cancel
				return;
            }

			waitDialog = new LoginWaitDialog(HOMainFrame.instance());
			waitDialog.setVisible(true);
			doExport (file);
			waitDialog.setVisible(false);
		}
	}

	private void doExport (File file) {
		HOLogger.instance().debug(getClass(),
				"Exporting all players as CSV to " + file.getName() + "...");
		List<Player> list = HOVerwaltung.instance().getModel().getAllSpieler();
		try {
			FileWriter writer = new FileWriter(file);
			//This is a try OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream(file), "utf-8");

			writer.write(
							"\"" + HOVerwaltung.instance().getLanguageString("ls.player.id") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.name") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("Aufgestellt") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.age") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.age") + " " + HOVerwaltung.instance().getLanguageString("ls.player.age.days") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.tsi") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.wage") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.warningstatus") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.injurystatus") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.short_motherclub") + "\","

							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.agreeability") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.aggressiveness") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.honesty") + "\","

							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.speciality") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.short_experience") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.short_leadership") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.form") + "\","
									// ls.player.skill_short
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.skill_short.stamina") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.short_loyalty") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.skill_short.keeper") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.skill_short.defending") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.skill_short.winger") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.skill_short.playmaking") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.skill_short.passing") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.skill_short.scoring") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.skill_short.setpieces") + "\","
							// ls.player.position_short
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.keeper") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefender") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefenderoffensive") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefendertowardswing") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingback") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbackoffensive") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbackdefensive") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbacktowardsmiddle") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielder") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielderoffensive") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielderdefensive") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfieldertowardswing") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.winger") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingeroffensive") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingerdefensive") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingertowardsmiddle") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.forward") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.forwarddefensive") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.position_short.forwardtowardswing") + "\","
							+ "\n");
			Iterator<Player> iter = list.iterator();
			while (iter.hasNext()) {
				Player curPlayer = (Player)iter.next();

				String [] outCols = {
						"" + curPlayer.getSpielerID(),
						"" + curPlayer.getName(),
						"" + curPlayer.getTrikotnummer(),
						"" + curPlayer.getAlter(),
						"" + curPlayer.getAgeDays(),
						"" + curPlayer.getTSI(),
						"" + (int)(curPlayer.getGehalt() / HOVerwaltung.instance().getModel().getXtraDaten().getCurrencyRate()),
						"" + curPlayer.getGelbeKarten(),
						"" + curPlayer.getVerletzt(),

						"" + (curPlayer.isHomeGrown()),
						"" + PlayerAgreeability.toString(curPlayer.getCharakter()),
						"" + PlayerAggressiveness.toString(curPlayer.getAgressivitaet()),
						"" + PlayerHonesty.toString(curPlayer.getAnsehen()),
						//
						"" + PlayerSpeciality.toString(curPlayer.getPlayerSpecialty()),
						"" + curPlayer.getErfahrung(),
						"" + curPlayer.getFuehrung(),
						"" + curPlayer.getForm(),
						// ls.player.skill_short
						"" + curPlayer.getKondition(),
						"" + (curPlayer.getLoyalty()),
						"" + (curPlayer.getTorwart() + curPlayer.getSubskill4PosAccurate(PlayerSkill.KEEPER)),
						"" + (curPlayer.getVerteidigung() + curPlayer.getSubskill4PosAccurate(PlayerSkill.DEFENDING)),
						"" + (curPlayer.getFluegelspiel() + curPlayer.getSubskill4PosAccurate(PlayerSkill.WINGER)),
						"" + (curPlayer.getSpielaufbau() + curPlayer.getSubskill4PosAccurate(PlayerSkill.PLAYMAKING)),
						"" + (curPlayer.getPasspiel() + curPlayer.getSubskill4PosAccurate(PlayerSkill.PASSING)),
						"" + (curPlayer.getTorschuss() + curPlayer.getSubskill4PosAccurate(PlayerSkill.SCORING)),
						"" + (curPlayer.getStandards() + curPlayer.getSubskill4PosAccurate(PlayerSkill.SET_PIECES)),
						// ls.player.position_short
						"" + curPlayer.calcPosValue(IMatchRoleID.KEEPER, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER_OFF, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER_TOWING, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.BACK, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.BACK_OFF, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.BACK_DEF, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.BACK_TOMID, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.MIDFIELDER, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_OFF, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_DEF, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_TOWING, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.WINGER, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.WINGER_OFF, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.WINGER_DEF, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.WINGER_TOMID, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.FORWARD, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.FORWARD_DEF, true),
						"" + curPlayer.calcPosValue(IMatchRoleID.FORWARD_TOWING, true)
				};
				for (int col=0; col < outCols.length; col++) {
					if (col > 0)
						writer.write (",");
					if(col==0 || col==1 || col==10 || col==11 || col==12 || col==13) {
						writer.write("\"");
						writer.write(outCols[col]);
						writer.write("\"");
					}else{
						writer.write(outCols[col]);
					}
				}
				writer.write ("\n");
			}
			writer.close();
			HOLogger.instance().info(getClass(), "CSV Export complete.");
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "CSV Export error!");
			e.printStackTrace();
		}
	}
}
