package core.gui;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.util.ExceptionUtils;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 * @author kruescho
 */
public class ExceptionDialog extends JDialog {

	private static final long serialVersionUID = 767279628764672228L;
	private JPanel detailsPanel;
	private JTextArea textArea;
	private JButton detailsButton;
	private Dimension savedDetailsSize;
	private ImageIcon showDetailsImage;
	private ImageIcon hideDetailsImage;
	private Throwable throwable;

	public ExceptionDialog(String msg, Throwable t) {
		setModal(true);
		this.throwable = t;
		setTitle(msg);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
	}

	private void initComponents() {
		try {
			this.showDetailsImage = ThemeManager.getIcon(HOIconName.CONTROL_DOUBLE_270);
			this.hideDetailsImage = ThemeManager.getIcon(HOIconName.CONTROL_DOUBLE_090);
			setIconImage(ThemeManager.getIcon(HOIconName.EXCLAMATION_RED).getImage());
		} catch (Exception ex) {
			HOLogger.instance().log(getClass(), ex);
		}
		setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());

		JLabel label = new JLabel("An error occurred.");
		label.setFont(label.getFont().deriveFont(label.getFont().getStyle() ^ Font.BOLD));
		try {
			label.setIcon(ThemeManager.getIcon(HOIconName.EXCLAMATION_RED));
		} catch (Exception ex) {
			HOLogger.instance().log(getClass(), ex);
		}
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;
		topPanel.add(label, gbc);

		JLabel errorLabel = new JLabel(getMessage(this.throwable));
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.insets = new Insets(4, 8, 8, 4);
		gbc.anchor = GridBagConstraints.WEST;
		topPanel.add(errorLabel, gbc);

		this.detailsButton = new JButton("details", this.showDetailsImage);
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 8, 8);
		gbc.gridy = 1;
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		topPanel.add(this.detailsButton, gbc);
		this.detailsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switchDetails();
			}
		});

		add(topPanel, BorderLayout.NORTH);
		pack();
	}

	private String getMessage(Throwable throwable) {
		String message = throwable.getMessage();
		if (message == null) {
			message = throwable.getClass().getName();
		}
		return message;
	}

	private void createDetails() {
		this.detailsPanel = new JPanel(new BorderLayout());
		this.textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setText(ExceptionUtils.getStackTrace(this.throwable));
		JScrollPane scrollPane = new JScrollPane(textArea);
		this.detailsPanel.add(scrollPane, BorderLayout.CENTER);
		this.detailsPanel.setPreferredSize(new Dimension(650, 300));
		this.savedDetailsSize = new Dimension(this.detailsPanel.getPreferredSize());
		add(this.detailsPanel, BorderLayout.CENTER);
	}

	private void switchDetails() {
		if (this.detailsPanel != null && this.detailsPanel.isVisible()) {
			this.savedDetailsSize = new Dimension(this.detailsPanel.getSize());
			this.detailsButton.setIcon(this.showDetailsImage);
			this.detailsPanel.setVisible(false);
		} else {
			if (this.detailsPanel == null) {
				createDetails();
			} else {
				this.detailsPanel.setPreferredSize(this.savedDetailsSize);
			}
			this.detailsButton.setIcon(this.hideDetailsImage);
			this.detailsPanel.setVisible(true);
			this.textArea.setCaretPosition(0);
		}
		pack();
	}
}
