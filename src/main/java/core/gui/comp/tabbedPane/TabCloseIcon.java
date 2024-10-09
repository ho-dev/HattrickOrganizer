package core.gui.comp.tabbedPane;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final class TabCloseIcon implements Icon {
	private final Icon mIcon = ThemeManager.getIcon(HOIconName.TABBEDPANE_CLOSE);
	private transient Rectangle mPosition = null;
	private int xOffset = 0;
	private final JTabbedPane mTabbedPane;

	TabCloseIcon(final JTabbedPane mTabbedPane, int xOffset) {
		this.xOffset = xOffset;
		this.mTabbedPane = mTabbedPane;
		this.mTabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// asking for isConsumed is *very* important, otherwise more
				// than one tab might get closed!
				if (!e.isConsumed() && mPosition.contains(e.getX(), e.getY())) {
					closeTab();
					e.consume();
				}
			}
		});
	}

	/**
	 * when painting, remember last position painted. add extra xOffset pixels
	 * to allow for Nimbus skin
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		mPosition = new Rectangle(x + xOffset, y, getIconWidth(), getIconHeight());
		mIcon.paintIcon(c, g, x + xOffset, y);
	}

	/**
	 * Returns the total width of the close icon, including <code>xOffset</code>.
	 */
	@Override
	public int getIconWidth() {
		return mIcon.getIconWidth() + xOffset;
	}

	/**
	 * just delegate
	 */
	@Override
	public int getIconHeight() {
		return mIcon.getIconHeight();
	}

	private void closeTab() {
		String title = TranslationFacility.tr("confirmation.title");
		String message = TranslationFacility.tr("tab.close.confirm.msg");
		if (JOptionPane.showConfirmDialog(mTabbedPane, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			int index = mTabbedPane.getSelectedIndex();
			mTabbedPane.remove(index);
		}
	}
}
