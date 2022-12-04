package core.option.db;

import core.db.DBManager;
import core.db.user.User;
import core.db.user.UserManager;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.util.GUIUtils;
import core.util.Helper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.io.Serial;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class UserAdministrationDialog extends JDialog {

	@Serial
	private static final long serialVersionUID = 3687310660515124201L;
	private MyTable table;
	private JButton newButton;
	private JButton editButton;
	private JButton	moveUpButton;
	private JButton	moveDownButton;
	private JButton deleteButton;
	private JButton closeButton;

	public UserAdministrationDialog(Window parent) {
		super(parent, ModalityType.APPLICATION_MODAL);
		initComponents();
		addListeners();
		pack();
		GUIUtils.setLocationCenteredToComponent(this, parent);
	}

	private void addListeners() {
		this.table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				boolean enable = (table.getSelectedRow() > -1);
				editButton.setEnabled(enable);
				deleteButton.setEnabled(enable);
				moveDownButton.setEnabled(enable && table.getSelectedRow() != table.getRowCount() - 1);
				moveUpButton.setEnabled(enable && table.getSelectedRow() != 0);
			}
		});

		this.newButton.addActionListener(e -> {
			User newUser = User.createDefaultUser();
			UserEditDialog dlg = new UserEditDialog(UserAdministrationDialog.this, newUser, true);
			dlg.setVisible(true);
			if (!dlg.isCanceled()) {
				UserManager.instance().addUser(dlg.getUser());
				saveAndReload();
			}
		});

		this.editButton.addActionListener(e -> {
			var editUser = getSelectedUser();
			if ( editUser == null) return;

			var oldDbName = editUser.getDbName();
			var userManager = UserManager.instance();
			var isLoggedIn = editUser == userManager.getCurrentUser();
			UserEditDialog dlg = new UserEditDialog(UserAdministrationDialog.this, editUser);
			dlg.setVisible(true);
			if (!dlg.isCanceled()) {
				var newDbDirectory = new File(userManager.getDbParentFolder(), editUser.getDbName());
				// if edited db folder exist - use it
				if (!newDbDirectory.exists()) {
					var oldDbDirectory = new File(userManager.getDbParentFolder(), oldDbName);
					if (oldDbDirectory.exists()) {
						boolean closeDBConnection = DBManager.instance() != null && isLoggedIn;
						if (closeDBConnection) {
							// close db connection if current user is edited
							DBManager.instance().disconnect();
						}

						// else rename old db folder to new name
						var success = oldDbDirectory.renameTo(newDbDirectory);
						if (!success) {
							JOptionPane.showMessageDialog(null,
									HOVerwaltung.instance().getLanguageString("ls.useradministration.rename.error_message"),
									"Error",
									JOptionPane.ERROR_MESSAGE);
							editUser.setDbName(oldDbName); // reset db name
						}

						if (closeDBConnection) {
							// reopen database connection
							DBManager.instance();
						}
					}
				} else if (isLoggedIn) {
					// restart HO to use new database
					Helper.showMessage(null,
							HOVerwaltung.instance().getLanguageString("NeustartErforderlich"), "",
							JOptionPane.INFORMATION_MESSAGE);
				}
				saveAndReload();
			}
		});

		this.moveDownButton.addActionListener(e -> {
			var selectedIndex = this.table.getSelectedRow();
			UserManager.instance().swapUsers(selectedIndex, selectedIndex+1);
			saveAndReload();
			this.table.setRowSelectionInterval(selectedIndex+1, selectedIndex+1);
		});

		this.moveUpButton.addActionListener(e -> {
			var selectedIndex = this.table.getSelectedRow();
			UserManager.instance().swapUsers(selectedIndex, selectedIndex-1);
			saveAndReload();
			this.table.setRowSelectionInterval(selectedIndex-1, selectedIndex-1);
		});

		this.deleteButton.addActionListener(e -> deleteSelectedUser());

		this.closeButton.addActionListener(e -> dispose());
	}

	private void saveAndReload() {
		UserManager.instance().save();
		((MyTableModel) this.table.getModel()).fireTableDataChanged();
	}

	private void initComponents() {
		setTitle(HOVerwaltung.instance().getLanguageString("ls.menu.file.database.dbuseradministration"));
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		this.table = new MyTable(new MyTableModel());
		this.table.setPreferredSize(new Dimension(400, 150));
		this.table.setPreferredScrollableViewportSize(new Dimension(400, 150));
		add(new JScrollPane(this.table), gbc);

		gbc.gridx = 1;
		add(getButtonPanel(), gbc);

		this.closeButton = new JButton();
		this.closeButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.close"));
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(6, 6, 6, 6);
		add(this.closeButton, gbc);
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		this.newButton = new JButton();
		this.newButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.add"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(4, 4, 2, 4);
		buttonPanel.add(this.newButton, gbc);

		this.editButton = new JButton();
		this.editButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.edit"));
		this.editButton.setEnabled(false);
		gbc.gridy++;
		gbc.insets = new Insets(2, 4, 2, 4);
		buttonPanel.add(this.editButton, gbc);


		this.moveUpButton = new JButton();
		this.moveUpButton.setIcon(ThemeManager.getIcon(HOIconName.MOVE_UP));
		this.moveUpButton.setEnabled(false);
		gbc.gridy++;
		buttonPanel.add(this.moveUpButton, gbc);

		this.moveDownButton = new JButton();
		this.moveDownButton.setIcon(ThemeManager.getIcon(HOIconName.MOVE_DOWN));
		this.moveDownButton.setEnabled(false);
		gbc.gridy++;
		buttonPanel.add(this.moveDownButton, gbc);

		this.deleteButton = new JButton();
		this.deleteButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.delete"));
		this.deleteButton.setEnabled(false);
		gbc.gridy++;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(2, 4, 4, 4);
		buttonPanel.add(this.deleteButton, gbc);

		GUIUtils.equalizeComponentSizes(this.newButton, this.editButton, this.deleteButton);

		return buttonPanel;
	}

	private User getSelectedUser() {
		if (this.table.getSelectedRow() != -1) {
			int row = table.convertRowIndexToModel(this.table.getSelectedRow());
			return ((MyTableModel) this.table.getModel()).getSelectedUser(row);
		}
		return null;
	}

	private void deleteSelectedUser() {
		User user = getSelectedUser();
		if (user != null) {
			int res = JOptionPane.showConfirmDialog(this, HOVerwaltung.instance()
					.getLanguageString("db.options.dlg.delete.question", user.getTeamName()),
					HOVerwaltung.instance().getLanguageString("confirmation.title"),
					JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				UserManager.instance().getAllUser().remove(user);
				saveAndReload();
			}
		}
	}

	private static class MyTable extends JTable {

		@Serial
		private static final long serialVersionUID = 7239292644198908535L;

		public MyTable(TableModel dm) {
			super(dm);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	private static class MyTableModel extends AbstractTableModel {

		@Serial
		private static final long serialVersionUID = 1975023278731081088L;
		private final String[] columnNames = new String[] {
				HOVerwaltung.instance().getLanguageString("teamSelect.teamName"),
				HOVerwaltung.instance().getLanguageString("db.options.dlg.label.dbName"),
				HOVerwaltung.instance().getLanguageString("db.options.dlg.label.zips"),
				HOVerwaltung.instance().getLanguageString("db.options.dlg.label.nt")
		};

		@Override
		public Object getValueAt(int row, int column) {
			User user = UserManager.instance().getAllUser().get(row);
			switch (column) {
			case 0:
				return user.getTeamName();
			case 1:
				return user.getDbName();
			case 2:
				return user.getNumberOfBackups();
			case 3:
				if (user.isNtTeam())
					return HOVerwaltung.instance().getLanguageString("ls.button.yes");
				else
					return HOVerwaltung.instance().getLanguageString("ls.button.no");
			}
			return null;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public int getRowCount() {
			return UserManager.instance().getAllUser().size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		public User getSelectedUser(int row) {
			return UserManager.instance().getAllUser().get(row);
		}
	}
}
