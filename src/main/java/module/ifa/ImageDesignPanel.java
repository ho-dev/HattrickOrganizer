package module.ifa;

import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;
import module.ifa.config.Config;
import module.ifa.model.IfaModel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.math.BigDecimal;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageDesignPanel extends JPanel {

	private static final long serialVersionUID = 4562976951725733955L;
	private static final int MIN_FLAG_WIDTH = 5;
	private static final int MAX_FLAG_WIDTH = 12;
	private EmblemPanel emblemPanel;
	private JScrollPane scrollPane;
	private JSpinner sizeSpinner;
	private boolean away;
	private JTextField headerTextField;
	private JCheckBox greyColoredCheckBox;
	private JCheckBox roundlyCheckBox;
	private JSlider brightnessSlider;
	private JCheckBox headerYesNoCheckBox;
	private JCheckBox animGifCheckBox;
	private JSpinner delaySpinner;
	private JLabel delayLabel;
	private final IfaModel model;

	public ImageDesignPanel(IfaModel model) {
		this.model = model;
		initialize();
		addListeners();
		setAway(true);
	}

	public void setAway(boolean away) {
		this.away = away;

		FlagDisplayModel flagDisplayModel = new FlagDisplayModel();
		int flagWidth;
		int brightness;
		String emblemPath;
		String headerText;
		boolean roundly;
		boolean grey;
		boolean showHeader;

		if (this.away) {
			flagWidth = ModuleConfig.instance().getInteger(Config.VISITED_FLAG_WIDTH.toString(),
					Integer.valueOf(8));
			emblemPath = ModuleConfig.instance().getString(Config.VISITED_EMBLEM_PATH.toString(),
					"");
			headerText = ModuleConfig.instance().getString(Config.VISITED_HEADER_TEXT.toString(),
					HOVerwaltung.instance().getLanguageString("ifa.visitedHeader.defaultText"));
			brightness = ModuleConfig.instance()
					.getInteger(Config.VISITED_BRIGHTNESS.toString(), Integer.valueOf(50))
					.intValue();
			grey = ModuleConfig.instance()
					.getBoolean(Config.VISITED_GREY.toString(), Boolean.FALSE).booleanValue();
			roundly = ModuleConfig.instance()
					.getBoolean(Config.VISITED_ROUNDLY.toString(), Boolean.FALSE).booleanValue();
			showHeader = ModuleConfig.instance()
					.getBoolean(Config.SHOW_VISITED_HEADER.toString(), Boolean.TRUE).booleanValue();
		} else {
			flagWidth = ModuleConfig.instance().getInteger(Config.HOSTED_FLAG_WIDTH.toString(),
					Integer.valueOf(8));
			emblemPath = ModuleConfig.instance()
					.getString(Config.HOSTED_EMBLEM_PATH.toString(), "");
			headerText = ModuleConfig.instance().getString(Config.HOSTED_HEADER_TEXT.toString(),
					HOVerwaltung.instance().getLanguageString("ifa.hostedHeader.defaultText"));
			brightness = ModuleConfig.instance()
					.getInteger(Config.HOSTED_BRIGHTNESS.toString(), Integer.valueOf(50))
					.intValue();
			grey = ModuleConfig.instance().getBoolean(Config.HOSTED_GREY.toString(), Boolean.FALSE)
					.booleanValue();
			roundly = ModuleConfig.instance()
					.getBoolean(Config.HOSTED_ROUNDLY.toString(), Boolean.FALSE).booleanValue();
			showHeader = ModuleConfig.instance()
					.getBoolean(Config.SHOW_HOSTED_HEADER.toString(), Boolean.TRUE).booleanValue();
		}
		flagDisplayModel.setRoundFlag(roundly);
		flagDisplayModel.setGrey(grey);
		flagDisplayModel.setFlagWidth(flagWidth);
		flagDisplayModel.setBrightness(brightness);
		if (this.emblemPanel != null) {
			this.scrollPane.getViewport().remove(this.emblemPanel);
		}
		this.emblemPanel = new EmblemPanel(away, this.model, flagDisplayModel);
		if (!emblemPath.equals("")) {
			File file = new File(emblemPath);
			if (file.exists()) {
				ImageIcon imageIcon = new ImageIcon(emblemPath);
				if (imageIcon != null) {
					this.emblemPanel.setLogo(imageIcon);
					this.emblemPanel.setImagePath(emblemPath);
				}
			}
		}
		this.roundlyCheckBox.setSelected(roundly);
		this.greyColoredCheckBox.setSelected(grey);
		this.sizeSpinner.setValue(Integer.valueOf(flagWidth));
		this.headerYesNoCheckBox.setSelected(showHeader);
		this.brightnessSlider.setValue(brightness);
		this.emblemPanel.setHeaderVisible(showHeader);
		this.emblemPanel.setHeaderText(headerText);
		this.headerTextField.setText(headerText);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.weighty = 0.2;
		gbc.anchor = GridBagConstraints.NORTH;
		this.scrollPane.getViewport().add(this.emblemPanel, gbc);

		validate();
		repaint();
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		JPanel settingsPanel = new JPanel(new GridBagLayout());

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.anchor = GridBagConstraints.WEST;
		gbc1.insets = new Insets(3, 5, 3, 5);
		this.roundlyCheckBox = new JCheckBox(getLangString("ifa.imageBuilder.roundly"));
		this.roundlyCheckBox.setSelected(false);
		panel.add(this.roundlyCheckBox, gbc1);
		this.greyColoredCheckBox = new JCheckBox(getLangString("ifa.imageBuilder.grey"), true);
		this.greyColoredCheckBox.setEnabled(true);
		gbc1.gridx = 1;
		panel.add(this.greyColoredCheckBox, gbc1);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(3, 5, 3, 5);
		settingsPanel.add(panel, gbc);

		JLabel brightnessLabel = new JLabel(getLangString("ifa.imageBuilder.brightness"));
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		settingsPanel.add(brightnessLabel, gbc);

		this.brightnessSlider = new JSlider(0, 100, 50);
		this.brightnessSlider.setMajorTickSpacing(25);
		this.brightnessSlider.setMinorTickSpacing(5);
		this.brightnessSlider.setPaintTicks(true);
		this.brightnessSlider.setPaintLabels(true);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		settingsPanel.add(this.brightnessSlider, gbc);

		JLabel sizeLabel = new JLabel(getLangString("ifa.imageBuilder.flagsPerRow"));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		settingsPanel.add(sizeLabel, gbc);

		int flagWidth = ModuleConfig.instance().getInteger(Config.VISITED_FLAG_WIDTH.toString(),
				Integer.valueOf(8));
		this.sizeSpinner = new JSpinner(new SpinnerNumberModel(flagWidth, MIN_FLAG_WIDTH,
				MAX_FLAG_WIDTH, 1));
		this.sizeSpinner.setName("size");
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		settingsPanel.add(this.sizeSpinner, gbc);

		this.headerYesNoCheckBox = new JCheckBox(getLangString("ifa.imageBuilder.showHeader"), true);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		settingsPanel.add(this.headerYesNoCheckBox, gbc);

		this.headerTextField = new JTextField();
		this.headerTextField.setPreferredSize(new Dimension(150, 25));
		this.headerTextField.setMinimumSize(new Dimension(150, 25));
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		settingsPanel.add(this.headerTextField, gbc);

		this.animGifCheckBox = new JCheckBox(getLangString("ifa.imageBuilder.animGif"),
				ModuleConfig.instance().getBoolean(Config.ANIMATED_GIF.toString(), Boolean.FALSE));
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		settingsPanel.add(this.animGifCheckBox, gbc);

		this.delayLabel = new JLabel(getLangString("ifa.imageBuilder.delay"));
		this.delayLabel.setEnabled(false);
		gbc.gridx = 1;
		gbc.insets = new Insets(5, 5, 5, 2);
		settingsPanel.add(this.delayLabel, gbc);
		double value = ModuleConfig.instance()
				.getBigDecimal(Config.ANIMATED_GIF_DELAY.toString(), BigDecimal.valueOf(5))
				.doubleValue();
		this.delaySpinner = new JSpinner(new SpinnerNumberModel(value, 0.0, 60.0, 0.1));
		this.delaySpinner.setEnabled(false);
		gbc.gridx = 2;
		gbc.insets = new Insets(5, 2, 5, 5);
		settingsPanel.add(this.delaySpinner, gbc);

		gbc = new GridBagConstraints();
		add(settingsPanel, gbc);

		this.scrollPane = new JScrollPane();
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(this.scrollPane, gbc);
	}

	public EmblemPanel getEmblemPanel() {
		return this.emblemPanel;
	}

	public boolean isAnimGif() {
		return this.animGifCheckBox.isSelected();
	}

	public JSpinner getDelaySpinner() {
		return this.delaySpinner;
	}

	private void addListeners() {
		this.headerYesNoCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				headerTextField.setEnabled(selected);
				if (away) {
					ModuleConfig.instance().setBoolean(Config.SHOW_VISITED_HEADER.toString(),
							selected);
				} else {
					ModuleConfig.instance().setBoolean(Config.SHOW_HOSTED_HEADER.toString(),
							selected);
				}
				emblemPanel.setHeaderVisible(selected);
			}
		});

		this.roundlyCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				if (away) {
					ModuleConfig.instance().setBoolean(Config.VISITED_ROUNDLY.toString(), selected);
				} else {
					ModuleConfig.instance().setBoolean(Config.HOSTED_ROUNDLY.toString(), selected);
				}
				emblemPanel.getFlagDisplayModel().setRoundFlag(selected);
			}
		});

		this.greyColoredCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				if (away) {
					ModuleConfig.instance().setBoolean(Config.VISITED_GREY.toString(), selected);
				} else {
					ModuleConfig.instance().setBoolean(Config.HOSTED_GREY.toString(), selected);
				}
				emblemPanel.getFlagDisplayModel().setGrey(selected);
			}
		});

		this.animGifCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = e.getStateChange() == ItemEvent.SELECTED;
				delaySpinner.setEnabled(selected);
				delayLabel.setEnabled(selected);
			}
		});

		this.brightnessSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (!brightnessSlider.getValueIsAdjusting()) {
					int value = brightnessSlider.getValue();
					if (away) {
						ModuleConfig.instance().setInteger(Config.VISITED_BRIGHTNESS.toString(),
								value);
					} else {
						ModuleConfig.instance().setInteger(Config.HOSTED_BRIGHTNESS.toString(),
								value);
					}
					emblemPanel.getFlagDisplayModel().setBrightness(value);
				}
			}
		});

		this.sizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int rowSize = ((Integer) ImageDesignPanel.this.sizeSpinner.getValue()).intValue();
				if (away) {
					ModuleConfig.instance().setInteger(Config.VISITED_FLAG_WIDTH.toString(),
							rowSize);
				} else {
					ModuleConfig.instance()
							.setInteger(Config.HOSTED_FLAG_WIDTH.toString(), rowSize);
				}
				emblemPanel.getFlagDisplayModel().setFlagWidth(rowSize);
			}
		});

		this.headerTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				ImageDesignPanel.this.emblemPanel.setHeaderText(((JTextField) arg0.getSource())
						.getText());
			}
		});

		this.headerTextField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String key = (away) ? Config.VISITED_HEADER_TEXT.toString()
						: Config.HOSTED_HEADER_TEXT.toString();
				ModuleConfig.instance().setString(key, headerTextField.getText());
			}
		});
	}

	/**
	 * Convenience method
	 * 
	 * @param key
	 * @return
	 */
	private static String getLangString(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}
}
