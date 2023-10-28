package module.transfer.history;

import core.db.DBManager;
import module.transfer.PlayerTransfer;

import java.util.List;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;

public class ButtonCellEditor extends AbstractCellEditor
                           implements TableCellEditor,
                                      ActionListener {

    final PlayerDetailPanel playerDetailPanel;
	final List<PlayerTransfer> values;
    final JButton button;

    PlayerTransfer transfer;

    public ButtonCellEditor(PlayerDetailPanel playerDetailPanel, List<PlayerTransfer> values) {
		this.values = values;
		this.playerDetailPanel = playerDetailPanel;
        button = new JButton();
        button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        DBManager.instance().removeTransfer(transfer.getTransferId());
        fireEditingStopped();
        playerDetailPanel.setPlayer(transfer.getPlayerId(), transfer.getPlayerName());
    }

    public Object getCellEditorValue() {
        return transfer.getTransferId();
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        transfer = values.get(row);
        return button;
    }
}