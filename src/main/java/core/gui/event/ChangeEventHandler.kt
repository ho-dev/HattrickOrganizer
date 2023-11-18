package core.gui.event

import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

/**
 * Support for handling [ChangeListener]s.
 */
open class ChangeEventHandler {
    /** CopyOnWriteArrayList ensures thread-safety  */
    private val changeListeners: MutableList<ChangeListener> = CopyOnWriteArrayList()
    fun addChangeListener(changeListener: ChangeListener) {
        changeListeners.add(changeListener)
    }

    fun removeChangeListener(changeListener: ChangeListener) {
        if (changeListeners.contains(changeListener)) {
            changeListeners.remove(changeListener)
        }
    }

    protected fun fireChangeEvent(event: ChangeEvent?) {
        for (listener in changeListeners) {
            listener.stateChanged(event)
        }
    }
}
