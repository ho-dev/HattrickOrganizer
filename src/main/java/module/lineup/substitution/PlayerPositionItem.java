package module.lineup.substitution;

import core.model.player.Player;
import core.playername.PlayerNameFormatter;
import core.playername.PlayerNameUtils;

public class PlayerPositionItem {
	private Integer position;
	private Player player;
    private final PlayerNameFormatter playerNameFormatter;

	PlayerPositionItem(Integer pos, Player player, PlayerNameFormatter playerNameFormatter) {
		this.player = player;
		this.position = pos;
        this.playerNameFormatter = playerNameFormatter;
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
            final var playerName = playerNameFormatter.format(PlayerNameUtils.createPlayerName(this.player), PlayerNameFormatter.NameFormat.SMART_LAST_FIRST);
			builder.append(playerName);
		}

		return builder.toString();
	}
}
