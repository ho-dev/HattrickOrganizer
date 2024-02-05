package module.teamAnalyzer.ui;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.event.ChangeEventHandler;
import core.model.HOVerwaltung;
import core.util.HOLogger;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ht.HattrickManager;
import module.teamAnalyzer.manager.TeamManager;
import module.teamAnalyzer.ui.component.TeamInfoPanel;
import module.teamAnalyzer.vo.Team;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * Panel to filter opponents matches.
 */
public class FilterPanel extends JPanel {

	private static final String CARD_AUTOMATIC = "AUTOMATIC CARD";
	private static final String CARD_MANUAL = "MANUAL CARD";
	private static boolean teamComboUpdating = false;
	private AutoFilterPanel autoPanel;
	private final JButton downloadButton = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.update"));
	private final JComboBox<Team> teamCombo = new JComboBox<>();
	private final JPanel cards = new JPanel(new CardLayout());
	private JRadioButton radioAutomatic;
	private JRadioButton radioManual;
	private ManualFilterPanel manualPanel;

	private final TeamInfoPanel teamInfoPanel = new TeamInfoPanel();

	/**
	 * Creates a new FilterPanel object.
	 */
	public FilterPanel() {
		jbInit();
	}

	/**
	 * Get the selected team from the list box with opponents
	 */
	public Team getSelectedTeam() {
		return (Team) teamCombo.getSelectedItem();
	}

	/**
	 * Update GUI elements.
	 */
	public void reload() {
		System.out.println("REload");
		if (TeamManager.isUpdated()) {
			fillTeamCombo();
		}

		downloadButton.setEnabled(true);
		downloadButton.setText(HOVerwaltung.instance().getLanguageString("ls.button.update"));

		CardLayout cLayout = (CardLayout) (cards.getLayout());

		if (TeamAnalyzerPanel.filter.isAutomatic()) {
			radioAutomatic.setSelected(true);
			cLayout.show(cards, CARD_AUTOMATIC);
			autoPanel.reload();
		}
		else {
			radioManual.setSelected(true);
			cLayout.show(cards, CARD_MANUAL);
			manualPanel.reload();
		}
	}

	/**
	 * Fill the combo box with teams.
	 */
	private void fillTeamCombo() {
		teamComboUpdating = true;
		teamCombo.removeAllItems();
		int i = 0;
		for (Team element : TeamManager.getTeams()) {
			teamCombo.addItem(element);
			if (SystemManager.getActiveTeamId() == element.getTeamId()) {
				teamCombo.setSelectedItem(element);
			}
			i++;
		}

		teamCombo.setMaximumRowCount(i);
		teamComboUpdating = false;
	}

