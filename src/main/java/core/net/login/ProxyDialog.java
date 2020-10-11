// %2032796658:de.hattrickorganizer.gui.login%
package core.net.login;

import core.gui.HOMainFrame;
import core.gui.comp.NumericDocument;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.net.MyConnector;
import core.util.GUIUtils;
import core.util.StringUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * ProxyDialog
 */
public class ProxyDialog extends JDialog {
	private static final long serialVersionUID = -2112562621278224332L;
	private HOMainFrame m_clMainFrame;
	private JButton cancelButton = new JButton();
	private JButton okButton = new JButton();
	private JCheckBox useProxyCheckBox = new JCheckBox();
	private JCheckBox useProxyAuthCheckBox = new JCheckBox();
	private JPasswordField proxyPasswordField = new JPasswordField();
	private JTextField proxyAuthNameTextField = new JTextField();
	private JTextField proxyHostTextField = new JTextField();
	private JTextField proxyPortTextField = new JTextField();

	public ProxyDialog(HOMainFrame mainFrame) {
		super(mainFrame, "Proxy", true);

		this.m_clMainFrame = mainFrame;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
		addListeners();

		proxyPortTextField.setText(UserParameter.instance().ProxyPort);
		proxyHostTextField.setText(UserParameter.instance().ProxyHost);
		useProxyCheckBox.setSelected(UserParameter.instance().ProxyAktiv);
		proxyHostTextField.setEnabled(useProxyCheckBox.isSelected());
		proxyPortTextField.setEnabled(useProxyCheckBox.isSelected());
		proxyAuthNameTextField.setText(UserParameter.instance().ProxyAuthName);
		proxyPasswordField.setText(UserParameter.instance().ProxyAuthPassword);
		useProxyAuthCheckBox
				.setSelected(UserParameter.instance().ProxyAuthAktiv);
		useProxyAuthCheckBox.setEnabled(UserParameter.instance().ProxyAktiv);
		proxyAuthNameTextField.setEnabled(useProxyAuthCheckBox.isSelected()
				&& useProxyAuthCheckBox.isEnabled());
		proxyPasswordField.setEnabled(useProxyAuthCheckBox.isSelected()
				&& useProxyAuthCheckBox.isEnabled());

		pack();
		setVisible(true);
		dispose();
	}

