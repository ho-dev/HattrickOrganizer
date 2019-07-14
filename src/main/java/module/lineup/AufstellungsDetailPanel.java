// %3280892954:de.hattrickorganizer.gui.lineup%
package module.lineup;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.constants.player.PlayerAbility;
import core.datatype.CBItem;
import core.gui.Refreshable;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.RatingTableEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.AufstellungCBItem;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.Team;
import core.model.match.IMatchDetails;
import core.model.match.Matchdetails;
import core.model.player.Player;
import core.rating.RatingPredictionConfig;
import core.util.HOLogger;
import core.util.Helper;
import module.teamAnalyzer.ui.RatingUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Create the lineup detail panel.
 */
public final class AufstellungsDetailPanel extends ImagePanel implements Refreshable, ItemListener, PopupMenuListener {
	// ~ Instance fields
	// ----------------------------------------------------------------------------

	private static final long serialVersionUID = -2077901764599789950L;

	private AufstellungsRatingPanel m_jpRating = new AufstellungsRatingPanel();
	
	private MinuteTogglerPanel m_jpMinuteToggler = new MinuteTogglerPanel(this);
	
	final String offensive_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.offensive");
	final String defensive_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.defensive");
	final String neutral_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.neutral");

	private ColorLabelEntry m_jpAktuellesSystem = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
			SwingConstants.CENTER);
	private ColorLabelEntry m_jpDurchschnittErfahrung = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
			SwingConstants.CENTER);
	private ColorLabelEntry m_jpErfahrungAktuellesSystem = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
			SwingConstants.CENTER);
//	private ColorLabelEntry m_jpGesamtStaerkeText = new ColorLabelEntry("",
//			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
	private ColorLabelEntry m_jpTaktikStaerke = new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
			SwingConstants.CENTER);
