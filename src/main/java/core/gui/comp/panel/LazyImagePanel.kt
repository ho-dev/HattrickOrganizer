package core.gui.comp.panel

import core.gui.CursorToolkit
import core.gui.IRefreshable
import core.gui.RefreshManager
import core.gui.RefreshManager.registerRefreshable
import core.gui.RefreshManager.unregisterRefreshable
import java.awt.LayoutManager
import java.awt.event.HierarchyEvent

/**
 * ImagePanel subclass which provides lazy initialization/refresh.
 */
abstract class LazyImagePanel : ImagePanel {
    /**
     * Indicates if the panel was already initialized.
     *
     * @return `true` if the panel was already initialized,
     * `false` otherwise.
     */
    var isInitialized: Boolean = false
        private set
    private var needsRefresh = false
    private var refreshable: IRefreshable? = null

    constructor() : super() {
        addHierarchyListener()
    }

    constructor(layout: LayoutManager?) : super(layout) {
        addHierarchyListener()
    }

    constructor(forprint: Boolean) : super(forprint) {
        addHierarchyListener()
    }

    constructor(layout: LayoutManager?, forprint: Boolean) : super(layout, forprint) {
        addHierarchyListener()
    }

    /**
     * Registers/unregisters this panel at the [RefreshManager] (default
     * it not registered). If registered and the [RefreshManager] forces
     * requests a refresh, the [.update] method is called immediately if
     * the panel currently showing. If the panel is not currently showing it is
     * marked as needed to be refreshed and the [.update] method is
     * called as soon as the panel gets shown.
     *
     * @param register
     * `true` to register this panel at the
     * [RefreshManager]. If `false` the panel is
     * unregistered if it was already registerd.
     */
    fun registerRefreshable(register: Boolean) {
        if (register) {
            if (refreshable == null) {
                refreshable = object : IRefreshable {
                    override fun refresh() {
                        if (isShowing()) {
                            callUpdate()
                        } else {
                            needsRefresh = true
                        }
                    }
                }
                registerRefreshable(refreshable as IRefreshable)
            } else {
                unregisterRefreshable(refreshable!!)
            }
        }
    }

    /**
     * Indicates if the panel needs to be refreshed.
     *
     * @return `true` if the panel needs to be refreshed,
     * `false` otherwise.
     */
    fun needsRefresh(): Boolean {
        return needsRefresh
    }

    /**
     * Marks the panel as needs to be refreshed. If the panel is currently
     * showing, the [.update] method is called immediately. If not, the
     * [.update] is called as soon as the panel is shown.
     *
     * @param needsRefresh
     */
    fun setNeedsRefresh(needsRefresh: Boolean) {
        this.needsRefresh = needsRefresh
        if (needsRefresh && isShowing()) {
            callUpdate()
        }
    }

    /**
     * This method has to be overwritten by subclasses to do the initialization
     * (create components, etc). This method is only called ones when the panel
     * gets shown the first time. During execution time of this method, an
     * WaitCursor is shown and the UI is blocked for key and mouse events (if
     * the method was called by this class).
     */
    protected abstract fun initialize()

    /**
     * This method has to be overwritten by subclasses to update the view when
     * the model changes. During execution time of this method, an
     * WaitCursor is shown and the UI is blocked for key and mouse events (if
     * the method was called by this class).
     */
    protected abstract fun update()
    private fun addHierarchyListener() {
        addHierarchyListener { e ->
            if (HierarchyEvent.SHOWING_CHANGED.toLong() == e.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong() && isShowing()) {
                if (!isInitialized) {
                    callInitialize()
                }
                if (needsRefresh) {
                    callUpdate()
                }
            }
        }
    }

    private fun callInitialize() {
        CursorToolkit.startWaitCursor(this)
        try {
            initialize()
            this.isInitialized = true
        } finally {
            CursorToolkit.stopWaitCursor(this)
        }
    }

    private fun callUpdate() {
        CursorToolkit.startWaitCursor(this)
        try {
            update()
            needsRefresh = false
        } finally {
            CursorToolkit.stopWaitCursor(this)
        }
    }

    companion object {
        private const val serialVersionUID = -9087071738813776734L
    }
}
