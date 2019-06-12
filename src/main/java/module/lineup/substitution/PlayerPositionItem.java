package module.lineup.substitution;

import core.model.player.Player;

public class PlayerPositionItem {
	private Integer position;
	private Player player;

	PlayerPositionItem(Integer pos, Player player) {
		this.player = player;
		this.position = pos;
	}

	public Integer getPosition() {
		return position;
	}

	public Player getSpieler() {
		return this.player;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.position != null) {
			builder.append("( ");
			builder.append(LanguageStringLookup.getPosition(this.position));
			builder.append(" ) ");
		}
		if (this.player != null) {
			builder.append(this.player.getName());
		}

		return builder.toString();
	}
}
