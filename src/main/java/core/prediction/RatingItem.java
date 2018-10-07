package core.prediction;

class RatingItem {
	private String description;
	private int value;

	RatingItem(String string, int i) {
		description = string;
		value = i;
	}

	public final void setDescription(String string) {
		description = string;
	}

	public final String getDescription() {
		return description;
	}

	public final void setValue(int i) {
		value = i;
	}

	public final int getValue() {
		return value;
	}

	@Override
	public final String toString() {
		return description;
	}
}
