package core.util;

import core.gui.ExceptionDialog;

import java.awt.EventQueue;

/**
 * 
 * @author kruescho
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(final Thread thread, final Throwable throwable) {
		logException(throwable);
		try {
			if (EventQueue.isDispatchThread()) {
				showErrorDialog(throwable);
			} else {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						showErrorDialog(throwable);
					}
				});
			}
		} catch (Exception ex) {
			logException(ex);
		}
	}

	private void logException(Throwable throwable) {
		HOLogger.instance().log(getClass(), throwable);
	}

	private void showErrorDialog(Throwable throwable) {
		ExceptionDialog dlg = new ExceptionDialog("Error", throwable);
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);
	}

	/**
	 * To be called from the EventDispatcherThread if an exception occurs while
	 * a modal dialog is shown. To let this work, the ExceptionHandler has to be
	 * set as the via 
	 * <blockquote>
	 * <pre>
	 * System.setProperty(&quot;sun.awt.exception.handler&quot;, ExceptionHandler.class.getName());
	 * </pre>
	 * </blockquote>
	 * <p/>
	 * Otherwise an error dialog would not be shown, the EventDispatcherThread
	 * would simply print the stacktrace to the console (which is normally not)
	 * visible. This a kind of work around which might be removed in later
	 * versions of the JDK.
	 * 
	 * @param e
	 */
	public void handle(Throwable e) {
		uncaughtException(null, e);
	}
}
