package tool.export;

import core.constants.player.PlayerSkill;
import core.file.ExampleFileFilter;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;

import java.io.*;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
	// Force using dot as decimal point despite of locale
	private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
	// Fix output decimal format to avoid roundings like 6.5509996
	private static final DecimalFormat df2 = new DecimalFormat("0.0#", dfs); // 1…2 digits
	private static final DecimalFormat df3 = new DecimalFormat("0.0##", dfs); // 1…3 digits

	public CsvPlayerExport() {
		df2.setRoundingMode(RoundingMode.HALF_UP);
		df3.setRoundingMode(RoundingMode.HALF_UP); // 8.56789012 -> 8.568
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

			HOMainFrame.instance().setWaitInformation(0);
			doExport (file);
			HOMainFrame.instance().resetInformation();
		}
	}

	private void doExport (File file) {
		HOLogger.instance().debug(getClass(),
				"Exporting all players as CSV to " + file.getName() + "...");
		List<Player> list = HOVerwaltung.instance().getModel().getCurrentPlayers();
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

			writer.write(
							"\"" + HOVerwaltung.instance().getLanguageString("ls.player.name") + "\","
							+ "\"" + HOVerwaltung.instance().getLanguageString("ls.player.id") + "\","
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
						"" + curPlayer.getFullName(),
						"" + curPlayer.getPlayerID(),
						"" + curPlayer.getTrikotnummer(),
						"" + curPlayer.getAlter(),
						"" + curPlayer.getAgeDays(),
						"" + curPlayer.getTSI(),
						"" + (int)(curPlayer.getGehalt() / HOVerwaltung.instance().getModel().getXtraDaten().getCurrencyRate()),
						"" + curPlayer.getGelbeKarten(),
						// empty field for a healthy player (injury == -1), +0 for bruised, +N for injured and +∞ for unrecoverable
						(curPlayer.isInjured() < 0) ? "" : "+" + (curPlayer.isInjured()>9 ? "∞" : curPlayer.isInjured()),

						curPlayer.isHomeGrown() ? "♥" : "",
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
						df3.format(curPlayer.getGKskill() + curPlayer.getSub4SkillAccurate(PlayerSkill.KEEPER)),
						df3.format(curPlayer.getDEFskill() + curPlayer.getSub4SkillAccurate(PlayerSkill.DEFENDING)),
						df3.format(curPlayer.getWIskill() + curPlayer.getSub4SkillAccurate(PlayerSkill.WINGER)),
						df3.format(curPlayer.getPMskill() + curPlayer.getSub4SkillAccurate(PlayerSkill.PLAYMAKING)),
						df3.format(curPlayer.getPSskill() + curPlayer.getSub4SkillAccurate(PlayerSkill.PASSING)),
						df3.format(curPlayer.getSCskill() + curPlayer.getSub4SkillAccurate(PlayerSkill.SCORING)),
						df3.format(curPlayer.getSPskill() + curPlayer.getSub4SkillAccurate(PlayerSkill.SET_PIECES)),
						// ls.player.position_short
						df2.format(curPlayer.calcPosValue(IMatchRoleID.KEEPER, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER_OFF, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER_TOWING, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.BACK, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.BACK_OFF, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.BACK_DEF, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.BACK_TOMID, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.MIDFIELDER, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_OFF, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_DEF, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_TOWING, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.WINGER, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.WINGER_OFF, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.WINGER_DEF, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.WINGER_TOMID, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.FORWARD, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.FORWARD_DEF, true)),
						df2.format(curPlayer.calcPosValue(IMatchRoleID.FORWARD_TOWING, true))
				};
				for (int col=0; col < outCols.length; col++) {
					if (col > 0)
						writer.write (",");
					switch(col) {
						case  0: // name
						case  1: // id
						case  8: // injury
						case  9: // homegrown
						case 10: // agreeability
						case 11: // aggressiveness
						case 12: // honesty
						case 13: // speciality
							writer.write("\"" + outCols[col]+ "\"");
							break;
						default:
							writer.write(outCols[col]);
							break;
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
