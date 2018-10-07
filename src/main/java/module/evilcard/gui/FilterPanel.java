package module.evilcard.gui;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import module.evilcard.Model;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

class FilterPanel extends ImagePanel {

	private static final long serialVersionUID = 5993279445476499431L;
	private JComboBox choosePlayersComboBox;
	private final Model model;

	FilterPanel(Model model) {
		super();
		this.model = model;
		initComponents();
		addListeners();
	}

	private void addListeners() {
		choosePlayersComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				switch (choosePlayersComboBox.getSelectedIndex()) {
				case 0:
					model.setPlayerFilter(Model.TYPE_CURRENT_PLAYERS);
					break;
				case 1:
					model.setPlayerFilter(Model.TYPE_ALL_PLAYERS);
					break;
				default:
					// no actions.
				}
			}
		});
	}

	private void initComponents() {
		setOpaque(false);
		setLayout(new FlowLayout(FlowLayout.CENTER));

		JLabel choosePlayersLabel = new javax.swing.JLabel(HOVerwaltung.instance()
				.getLanguageString("Spieler"));

		add(choosePlayersLabel);

		this.choosePlayersComboBox = new JComboBox();
		this.choosePlayersComboBox.addItem(HOVerwaltung.instance().getLanguageString(
				"label.CurrentPlayersOnly"));
		this.choosePlayersComboBox.addItem(HOVerwaltung.instance().getLanguageString("alle"));
		this.choosePlayersComboBox.setSelectedIndex(0);
		add(this.choosePlayersComboBox);
	}

}
