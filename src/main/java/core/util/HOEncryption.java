package core.util;

public final class HOEncryption {

    private HOEncryption() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Decrypt string
     * encrypted by method crypt
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
     * Encrypt a string consisting on numbers and characters only
     */
    public static String cryptString(String text) {
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
