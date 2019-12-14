package module.lineup;

import core.datatype.CBItem;
import core.gui.CursorToolkit;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.Weather;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

public class AufstellungsAssistentPanelNew extends ImagePanel implements
		IAufstellungsAssistentPanel {

	private static final long serialVersionUID = -6853036429678216392L;
	private WeatherChooser weatherChooser;
	private GroupChooser groupChooser;
	private JLabel groupLabel;
	private JComboBox assistantPriorityComboBox;
	private JCheckBox idealPositionFirst;
	private JCheckBox considerFormCheckBox;
	private JCheckBox injuredCheckBox;
	private JCheckBox suspendedCheckBox;
	private JCheckBox excludeLastLinupCheckBox;
	private JCheckBox allPlayersCheckBox;

	public AufstellungsAssistentPanelNew() {
		initComponents();
		initData();
		addListeners();
	}

	@Override
	public boolean isExcludeLastMatch() {
		return false;
	}

	@Override
	public boolean isConsiderForm() {
		return this.considerFormCheckBox.isSelected();
	}

	@Override
	public boolean isIgnoreSuspended() {
		return this.suspendedCheckBox.isSelected();
	}

	@Override
	public String getGroup() {
		List<String> groups = this.groupChooser.getGroups();
		if (!groups.isEmpty()) {
			return groups.get(0);
		}
		return "";
	}

	@Override
	public boolean isGroupFilter() {
		return true;
	}

	@Override
	public boolean isIdealPositionZuerst() {
		return this.idealPositionFirst.isSelected();
	}

	@Override
	public boolean isNotGroup() {
		return false;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public boolean isIgnoreInjured() {
		return this.injuredCheckBox.isSelected();
	}

	@Override
	public Weather getWeather() {
		return weatherChooser.getWeather();
	}

	@Override
	public void setWeather(Weather weather) {weatherChooser.setWeather(weather);}

	@Override
	public void addToAssistant(PlayerPositionPanel positionPanel) {
	}

	@Override
	public Map<Integer, Boolean> getPositionStatuses() {
		return null;
	}

	@Override
	public List<String> getGroups() {
		return this.groupChooser.getGroups();
	}

	public void setGroups(List<String> groups) {
		this.groupChooser.setGroups(groups);
	}

	public static List<String> asList(String str) {
		String[] splitted = str.split(";");
		return Arrays.asList(splitted);
	}

	public static String asString(List<String> list) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String group : list) {
			stringBuilder.append(group).append(';');
		}
		if (stringBuilder.length() > 0) {
			stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
		}
		return stringBuilder.toString();
	}

	private void initData() {
		this.groupChooser
				.setGroups(asList(UserParameter.instance().aufstellungsAssistentPanel_gruppe));
	}

	private void initComponents() {
		setLayout(new GridBagLayout());

		JLabel weatherLabel = new JLabel(getLangStr("ls.match.weather"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 2, 2);
		add(weatherLabel, gbc);

		this.weatherChooser = new WeatherChooser();
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 2, 4);
		add(this.weatherChooser, gbc);

		this.groupLabel = new JLabel(getLangStr("Gruppe"));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 2, 2);
		add(this.groupLabel, gbc);

		this.groupChooser = new GroupChooser();
		gbc.gridx = 1;
		gbc.insets = new Insets(2, 2, 2, 4);
		add(this.groupChooser, gbc);

		this.allPlayersCheckBox = new JCheckBox("all");
