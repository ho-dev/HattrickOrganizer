package module.transfer;

import core.db.DBManager;
import core.gui.IRefreshable;
import core.gui.RefreshManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.player.Player;
import module.transfer.history.HistoryPane;
import module.transfer.scout.TransferScoutPanel;
import module.transfer.transfertype.TransferTypePane;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class TransfersPanel extends JPanel implements IRefreshable {

	private final HistoryPane historyPane;
	private List<Player> oldplayers;
	private List<Player> players;
	private final TransferTypePane transferTypePane;
	private final TransferScoutPanel scoutPanel;

	public TransfersPanel() {

		this.players = HOVerwaltung.instance().getModel().getCurrentPlayers();
		this.oldplayers = HOVerwaltung.instance().getModel().getFormerPlayers();

		// Create the top panel
		final JTabbedPane tabPane = new JTabbedPane();

		historyPane = new HistoryPane(this);
		tabPane.add(TranslationFacility.tr("History"), historyPane); //$NON-NLS-1$

		transferTypePane = new TransferTypePane(this);
		tabPane.add(TranslationFacility.tr("TransferTypes"), transferTypePane);

		scoutPanel = new TransferScoutPanel();
		tabPane.add(TranslationFacility.tr("TransferScout"), scoutPanel);

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

        allOutdated.removeIf(player -> player.getPlayerId() < 0);
		boolean success = false;
		if ((!allOutdated.isEmpty()) && !HOVerwaltung.instance().getModel().getBasics().isNationalTeam() &&  (DBManager.instance().getTransfers(0, true, true).isEmpty())) {
			success = XMLParser.updateTeamTransfers(HOVerwaltung.instance().getModel().getBasics().getTeamId());
		}
		
		// If download is cancelled, the below will give authorization requests for each player.
		// Don't do this if first download access was cancelled. This is relevant on hrf import to empty db.
		// Also, Db is called to do downloads? Truly messed up. 
		if (success) {
			for (final Player player : allOutdated) {
				XMLParser.updatePlayerTransfers(player.getPlayerId());
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
		scoutPanel.storeUserSettings();
	}

	public void selectTransfer(int transferId) {
		this.historyPane.selectTransfer(transferId);
		this.transferTypePane.selectTransfer(transferId);
	}
}