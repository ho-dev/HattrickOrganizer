package module.evilcard;

import java.util.ArrayList;
import java.util.List;

public class Model {

	public static final int TYPE_CURRENT_PLAYERS = 1;
	public static final int TYPE_ALL_PLAYERS = 2;
	private int selectedPlayer;
	private int playerFilter = TYPE_CURRENT_PLAYERS;
	private List<ModelChangeListener> listeners = new ArrayList<ModelChangeListener>();

	public int getSelectedPlayer() {
		return selectedPlayer;
	}

	public void setSelectedPlayer(int selectedPlayer) {
		if (this.selectedPlayer != selectedPlayer) {
			this.selectedPlayer = selectedPlayer;
			fireSelectedPlayerChanged();
		}
	}

	public int getPlayerFilter() {
		return playerFilter;
	}

	public void setPlayerFilter(int playerFilter) {
		if (this.playerFilter != playerFilter) {
			this.playerFilter = playerFilter;
			firePlayerFilterChanged();
		}
	}

	public void addModelChangeListener(ModelChangeListener listener) {
		if (listener != null && !this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeModelChangeListener(ModelChangeListener listener) {
		this.listeners.remove(listener);
	}

	public void update() {
		for (int i = this.listeners.size() - 1; i >= 0; i--) {
			this.listeners.get(i).completeDataChanged();
		}
	}

	private void fireSelectedPlayerChanged() {
		for (int i = this.listeners.size() - 1; i >= 0; i--) {
			this.listeners.get(i).selectedPlayerChanged();
		}
	}

	private void firePlayerFilterChanged() {
		for (int i = this.listeners.size() - 1; i >= 0; i--) {
			this.listeners.get(i).playerFilterChanged();
		}
	}
}
