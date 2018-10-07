package module.playeranalysis.experience;

import core.gui.comp.panel.LazyImagePanel;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ExperienceViewer extends LazyImagePanel {

	private static final long serialVersionUID = 3294326447950073349L;
	private Spielertabelle spielertabelle;

	@Override
	protected void initialize() {
		initComponents();
		registerRefreshable(true);
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		spielertabelle.aktualisieren();
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		spielertabelle = new Spielertabelle();
		JPanel tabelle = new JPanel();
		tabelle.setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(spielertabelle);
		scroll.setSize(1200, 600);
		tabelle.add(scroll, "Center");
		tabelle.setBorder(BorderFactory.createEtchedBorder());
		add(tabelle);
	}
}
