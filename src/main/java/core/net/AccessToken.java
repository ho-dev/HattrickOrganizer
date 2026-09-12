package core.net;

import core.util.HOEncryption;

public record AccessToken(String encryptedToken, String encryptedTokenSecret) {

    public static AccessToken ofEncryptedData(String encryptedToken, String encryptedTokenSecret) {
        return new AccessToken(encryptedToken, encryptedTokenSecret);
    }

    public static AccessToken ofData(String token, String tokenSecret) {
        return new AccessToken(HOEncryption.encryptString(token), HOEncryption.encryptString(tokenSecret));
    }

    public String getToken() {
        return HOEncryption.decryptString(encryptedToken);
    }

    public String getTokenSecret() {
        return HOEncryption.decryptString(encryptedTokenSecret);
    }
}
