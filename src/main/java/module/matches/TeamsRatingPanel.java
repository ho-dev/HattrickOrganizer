package module.matches;

import core.constants.player.PlayerAbility;
import core.gui.comp.CustomProgressBar;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Panel showing detailed team ratings of selected match
 */
class TeamsRatingPanel extends LazyImagePanel {

	private final int RATING_BAR_WIDTH = 18 * UserParameter.instance().fontSize;
	private final int RATING_BAR_HEIGHT = 4 * UserParameter.instance().fontSize;
	private final int INSET = UserParameter.instance().fontSize;
	private final int LARGE_RATING_BAR_HEIGHT = 2 * (RATING_BAR_HEIGHT + INSET);

    private final MatchesModel matchesModel;

    private record ProgressBarPair(JProgressBar progressBarHome, JProgressBar progressBarGuest) {
    }

	private JLabel m_jlGuestTeamName;
	private JLabel m_jlHomeTeamName;
	private CustomProgressBar homeLeftDefenseVsGuestRightAttack;
    private CustomProgressBar homeCentralDefenseVsGuestCentralAttack;
    private CustomProgressBar homeRightDefenseVsGuestLeftAttack;
    private CustomProgressBar homeMidfieldVsGuestMidfield;
    private CustomProgressBar homeLeftAttackVsGuestRightDefense;
    private CustomProgressBar homeCentralAttackVsGuestCentralDefense;
    private CustomProgressBar homeRightAttackVsGuestLeftDefense;
	private ArrayList<ProgressBarPair> barPairs;
	private JPanel m_jpBottom;
	private GridBagConstraints m_jgbcBottom;
	private Font generalFont;

    @Getter
    @RequiredArgsConstructor
    private enum RatingSector {
        MIDFIELD(0, "ls.match.ratingsector.midfield", Matchdetails::getHomeMidfield, Matchdetails::getGuestMidfield, Matchdetails::getGuestMidfield),
        RIGHT_DEFENSE(1, "ls.match.ratingsector.rightdefence", Matchdetails::getHomeRightDef, Matchdetails::getGuestRightDef, Matchdetails::getGuestLeftAtt),
        CENTRAL_DEFENSE(2, "ls.match.ratingsector.centraldefence", Matchdetails::getHomeMidDef, Matchdetails::getGuestMidDef, Matchdetails::getGuestMidAtt),
        LEFT_DEFENSE(3, "ls.match.ratingsector.leftdefence", Matchdetails::getHomeLeftDef, Matchdetails::getGuestLeftDef, Matchdetails::getGuestRightAtt),
        RIGHT_ATTACK(4, "ls.match.ratingsector.rightattack", Matchdetails::getHomeRightAtt, Matchdetails::getGuestRightAtt, Matchdetails::getGuestLeftDef),
        CENTRAL_ATTACK(5, "ls.match.ratingsector.centralattack", Matchdetails::getHomeMidAtt, Matchdetails::getGuestMidAtt, Matchdetails::getGuestMidDef),
        LEFT_ATTACK(6, "ls.match.ratingsector.leftattack", Matchdetails::getHomeLeftAtt, Matchdetails::getGuestLeftAtt, Matchdetails::getGuestRightDef);

        private final int rowIndex;
        private final String labelTranslationKey;
        private final Function<Matchdetails, Integer> homeTeamFunction;
        private final Function<Matchdetails, Integer> guestTeamFunction;
        private final Function<Matchdetails, Integer> versusGuestTeamFunction;

        public int getRowNumber() {
            return getRowIndex() + 1;
        }

        public int getHomeTeamValue(Matchdetails matchdetails) {
            return getHomeTeamFunction().apply(matchdetails);
        }

        public int getGuestTeamValue(Matchdetails matchdetails) {
            return getGuestTeamFunction().apply(matchdetails);
        }

        public int versusGuestTeamValue(Matchdetails matchdetails) {
            return getVersusGuestTeamFunction().apply(matchdetails);
        }
    }

	TeamsRatingPanel(MatchesModel matchesModel) {
		this(matchesModel, false);
	}