	/**
	 * Komponenten des Panels initial setzen
	 */
	private void initComponents() {
		HOVerwaltung hov = HOVerwaltung.instance();

		setContentPane(new ImagePanel());
		getContentPane().setLayout(new BorderLayout());

		Dimension textFieldSize = new Dimension(155, (int) proxyHostTextField
				.getPreferredSize().getHeight());
		proxyHostTextField.setMinimumSize(textFieldSize);
		proxyHostTextField.setPreferredSize(textFieldSize);
		GUIUtils.equalizeComponentSizes(proxyAuthNameTextField,
				proxyHostTextField, proxyPasswordField, proxyPortTextField);

		// Proxy Daten
		JPanel panel = new ImagePanel(new GridBagLayout());

		useProxyCheckBox
				.setToolTipText(hov.getLanguageString("tt_Login_Proxy"));
		useProxyCheckBox.setText(hov.getLanguageString("ProxyAktiv"));
		useProxyCheckBox.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(8, 8, 4, 8);
		panel.add(useProxyCheckBox, gbc);

		JLabel label = new JLabel(hov.getLanguageString("ProxyHost"));
		label.setToolTipText(hov.getLanguageString("tt_Login_ProxyHost"));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(4, 8, 4, 2);
		panel.add(label, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(4, 2, 4, 8);
		panel.add(proxyHostTextField, gbc);

		label = new JLabel(hov.getLanguageString("ProxyPort"));
		label.setToolTipText(hov.getLanguageString("tt_Login_ProxyPort"));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(4, 8, 4, 2);
		panel.add(label, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(4, 2, 4, 8);
		proxyPortTextField.setDocument(new NumericDocument(5, false));
		panel.add(proxyPortTextField, gbc);

		// Auth
		useProxyAuthCheckBox.setToolTipText(hov
				.getLanguageString("tt_Login_ProxyAuth"));
		useProxyAuthCheckBox.setText(hov.getLanguageString("ProxyAuthAktiv"));
		useProxyAuthCheckBox.setOpaque(false);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(8, 8, 4, 8);
		panel.add(useProxyAuthCheckBox, gbc);

		label = new JLabel(hov.getLanguageString("ProxyAuthName"));
		label.setToolTipText(hov.getLanguageString("tt_Login_ProxyAuthName"));
		label.setLocation(10, 155);
		label.setSize(185, 25);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(4, 8, 4, 2);
		panel.add(label, gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.insets = new Insets(4, 2, 4, 8);
		panel.add(proxyAuthNameTextField, gbc);

		label = new JLabel(hov.getLanguageString("ProxyAuthPassword"));
		label.setToolTipText(hov
				.getLanguageString("tt_Login_ProxyAuthPassword"));
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4, 8, 4, 2);
		panel.add(label, gbc);

		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.insets = new Insets(4, 2, 4, 8);
		panel.add(proxyPasswordField, gbc);

		panel.setBorder(new TitledBorder(hov.getLanguageString("Proxydaten")));
		getContentPane().add(panel, BorderLayout.CENTER);

		// Buttons
		JPanel buttonPanel = new ImagePanel(new GridBagLayout());
		okButton.setToolTipText(hov.getLanguageString("tt_Login_Anmelden"));
		okButton.setText(hov.getLanguageString("ls.button.save"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(6, 6, 6, 4);
		buttonPanel.add(okButton, gbc);

		cancelButton
				.setToolTipText(hov.getLanguageString("tt_Login_Abbrechen"));
		cancelButton.setText(hov.getLanguageString("ls.button.cancel"));
		gbc.gridx = 1;
		gbc.weightx = 0.0;
		gbc.insets = new Insets(6, 4, 6, 6);
		buttonPanel.add(cancelButton, gbc);

		GUIUtils.equalizeComponentSizes(okButton, cancelButton);

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		Dimension size = m_clMainFrame.getToolkit().getScreenSize();
		if (size.width > this.getSize().width) {
			// Mittig positionieren
			this.setLocation((size.width / 2) - (this.getSize().width / 2),
					(size.height / 2) - (this.getSize().height / 2));
		}

		HOMainFrame.instance().setWaitInformation(0);
	}

	private void addListeners() {
		KeyListener kl = new KeyAdapter() {
			@Override
			public final void keyReleased(KeyEvent keyEvent) {
				// Return = ok
				if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
						&& okButton.isEnabled()) {
					okButton.doClick();
				}
			}
		};
		proxyHostTextField.addKeyListener(kl);
		proxyPortTextField.addKeyListener(kl);

		FocusListener fl = new FocusAdapter() {
			@Override
			public final void focusGained(FocusEvent focusEvent) {
				// Selektiert den Inhalt des Textfeldes beim eintreffen
				if (focusEvent.getSource() instanceof JTextField) {
					((JTextField) focusEvent.getSource()).selectAll();
				}
			}
		};
		proxyHostTextField.addFocusListener(fl);
		proxyPortTextField.addFocusListener(fl);
		proxyAuthNameTextField.addFocusListener(fl);
		proxyPasswordField.addFocusListener(fl);

		ActionListener al = new ActionListener() {

			@Override
			public final void actionPerformed(ActionEvent actionEvent) {
				if (actionEvent.getSource().equals(okButton)) {
					saveSettings();
					setVisible(false);
				} else if (actionEvent.getSource().equals(useProxyCheckBox)) {
					proxyHostTextField
							.setEnabled(useProxyCheckBox.isSelected());
					proxyPortTextField
							.setEnabled(useProxyCheckBox.isSelected());
					useProxyAuthCheckBox.setEnabled(useProxyCheckBox
							.isSelected());
					proxyAuthNameTextField.setEnabled(useProxyAuthCheckBox
							.isEnabled() && useProxyAuthCheckBox.isSelected());
					proxyPasswordField.setEnabled(useProxyAuthCheckBox
							.isEnabled() && useProxyAuthCheckBox.isSelected());
				} else if (actionEvent.getSource().equals(useProxyAuthCheckBox)) {
					proxyAuthNameTextField.setEnabled(useProxyAuthCheckBox
							.isSelected());
					proxyPasswordField.setEnabled(useProxyAuthCheckBox
							.isSelected());
				} else {
					// Beenden
					dispose();
				}
			}
		};
		useProxyCheckBox.addActionListener(al);
		okButton.addActionListener(al);
		useProxyAuthCheckBox.addActionListener(al);
		cancelButton.addActionListener(al);
	}

	/**
	 * Login versuchen
	 */
	private void saveSettings() {
		ProxySettings settings = new ProxySettings();
		settings.setUseProxy(useProxyCheckBox.isSelected());
		settings.setProxyHost(proxyHostTextField.getText());
		if (!StringUtils.isEmpty(proxyPortTextField.getText())) {
			settings.setProxyPort(Integer.parseInt(proxyPortTextField.getText()));
		}
		settings.setAuthenticationNeeded(useProxyAuthCheckBox.isSelected());
		settings.setUsername(proxyAuthNameTextField.getText());
		settings.setPassword(new String(proxyPasswordField.getPassword()));

		MyConnector.instance().enableProxy(settings);

		UserParameter.instance().ProxyAktiv = useProxyCheckBox.isSelected();
		UserParameter.instance().ProxyHost = proxyHostTextField.getText();
		UserParameter.instance().ProxyPort = proxyPortTextField.getText();
		UserParameter.instance().ProxyAuthAktiv = useProxyAuthCheckBox
				.isSelected();
		UserParameter.instance().ProxyAuthName = proxyAuthNameTextField
				.getText();
		UserParameter.instance().ProxyAuthPassword = new String(
				proxyPasswordField.getPassword());
	}
}
