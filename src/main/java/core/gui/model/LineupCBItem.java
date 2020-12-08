// %1683810490:de.hattrickorganizer.gui.model%
package core.gui.model;

import module.lineup.LineupsComparisonHistoryPanel;
import module.lineup.Lineup;
import org.jetbrains.annotations.Nullable;

/**
 * Named Lineup item.
 */
public class LineupCBItem {
	private Lineup m_clAufstellung;
	private String m_sText;

	/**
	 * Creates a new AufstellungCBItem object.
	 */
	public LineupCBItem(String text, @Nullable Lineup aufstellung) {
		m_sText = text;
		m_clAufstellung = aufstellung;
	}

	/**
	 * Check, if displayed.
	 */
	public final boolean isAngezeigt() {
		return LineupsComparisonHistoryPanel.isAngezeigt(this);
	}

	/**
	 * Set the Lineup.
	 */
	public final void setAufstellung(Lineup aufstellung) {
		m_clAufstellung = aufstellung;
	}

	public final Lineup getAufstellung() {
		return m_clAufstellung;
	}

	/**
	 * Set a name.
	 */
	public final void setText(String text) {
		m_sText = text;
	}

	/**
	 * Get the name.
	 */
	public final String getText() {
		return m_sText;
	}

	/**
	 * Duplicate a AufstellungCBItem.
	 */
	public final LineupCBItem duplicate() {
		return new LineupCBItem(this.getText(), this.getAufstellung().duplicate());
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof LineupCBItem) {
			LineupCBItem temp = (LineupCBItem) obj;

			if ((temp.getText() != null) && temp.getText().equals(getText())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public final String toString() {
		return m_sText;
	}
}
