package core.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

/**
 * Switches the cursor to/from WaitCursor state and blocks all input for
 * components on the top level ancestor root pane.
 * <p/>
 * If an exception occours after the wait cursor was started, and the cursor is
 * not resetted with
 * {@link CursorToolkit#stopWaitCursor(javax.swing.JComponent) }, the GUI will
 * stay blocked and unusable. To prevent this, use
 * <code>startWaitCursor/stopWaitCursor</code> with a <code>try</code> -
 * <code>finally</code> block:
 * <p/>
 * <blockquote>
 * 
 * <pre>
 * CursorToolkit.startWaitCursor(this);
 * try {
 * 	// do time consuming stuff
 * } finally {
 * 	CursorToolkit.stopWaitCursor(this);
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 */
public class CursorToolkit {

	/**
	 * 'Do nothing' MouseAdapter for consuming mouse events.
	 */
	private static final MouseAdapter mouseAdapter;
	/**
	 * KeyEventDispatcher which consumes all key events.
	 */
	private static final KeyEventDispatcher keyEventConsumer;

	static {
		mouseAdapter = new MouseAdapter() {
			// do nothing
		};

		keyEventConsumer = new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				// consume the key event
				e.consume();
				return true;
			}
		};
	}

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private CursorToolkit() {
		// do nothing
	}

	/**
	 * Chanages the cursor to wait cursor ({@link java.awt.Cursor#WAIT_CURSOR}).
	 * The visibility of the glassPane from the top level ancestor root pane is
	 * set to <code>true</code> to grab all keyboard and mouse input.
	 * 
	 * @param component
	 *            The component to start the wait cursor for. The wait cursor is
	 *            switched on for top level ancestor root pane of this component
	 *            and all interaction with components on this root pane will be
	 *            blocked.
	 * @throws IllegalStateException
	 *             If the given <code>componenty</code> is not (yet) added to a
	 *             window.
	 */
	public static synchronized void startWaitCursor(JComponent component)
			throws IllegalStateException {
		RootPaneContainer rootPane = (RootPaneContainer) component
				.getTopLevelAncestor();
		if (rootPane == null) {
			throw new IllegalStateException(
					"Component has not been added to a window yet!");
		}

		Component rootGlassPane = rootPane.getGlassPane();
		rootGlassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// add a 'do-nothing-MouseListener' to prevent any mouse events from
		// getting through to underlying components
		rootGlassPane.addMouseListener(mouseAdapter);
		// add a KeyEventDispatcher to consume all KeyEvents
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(keyEventConsumer);
		// make the glassPane temporary visible to grab all keyboard
		// and mouse input
		rootGlassPane.setVisible(true);
	}

	/**
	 * Switches the cursor back to {@link java.awt.Cursor#DEFAULT_CURSOR} and
	 * allows input for components on the top level ancestor root pane.
	 * 
	 * @param component
	 *            The component to stop the wait cursor for. The wait cursor is
	 *            switched off for top level ancestor root pane of this
	 *            component.
	 * @throws IllegalStateException
	 *             If the given <code>componenty</code> is not (yet) added to a
	 *             window.
	 */
	public static synchronized void stopWaitCursor(JComponent component) {

		RootPaneContainer rootPane = (RootPaneContainer) component
				.getTopLevelAncestor();
		if (rootPane == null) {
			throw new IllegalStateException(
					"Component has not been added to a window yet!");
		}

		Component rootGlassPane = rootPane.getGlassPane();
		rootGlassPane.setCursor(Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		rootGlassPane.removeMouseListener(mouseAdapter);
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(keyEventConsumer);
		rootGlassPane.setVisible(false);
	}
}