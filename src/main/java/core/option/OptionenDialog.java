package core.option;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.module.ModuleConfigPanel;
import core.module.ModuleManager;
import core.module.config.ModuleConfig;
import core.util.Helper;
import core.util.Updater;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * A dialog for all HO options/preferences
 */
public class OptionenDialog extends JDialog {

	private ReleaseChannelPanel m_jpReleaseChannelsPanel;
	private JButton saveButton;
	private JButton cancelButton;
	private UserColorsPanel userColorsPanel;

	public OptionenDialog(JFrame owner) {
		super(owner, HOVerwaltung.instance().getLanguageString("ls.menu.file.preferences"), true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
		addListeners();
	}

	private void addListeners() {

		saveButton.addActionListener(e -> {
			save();
			OptionManager.deleteInstance();
			dispose();
		});

		cancelButton.addActionListener(e -> {
			UserParameter.deleteTempParameter();
			ModuleManager.instance().clearTemp();
			OptionManager.deleteInstance();
			dispose();
		});

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (OptionManager.instance().isOptionsChanged()){
					// Warning user
					var choice = JOptionPane.showConfirmDialog(null,
							HOVerwaltung.instance().getLanguageString("ls.options.changed.save.question"),
							HOVerwaltung.instance().getLanguageString("ls.options.warning"),
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (choice == JOptionPane.YES_OPTION){
						save();
					}
					else if (choice == JOptionPane.CANCEL_OPTION){
						return; // do not close window
					}
				}
				dispose();	// close window
			}
		});
	}

	private void initComponents() {
		setContentPane(new ImagePanel());
		getContentPane().setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();

		// Misc
		GeneralSettingsPanel m_jpSonstigeOptionen = new GeneralSettingsPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Verschiedenes"), new JScrollPane(m_jpSonstigeOptionen));

		// Lineup settings
		LineupSettingsPanel m_jpLineupSettings = new LineupSettingsPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Aufstellung"), new JScrollPane(m_jpLineupSettings));

		// Modules
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Module"), new JScrollPane(new ModuleConfigPanel()));

		// Formula
		FormelPanel m_jpFormeln = new FormelPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Formeln"), new JScrollPane(m_jpFormeln));

		// Training
		TrainingPreferencesPanel m_jpTrainingsOptionen = new TrainingPreferencesPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Training"), new JScrollPane(m_jpTrainingsOptionen));

		// Release Channels
		m_jpReleaseChannelsPanel = new ReleaseChannelPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("options.tabtitle.release_channels"), new JScrollPane(m_jpReleaseChannelsPanel));

		// Columns
		UserColumnsPanel m_jpUserColumns = new UserColumnsPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("columns"), new JScrollPane(m_jpUserColumns));

		// Colors
		userColorsPanel = new UserColorsPanel();
        tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("colors"), new JScrollPane(userColorsPanel));

		// Colors

		// Tabs der plugins
		for (int i = 0; (i < HOMainFrame.instance().getOptionPanelNames().size())
				&& (i < HOMainFrame.instance().getOptionPanels().size()); ++i) {
			tabbedPane.addTab(HOMainFrame.instance().getOptionPanelNames().get(i),
					HOMainFrame.instance().getOptionPanels().get(i));
		}

		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		ImagePanel buttonPanel = new ImagePanel();
		// Add Buttons
		saveButton = new JButton();
		saveButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.save"));
		saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
		buttonPanel.add(saveButton);
		cancelButton = new JButton();
		cancelButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
		buttonPanel.add(cancelButton);

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		if (HOMainFrame.instance().getToolkit().getScreenSize().height >= 700) {
			setSize(new Dimension(540, 700));
		} else {
			setSize(new Dimension(540,
					HOMainFrame.instance().getToolkit().getScreenSize().height - 50));
		}

		Dimension size = HOMainFrame.instance().getToolkit().getScreenSize();
		if (size.width > this.getSize().width) {
			// Mittig positionieren
			setLocation((size.width / 2) - (this.getSize().width / 2), (size.height / 2)
					- (getSize().height / 2));
		}

//		setResizable(false);
	}

	private void save() {
		// Store user colors before theme setting in user parameters might change
		userColorsPanel.storeChangedColorSettings();

		UserParameter.saveTempParameter();
		ModuleConfig.instance().save();
		ModuleManager.instance().saveTemp();

		//save release channel information in java store
		Updater.instance().saveReleaseChannelPreference(m_jpReleaseChannelsPanel.getRc());

		if (OptionManager.instance().isRestartNeeded()) {
			Helper.showMessage(OptionenDialog.this,
					HOVerwaltung.instance().getLanguageString("NeustartErforderlich"), "",
					JOptionPane.INFORMATION_MESSAGE);
		}
		if (OptionManager.instance().isReInitNeeded()) {
			HOMainFrame.instance().resetInformation();
			RefreshManager.instance().doReInit();
			HOMainFrame.instance().setInformationCompleted();
		}
	}
}
