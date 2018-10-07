package module.evilcard.gui;

import core.gui.comp.panel.LazyPanel;
import module.evilcard.Model;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class EvilCardPanel extends LazyPanel {

	private static final long serialVersionUID = 1L;
	private boolean initialized = false;
	private Model model;

	@Override
	protected void initialize() {
		initComponents();
		registerRefreshable(true);
	}

	@Override
	protected void update() {
		this.model.update();
	}

	private void initComponents() {
		this.model = new Model();

		JPanel mainPanel = new JPanel(new BorderLayout());
		PlayersPanel playersPanel = new PlayersPanel(this.model);
		DetailsPanel detailsPanel = new DetailsPanel(this.model);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, playersPanel, detailsPanel);
		splitPane.setResizeWeight(0.5d);
		mainPanel.add(splitPane);

		setLayout(new BorderLayout());
		add(new FilterPanel(this.model), BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
	}
}
