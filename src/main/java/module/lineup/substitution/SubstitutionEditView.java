package module.lineup.substitution;

import core.datatype.CBItem;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.util.Helper;
import module.lineup.Lineup;
import module.lineup.substitution.model.GoalDiffCriteria;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.RedCardCriteria;
import module.lineup.substitution.model.Substitution;
import module.lineup.substitution.positionchooser.PositionChooser;
import module.lineup.substitution.positionchooser.PositionSelectionEvent.Change;
import module.teamAnalyzer.vo.PlayerInfo;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.*;
import static core.model.player.IMatchRoleID.aSubstitutesMatchRoleID;

/**
 * Panel displaying controls for substitution in the popup.
 */
public class SubstitutionEditView extends JPanel {

	private MatchOrderType orderType;
	private JComboBox playerComboBox;
	private JComboBox playerInComboBox;
	private JComboBox behaviourComboBox;
	private JComboBox positionComboBox;
	private JComboBox redCardsComboBox;
	private JComboBox standingComboBox;
	private PositionChooser positionChooser;
	private JSlider whenSlider;
	private WhenTextField whenTextField;

	private Lineup lineup;
	private Substitution substitution;

	private static class EffectOnRatingDisplay {
		private Integer oldValue;
		private final JLabel displayChangeField;
		private final JLabel labelField;

		public EffectOnRatingDisplay(String label){
			labelField = new JLabel(label);
			displayChangeField = new JLabel();
		}

		public void setOldValue(Integer value){
			this.oldValue = value;
			displayChangeField.setText(getChangeText(value));
		}

		public void setNewValue(Integer value){
			displayChangeField.setText(getChangeText(value));
		}
		private String getChangeText(Integer value) {
			if (oldValue == null || value == null) return "";
			return this.oldValue + "->" + value;
		}

		public JLabel getLabelField() {
			return labelField;
		}

		public JLabel getDisplayChangeField(){
			return displayChangeField;
		}
	}

	private EffectOnRatingDisplay effectOnHatstats;

	private boolean initDone=false;

	public SubstitutionEditView(MatchOrderType orderType) {
		this.orderType = orderType;
		initComponents();
		addListeners();

		Map<Integer, PlayerPositionItem> lineupPositions = SubstitutionDataProvider
				.getFieldAndSubPlayerPosition();

		this.playerComboBox.setModel(new DefaultComboBoxModel(lineupPositions
				.values().toArray()));
		this.playerComboBox.setSelectedItem(null);

		switch (this.orderType){
			case SUBSTITUTION:
				List<PlayerPositionItem> substitutionPlayers = SubstitutionDataProvider
						.getFieldPositions(aSubstitutesMatchRoleID, false);
				this.playerInComboBox.setModel(new DefaultComboBoxModel(
						substitutionPlayers.toArray()));
				this.playerInComboBox.setSelectedItem(null);
				// NO break, fall through
			case NEW_BEHAVIOUR:
				List<PlayerPositionItem> positions = SubstitutionDataProvider
						.getFieldPositions(IMatchRoleID.keeper,
								IMatchRoleID.leftForward, true);
				this.positionComboBox.setModel(new DefaultComboBoxModel(positions
						.toArray()));
				this.positionComboBox.setSelectedItem(null);
				this.positionChooser.init(lineupPositions);

				this.behaviourComboBox.setModel(new DefaultComboBoxModel(
						SubstitutionDataProvider.getBehaviourItems(this.orderType != MatchOrderType.NEW_BEHAVIOUR).toArray()));
				if (this.orderType == MatchOrderType.NEW_BEHAVIOUR) {
					this.behaviourComboBox.setSelectedItem(null);
				}
				break;

			case POSITION_SWAP:
				this.playerInComboBox.setModel(new DefaultComboBoxModel(
						lineupPositions.values().toArray()));
				this.playerInComboBox.setSelectedItem(null);
				break;

			case MAN_MARKING:
				List<PlayerInfo> opponentPlayers = SubstitutionDataProvider.getOpponentPlayers();
				this.playerInComboBox.setModel(new DefaultComboBoxModel(opponentPlayers.toArray()));
				this.playerInComboBox.setSelectedItem(null);
				break;
		}

	}

	private Integer hatstats() {
		return lineup.getRatings().getHatStats().get(-90d);	// 90 minutes average
	}

