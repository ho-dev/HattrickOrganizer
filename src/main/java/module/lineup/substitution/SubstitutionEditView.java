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
import module.lineup.substitution.positionchooser.PositionSelectionEvent;
import module.lineup.substitution.positionchooser.PositionSelectionListener;
import module.lineup.substitution.positionchooser.PositionSelectionEvent.Change;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static core.model.player.IMatchRoleID.aSubstitutesMatchRoleID;

public class SubstitutionEditView extends JPanel {

	private static final long serialVersionUID = 6041242290064429972L;
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
	private Integer oldHatStats;
	private JLabel hatstatsChangeField;
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

		if (isSubstitution()) {
			List<PlayerPositionItem> substitutionPlayers = SubstitutionDataProvider
					.getFieldPositions(aSubstitutesMatchRoleID, false);
			this.playerInComboBox.setModel(new DefaultComboBoxModel(
					substitutionPlayers.toArray()));
			this.playerInComboBox.setSelectedItem(null);
		} else if (isPositionSwap()) {
			this.playerInComboBox.setModel(new DefaultComboBoxModel(
					lineupPositions.values().toArray()));
			this.playerInComboBox.setSelectedItem(null);
		}

		if (!isPositionSwap()) {
			List<PlayerPositionItem> positions = SubstitutionDataProvider
					.getFieldPositions(IMatchRoleID.keeper,
							IMatchRoleID.leftForward, true);
			this.positionComboBox.setModel(new DefaultComboBoxModel(positions
					.toArray()));
			this.positionComboBox.setSelectedItem(null);
			this.positionChooser.init(lineupPositions);

			this.behaviourComboBox.setModel(new DefaultComboBoxModel(
					SubstitutionDataProvider.getBehaviourItems(
							!isNewBehaviour()).toArray()));
		}

