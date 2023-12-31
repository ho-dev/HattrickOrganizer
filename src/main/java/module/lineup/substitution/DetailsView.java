package module.lineup.substitution;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.Helper;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DetailsView extends JPanel {

	private static final Dimension COMPONENTENSIZE = new Dimension(Helper.calcCellWidth(300),
			Helper.calcCellWidth(18));
	private static final long serialVersionUID = -8046083206070885556L;
	private Substitution substitution;
	private JLabel orderTypeEntry;
	private JLabel firstPlayerEntry;
	private JLabel secondPlayerEntry;
	private JLabel whenEntry;
	private JLabel newBehaviourEntry;
	private JLabel newPositionEntry;
	private JLabel standingEntry;
	private JLabel redCardsEntry;
	private JLabel playerLabel;
	private JLabel playerInLabel;

	public DetailsView() {
		initComponents();
	}

	public void setSubstitution(Substitution sub) {
		this.substitution = sub;
		refresh();
	}

	public void refresh() {
		updateData();
		updateView();
	}

	private void updateView() {
		if (this.substitution != null) {
			HOColorName color = (this.substitution.getSubjectPlayerID() != -1) ? HOColorName.SUBST_CHANGED_VALUE_BG
					: HOColorName.TABLEENTRY_BG;
			this.firstPlayerEntry.setBackground(ThemeManager.getColor(color));

			color = (this.substitution.getObjectPlayerID() != -1 && this.substitution
					.getOrderType() != MatchOrderType.NEW_BEHAVIOUR) ? HOColorName.SUBST_CHANGED_VALUE_BG
					: HOColorName.TABLEENTRY_BG;
			this.secondPlayerEntry.setBackground(ThemeManager.getColor(color));

			color = (this.substitution.getMatchMinuteCriteria() > 0) ? HOColorName.SUBST_CHANGED_VALUE_BG
					: HOColorName.TABLEENTRY_BG;
			this.whenEntry.setBackground(ThemeManager.getColor(color));

			color = (this.substitution.getRoleId() != -1) ? HOColorName.SUBST_CHANGED_VALUE_BG
					: HOColorName.TABLEENTRY_BG;
			this.newPositionEntry.setBackground(ThemeManager.getColor(color));

			color = (this.substitution.getBehaviour() != -1) ? HOColorName.SUBST_CHANGED_VALUE_BG
					: HOColorName.TABLEENTRY_BG;
			this.newBehaviourEntry.setBackground(ThemeManager.getColor(color));

			color = (this.substitution.getRedCardCriteria() != RedCardCriteria.IGNORE) ? HOColorName.SUBST_CHANGED_VALUE_BG
					: HOColorName.TABLEENTRY_BG;
			this.redCardsEntry.setBackground(ThemeManager.getColor(color));

			color = (this.substitution.getStanding() != GoalDiffCriteria.ANY_STANDING) ? HOColorName.SUBST_CHANGED_VALUE_BG
					: HOColorName.TABLEENTRY_BG;
			this.standingEntry.setBackground(ThemeManager.getColor(color));

			switch (this.substitution.getOrderType()) {
				case SUBSTITUTION -> {
					this.playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.Out"));
					this.playerInLabel.setText(HOVerwaltung.instance().getLanguageString("subs.In"));
				}
				case NEW_BEHAVIOUR -> {
					this.playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.Player"));
					this.playerInLabel.setText("");
				}
				case POSITION_SWAP -> {
					this.playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.Reposition"));
					this.playerInLabel.setText(HOVerwaltung.instance().getLanguageString("subs.RepositionWith"));
				}
				case MAN_MARKING -> {
					this.playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.manMarkingPlayer"));
					this.playerInLabel.setText(HOVerwaltung.instance().getLanguageString("subs.manMarkedOpponentPlayer"));
				}
			}
		} else {
			this.playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.Out"));
			this.playerInLabel.setText(HOVerwaltung.instance().getLanguageString("subs.In"));

			Color color = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
			this.firstPlayerEntry.setBackground(color);
			this.secondPlayerEntry.setBackground(color);
			this.whenEntry.setBackground(color);
			this.newPositionEntry.setBackground(color);
			this.newBehaviourEntry.setBackground(color);
			this.redCardsEntry.setBackground(color);
			this.standingEntry.setBackground(color);
		}
	}

	private void updateData() {
		String orderType = "";
		String playerIn = "";
		String playerOut = "";
		String when = "";
		String newBehaviour = "";
		String newPosition = "";
		String standing = "";
		String redCards = "";

		if (this.substitution != null) {
			HOModel hoModel = HOVerwaltung.instance().getModel();
			orderType = LanguageStringLookup.getOrderType(this.substitution.getOrderType());

			playerOut = this.substitution.getSubjectPlayerName();
			playerIn = this.substitution.getObjectPlayerName();

			if (this.substitution.getMatchMinuteCriteria() > 0) {
				when = MessageFormat.format(
						HOVerwaltung.instance().getLanguageString("subs.MinuteAfterX"),
						(int) this.substitution.getMatchMinuteCriteria());
			} else {
				when = HOVerwaltung.instance().getLanguageString("subs.MinuteAnytime");
			}

			if (this.substitution.getRoleId() != -1) {
				newPosition = LanguageStringLookup.getPosition(this.substitution.getRoleId());
			}

			newBehaviour = LanguageStringLookup.getBehaviour(this.substitution.getBehaviour());
			redCards = LanguageStringLookup.getRedCard(this.substitution.getRedCardCriteria());
			standing = LanguageStringLookup.getStanding(this.substitution.getStanding());
		}
		this.orderTypeEntry.setText(orderType);
		this.firstPlayerEntry.setText(playerOut);
		this.secondPlayerEntry.setText(playerIn);
		this.whenEntry.setText(when);
		this.newBehaviourEntry.setText(newBehaviour);
		this.newPositionEntry.setText(newPosition);
		this.redCardsEntry.setText(redCards);
		this.standingEntry.setText(standing);
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Order type
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel(HOVerwaltung.instance().getLanguageString("subs.Order")), gbc);

		this.orderTypeEntry = createValueLabel();
		gbc.gridx = 1;
		gbc.insets = new Insets(10, 2, 2, 10);
		add(this.orderTypeEntry, gbc);

		// Player (Out/Reposition)
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 2);
		this.playerLabel = new JLabel(HOVerwaltung.instance().getLanguageString("subs.Out"));
		add(this.playerLabel, gbc);

		this.firstPlayerEntry = createValueLabel();
		gbc.gridx = 1;
		gbc.insets = new Insets(2, 2, 2, 10);
		add(this.firstPlayerEntry, gbc);

		// Player (In/With)
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 2);
		this.playerInLabel = new JLabel(HOVerwaltung.instance().getLanguageString("subs.In"));
		add(this.playerInLabel, gbc);

		this.secondPlayerEntry = createValueLabel();
		gbc.gridx = 1;
		gbc.insets = new Insets(2, 2, 2, 10);
		add(this.secondPlayerEntry, gbc);

		// When
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 2);
		add(new JLabel(HOVerwaltung.instance().getLanguageString("subs.When")), gbc);

		this.whenEntry = createValueLabel();
		gbc.gridx = 1;
		gbc.insets = new Insets(2, 2, 2, 10);
		add(this.whenEntry, gbc);

		// New behaviour
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 2);
		add(new JLabel(HOVerwaltung.instance().getLanguageString("subs.Behavior")), gbc);

		this.newBehaviourEntry = createValueLabel();
		gbc.gridx = 1;
		gbc.insets = new Insets(2, 2, 2, 10);
		add(this.newBehaviourEntry, gbc);

		// New position
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 2);
		add(new JLabel(HOVerwaltung.instance().getLanguageString("subs.Position")), gbc);

		this.newPositionEntry = createValueLabel();
		gbc.gridx = 1;
		gbc.insets = new Insets(2, 2, 2, 10);
		add(this.newPositionEntry, gbc);

		// Red Cards
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 2);
		add(new JLabel(HOVerwaltung.instance().getLanguageString("subs.RedCard")), gbc);

		this.redCardsEntry = createValueLabel();
		gbc.gridx = 1;
		gbc.insets = new Insets(2, 2, 2, 10);
		add(this.redCardsEntry, gbc);

		// Standing
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(2, 10, 2, 2);
		add(new JLabel(HOVerwaltung.instance().getLanguageString("subs.Standing")), gbc);

		this.standingEntry = createValueLabel();
		gbc.gridx = 1;
		gbc.insets = new Insets(2, 2, 2, 10);
		add(this.standingEntry, gbc);

		// dummy label to consume all extra space
		JLabel dummy = new JLabel("");
		gbc.gridx = 2;
		gbc.gridy++;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(dummy, gbc);
	}

	private JLabel createValueLabel() {
		JLabel label = new JLabel("", SwingConstants.LEFT);
		label.setForeground(ColorLabelEntry.FG_STANDARD);
		label.setBackground(ColorLabelEntry.BG_STANDARD);
		label.setOpaque(true);
		label.setPreferredSize(COMPONENTENSIZE);
		label.setMinimumSize(COMPONENTENSIZE);
		return label;
	}
}
