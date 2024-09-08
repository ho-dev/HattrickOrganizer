package module.lineup.substitution.positionchooser;

public class PositionSelectionEvent {

	private final Integer position;
	private final Change change;

	public enum Change {
		SELECTED, DESELECTED
    }

	public PositionSelectionEvent(Integer position, Change change) {
		this.position = position;
		this.change = change;
	}

	public Integer getPosition() {
		return this.position;
	}

	public Change getChange() {
		return this.change;
	}

}
