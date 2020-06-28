// %198737965:de.hattrickorganizer.gui.menu.option%
package core.option;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.module.ModuleConfigPanel;
import core.module.ModuleManager;
import core.module.config.ModuleConfig;
import core.net.login.LoginWaitDialog;
import core.util.Helper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Ein Dialog mit allen Optionen für HO
 */
public class OptionenDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private FormelPanel m_jpFormeln;
	private SonstigeOptionenPanel m_jpSonstigeOptionen;
	private TrainingsOptionenPanel m_jpTrainingsOptionen;
	private UserColumnsPanel m_jpUserColumns;
	private ReleaseChannelPanel m_jpReleaseChannelsPanel;
	private JButton saveButton;
	private JButton cancelButton;
	private ImagePanel buttonPanel;

	public OptionenDialog(JFrame owner) {
		super(owner, HOVerwaltung.instance().getLanguageString("ls.menu.file.preferences"), true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
		addListeners();
	}

	private void addListeners() {

		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				OptionManager.deleteInstance();
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UserParameter.deleteTempParameter();
				ModuleManager.instance().clearTemp();
				OptionManager.deleteInstance();
				dispose();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				save();
			}
		});
	}

	private void initComponents() {
		setContentPane(new ImagePanel());
		getContentPane().setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();

		// Misc
		m_jpSonstigeOptionen = new SonstigeOptionenPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Verschiedenes"),
				new JScrollPane(m_jpSonstigeOptionen));

		// Modules
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Module"), new JScrollPane(
				new ModuleConfigPanel()));

		// Formula
		m_jpFormeln = new FormelPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Formeln"), new JScrollPane(
				m_jpFormeln));

		// Training
		m_jpTrainingsOptionen = new TrainingsOptionenPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("Training"), new JScrollPane(
				m_jpTrainingsOptionen));

		// Release Channels
		m_jpReleaseChannelsPanel = new ReleaseChannelPanel();
		tabbedPane.addTab(
				core.model.HOVerwaltung.instance()
						.getLanguageString("options.tabtitle.release_channels"), new JScrollPane(
						m_jpReleaseChannelsPanel));

		// Columns
		m_jpUserColumns = new UserColumnsPanel();
		tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("columns"), new JScrollPane(
				m_jpUserColumns));

		// Tabs der plugins
		for (int i = 0; (i < HOMainFrame.instance().getOptionPanelNames().size())
				&& (i < HOMainFrame.instance().getOptionPanels().size()); ++i) {
			tabbedPane.addTab(HOMainFrame.instance().getOptionPanelNames().get(i).toString(),
					HOMainFrame.instance().getOptionPanels().get(i));
		}

		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		buttonPanel = new ImagePanel();
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
			setSize(new Dimension(450, 700));
		} else {
			setSize(new Dimension(450,
					HOMainFrame.instance().getToolkit().getScreenSize().height - 50));
		}

		Dimension size = HOMainFrame.instance().getToolkit().getScreenSize();
		if (size.width > this.getSize().width) {
			// Mittig positionieren
			setLocation((size.width / 2) - (this.getSize().width / 2), (size.height / 2)
					- (getSize().height / 2));
		}

		setResizable(false);
	}

	private void save() {
		UserParameter.saveTempParameter();
		ModuleConfig.instance().save();
		ModuleManager.instance().saveTemp();
		if (OptionManager.instance().isRestartNeeded()) {
			Helper.showMessage(OptionenDialog.this,
					HOVerwaltung.instance().getLanguageString("NeustartErforderlich"), "",
					JOptionPane.INFORMATION_MESSAGE);
		}
		if (OptionManager.instance().isReInitNeeded()) {
			LoginWaitDialog waitdialog = new LoginWaitDialog(HOMainFrame.instance());
			waitdialog.setVisible(true);
			RefreshManager.instance().doReInit();
			waitdialog.setVisible(false);
		}
	}
}
