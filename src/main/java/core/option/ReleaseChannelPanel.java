package core.option;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.Updater;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

/**
 * Controls for release channel with description
 * and auto-update-check control.
 * Release Channel Panel
 */
public final class ReleaseChannelPanel extends ImagePanel {

	//~ Static fields/initializers -----------------------------------------------------------------
	private final JRadioButton m_jrb_Stable = new JRadioButton(Updater.ReleaseChannel.STABLE.label, false);
	private final JRadioButton m_jrb_Beta = new JRadioButton(Updater.ReleaseChannel.BETA.label, false);
	private final JRadioButton m_jrb_Dev = new JRadioButton(Updater.ReleaseChannel.DEV.label, false);
	private final ButtonGroup m_bg_ButtonGroup = new ButtonGroup();
	private final JLabel m_jl_PleaseSelect = new JLabel(HOVerwaltung.instance().getLanguageString("options.release_channels_pleaseSelect"));
	private final JTextArea m_jta_Description = new JTextArea("", 8, 1);
	private Updater.ReleaseChannel rc;
    private JCheckBox m_jchUpdateCheck;

	public Updater.ReleaseChannel getRc() {
		return rc;
	}

	//Constructors
	public ReleaseChannelPanel() {
		initComponents();
	}

	//~ Methods ------------------------------------------------------------------------------------

	ItemListener releaseChannelListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent itemEvent) {
			JRadioButton source = (JRadioButton)itemEvent.getItem();
			String ReleaseChannelLabel;
			if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
				ReleaseChannelLabel = source.getText();
				core.model.UserParameter.temp().ReleaseChannel = ReleaseChannelLabel;
				m_jta_Description.setText(
						core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_" +
								source.getText().toUpperCase(Locale.ENGLISH) + "_desc")
				);
				rc = Updater.ReleaseChannel.byLabel(ReleaseChannelLabel);
			}
		}
	};

	public void stateChanged(ChangeEvent arg0) {}

	private void initComponents() {
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

		GridBagConstraints placement;

		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 0, 25, 0);
		placement.anchor = GridBagConstraints.NORTH;
		placement.gridwidth = GridBagConstraints.REMAINDER;
		placement.weightx = 1;
		placement.gridx = 0;
		placement.gridy = 0;
		add(m_jl_PleaseSelect, placement);

		m_jrb_Stable.addItemListener(releaseChannelListener);
		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 50, 25, 0);
		placement.anchor = GridBagConstraints.WEST;
		placement.gridx = 0;
		placement.gridy = 1;
		add(m_jrb_Stable, placement);

		m_jrb_Beta.addItemListener(releaseChannelListener);
		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 0, 25, 0);
		placement.weightx = 1;
		placement.gridx = 1;
		placement.gridy = 1;
		add(m_jrb_Beta, placement);

		m_jrb_Dev.addItemListener(releaseChannelListener);
		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 0, 25, 50);
		placement.anchor = GridBagConstraints.EAST;
		placement.gridx = 2;
		placement.gridy = 1;
		add(m_jrb_Dev, placement);

		m_jta_Description.setLineWrap(true);
		m_jta_Description.setWrapStyleWord(true);
		m_jta_Description.setEditable(false);
		m_jta_Description.setPreferredSize(new Dimension(430, 200));
		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 0, 25, 0);
		placement.fill = GridBagConstraints.BOTH;
		placement.anchor = GridBagConstraints.CENTER;
		placement.gridwidth = GridBagConstraints.REMAINDER;
		placement.gridx = 0;
		placement.gridy = 2;
		add(m_jta_Description, placement);

		m_jchUpdateCheck = new JCheckBox(core.model.HOVerwaltung.instance().getLanguageString("UpdateCheck"));
        m_jchUpdateCheck.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString("tt_Optionen_UpdateCheck"));
        m_jchUpdateCheck.setSelected(UserParameter.instance().updateCheck);
        m_jchUpdateCheck.setOpaque(false);
        m_jchUpdateCheck.setEnabled(true);
        m_jchUpdateCheck.addItemListener(itemEvent -> UserParameter.temp().updateCheck = (itemEvent.getStateChange() == ItemEvent.SELECTED));
		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 0, 25, 0);
		placement.anchor = GridBagConstraints.NORTH;
		placement.gridwidth = GridBagConstraints.REMAINDER;
		placement.weightx = 1;
		placement.weighty = 1;
		placement.gridx = 0;
		placement.gridy = 3;
        add(m_jchUpdateCheck, placement);

		m_bg_ButtonGroup.add(m_jrb_Stable);
		m_bg_ButtonGroup.add(m_jrb_Beta);
		m_bg_ButtonGroup.add(m_jrb_Dev);

		switch (core.model.UserParameter.temp().ReleaseChannel) {
			case "Stable":
				m_jrb_Stable.setSelected(true);
				break;
			case "Beta":
				m_jrb_Beta.setSelected(true);
				break;
			case "Dev":
				m_jrb_Dev.setSelected(true);
				break;
			default:
				break;
		}
	}
}
