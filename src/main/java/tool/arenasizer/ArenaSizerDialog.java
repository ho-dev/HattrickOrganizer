package tool.arenasizer;

import core.model.HOVerwaltung;
import core.model.TranslationFacility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ArenaSizerDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private ArenaPanel arenaPanel;
	private DistributionStatisticsPanel historyPanel;
	private ArenaInfoPanel arenaInfoPanel;
	private ControlPanel controlPanel;
	private JPanel toolbar;
	private JButton refreshButton = new JButton(TranslationFacility.tr("ls.button.apply"));

	public ArenaSizerDialog(JFrame owner) {
		super(owner, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setTitle(TranslationFacility.tr("ArenaSizer"));
		add(getToolbar(), BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());

		JPanel panelC = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelC.add(getControlPanel());
		centerPanel.add(panelC, BorderLayout.NORTH);
		centerPanel.add(getTabbedPane(), BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);

		pack();
		setLocationByPlatform(true);
	}

	private JPanel getToolbar() {
		if (toolbar == null) {
			toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING));
			toolbar.add(refreshButton);
			refreshButton.addActionListener(this);
			// reset
			// save
		}
		return toolbar;
	}

	private ControlPanel getControlPanel() {
		if (controlPanel == null) {
			controlPanel = new ControlPanel();
		}
		return controlPanel;
	}

	private JPanel getHistoryPanel() {
		if (historyPanel == null) {
			historyPanel = new DistributionStatisticsPanel();
		}
		return historyPanel;
	}

	ArenaPanel getArenaPanel() {
		if (arenaPanel == null) {
			arenaPanel = new ArenaPanel();
		}
		return arenaPanel;
	}

	ArenaInfoPanel getArenaInfoPanel() {
		if (arenaInfoPanel == null) {
			arenaInfoPanel = new ArenaInfoPanel();
		}
		return arenaInfoPanel;
	}

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			HOVerwaltung hoV = HOVerwaltung.instance();
			tabbedPane.addTab(TranslationFacility.tr("Stadion"), getArenaPanel());
			tabbedPane.addTab(hoV.getModel().getStadium().getStadiumName(), getArenaInfoPanel());
			tabbedPane.addTab(TranslationFacility.tr("Statistik"), getHistoryPanel());
		}
		return tabbedPane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == refreshButton) {
			Stadium stadium = getControlPanel().getStadium();
			int[] supporter = getControlPanel().getModifiedSupporter();
			getArenaPanel().reinitArena(stadium, supporter[0], supporter[1], supporter[2]);
		}
	}
}
