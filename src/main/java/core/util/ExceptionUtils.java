package core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Utility class for handling exceptions.
 * 
 */
public class ExceptionUtils {

	private ExceptionUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
	 * Gets the throwable's stacktrace (the text which will be produced by
	 * {@link Throwable#printStackTrace() }) as a string.
	 * 
	 * @param aThrowable
	 *            the throwable.
	 * @return the throwable's stacktrace as a string.
	 */
	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
}
