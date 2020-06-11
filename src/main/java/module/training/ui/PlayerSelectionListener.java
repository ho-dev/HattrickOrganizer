// %2953851721:hoplugins.trainingExperience.ui%
/*
 * Created on 12.10.2005
 */
package module.training.ui;

import core.model.HOVerwaltung;
import core.util.StringUtils;
import module.training.ui.model.TrainingModel;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PlayerSelectionListener implements ListSelectionListener {
	private JTable table;
	private int playerIdColumn = 0;
	private TrainingModel model;

	public PlayerSelectionListener(TrainingModel model, JTable table, int col) {
		this.model = model;
		this.table = table;
		this.playerIdColumn = col;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
	 * .ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			int index = table.getSelectedRow();
			if (index >= 0) {
				String playerId = (String) table.getValueAt(index, playerIdColumn);
				if (StringUtils.isNumeric(playerId)) {
					model.setActivePlayer(HOVerwaltung.instance().getModel()
							.getCurrentPlayer(Integer.parseInt(playerId)));
				} else {
					model.setActivePlayer(null);
				}
			}
		}
	}
}
