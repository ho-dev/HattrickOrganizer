package core.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public class GUIUtils {

	private GUIUtils() {
	}

	/**
	 * Equalizes the sizes of the given components. All components will have the
	 * height of the highest and the width of the widest component. The sizes
	 * will be set with {@link Component#setMinimumSize(java.awt.Dimension) } and
	 * {@link Component#setPreferredSize(java.awt.Dimension) }.
	 * 
	 * @param components
	 *            the components to equalize in size.
	 */
	public static void equalizeComponentSizes(Component... components) {
		Dimension size = getMaximumPreferredSize(components);
		for (Component component : components) {
			component.setPreferredSize(size);
			component.setMinimumSize(size);
		}
	}

	/**
	 * Gets the maximum preferred size from a group of components. The maximum
	 * preferred size is estimated the following way: the maximum preferred
	 * height and the maximum preferred width is determined (over all
	 * components) an {@link Dimension} object is created with this height and
	 * width.
	 * 
	 * @param components
	 *            the components to get the maximum preferred width and maximum
	 *            preferred height from.
	 * @return the maximum preferred width and maximum preferred height as a
	 *         dimension object.
	 */
	public static Dimension getMaximumPreferredSize(Component... components) {
		int maxWidth = 0;
		int maxHeight = 0;

		for (Component component : components) {
			Dimension preferredSize = component.getPreferredSize();
			if (preferredSize.getWidth() > maxWidth) {
				maxWidth = (int) preferredSize.getWidth();
			}
			if (preferredSize.getHeight() > maxHeight) {
				maxHeight = (int) preferredSize.getHeight();
			}
		}
		return new Dimension(maxWidth, maxHeight);
	}

	/**
	 * Decorates the given dialog with a keyboard action which calls
	 * {@link JDialog#dispose() } when the ESC key is hit while the dialog or one
	 * of its subcomponents has the focus.
	 * 
	 * @param dialog
	 *            the dialog to decorate.
	 */
	public static void decorateWithDisposeOnESC(final JDialog dialog) {
		dialog.getRootPane().registerKeyboardAction(new AbstractAction() {

			private static final long serialVersionUID = 4054546658098440109L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	/**
	 * Decorates the given dialog with a action which will be executed when the
	 * ESC key is hit while the dialog or one of its subcomponents has the
	 * focus.
	 * 
	 * @param dialog
	 *            the dialog to decorate.
	 * @param action
	 *            to execute when ESC was pressed.
	 */
	public static void decorateWithActionOnESC(JDialog dialog, Action action) {
		dialog.getRootPane().registerKeyboardAction(action,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	/**
	 * Selects the first row in a table. This method is tolerant, it does
	 * nothing if the given table is <code>null</code> or does not contain any
	 * row.
	 * 
	 * @param table
	 *            the table to select the first row from.
	 */
	public static void selectFirstRow(JTable table) {
		if (table != null && table.getRowCount() > 0) {
			table.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	/**
	 * Centers the <code>window</code> over the given <code>component</code>.
	 * The <code>window</code> might not be exactly centered over the
	 * <code>component</code> if the <code>window</code> would not fit on the
	 * screen if set to the calculated position.
	 * <p>
	 * The window instance must be visible, otherwise the location won't be set.
	 * 
	 * @param window
	 *            The <code>window</code> to be positioned.
	 * @param component
	 *            The <code>component</code>, the <code>window</code> should be
	 *            centered over.
	 */
	public static void setLocationCenteredToComponent(Window window, Component component) {
		if (component != null && component.isVisible()) {
			Point componentLocation = component.getLocationOnScreen();
			Point componentCenter = new Point(componentLocation.x + component.getWidth() / 2,
					componentLocation.y + component.getHeight() / 2);

			window.setLocation(componentCenter.x - window.getWidth() / 2, componentCenter.y
					- window.getHeight() / 2);
		}
	}
}
