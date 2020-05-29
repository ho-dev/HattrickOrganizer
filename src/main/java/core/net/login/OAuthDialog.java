package core.net.login;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.BrowserLauncher;
import core.util.HOLogger;
import core.util.Helper;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class OAuthDialog extends JDialog {

	private static final long serialVersionUID = 1798304851624958795L;

	private HOMainFrame m_clMainFrame;
	private JButton m_jbOK = new JButton();
	private JButton m_jbBrowse = new JButton();
	private JButton m_jbCancel = new JButton();
	private JTextField m_jtfAuthString = new JTextField();
	private JTextField m_jtfAuthURL = new JTextField();
	private String m_sUserURL;
	private boolean m_bUserCancel = false;
	private boolean m_bFirstTry = true;
	private OAuthService m_service;
	private Token m_AccessToken;
	private Token m_RequestToken;
	private String scopes = "";

	public OAuthDialog(HOMainFrame mainFrame, OAuthService service, String scope) {
		super(mainFrame, HOVerwaltung.instance().getLanguageString(
				"oauth.Title"), true);

		this.m_clMainFrame = mainFrame;
		this.m_service = service;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Hard code set_matchorder due to authorization woes.
		scopes = "&scope=" + "set_matchorder";
		
		obtainUserURL();
		initComponents();
		addListeners();
	}

	private void obtainUserURL() {
		try {
			m_RequestToken = m_service.getRequestToken();
			m_sUserURL = m_service.getAuthorizationUrl(m_RequestToken);
			m_sUserURL += scopes;
		} catch (Exception e) {
			HOLogger.instance().error(getClass(),
					"Exception in obtainUserCode: " + e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(),
					"Error obtaining URL", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void doAuthorize() {
		Verifier verifier = new Verifier(m_jtfAuthString.getText().trim());
		try {
			m_AccessToken = m_service.getAccessToken(m_RequestToken, verifier);
			UserParameter.instance().AccessToken = Helper
					.cryptString(m_AccessToken.getToken());
			UserParameter.instance().TokenSecret = Helper
					.cryptString(m_AccessToken.getSecret());

		} catch (Exception e) {
			HOLogger.instance().error(getClass(),
					"Exception in doAuthorize: " + e.getMessage());
		}
		m_bFirstTry = false;
		this.dispose();
	}

	public boolean getUserCancel() {
		return m_bUserCancel;
	}

	public Token getAccessToken() {
		return m_AccessToken;
	}

	@Override
	public void setVisible(boolean b) {
		if (m_bFirstTry == false) {
			JOptionPane.showMessageDialog(null, HOVerwaltung.instance()
					.getLanguageString("oauth.FailedTry"), HOVerwaltung
					.instance().getLanguageString("oauth.FailedTryHeader"),
					JOptionPane.INFORMATION_MESSAGE);
			m_jtfAuthString.setText("");
		}
		super.setVisible(b);
	}

	private void addListeners() {
		ActionListener actionListener = new ActionListener() {

			@Override
			public final void actionPerformed(ActionEvent actionEvent) {
				if (actionEvent.getSource().equals(m_jbOK)) {
					doAuthorize();
				} else if (actionEvent.getSource().equals(m_jbBrowse)) {
					try {
						BrowserLauncher.openURL(m_sUserURL);
					} catch (Exception e) {
						HOLogger.instance().log(getClass(), e);
					}
				} else if (actionEvent.getSource().equals(m_jbCancel)) {
					m_bUserCancel = true;
					dispose();
				}
			}
		};
		m_jbBrowse.addActionListener(actionListener);
		m_jbOK.addActionListener(actionListener);
		m_jbCancel.addActionListener(actionListener);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				m_jtfAuthString.requestFocusInWindow();
			}

		});
	}

	private void initComponents() {
		setContentPane(new ImagePanel(new FlowLayout()));

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridBagLayout());
		panel.setSize(370, 470);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(2, 2, 2, 2);

		JLabel infoLabel = new JLabel();
		infoLabel.setText(HOVerwaltung.instance().getLanguageString(
				"oauth.Intro"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		panel.add(infoLabel, constraints);

		JPanel spacer = new JPanel();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		spacer.setOpaque(false);
		spacer.setPreferredSize(new Dimension(25, 10));
		panel.add(spacer, constraints);

		JLabel authLink = new JLabel();
		authLink.setText(HOVerwaltung.instance().getLanguageString(
				"oauth.URLOrButton"));

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		panel.add(authLink, constraints);

		m_jbBrowse.setText(HOVerwaltung.instance().getLanguageString(
				"oauth.OpenUrl"));
		m_jbBrowse.setSize(170, 35);
		m_jbBrowse.setEnabled(true);
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		panel.add(m_jbBrowse, constraints);

		m_jtfAuthURL.setText(m_sUserURL);
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		panel.add(m_jtfAuthURL, constraints);

		JLabel authInput = new JLabel();
		authInput.setText(HOVerwaltung.instance().getLanguageString(
				"oauth.EnterCode"));
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		panel.add(authInput, constraints);

		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		panel.add(m_jtfAuthString, constraints);

		m_jbOK.setText(HOVerwaltung.instance().getLanguageString("ls.button.ok"));
		m_jbOK.setSize(170, 35);
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		panel.add(m_jbOK, constraints);

		m_jbCancel.setText(HOVerwaltung.instance().getLanguageString(
				"ls.button.cancel"));
		m_jbCancel.setEnabled(true);
		constraints.gridx = 1;
		constraints.gridy = 7;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		m_jbCancel.setSize(170, 35);
		panel.add(m_jbCancel, constraints);

		panel.setBorder(new javax.swing.border.EtchedBorder());

		getContentPane().add(panel);

		this.setSize(400, 500);
		pack();

		Dimension size = m_clMainFrame.getToolkit().getScreenSize();
		if (size.width > this.getSize().width) { // open dialog in the middle of
													// the screen
			this.setLocation((size.width / 2) - (this.getSize().width / 2),
					(size.height / 2) - (this.getSize().height / 2));
		}
	}
}
