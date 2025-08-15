package tool.export;

import core.constants.player.*;
import core.file.ExampleFileFilter;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.util.HOLogger;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

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
		fileChooser.setDialogTitle(TranslationFacility.tr("CSVExporter"));

		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("csv");
		filter.setDescription(TranslationFacility.tr("filetypedescription.csv"));
		fileChooser.setFileFilter(filter);
		fileChooser.setSelectedFile(file);

		int returnVal = fileChooser.showSaveDialog(HOMainFrame.instance());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			if (file.exists() && JOptionPane.showConfirmDialog(
					HOMainFrame.instance(),
					TranslationFacility.tr("overwrite"), TranslationFacility.tr("CSVExporter"),
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
							"\"" + TranslationFacility.tr("ls.player.name") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.id") + "\","
							+ "\"" + TranslationFacility.tr("Aufgestellt") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.age") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.age") + " " + TranslationFacility.tr("ls.player.age.days") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.tsi") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.wage") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.warningstatus") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.injurystatus") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.short_motherclub") + "\","

							+ "\"" + TranslationFacility.tr("ls.player.agreeability") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.aggressiveness") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.honesty") + "\","

							+ "\"" + TranslationFacility.tr("ls.player.speciality") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.short_experience") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.short_leadership") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.form") + "\","
									// ls.player.skill_short
							+ "\"" + TranslationFacility.tr("ls.player.skill_short.stamina") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.short_loyalty") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.skill_short.keeper") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.skill_short.defending") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.skill_short.winger") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.skill_short.playmaking") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.skill_short.passing") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.skill_short.scoring") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.skill_short.setpieces") + "\","
							// ls.player.position_short
							+ "\"" + TranslationFacility.tr("ls.player.position_short.keeper") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.centraldefender") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.centraldefenderoffensive") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.centraldefendertowardswing") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.wingback") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.wingbackoffensive") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.wingbackdefensive") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.wingbacktowardsmiddle") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.innermidfielder") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.innermidfielderoffensive") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.innermidfielderdefensive") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.innermidfieldertowardswing") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.winger") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.wingeroffensive") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.wingerdefensive") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.wingertowardsmiddle") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.forward") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.forwarddefensive") + "\","
							+ "\"" + TranslationFacility.tr("ls.player.position_short.forwardtowardswing") + "\","
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
						String.valueOf(curPlayer.getWage().toLocale()),
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
						df3.format(curPlayer.getSetPiecesSkill() + curPlayer.getSub4Skill(PlayerSkill.SETPIECES)),
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
