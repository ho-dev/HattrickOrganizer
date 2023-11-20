package module.transfer;

import core.db.DBManager;
import core.gui.IRefreshable;
import core.gui.RefreshManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import module.transfer.history.HistoryPane;
import module.transfer.scout.TransferScoutPanel;
import module.transfer.transfertype.TransferTypePane;
import java.awt.BorderLayout;
import java.io.Serial;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TransfersPanel extends JPanel implements IRefreshable {

	@Serial
	private static final long serialVersionUID = -5312017309355429020L;
	private HistoryPane historyPane;
	private List<Player> oldplayers;
	private List<Player> players;
	private TransferTypePane transferTypePane;
	private TransferScoutPanel scoutPanel;

	public TransfersPanel() {
		initialize();
	}

	private void initialize() {
		this.players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		this.oldplayers = HOVerwaltung.instance().getModel().getFormerPlayers();

		// Create the top panel
		final JTabbedPane tabPane = new JTabbedPane();

		historyPane = new HistoryPane();
		tabPane.add(HOVerwaltung.instance().getLanguageString("History"), historyPane); //$NON-NLS-1$

		transferTypePane = new TransferTypePane();
		tabPane.add(HOVerwaltung.instance().getLanguageString("TransferTypes"), transferTypePane);

		scoutPanel = new TransferScoutPanel();
		tabPane.add(HOVerwaltung.instance().getLanguageString("TransferScout"), scoutPanel);

		// this.overviewPanel = new OverviewPanel();
		// tabPane.add("Financial", this.overviewPanel);

		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);

		RefreshManager.instance().registerRefreshable(this);

	}

	public void refresh() {
		// Check for outdated players.
        final List<Player> tmp = new Vector<>(HOVerwaltung.instance().getModel().getCurrentPlayers());
		tmp.removeAll(this.players);
		final List<Player> allOutdated = new Vector<>(tmp);

		// Check for outdated old players.
		tmp.clear();
		tmp.addAll(HOVerwaltung.instance().getModel().getFormerPlayers());
		tmp.removeAll(this.oldplayers);
		allOutdated.addAll(tmp);

        allOutdated.removeIf(player -> player.getPlayerID() < 0);
		boolean success = false;
		if ((!allOutdated.isEmpty()) && !HOVerwaltung.instance().getModel().getBasics().isNationalTeam() &&  (DBManager.instance().getTransfers(0, true, true).isEmpty())) {
			success = XMLParser.updateTeamTransfers(HOVerwaltung.instance().getModel().getBasics().getTeamId());
		}
		
		// If download is cancelled, the below will give authorization requests for each player.
		// Don't do this if first download access was cancelled. This is relevant on hrf import to empty db.
		// Also, Db is called to do downloads? Truly messed up. 
		if (success) {
			for (final Player player : allOutdated) {
				XMLParser.updatePlayerTransfers(player.getPlayerID());
			}
		}

		reloadData();
	}

	/**
	 * Refresh the data in the plugin
	 */
	private void reloadData() {
		this.players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		this.oldplayers = HOVerwaltung.instance().getModel().getFormerPlayers();

		final List<PlayerTransfer> transfers = DBManager.instance().getTransfers(0, true, true);

		historyPane.refresh();
		transferTypePane.refresh(transfers);
	}

	public TransferScoutPanel getScoutPanel() {
		return scoutPanel;
	}

	public void storeUserSettings() {
		historyPane.storeUserSettings();
	}
}