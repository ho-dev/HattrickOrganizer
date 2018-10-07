package core.gui;

import core.util.HOLogger;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import javax.swing.JFrame;


/**
 * Singleton to switch a JFrame from normal windows mode to full screen
 * mode and vice versa. The properties of the Frame are determined and
 * restored when returning back to windowed mode.
 *
 * @author leutloff@users.sourceforge.net
 * @copyright GPL v2 or later, or the copyright of Hattrick Organizer
 */
class FullScreen
{
	protected boolean wasUndecorated; ///< isUndecorated of the Frame to restore
    protected boolean wasResizable; ///< isResizable of the Frame to restore
    protected Dimension actDimension; ///< getSize of the Frame to restore
    protected Point actLocation; ///< getLocation of the Frame to restore
    protected int actExtendedState; ///< getExtendedState of the Frame to restore

	private boolean isInitialized; ///< set when the physical device is determined
    private boolean isFullScreenSupported; ///< set when the actual system platform does support the full screen mode
    private boolean isFullScreen; ///< is set to true when full screen mode is enabled
    private GraphicsDevice device; ///< physical device used for the full screen mode
	private Dimension deviceDimension; ///< size of the physical device used for the full screen mode
	
	private Thread threadToAvoidJvmTermination; ///< non daemon thread to avoid unwanted JVM termination
	private boolean isThreadRunning; ///< set/reset when threadToAvoidJvmTermination is started or stopped

	private static FullScreen m_clFullScreen = null; ///< instance of the singleton

	/**
	 * Constructor.
	 */
    private FullScreen() {
    	isInitialized = false;
    	isFullScreenSupported = false;
    	isFullScreen = false;

    	device = null;
    	deviceDimension = null;
    	
    	threadToAvoidJvmTermination = null;
    	isThreadRunning = false;
    }


	/**
	 * Initialize the internally used data.
	 * @param frame the frame that is manipulated and displayed in normal windowed or full screen mode
	 */
	void init(JFrame frame) {
		if (!isInitialized) {
	        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        GraphicsDevice[] devices = env.getScreenDevices();
	        boolean bFound = false;
	        final GraphicsConfiguration frameConf = frame.getGraphicsConfiguration();
	        for (int i = 0; i < devices.length; i++) {
	        	GraphicsConfiguration[] confs = devices[i].getConfigurations();
//				HOLogger.instance().debug(getClass(), "FullScreen: "+confs.length+" configs for dev "+devices[i].getIDstring()+" found, iFSS="+devices[i].isFullScreenSupported());
		        for (int k = 0; k < confs.length; k++) {
//					HOLogger.instance().debug(getClass(), "FullScreen: Conf found: "+k+"="+confs[k].toString());
		        	if (frameConf == confs[k]) {
						HOLogger.instance().debug(getClass(), "FullScreen: Matching conf found: ["+k+"]="+confs[k].toString()+" for dev["+i+"]="+devices[i].getIDstring());
		        		device = devices[i];
		        		bFound = true;
		        		break;
		        	}
		        }
	        }
	        if (!bFound) {
				HOLogger.instance().debug(getClass(), "FullScreen: NO matching conf found");
	        	device = devices[0]; // if nothing matches stay with the first one
	        }
	        try {
				DisplayMode dm = device.getDisplayMode();
				deviceDimension = new Dimension(dm.getWidth(), dm.getHeight());
				// isFullScreenSupported on X11 > 1.3.0 requires fixed java 6 u10 or greater,
				// see http://bugs.sun.com/view_bug.do?bug_id=6636469 for details
				isFullScreenSupported = device.isFullScreenSupported();
				HOLogger.instance().debug(getClass(), "FullScreen: dev "+device.getIDstring()+", isFullScreenSupported="+device.isFullScreenSupported());
			} catch (Exception e) {
				HOLogger.instance().debug(getClass(), "Error checking FullScreen support: " + e);
				isFullScreenSupported = false;
			}
//			if (!isFullScreenSupported) {
//				HOLogger.instance().debug(getClass(), "FullScreen: Forcing fake-fullscreen");
//		        isFullScreenSupported = true;
//			}
			if (isFullScreenSupported) {
				startBlockingThread();
			}
			isInitialized = true;
		}
	}
	
	/**
	 * Returns true if full screen mode is possible.
	 * @param frame the frame that is manipulated and displayed in normal windowed or full screen mode
	 */
	public boolean isFullScreenSupported(JFrame frame) {
		init(frame);
		return isFullScreenSupported;
	}