//		gbc.gridy++;
//		add(this.allPlayersCheckBox, gbc);
		
		JLabel priorityLabel = new JLabel(getLangStr("lineupassist.priority"));
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(6, 4, 6, 2);
		add(priorityLabel, gbc);

		CBItem[] priority = {
				new CBItem(HOVerwaltung.instance().getLanguageString("AW-MF-ST"),
						LineupAssistant.AW_MF_ST),
				new CBItem(HOVerwaltung.instance().getLanguageString("AW-ST-MF"),
						LineupAssistant.AW_ST_MF),
				new CBItem(HOVerwaltung.instance().getLanguageString("MF-AW-ST"),
						LineupAssistant.MF_AW_ST),
				new CBItem(HOVerwaltung.instance().getLanguageString("MF-ST-AW"),
						LineupAssistant.MF_ST_AW),
				new CBItem(HOVerwaltung.instance().getLanguageString("ST-AW-MF"),
						LineupAssistant.ST_AW_MF),
				new CBItem(HOVerwaltung.instance().getLanguageString("ST-MF-AW"),
						LineupAssistant.ST_MF_AW) };
		this.assistantPriorityComboBox = new JComboBox(priority);
		this.assistantPriorityComboBox
				.setToolTipText(getLangStr("tt_AufstellungsAssistent_Reihenfolge"));
		gbc.gridx = 1;
		gbc.insets = new Insets(6, 2, 6, 4);
		add(this.assistantPriorityComboBox, gbc);

		this.idealPositionFirst = new JCheckBox(getLangStr("Idealposition_zuerst"),
				UserParameter.instance().aufstellungsAssistentPanel_idealPosition);
		this.idealPositionFirst
				.setToolTipText(getLangStr("tt_AufstellungsAssistent_Idealposition"));
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 2, 2, 4);
		add(this.idealPositionFirst, gbc);

		this.considerFormCheckBox = new JCheckBox(getLangStr("Form_beruecksichtigen"),
				UserParameter.instance().aufstellungsAssistentPanel_form);
		this.considerFormCheckBox.setToolTipText(getLangStr("tt_AufstellungsAssistent_Form"));
		gbc.gridy++;
		gbc.insets = new Insets(2, 2, 2, 4);
		add(this.considerFormCheckBox, gbc);

		this.injuredCheckBox = new JCheckBox(getLangStr("Verletze_aufstellen"),
				UserParameter.instance().aufstellungsAssistentPanel_verletzt);
		this.injuredCheckBox.setToolTipText(getLangStr("tt_AufstellungsAssistent_Verletzte"));
		gbc.gridy++;
		gbc.insets = new Insets(2, 2, 2, 4);
		add(this.injuredCheckBox, gbc);

		this.suspendedCheckBox = new JCheckBox(getLangStr("Gesperrte_aufstellen"),
				UserParameter.instance().aufstellungsAssistentPanel_gesperrt);
		this.suspendedCheckBox.setToolTipText(getLangStr("tt_AufstellungsAssistent_Gesperrte"));
		gbc.gridy++;
		gbc.insets = new Insets(2, 2, 2, 4);
		add(this.suspendedCheckBox, gbc);

		this.excludeLastLinupCheckBox = new JCheckBox(getLangStr("NotLast_aufstellen"),
				UserParameter.instance().aufstellungsAssistentPanel_notLast);
		this.excludeLastLinupCheckBox
				.setToolTipText(getLangStr("tt_AufstellungsAssistent_NotLast"));
		gbc.gridy++;
		gbc.insets = new Insets(2, 2, 2, 4);
		add(this.excludeLastLinupCheckBox, gbc);

		// dummy component to consume all extra space
		gbc.gridx++;
		gbc.gridy++;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(new JPanel(), gbc);
	}

	private void addListeners() {
		this.weatherChooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						CursorToolkit.startWaitCursor(AufstellungsAssistentPanelNew.this);
						try {
							HOMainFrame.instance().getAufstellungsPanel().update();
						} finally {
							CursorToolkit.stopWaitCursor(AufstellungsAssistentPanelNew.this);
						}
					}
				});
			}
		});
		
		this.allPlayersCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean enable = e.getStateChange() == ItemEvent.DESELECTED;
				groupChooser.setChooserEnabled(enable);
				groupLabel.setEnabled(enable);
			}
		});
	}

	private class GroupChooser extends JPanel {

		private static final long serialVersionUID = -8049080706451222927L;
		private JToggleButton aBtn;
		private JToggleButton bBtn;
		private JToggleButton cBtn;
		private JToggleButton dBtn;
		private JToggleButton eBtn;
		private JToggleButton fBtn;
		private JToggleButton ungroupedBtn;

		public GroupChooser() {
			initComponents();
		}
		
		List<String> getGroups() {
			List<String> list = new ArrayList<String>();
			if (aBtn.isSelected()) {
				list.add(HOIconName.TEAMSMILIES[1]);
			}
			if (bBtn.isSelected()) {
				list.add(HOIconName.TEAMSMILIES[2]);
			}
			if (cBtn.isSelected()) {
				list.add(HOIconName.TEAMSMILIES[3]);
			}
			if (dBtn.isSelected()) {
				list.add(HOIconName.TEAMSMILIES[4]);
			}
			if (eBtn.isSelected()) {
				list.add(HOIconName.TEAMSMILIES[5]);
			}
			if (fBtn.isSelected()) {
				list.add(HOIconName.TEAMSMILIES[6]);
			}
			return list;
		}

		public void setChooserEnabled(boolean enabled) {
			this.aBtn.setEnabled(enabled);
			this.bBtn.setEnabled(enabled);
			this.cBtn.setEnabled(enabled);
			this.dBtn.setEnabled(enabled);
			this.eBtn.setEnabled(enabled);
			this.fBtn.setEnabled(enabled);
			this.ungroupedBtn.setEnabled(enabled);
		}

		void setGroups(List<String> groups) {
			List<String> list = (groups != null) ? groups : Collections.<String> emptyList();
			this.aBtn.setSelected(list.contains(HOIconName.TEAMSMILIES[1]));
			this.bBtn.setSelected(list.contains(HOIconName.TEAMSMILIES[2]));
			this.cBtn.setSelected(list.contains(HOIconName.TEAMSMILIES[3]));
			this.dBtn.setSelected(list.contains(HOIconName.TEAMSMILIES[4]));
			this.eBtn.setSelected(list.contains(HOIconName.TEAMSMILIES[5]));
			this.fBtn.setSelected(list.contains(HOIconName.TEAMSMILIES[6]));
			// ungroupedBtn
		}

		private void initComponents() {
			setOpaque(false);
			setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));

			Dimension btnSize = new Dimension(28, 28);
			this.aBtn = new JToggleButton();
			this.aBtn.setPreferredSize(btnSize);
			this.aBtn.setIcon(ThemeManager.getIcon(HOIconName.TEAMSMILIES[1]));
			add(this.aBtn);

			this.bBtn = new JToggleButton();
			this.bBtn.setPreferredSize(btnSize);
			this.bBtn.setIcon(ThemeManager.getIcon(HOIconName.TEAMSMILIES[2]));
			add(this.bBtn);

			this.cBtn = new JToggleButton();
			this.cBtn.setPreferredSize(btnSize);
			this.cBtn.setIcon(ThemeManager.getIcon(HOIconName.TEAMSMILIES[3]));
			add(this.cBtn);

			this.dBtn = new JToggleButton();
			this.dBtn.setPreferredSize(btnSize);
			this.dBtn.setIcon(ThemeManager.getIcon(HOIconName.TEAMSMILIES[4]));
			add(this.dBtn);

			this.eBtn = new JToggleButton();
			this.eBtn.setPreferredSize(btnSize);
			this.eBtn.setIcon(ThemeManager.getIcon(HOIconName.TEAMSMILIES[5]));
			add(this.eBtn);

			this.fBtn = new JToggleButton();
			this.fBtn.setPreferredSize(btnSize);
			this.fBtn.setIcon(ThemeManager.getIcon(HOIconName.TEAMSMILIES[6]));
			add(this.fBtn);
			
			this.ungroupedBtn  = new JToggleButton();
			this.ungroupedBtn.setPreferredSize(btnSize);
