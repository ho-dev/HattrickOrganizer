package core.gui.comp.panel;

import core.gui.CursorToolkit;
import core.gui.IRefreshable;
import core.gui.RefreshManager;

import java.awt.event.HierarchyEvent;

import javax.swing.JComponent;

/**
 * Lazy initialization/refresh logic shared by {@link LazyPanel} and {@link LazyImagePanel}.
 * <p>
 * The initializer runs once, when the component is shown for the first time. The updater runs
 * whenever a refresh is requested while the component is showing, or as soon as it gets shown
 * after a refresh was requested. Both run with a wait cursor.
 */
final class LazyPanelSupport {

	private final JComponent component;
	private final Runnable initializer;
	private final Runnable updater;
	private boolean initialized = false;
	private boolean needsRefresh = false;
	private IRefreshable refreshable;

	LazyPanelSupport(JComponent component, Runnable initializer, Runnable updater) {
		this.component = component;
		this.initializer = initializer;
		this.updater = updater;
		component.addHierarchyListener(this::hierarchyChanged);
	}

	/**
	 * Registers/unregisters the component at the {@link RefreshManager}.
	 *
	 * @param register <code>true</code> to register, <code>false</code> to unregister if registered.
	 */
	void registerRefreshable(boolean register) {
		if (register) {
			if (this.refreshable == null) {
				this.refreshable = () -> {
					if (component.isShowing()) {
						callUpdate();
					} else {
						needsRefresh = true;
					}
				};
				RefreshManager.instance().registerRefreshable(this.refreshable);
			}
		} else if (this.refreshable != null) {
			RefreshManager.instance().unregisterRefreshable(this.refreshable);
			this.refreshable = null;
		}
	}

	boolean isInitialized() {
		return this.initialized;
	}

	boolean needsRefresh() {
		return this.needsRefresh;
	}

	void setNeedsRefresh(boolean needsRefresh) {
		this.needsRefresh = needsRefresh;
		if (needsRefresh && component.isShowing()) {
			callUpdate();
		}
	}

	private void hierarchyChanged(HierarchyEvent e) {
		if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && component.isShowing()) {
			if (!initialized) {
				callInitialize();
			}
			if (needsRefresh) {
				callUpdate();
			}
		}
	}

	private void callInitialize() {
		CursorToolkit.startWaitCursor(component);
		try {
			initializer.run();
			this.initialized = true;
		} finally {
			CursorToolkit.stopWaitCursor(component);
		}
	}

	private void callUpdate() {
		CursorToolkit.startWaitCursor(component);
		try {
			updater.run();
			this.needsRefresh = false;
		} finally {
			CursorToolkit.stopWaitCursor(component);
		}
	}
}
