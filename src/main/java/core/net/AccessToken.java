package core.net;

import core.util.HOEncryption;

public record AccessToken(String cryptedToken, String cryptedTokenSecret) {
    public static AccessToken ofCryptedData(String cryptedToken, String cryptedTokenSecret) {
        return new AccessToken(cryptedToken, cryptedTokenSecret);
    }

    public String getToken() {
        return HOEncryption.decryptString(cryptedToken);
    }

    public String getTokenSecret() {
        return HOEncryption.decryptString(cryptedTokenSecret);
    }
}
