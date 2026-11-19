package core.gui.comp.panel;

import core.gui.RefreshManager;

import javax.swing.JPanel;

/**
 * JPanel subclass which provides lazy initialization/refresh.
 */
public abstract class LazyPanel extends JPanel {

	private final LazyPanelSupport lazySupport = new LazyPanelSupport(this, this::initialize, this::update);

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
		lazySupport.registerRefreshable(register);
	}

	/**
	 * Indicates if the panel was already initialized.
	 *
	 * @return <code>true</code> if the panel was already initialized,
	 *         <code>false</code> otherwise.
	 */
	public boolean isInitialized() {
		return lazySupport.isInitialized();
	}

	/**
	 * Indicates if the panel needs to be refreshed.
	 *
	 * @return <code>true</code> if the panel needs to be refreshed,
	 *         <code>false</code> otherwise.
	 */
	public boolean needsRefresh() {
		return lazySupport.needsRefresh();
	}

	/**
	 * Marks the panel as needs to be refreshed. If the panel is currently
	 * showing, the {@link #update()} method is called immediately. If not, the
	 * {@link #update()} is called as soon as the panel is shown.
	 *
	 * @param needsRefresh
	 */
	public void setNeedsRefresh(boolean needsRefresh) {
		lazySupport.setNeedsRefresh(needsRefresh);
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
}
