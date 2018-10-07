package core.util;

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/***
 * <p>
 * This class is used to detect Event Dispatch Thread rule violations<br>
 * See <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</a> for more info
 * </p>
 * <p/>
 * <p>
 * This is a modification of original idea of Scott Delap<br>
 * Initial version of ThreadCheckingRepaintManager can be found here<br>
 * <a href="http://www.clientjava.com/blog/2004/08/20/1093059428000.html">Easily
 * Find Swing Threading Mistakes</a>
 * </p>
 * 
 * Use with: RepaintManager.setCurrentManager( new
 * CheckThreadViolationRepaintManager() );
 * 
 * @author Scott Delap
 * @author Alexander Potochkin
 * @author Noel Winstanley took a copy, loosened up some member variables so it
 *         can be used in unit testing, and removed Java-5isms. and replaced
 *         sys.out with logging https://swinghelper.dev.java.net/
 */
public class CheckThreadViolationRepaintManager extends RepaintManager {

	/***
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(CheckThreadViolationRepaintManager.class.getName());
	// it is recommended to pass the complete check
	private boolean completeCheck = true;
	private WeakReference<JComponent> lastComponent;

	public CheckThreadViolationRepaintManager(boolean completeCheck) {
		this.completeCheck = completeCheck;
	}

	public CheckThreadViolationRepaintManager() {
		this(true);
	}

	public boolean isCompleteCheck() {
		return completeCheck;
	}

	public void setCompleteCheck(boolean completeCheck) {
		this.completeCheck = completeCheck;
	}

	@Override
	public synchronized void addInvalidComponent(JComponent component) {
		checkThreadViolations(component);
		super.addInvalidComponent(component);
	}

	@Override
	public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
		checkThreadViolations(component);
		super.addDirtyRegion(component, x, y, w, h);
	}

	protected void checkThreadViolations(JComponent c) {
		if (!SwingUtilities.isEventDispatchThread()
				&& (completeCheck || c.isShowing())) {
			boolean repaint = false;
			boolean fromSwing = false;
			boolean imageUpdate = false;
			// StackTraceElement[] stackTrace =
			// Thread.currentThread().getStackTrace();
			// NW - can't get trace from Thread in java 1.4, instead, create an
			// exception, and get it from that.
			StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
			// for (StackTraceElement st : stackTrace) {
			for (int i = 0; i < stackTrace.length; i++) {
				StackTraceElement st = stackTrace[i];
				if (repaint && st.getClassName().startsWith("javax.swing.")) {
					fromSwing = true;
				}
				if (repaint && "imageUpdate".equals(st.getMethodName())) {
					imageUpdate = true;
				}
				if ("repaint".equals(st.getMethodName())) {
					repaint = true;
					fromSwing = false;
				}
			}
			if (imageUpdate) {
				// assuming it is java.awt.image.ImageObserver.imageUpdate(...)
				// image was asynchronously updated, that's ok
				return;
			}
			if (repaint && !fromSwing) {
				// no problems here, since repaint() is thread safe
				return;
			}
			// ignore the last processed component
			if (lastComponent != null && c == lastComponent.get()) {
				return;
			}
			lastComponent = new WeakReference<JComponent>(c);
			violationDetected(c, stackTrace);
		}
	}

	/***
	 * a violation has been detected, do with it what you will..
	 * 
	 * @param c
	 * @param stackTrace
	 */
	protected void violationDetected(JComponent c,
			StackTraceElement[] stackTrace) {
		Throwable t = new Throwable("EDT violation");
		t.setStackTrace(stackTrace);
		logger.log(Level.WARNING, c.getClass().getName(), t);
	}
}
