package module.playerOverview;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.model.ReduzedTableModel;
import core.model.player.Player;

import javax.swing.*;

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
