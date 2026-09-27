package module.lineup.substitution;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class MessageBox extends JPanel {

	private JLabel iconLabel = new JLabel();
	private JTextArea commentsTextArea;

	public MessageBox() {
		initComponents();
	}

	public MessageBox(Icon icon, String message) {
		initComponents();
		setIcon(icon);
	}

	public void setIcon(Icon icon) {
		this.iconLabel.setIcon(icon);
	}

	public void setMessage(String message) {
		if (StringUtils.isEmpty(message)) {
			this.commentsTextArea.setText("");
		} else {
			this.commentsTextArea.setText(message);
		}
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 2);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(this.iconLabel, gbc);

		this.commentsTextArea = new JTextArea();
		this.commentsTextArea.setEditable(false);
		this.commentsTextArea.setOpaque(false);
		this.commentsTextArea.setBackground(new Color(0, 0, 0, 0));
		this.commentsTextArea.setBorder(null);
		this.commentsTextArea.setLineWrap(true);
		this.commentsTextArea.setWrapStyleWord(true);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(this.commentsTextArea, gbc);
	}
}
