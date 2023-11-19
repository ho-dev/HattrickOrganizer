package core.gui

import java.awt.Cursor
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import javax.swing.JComponent
import javax.swing.RootPaneContainer

/**
 * Switches the cursor to/from WaitCursor state and blocks all input for
 * components on the top level ancestor root pane.
 *
 *
 * If an exception occurs after the wait cursor was started, and the cursor is
 * not reset with
 * [CursorToolkit.stopWaitCursor], the GUI will
 * stay blocked and unusable. To prevent this, use
 * `startWaitCursor/stopWaitCursor` with a `try` -
 * `finally` block:
 *
 *
 * <blockquote>
 *
 * <pre>
 * CursorToolkit.startWaitCursor(this);
 * try {
 * // do time consuming stuff
 * } finally {
 * CursorToolkit.stopWaitCursor(this);
 * }
</pre> *
 *
</blockquote> *
 *
 */
object CursorToolkit {
    /**
     * 'Do nothing' MouseAdapter for consuming mouse events.
     */
    private var mouseAdapter: MouseAdapter? = null

    /**
     * KeyEventDispatcher which consumes all key events.
     */
    private var keyEventConsumer: KeyEventDispatcher? = null

    init {
        mouseAdapter = object : MouseAdapter() { // do nothing
        }
        keyEventConsumer = KeyEventDispatcher { e: KeyEvent ->
            // consume the key event
            e.consume()
            true
        }
    }

    /**
     * Changes the cursor to wait cursor ([java.awt.Cursor.WAIT_CURSOR]).
     * The visibility of the glassPane from the top level ancestor root pane is
     * set to `true` to grab all keyboard and mouse input.
     *
     * @param component
     * The component to start the wait cursor for. The wait cursor is
     * switched on for top level ancestor root pane of this component
     * and all interaction with components on this root pane will be
     * blocked.
     * @throws IllegalStateException
     * If the given `component` is not (yet) added to a
     * window.
     */
    @JvmStatic
	@Synchronized
    @Throws(IllegalStateException::class)
    fun startWaitCursor(component: JComponent) {
        val rootPane = component.getTopLevelAncestor() as RootPaneContainer
            ?: throw IllegalStateException("Component has not been added to a window yet!")
        val rootGlassPane = rootPane.glassPane
        rootGlassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))
        // add a 'do-nothing-MouseListener' to prevent any mouse events from
        // getting through to underlying components
        rootGlassPane.addMouseListener(mouseAdapter)
        // add a KeyEventDispatcher to consume all KeyEvents
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventConsumer)
        // make the glassPane temporary visible to grab all keyboard
        // and mouse input
        rootGlassPane.isVisible = true
    }

    /**
     * Switches the cursor back to [java.awt.Cursor.DEFAULT_CURSOR] and
     * allows input for components on the top level ancestor root pane.
     *
     * @param component
     * The component to stop the wait cursor for. The wait cursor is
     * switched off for top level ancestor root pane of this
     * component.
     * @throws IllegalStateException
     * If the given `component` is not (yet) added to a
     * window.
     */
    @JvmStatic
	@Synchronized
    fun stopWaitCursor(component: JComponent) {
        val rootPane = component.getTopLevelAncestor() as RootPaneContainer
            ?: throw IllegalStateException("Component has not been added to a window yet!")
        val rootGlassPane = rootPane.glassPane
        rootGlassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
        rootGlassPane.removeMouseListener(mouseAdapter)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventConsumer)
        rootGlassPane.isVisible = false
    }
}