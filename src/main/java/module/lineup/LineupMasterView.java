package module.lineup;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.model.HOVerwaltung;
import core.model.match.MatchLineupPosition;
import core.model.player.IMatchRoleID;
import module.lineup.penalties.PenaltyTaker;
import module.lineup.penalties.PenaltyTakersView;
import module.lineup.substitution.SubstitutionOverview;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Top-Level Container for the Lineups (contains a tab for the lineup, a tab for
 * the match orders...)
 * 
 * @author kruescho
 * 
 */
public class LineupMasterView extends JPanel {

	private JTabbedPane tabbedPane;
	private LineupPanel lineupPanel;
	private SubstitutionOverview substitutionOverview;
	private PenaltyTakersView penaltyTakersView;
	private int oldTabIndex = -1;

	public LineupMasterView() {
		initComponents();
		addListeners();
	}

	public LineupPanel getLineupPanel() {
		return this.lineupPanel;
	}

	private void initComponents() {
		this.tabbedPane = new JTabbedPane();
		HOVerwaltung hov = HOVerwaltung.instance();

		this.lineupPanel = new LineupPanel();
		this.tabbedPane.addTab(hov.getLanguageString("Aufstellung"), this.lineupPanel);

		this.substitutionOverview = new SubstitutionOverview(hov.getModel().getCurrentLineup());
		this.tabbedPane.addTab(hov.getLanguageString("subs.Title"), this.substitutionOverview);

		this.penaltyTakersView = new PenaltyTakersView();
		this.penaltyTakersView.setPlayers(hov.getModel().getCurrentPlayers());
		this.penaltyTakersView.setLineup(hov.getModel().getCurrentLineup());
		this.tabbedPane.addTab(hov.getLanguageString("lineup.penaltytakers.tab.title"), this.penaltyTakersView);

		setLayout(new BorderLayout());
		add(this.tabbedPane, BorderLayout.CENTER);
	}

	private void addListeners() {
		this.lineupPanel.addUpdateable(this::refreshView);
		
		this.tabbedPane.addChangeListener(e -> {
            // if penalty takers tab is left, update the lineup
            if (oldTabIndex == tabbedPane.indexOfComponent(penaltyTakersView) )	{
                updatePenaltyTakersInLineup();
            }
            oldTabIndex = tabbedPane.getSelectedIndex();
        });
		
		RefreshManager.instance().registerRefreshable(new Refreshable() {
			
			@Override
			public void refresh() {
				refreshView();
				
			}
			
			@Override
			public void reInit() {
				refreshView();
			}
		});
	}
	
	private void updatePenaltyTakersInLineup() {
		List<PenaltyTaker> takers = this.penaltyTakersView.getPenaltyTakers();
		List<MatchLineupPosition>  list = new ArrayList<>(takers.size());

		for (int i = 0; i < takers.size(); i++) {
			list.add(new MatchLineupPosition(IMatchRoleID.penaltyTaker1 + i, takers.get(i).getPlayer().getPlayerId(), IMatchRoleID.NORMAL));
		}
		HOVerwaltung.instance().getModel().getCurrentLineup().setPenaltyTakers(list);
	}
	
	private void refreshView() {
		this.substitutionOverview.setLineup(HOVerwaltung.instance().getModel().getCurrentLineup());
		this.penaltyTakersView.setPlayers(HOVerwaltung.instance().getModel().getCurrentPlayers());
		this.penaltyTakersView.setLineup(HOVerwaltung.instance().getModel().getCurrentLineup());
	}
}
