// %2517784300:de.hattrickorganizer.gui.matches%
package module.matches;

import core.constants.player.PlayerAbility;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;
import core.util.Helper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Zeigt die Stärken eines Matches an
 */
class ManschaftsBewertungsPanel extends LazyImagePanel {

	private static final long serialVersionUID = 1835093736247065469L;
	private JLabel gastCenterAttLabel;
	private JLabel gastCenterDefLabel;
	private JLabel gastGesamtLabel;
	private JLabel gastLeftAttLabel;
	private JLabel gastLeftDefLabel;
	private JLabel gastMidfieldLabel;
	private JLabel gastRightAttLabel;
	private JLabel gastRightDefLabel;
	private JLabel gastTeamNameLabel;
	private JLabel gastTeamToreLabel;
	private JLabel heimCenterAttLabel;
	private JLabel heimCenterDefLabel;
	private JLabel heimGesamtLabel;
	private JLabel heimLeftAttLabel;
	private JLabel heimLeftDefLabel;
	private JLabel heimMidfieldLabel;
	private JLabel heimRightAttLabel;
	private JLabel heimRightDefLabel;
	private JLabel heimTeamNameLabel;
	private JLabel heimTeamToreLabel;
	private boolean initialized = false;
	private boolean needsRefresh = false;
	private final MatchesModel matchesModel;

	/**
	 * Creates a new ManschaftsBewertungsPanel object.
	 */
	ManschaftsBewertungsPanel(MatchesModel matchesModel) {
		this(matchesModel, false);
	}

	ManschaftsBewertungsPanel(MatchesModel matchesModel, boolean print) {
		super(print);
		this.matchesModel = matchesModel;
	}

	private void addListeners() {
		this.matchesModel.addMatchModelChangeListener(new MatchModelChangeListener() {

			@Override
			public void matchChanged() {
				setNeedsRefresh(true);
			}
		});
	}

	private void initComponents() {

		setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.weighty = 0.0;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 3, 2, 2);

		JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager
				.getColor(HOColorName.PANEL_BORDER)));
		panel.setBackground(getBackground());

		// Platzhalter
		JLabel label = new JLabel("  ");
		constraints.weightx = 0.0;
		constraints.gridheight = 20;
		constraints.gridwidth = 1;
		add(panel, label, layout, constraints, 3, 1);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Heim"));
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("Gast"));
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 1));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 4;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		layout.setConstraints(label, constraints);
		panel.add(label);

		// Teams mit Ergebnis
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.result"));
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 0;
		constraints.gridy = 4;
		layout.setConstraints(label, constraints);
		panel.add(label);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 1;
		constraints.gridy = 4;
		heimTeamNameLabel = new JLabel();
		heimTeamNameLabel.setPreferredSize(new Dimension(140, 14));
		heimTeamNameLabel.setFont(heimTeamNameLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimTeamNameLabel, constraints);
		panel.add(heimTeamNameLabel);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 2;
		constraints.gridy = 4;
		heimTeamToreLabel = new JLabel();
		heimTeamToreLabel.setFont(heimTeamToreLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(heimTeamToreLabel, constraints);
		panel.add(heimTeamToreLabel);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.gridx = 4;
		constraints.gridy = 4;
		gastTeamNameLabel = new JLabel();
		gastTeamNameLabel.setPreferredSize(new Dimension(140, 14));
		gastTeamNameLabel.setFont(gastTeamNameLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastTeamNameLabel, constraints);
		panel.add(gastTeamNameLabel);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.gridx = 5;
		constraints.gridy = 4;
		gastTeamToreLabel = new JLabel();
		gastTeamToreLabel.setFont(gastTeamToreLabel.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(gastTeamToreLabel, constraints);
		panel.add(gastTeamToreLabel);

		// Platzhalter
		label = new JLabel(" ");
		add(panel, label, layout, constraints, 0, 5);

		// Bewertungen
		// Mittelfeld
		label = new JLabel(HOVerwaltung.instance().getLanguageString("Gesamtstaerke"));
		add(panel, label, layout, constraints, 0, 6);
		heimGesamtLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, heimGesamtLabel, layout, constraints, 1, 6);
		gastGesamtLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, gastGesamtLabel, layout, constraints, 4, 6);

		// Platzhalter
		label = new JLabel(" ");
		add(panel, label, layout, constraints, 0, 7);

		// Mittelfeld
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
		add(panel, label, layout, constraints, 0, 8);
		heimMidfieldLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, heimMidfieldLabel, layout, constraints, 1, 8);
		gastMidfieldLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, gastMidfieldLabel, layout, constraints, 4, 8);

		// rechte Abwehrseite
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
		add(panel, label, layout, constraints, 0, 9);
		heimRightDefLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, heimRightDefLabel, layout, constraints, 1, 9);
		gastRightDefLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, gastRightDefLabel, layout, constraints, 4, 9);

		// Abwehrzentrum
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
		add(panel, label, layout, constraints, 0, 10);
		heimCenterDefLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, heimCenterDefLabel, layout, constraints, 1, 10);
		gastCenterDefLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, gastCenterDefLabel, layout, constraints, 4, 10);

		// Linke Abwehrseite
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
		add(panel, label, layout, constraints, 0, 11);
		heimLeftDefLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, heimLeftDefLabel, layout, constraints, 1, 11);
		gastLeftDefLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, gastLeftDefLabel, layout, constraints, 4, 11);

		// Rechte Angriffsseite
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
		add(panel, label, layout, constraints, 0, 12);
		heimRightAttLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, heimRightAttLabel, layout, constraints, 1, 12);
		gastRightAttLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, gastRightAttLabel, layout, constraints, 4, 12);

		// Angriffszentrum
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
		add(panel, label, layout, constraints, 0, 13);
		heimCenterAttLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, heimCenterAttLabel, layout, constraints, 1, 13);
		gastCenterAttLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, gastCenterAttLabel, layout, constraints, 4, 13);

		// Linke Angriffsseite
		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
		add(panel, label, layout, constraints, 0, 14);
		heimLeftAttLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, heimLeftAttLabel, layout, constraints, 1, 14);
		gastLeftAttLabel = new JLabel("", SwingConstants.LEFT);
		add(panel, gastLeftAttLabel, layout, constraints, 4, 14);

		setLayout(new GridBagLayout());
		GridBagConstraints mainconstraints = new GridBagConstraints();
		mainconstraints.anchor = GridBagConstraints.NORTH;
		mainconstraints.fill = GridBagConstraints.HORIZONTAL;
		mainconstraints.weighty = 0.1;
		mainconstraints.weightx = 1.0;
		mainconstraints.insets = new Insets(4, 6, 4, 6);
		add(panel, mainconstraints);
		clear();
	}

	private void add(JPanel panel, JLabel label, GridBagLayout layout,
			GridBagConstraints constraints, int x, int y) {
		if (x == 0) {
			constraints.weightx = 0.0;
			constraints.gridwidth = 1;
		} else {
			constraints.weightx = 1.0;
			constraints.gridwidth = 2;
		}

		constraints.gridx = x;
		constraints.gridy = y;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);
	}

	private void clear() {
		heimTeamNameLabel.setText(" ");
		gastTeamNameLabel.setText(" ");
		heimTeamToreLabel.setText(" ");
		gastTeamToreLabel.setText(" ");
		heimTeamNameLabel.setIcon(null);
		gastTeamNameLabel.setIcon(null);

		heimGesamtLabel.setText("");
		gastGesamtLabel.setText("");
		heimMidfieldLabel.setText("");
		gastMidfieldLabel.setText("");
		heimRightDefLabel.setText("");
		gastRightDefLabel.setText("");
		heimCenterDefLabel.setText("");
		gastCenterDefLabel.setText("");
		heimLeftDefLabel.setText("");
		gastLeftDefLabel.setText("");
		heimRightAttLabel.setText("");
		gastRightAttLabel.setText("");
		heimCenterAttLabel.setText("");
		gastCenterAttLabel.setText("");
		heimLeftAttLabel.setText("");
		gastLeftAttLabel.setText("");

		heimGesamtLabel.setIcon(null);
		gastGesamtLabel.setIcon(null);
		heimMidfieldLabel.setIcon(null);
		gastMidfieldLabel.setIcon(null);
		heimRightDefLabel.setIcon(null);
		gastRightDefLabel.setIcon(null);
		heimCenterDefLabel.setIcon(null);
		gastCenterDefLabel.setIcon(null);
		heimLeftDefLabel.setIcon(null);
		gastLeftDefLabel.setIcon(null);
		heimRightAttLabel.setIcon(null);
		gastRightAttLabel.setIcon(null);
		heimCenterAttLabel.setIcon(null);
		gastCenterAttLabel.setIcon(null);
		heimLeftAttLabel.setIcon(null);
		gastLeftAttLabel.setIcon(null);
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		MatchKurzInfo info = this.matchesModel.getMatch();
		Matchdetails details = this.matchesModel.getDetails();

		// there is no match at all
		if (info == null) {
			clear();
			return;
		}

		// match is upcoming or not finished, clear and display team names only
		if (info.getMatchStatus() != MatchKurzInfo.FINISHED) {
			clear();
		}

		// Teams
		int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();

		heimTeamNameLabel.setText(info.getHeimName());
		gastTeamNameLabel.setText(info.getGastName());

		if (info.getHeimID() == teamid) {
			heimTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.TEAM_FG));
		} else {
			heimTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
		}

		if (info.getGastID() == teamid) {
			gastTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.TEAM_FG));
		} else {
			gastTeamNameLabel.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
		}

		if (info.getMatchStatus() == MatchKurzInfo.FINISHED) {
			heimTeamToreLabel.setText(info.getHeimTore() + " (" + details.getHomeHalfTimeGoals()
					+ ") ");
			gastTeamToreLabel.setText(info.getGastTore() + " (" + details.getGuestHalfTimeGoals()
					+ ") ");

			// Sterne für Sieger!
			if (info.getMatchStatus() != MatchKurzInfo.FINISHED) {
				heimTeamNameLabel.setIcon(null);
				gastTeamNameLabel.setIcon(null);
			} else if (info.getHeimTore() > info.getGastTore()) {
				heimTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR,
						Color.WHITE));
				gastTeamNameLabel.setIcon(null);
			} else if (info.getHeimTore() < info.getGastTore()) {
				heimTeamNameLabel.setIcon(null);
				gastTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR,
						Color.WHITE));
			} else {
				heimTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR_GRAY,
						Color.WHITE));
				gastTeamNameLabel.setIcon(ThemeManager.getTransparentIcon(HOIconName.STAR_GRAY,
						Color.WHITE));
			}

			String temp;
			temp = PlayerAbility.getNameForSkill(details.getHomeGesamtstaerke(false), false, true);

			if (core.model.UserParameter.instance().zahlenFuerSkill) {
				temp += (" ("
						+ Helper.round((((details.getHomeGesamtstaerke(false) - 1) / 4) + 1), 2) + ")");
			}

			heimGesamtLabel.setText(temp);
			temp = PlayerAbility.getNameForSkill(details.getGuestGesamtstaerke(false), false, true);

			if (core.model.UserParameter.instance().zahlenFuerSkill) {
				temp += (" ("
						+ Helper.round((((details.getGuestGesamtstaerke(false) - 1) / 4) + 1), 2) + ")");
			}

			gastGesamtLabel.setText(temp);
			heimMidfieldLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getHomeMidfield()));
			gastMidfieldLabel.setText(PlayerAbility.getNameForSkill(true,
					details.getGuestMidfield()));
			heimRightDefLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getHomeRightDef()));
			gastRightDefLabel.setText(PlayerAbility.getNameForSkill(true,
					details.getGuestRightDef()));
			heimCenterDefLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getHomeMidDef()));
			gastCenterDefLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getGuestMidDef()));
			heimLeftDefLabel.setText(PlayerAbility.getNameForSkill(true, details.getHomeLeftDef()));
			gastLeftDefLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getGuestLeftDef()));
			heimRightAttLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getHomeRightAtt()));
			gastRightAttLabel.setText(PlayerAbility.getNameForSkill(true,
					details.getGuestRightAtt()));
			heimCenterAttLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getHomeMidAtt()));
			gastCenterAttLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getGuestMidAtt()));
			heimLeftAttLabel.setText(PlayerAbility.getNameForSkill(true, details.getHomeLeftAtt()));
			gastLeftAttLabel
					.setText(PlayerAbility.getNameForSkill(true, details.getGuestLeftAtt()));

			heimGesamtLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung((int) (details
					.getHomeGesamtstaerke(false) - details.getGuestGesamtstaerke(false)), true));
			gastGesamtLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung((int) (details
					.getGuestGesamtstaerke(false) - details.getHomeGesamtstaerke(false)), true));
			heimMidfieldLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getHomeMidfield() - details.getGuestMidfield(), true));
			gastMidfieldLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getGuestMidfield() - details.getHomeMidfield(), true));
			heimRightDefLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getHomeRightDef() - details.getGuestLeftAtt(), true));
			gastRightDefLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getGuestRightDef() - details.getHomeLeftAtt(), true));
			heimCenterDefLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getHomeMidDef() - details.getGuestMidAtt(), true));
			gastCenterDefLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getGuestMidDef() - details.getHomeMidAtt(), true));
			heimLeftDefLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getHomeLeftDef() - details.getGuestRightAtt(), true));
			gastLeftDefLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getGuestLeftDef() - details.getHomeRightAtt(), true));
			heimRightAttLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getHomeRightAtt() - details.getGuestLeftDef(), true));
			gastRightAttLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getGuestRightAtt() - details.getHomeLeftDef(), true));
			heimCenterAttLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getHomeMidAtt() - details.getGuestMidDef(), true));
			gastCenterAttLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getGuestMidAtt() - details.getHomeMidDef(), true));
			heimLeftAttLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getHomeLeftAtt() - details.getGuestRightDef(), true));
			gastLeftAttLabel.setIcon(ImageUtilities.getImageIcon4Veraenderung(
					details.getGuestLeftAtt() - details.getHomeRightDef(), true));
		}
	}
}
