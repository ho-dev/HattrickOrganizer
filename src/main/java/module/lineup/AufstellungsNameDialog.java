// %771549764:de.hattrickorganizer.gui.lineup%
package module.lineup;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.model.AufstellungCBItem;
import core.model.HOVerwaltung;
import core.util.GUIUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Erfragt einen Namen für die zu Speichernde Aufstellung und fügt sie in die
 * Datenbank ein, wenn gewünscht
 */
final class AufstellungsNameDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 7318780000118008882L;
	private Lineup lineup;
	private JButton cancelButton;
	private JButton okButton;
	private JTextField nameTextField;
	private boolean canceled = true;

	protected AufstellungsNameDialog(JFrame owner, String aufstellungsName, Lineup aufstellung,
			int x, int y) {
		super(owner, true);
		this.lineup = aufstellung;
		initComponents(aufstellungsName);
		pack();
		GUIUtils.setLocationCenteredToComponent(this, owner);
	}

	private void initComponents(String aufstellungsName) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(HOVerwaltung.instance().getLanguageString("AufstellungSpeichern"));

		JPanel contentPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weighty = 1.0;

		JLabel nameLabel = new JLabel(HOVerwaltung.instance().getLanguageString("Name"));
		contentPanel.add(nameLabel, gbc);

		this.nameTextField = new JTextField();
		Dimension size = new Dimension(200, (int) this.nameTextField.getPreferredSize().getHeight());
		this.nameTextField.setMinimumSize(size);
		this.nameTextField.setPreferredSize(size);
		gbc.insets = new Insets(8, 2, 8, 8);
		gbc.gridx = 1;
		contentPanel.add(this.nameTextField, gbc);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 8, 8, 2);
		gbc.anchor = GridBagConstraints.NORTHEAST;
		this.okButton = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.save"));
		gbc.weightx = 1.0;
		buttonPanel.add(this.okButton, gbc);

		gbc.gridx = 1;
		gbc.weightx = 0.0;
		gbc.insets = new Insets(2, 2, 8, 8);
		this.cancelButton = new JButton(HOVerwaltung.instance().getLanguageString(
				"ls.button.cancel"));
		buttonPanel.add(this.cancelButton, gbc);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		if (checkName(aufstellungsName, false)) {
			this.nameTextField.setText(aufstellungsName);
		}
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(okButton)) {
			if (!checkName(nameTextField.getText(), false)) {
				// Name nicht erlaubt / Keine Meldung
				return;
			}

			if (checkName(nameTextField.getText(), true)) {
				HOMainFrame
						.instance()
						.getInfoPanel()
						.setLangInfoText(
								HOVerwaltung.instance().getLanguageString("Aufstellung") + " "
										+ nameTextField.getText() + " "
										+ HOVerwaltung.instance().getLanguageString("gespeichert"));
				AufstellungsVergleichHistoryPanel.setAngezeigteAufstellung(new AufstellungCBItem(
						nameTextField.getText(), lineup.duplicate()));
				saveLineup(lineup, nameTextField.getText());
				setVisible(false);

			} else {
				final int value = JOptionPane.showConfirmDialog(this, HOVerwaltung.instance()
						.getLanguageString("Aufstellung_NameSchonVorhanden"),
						HOVerwaltung.instance().getLanguageString("confirmation.title"),
						JOptionPane.YES_NO_OPTION);

				if (value == JOptionPane.YES_OPTION) {
					HOMainFrame
							.instance()
							.getInfoPanel()
							.setLangInfoText(
									HOVerwaltung.instance().getLanguageString("Aufstellung")
											+ " "
											+ nameTextField.getText()
											+ " "
											+ HOVerwaltung.instance().getLanguageString(
													"gespeichert"));
					AufstellungsVergleichHistoryPanel
							.setAngezeigteAufstellung(new AufstellungCBItem(
									nameTextField.getText(), lineup.duplicate()));
					saveLineup(lineup, nameTextField.getText());

					// Should prepare it for the new lineup
					HOMainFrame.instance().getAufstellungsPanel().update();
					setVisible(false);
				}
			}
		} else if (actionEvent.getSource().equals(cancelButton)) {
			this.canceled = true;
			setVisible(false);
		}
	}

	public boolean isCanceled() {
		return this.canceled;
	}

	// Name noch nicht in DB oder Aktuelle Aufstellung
	private boolean checkName(String name, boolean dbcheck) {
		List<String> aufstellungsNamen = new ArrayList<String>();

		// nicht schon vorhanden
		if (dbcheck) {
			aufstellungsNamen = DBManager.instance().getAufstellungsListe(Lineup.NO_HRF_VERBINDUNG);
		}

		// nicht Aktuelle Aufstellung
		aufstellungsNamen.add(HOVerwaltung.instance().getLanguageString("AktuelleAufstellung"));
		aufstellungsNamen.add(HOVerwaltung.instance().getLanguageString("LetzteAufstellung"));

		// nicht HO!
		aufstellungsNamen.add(Lineup.DEFAULT_NAME);

		return (!(aufstellungsNamen.contains(name)));
	}

	private void saveLineup(Lineup lineup, String name) {
		lineup.save(nameTextField.getText());
		HOMainFrame.instance().getAufstellungsPanel().getAufstellungsPositionsPanel()
				.exportOldLineup(name);
		this.canceled = false;
	}
}
