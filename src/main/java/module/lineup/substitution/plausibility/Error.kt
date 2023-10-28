package module.lineup.substitution.plausibility;

public enum Error implements Problem {
	MANMARKING_PLAYER_MISSING("subs.plausibility.manmarking.playermissing"),
	NEWBEHAVIOUR_PLAYER_MISSING("subs.plausibility.newbehaviour.playermissing"),
	SUBSTITUTION_PLAYER_MISSING("subs.plausibility.substitution.playermissing"),
	POSITIONSWAP_PLAYER_MISSING("subs.plausibility.positionswap.playermissing"),
	PLAYERIN_NOT_IN_LINEUP("subs.plausibility.playerIn.notInLineup"),
	PLAYEROUT_NOT_IN_LINEUP("subs.plausibility.playerOut.notInLineup"),
	PLAYEROUT_NOT_REAL("subs.plausibility.player.notReal"),
	PLAYERIN_NOT_REAL("subs.plausibility.player.notReal"),
	TOO_MANY_ORDERS("subs.plausibility.toomanysubs");
	
	private String languageKey;

	Error(String languageKey) {
		this.languageKey = languageKey;
	}

	@Override
	public String getLanguageKey() {
		return this.languageKey;
	}
}
