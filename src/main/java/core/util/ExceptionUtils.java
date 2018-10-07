package core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Utility class for handling exceptions.
 * 
 */
public class ExceptionUtils {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private ExceptionUtils() {
		// do nothing
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
