package core.gui.comp.panel;

import core.gui.CursorToolkit;
import core.gui.IRefreshable;
import core.gui.RefreshManager;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

/**
 * ImagePanel subclass which provides lazy initialization/refresh.
 */
public abstract class LazyImagePanel extends ImagePanel {

	private static final long serialVersionUID = -9087071738813776734L;
	private boolean initialized = false;
	private boolean needsRefresh = false;
	private IRefreshable refreshable;

	public LazyImagePanel() {
		super();
		addHierarchyListener();
	}

	public LazyImagePanel(java.awt.LayoutManager layout) {
		super(layout);
		addHierarchyListener();
	}

	public LazyImagePanel(boolean forprint) {
		super(forprint);
		addHierarchyListener();
	}

	public LazyImagePanel(java.awt.LayoutManager layout, boolean forprint) {
		super(layout, forprint);
		addHierarchyListener();
	}

	/**
	 * Registers/unregisters this panel at the {@link RefreshManager} (default
	 * it not registered). If registered and the {@link RefreshManager} forces
	 * requests a refresh, the {@link #update()} method is called immediately if
	 * the panel currently showing. If the panel is not currently showing it is
	 * marked as needed to be refreshed and the {@link #update()} method is
	 * called as soon as the panel gets shown.
	 * 
	 * @param register
	 *            <code>true</code> to register this panel at the
	 *            {@link RefreshManager}. If <code>false</code> the panel is
	 *            unregistered if it was already registerd.
	 */
	public void registerRefreshable(boolean register) {
		if (register) {
			if (this.refreshable == null) {
				this.refreshable = new IRefreshable() {

					@Override
					public void refresh() {
						if (isShowing()) {
							callUpdate();
						} else {
							needsRefresh = true;
						}
					}
				};
				RefreshManager.instance().registerRefreshable(this.refreshable);
			} else {
				RefreshManager.instance().unregisterRefreshable(this.refreshable);
			}
		}
	}

	/**
	 * Indicates if the panel was already initialized.
	 * 
	 * @return <code>true</code> if the panel was already initialized,
	 *         <code>false</code> otherwise.
	 */
	public boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * Indicates if the panel needs to be refreshed.
	 * 
	 * @return <code>true</code> if the panel needs to be refreshed,
	 *         <code>false</code> otherwise.
	 */
	public boolean needsRefresh() {
		return this.needsRefresh;
	}

	/**
	 * Marks the panel as needs to be refreshed. If the panel is currently
	 * showing, the {@link #update()} method is called immediately. If not, the
	 * {@link #update()} is called as soon as the panel is shown.
	 * 
	 * @param needsRefresh
	 */
	public void setNeedsRefresh(boolean needsRefresh) {
		this.needsRefresh = needsRefresh;
		if (needsRefresh && isShowing()) {
			callUpdate();
		}
	}

	/**
	 * This method has to be overwritten by subclasses to do the initialization
	 * (create components, etc). This method is only called ones when the panel
	 * gets shown the first time. During execution time of this method, an
	 * WaitCursor is shown and the UI is blocked for key and mouse events (if
	 * the method was called by this class).
	 */
	protected abstract void initialize();

	/**
	 * This method has to be overwritten by subclasses to update the view when
	 * the model changes. During execution time of this method, an
	 * WaitCursor is shown and the UI is blocked for key and mouse events (if
	 * the method was called by this class).
	 */
	protected abstract void update();

	private void addHierarchyListener() {
		addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if ((HierarchyEvent.SHOWING_CHANGED == (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) && isShowing())) {
					if (!initialized) {
						callInitialize();
					}
					if (needsRefresh) {
						callUpdate();
					}
				}
			}
		});
	}

	private void callInitialize() {
		CursorToolkit.startWaitCursor(this);
		try {
			initialize();
			this.initialized = true;
		} finally {
			CursorToolkit.stopWaitCursor(this);
		}
	}

	private void callUpdate() {
		CursorToolkit.startWaitCursor(this);
		try {
			update();
			this.needsRefresh = false;
		} finally {
			CursorToolkit.stopWaitCursor(this);
		}
	}

}