	/**
	 * Force calling @see init() again when @see toggle() is called the next time.
	 * Should be called when the device default resolution has changed.
	 */
	public void invalidate() {
		isInitialized = false;
	}

	/**
	 * Switch from normal to full screen mode and vice versa.
	 * @param frame the frame that is manipulated and displayed in normal windowed or full screen mode
	 */
	public void toggle(JFrame frame) {
		init(frame);
		if (!isFullScreenSupported) {
			return; // nothing to do
		}
        if (isFullScreen) {
            setupNormalMode(frame);
        } else {
        	setupFullScreen(frame);
        }
	}

	/**
	 * Restore normal mode and stops the non daemon thread. Should be called on exit.
	 * @param frame the frame that is manipulated and displayed in normal windowed mode
	 */
	public void restoreNormalMode(JFrame frame) {
		init(frame);
		if (!isFullScreenSupported) {
			return; // nothing to do
		}
        setupNormalMode(frame);
		stopBlockingThread();
	}


	/**
	 * Returning to normal windowed mode. It is save to call it when not in full screen mode.
	 * @param frame the frame that is manipulated and displayed in normal windowed mode
	 */
	private void setupNormalMode(JFrame frame) {
		if (!isFullScreen) {
			return;
		}
		HOLogger.instance().debug(getClass(), "FullScreen: Switching to normal mode");
		// return to normal Windowed mode
		frame.setVisible(false);
		frame.dispose();
		device.setFullScreenWindow(null);
		// restore old values
		if (!frame.isDisplayable()) {
			frame.setUndecorated(wasUndecorated);
		}
		frame.setResizable(true);
		frame.setLocation(actLocation);
		frame.setSize(actDimension);
		frame.setResizable(wasResizable);
		// show frame again
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(actExtendedState); //should be called after setVisible()
		isFullScreen = false;
	}


	/**
	 * Setting up the full screen mode.
	 * @param frame the frame that is manipulated and displayed in full screen mode
	 */
	private void setupFullScreen(JFrame frame) {
		if (isFullScreen) {
			return;
		}
		HOLogger.instance().debug(getClass(), "FullScreen: Switching to fullscreen mode");
		// get values for restore
		wasUndecorated = frame.isUndecorated();
		actDimension = frame.getSize();
		wasResizable = frame.isResizable();
		actLocation = frame.getLocation();
		actExtendedState = frame.getExtendedState();
		// set to Full-screen Mode
		frame.setVisible(false);
		frame.dispose();
		if (!frame.isDisplayable()) {
			frame.setUndecorated(true);
		}
		device.setFullScreenWindow(frame);
		frame.setResizable(true);
		frame.setSize(deviceDimension);
		frame.setResizable(false); // Prohibit any resize operations that are not allowed
		// show frame again
		frame.validate();
		frame.setVisible(true);
		isFullScreen = true;
	}


	/**
	 * To avoid an unexpected termination of the JVM a non daemon thread is started.
	 * @see http://java.sun.com/javase/6/docs/api/java/awt/doc-files/AWTThreadIssues.html#Autoshutdown
	 */
	void startBlockingThread() {
		if (isThreadRunning) {
			return;
		}
		Runnable r = new Runnable() {
	        public void run() {
	            Object o = new Object();
	            try {
	                synchronized (o) {
	                    o.wait();
	                }
	            } catch (InterruptedException ie) {
					HOLogger.instance().debug(getClass(), "FullScreen: Blocking thread terminated (interrupted).");
	            }
				HOLogger.instance().debug(getClass(), "FullScreen: Blocking thread finished.");
	        }
	    };
	    threadToAvoidJvmTermination = new Thread(r);
	    threadToAvoidJvmTermination.setDaemon(false);
	    threadToAvoidJvmTermination.setName("Avoid JVM Termination");
	    threadToAvoidJvmTermination.start();
	    isThreadRunning = true;
		HOLogger.instance().debug(getClass(), "FullScreen: Blocking thread started.");
	}
	/**
	 * stops the non daemon thread to avoid the unwanted JVM termination.
	 */
	void stopBlockingThread() {
		if (!isThreadRunning) {
			return;
		}
		threadToAvoidJvmTermination.interrupt();
		threadToAvoidJvmTermination = null;
	    isThreadRunning = false;
	}
	
    /**
	 * Singleton.
 	 * @return single instance of this class
	 */

	public static FullScreen instance() {
    	 if (m_clFullScreen == null) {
    		m_clFullScreen = new FullScreen();
    	 }
         return m_clFullScreen;
	}
}
