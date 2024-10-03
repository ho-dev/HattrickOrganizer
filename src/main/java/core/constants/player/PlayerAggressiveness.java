package core.constants.player;

import core.model.TranslationFacility;

public class PlayerAggressiveness {
	private static final String[] languageKeys = {
			"ls.player.aggressiveness.tranquil",
			"ls.player.aggressiveness.calm",
			"ls.player.aggressiveness.balanced",
			"ls.player.aggressiveness.temperamental",
			"ls.player.aggressiveness.fiery",
			"ls.player.aggressiveness.unstable"};

	public static final int TRANQUIL = 0;
	public static final int CALM = 1;
	public static final int BALANCED = 2;
	public static final int TEMPERAMENTAL = 3;
	public static final int FIERY = 4;
	public static final int UNSTABLE = 5;


	private PlayerAggressiveness() {
	}

	public static String toString(Integer aggressiveness) {
		if (aggressiveness != null && aggressiveness >= TRANQUIL && aggressiveness <= UNSTABLE)
			return TranslationFacility.tr(languageKeys[aggressiveness]);
		return TranslationFacility.tr("Unbestimmt");
	}
}