	/**
	 * Initializes the view with the given {@link Substitution}. The given
	 * object will not be changed. To retrieve the data from the view, use
	 * {@link #getSubstitution(int)} method.
	 *
	 * @param lineup
	 * @param sub
	 */
	public void init(Lineup lineup, Substitution sub) {
		this.lineup = lineup;
		this.substitution = sub;
		this.orderType = sub.getOrderType();

		this.effectOnHatstats.setOldValue(hatstats());

		if (sub.getSubjectPlayerID() != -1) {
			ComboBoxModel model = this.playerComboBox.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (((PlayerPositionItem) model.getElementAt(i)).getSpieler()
						.getPlayerID() == sub.getSubjectPlayerID()) {
					playerComboBox.setSelectedItem(model.getElementAt(i));
					break;
				}
			}
		}

		if (this.orderType!=MatchOrderType.NEW_BEHAVIOUR && sub.getObjectPlayerID() != -1) {
			ComboBoxModel model = this.playerInComboBox.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (this.orderType != MatchOrderType.MAN_MARKING) {
					if (((PlayerPositionItem) model.getElementAt(i)).getSpieler().getPlayerID() == sub.getObjectPlayerID()) {
						playerInComboBox.setSelectedItem(model.getElementAt(i));
						break;
					}
				} else {
					if (((PlayerInfo) model.getElementAt(i)).getPlayerId() == sub.getObjectPlayerID()) {
						playerInComboBox.setSelectedItem(model.getElementAt(i));
						break;
					}
				}
			}
		}

		if (this.orderType != MatchOrderType.POSITION_SWAP && this.orderType != MatchOrderType.MAN_MARKING) {
			ComboBoxModel model = this.positionComboBox.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (((PlayerPositionItem) model.getElementAt(i)).getPosition()
						.byteValue() == sub.getRoleId()) {
					positionComboBox.setSelectedItem(model.getElementAt(i));
					break;
				}
			}
		}

		if (this.orderType != MatchOrderType.MAN_MARKING) {
			Helper.setComboBoxFromID(this.behaviourComboBox, sub.getBehaviour());
			Helper.setComboBoxFromID(this.redCardsComboBox, sub.getRedCardCriteria().getId());
			Helper.setComboBoxFromID(this.standingComboBox, sub.getStanding().getId());
			this.whenTextField.setValue((int) sub.getMatchMinuteCriteria());
		}
		initDone = true;
	}

	/**
	 * Gets a new {@link Substitution} which represents the values chosen in
	 * the view. Note that <i>new</i> is returned and not the one which may be
	 * provided to the {@link #init(Lineup, Substitution)} method.
	 *
	 * @param nextOrderID
	 * @return new Substitution
	 */
	public Substitution getSubstitution(int nextOrderID) {
		if ( this.substitution == null){
			this.substitution = new Substitution(this.orderType);
		}
		this.substitution.setPlayerOrderId(nextOrderID);
		var item = (PlayerPositionItem) this.playerComboBox.getSelectedItem();
		if (item != null) {
			this.substitution.setSubjectPlayerID(item.getSpieler().getPlayerID());
		}

		if ( this.orderType != MatchOrderType.MAN_MARKING) {
			if (this.orderType == MatchOrderType.POSITION_SWAP || this.orderType == MatchOrderType.SUBSTITUTION) {
				item = (PlayerPositionItem) this.playerInComboBox.getSelectedItem();
				if (item != null) {
					this.substitution.setObjectPlayerID(item.getSpieler().getPlayerID());
				}
			} else if (this.orderType == MatchOrderType.NEW_BEHAVIOUR) {
				// the player should be both object and subject, per API.
				this.substitution.setObjectPlayerID(substitution.getSubjectPlayerID());
			}
		}
		else {
			// man marking
			var oItem = (PlayerInfo) this.playerInComboBox.getSelectedItem();
			if ( oItem != null){
				this.substitution.setObjectPlayerID(oItem.getPlayerId());
			}
		}

		if ( this.orderType != MatchOrderType.MAN_MARKING) {
			if (this.orderType != MatchOrderType.POSITION_SWAP ) {
				item = (PlayerPositionItem) this.positionComboBox.getSelectedItem();
				if (item != null) {
					this.substitution.setRoleId(item.getPosition().byteValue());
				}
			}

			this.substitution.setMatchMinuteCriteria(((Integer) this.whenTextField.getValue()).byteValue());
			this.substitution.setBehaviour((byte) getSelectedId(this.behaviourComboBox));
			this.substitution.setRedCardCriteria(RedCardCriteria.getById((byte) getSelectedId(this.redCardsComboBox)));
			this.substitution.setStanding(GoalDiffCriteria.getById((byte) getSelectedId(this.standingComboBox)));
		}
		return this.substitution;
	}

	private int getSelectedId(JComboBox comboBox) {
		CBItem item = (CBItem) comboBox.getSelectedItem();
		if (item != null) {
			return item.getId();
		}
		return -1;
	}

	public void ratingRecalc() {
		if (substitution == null || !initDone) return;
		this.substitution = getSubstitution(-1);
		if (substitution.getSubjectPlayerID() !=  -1 &&
				(this.orderType == MatchOrderType.NEW_BEHAVIOUR || substitution.getObjectPlayerID() != -1)) {
			this.lineup.setRatings();
			this.effectOnHatstats.setNewValue(hatstats());
		}
	}

	private void addListeners() {
		// ChangeListener that will updates the "when" textfield with the number
		// of minutes when slider changed
		if ( this.whenSlider != null) {
			this.whenSlider.addChangeListener(e -> whenTextField.setValue(whenSlider.getModel().getValue()));

			// PropertyChangeListener that will update the slider when value in the
			// "when" textfield changed
			this.whenTextField.addPropertyChangeListener("value",
					evt -> {
						Integer value = (Integer) whenTextField.getValue();
						whenSlider.setValue(Objects.requireNonNullElse(value, -1));
						// calculate hatstat change
						ratingRecalc();
					});
		}

		if ( this.playerComboBox != null){
			this.playerComboBox.addActionListener(e-> ratingRecalc());
		}
		if ( this.playerInComboBox != null){
			this.playerInComboBox.addActionListener(e-> ratingRecalc());
		}

		if (this.orderType != MatchOrderType.POSITION_SWAP && this.orderType != MatchOrderType.MAN_MARKING) {
			// ItemListener that will update the PositionChooser if selection in
			// the position combobox changes
			this.positionComboBox.addItemListener(e -> {
				PlayerPositionItem item = (PlayerPositionItem) positionComboBox.getSelectedItem();
				if (item != null) {
					positionChooser.select(item.getPosition());
				} else {
					positionChooser.select(null);
				}
			});

			// PositionSelectionListener that will update position combobox
			// selection if selection in the PositionChooser changes
			this.positionChooser.addPositionSelectionListener(event -> {
				if (event.getChange() == Change.SELECTED) {
					for (int i = 0; i < positionComboBox.getModel().getSize(); i++) {
						PlayerPositionItem item = (PlayerPositionItem) positionComboBox.getModel().getElementAt(i);
						if (event.getPosition().equals(item.getPosition())) {
							if (item != positionComboBox.getSelectedItem()) {
								positionComboBox.setSelectedItem(item);
							}
							break;
						}
					}
				} else {
					if (positionComboBox.getSelectedItem() != null) {
						positionComboBox.setSelectedItem(null);
					}
				}
			});
		}
	}

	private void initComponents() {
		this.effectOnHatstats = new EffectOnRatingDisplay("HatStats:");

		setLayout(new GridBagLayout());

		JLabel playerLabel = new JLabel();
		switch (this.orderType) {
			case SUBSTITUTION -> playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.Out"));
			case POSITION_SWAP -> playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.Reposition"));
			case NEW_BEHAVIOUR -> playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.Player"));
			case MAN_MARKING -> playerLabel.setText(HOVerwaltung.instance().getLanguageString("subs.manMarkingPlayer"));
		}

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(10, 10, 4, 2);
		add(playerLabel, gbc);

		this.playerComboBox = new JComboBox();
		Dimension comboBoxSize = new Dimension(200,
				this.playerComboBox.getPreferredSize().height);
		this.playerComboBox.setMinimumSize(comboBoxSize);
		this.playerComboBox.setPreferredSize(comboBoxSize);
		gbc.gridx = 1;
		gbc.insets = new Insets(10, 2, 4, 10);
		add(this.playerComboBox, gbc);

		if ( this.orderType !=  MatchOrderType.NEW_BEHAVIOUR) {
			JLabel playerInLabel = new JLabel();
			if (this.orderType == MatchOrderType.SUBSTITUTION) {
				playerInLabel.setText(HOVerwaltung.instance().getLanguageString("subs.In"));
			} else if (this.orderType == MatchOrderType.POSITION_SWAP){
				playerInLabel.setText(HOVerwaltung.instance().getLanguageString("subs.RepositionWith"));
			}
			else {
				playerInLabel.setText(HOVerwaltung.instance().getLanguageString("subs.manMarkedOpponentPlayer"));
			}
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 10, 4, 2);
			add(playerInLabel, gbc);

			this.playerInComboBox = new JComboBox();
			this.playerInComboBox.setMinimumSize(comboBoxSize);
			this.playerInComboBox.setPreferredSize(comboBoxSize);
			gbc.gridx = 1;
			gbc.insets = new Insets(4, 2, 4, 10);
			add(this.playerInComboBox, gbc);
		}

		if( this.orderType != MatchOrderType.MAN_MARKING) {
			this.behaviourComboBox = new JComboBox();
			if (this.orderType != MatchOrderType.POSITION_SWAP ) {
				JLabel behaviourLabel = new JLabel(HOVerwaltung.instance()
						.getLanguageString("subs.Behavior"));
				gbc.gridx = 0;
				gbc.gridy++;
				gbc.anchor = GridBagConstraints.WEST;
				gbc.insets = new Insets(4, 10, 4, 2);
				add(behaviourLabel, gbc);

				this.behaviourComboBox.setMinimumSize(comboBoxSize);
				this.behaviourComboBox.setPreferredSize(comboBoxSize);
				gbc.gridx = 1;
				gbc.insets = new Insets(4, 2, 4, 10);
				add(this.behaviourComboBox, gbc);
			}
		}

		if ( this.orderType != MatchOrderType.MAN_MARKING) {
			JLabel whenLabel = new JLabel(HOVerwaltung.instance()
					.getLanguageString("subs.When"));
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.insets = new Insets(4, 10, 4, 2);
			add(whenLabel, gbc);

			this.whenTextField = new WhenTextField(HOVerwaltung.instance()
					.getLanguageString("subs.MinuteAnytime"), HOVerwaltung
					.instance().getLanguageString("subs.MinuteAfterX"));
			Dimension textFieldSize = new Dimension(200,
					this.whenTextField.getPreferredSize().height);
			this.whenTextField.setMinimumSize(textFieldSize);
			this.whenTextField.setPreferredSize(textFieldSize);
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 1;
			gbc.insets = new Insets(4, 2, 4, 10);
			add(this.whenTextField, gbc);

			this.whenSlider = new JSlider(-1, 119, -1);
			this.whenSlider.setMinimumSize(new Dimension(this.whenTextField
					.getMinimumSize().width,
					this.whenSlider.getPreferredSize().height));
			gbc.gridx = 1;
			gbc.gridy++;
			gbc.insets = new Insets(0, 2, 8, 10);
			add(this.whenSlider, gbc);

			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(8, 4, 8, 4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			add(new Divider(HOVerwaltung.instance().getLanguageString(
					"subs.AdvancedConditions")), gbc);

			gbc.gridwidth = 1;
			gbc.weightx = 0.0;

			if (this.orderType != MatchOrderType.POSITION_SWAP) {
				JLabel positionLabel = new JLabel(HOVerwaltung.instance()
						.getLanguageString("subs.Position"));
				gbc.gridx = 0;
				gbc.gridy++;
				gbc.insets = new Insets(4, 10, 4, 2);
				add(positionLabel, gbc);

				this.positionComboBox = new JComboBox();
				this.positionComboBox.setMinimumSize(comboBoxSize);
				this.positionComboBox.setPreferredSize(comboBoxSize);
				gbc.gridx = 1;
				gbc.insets = new Insets(4, 2, 4, 10);
				add(this.positionComboBox, gbc);

				this.positionChooser = new PositionChooser();
				gbc.gridy++;
				gbc.insets = new Insets(2, 10, 8, 10);
				gbc.fill = GridBagConstraints.NONE;
				add(this.positionChooser, gbc);
			}

			JLabel redCardsLabel = new JLabel(HOVerwaltung.instance()
					.getLanguageString("subs.RedCard"));
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.insets = new Insets(4, 10, 4, 2);
			add(redCardsLabel, gbc);

			this.redCardsComboBox = new JComboBox(
					SubstitutionDataProvider.getRedCardItems());
			this.redCardsComboBox.setMinimumSize(comboBoxSize);
			this.redCardsComboBox.setPreferredSize(comboBoxSize);
			gbc.gridx = 1;
			gbc.insets = new Insets(4, 2, 4, 10);
			add(this.redCardsComboBox, gbc);

			JLabel standingLabel = new JLabel(HOVerwaltung.instance()
					.getLanguageString("subs.Standing"));
			gbc.gridx = 0;
			gbc.gridy++;
			gbc.insets = new Insets(4, 10, 4, 2);
			add(standingLabel, gbc);

			this.standingComboBox = new JComboBox(
					SubstitutionDataProvider.getStandingItems());
			this.standingComboBox.setMinimumSize(comboBoxSize);
			this.standingComboBox.setPreferredSize(comboBoxSize);
			gbc.gridx = 1;
			gbc.insets = new Insets(4, 2, 4, 10);
			add(this.standingComboBox, gbc);
		}

		JLabel hatstatsLabel = this.effectOnHatstats.getLabelField();
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(4, 10, 4, 2);
		add(hatstatsLabel, gbc);

		var hatstatsChangeField = this.effectOnHatstats.getDisplayChangeField();
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 4, 10);
		add(hatstatsChangeField, gbc);

		// dummy to consume all extra space
		gbc.gridy++;
		gbc.weighty = 1.0;
		add(new JPanel(), gbc);
	}
}
