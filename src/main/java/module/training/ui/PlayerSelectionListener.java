// %2953851721:hoplugins.trainingExperience.ui%
/*
 * Created on 12.10.2005
 */
package module.training.ui;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.table.FixedColumnsTable;
import core.model.HOVerwaltung;
import core.util.StringUtils;
import module.training.ui.model.TrainingModel;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PlayerSelectionListener implements ListSelectionListener {
	private final JTable table;
	private int playerIdColumnModelIndex = 0;
	private final TrainingModel model;

	public PlayerSelectionListener(TrainingModel model, JTable table, int col) {
		this.model = model;
		this.table = table;
		this.playerIdColumnModelIndex = col;
	}

	private boolean isPlayerSelectionChanging = false;
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			if ( !isPlayerSelectionChanging) {
				isPlayerSelectionChanging = true;
				int selectedRow = table.getSelectedRow();
				if (selectedRow >= 0) {
					var colViewIndex = table.convertColumnIndexToView(playerIdColumnModelIndex);
					if (this.table instanceof FixedColumnsTable fixedColumnsTable){
						if ( colViewIndex >= fixedColumnsTable.getFixedColumnsCount()){
							colViewIndex -= fixedColumnsTable.getFixedColumnsCount();
						}
					}
					if ( colViewIndex >= 0 && colViewIndex < table.getColumnCount()) {
						var entry = table.getValueAt(selectedRow, colViewIndex);
						String playerId;
						if (entry instanceof ColorLabelEntry colorLabelEntry) {
							playerId = colorLabelEntry.getText();
						} else if (entry instanceof String playerIdString) {
							playerId = playerIdString;
						} else {
							playerId = null;
						}
						if (playerId != null && StringUtils.isNumeric(playerId)) {
							model.setActivePlayer(HOVerwaltung.instance().getModel().getCurrentPlayer(Integer.parseInt(playerId)));
						} else {
							model.setActivePlayer(null);
						}
					}
				}
				isPlayerSelectionChanging = false;
			}
		}
	}
}
