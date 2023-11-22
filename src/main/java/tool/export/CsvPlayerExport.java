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
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import core.constants.player.PlayerAggressiveness;
import core.constants.player.PlayerAgreeability;
import core.constants.player.PlayerHonesty;
import core.constants.player.PlayerSpeciality;

/**
 * CsvPlayerExport
 * Export all players as CSV file
 *
 * @author flattermann <HO@flattermann.net>
 */
public class CsvPlayerExport {
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

			HOMainFrame.instance().resetInformation();
			doExport (file);
			HOMainFrame.instance().setInformationCompleted();
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

			var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();

			for (Player curPlayer : list) {
				String[] outCols = {
						curPlayer.getFullName(),
						String.valueOf(curPlayer.getPlayerId()),
						String.valueOf(curPlayer.getShirtNumber()),
						String.valueOf(curPlayer.getAge()),
						String.valueOf(curPlayer.getAgeDays()),
						String.valueOf(curPlayer.getTsi()),
						String.valueOf((int) (curPlayer.getWage() / HOVerwaltung.instance().getModel().getXtraDaten().getCurrencyRate())),
						String.valueOf(curPlayer.getTotalCards()),
						// empty field for a healthy player (injury == -1), +0 for bruised, +N for injured and +∞ for unrecoverable
						(curPlayer.getInjuryWeeks() < 0) ? "" : "+" + (curPlayer.getInjuryWeeks() > 9 ? "∞" : curPlayer.getInjuryWeeks()),

						curPlayer.isHomeGrown() ? "♥" : "",
						PlayerAgreeability.toString(curPlayer.getGentleness()),
						PlayerAggressiveness.toString(curPlayer.getAggressivity()),
						PlayerHonesty.toString(curPlayer.getHonesty()),
						//
						PlayerSpeciality.toString(curPlayer.getSpecialty()),
						String.valueOf(curPlayer.getExperience()),
						String.valueOf(curPlayer.getLeadership()),
						String.valueOf(curPlayer.getForm()),
						// ls.player.skill_short
						"" + curPlayer.getStamina(),
						"" + (curPlayer.getLoyalty()),
						df3.format(curPlayer.getGoalkeeperSkill() + curPlayer.getSub4Skill(PlayerSkill.KEEPER)),
						df3.format(curPlayer.getDefendingSkill() + curPlayer.getSub4Skill(PlayerSkill.DEFENDING)),
						df3.format(curPlayer.getWingerSkill() + curPlayer.getSub4Skill(PlayerSkill.WINGER)),
						df3.format(curPlayer.getPlaymakingSkill() + curPlayer.getSub4Skill(PlayerSkill.PLAYMAKING)),
						df3.format(curPlayer.getPassingSkill() + curPlayer.getSub4Skill(PlayerSkill.PASSING)),
						df3.format(curPlayer.getScoringSkill() + curPlayer.getSub4Skill(PlayerSkill.SCORING)),
						df3.format(curPlayer.getSetPiecesSkill() + curPlayer.getSub4Skill(PlayerSkill.SET_PIECES)),
						// ls.player.position_short
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.KEEPER)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.CENTRAL_DEFENDER)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.CENTRAL_DEFENDER_OFF)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.CENTRAL_DEFENDER_TOWING)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.BACK)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.BACK_OFF)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.BACK_DEF)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.BACK_TOMID)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.MIDFIELDER)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.MIDFIELDER_OFF)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.MIDFIELDER_DEF)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.MIDFIELDER_TOWING)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.WINGER)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.WINGER_OFF)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.WINGER_DEF)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.WINGER_TOMID)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.FORWARD)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.FORWARD_DEF)),
						df2.format(ratingPredictionModel.getPlayerMatchAverageRating(curPlayer, IMatchRoleID.FORWARD_TOWING))
				};
				for (int col = 0; col < outCols.length; col++) {
					if (col > 0)
						writer.write(",");
					switch (col) { // name
						// id
						// injury
						// homegrown
						// agreeability
						// aggressiveness
						// honesty
						case 0, 1, 8, 9, 10, 11, 12, 13 -> // speciality
								writer.write("\"" + outCols[col] + "\"");
						default -> writer.write(outCols[col]);
					}
				}
				writer.write("\n");
			}
			writer.close();
			HOLogger.instance().info(getClass(), "CSV Export complete.");
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "CSV Export error: " + e);
		}
	}
}
