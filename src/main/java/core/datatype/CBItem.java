package core.datatype;

/**
 * Combo item that associates an ID to a String.
 *
 * @author thomas.werth
 */
public class CBItem implements ComboItem {

	private final String m_sText;
	private final int m_iId;

	public CBItem(String text, int id) {
		m_sText = text;
		m_iId = id;
	}

	@Override
	public final int getId() {
		return m_iId;
	}

	@Override
	public final String getText() {
		return m_sText;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof CBItem temp) {
			return this.getId() == temp.getId();
		}
		return false;
	}

	@Override
	public final String toString() {
		return m_sText;
	}
}
