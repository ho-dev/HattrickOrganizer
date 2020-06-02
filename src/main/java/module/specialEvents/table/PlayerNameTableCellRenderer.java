package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.StringUtils;
import org.javatuples.Pair;
import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class PlayerNameTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -70362628083065436L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		Pair oValue = (Pair) value;
		String playerName = (String) oValue.getValue0();

		JLabel label = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
				hasFocus, row, column);


		if (!StringUtils.isEmpty(playerName)) {
			String rName = playerName.substring(0, playerName.length() - 2);
			String rType = playerName.substring(playerName.length() - 1);
			label.setText(rName);
			if (rType.equals("-")) {
				label.setForeground(Color.RED);
			} else if (rType.equals("#")) {
				label.setForeground(Color.LIGHT_GRAY);
			} else {
				label.setForeground(Color.BLACK);
			}
		} else {
			label.setForeground(Color.BLACK);
			label.setText("");
		}

		Player oPlayer = HOVerwaltung.instance().getModel().getSpieler((int) oValue.getValue1());
		if (oPlayer != null) {
			int iPlayerSpecialty = oPlayer.getPlayerSpecialty();
			ImageIcon oPlayerSpecialty = ThemeManager.getIcon(HOIconName.SPECIALTIES_SMALL[iPlayerSpecialty]);
			label.setIcon(oPlayerSpecialty);
		}
		else
		{
			label.setIcon(null);
		}

		RowColorDecorator.decorate(table, row, label, isSelected);
		return label;
	}

}