	TeamsRatingPanel(MatchesModel matchesModel, boolean print) {
		super(print);
		this.matchesModel = matchesModel;
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		MatchKurzInfo matchInfo = matchesModel.getMatch();

		if ((matchInfo == null) || matchInfo.getMatchStatus() != MatchKurzInfo.FINISHED) {
			clear();
			return;
		}

		Matchdetails matchdetails = matchesModel.getDetails();

		if (matchdetails.getHomeHatStats() == 0) {
			clear();
			return;
		}

		m_jlHomeTeamName.setText(matchdetails.getHomeTeamName());
		m_jlGuestTeamName.setText(matchdetails.getGuestTeamName());
        setValue(homeRightDefenseVsGuestLeftAttack, RatingSector.RIGHT_DEFENSE, matchdetails);
		setValue(homeCentralDefenseVsGuestCentralAttack, RatingSector.CENTRAL_DEFENSE, matchdetails);
        setValue(homeLeftDefenseVsGuestRightAttack, RatingSector.LEFT_DEFENSE, matchdetails);
        setValue(homeMidfieldVsGuestMidfield, RatingSector.MIDFIELD, matchdetails);
        setValue(homeRightAttackVsGuestLeftDefense, RatingSector.RIGHT_ATTACK, matchdetails);
        setValue(homeCentralAttackVsGuestCentralDefense, RatingSector.CENTRAL_ATTACK, matchdetails);
        setValue(homeLeftAttackVsGuestRightDefense, RatingSector.LEFT_ATTACK, matchdetails);
        Stream.of(RatingSector.values()).forEach(ratingSector -> setBarsValue(ratingSector, matchdetails));
	}

	private void addListeners() {
		this.matchesModel.addMatchModelChangeListener(() -> setNeedsRefresh(true));
	}

	private void initComponents() {
		final var fontSize = UserParameter.instance().fontSize;

		generalFont = new JLabel("").getFont();
		generalFont = generalFont.deriveFont(generalFont.getStyle() | Font.BOLD);

        barPairs = new ArrayList<>();
        Stream.of(RatingSector.values()).forEach(ratingSector ->
            barPairs.add(new ProgressBarPair(createProgressBar(fontSize), createProgressBar(fontSize))));

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints gbc = new GridBagConstraints();

		setLayout(layout);

		homeLeftDefenseVsGuestRightAttack = createRatingBar();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, INSET, 0, 0);
		add(homeLeftDefenseVsGuestRightAttack, gbc);

		homeCentralDefenseVsGuestCentralAttack = createRatingBar();
        gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(INSET, INSET, 0, 0);
		add(homeCentralDefenseVsGuestCentralAttack, gbc);

		homeRightDefenseVsGuestLeftAttack = createRatingBar();
        gbc.gridx = 0;
		gbc.gridy = 2;
		add(homeRightDefenseVsGuestLeftAttack, gbc);

		homeMidfieldVsGuestMidfield = createRatingBar(true);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 3;
		gbc.insets = new Insets(0, INSET, 0, INSET);
		add(homeMidfieldVsGuestMidfield, gbc);

