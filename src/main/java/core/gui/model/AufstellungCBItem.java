// %1683810490:de.hattrickorganizer.gui.model%
package core.gui.model;

import module.lineup.AufstellungsVergleichHistoryPanel;
import module.lineup.Lineup;

/**
 * Named Lineup item.
 */
public class AufstellungCBItem {
	private Lineup m_clAufstellung;
	private String m_sText;

	/**
	 * Creates a new AufstellungCBItem object.
	 */
	public AufstellungCBItem(String text, Lineup aufstellung) {
		m_sText = text;
		m_clAufstellung = aufstellung;
	}

	/**
	 * Check, if displayed.
	 */
	public final boolean isAngezeigt() {
		return AufstellungsVergleichHistoryPanel.isAngezeigt(this);
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
	public final AufstellungCBItem duplicate() {
		return new AufstellungCBItem(this.getText(), this.getAufstellung().duplicate());
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof AufstellungCBItem) {
			AufstellungCBItem temp = (AufstellungCBItem) obj;

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
