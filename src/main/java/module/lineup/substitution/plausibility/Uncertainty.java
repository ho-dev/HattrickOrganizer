package module.lineup.substitution.plausibility;

public enum Uncertainty implements Problem {
	SAME_TACTIC("subs.plausibility.sameTactic");

	private final String languageKey;

	Uncertainty(String languageKey) {
		this.languageKey = languageKey;
	}

	@Override
	public String getLanguageKey() {
		return this.languageKey;
	}
}
