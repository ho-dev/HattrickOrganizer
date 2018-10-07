package module.lineup.substitution;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class Divider extends JPanel {

	private static final long serialVersionUID = 4363337246058305684L;

	public Divider(String text) {
		setLayout(new GridBagLayout());

		JLabel label = new JLabel(text);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		add(label, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		add(new JSeparator(), gbc);
	}
}
