package module.playerOverview;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.model.ReduzedTableModel;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.net.HattrickLink;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpielerUebersichtNamenTable extends JTable implements Refreshable, PlayerTable {

	private static final long serialVersionUID = -7686660400379157142L;
	private TableSorter tableSorter;

	/**
	 * Nur Namensspalte anzeigen
	 * 
	 */
	public SpielerUebersichtNamenTable(TableSorter model) {
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
						HattrickLink.showPlayer(player.getSpielerID());
					}
				}
			}
		});
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	@Override
	public Player getSpieler(int row) {
		return this.tableSorter.getSpieler(row);
	}

	@Override
	public final void setSpieler(int spielerid) {
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