	/**
	 * Init GUI.
	 */
	private void jbInit() {
		JPanel main = new ImagePanel();

		main.setLayout(new BorderLayout());
		setLayout(new BorderLayout());
		setOpaque(false);

		fillTeamCombo();
		teamCombo.setRenderer(new MatchComboBoxRenderer());
		teamCombo.setOpaque(false);
		teamCombo.addItemListener(e -> {
			if (!teamComboUpdating) {
				Team selectedTeam = (Team) teamCombo.getSelectedItem();
				SystemManager.setActiveTeam(selectedTeam);
				Map<String, String> teamDetails = HattrickManager.getTeamDetails(selectedTeam.getTeamId());
				teamInfoPanel.setTeam(teamDetails);
				SystemManager.refresh();
			}
		});

		if (teamCombo.getSelectedItem() != null) {
			Team selectedTeam = (Team) teamCombo.getSelectedItem();
			Map<String, String> teamDetails = HattrickManager.getTeamDetails(selectedTeam.getTeamId());
			teamInfoPanel.setTeam(teamDetails);
		}

		JButton analyzeButton = new JButton(HOVerwaltung.instance().getLanguageString(
				"AutoFilterPanel.Analyze"));

		analyzeButton.addActionListener(e -> {
			if (radioManual.isSelected()) {
				manualPanel.setFilter();
			} else {
				autoPanel.setFilter();
			}
			SystemManager.updateReport();
		});

		downloadButton.addActionListener(e -> {
            downloadButton.setEnabled(false);
			// Trigger event in a separate thread to avoid Button UI from being blocked.
			SwingUtilities.invokeLater(() -> {
				final ExecutorService downloadExecutor = Executors.newCachedThreadPool();

				HOLogger.instance().log(getClass(),
						"UPDATE for Team " + SystemManager.getActiveTeamId());

				// Load squad info of all teams
				try {
					for (var team : TeamManager.getTeams()) {
						Map<String, String> teamDetails = HattrickManager.getTeamDetails(team.getTeamId());
						System.out.println(teamDetails);
						downloadExecutor.execute(() -> HattrickManager.downloadPlayers(team.getTeamId()));
					}

					downloadExecutor.execute(() ->
							HattrickManager.downloadMatches(SystemManager.getActiveTeamId(), TeamAnalyzerPanel.filter));
				} finally {
					downloadExecutor.shutdown();
					try {
						downloadExecutor.awaitTermination(30, TimeUnit.SECONDS);
					} catch (Exception ee) {
						HOLogger.instance().error(FilterPanel.class, "Error awaiting termination: "  + ee.getMessage());
					}
				}
				HOMainFrame.instance().setInformationCompleted();
				SystemManager.refresh();

				HOLogger.instance().info(getClass(),
						"Download complete for Team " + SystemManager.getActiveTeamId());

				downloadButton.setEnabled(true);
			});
        });

		JPanel mainTeamPanel = new JPanel();
		mainTeamPanel.setLayout(new BorderLayout());

		JPanel teamPanel = new ImagePanel();
		teamPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		teamPanel.setLayout(new BorderLayout());
		teamPanel.add(downloadButton, BorderLayout.NORTH);
		teamPanel.add(teamCombo, BorderLayout.SOUTH);
		teamPanel.setOpaque(false);

		mainTeamPanel.add(teamPanel, BorderLayout.NORTH);
		mainTeamPanel.add(teamInfoPanel, BorderLayout.CENTER);

		JPanel topPanel = new ImagePanel();

		topPanel.setLayout(new BorderLayout());
		radioAutomatic = new JRadioButton(HOVerwaltung.instance().getLanguageString("Option.Auto")); //$NON-NLS-1$
		radioAutomatic.setSelected(true);
		radioAutomatic.addActionListener(e -> {
			final CardLayout cLayout = (CardLayout) cards.getLayout();
			TeamAnalyzerPanel.filter.setAutomatic(true);
			autoPanel.reload();
			cLayout.show(cards, CARD_AUTOMATIC);
		});
		radioAutomatic.setOpaque(false);
		radioManual = new JRadioButton(HOVerwaltung.instance().getLanguageString("Manual")); //$NON-NLS-1$
		radioManual.addActionListener(e -> {
			final CardLayout cLayout = (CardLayout) cards.getLayout();
			cLayout.show(cards, CARD_MANUAL);
			TeamAnalyzerPanel.filter.setAutomatic(false);
			manualPanel.reload();
		});
		radioManual.setOpaque(false);

		ButtonGroup groupRadio = new ButtonGroup();
		JPanel buttonPanel = new ImagePanel();

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		groupRadio.add(radioAutomatic);
		groupRadio.add(radioManual);

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(radioAutomatic);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(radioManual);
		buttonPanel.add(Box.createHorizontalGlue());

		topPanel.add(mainTeamPanel, BorderLayout.NORTH);
		topPanel.add(buttonPanel, BorderLayout.SOUTH);

		main.add(topPanel, BorderLayout.NORTH);
		add(main, BorderLayout.NORTH);
		autoPanel = new AutoFilterPanel();
		manualPanel = new ManualFilterPanel();
		cards.add(autoPanel, CARD_AUTOMATIC);
		cards.add(manualPanel, CARD_MANUAL);
		add(cards, BorderLayout.CENTER);
		add(analyzeButton, BorderLayout.SOUTH);
	}
}
