package core.option.db;

import core.db.user.User;
import core.gui.comp.NumericDocument;
import core.model.HOVerwaltung;
import core.util.GUIUtils;
import core.util.StringUtils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DatabaseUserEditDialog extends JDialog {

	private JButton saveButton;
	private JButton cancelButton;
	private JTextField nameTextField;
	private JTextField databaseNameTextField;
	private JTextField numberOfBackupsTextField;
	private User user;
	private boolean canceled = true;
	private final boolean isNew;
	private static final long serialVersionUID = -98754947290884048L;
	private JRadioButton ntTeamYes;
	private JRadioButton ntTeamNo;


	public DatabaseUserEditDialog(Window parent, User user, boolean isNew) {
		super(parent, ModalityType.APPLICATION_MODAL);
		this.isNew = isNew;
		this.user = user;
		initComponents();
		initData();
		addListeners();
		pack();
		GUIUtils.setLocationCenteredToComponent(this, parent);
		checkCanSave();
	}

	public DatabaseUserEditDialog(Window parent, User user) {
		this(parent, user, false);
	}

	public boolean isCanceled() {
		return this.canceled;
	}

	private void initData() {
		this.nameTextField.setText(this.user.getTeamName());
		this.databaseNameTextField.setText(this.user.getDbName());
		this.numberOfBackupsTextField.setText(String.valueOf(this.user.getBackupLevel()));
		if (this.user.isNtTeam())
			ntTeamYes.setSelected(true);
		else
			ntTeamNo.setSelected(true);
	}

	private void initComponents() {
		if (this.isNew) {
			setTitle(getLangStr("db.options.dlg.title.new"));
		} else {
			setTitle(getLangStr("db.options.dlg.title.edit"));
		}
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(getContentPanel(), gbc);

		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridy = 1;
		add(getButtonPanel(), gbc);
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.weightx = 1;
		gbc.insets = new Insets(6, 6, 6, 2);
		this.saveButton = new JButton();
		this.saveButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.save"));
		this.saveButton.setEnabled(false);
		buttonPanel.add(this.saveButton, gbc);

		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.insets = new Insets(6, 2, 6, 6);
		this.cancelButton = new JButton();
		this.cancelButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
		buttonPanel.add(this.cancelButton, gbc);

		GUIUtils.equalizeComponentSizes(this.saveButton, this.cancelButton);

		return buttonPanel;
	}

	private JPanel getContentPanel() {
		JPanel contentPanel = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		JLabel nameLabel = new JLabel(getLangStr("Benutzername"));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(4, 4, 4, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.0;
		contentPanel.add(nameLabel, gbc);

		this.nameTextField = new JTextField();
		int textFieldHeight = (int) nameTextField.getPreferredSize().getHeight();
		this.nameTextField.setMinimumSize(new Dimension(150, textFieldHeight));
		this.nameTextField.setPreferredSize(new Dimension(150, textFieldHeight));
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 4, 4);
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		contentPanel.add(this.nameTextField, gbc);

		JLabel databaseLocationLabel = new JLabel(getLangStr("db.options.dlg.label.dbName"));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(4, 4, 4, 2);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		contentPanel.add(databaseLocationLabel, gbc);

		this.databaseNameTextField = new JTextField();
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 4, 0);
		gbc.weightx = 1.0;
		contentPanel.add(this.databaseNameTextField, gbc);

		JLabel numberOfBackupsLabel = new JLabel(getLangStr("db.options.dlg.label.zips"));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(4, 4, 4, 2);
		gbc.gridwidth = 1;
		contentPanel.add(numberOfBackupsLabel, gbc);

		this.numberOfBackupsTextField = new JTextField();
		this.numberOfBackupsTextField.setDocument(new NumericDocument(3));
		this.numberOfBackupsTextField.setMinimumSize(new Dimension(40, textFieldHeight));
		this.numberOfBackupsTextField.setPreferredSize(new Dimension(40, textFieldHeight));
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 4, 4);
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		contentPanel.add(this.numberOfBackupsTextField, gbc);

		JLabel ntTeamLabel = new JLabel(getLangStr("db.options.dlg.label.nt"));
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(4, 4, 4, 2);
		gbc.gridwidth = 1;
		contentPanel.add(ntTeamLabel, gbc);

		ntTeamNo = new JRadioButton(HOVerwaltung.instance().getLanguageString("ls.button.no"));
		ntTeamNo.setSelected(true);
		ntTeamYes = new JRadioButton(HOVerwaltung.instance().getLanguageString("ls.button.yes"));
		ButtonGroup group = new ButtonGroup();
		group.add(ntTeamNo);
		group.add(ntTeamYes);
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 2, 4, 4);
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		contentPanel.add(this.ntTeamYes, gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		contentPanel.add(this.ntTeamNo, gbc);

		return contentPanel;
	}

	private void addListeners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				canceled = true;
			}
		});


		this.cancelButton.addActionListener(e -> {
			canceled = true;
			dispose();
		});

		this.saveButton.addActionListener(e -> {
//				user.setName(nameTextField.getText());
//				user.setDbName(databaseNameTextField.getText());
//				user.setNtTeam(ntTeamYes.isSelected());
//				user.setBackupLevel(Integer.parseInt(numberOfBackupsTextField.getText()));
//				user.fillUserInfos();
				user = new User(nameTextField.getText(), databaseNameTextField.getText(), Integer.parseInt(numberOfBackupsTextField.getText()),
						ntTeamYes.isSelected());
				canceled = false;
				dispose();
		});

		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkCanSave();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkCanSave();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkCanSave();
			}
		};

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				checkCanSave();
			}
		};

		this.nameTextField.getDocument().addDocumentListener(documentListener);
		this.databaseNameTextField.getDocument().addDocumentListener(documentListener);
		this.numberOfBackupsTextField.getDocument().addDocumentListener(documentListener);
		this.ntTeamNo.addActionListener(actionListener);
		this.ntTeamYes.addActionListener(actionListener);
	}

	private void openFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File(System.getProperty("user.dir")));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int state = fileChooser.showOpenDialog(this);
		if (state == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (file != null) {
				this.databaseNameTextField.setText(file.getAbsolutePath());
			}
		}
	}

	private void checkCanSave() {
		boolean canSave = StringUtils.isNumeric(this.numberOfBackupsTextField.getText())
				&& !StringUtils.isEmpty(this.nameTextField.getText())
				&& !StringUtils.isEmpty(this.databaseNameTextField.getText());
		if (!this.isNew) {
			this.saveButton.setEnabled(isChanged() && canSave);
		} else {
			this.saveButton.setEnabled(canSave);
		}
	}

	private boolean isChanged() {
		return (!this.user.getTeamName().equals(this.nameTextField.getText()) ||
				!this.user.getDbName().equals(this.databaseNameTextField.getText()) ||
				!String.valueOf(this.user.getBackupLevel()).equals(this.numberOfBackupsTextField.getText()) ||
				!(this.user.isNtTeam() == ntTeamYes.isSelected()));
	}

	private boolean checkDirectory() {
		String path = this.databaseNameTextField.getText();
		File file = new File(path);

		if (file.isFile()) {
			JOptionPane.showMessageDialog(this,
					getLangStr("db.options.dlg.error.locationIsNotDir"), getLangStr("Fehler"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (!file.exists()) {
			String msg = getLangStr("db.options.dlg.msg.dirDoesNotExist", file.getAbsolutePath())
					+ "\n" + getLangStr("db.options.dlg.question.createDir");
			int res = JOptionPane.showConfirmDialog(this, msg, HOVerwaltung.instance().getLanguageString("confirmation.title"), JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				if (!file.mkdirs()) {
					JOptionPane
							.showMessageDialog(
									this,
									getLangStr("db.options.dlg.error.dirNotCreated",
											file.getAbsolutePath()), getLangStr("Fehler"),
									JOptionPane.ERROR_MESSAGE);
					return false;
				}
				return true;
			} else {
				return false;
			}
		}

		if (file.exists() && !file.canRead()) {
			JOptionPane.showMessageDialog(this,
					getLangStr("db.options.dlg.error.dirNotReadable", file.getAbsolutePath()),
					getLangStr("Fehler"), JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (file.exists() && !file.canWrite()) {
			JOptionPane.showMessageDialog(this,
					getLangStr("db.options.dlg.error.dirNotWriteable", file.getAbsolutePath()),
					getLangStr("Fehler"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private String getLangStr(String key) {
		return HOVerwaltung.instance().getLanguageString(key);
	}

	private String getLangStr(String key, Object... values) {
		return HOVerwaltung.instance().getLanguageString(key, values);
	}
}