		homeLeftAttackVsGuestRightDefense = createRatingBar();
		gbc.gridx = 2;
        gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 0, 0, INSET);
		add(homeLeftAttackVsGuestRightDefense, gbc);

		homeCentralAttackVsGuestCentralDefense = createRatingBar();
        gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.insets = new Insets(INSET, 0, 0, INSET);
		add(homeCentralAttackVsGuestCentralDefense, gbc);

		homeRightAttackVsGuestLeftDefense = createRatingBar();
        gbc.gridx = 2;
		gbc.gridy = 2;
		add(homeRightAttackVsGuestLeftDefense, gbc);

		m_jpBottom = new JPanel(new GridBagLayout());
		m_jgbcBottom = new GridBagConstraints();

		m_jpBottom.setBorder(BorderFactory.createLineBorder(ThemeManager
				.getColor(HOColorName.PANEL_BORDER)));

		m_jgbcBottom.insets = new Insets(8, 8, 0, 8);

		m_jgbcBottom.gridx = 0;
		m_jgbcBottom.gridy = 0;
		m_jpBottom.add(new JLabel(""), m_jgbcBottom);

		m_jlHomeTeamName = new JLabel("");
		m_jlHomeTeamName.setFont(generalFont);
		m_jgbcBottom.gridx = 1;
		m_jpBottom.add(m_jlHomeTeamName, m_jgbcBottom);


		m_jlGuestTeamName = new JLabel("");
		m_jlGuestTeamName.setFont(generalFont);
		m_jgbcBottom.gridx = 2;
		m_jpBottom.add(m_jlGuestTeamName, m_jgbcBottom);

        Stream.of(RatingSector.values()).forEach(this::addRow);
		m_jgbcBottom.insets = new Insets(8, 8, 8, 8);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(m_jpBottom, gbc);
	}

    private static JProgressBar createProgressBar(int fontSize) {
        var progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(20 * fontSize, 2 * fontSize));
        progressBar.setStringPainted(true);
        return progressBar;
    }

	private CustomProgressBar createRatingBar() {
		return createRatingBar(false);
	}

	private CustomProgressBar createRatingBar(boolean isLarge) {
		int iHeight = isLarge ? LARGE_RATING_BAR_HEIGHT : RATING_BAR_HEIGHT;
		return new CustomProgressBar(ThemeManager.getColor(HOColorName.GUEST_ACTION), ThemeManager.getColor(HOColorName.HOME_ACTION),
				ThemeManager.getColor(HOColorName.BORDER_RATING_BAR), RATING_BAR_WIDTH, iHeight, generalFont);
	}

	private void addRow(RatingSector ratingSector) {
        final String text = TranslationFacility.tr(ratingSector.getLabelTranslationKey());
        final int row = ratingSector.getRowNumber();
		JLabel label = new JLabel(text);
		label.setFont(generalFont);
		add(label, 0, row);
        var barPair = barPairs.get(ratingSector.getRowIndex());
        add(barPair.progressBarHome(), 1, row);
        add(barPair.progressBarGuest(), 2, row);
	}

	private void add(JComponent comp, int x, int y) {
		m_jgbcBottom.gridx = x;
		m_jgbcBottom.gridy = y;
		m_jgbcBottom.anchor = GridBagConstraints.WEST;
		m_jgbcBottom.fill = GridBagConstraints.HORIZONTAL;
		m_jpBottom.add(comp, m_jgbcBottom);
	}

	private void clear() {
		resetValue(homeLeftDefenseVsGuestRightAttack);
		resetValue(homeCentralDefenseVsGuestCentralAttack);
		resetValue(homeRightDefenseVsGuestLeftAttack);
		resetValue(homeMidfieldVsGuestMidfield);
		resetValue(homeLeftAttackVsGuestRightDefense);
		resetValue(homeCentralAttackVsGuestCentralDefense);
		resetValue(homeRightAttackVsGuestLeftDefense);

        barPairs.forEach(barPair -> {
            resetProgressBar(barPair.progressBarHome());
            resetProgressBar(barPair.progressBarGuest());
        } );

		m_jlGuestTeamName.setText("");
		m_jlHomeTeamName.setText("");
	}

    private static void resetProgressBar(JProgressBar progressBar) {
        progressBar.setValue(0);
        progressBar.setString("");
    }

	private void setBarsValue(RatingSector ratingSector, Matchdetails matchdetails) {
        var barPar = barPairs.get(ratingSector.getRowIndex());
        setBarValue(barPar.progressBarHome(), ratingSector.getHomeTeamValue(matchdetails));
        setBarValue(barPar.progressBarGuest(), ratingSector.getGuestTeamValue(matchdetails));
	}

    private static void setBarValue(JProgressBar progressBar, int value) {
        double htValue = calcHtValue(value);
        double RATING_MAX = 21d;
        progressBar.setValue((int) (htValue * 100 / RATING_MAX));
        progressBar.setString(PlayerAbility.getNameForSkill(true, htValue));
    }

	private void setValue(CustomProgressBar customProgressBar, RatingSector ratingSector, Matchdetails matchdetails) {
        final int homeTeamValue = ratingSector.getHomeTeamValue(matchdetails);
        final int versusGuestTeamValue = ratingSector.versusGuestTeamValue(matchdetails);
		customProgressBar.setBackground(ThemeManager.getColor(HOColorName.GUEST_ACTION));
		// minimum width of the rating bar to ensure proper visibility in case of extreme ratings ratio
		double MIN_WIDTH_BAR = 0.2;
		customProgressBar.setValue(homeTeamValue, versusGuestTeamValue, MIN_WIDTH_BAR);
		double htValHome = calcHtValue(homeTeamValue);
		double htValGuest = calcHtValue(versusGuestTeamValue);
		customProgressBar.setToolTipText(String.format(TranslationFacility.tr("ls.module.matches.ratingRatio"), htValHome, htValGuest));
	}

    private static double calcHtValue(int value) {
        return 1 + (value - 1d) / 4;
    }

	private static void resetValue(CustomProgressBar rating) {
		rating.setBackground(ThemeManager.getColor(HOColorName.NEUTRAL_ACTION));
		rating.resetValue();
	}
}
