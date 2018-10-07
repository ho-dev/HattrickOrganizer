// %1374340947:de.hattrickorganizer.gui.matches%
package module.matches;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerAggressiveness;
import core.constants.player.PlayerAgreeability;
import core.constants.player.PlayerHonesty;
import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.db.DBManager;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.entry.RatingTableEntry;
import core.gui.comp.entry.SpielerLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupPlayer;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.model.player.SpielerPosition;
import core.util.HOLogger;
import core.util.Helper;
import module.playerOverview.SpielerDetailPanel;
import module.playerOverview.SpielerStatusLabelEntry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Zeigt Details zu einem Spieler zu einer Zeit an
 */
final class SpielerDetailDialog extends JDialog {

	private static final long serialVersionUID = 7104209757847006926L;
	private final Dimension COMPONENTENSIZE3 = new Dimension(Helper.calcCellWidth(100),
			Helper.calcCellWidth(18));
	private final Dimension COMPONENTENSIZE4 = new Dimension(Helper.calcCellWidth(50),
			Helper.calcCellWidth(18));
	private final ColorLabelEntry m_jpAggressivitaet = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpAlter = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
			ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpAnsehen = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpAufgestellt = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpBestPos = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpCharakter = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpErfahrung = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpErfahrung2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpFluegelspiel = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpFluegelspiel2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpForm = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
			ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpForm2 = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
			ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpFuehrung = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpFuehrung2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpHattriks = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpKondition = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpKondition2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpName = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
			ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpNationalitaet = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpPasspiel = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpPasspiel2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpSpezialitaet = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpSpielaufbau = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpSpielaufbau2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpStandards = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpStandards2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpToreFreund = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpToreGesamt = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpToreLiga = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpTorePokal = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
	private final ColorLabelEntry m_jpTorschuss = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpTorschuss2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpTorwart = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpTorwart2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
			SwingConstants.CENTER);
	private final ColorLabelEntry m_jpVerteidigung = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
	private final ColorLabelEntry m_jpVerteidigung2 = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
			SwingConstants.CENTER);
	private final DoppelLabelEntry m_jpGehalt = new DoppelLabelEntry(ColorLabelEntry.BG_STANDARD);
	private final DoppelLabelEntry m_jpGruppeSmilie = new DoppelLabelEntry(
			ColorLabelEntry.BG_STANDARD);
	private final DoppelLabelEntry m_jpMartwert = new DoppelLabelEntry(ColorLabelEntry.BG_STANDARD);
	private final DoppelLabelEntry m_jpWertAussenVert = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertAussenVertDef = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertAussenVertIn = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertAussenVertOff = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertFluegel = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertFluegelDef = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertFluegelIn = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertFluegelOff = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertInnenVert = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertInnenVertAus = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertInnenVertOff = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertMittelfeld = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertMittelfeldAus = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertMittelfeldDef = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertMittelfeldOff = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertSturmAus = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertSturm = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertSturmDef = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
	private final DoppelLabelEntry m_jpWertTor = new DoppelLabelEntry(
			ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
	private final RatingTableEntry m_jpAktuellRating = new RatingTableEntry();
	private final RatingTableEntry m_jpRating = new RatingTableEntry();
	private final SpielerStatusLabelEntry m_jpStatus = new SpielerStatusLabelEntry();

	private final DoppelLabelEntry[] playerPositionValues = new DoppelLabelEntry[] { m_jpWertTor,
			m_jpWertInnenVert, m_jpWertInnenVertAus, m_jpWertInnenVertOff, m_jpWertAussenVert,
			m_jpWertAussenVertIn, m_jpWertAussenVertOff, m_jpWertAussenVertDef, m_jpWertMittelfeld,
			m_jpWertMittelfeldAus, m_jpWertMittelfeldOff, m_jpWertMittelfeldDef, m_jpWertFluegel,
			m_jpWertFluegelIn, m_jpWertFluegelOff, m_jpWertFluegelDef, m_jpWertSturm,
			m_jpWertSturmAus, m_jpWertSturmDef };

	private final byte[] playerPosition = new byte[] { ISpielerPosition.KEEPER,
			ISpielerPosition.CENTRAL_DEFENDER, ISpielerPosition.CENTRAL_DEFENDER_TOWING,
			ISpielerPosition.CENTRAL_DEFENDER_OFF, ISpielerPosition.BACK,
			ISpielerPosition.BACK_TOMID, ISpielerPosition.BACK_OFF, ISpielerPosition.BACK_DEF,
			ISpielerPosition.MIDFIELDER, ISpielerPosition.MIDFIELDER_TOWING,
			ISpielerPosition.MIDFIELDER_OFF, ISpielerPosition.MIDFIELDER_DEF,
			ISpielerPosition.WINGER, ISpielerPosition.WINGER_TOMID, ISpielerPosition.WINGER_OFF,
			ISpielerPosition.WINGER_DEF, ISpielerPosition.FORWARD, ISpielerPosition.FORWARD_TOWING,
			ISpielerPosition.FORWARD_DEF

	};

	public SpielerDetailDialog(JFrame owner, MatchLineupPlayer matchplayer, MatchLineup matchlineup) {
		super(owner);
		HOLogger.instance().log(getClass(), "SpielerDetailDialog");
		Spieler player = DBManager.instance().getSpielerAtDate(matchplayer.getSpielerId(),
				matchlineup.getSpielDatum());

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// Nicht gefunden
		if (player == null) {
			Helper.showMessage(owner,
					HOVerwaltung.instance().getLanguageString("Fehler_Spielerdetails"),
					HOVerwaltung.instance().getLanguageString("Fehler"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		HOLogger.instance().log(getClass(), "Show Player: " + player.getName());

		setTitle(player.getName() + " (" + player.getSpielerID() + ")");
		addWindowListener(new WindowAdapter() {
			@Override
			public final void windowClosing(WindowEvent e) {
				UserParameter.instance().spielerDetails_PositionX = getLocation().x;
				UserParameter.instance().spielerDetails_PositionY = getLocation().y;
			}
		});

		initComponents(player, matchplayer);

		m_jpRating.setRating((float) matchplayer.getRating() * 2, true);
		m_jpAktuellRating.setRating(DBManager.instance().getLetzteBewertung4Spieler(
				player.getSpielerID()));
		setLabels(player);

		pack();
		setSize(getSize().width + Helper.calcCellWidth(30), getSize().height + 10);
		setLocation(core.model.UserParameter.instance().spielerDetails_PositionX,
				core.model.UserParameter.instance().spielerDetails_PositionY);
		setVisible(true);
	}

	private void setLabels(Spieler m_clSpieler) {
		Spieler m_clVergleichsSpieler = HOVerwaltung.instance().getModel()
				.getSpieler(m_clSpieler.getSpielerID());

		m_jpName.setText(m_clSpieler.getName());
		m_jpName.setFGColor(SpielerLabelEntry.getForegroundForSpieler(m_clSpieler));
		m_jpAlter.setText(m_clSpieler.getAlter() + "");
		m_jpNationalitaet.setIcon(ImageUtilities.getFlagIcon(m_clSpieler.getNationalitaet()));

		if (HOVerwaltung.instance().getModel().getAufstellung()
				.isSpielerAufgestellt(m_clSpieler.getSpielerID())
				&& (HOVerwaltung.instance().getModel().getAufstellung()
						.getPositionBySpielerId(m_clSpieler.getSpielerID()) != null)) {
			m_jpAufgestellt.setIcon(ImageUtilities.getImage4Position(
					HOVerwaltung.instance().getModel().getAufstellung()
							.getPositionBySpielerId(m_clSpieler.getSpielerID()),
					m_clSpieler.getTrikotnummer()));
			m_jpAufgestellt.setText(SpielerPosition.getNameForPosition(HOVerwaltung.instance()
					.getModel().getAufstellung().getPositionBySpielerId(m_clSpieler.getSpielerID())
					.getPosition()));
		} else {
			m_jpAufgestellt.setIcon(ImageUtilities.getImage4Position(null,
					m_clSpieler.getTrikotnummer()));
			m_jpAufgestellt.setText("");
		}

		m_jpGruppeSmilie.getLinks().setAlignment(SwingConstants.CENTER);
		m_jpGruppeSmilie.getRechts().setAlignment(SwingConstants.CENTER);
		m_jpGruppeSmilie.getLinks().setIcon(ThemeManager.getIcon(m_clSpieler.getTeamInfoSmilie()));
		m_jpGruppeSmilie.getRechts()
				.setIcon(ThemeManager.getIcon(m_clSpieler.getManuellerSmilie()));

		m_jpStatus.setSpieler(m_clSpieler);

		if (m_clVergleichsSpieler == null) {
			String bonus = "";
			int gehalt = (int) (m_clSpieler.getGehalt() / core.model.UserParameter.instance().faktorGeld);
			String gehalttext = NumberFormat.getCurrencyInstance().format(gehalt);

			if (m_clSpieler.getBonus() > 0) {
				bonus = " (" + m_clSpieler.getBonus() + "% "
						+ HOVerwaltung.instance().getLanguageString("Bonus") + ")";
			}

			m_jpGehalt.getLinks().setText(gehalttext + "" + bonus);
			m_jpGehalt.getRechts().clear();
			m_jpMartwert.getLinks().setText(m_clSpieler.getTSI() + "");
			m_jpMartwert.getRechts().clear();
			m_jpForm.setText(PlayerAbility.getNameForSkill(m_clSpieler.getForm()) + "");
			m_jpForm2.clear();
			m_jpKondition.setText(PlayerAbility.getNameForSkill(m_clSpieler.getKondition()) + "");
			m_jpKondition2.clear();
			m_jpTorwart.setText(PlayerAbility.getNameForSkill(m_clSpieler.getTorwart()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.KEEPER))
					+ "");
			m_jpTorwart2.clear();
			m_jpVerteidigung.setText(PlayerAbility.getNameForSkill(m_clSpieler.getVerteidigung()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.DEFENDING))
					+ "");
			m_jpVerteidigung2.clear();
			m_jpSpielaufbau.setText(PlayerAbility.getNameForSkill(m_clSpieler.getSpielaufbau()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.PLAYMAKING))
					+ "");
			m_jpSpielaufbau2.clear();
			m_jpPasspiel.setText(PlayerAbility.getNameForSkill(m_clSpieler.getPasspiel()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.PASSING))
					+ "");
			m_jpPasspiel2.clear();
			m_jpFluegelspiel.setText(PlayerAbility.getNameForSkill(m_clSpieler.getFluegelspiel()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.WINGER))
					+ "");
			m_jpFluegelspiel2.clear();
			m_jpStandards.setText(PlayerAbility.getNameForSkill(m_clSpieler.getStandards()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.SET_PIECES))
					+ "");
			m_jpStandards2.clear();
			m_jpTorschuss.setText(PlayerAbility.getNameForSkill(m_clSpieler.getTorschuss()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.SCORING))
					+ "");
			m_jpTorschuss2.clear();
			m_jpErfahrung.setText(PlayerAbility.getNameForSkill(m_clSpieler.getErfahrung()) + "");
			m_jpErfahrung2.clear();
			m_jpFuehrung.setText(PlayerAbility.getNameForSkill(m_clSpieler.getFuehrung()) + "");
			m_jpFuehrung2.clear();
			m_jpBestPos.setText(SpielerPosition.getNameForPosition(m_clSpieler.getIdealPosition())
					+ " (" + m_clSpieler.calcPosValue(m_clSpieler.getIdealPosition(), true) + ")");
			for (int i = 0; i < playerPositionValues.length; i++) {
				showNormal(playerPositionValues[i], playerPosition[i], m_clSpieler);
			}
		} else {
			String bonus = "";
			int gehalt = (int) (m_clSpieler.getGehalt() / core.model.UserParameter.instance().faktorGeld);
			int gehalt2 = (int) (m_clVergleichsSpieler.getGehalt() / core.model.UserParameter
					.instance().faktorGeld);
			String gehalttext = NumberFormat.getCurrencyInstance().format(gehalt);

			if (m_clSpieler.getBonus() > 0) {
				bonus = " (" + m_clSpieler.getBonus() + "% "
						+ HOVerwaltung.instance().getLanguageString("Bonus") + ")";
			}

			m_jpGehalt.getLinks().setText(gehalttext + "" + bonus);
			m_jpGehalt.getRechts().setSpecialNumber((gehalt2 - gehalt), true);
			m_jpMartwert.getLinks().setText(m_clSpieler.getTSI() + "");
			m_jpMartwert.getRechts().setSpecialNumber(
					(m_clVergleichsSpieler.getTSI() - m_clSpieler.getTSI()), false);
			m_jpForm.setText(PlayerAbility.getNameForSkill(m_clSpieler.getForm()) + "");
			m_jpForm2.setGraphicalChangeValue(
					m_clVergleichsSpieler.getForm() - m_clSpieler.getForm(), !m_clSpieler.isOld(),
					true);
			m_jpKondition.setText(PlayerAbility.getNameForSkill(m_clSpieler.getKondition()) + "");
			m_jpKondition2.setGraphicalChangeValue(m_clVergleichsSpieler.getKondition()
					- m_clSpieler.getKondition(), !m_clVergleichsSpieler.isOld(), true);
			m_jpTorwart.setText(PlayerAbility.getNameForSkill(m_clSpieler.getTorwart()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.KEEPER))
					+ "");
			m_jpTorwart2.setGraphicalChangeValue(
					m_clVergleichsSpieler.getTorwart() - m_clSpieler.getTorwart(),
					m_clVergleichsSpieler.getSubskill4Pos(PlayerSkill.KEEPER)
							- m_clSpieler.getSubskill4Pos(PlayerSkill.KEEPER),
					!m_clVergleichsSpieler.isOld(), true);
			m_jpVerteidigung.setText(PlayerAbility.getNameForSkill(m_clSpieler.getVerteidigung()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.DEFENDING))
					+ "");
			m_jpVerteidigung2.setGraphicalChangeValue(
					m_clVergleichsSpieler.getVerteidigung() - m_clSpieler.getVerteidigung(),
					m_clVergleichsSpieler.getSubskill4Pos(PlayerSkill.DEFENDING)
							- m_clSpieler.getSubskill4Pos(PlayerSkill.DEFENDING),
					!m_clVergleichsSpieler.isOld(), true);
			m_jpSpielaufbau.setText(PlayerAbility.getNameForSkill(m_clSpieler.getSpielaufbau()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.PLAYMAKING))
					+ "");
			m_jpSpielaufbau2.setGraphicalChangeValue(
					m_clVergleichsSpieler.getSpielaufbau() - m_clSpieler.getSpielaufbau(),
					m_clVergleichsSpieler.getSubskill4Pos(PlayerSkill.PLAYMAKING)
							- m_clSpieler.getSubskill4Pos(PlayerSkill.PLAYMAKING),
					!m_clVergleichsSpieler.isOld(), true);
			m_jpPasspiel.setText(PlayerAbility.getNameForSkill(m_clSpieler.getPasspiel()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.PASSING))
					+ "");
			m_jpPasspiel2.setGraphicalChangeValue(
					m_clVergleichsSpieler.getPasspiel() - m_clSpieler.getPasspiel(),
					m_clVergleichsSpieler.getSubskill4Pos(PlayerSkill.PASSING)
							- m_clSpieler.getSubskill4Pos(PlayerSkill.PASSING),
					!m_clVergleichsSpieler.isOld(), true);
			m_jpFluegelspiel.setText(PlayerAbility.getNameForSkill(m_clSpieler.getFluegelspiel()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.WINGER))
					+ "");
			m_jpFluegelspiel2.setGraphicalChangeValue(
					m_clVergleichsSpieler.getFluegelspiel() - m_clSpieler.getFluegelspiel(),
					m_clVergleichsSpieler.getSubskill4Pos(PlayerSkill.WINGER)
							- m_clSpieler.getSubskill4Pos(PlayerSkill.WINGER),
					!m_clVergleichsSpieler.isOld(), true);
			m_jpStandards.setText(PlayerAbility.getNameForSkill(m_clSpieler.getStandards()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.SET_PIECES))
					+ "");
			m_jpStandards2.setGraphicalChangeValue(
					m_clVergleichsSpieler.getStandards() - m_clSpieler.getStandards(),
					m_clVergleichsSpieler.getSubskill4Pos(PlayerSkill.SET_PIECES)
							- m_clSpieler.getSubskill4Pos(PlayerSkill.SET_PIECES),
					!m_clVergleichsSpieler.isOld(), true);
			m_jpTorschuss.setText(PlayerAbility.getNameForSkill(m_clSpieler.getTorschuss()
					+ m_clSpieler.getSubskill4Pos(PlayerSkill.SCORING))
					+ "");
			m_jpTorschuss2.setGraphicalChangeValue(
					m_clVergleichsSpieler.getTorschuss() - m_clSpieler.getTorschuss(),
					m_clVergleichsSpieler.getSubskill4Pos(PlayerSkill.SCORING)
							- m_clSpieler.getSubskill4Pos(PlayerSkill.SCORING),
					!m_clVergleichsSpieler.isOld(), true);
			m_jpErfahrung.setText(PlayerAbility.getNameForSkill(m_clSpieler.getErfahrung()) + "");
			m_jpErfahrung2.setGraphicalChangeValue(m_clVergleichsSpieler.getErfahrung()
					- m_clSpieler.getErfahrung(), !m_clSpieler.isOld(), true);
			m_jpFuehrung.setText(PlayerAbility.getNameForSkill(m_clSpieler.getFuehrung()) + "");
			m_jpFuehrung2.setGraphicalChangeValue(m_clVergleichsSpieler.getFuehrung()
					- m_clSpieler.getFuehrung(), !m_clVergleichsSpieler.isOld(), true);
			m_jpBestPos.setText(core.model.player.SpielerPosition.getNameForPosition(m_clSpieler
					.getIdealPosition())
					+ " ("
					+ m_clSpieler.calcPosValue(m_clSpieler.getIdealPosition(), true) + ")");

			for (int i = 0; i < playerPositionValues.length; i++) {
				showWithCompare(playerPositionValues[i], playerPosition[i], m_clSpieler,
						m_clVergleichsSpieler);
			}
		}
		m_jpToreFreund.setText(m_clSpieler.getToreFreund() + "");
		m_jpToreLiga.setText(m_clSpieler.getToreLiga() + "");
		m_jpTorePokal.setText(m_clSpieler.getTorePokal() + "");
		m_jpToreGesamt.setText(m_clSpieler.getToreGesamt() + "");
		m_jpHattriks.setText(m_clSpieler.getHattrick() + "");
		m_jpSpezialitaet.setText(PlayerSpeciality.toString(m_clSpieler.getSpezialitaet()));
		m_jpSpezialitaet.setIcon(ThemeManager.getIcon(HOIconName.SPECIAL[m_clSpieler
				.getSpezialitaet()]));
		m_jpAggressivitaet.setText(PlayerAggressiveness.toString(m_clSpieler.getAgressivitaet()));

		// Dreher!
		m_jpAnsehen.setText(PlayerAgreeability.toString(m_clSpieler.getCharakter()));
		m_jpCharakter.setText(PlayerHonesty.toString(m_clSpieler.getAnsehen()));

	}

	private void initComponents(Spieler player, MatchLineupPlayer matchplayer) {
		JComponent component = null;

		getContentPane().setLayout(new BorderLayout());

		JPanel panel = new ImagePanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(1, 2, 1, 1);
		panel.setLayout(layout);

		JLabel label;

		// Leerzeile
		label = new JLabel("  ");
		constraints.gridx = 3;
		constraints.weightx = 0.0;
		constraints.gridy = 0;
		constraints.gridheight = 11;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridheight = 1;

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.name"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		component = m_jpName.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.age"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		component = m_jpAlter.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.nationality"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		component = m_jpNationalitaet.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Aufgestellt"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		component = m_jpAufgestellt.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Aktuell") + " "
				+ HOVerwaltung.instance().getLanguageString("Rating"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		component = m_jpAktuellRating.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("BestePosition"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		component = m_jpBestPos.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Gruppe"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		component = m_jpGruppeSmilie.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;

		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Status"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		component = m_jpStatus.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.wage"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		component = m_jpGehalt.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.tsi"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		component = m_jpMartwert.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Rating"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		component = m_jpRating.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		// /////////////////////////////////////////////////////////////////////////////////////
		// Leerzeile
		label = new JLabel();
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 6;
		constraints.gridwidth = 4;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridwidth = 1;

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.experience"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 7;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 7;
		component = m_jpErfahrung.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 2;
		constraints.weightx = 1.0;
		constraints.gridy = 7;
		component = m_jpErfahrung2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.form"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 7;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 7;
		component = m_jpForm.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 6;
		constraints.weightx = 1.0;
		constraints.gridy = 7;
		component = m_jpForm2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 8;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 8;
		component = m_jpKondition.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 2;
		constraints.weightx = 1.0;
		constraints.gridy = 8;
		component = m_jpKondition2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 8;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 8;
		component = m_jpTorwart.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 6;
		constraints.weightx = 1.0;
		constraints.gridy = 8;
		component = m_jpTorwart2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 9;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 9;
		component = m_jpSpielaufbau.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 2;
		constraints.weightx = 1.0;
		constraints.gridy = 9;
		component = m_jpSpielaufbau2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 9;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 9;
		component = m_jpPasspiel.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 6;
		constraints.weightx = 1.0;
		constraints.gridy = 9;
		component = m_jpPasspiel2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 10;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 10;
		component = m_jpFluegelspiel.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 2;
		constraints.weightx = 1.0;
		constraints.gridy = 10;
		component = m_jpFluegelspiel2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 10;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 10;
		component = m_jpVerteidigung.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 6;
		constraints.weightx = 1.0;
		constraints.gridy = 10;
		component = m_jpVerteidigung2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"));
		constraints.gridx = 0;
		constraints.weightx = 0.0;
		constraints.gridy = 11;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 11;
		component = m_jpTorschuss.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 2;
		constraints.weightx = 1.0;
		constraints.gridy = 11;
		component = m_jpTorschuss2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"));
		constraints.gridx = 4;
		constraints.weightx = 0.0;
		constraints.gridy = 11;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 5;
		constraints.weightx = 1.0;
		constraints.gridy = 11;
		component = m_jpStandards.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 6;
		constraints.weightx = 1.0;
		constraints.gridy = 11;
		component = m_jpStandards2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		// //////////////////////////////////////////////////////////////////////
		// Leerzeile
		label = new JLabel("  ");
		constraints.gridx = 7;
		constraints.weightx = 0.0;
		constraints.gridy = 0;
		constraints.gridheight = 11;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridheight = 1;

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.leadership"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 0;
		component = m_jpFuehrung.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE3);
		layout.setConstraints(component, constraints);
		panel.add(component);
		constraints.gridx = 10;
		constraints.weightx = 1.0;
		constraints.gridy = 0;
		component = m_jpFuehrung2.getComponent(false);
		component.setPreferredSize(COMPONENTENSIZE4);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.speciality"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		component = m_jpSpezialitaet.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.aggressiveness"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		component = m_jpAggressivitaet.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.agreeability"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		component = m_jpAnsehen.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.honesty"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		component = m_jpCharakter.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		// 2 Leerzeile
		label = new JLabel();
		constraints.gridx = 11;
		constraints.weightx = 0.0;
		constraints.gridy = 5;
		constraints.gridwidth = 3;
		constraints.gridheight = 2;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridwidth = 1;
		constraints.gridheight = 1;

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ToreFreund"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 7;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 7;
		constraints.gridwidth = 2;
		component = m_jpToreFreund.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ToreLiga"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 8;
		constraints.gridwidth = 2;
		component = m_jpToreLiga.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("TorePokal"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 9;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 9;
		constraints.gridwidth = 2;
		component = m_jpTorePokal.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ToreGesamt"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 10;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 10;
		constraints.gridwidth = 2;
		component = m_jpToreGesamt.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Hattricks"));
		constraints.gridx = 8;
		constraints.weightx = 0.0;
		constraints.gridy = 11;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridx = 9;
		constraints.weightx = 1.0;
		constraints.gridy = 11;
		constraints.gridwidth = 2;
		component = m_jpHattriks.getComponent(false);
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE);
		layout.setConstraints(component, constraints);
		panel.add(component);

		// //////////////////////////////////////////////////////////////////////
		// Leerzeile
		label = new JLabel("  ");
		constraints.gridx = 11;
		constraints.weightx = 0.0;
		constraints.gridy = 0;
		constraints.gridheight = 18;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);
		constraints.gridheight = 1;

		for (int i = 0; i < playerPositionValues.length; i++) {
			label = new JLabel(SpielerPosition.getKurzNameForPosition(playerPosition[i]));
			label.setToolTipText(SpielerPosition.getNameForPosition(playerPosition[i]));
			initBlueLabel(i, constraints, layout, panel, label);
			initBlueField(i, constraints, layout, panel,
					playerPositionValues[i].getComponent(false));
		}

		// //////////////////////////////////////////////////////////////////////
		final float[] rating = core.db.DBManager.instance().getBewertungen4Player(
				player.getSpielerID());
		final float[] ratingPos = core.db.DBManager.instance().getBewertungen4PlayerUndPosition(
				player.getSpielerID(), matchplayer.getPosition());

		// Rating insgesamt
		GridBagLayout sublayout = new GridBagLayout();
		GridBagConstraints subconstraints = new GridBagConstraints();
		subconstraints.anchor = GridBagConstraints.CENTER;
		subconstraints.fill = GridBagConstraints.HORIZONTAL;
		subconstraints.weightx = 1.0;
		subconstraints.weighty = 0.0;
		subconstraints.insets = new Insets(1, 2, 1, 1);

		JPanel subpanel = new ImagePanel(sublayout);
		subpanel.setBorder(BorderFactory.createTitledBorder(HOVerwaltung.instance()
				.getLanguageString("Rating")));

		subconstraints.gridx = 0;
		subconstraints.gridy = 0;
		subconstraints.weightx = 0.0;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Maximal"));
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 0;
		subconstraints.gridy = 1;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Minimal"));
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 0;
		subconstraints.gridy = 2;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Durchschnitt"));
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 0;
		subconstraints.gridy = 3;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Spiele"));
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 1;
		subconstraints.gridy = 0;
		subconstraints.weightx = 1.0;

		RatingTableEntry ratingentry = new RatingTableEntry(rating[0] * 2, true);

		sublayout.setConstraints(ratingentry.getComponent(false), subconstraints);
		subpanel.add(ratingentry.getComponent(false));

		subconstraints.gridx = 1;
		subconstraints.gridy = 1;
		ratingentry = new RatingTableEntry(rating[1] * 2, true);
		sublayout.setConstraints(ratingentry.getComponent(false), subconstraints);
		subpanel.add(ratingentry.getComponent(false));

		subconstraints.gridx = 1;
		subconstraints.gridy = 2;
		ratingentry = new RatingTableEntry(Math.round(rating[2] * 2), true);
		sublayout.setConstraints(ratingentry.getComponent(false), subconstraints);
		subpanel.add(ratingentry.getComponent(false));

		subconstraints.gridx = 2;
		subconstraints.gridy = 0;
		subconstraints.weightx = 0.0;
		label = new JLabel(rating[0] + "");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 2;
		subconstraints.gridy = 1;
		label = new JLabel(rating[1] + "");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 2;
		subconstraints.gridy = 2;
		label = new JLabel(core.util.Helper.round(rating[2], 2) + "");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 1;
		subconstraints.gridy = 3;
		subconstraints.gridwidth = 2;
		label = new JLabel(((int) rating[3]) + "", SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		constraints.gridx = 0;
		constraints.gridy = 13;
		constraints.weightx = 0.5;
		constraints.gridheight = 4;
		constraints.gridwidth = 5;

		layout.setConstraints(subpanel, constraints);
		panel.add(subpanel);

		// Rating Position
		sublayout = new GridBagLayout();
		subconstraints = new GridBagConstraints();
		subconstraints.anchor = GridBagConstraints.CENTER;
		subconstraints.fill = GridBagConstraints.HORIZONTAL;
		subconstraints.weightx = 1.0;
		subconstraints.weighty = 0.0;
		subconstraints.insets = new Insets(1, 2, 1, 1);
		subpanel = new ImagePanel(sublayout);
		subpanel.setBorder(BorderFactory.createTitledBorder(HOVerwaltung.instance()
				.getLanguageString("Rating")
				+ " "
				+ SpielerPosition.getNameForPosition(core.model.player.SpielerPosition
						.getPosition(matchplayer.getId(), matchplayer.getTaktik()))));

		subconstraints.gridx = 0;
		subconstraints.gridy = 0;
		subconstraints.weightx = 0.0;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Maximal"));
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 0;
		subconstraints.gridy = 1;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Minimal"));
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 0;
		subconstraints.gridy = 2;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Durchschnitt"));
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 0;
		subconstraints.gridy = 3;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Spiele"));
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 1;
		subconstraints.gridy = 0;
		subconstraints.weightx = 1.0;
		ratingentry = new RatingTableEntry(ratingPos[0] * 2, true);

		// ratingentry.getComponent ( false ).setPreferredSize ( new Dimension(
		// 120, 14 ) );
		sublayout.setConstraints(ratingentry.getComponent(false), subconstraints);
		subpanel.add(ratingentry.getComponent(false));

		subconstraints.gridx = 1;
		subconstraints.gridy = 1;
		ratingentry = new RatingTableEntry(ratingPos[1] * 2, true);
		sublayout.setConstraints(ratingentry.getComponent(false), subconstraints);
		subpanel.add(ratingentry.getComponent(false));

		subconstraints.gridx = 1;
		subconstraints.gridy = 2;
		ratingentry = new RatingTableEntry(Math.round(ratingPos[2] * 2), true);
		sublayout.setConstraints(ratingentry.getComponent(false), subconstraints);
		subpanel.add(ratingentry.getComponent(false));

		subconstraints.gridx = 2;
		subconstraints.gridy = 0;
		subconstraints.weightx = 0.0;
		label = new JLabel(ratingPos[0] + "");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 2;
		subconstraints.gridy = 1;
		label = new JLabel(ratingPos[1] + "");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 2;
		subconstraints.gridy = 2;
		label = new JLabel(core.util.Helper.round(ratingPos[2], 2) + "");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		subconstraints.gridx = 1;
		subconstraints.gridy = 3;
		subconstraints.gridwidth = 2;
		label = new JLabel(((int) ratingPos[3]) + "", SwingConstants.CENTER);
		label.setHorizontalAlignment(JLabel.RIGHT);
		sublayout.setConstraints(label, subconstraints);
		subpanel.add(label);

		constraints.gridx = 6;
		constraints.gridy = 13;
		constraints.weightx = 0.5;
		constraints.gridheight = 4;
		constraints.gridwidth = 5;
		layout.setConstraints(subpanel, constraints);
		panel.add(subpanel);

		getContentPane().add(panel, BorderLayout.CENTER);
	}

	/**
	 * init a label
	 * 
	 * @param y
	 * @param constraints
	 * @param layout
	 * @param panel
	 * @param label
	 */
	private void initBlueLabel(int y, GridBagConstraints constraints, GridBagLayout layout,
			JPanel panel, JLabel label) {
		setPosition(constraints, 12, y);
		constraints.weightx = 0.0;
		layout.setConstraints(label, constraints);
		panel.add(label);
	}

	/**
	 * init a value field
	 * 
	 * @param y
	 * @param constraints
	 * @param layout
	 * @param panel
	 * @param component
	 */
	private void initBlueField(int y, GridBagConstraints constraints, GridBagLayout layout,
			JPanel panel, JComponent component) {
		setPosition(constraints, 13, y);
		constraints.weightx = 1.0;
		component.setPreferredSize(SpielerDetailPanel.COMPONENTENSIZE2);
		layout.setConstraints(component, constraints);
		panel.add(component);
	}

	/**
	 * set position in gridBag
	 * 
	 * @param constraints
	 * @param x
	 * @param y
	 */
	private void setPosition(GridBagConstraints c, int x, int y) {
		c.gridx = x;
		c.gridy = y;
	}

	private void showNormal(DoppelLabelEntry labelEntry, byte playerPosition, Spieler m_clSpieler) {
		labelEntry.getLinks().setText(
				Helper.round(m_clSpieler.calcPosValue(playerPosition, true),
						core.model.UserParameter.instance().anzahlNachkommastellen) + "");
		labelEntry.getRechts().clear();
	}

	private void showWithCompare(DoppelLabelEntry labelEntry, byte playerPosition,
			Spieler m_clSpieler, Spieler m_clVergleichsSpieler) {
		labelEntry.getLinks().setText(
				Helper.round(m_clSpieler.calcPosValue(playerPosition, true),
						core.model.UserParameter.instance().anzahlNachkommastellen) + "");

		labelEntry.getRechts().setSpecialNumber(
				m_clSpieler.calcPosValue(playerPosition, true)
						- m_clVergleichsSpieler.calcPosValue(playerPosition, true), false);
	}
}