//	private RatingTableEntry m_jpGesamtStaerke = new RatingTableEntry();
	private CBItem[] EINSTELLUNG = {
			new CBItem(
					HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.playitcool"),
					IMatchDetails.EINSTELLUNG_PIC),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.normal"),
					IMatchDetails.EINSTELLUNG_NORMAL),
			new CBItem(HOVerwaltung.instance().getLanguageString(
					"ls.team.teamattitude.matchoftheseason"), IMatchDetails.EINSTELLUNG_MOTS) };
	private JComboBox m_jcbEinstellung = new JComboBox(EINSTELLUNG);

	private JComboBox m_jcbSelbstvertrauen = new JComboBox(TeamConfidence.ITEMS);

	private CBItem[] TRAINERTYPE = {
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.coachtype.defensive"), 0),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.coachtype.neutral"), 2),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.coachtype.offensive"), 1), };

	private JComboBox m_jcbTrainerType = new JComboBox(TRAINERTYPE);

	private CBItem[] PREDICTIONTYPE = getPredictionItems();
	private JComboBox m_jcbPredictionType = new JComboBox(PREDICTIONTYPE);

	private JComboBox m_jcbMainStimmung = new JComboBox(TeamSpirit.ITEMS);
	private CBItem[] SUBSTIMM = {
			new CBItem(HOVerwaltung.instance().getLanguageString("verylow"), 0),
			new CBItem(HOVerwaltung.instance().getLanguageString("low"), 1),
			new CBItem(HOVerwaltung.instance().getLanguageString("Durchschnitt"), 2),
			new CBItem(HOVerwaltung.instance().getLanguageString("high"), 3),
			new CBItem(HOVerwaltung.instance().getLanguageString("veryhigh"), 4) };
	private JComboBox m_jcbSubStimmung = new JComboBox(SUBSTIMM);
	private CBItem[] TAKTIK = {
			new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_NORMAL),
					IMatchDetails.TAKTIK_NORMAL),
			new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_PRESSING),
					IMatchDetails.TAKTIK_PRESSING),
			new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_KONTER),
					IMatchDetails.TAKTIK_KONTER),
			new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_MIDDLE),
					IMatchDetails.TAKTIK_MIDDLE),
			new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_WINGS),
					IMatchDetails.TAKTIK_WINGS),
			new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_CREATIVE),
					IMatchDetails.TAKTIK_CREATIVE),
			new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_LONGSHOTS),
					IMatchDetails.TAKTIK_LONGSHOTS) };
	private JComboBox m_jcbTaktik = new JComboBox(TAKTIK);

	// home / away / away-derby
	private CBItem[] LOCATION = {
			new CBItem(HOVerwaltung.instance().getLanguageString("Heimspiel"),
					IMatchDetails.LOCATION_HOME), //
			new CBItem(HOVerwaltung.instance().getLanguageString("matchlocation.away"),
					IMatchDetails.LOCATION_AWAY), //
			new CBItem(HOVerwaltung.instance().getLanguageString("matchlocation.awayderby"),
					IMatchDetails.LOCATION_AWAYDERBY), //
			new CBItem(HOVerwaltung.instance().getLanguageString("matchlocation.tournament"),
					IMatchDetails.LOCATION_TOURNAMENT) //
	};
	private JComboBox m_jcbLocation = new JComboBox(LOCATION);

	// Pull back minute
	private CBItem[] PULLBACK_MINUTE = {
			new CBItem(HOVerwaltung.instance().getLanguageString("PullBack.None"), 90),
			new CBItem("85", 85), new CBItem("80", 80), new CBItem("75", 75), new CBItem("70", 70),
			new CBItem("65", 65), new CBItem("60", 60), new CBItem("55", 55), new CBItem("50", 50),
			new CBItem("45", 45), new CBItem("40", 40), new CBItem("35", 35), new CBItem("30", 30),
			new CBItem("25", 25), new CBItem("20", 20), new CBItem("15", 15), new CBItem("10", 10),
			new CBItem("5", 5),
			new CBItem(HOVerwaltung.instance().getLanguageString("PullBack.WholeGame"), 0) };

	private JComboBox m_jcbPullBackMinute = new JComboBox(PULLBACK_MINUTE);

	private JCheckBox m_jchPullBackOverride = new JCheckBox(HOVerwaltung.instance()
			.getLanguageString("PullBack.Override"), false);
	
	private CBItem[] STYLE_OF_PLAY = {
			new CBItem("100% " + defensive_sop, -10),
			new CBItem("80% " + defensive_sop, -8),
			new CBItem("60% " + defensive_sop, -6),
			new CBItem("50% " + defensive_sop, -5),
			new CBItem("40% " + defensive_sop, -4),
			new CBItem("30% " + defensive_sop, -3),
			new CBItem("20% " + defensive_sop, -2),
			new CBItem("10% " + defensive_sop, -1),
			new CBItem(neutral_sop, 0),
			new CBItem("10% " + offensive_sop, 1),
			new CBItem("20% " + offensive_sop, 2),
			new CBItem("30% " + offensive_sop, 3),
			new CBItem("40% " + offensive_sop, 4),
			new CBItem("50% " + offensive_sop, 5),
			new CBItem("60% " + offensive_sop, 6),
			new CBItem("80% " + offensive_sop, 8),
			new CBItem("100% " + offensive_sop, 10)
	};
	
	private JComboBox  m_jcbStyleOfPlay = new JComboBox(STYLE_OF_PLAY);
	
	private CBItem[] TACTICAL_ASSISTANTS = {
			new CBItem("0", 0),
			new CBItem("1", 1),
			new CBItem("2", 2),
			new CBItem("3", 3),
			new CBItem("4", 4),
			new CBItem("5", 5)
	};
	
	private JComboBox  m_jcbTacticalAssistants = new JComboBox(TACTICAL_ASSISTANTS);
	
	private boolean doSilentRefresh = false;
	
	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new AufstellungsDetailPanel object.
	 */
	public AufstellungsDetailPanel() {
		
		// Init the local tactical assistant count; ReInit is not called on creation. Do this before initComponents to have
		// this happen before UpdateStyleOfPlayBox() is called.
		
		initComponents();
		
		core.gui.RefreshManager.instance().registerRefreshable(this);
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * Set the constant for Pic/Mots/Normal.
	 *
	 * @param einstellung
	 *            the constant for Pic/Mots/Normal
	 */
	public void setEinstellung(int einstellung) {
		Helper.markierenComboBox(m_jcbEinstellung, einstellung);
	}

	/**
	 * Get the constant for Pic/Mots/Normal.
	 *
	 * @return the constant for Pic/Mots/Normal
	 */
	public int getEinstellung() {
		return ((CBItem) m_jcbEinstellung.getSelectedItem()).getId();
	}

	/**
	 * Set the match location (home/away/awayderby/tournament).
	 * And in case it is a tournament match, it will also set TS and confidence to default values
	 * @param location
	 *            the constant for the location
	 */
	private void setLocation(int location) {
		// Set the location
		Helper.markierenComboBox(m_jcbLocation, location);

		if (location == IMatchDetails.LOCATION_TOURNAMENT) {
			setStimmung(6, 2); // Set Team Spirit to content (cf: https://www.hattrick.org/Help/Rules/Tournaments.aspx)
			m_jcbMainStimmung.setEnabled(false);
			m_jcbSubStimmung.setEnabled(false);
			setSelbstvertrauen(6); // Set Team Spirit to wonderful (cf: https://www.hattrick.org/Help/Rules/Tournaments.aspx)
			m_jcbSelbstvertrauen.setEnabled(false);
		}
		else
		{
			m_jcbMainStimmung.setEnabled(true);
			m_jcbSubStimmung.setEnabled(true);
			m_jcbSelbstvertrauen.setEnabled(true);
		}
	}

	public void setLabels() {
		if (HOVerwaltung.instance().getModel().getTeam() != null) {
			final HOModel homodel = HOVerwaltung.instance().getModel();
			final Vector<Player> allPlayer = homodel.getAllSpieler();
			final Lineup aufstellung = homodel.getLineup();

			// HRF comparison required
			if (AufstellungsVergleichHistoryPanel.isVergleichgefordert()) {
				// First set the values ​​to those of the loaded setup
				final AufstellungCBItem vergleichsaufstellungcbitem = AufstellungsVergleichHistoryPanel
 						.getVergleichsAufstellung();

				if (vergleichsaufstellungcbitem != null) {
					final Lineup vergleichsaufstellung = vergleichsaufstellungcbitem
							.getAufstellung();

					if (vergleichsaufstellung != null) {
						// Wegen der Berechnung zuerst die Aufstellung kurz in
						// Model packen, da immer die aktuelle Aufstellung
						// genommen wird
						// vergleichsaufstellung.updateRatingPredictionConfig();
						homodel.setAufstellung(vergleichsaufstellung);
						m_jpRating.setTopRight(vergleichsaufstellung.getRatings().getLeftDefense().get(m_jpMinuteToggler.getCurrentKey()));
						m_jpRating.setTopCenter(vergleichsaufstellung.getRatings().getCentralDefense().get(m_jpMinuteToggler.getCurrentKey()));
						m_jpRating.setTopLeft(vergleichsaufstellung.getRatings().getRightDefense().get(m_jpMinuteToggler.getCurrentKey()));
						m_jpRating.setMiddle(vergleichsaufstellung.getRatings().getMidfield().get(m_jpMinuteToggler.getCurrentKey()));
						m_jpRating.setBottomRight(vergleichsaufstellung.getRatings().getLeftAttack().get(m_jpMinuteToggler.getCurrentKey()));
						m_jpRating.setBottomCenter(vergleichsaufstellung.getRatings().getCentralAttack().get(m_jpMinuteToggler.getCurrentKey()));
						m_jpRating.setBottomLeft(vergleichsaufstellung.getRatings().getRightAttack().get(m_jpMinuteToggler.getCurrentKey()));

						// Put back the right Lineup
						homodel.setAufstellung(aufstellung);
					}
				}
			}

			// no comparison required
			m_jpRating.clear();
			m_jpRating.setTopRightText(PlayerAbility.getNameForSkill(
					(RatingUtil.getIntValue4Rating(aufstellung.getRatings().getLeftDefense().get(m_jpMinuteToggler.getCurrentKey()))), false, true));
			m_jpRating.setTopCenterText(PlayerAbility.getNameForSkill(
					(RatingUtil.getIntValue4Rating(aufstellung.getRatings().getCentralDefense().get(m_jpMinuteToggler.getCurrentKey()))), false,	true));
			m_jpRating.setTopLeftText(PlayerAbility.getNameForSkill(
					(RatingUtil.getIntValue4Rating(aufstellung.getRatings().getRightDefense().get(m_jpMinuteToggler.getCurrentKey()))), false,	true));
			m_jpRating.setMiddleText(PlayerAbility.getNameForSkill(
					(RatingUtil.getIntValue4Rating(aufstellung.getRatings().getMidfield().get(m_jpMinuteToggler.getCurrentKey()))), false, true));
			m_jpRating.setBottomRightText(PlayerAbility.getNameForSkill(
					(RatingUtil.getIntValue4Rating(aufstellung.getRatings().getLeftAttack().get(m_jpMinuteToggler.getCurrentKey()))),false, true));
			m_jpRating.setBottomCenterText(PlayerAbility.getNameForSkill(
					(RatingUtil.getIntValue4Rating(aufstellung.getRatings().getCentralAttack().get(m_jpMinuteToggler.getCurrentKey()))), false,true));
			m_jpRating.setBottomLeftText(PlayerAbility.getNameForSkill(
					(RatingUtil.getIntValue4Rating(aufstellung.getRatings().getRightAttack().get(m_jpMinuteToggler.getCurrentKey()))), false,true));
			m_jpRating.setTopRight(aufstellung.getRatings().getLeftDefense().get(m_jpMinuteToggler.getCurrentKey()));
			m_jpRating.setTopCenter(aufstellung.getRatings().getCentralDefense().get(m_jpMinuteToggler.getCurrentKey()));
			m_jpRating.setTopLeft(aufstellung.getRatings().getRightDefense().get(m_jpMinuteToggler.getCurrentKey()));
			m_jpRating.setMiddle(aufstellung.getRatings().getMidfield().get(m_jpMinuteToggler.getCurrentKey()));
			m_jpRating.setBottomRight(aufstellung.getRatings().getLeftAttack().get(m_jpMinuteToggler.getCurrentKey()));
			m_jpRating.setBottomCenter(aufstellung.getRatings().getCentralAttack().get(m_jpMinuteToggler.getCurrentKey()));
			m_jpRating.setBottomLeft(aufstellung.getRatings().getRightAttack().get(m_jpMinuteToggler.getCurrentKey()));
			m_jpRating.setLoddar(Helper.round(aufstellung.getRatings().getLoddarStat().get(m_jpMinuteToggler.getCurrentKey()), 2));
			m_jpRating.setHatstat(aufstellung.getRatings().getHatStats().get(m_jpMinuteToggler.getCurrentKey()));

			// Recalculate Borders
			m_jpRating.calcColorBorders();

			// get Total Strength
//			final double gesamtstaerke = aufstellung.getGesamtStaerke(allPlayer, true);

			// *2 wegen halben Sternen
//			m_jpGesamtStaerke.setRating((int) (gesamtstaerke * 2));
//			m_jpGesamtStaerkeText.setText(Helper.DEFAULTDEZIMALFORMAT.format(gesamtstaerke));

			setStimmung(homodel.getTeam().getStimmungAsInt(), homodel.getTeam().getSubStimmung());
			setSelbstvertrauen(homodel.getTeam().getSelbstvertrauenAsInt());
			setTrainerType(homodel.getTrainer().getTrainerTyp());
			setPredictionType(RatingPredictionConfig.getInstancePredictionType());
			setTaktik(aufstellung.getTacticType());
			m_jpTaktikStaerke.setText(getTaktikString());

			setTacticalAssistants(homodel.getVerein().getTacticalAssistantLevels());
			setStyleOfPlay(aufstellung.getStyleOfPlay());
			setEinstellung(aufstellung.getAttitude());
			setLocation(aufstellung.getLocation());
			setPullBackMinute(aufstellung.getPullBackMinute());
			m_jcbPullBackMinute.setEnabled(!aufstellung.isPullBackOverride());
			setPullBackOverride(aufstellung.isPullBackOverride());

			float avXp = homodel.getLineupWithoutRatingRecalc().getAverageExperience();
			m_jpDurchschnittErfahrung.setText(PlayerAbility.getNameForSkill(avXp));
			m_jpDurchschnittErfahrung.setToolTipText((avXp < 0 ? (HOVerwaltung.instance()
					.getLanguageString("lineup.upload.check.captainNotSet")) : ""));

			String formationExperienceTooltip = getFormationExperienceTooltip();
			m_jpAktuellesSystem.setText(Lineup.getNameForSystem(aufstellung.ermittelSystem()));
			m_jpAktuellesSystem.setToolTipText(formationExperienceTooltip);
			int exp = homodel.getLineupWithoutRatingRecalc().getTeamErfahrung4AktuellesSystem();
			m_jpErfahrungAktuellesSystem.setText(PlayerAbility.toString(exp) + " (" + exp + ")");
			m_jpErfahrungAktuellesSystem.setToolTipText(formationExperienceTooltip);
			m_jpErfahrungAktuellesSystem.setFGColor(new Color(Math.min(
					Math.max(((8 - exp) * 32) - 1, 0), 255), 0, 0));
		}
	}

	private String getFormationExperienceTooltip() {
		Team team = HOVerwaltung.instance().getModel().getTeam();
		StringBuilder builder = new StringBuilder();
		int exp = team.getFormationExperience550();
		builder.append("<html>");
		builder.append("<b>").append(HOVerwaltung.instance().getLanguageString("ls.team.formationexperience")).append("</b><br><br>");
		builder.append("5-5-0&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience541();
		builder.append("5-4-1&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience532();
		builder.append("5-3-2&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience523();
		builder.append("5-2-3&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience451();
		builder.append("4-5-1&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience442();
		builder.append("4-4-2&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience433();
		builder.append("4-3-3&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience352();
		builder.append("3-5-2&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience343();
		builder.append("3-4-3&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		exp = team.getFormationExperience253();
		builder.append("2-5-3&#160&#160&#160");
		builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
		builder.append("</html>");
		return builder.toString();
	}

	/**
	 * Set the team confidence.
	 *
	 * @param selbstvertrauen
	 *            the confidence value
	 */
	public void setSelbstvertrauen(int selbstvertrauen) {
		Helper.markierenComboBox(m_jcbSelbstvertrauen, selbstvertrauen);
	}

	/**
	 * Set the team spirit values.
	 *
	 * @param stimmung
	 *            team spirit
	 * @param subStimmung
	 *            subskill of the team spirit
	 */
	public void setStimmung(int stimmung, int subStimmung) {
		Helper.markierenComboBox(m_jcbMainStimmung, stimmung);
		Helper.markierenComboBox(m_jcbSubStimmung, subStimmung);
	}

	/**
	 * Set the trainer type.
	 */
	public void setTrainerType(int newTrainerType) {
		Helper.markierenComboBox(m_jcbTrainerType, newTrainerType);
	}

	/**
	 * Set the prediction type.
	 */
	public void setPredictionType(int newPredictionType) {
		core.util.Helper.markierenComboBox(m_jcbPredictionType, newPredictionType);
	}

	/**
	 * Set the tactic using it's constant.
	 *
	 * @param taktik
	 *            the tactic constant
	 */
	public void setTaktik(int taktik) {
		Helper.markierenComboBox(m_jcbTaktik, taktik);
	}

	/**
	 * Get the tactic constant.
	 *
	 * @return get the tactic constant
	 */
	public int getTaktik() {
		return ((CBItem) m_jcbTaktik.getSelectedItem()).getId();
	}

	public void setStyleOfPlay(int style){
		Helper.markierenComboBox(m_jcbStyleOfPlay, style);
	}
	
	public int getStyleOfPlay() {
		return ((CBItem) m_jcbStyleOfPlay.getSelectedItem()).getId();
	}
	
	public void setTacticalAssistants(int assistants){
		Helper.markierenComboBox(m_jcbTacticalAssistants, assistants);
	}
	
	public int getTacticalAssistants() {
		return ((CBItem) m_jcbTacticalAssistants.getSelectedItem()).getId();
	}
	
	/**
	 * Set the pullback minute
	 *
	 * @param minute
	 */
	public void setPullBackMinute(int minute) {
		Helper.markierenComboBox(m_jcbPullBackMinute, minute);
	}

	/**
	 * Get the pullback minute
	 *
	 * @return get the pullback minute
	 */
	public int getPullBackMinute() {
		return ((CBItem) m_jcbPullBackMinute.getSelectedItem()).getId();
	}

	/**
	 * Sets the pullback override flag.
	 *
	 * @param pullBackOverride
	 */
	private void setPullBackOverride(boolean pullBackOverride) {
		m_jchPullBackOverride.setSelected(pullBackOverride);
	}

	/**
	 * React on state changed events
	 *
	 * @param event
	 *            the event
	 */
	@Override
	public void itemStateChanged(ItemEvent event) {

		if (event.getStateChange() == ItemEvent.DESELECTED) {
			if (event.getSource().equals(m_jchPullBackOverride)) {
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setPullBackOverride(false);
				m_jcbPullBackMinute.setEnabled(true);
				refresh();
			}

		} else if (event.getStateChange() == ItemEvent.SELECTED) {
			if (event.getSource().equals(m_jchPullBackOverride)) {
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setPullBackOverride(true);
				m_jcbPullBackMinute.setEnabled(false);
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbPullBackMinute)) {
				// Pull Back minute changed
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setPullBackMinute(((CBItem) m_jcbPullBackMinute.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbTaktik)) {
				// Tactic changed
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setTacticType(((CBItem) m_jcbTaktik.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbEinstellung)) {
				// Attitude changed
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setAttitude(((CBItem) m_jcbEinstellung.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbMainStimmung)) {
				// team spirit changed
				HOVerwaltung.instance().getModel().getTeam().setStimmungAsInt(((CBItem) m_jcbMainStimmung.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbSubStimmung)) {
				// team spirit (sub) changed
				HOVerwaltung.instance().getModel().getTeam().setSubStimmung(((CBItem) m_jcbSubStimmung.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbSelbstvertrauen)) {
				// team confidence changed
				HOVerwaltung.instance().getModel().getTeam().setSelbstvertrauenAsInt(((CBItem) m_jcbSelbstvertrauen.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbTrainerType)) {
				// trainer type changed
				HOVerwaltung.instance().getModel().getTrainer().setTrainerTyp(((CBItem) m_jcbTrainerType.getSelectedItem()).getId());
				doSilentRefresh = true; // To save the rating change display.
				// will set current styleOfPlay value if valid otherwise set to trainer default
				updateStyleOfPlayBox(HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getStyleOfPlay());
				addAllStyleofPlayItems();
				doSilentRefresh = false;
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbPredictionType)) {
				// prediction type changed
				RatingPredictionConfig.setInstancePredictionType(((CBItem) m_jcbPredictionType.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbLocation)) {
				// location changed
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setLocation((short) ((CBItem) m_jcbLocation.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbStyleOfPlay)) {
				// CoachModifier/StyleOfPlay changed
				HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().setStyleOfPlay(((CBItem) m_jcbStyleOfPlay.getSelectedItem()).getId());
				HOVerwaltung.instance().getModel().getLineup(); // => Force rating calculation
			} else if (event.getSource().equals(m_jcbTacticalAssistants)) {
				HOVerwaltung.instance().getModel().getVerein().setTacticalAssistantLevels(((CBItem) m_jcbTacticalAssistants.getSelectedItem()).getId());
				// Number of tactical assistants changed
					doSilentRefresh = true; // To save the item change display
					updateStyleOfPlayBox(HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getStyleOfPlay()); // Attempt to set the old value, otherwise trainer default.
					addAllStyleofPlayItems();
					doSilentRefresh = false;
			}
			refresh();
		}
	}

	/**
	 * Reinit the GUI:
	 */
	@Override
	public void reInit() {
		setLabels();
	}

	/**
	 * Refresh the GUI.
	 */
	@Override
	public void refresh() {
		removeItemListeners();
		
		if (!doSilentRefresh) {
			setLabels();
		}
		
		addItemListeners();
	}

	/**
	 * Get the i18n'ed name of the tactic together with it's strength.
	 *
	 * @return the name of the tactic incl. strength
	 */
	private String getTaktikString() {
		final Lineup aufstellung = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

		// getTaktik liefert Taktik aus ComboBox, wir wollen Taktik aus
		// aufstellung!
		// switch (getTaktik()) {
		switch (aufstellung.getTacticType()) {
		case IMatchDetails.TAKTIK_NORMAL:
			// Play creatively has no tactic level
		case IMatchDetails.TAKTIK_CREATIVE:
			return (" ");

		case IMatchDetails.TAKTIK_PRESSING:
		case IMatchDetails.TAKTIK_KONTER:
		case IMatchDetails.TAKTIK_MIDDLE:
		case IMatchDetails.TAKTIK_WINGS:
		case IMatchDetails.TAKTIK_LONGSHOTS:
			return PlayerAbility.getNameForSkill(aufstellung.getTacticLevel(getTaktik()));

		default:
			return HOVerwaltung.instance().getLanguageString("Unbestimmt");
		}
	}

	/**
	 * Initialize the GUI and layout components.
	 */
	private void initComponents() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;

		setLayout(layout);

		JLabel label;
		JPanel panel;

		int yPos = 1;

		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(m_jpMinuteToggler, constraints);

		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.gridwidth = 2;
		layout.setConstraints(m_jpRating, constraints);
		add(m_jpRating, constraints);

		yPos++;
		panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);
//		m_jpGesamtStaerke.setToolTipText(HOVerwaltung.instance().getLanguageString("Rating"));
//		panel.add(m_jpGesamtStaerke.getComponent(false), BorderLayout.CENTER);
//		m_jpGesamtStaerkeText.setFontStyle(Font.BOLD);
//		m_jpGesamtStaerkeText.setToolTipText(HOVerwaltung.instance().getLanguageString("Rating"));
//		panel.add(m_jpGesamtStaerkeText.getComponent(false), BorderLayout.EAST);
		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.gridwidth = 2;
		constraints.weighty = 0.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(panel, constraints);
		add(panel);

		yPos++;
		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.teamattitude")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbSelbstvertrauen.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbSelbstvertrauen.setMaximumRowCount(3);
		m_jcbEinstellung.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"tt_AufstellungsDetails_Einstellung"));
		layout.setConstraints(m_jcbEinstellung, constraints);
		add(m_jcbEinstellung);

		yPos++;
		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.tactic")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbSelbstvertrauen.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbSelbstvertrauen.setMaximumRowCount(7);
		m_jcbTaktik.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"tt_AufstellungsDetails_Taktik"));
		layout.setConstraints(m_jcbTaktik, constraints);
		add(m_jcbTaktik);

		yPos++;
		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.styleofPlay")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		layout.setConstraints(m_jcbStyleOfPlay, constraints);
		add(m_jcbStyleOfPlay);
		
		yPos++;
		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		initLabel(constraints, layout, new JLabel(HOVerwaltung.instance()
				.getLanguageString("Venue")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbSelbstvertrauen.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbSelbstvertrauen.setMaximumRowCount(4);
		m_jcbLocation.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"tt_AufstellungsDetails_Spielort"));
		m_jcbLocation.setOpaque(false);
		layout.setConstraints(m_jcbLocation, constraints);
		add(m_jcbLocation);

		yPos++;
		constraints.gridx = 1;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.tacticalskill"));
		layout.setConstraints(label, constraints);
		add(label);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		constraints.weightx = 1.0;
		layout.setConstraints(m_jpTaktikStaerke.getComponent(false), constraints);
		add(m_jpTaktikStaerke.getComponent(false));

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbMainStimmung.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbMainStimmung.setMaximumRowCount(13);
		layout.setConstraints(m_jcbMainStimmung, constraints);
		add(m_jcbMainStimmung);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("lineup.teamspiritsub")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbSubStimmung.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbSubStimmung.setMaximumRowCount(5);
		layout.setConstraints(m_jcbSubStimmung, constraints);
		add(m_jcbSubStimmung);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.confidence")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbSelbstvertrauen.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbSelbstvertrauen.setMaximumRowCount(10);
		layout.setConstraints(m_jcbSelbstvertrauen, constraints);
		add(m_jcbSelbstvertrauen);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.coachtype")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTrainerType.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbTrainerType.setMaximumRowCount(3);
		layout.setConstraints(m_jcbTrainerType, constraints);
		add(m_jcbTrainerType);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.club.staff.tacticalassistant")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbTacticalAssistants.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		layout.setConstraints(m_jcbTacticalAssistants, constraints);
		add(m_jcbTacticalAssistants);
		
		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance()
						.getLanguageString("PullBack.PullBackStartMinute")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		constraints.gridwidth = 1;
		m_jcbPullBackMinute.setPreferredSize(new Dimension(50, Helper.calcCellWidth(20)));
		m_jcbPullBackMinute.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"PullBack.PullBackStartMinute.ToolTip"));
		m_jcbPullBackMinute.setOpaque(false);
		layout.setConstraints(m_jcbPullBackMinute, constraints);
		add(m_jcbPullBackMinute);

		yPos++;
		initLabel(constraints, layout, new JLabel(""), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jchPullBackOverride.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"PullBack.Override.ToolTip"));
		m_jchPullBackOverride.setOpaque(false);
		layout.setConstraints(m_jchPullBackOverride, constraints);
		add(m_jchPullBackOverride);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("PredictionType")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		m_jcbPredictionType.setPreferredSize(new Dimension(50, core.util.Helper
				.calcCellWidth(20)));
		// m_jcbPredictionType.setMaximumRowCount(3);
		layout.setConstraints(m_jcbPredictionType, constraints);
		add(m_jcbPredictionType);

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("DurchschnittErfahrung")),
				yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		layout.setConstraints(m_jpDurchschnittErfahrung.getComponent(false), constraints);
		add(m_jpDurchschnittErfahrung.getComponent(false));

		yPos++;
		initLabel(constraints, layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.formation")), yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		layout.setConstraints(m_jpAktuellesSystem.getComponent(false), constraints);
		add(m_jpAktuellesSystem.getComponent(false));

		yPos++;
		initLabel(
				constraints,
				layout,
				new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.formationexperience")),
				yPos);
		constraints.gridx = 2;
		constraints.gridy = yPos;
		layout.setConstraints(m_jpErfahrungAktuellesSystem.getComponent(false), constraints);
		add(m_jpErfahrungAktuellesSystem.getComponent(false));

		// Add all item listeners
		addItemListeners();
	}

	/**
	 * Add all item listeners to the combo boxes
	 */
	private void addItemListeners() {
		m_jcbEinstellung.addItemListener(this);
		m_jcbTaktik.addItemListener(this);
		m_jcbLocation.addItemListener(this);
		m_jcbMainStimmung.addItemListener(this);
		m_jcbSubStimmung.addItemListener(this);
		m_jcbSelbstvertrauen.addItemListener(this);
		m_jcbTrainerType.addItemListener(this);
		m_jcbPredictionType.addItemListener(this);
		m_jcbPullBackMinute.addItemListener(this);
		m_jchPullBackOverride.addItemListener(this);
		m_jcbTacticalAssistants.addItemListener(this);
		m_jcbStyleOfPlay.addItemListener(this);
		m_jcbStyleOfPlay.addPopupMenuListener(this);
	}

	/**
	 * Remove all item listeners from the combo boxes
	 */
	private void removeItemListeners() {
		m_jcbEinstellung.removeItemListener(this);
		m_jcbTaktik.removeItemListener(this);
		m_jcbLocation.removeItemListener(this);
		m_jcbMainStimmung.removeItemListener(this);
		m_jcbSubStimmung.removeItemListener(this);
		m_jcbSelbstvertrauen.removeItemListener(this);
		m_jcbTrainerType.removeItemListener(this);
		m_jcbPredictionType.removeItemListener(this);
		m_jcbPullBackMinute.removeItemListener(this);
		m_jchPullBackOverride.removeItemListener(this);
		m_jcbTacticalAssistants.removeItemListener(this);
		m_jcbStyleOfPlay.removeItemListener(this);
		m_jcbStyleOfPlay.removePopupMenuListener(this);
	}

	private CBItem[] getPredictionItems() {
		final ResourceBundle bundle = HOVerwaltung.instance().getResource();
		String[] allPredictionNames = RatingPredictionConfig.getAllPredictionNames();
		CBItem[] allItems = new CBItem[allPredictionNames.length];
		for (int i = 0; i < allItems.length; i++) {
			String predictionName = allPredictionNames[i];
			if (bundle.containsKey("prediction." + predictionName))
				predictionName = HOVerwaltung.instance().getLanguageString(
						"prediction." + predictionName);
			allItems[i] = new CBItem(predictionName, i);
		}
		return allItems;
	}

	private void initLabel(GridBagConstraints constraints, GridBagLayout layout, JLabel label, int y) {
		constraints.gridx = 1;
		constraints.gridy = y;
		layout.setConstraints(label, constraints);
		add(label);
	}
	
	// each time updateStyleOfPlayBox gets called we need to add all elements back so that we can load stored lineups
	// so we need addAllStyleOfPlayItems() after every updateStyleOfPlayBox()
	private void updateStyleOfPlayBox(int oldValue)
	{
		// remove all combo box items and add new ones.
		
		List<Integer> legalValues = getValidStyleOfPlayValues();
		
		m_jcbStyleOfPlay.removeAllItems();
		
		for (int value : legalValues)
		{
			CBItem cbItem;
			
			if (value == 0) {
				cbItem = new CBItem(neutral_sop, value);
			} else if (value > 0) {
				cbItem = new CBItem((value * 10) + "% " + offensive_sop, value);
			} else {
				cbItem = new CBItem((Math.abs(value) * 10) + "% " + defensive_sop, value);
			}
			m_jcbStyleOfPlay.addItem(cbItem);
			
		}
		// Set trainer default value
		setStyleOfPlay(getDefaultTrainerStyleOfPlay());
		// Attempt to set the old value. If it is not possible it will do nothing.
		setStyleOfPlay(oldValue);
	}
	// this is needed so that you can load every style of play value from stored lineups
	public void addAllStyleofPlayItems() {
		for (CBItem c : STYLE_OF_PLAY) {
			m_jcbStyleOfPlay.addItem(c);
		}
	}
	
	public List<Integer> getValidStyleOfPlayValues()
	{
		int trainer;
		int tacticalAssistants;
		try {
			trainer = HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp();
			tacticalAssistants = HOVerwaltung.instance().getModel().getVerein().getTacticalAssistantLevels();
			
		} catch (Exception e) {
			trainer = 2;
			tacticalAssistants = 0;
			HOLogger.instance().error(getClass(), "Model not ready, put default value " + trainer + " for trainer and "  + tacticalAssistants + " for tactical Assistants.");
		}
		
		int min = -10 ;
		int max = 10;
		int step = 1; // For all types
		
		switch (trainer) {
			case  0 : // Defensive
				min = -10;
				max = -10 + 2 * tacticalAssistants;
				break;
				
			case 1: // Offensive
				min = 10 - 2 * tacticalAssistants;
				max = 10;
				break;
				
			case 2: // Neutral
				min = 0 - tacticalAssistants;
				max = tacticalAssistants;
				break;
				
			default:
				HOLogger.instance().error(getClass(), "Illegal trainer type found: " + trainer);
				break;
		}
		
		List<Integer> result = new ArrayList<Integer>();
		int value = min;
		do {
		
			result.add(value);
			value = value + step;

		} while (value <= max); 
		
		return result;
	}
	
	public int getDefaultTrainerStyleOfPlay() {
		int result = 0;
		try {
			int trainer = HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp();
			
			switch (trainer) {
				case  0 : // Defensive
					result = -10;
					break;
					
				case 1: // Offensive
					result = 10;
					break;
					
				case 2: // Neutral
					result = 0;
					break;
					
				default:
					HOLogger.instance().error(getClass(), "Illegal trainer type found: " + trainer);
					break;
			}
			
			return result;
			
		} catch (Exception e) {
			// Happens at empty db, for instance.
			return result;
		} 
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// no silent refresh here
		updateStyleOfPlayBox(HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getStyleOfPlay());
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		addAllStyleofPlayItems();	
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
		
	}
}
