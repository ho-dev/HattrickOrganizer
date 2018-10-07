package core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Simple converter to convert an language file for the use in HO. Input must be
 * UTF-16 encoded, the output will be ASCII with Unicode notation.
 * 
 * Note: the Oracle JDK contains a generic tool already, example call:
 * C:\Java\jdk6\bin\native2ascii -encoding utf-16 Hebrew.utf16.properties Hebrew.properties
 */
public class UnicodeConverter {
	private final static String CRLF = "\r\n";

	public static void main(String[] args) throws Exception {
		String input = args.length > 0 ? args[0] : "Hebrew.properties";
		if (args.length < 1) {
			System.out.println("Usage: java UnicodeConverter <filename>\n\tUsing default [Hebrew.properties]");
		}
		BufferedReader r = null;
		StringBuilder ret = new StringBuilder(50000);
		try {
			File f = new File(input);
			if (f == null || !f.exists() || f.isDirectory() || !f.canRead()) {
				System.out.println("Can't access input file '" + input + "'!");
				return;
			}
			r = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF-16"));
			String line;
			int linecount = 0;
			while ((line = r.readLine()) != null) {
				ret.append(unicodeEscape(line));
				ret.append(CRLF);
				linecount++;
			}

			System.out.println("Conversion done for " + linecount + " lines.");
			File out = new File(input + ".ascii");
			BufferedWriter bw = null;
			FileOutputStream fos = null;
			try {
				if (out != null && (!out.exists() || out.canWrite())) {
					if (!out.exists()) {
						out.createNewFile();
					}
					fos = new FileOutputStream(out);
					bw = new BufferedWriter(new OutputStreamWriter(fos));
					bw.write(ret.toString());
					bw.flush();
				} else {
					System.out.println("Error: can not save result to '" + (out != null ? out.getAbsolutePath() : "null") + "'.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					fos.close();
				}
				if (bw != null) {
					bw.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (r != null)
				r.close();
		}
	}

	private static final char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String unicodeEscape(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c >> 7) > 0) {
				sb.append("\\u");
				sb.append(hexChar[(c >> 12) & 0xF]); // append the hex character for the left-most 4-bits
				sb.append(hexChar[(c >> 8) & 0xF]); // hex for the second group of 4-bits from the left
				sb.append(hexChar[(c >> 4) & 0xF]); // hex for the third group
				sb.append(hexChar[c & 0xF]); // hex for the last group, e.g., the right most 4-bits
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