		if (isNewBehaviour()) {
			this.behaviourComboBox.setSelectedItem(null);
		}
	}

	private Integer Hatstats()
	{
		return  lineup.getRatings().getHatStats().get(-90d);	// 90 minutes average
	}

	/**
	 * Initializes the view with the given {@link Substitution}. The given
	 * object will not be changed. To retrieve the data from the view, use
	 * {@link #getSubstitution()} method.
	 *
	 * @param lineup
	 * @param sub
	 */
	public void init(Lineup lineup, Substitution sub) {
		this.lineup = lineup;
		this.substitution  = sub;
		this.oldHatStats = Hatstats();
		this.orderType = sub.getOrderType();

		if (sub.getSubjectPlayerID() != -1) {
			ComboBoxModel model = this.playerComboBox.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (((PlayerPositionItem) model.getElementAt(i)).getSpieler()
						.getSpielerID() == sub.getSubjectPlayerID()) {
					playerComboBox.setSelectedItem(model.getElementAt(i));
					break;
				}
			}
		}

		if (!isNewBehaviour() && sub.getObjectPlayerID() != -1) {
			ComboBoxModel model = this.playerInComboBox.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (((PlayerPositionItem) model.getElementAt(i)).getSpieler()
						.getSpielerID() == sub.getObjectPlayerID()) {
					playerInComboBox.setSelectedItem(model.getElementAt(i));
					break;
				}
			}
		}

		if (!isPositionSwap()) {
			ComboBoxModel model = this.positionComboBox.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (((PlayerPositionItem) model.getElementAt(i)).getPosition()
						.byteValue() == sub.getRoleId()) {
					positionComboBox.setSelectedItem(model.getElementAt(i));
					break;
				}
			}
		}

		Helper.markierenComboBox(this.behaviourComboBox, sub.getBehaviour());
		Helper.markierenComboBox(this.redCardsComboBox, sub
				.getRedCardCriteria().getId());
		Helper.markierenComboBox(this.standingComboBox, sub.getStanding()
				.getId());
		this.whenTextField.setValue(Integer.valueOf(sub
				.getMatchMinuteCriteria()));

		this.hatstatsChangeField.setText(HatStatsChange());
		initDone=true;
	}

	/**
	 * Gets a new {@link ISubstitution} which represents the values chosen in
	 * the view. Note that <i>new</i> is returned and not the one which may be
	 * provided to the {@link #init(ISubstitution)} method.
	 * 
	 * @return
	 */
	public Substitution getSubstitution() {
		Substitution sub = new Substitution();
		return getSubstitution(sub);
	}

	public Substitution getSubstitution(Substitution sub)
	{
		sub.setBehaviour((byte) getSelectedId(this.behaviourComboBox));
		sub.setRedCardCriteria(RedCardCriteria
				.getById((byte) getSelectedId(this.redCardsComboBox)));
		sub.setStanding(GoalDiffCriteria
				.getById((byte) getSelectedId(this.standingComboBox)));
		sub.setMatchMinuteCriteria(((Integer) this.whenTextField.getValue())
				.byteValue());
		sub.setOrderType(this.orderType);
		PlayerPositionItem item = (PlayerPositionItem) this.playerComboBox
				.getSelectedItem();
		if (item != null) {
			sub.setObjectPlayerID(item.getSpieler().getSpielerID());
		}

		// sub.setPlayerOrderId(id); ???????????
		if (!isPositionSwap()) {
			item = (PlayerPositionItem) this.positionComboBox.getSelectedItem();
			if (item != null) {
				if (item.getSpieler() != null) {
					sub.setRoleId(item.getPosition().byteValue());
				}
				sub.setRoleId(item.getPosition().byteValue());
			}
		}

		item = (PlayerPositionItem) this.playerComboBox.getSelectedItem();
		if (item != null) {
			sub.setSubjectPlayerID(item.getSpieler().getSpielerID());
		}
		if (isPositionSwap() || isSubstitution()) {
			item = (PlayerPositionItem) this.playerInComboBox.getSelectedItem();
			if (item != null) {
				sub.setObjectPlayerID(item.getSpieler().getSpielerID());
			}
		} else if (isNewBehaviour()) {
			// the player should be both object and subject, per API.
			sub.setObjectPlayerID(sub.getSubjectPlayerID());
		}
		return sub;
	}

	private int getSelectedId(JComboBox comboBox) {
		CBItem item = (CBItem) comboBox.getSelectedItem();
		if (item != null) {
			return item.getId();
		}
		return -1;
	}

	void RatingRecalc()
	{
		if ( substitution == null || initDone == false) return;
		setSubstitution();
		if ( substitution.getSubjectPlayerID() !=  -1 &&
				(isNewBehaviour() || substitution.getObjectPlayerID() != -1)){
			this.lineup.setRatings();
			this.hatstatsChangeField.setText(HatStatsChange());
		}
	}

	private void addListeners() {
		// ChangeListener that will updates the "when" textfield with the number
		// of minutes when slider changed
		this.whenSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				whenTextField.setValue(Integer.valueOf(whenSlider.getModel().getValue()));
			}
		});

		// PropertyChangeListener that will update the slider when value in the
		// "when" textfield changed
		this.whenTextField.addPropertyChangeListener("value",
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						Integer value = (Integer) whenTextField.getValue();
						if (value != null) {
							whenSlider.setValue(value.intValue());
						} else {
							whenSlider.setValue(-1);
						}
						RatingRecalc();
					}
				});


		ItemListener ratingRecalcListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				RatingRecalc();
			}
		};

		this.playerComboBox.addItemListener( ratingRecalcListener);
		if (this.playerInComboBox != null )  this.playerInComboBox.addItemListener(ratingRecalcListener);
		if (this.positionComboBox != null )  this.positionComboBox.addItemListener(ratingRecalcListener);
		this.behaviourComboBox.addItemListener(ratingRecalcListener);

		if (!isPositionSwap()) {
			// ItemListener that will update the PositionChooser if selection in
			// the position combobox changes
			this.positionComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					PlayerPositionItem item = (PlayerPositionItem) positionComboBox
							.getSelectedItem();
					if (item != null) {
						positionChooser.select(Integer.valueOf(item
								.getPosition()));
					} else {
						positionChooser.select(null);
					}
				}
			});

			// PositionSelectionListener that will update position combobox
			// selection if selection in the PositionChooser changes
			this.positionChooser
					.addPositionSelectionListener(new PositionSelectionListener() {

						@Override
						public void selectionChanged(
								PositionSelectionEvent event) {
							if (event.getChange() == Change.SELECTED) {
								for (int i = 0; i < positionComboBox.getModel()
										.getSize(); i++) {
									PlayerPositionItem item = (PlayerPositionItem) positionComboBox
											.getModel().getElementAt(i);
									if (event.getPosition().equals(
											item.getPosition())) {
										if (item != positionComboBox
												.getSelectedItem()) {
											positionComboBox
													.setSelectedItem(item);
										}
										break;
									}
								}
							} else {
								if (positionComboBox.getSelectedItem() != null) {
									positionComboBox.setSelectedItem(null);
								}
							}
						}
					});
		}
	}

	private void initComponents() {
		setLayout(new GridBagLayout());

		JLabel playerLabel = new JLabel();
		if (isSubstitution()) {
			playerLabel.setText(HOVerwaltung.instance().getLanguageString(
					"subs.Out"));
		} else if (isPositionSwap()) {
			playerLabel.setText(HOVerwaltung.instance().getLanguageString(
					"subs.Reposition"));
		} else {
			playerLabel.setText(HOVerwaltung.instance().getLanguageString(
					"subs.Player"));
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

		if (isSubstitution() || isPositionSwap()) {
			JLabel playerInLabel = new JLabel();
			if (isSubstitution()) {
				playerInLabel.setText(HOVerwaltung.instance()
						.getLanguageString("subs.In"));
			} else {
				playerInLabel.setText(HOVerwaltung.instance()
						.getLanguageString("subs.RepositionWith"));
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

		this.behaviourComboBox = new JComboBox();
		if (!isPositionSwap()) {
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

		if (!isPositionSwap()) {
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

		JLabel hatstatsLabel = new JLabel("HatStats:");
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(4, 10, 4, 2);
		add(hatstatsLabel, gbc);

		this.hatstatsChangeField = new JLabel(HatStatsChange());
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 4, 10);
		add(this.hatstatsChangeField, gbc);

		// dummy to consume all extra space
		gbc.gridy++;
		gbc.weighty = 1.0;
		add(new JPanel(), gbc);
	}

	private String HatStatsChange() {
		if (oldHatStats == null) return "";
		return this.oldHatStats + "->" + Hatstats();
	}

	private boolean isSubstitution() {
		return this.orderType == MatchOrderType.SUBSTITUTION;
	}

	private boolean isPositionSwap() {
		return this.orderType == MatchOrderType.POSITION_SWAP;
	}

	private boolean isNewBehaviour() {
		return this.orderType == MatchOrderType.NEW_BEHAVIOUR;
	}

	public void setSubstitution() {
		getSubstitution(this.substitution);
	}
}
