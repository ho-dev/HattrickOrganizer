package core.datatype;

/**
 * @author thomas.werth
 * @version
 */
public class CBItem implements ComboItem {

	private String m_sText;
	private int m_iId;

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
		if (obj instanceof CBItem) {
			final CBItem temp = (CBItem) obj;

			if (this.getId() == temp.getId()) {
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
