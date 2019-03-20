package module.specialEvents;

import core.datatype.CBItem;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.player.Player;
import module.specialEvents.filter.Filter;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class FilterPanel extends JPanel {

	private static final long serialVersionUID = 6299290138063653349L;
	// matches
	private JCheckBox onlySEMatchesCheckBox;
	private JComboBox seasonComboBox;
	private JComboBox tacticComboBox;
	// match types
	private JCheckBox friendliesCheckBox;
	private JCheckBox leagueCheckBox;
	private JCheckBox relegationCheckBox;
	private JCheckBox tournamentCheckBox;
	private JCheckBox cupCheckBox;
	private JCheckBox mastersCheckBox;
	// SE types
	private JCheckBox specialitySECheckBox;
	private JCheckBox weatherSECheckBox;
	private JCheckBox counterAttackSECheckBox;
	private JCheckBox freeKickSECheckBox;
	private JCheckBox freeKickIndirectSECheckBox;
	private JCheckBox penaltySECheckBox;
	private JCheckBox longshotSECheckBox;
	// player
	private JComboBox playerComboBox;
	private JCheckBox currentOwnPlayersCheckBox;
	private JCheckBox ownPlayersCheckBox;
	private final Filter filter;

	public FilterPanel(Filter filter) {
		this.filter = filter;
		initComponents();
		initFromFilter();
		addListeners();
	}

	private void initFromFilter() {
		if (this.filter.getSeasonFilterValue() != null) {
			restoreComboBoxSelection(this.filter.getSeasonFilterValue().getId(),
					this.seasonComboBox);
		}

		if (this.filter.getTactic() != null) {
			restoreComboBoxSelection(this.filter.getTactic(), this.tacticComboBox);
		}

		this.onlySEMatchesCheckBox.setSelected(this.filter.isShowMatchesWithSEOnly());
		this.friendliesCheckBox.setSelected(this.filter.isShowFriendlies());
		this.leagueCheckBox.setSelected(this.filter.isShowLeague());
		this.relegationCheckBox.setSelected(this.filter.isShowRelegation());
		this.tournamentCheckBox.setSelected(this.filter.isShowTournament());
		this.cupCheckBox.setSelected(this.filter.isShowCup());
		this.mastersCheckBox.setSelected(this.filter.isShowMasters());
		this.specialitySECheckBox.setSelected(this.filter.isShowSpecialitySE());
		this.weatherSECheckBox.setSelected(this.filter.isShowWeatherSE());
		this.counterAttackSECheckBox.setSelected(this.filter.isShowCounterAttack());
		this.freeKickSECheckBox.setSelected(this.filter.isShowFreeKick());
		this.freeKickIndirectSECheckBox.setSelected(this.filter.isShowFreeKickIndirect());
		this.penaltySECheckBox.setSelected(this.filter.isShowPenalty());
		this.longshotSECheckBox.setSelected(this.filter.isShowLongShot());

		this.ownPlayersCheckBox.setSelected(this.filter.isShowOwnPlayersOnly());
		if (this.filter.isShowCurrentOwnPlayersOnly()) {
			this.currentOwnPlayersCheckBox.setSelected(true);
			this.ownPlayersCheckBox.setSelected(true);
			this.filter.setShowOwnPlayersOnly(true);
			this.ownPlayersCheckBox.setEnabled(false);
		}
		updatePlayerComboBoxData(this.filter.isShowCurrentOwnPlayersOnly());
		if (this.filter.getPlayerId() != null) {
			restoreComboBoxSelection(this.filter.getPlayerId(), this.playerComboBox);
		}
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.VERTICAL;

		add(createMatchFilterPanel(), gbc);
		gbc.gridx = 1;
		add(createMatchTypeFilterPanel(), gbc);
		gbc.gridx = 2;
		add(createSEFilterPanel(), gbc);
		gbc.gridx = 3;
		gbc.weightx = 1.0;
		add(createPlayerFilterPanel(), gbc);
	}

	private void addListeners() {

		this.seasonComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CBItem item = (CBItem) seasonComboBox.getSelectedItem();
				if (item == null) {
					filter.setSeasonFilterValue(null);
				} else {
					filter.setSeasonFilterValue(SeasonFilterValue.getById(item.getId()));
				}
			}
		});

		this.tacticComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CBItem item = (CBItem) tacticComboBox.getSelectedItem();
				if (item == null) {
					filter.setTactic(null);
				} else {
					filter.setTactic(item.getId());
				}
			}
		});

		ActionListener checkBoxListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				boolean selected = ((JCheckBox) e.getSource()).isSelected();

				if (source == onlySEMatchesCheckBox) {
					filter.setShowMatchesWithSEOnly(selected);
				} else if (source == friendliesCheckBox) {
					filter.setShowFriendlies(selected);
				} else if (source == leagueCheckBox) {
					filter.setShowLeague(selected);
				} else if (source == relegationCheckBox) {
					filter.setShowRelegation(selected);
				} else if (source == tournamentCheckBox) {
					filter.setShowTournament(selected);
				} else if (source == cupCheckBox) {
					filter.setShowCup(selected);
				} else if (source == mastersCheckBox) {
					filter.setShowMasters(selected);
				} else if (source == specialitySECheckBox) {
					filter.setShowSpecialitySE(selected);
				} else if (source == weatherSECheckBox) {
					filter.setShowWeatherSE(selected);
				} else if (source == counterAttackSECheckBox) {
					filter.setShowCounterAttack(selected);
				} else if (source == freeKickSECheckBox) {
					filter.setShowFreeKick(selected);
				} else if (source == freeKickIndirectSECheckBox) {
					filter.setShowFreeKickIndirect(selected);
				} else if (source == penaltySECheckBox) {
					filter.setShowPenalty(selected);
				} else if (source == longshotSECheckBox) {
					filter.setShowLongShot(selected);
				} else if (source == currentOwnPlayersCheckBox) {
					if (selected) {
						ownPlayersCheckBox.setSelected(true);
					}
					ownPlayersCheckBox.setEnabled(!selected);
					filter.setShowCurrentOwnPlayersOnly(selected);
					updatePlayerComboBoxData(selected);
				} else if (source == ownPlayersCheckBox) {
					filter.setShowOwnPlayersOnly(selected);
				}
			}
		};

		this.onlySEMatchesCheckBox.addActionListener(checkBoxListener);
		this.friendliesCheckBox.addActionListener(checkBoxListener);
		this.leagueCheckBox.addActionListener(checkBoxListener);
		this.relegationCheckBox.addActionListener(checkBoxListener);
		this.tournamentCheckBox.addActionListener(checkBoxListener);
		this.cupCheckBox.addActionListener(checkBoxListener);
		this.mastersCheckBox.addActionListener(checkBoxListener);
		this.specialitySECheckBox.addActionListener(checkBoxListener);
		this.weatherSECheckBox.addActionListener(checkBoxListener);
		this.counterAttackSECheckBox.addActionListener(checkBoxListener);
		this.freeKickSECheckBox.addActionListener(checkBoxListener);
		this.freeKickIndirectSECheckBox.addActionListener(checkBoxListener);
		this.penaltySECheckBox.addActionListener(checkBoxListener);
		this.longshotSECheckBox.addActionListener(checkBoxListener);
		this.currentOwnPlayersCheckBox.addActionListener(checkBoxListener);
		this.ownPlayersCheckBox.addActionListener(checkBoxListener);

		this.playerComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CBItem item = (CBItem) playerComboBox.getSelectedItem();
				if (item == null) {
					filter.setPlayerId(null);
				} else {
					filter.setPlayerId(Integer.valueOf(item.getId()));
				}
			}
		});

	}

	private JPanel createSEFilterPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory
				.createTitledBorder(getLangStr("specialEvents.filter.se.title")));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(3, 3, 3, 3);

		this.specialitySECheckBox = new JCheckBox();
		this.specialitySECheckBox.setText(getLangStr("SPECIALTYSE"));
		panel.add(this.specialitySECheckBox, gbc);

		this.weatherSECheckBox = new JCheckBox();
		this.weatherSECheckBox.setText(getLangStr("WEATHERSE"));
		gbc.gridy = 1;
		panel.add(this.weatherSECheckBox, gbc);

		this.counterAttackSECheckBox = new JCheckBox();
		this.counterAttackSECheckBox.setText(getLangStr("ls.match.event.counter-attack"));
		gbc.gridy = 2;
		panel.add(this.counterAttackSECheckBox, gbc);

		this.freeKickSECheckBox = new JCheckBox();
		this.freeKickSECheckBox.setText(getLangStr("highlight_freekick"));
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(this.freeKickSECheckBox, gbc);

		this.freeKickIndirectSECheckBox = new JCheckBox();
		this.freeKickIndirectSECheckBox.setText(getLangStr("IFK"));
		gbc.gridy = 1;
		panel.add(this.freeKickIndirectSECheckBox, gbc);

		this.penaltySECheckBox = new JCheckBox();
		this.penaltySECheckBox.setText(getLangStr("highlight_penalty"));
		gbc.gridy = 2;
		gbc.weighty = 1.0;
		panel.add(this.penaltySECheckBox, gbc);

		this.longshotSECheckBox = new JCheckBox();
		this.longshotSECheckBox.setText(getLangStr("ls.match.event.longshot"));
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		panel.add(this.longshotSECheckBox, gbc);

		return panel;
	}

	private JPanel createMatchFilterPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory
				.createTitledBorder(getLangStr("specialEvents.filter.matches.title")));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.gridwidth = 2;

		this.onlySEMatchesCheckBox = new JCheckBox();
		this.onlySEMatchesCheckBox.setText(getLangStr("SpieleMitSEs"));
		panel.add(this.onlySEMatchesCheckBox, gbc);

		JLabel matchesLabel = new JLabel("Matches:");
		matchesLabel.setText(getLangStr("specialEvents.filter.matches.matches"));
		gbc.gridwidth = 1;
		gbc.gridy = 1;
		panel.add(matchesLabel, gbc);

		CBItem[] comboItems = new CBItem[3];
		comboItems[0] = new CBItem(getLangStr("AktSaison"),
				SeasonFilterValue.CURRENT_SEASON.getId());
		comboItems[1] = new CBItem(getLangStr("2Saison"),
				SeasonFilterValue.LAST_TWO_SEASONS.getId());
		comboItems[2] = new CBItem(getLangStr("AllSeasons"), SeasonFilterValue.ALL_SEASONS.getId());
		this.seasonComboBox = new JComboBox(comboItems);
		gbc.gridx = 1;
		panel.add(this.seasonComboBox, gbc);

		JLabel tacticsLabel = new JLabel();
		tacticsLabel.setText(getLangStr("ls.team.tactic") + ":");
		gbc.gridy = 2;
		gbc.gridx = 0;
		panel.add(tacticsLabel, gbc);

		this.tacticComboBox = new JComboBox(getTactics().toArray());
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = 1;
		panel.add(this.tacticComboBox, gbc);
		return panel;
	}

	private List<CBItem> getTactics() {
		List<CBItem> list = new ArrayList<CBItem>();

		list.add(new CBItem(getLangStr("ls.team.tactic_short.pressing"),
				IMatchDetails.TAKTIK_PRESSING));
		list.add(new CBItem(getLangStr("ls.team.tactic_short.counter-attacks"),
				IMatchDetails.TAKTIK_KONTER));
		list.add(new CBItem(getLangStr("ls.team.tactic_short.attackinthemiddle"),
				IMatchDetails.TAKTIK_MIDDLE));
		list.add(new CBItem(getLangStr("ls.team.tactic_short.attackonwings"),
				IMatchDetails.TAKTIK_WINGS));
		list.add(new CBItem(getLangStr("ls.team.tactic_short.playcreatively"),
				IMatchDetails.TAKTIK_CREATIVE));
		list.add(new CBItem(getLangStr("ls.team.tactic_short.longshots"),
				IMatchDetails.TAKTIK_LONGSHOTS));

		Collections.sort(list, new Comparator<CBItem>() {

			@Override
			public int compare(CBItem o1, CBItem o2) {
				return o1.getText().compareTo(o2.getText());
			}
		});

		list.add(0, new CBItem(getLangStr("ls.team.tactic_short.normal"), IMatchDetails.TAKTIK_NORMAL));
		list.add(0, null);
		return list;
	}

	private JPanel createMatchTypeFilterPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory
				.createTitledBorder(getLangStr("specialEvents.filter.matchTypes.title")));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(3, 3, 3, 3);

		this.friendliesCheckBox = new JCheckBox();
		this.friendliesCheckBox.setText(getLangStr("specialEvents.filter.matchTypes.friendly"));
		panel.add(this.friendliesCheckBox, gbc);

		this.leagueCheckBox = new JCheckBox();
		this.leagueCheckBox.setText(getLangStr("specialEvents.filter.matchTypes.league"));
		gbc.gridy = 1;
		panel.add(this.leagueCheckBox, gbc);

		this.relegationCheckBox = new JCheckBox();
		this.relegationCheckBox.setText(getLangStr("specialEvents.filter.matchTypes.relegation"));
		gbc.gridy = 2;
		panel.add(this.relegationCheckBox, gbc);

		this.cupCheckBox = new JCheckBox();
		this.cupCheckBox.setText(getLangStr("specialEvents.filter.matchTypes.cup"));
		gbc.gridy = 0;
		gbc.gridx = 1;
		panel.add(this.cupCheckBox, gbc);

		this.tournamentCheckBox = new JCheckBox();
		this.tournamentCheckBox.setText(getLangStr("specialEvents.filter.matchTypes.tournament"));
		gbc.gridy = 1;
		panel.add(this.tournamentCheckBox, gbc);

		this.mastersCheckBox = new JCheckBox();
		this.mastersCheckBox.setText(getLangStr("specialEvents.filter.matchTypes.masters"));
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panel.add(this.mastersCheckBox, gbc);

		return panel;
	}

	private JPanel createPlayerFilterPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory
				.createTitledBorder(getLangStr("specialEvents.filter.players.title")));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(3, 3, 3, 3);

		JLabel playerLabel = new JLabel();
		playerLabel.setText(getLangStr("specialEvents.filter.players.player"));
		panel.add(playerLabel, gbc);

		this.playerComboBox = new JComboBox();
		this.playerComboBox.setRenderer(new ComboBoxRenderer(this.playerComboBox.getRenderer()));
		gbc.gridx = 1;
		panel.add(this.playerComboBox, gbc);

		this.currentOwnPlayersCheckBox = new JCheckBox();
		this.currentOwnPlayersCheckBox
				.setText(getLangStr("specialEvents.filter.players.currentOwnPlayers"));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(this.currentOwnPlayersCheckBox, gbc);

		this.ownPlayersCheckBox = new JCheckBox();
		this.ownPlayersCheckBox.setText(getLangStr("specialEvents.filter.players.ownPlayers"));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		panel.add(this.ownPlayersCheckBox, gbc);

		return panel;
	}

	/**
	 * Convenience method.
	 * 
	 * @param key
	 * @return
	 */
	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}

	private void updatePlayerComboBoxData(boolean currentPlayersOnly) {
		CBItem oldItem = (CBItem) this.playerComboBox.getSelectedItem();

		Comparator<Player> comparator = new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};

		List<CBItem> playerItems = new ArrayList<CBItem>();
		List<Player> players = new ArrayList<Player>(HOVerwaltung.instance().getModel()
				.getAllSpieler());
		Collections.sort(players, comparator);
		for (Player player : players) {
			playerItems.add(new PlayerCBItem(player.getName(), player.getSpielerID(), player
					.getSpezialitaet()));
		}

		if (!currentPlayersOnly) {
			players = new ArrayList<Player>(HOVerwaltung.instance().getModel().getAllOldSpieler());
			Collections.sort(players, comparator);
			if (!players.isEmpty()) {
				playerItems.add(null);
			}
			for (Player player : players) {
				playerItems.add(new PlayerCBItem(player.getName(), player.getSpielerID(), player
						.getSpezialitaet()));
			}
		}

		playerItems.add(0, null);
		this.playerComboBox.setModel(new DefaultComboBoxModel(playerItems.toArray()));

		if (oldItem != null) {
			restoreComboBoxSelection(oldItem.getId(), this.playerComboBox);
		} else {
			this.playerComboBox.setSelectedItem(null);
		}
	}

	private void restoreComboBoxSelection(int id, JComboBox comboBox) {
		ComboBoxModel model = comboBox.getModel();
		CBItem item = null;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) != null && ((CBItem) model.getElementAt(i)).getId() == id) {
				item = (CBItem) model.getElementAt(i);
				break;
			}
		}
		comboBox.setSelectedItem(item);
	}

	private class ComboBoxRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1148438406134827829L;
		private final ListCellRenderer delegate;

		public ComboBoxRenderer(ListCellRenderer delegate) {
			this.delegate = delegate;
		}

		/*
		 * This method finds the image and text corresponding to the selected
		 * value and returns the label, set up to display the text and image.
		 */
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {

			PlayerCBItem item = (PlayerCBItem) value;
			String text = (item != null) ? item.getText() : null;
			setText(text);

			Component component = this.delegate.getListCellRendererComponent(list, text, index,
					isSelected, cellHasFocus);

			if (component instanceof JLabel) {
				if (item != null && item.getId() >= 0) {
					if (item.getSpeciality() > 0) {
						((JLabel) component).setIcon(ThemeManager.getIcon(HOIconName.SPECIAL[item
								.getSpeciality()]));
					} else {
						((JLabel) component).setIcon(ThemeManager.getIcon(HOIconName.EMPTY));
					}
				} else {
					((JLabel) component).setIcon(null);
				}
			}

			return component;
		}
	}

	private class PlayerCBItem extends CBItem {

		private int speciality = -1;

		public PlayerCBItem(String text, int id, int speciality) {
			super(text, id);
			this.speciality = speciality;
		}

		public int getSpeciality() {
			return speciality;
		}
	}
}