//			this.fBtn.setIcon(ThemeManager.getIcon(HOIconName.TEAMSMILIES[6]));
			add(this.ungroupedBtn);
		}
	}

	/**
	 * Component to show/choose the weather via some toggle buttons.
	 * 
	 */
	private class WeatherChooser extends JPanel {

		private static final long serialVersionUID = -3666581300985404900L;
		private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();
		private JToggleButton sunnyBtn;
		private JToggleButton partiallyCloudyBtn;
		private JToggleButton overcastBtn;
		private JToggleButton rainyBtn;
		private ButtonGroup buttonGroup;

		public WeatherChooser() {
			initComponents();
			setWeather(Weather.PARTIALLY_CLOUDY);
		}

		public void addActionListener(ActionListener listener) {
			if (!this.actionListeners.contains(listener)) {
				this.actionListeners.add(listener);
			}
		}

		public void removeActionListener(ActionListener listener) {
			this.actionListeners.remove(listener);
		}

		public void setWeather(Weather weather) {
			switch (weather) {
			case SUNNY:
				this.buttonGroup.setSelected(this.sunnyBtn.getModel(), true);
				break;
			case PARTIALLY_CLOUDY:
				this.buttonGroup.setSelected(this.partiallyCloudyBtn.getModel(), true);
				break;
			case OVERCAST:
				this.buttonGroup.setSelected(this.overcastBtn.getModel(), true);
				break;
			case RAINY:
				this.buttonGroup.setSelected(this.rainyBtn.getModel(), true);
				break;
			default:
				this.buttonGroup.clearSelection();
				break;
			}
		}

		public Weather getWeather() {
			ButtonModel btnModel = this.buttonGroup.getSelection();
			if (btnModel != null) {
				if (btnModel == this.sunnyBtn.getModel()) {
					return Weather.SUNNY;
				}
				if (btnModel == this.partiallyCloudyBtn.getModel()) {
					return Weather.PARTIALLY_CLOUDY;
				}
				if (btnModel == this.overcastBtn.getModel()) {
					return Weather.OVERCAST;
				}
				if (btnModel == this.rainyBtn.getModel()) {
					return Weather.RAINY;
				}
			}

			return null;
		}

		private void initComponents() {
			setOpaque(false);
			setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));

			Dimension btnSize = new Dimension(28, 28);
			this.buttonGroup = new ButtonGroup();
			this.sunnyBtn = new JToggleButton();
			this.sunnyBtn.setPreferredSize(btnSize);
			this.sunnyBtn.setIcon(ThemeManager.getIcon(HOIconName.WEATHER[Weather.SUNNY.getId()]));
			add(this.sunnyBtn);
			this.buttonGroup.add(this.sunnyBtn);

			this.partiallyCloudyBtn = new JToggleButton();
			this.partiallyCloudyBtn.setPreferredSize(btnSize);
			this.partiallyCloudyBtn.setIcon(ThemeManager
					.getIcon(HOIconName.WEATHER[Weather.PARTIALLY_CLOUDY.getId()]));
			add(this.partiallyCloudyBtn);
			this.buttonGroup.add(this.partiallyCloudyBtn);

			this.overcastBtn = new JToggleButton();
			this.overcastBtn.setPreferredSize(btnSize);
			this.overcastBtn.setIcon(ThemeManager.getIcon(HOIconName.WEATHER[Weather.OVERCAST
					.getId()]));
			add(this.overcastBtn);
			this.buttonGroup.add(this.overcastBtn);

			this.rainyBtn = new JToggleButton();
			this.rainyBtn.setPreferredSize(btnSize);
			this.rainyBtn.setIcon(ThemeManager.getIcon(HOIconName.WEATHER[Weather.RAINY.getId()]));
			add(this.rainyBtn);
			this.buttonGroup.add(this.rainyBtn);

			ActionListener al = new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					for (int i = actionListeners.size() - 1; i >= 0; i--) {
						actionListeners.get(i).actionPerformed(e);
					}
				}
			};
			this.sunnyBtn.addActionListener(al);
			this.partiallyCloudyBtn.addActionListener(al);
			this.overcastBtn.addActionListener(al);
			this.rainyBtn.addActionListener(al);
		}
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
