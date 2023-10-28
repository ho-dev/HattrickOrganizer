// %1193228360:hoplugins.teamAnalyzer.ui.component%
package module.playeranalysis;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class SettingPanel extends JPanel {

	public static ModuleConfig config = ModuleConfig.instance();
	private static final long serialVersionUID = -5721035453587068724L;
	private JCheckBox skillsCheckbox = new JCheckBox();

	SettingPanel() {
		super();
		skillsCheckbox.setSelected(config.getBoolean(PlayerAnalysisModule.SHOW_PLAYERCOMPARE));
		skillsCheckbox.setOpaque(false);
		jbInit();
	}

	/**
	 * Create a new Panel
	 * 
	 * @param string
	 *            Label text
	 * @param checkBox
	 *            CheckBox
	 * 
	 * @return a panel
	 */
	private JPanel createPanel(String string, JComponent checkBox) {
		JPanel panel = new ImagePanel();
		panel.setLayout(new BorderLayout());
		panel.setOpaque(false);

		JPanel innerPanel = new ImagePanel();
		innerPanel.add(checkBox);
		innerPanel.add(new JLabel(string, SwingConstants.LEFT));
		innerPanel.setOpaque(false);
		panel.add(innerPanel, BorderLayout.WEST);

		return panel;
	}

	/**
	 * Initialize listeners
	 */
	private void initListeners() {
		skillsCheckbox.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				config.setBoolean(PlayerAnalysisModule.SHOW_PLAYERCOMPARE,
						skillsCheckbox.isSelected());
			}
		});
	}

	/**
	 * Initializes the state of this instance.
	 */
	private void jbInit() {
		initListeners();
		JPanel mainPanel = new ImagePanel();
		mainPanel.setLayout(new GridLayout(12, 1));
		mainPanel.setOpaque(false);
		mainPanel.add(createPanel(HOVerwaltung.instance().getLanguageString("PlayerCompare"),
				skillsCheckbox));
		setLayout(new BorderLayout());
		setOpaque(false);
		add(mainPanel, BorderLayout.CENTER);
	}
}
