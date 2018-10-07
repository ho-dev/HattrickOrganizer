package core.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Utility class for various IO/file related utility methods.
 * 
 */
public class IOUtils {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private IOUtils() {
	}

	/**
	 * Writes a String to a file. The file will be overwritten if existing.
	 * 
	 * @param content
	 *            the content to write to the file.
	 * @param file
	 *            the file to write.
	 * @param encoding
	 *            the charset to use.
	 * @throws UnsupportedEncodingException
	 *             if the specified encoding is not supported.
	 * @throws FileNotFoundException
	 *             if the given file could not be found.
	 * @throws IOException
	 *             if an error occurs while writing the file.
	 */
	public static void writeToFile(String content, File file, String encoding)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		Writer writer = null;
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), encoding));
			writer.write(content);
		} finally {
			closeQuietly(writer);
		}
	}

	/**
	 * Reads the content of a file into a String.
	 * 
	 * @param file
	 *            the file to read.
	 * @param encoding
	 *            the charset to use.
	 * @return the content of the specified file as a String.
	 * @throws FileNotFoundException
	 *             if the specified file was not found.
	 * @throws UnsupportedEncodingException
	 *             if the specified encoding is not supported.
	 * @throws IOException
	 *             if an error occurs while reading the file.
	 */
	public static String readFromFile(File file, String encoding) throws FileNotFoundException,
			UnsupportedEncodingException, IOException {

		StringBuilder builder = new StringBuilder();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file), encoding);
			char[] buf = new char[1024];
			int r = 0;

			while ((r = reader.read(buf)) != -1) {
				builder.append(buf, 0, r);
			}
		} finally {
			closeQuietly(reader);
		}
		return builder.toString();
	}

	/**
	 * Closes the given {@link Closeable} (e.g. a writer or stream). This method
	 * is null-safe, if the given closeable is null, it does nothing.
	 * 
	 * @param closeable
	 *            the {@link Closeable} to close (writer, stream, ...).
	 * @throws IOException
	 *             if an error occurs while closing the closeable.
	 */
	public static void close(Closeable closeable) throws IOException {
		if (closeable != null) {
			closeable.close();
		}
	}

	/**
	 * Closes a {@link Closeable} (e.g. a writer or stream) quietly (without
	 * throwing any exception. This method is <code>null</code> safe, which
	 * means that nothing will be done if <code>null</code> is provided as a
	 * parameter.
	 * 
	 * @param closeable
	 *            the {@link Closeable} to close (writer, stream, ...).
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				// be quiet
			}
		}
	}

}
