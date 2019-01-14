package core.option;

import core.model.HOVerwaltung;
import core.gui.comp.panel.ImagePanel;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;

/**
 * Controls for release channel with description
 * and auto-update-check control.
 * Release Channel Panel
 */
public final class ReleaseChannelPanel extends ImagePanel
	implements javax.swing.event.ChangeListener, java.awt.event.ItemListener {

	//~ Static fields/initializers -----------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	private JRadioButton m_jrb_Stable = new JRadioButton("Stable", false);
	private JRadioButton m_jrb_Beta = new JRadioButton("Beta", false);
	private JRadioButton m_jrb_Dev = new JRadioButton("Dev", false);
	private	ButtonGroup m_bg_ButtonGroup = new ButtonGroup();
	private JLabel m_jl_PleaseSelect = new JLabel(HOVerwaltung.instance().getLanguageString("options.release_channels_pleaseSelect"));
	private JTextArea m_jta_Description = new JTextArea("", 8, 1);
    private JCheckBox m_jchUpdateCheck;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	* Creates a new ReleaseChannelPanel object.
	*/
	public ReleaseChannelPanel() {
		initComponents();
	}

	//~ Methods ------------------------------------------------------------------------------------

	public final void itemStateChanged(ItemEvent itemEvent) {
		JRadioButton source = (JRadioButton)itemEvent.getItem();
		if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
			core.model.UserParameter.temp().ReleaseChannel = source.getText();
			m_jta_Description.setText(
					core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_" +
					source.getText().toUpperCase(java.util.Locale.ENGLISH) + "_desc")
				);
		}
		core.model.UserParameter.temp().updateCheck = m_jchUpdateCheck.isSelected();
	}

	public void stateChanged(ChangeEvent arg0) {}

	private void initComponents() {
		setLayout(new GridBagLayout());

		GridBagConstraints placement;

		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 0, 25, 0);
		placement.anchor = GridBagConstraints.NORTH;
		placement.gridwidth = GridBagConstraints.REMAINDER;
		placement.weightx = 1;
		placement.gridx = 0;
		placement.gridy = 0;
		add(m_jl_PleaseSelect, placement);

		m_jrb_Stable.addItemListener(this);
		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 50, 25, 0);
		placement.anchor = GridBagConstraints.WEST;
		placement.gridx = 0;
		placement.gridy = 1;
		add(m_jrb_Stable, placement);

		m_jrb_Beta.addItemListener(this);
		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 0, 25, 0);
		placement.weightx = 1;
		placement.gridx = 1;
		placement.gridy = 1;
		add(m_jrb_Beta, placement);

		m_jrb_Dev.addItemListener(this);
		placement = new GridBagConstraints();
		placement.insets = new Insets(25, 0, 25, 50);
		placement.anchor = GridBagConstraints.EAST;
		placement.gridx = 2;
		placement.gridy = 1;
		add(m_jrb_Dev, placement);

		m_jta_Description.setLineWrap(true);
		m_jta_Description.setWrapStyleWord(true);
		m_jta_Description.setEditable(false);
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
        m_jchUpdateCheck.setSelected(core.model.UserParameter.temp().updateCheck);
        m_jchUpdateCheck.setOpaque(false);
        m_jchUpdateCheck.setEnabled(false);
        m_jchUpdateCheck.addItemListener(this);
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
