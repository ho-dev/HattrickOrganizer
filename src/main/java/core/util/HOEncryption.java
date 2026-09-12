package core.util;

public final class HOEncryption {

    private HOEncryption() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Decrypts the string {@code text} that was encrypted with {@link #encryptString(String)}.
     *
     * @param text encrypted text that shall be decrypted
     * @return encrypted text
     * @see #encryptString(String)
     */
    public static String decryptString(String text) {
        byte[] encoded;

        if (text == null) {
            return "";
        }

        encoded = text.getBytes();

        for (int i = 0; (i < encoded.length); ++i) {
            //check ob Zeichen gleich ~ = 126 ?
            if (encoded[i] == 126) {
                //Dann mit tilde ersetzen slash = 92
                encoded[i] = 92;
            }

            encoded[i] += 7;

            if ((encoded[i] % 2) == 0) {
                ++encoded[i];
            } else {
                --encoded[i];
            }
        }

        return new String(encoded);
    }

    /**
     * Provided for convenience and is the same as {@link #encryptString(String)}.
     *
     * @param text the text that shall be encrypted
     * @return encrypted text
     * @deprecated Provided for for convenience. Please use {@link #encryptString(String)} instead. Can be removed
     * in a later version.
     */
    @Deprecated(since = "10.0")
    public static String cryptString(String text) {
        return encryptString(text);
    }

    /**
     * Encrypts the string {@code text} consisting on numbers and characters only.
     *
     * @param text the text that shall be encrypted
     * @return encrypted text
     * @see #decryptString(String)
     */
    public static String encryptString(String text) {
        byte[] encoded;

        if (text == null) {
            return "";
        }

        for (int j = 0; j < text.length(); j++) {
            if (!Character.isLetterOrDigit(text.charAt(j))) {
                return null;
            }
        }

        encoded = text.getBytes();

        for (int i = 0; (i < encoded.length); ++i) {
            if ((encoded[i] % 2) == 0) {
                ++encoded[i];
            } else {
                --encoded[i];
            }

            encoded[i] -= 7;

            //check for slash character = 92 ?
            if (encoded[i] == 92) {
                // replace it by  ~ = 126
                encoded[i] = 126;
            }
        }

        return new String(encoded);
    }
}
