package module.playerOverview;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.model.ReduzedTableModel;
import core.model.player.Player;
import core.net.HattrickLink;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LineupPlayersTableNameColumn extends JTable implements Refreshable, PlayerTable {

	private final TableSorter tableSorter;

	/**
	 * Only the name column
	 */
	public LineupPlayersTableNameColumn(TableSorter model) {
		super();
		tableSorter = model;
		model.addMouseListenerToHeaderInTable(this);
		model.addTableModelListener(this);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setModel(new ReduzedTableModel(model, 0));
		setDefaultRenderer(java.lang.Object.class, new HODefaultTableCellRenderer());
		RefreshManager.instance().registerRefreshable(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int rowindex = getSelectedRow();
				if (rowindex >= 0){
					Player player = tableSorter.getSpieler(rowindex);
					if(player!=null && e.isShiftDown()){
						HattrickLink.showPlayer(player.getPlayerID());
					}
				}
			}
		});
	}


	@Override
	public @Nullable Player getPlayer(int row) {
		return this.tableSorter.getSpieler(row);
	}

	@Override
	public final void setPlayer(int spielerid) {
		final int index = tableSorter.getRow4Spieler(spielerid);

		if (index >= 0) {
			this.setRowSelectionInterval(index, index);
		}
	}

	@Override
	public final void reInit() {
		initModelNamen();
		repaint();
	}

	@Override
	public final void refresh() {
		// Datenanpassung wird vom SpielerUbersichtsTable erledigt
		repaint();
	}

	/**
	 * Initialisiert das Model für die Namen
	 */
	private void initModelNamen() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowSelectionAllowed(true);
		getColumnModel().getColumn(0).setMinWidth(167);
	}
}
