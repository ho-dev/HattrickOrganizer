package core.db.backup;

import core.db.user.User;
import core.db.user.UserManager;
import core.file.ExampleFileFilter;
import core.file.ZipHelper;
import core.gui.comp.panel.ImagePanel;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Backupmanagement dialog
 * 
 * @author Thorsten Dietz
 * 
 */
public final class BackupDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 8021487086770633938L;
	private JButton okButton = new JButton("Restore");
	private JButton cancelButton = new JButton("Cancel");
	private JList list;

	public BackupDialog() {
		super();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initialize();
	}

	private void initialize() {
		setTitle("Restore database");

		int dialogWidth = 320;
		int dialogHeight = 320;

		int with = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()
				.getWidth();
		int height = (int) GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().getHeight();
		setLocation((with - dialogWidth) / 2, (height - dialogHeight) / 2);
		setSize(dialogWidth, dialogHeight);

		Container contenPane = getContentPane();
		contenPane.add(getTopPanel(), BorderLayout.NORTH);
		contenPane.add(getList(), BorderLayout.CENTER);
		contenPane.add(createButtons(), BorderLayout.SOUTH);
	}

	private JPanel getTopPanel() {
		JPanel panel = new ImagePanel();
		panel.add(new JLabel("Select a database (ZIP-file) to restore from:"));
		return panel;
	}

	private JPanel createButtons() {
		JPanel buttonPanel = new ImagePanel();
		((FlowLayout) buttonPanel.getLayout()).setAlignment(FlowLayout.RIGHT);

		okButton.addActionListener(this);

		cancelButton.addActionListener(this);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}

	private JScrollPane getList() {

		File dbDirectory = new File(UserManager.instance().getCurrentUser().getDbFolder());
		ExampleFileFilter filter = new ExampleFileFilter("zip");
		filter.setIgnoreDirectories(true);
		File[] files = dbDirectory.listFiles(filter);
		list = new JList(files);

		JScrollPane scroll = new JScrollPane(list);
		return scroll;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			try {
				ZipHelper.unzip((File) list.getSelectedValue(), new File(UserManager.instance().getCurrentUser().getDbFolder()));
			} catch (Exception e1) {
				HOLogger.instance().log(getClass(), e1);
			}
		}

		setVisible(false);
		dispose();
	}

}
