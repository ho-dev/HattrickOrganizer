package core.gui.comp.entry;

import core.model.player.Player;

import javax.swing.JComponent;
import javax.swing.SwingConstants;



public class HomegrownEntry implements IHOTableEntry{

	private ColorLabelEntry icon = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
			ColorLabelEntry.BG_STANDARD,
			SwingConstants.CENTER);
	private Player player;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new Homegrown Entry.
	 */
	public HomegrownEntry() {
		super();
	}

	public final void setPlayer(Player player) {
		this.player = player;
		updateComponent();
	}

	public final Player getPlayer() {
		return player;
	}


	@Override
	public int compareTo(IHOTableEntry obj) {
		if (obj instanceof HomegrownEntry) {
			final HomegrownEntry entry = (HomegrownEntry) obj;

			if ((entry.getPlayer() != null) && (getPlayer() != null)) {

				if (entry.getPlayer().isHomeGrown() != getPlayer().isHomeGrown()) {
					if (getPlayer().isHomeGrown() == true) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		}
		return 0;
	}

	
	@Override
	public final void updateComponent() {
		if (player != null) {
			if (player.isHomeGrown()) {
				icon.setIcon(core.gui.theme.ThemeManager.getIcon(core.gui.theme.HOIconName.HOMEGROWN));
			} else {
				icon.clear();
			}

		} else {
			icon.clear();
		}
	}


	@Override
	public JComponent getComponent(boolean isSelected) {
		return icon.getComponent(isSelected);
	}


	@Override
	public void clear() {
		player = null;
		updateComponent();
		
	}


	@Override
	public void createComponent() {
		icon = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
				ColorLabelEntry.BG_STANDARD,
				SwingConstants.CENTER);
	}
}

