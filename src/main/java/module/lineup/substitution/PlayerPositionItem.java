package module.lineup.substitution;

import core.model.player.Spieler;

public class PlayerPositionItem {
	private Integer position;
	private Spieler player;

	PlayerPositionItem(Integer pos, Spieler player) {
		this.player = player;
		this.position = pos;
	}

	public Integer getPosition() {
		return position;
	}

	public Spieler getSpieler() {
		return this.player;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.position != null) {
			builder.append(LanguageStringLookup.getPosition(this.position.byteValue()));
		}
		if (this.player != null) {
			builder.append(" - ");
			if (this.player.getTrikotnummer() > 0 && this.player.getTrikotnummer() != 100) {
				builder.append(player.getTrikotnummer());
				builder.append(" ");
			}
			builder.append(this.player.getName());
		}

		return builder.toString();
	}
}
