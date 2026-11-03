package module.lineup.substitution;

import core.model.player.Player;
import core.playername.PlayerNameFormat;
import core.playername.PlayerNameFormatter;
import core.playername.PlayerNameUtils;

public class PlayerPositionItem {
	private Integer position;
	private Player player;
    private final PlayerNameFormatter playerNameFormatter;
    private final PlayerNameFormat playerNameFormat;

	PlayerPositionItem(Integer pos, Player player, PlayerNameFormatter playerNameFormatter, PlayerNameFormat playerNameFormat) {
		this.player = player;
		this.position = pos;
        this.playerNameFormatter = playerNameFormatter;
        this.playerNameFormat = playerNameFormat;
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
            final var playerName = playerNameFormatter.format(PlayerNameUtils.createPlayerName(this.player), playerNameFormat);
			builder.append(playerName);
		}

		return builder.toString();
	}
}
